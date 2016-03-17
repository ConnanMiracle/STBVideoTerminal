/* Copyright (C) 2010-2014, IMSHello R&D Group
*  
*  Copyright (C) 2011, Philippe Verney <verney(dot)philippe(AT)gmail(dot)com>
*
* Contact:  <rd(at)imshello(dot)org>
*	
* This file is part of 'IMSHello for Android' Project 
*
* Notes: you can redistribute it and/or modify it under the terms of 
* the GNU General Public License as published by the Free Software Foundation, either version 3 
* of the License, or (at your option) any later version.
*	
* it is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
* See the GNU General Public License for more details.
*	
* You should have received a copy of the GNU General Public License along 
* with this program; if not, write to the Free Software Foundation, Inc., 
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package org.imshello.ngn.media;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.imshello.ngn.events.NgnMediaPluginEventArgs;
import org.imshello.ngn.events.NgnMediaPluginEventTypes;
import org.imshello.utils.AVCDecoder;
import org.imshello.utils.HWHiDecoder;
import org.imshello.baseWRAP.ProxyVideoConsumer;
import org.imshello.baseWRAP.ProxyVideoConsumerCallback;
import org.imshello.baseWRAP.ProxyVideoFrame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Video consumer using SurfaceView
 */
public class NgnProxyVideoConsumerSV extends NgnProxyVideoConsumer{
	private static final String TAG = NgnProxyVideoConsumerSV.class.getCanonicalName();
	private static final int DEFAULT_VIDEO_WIDTH = 176;
	private static final int DEFAULT_VIDEO_HEIGHT = 144;
	private static final int DEFAULT_VIDEO_FPS = 15;
	
	public static final int SOFT_DECODE=1;
	public static final int HARD_DECODE=2;
	
	private final MyProxyVideoConsumerCallback mCallback;
	private final ProxyVideoConsumer mConsumer;
	private Context mContext;
	private MyProxyVideoConsumerPreview mPreview;
	private int mWidth;
	private int mHeight;
	private int mFps;
	private ByteBuffer mVideoFrame,mDecodedFrame;
	private Bitmap mRGB565Bitmap;
	private Bitmap mRGBCroppedBitmap;
	private Looper mLooper;
    private Handler mHandler;
    
    private boolean isH264passThrough=true;
    //static AVCDecoder decoder=null;
    HWHiDecoder hiDecoder=null;
    File h264RawFile;
    FileOutputStream os;
    protected NgnProxyVideoConsumerSV(BigInteger id, ProxyVideoConsumer consumer){
    	super(id, consumer);
    	mConsumer = consumer;
    	mCallback = new MyProxyVideoConsumerCallback(this);
    	mConsumer.setCallback(mCallback);

    	// Initialize video stream parameters with default values
    	mWidth = NgnProxyVideoConsumerSV.DEFAULT_VIDEO_WIDTH;
    	mHeight = NgnProxyVideoConsumerSV.DEFAULT_VIDEO_HEIGHT;
    	mFps = NgnProxyVideoConsumerSV.DEFAULT_VIDEO_FPS;
    	
    	if(isH264passThrough){
    		//decoder=new AVCDecoder();
    		//hiDecoder=HWHiDecoder.getDefaultDecoder();

    		//h264RawFile=new File("/mnt/sda/sda1/h264RawFile.h264");
    		/*if(h264RawFile!=null){
				try {
					os=new FileOutputStream(h264RawFile);
				} catch (FileNotFoundException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
    		}*/
    	}
    }
    
    @Override
    public void invalidate(){
    	super.invalidate();
    	if(mRGBCroppedBitmap != null){
    		mRGBCroppedBitmap.recycle();
    	}
    	if(mRGB565Bitmap != null){
    		mRGB565Bitmap.recycle();
    	}
    	mRGBCroppedBitmap = null;
    	mRGB565Bitmap = null;
    	mVideoFrame = null;
    	System.gc();
    }
    
    @Override
    public void setContext(Context context){
    	mContext = context;
    }
    
    @Override
    public final View startPreview(Context context){
    	mContext = context == null ? mContext : context;
    	if(mPreview == null && mContext != null){
			if(mLooper != null){
				mLooper.quit();
				mLooper = null;
			}
			
			final Thread previewThread = new Thread() {
				@Override
				public void run() {
					android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);
					Looper.prepare();
					mLooper = Looper.myLooper();
					
					synchronized (this) {
						mPreview = new MyProxyVideoConsumerPreview(mContext, mWidth, mHeight, mFps);
						notify();
					}
					
					mHandler = new Handler() {
						public void handleMessage(Message message) {
							if(message.what==SOFT_DECODE){
								final int nCopiedSize = message.arg1;
								final int nAvailableSize = message.arg2;
								long frameWidth = mConsumer.getDisplayWidth();
								long frameHeight = mConsumer.getDisplayHeight();
								if(mVideoFrame == null || mWidth != frameWidth || frameHeight != mHeight || mVideoFrame.capacity() != nAvailableSize){
									if(frameWidth <=0 || frameHeight <= 0){
										Log.e(TAG,"nCopiedSize="+nCopiedSize+" and newWidth="+frameWidth+" and newHeight="+frameHeight);
										return;
									}
									//Log.d(TAG,"resizing the buffer nAvailableSize="+nAvailableSize+" and newWidth="+frameWidth+" and newHeight="+frameHeight);
									if(mRGB565Bitmap != null){
										mRGB565Bitmap.recycle();
									}
									if(mRGBCroppedBitmap != null){
										mRGBCroppedBitmap.recycle();
										mRGBCroppedBitmap = null;
										// do not create the cropped bitmap, wait for drawFrame()
									}
									mRGB565Bitmap = Bitmap.createBitmap((int)frameWidth, (int)frameHeight, Bitmap.Config.RGB_565);
									mVideoFrame = ByteBuffer.allocateDirect((int)nAvailableSize);
									mConsumer.setConsumeBuffer(mVideoFrame, mVideoFrame.capacity());
									mWidth = (int)frameWidth;
									mHeight = (int)frameHeight;
									return; // Draw the picture next time
								}
								
								drawFrame();
							}else if(message.what==HARD_DECODE){
								//Log.i(TAG, "new decoded frame!");
								//decoder.updateView(message.arg1);
							}
						}
					};		    	
					
					Looper.loop();
					mHandler = null;
					//Log.d(TAG, "VideoConsumer::Looper::exit");
				}
			};
			
			/*new Thread(){
				public void run() {
					InputStream inputStream=null;
					try {
						if(mContext==null)
							Log.e(TAG, "mCOntext is null");
						inputStream = mContext.getAssets().open("test.h264");
					} catch (IOException e1) {
						// TODO 自动生成的 catch 块
						e1.printStackTrace();
					}catch(NullPointerException npException){
						npException.printStackTrace();
						return;
					}
					while(true){
						if(inputStream!=null)
							try {
								int size=inputStream.read(h264RawData);
								if(size==-1)
									break;
								hiDecoder.handleData(h264RawData, size, HWHiDecoder.VIDEO_DATA);
							} catch (IOException e) {
								// TODO 自动生成的 catch 块
								e.printStackTrace();
							}	catch(NullPointerException npException){
								npException.printStackTrace();
								return;
							}		
					}
					Log.i(TAG, "sleep 5s");
					SystemClock.sleep(5000);
					hiDecoder.stop();
					Log.i(TAG, "mplayer is stopped: state"+hiDecoder.getStatus());
					hiDecoder.finish();
					Log.i(TAG, "mplayer is finished: state"+hiDecoder.getStatus());
					//在prepareCallback中prepare和start HWHiDecoder，在线程中stop 和finish，两端测试成功，包括两端分别主动拨打，分别主动挂机，均测试了两遍，没有问题
				};
			}.start();*/
			
			previewThread.setPriority(Thread.MAX_PRIORITY);
			synchronized(previewThread) {
				previewThread.start();
				try {
					previewThread.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return null;
				}
	        }
		}
		else{
			Log.e(TAG, "Invalid state");
		}
		return mPreview;
    }
    
    @Override
	public final View startPreview(){
		return startPreview(null);
	}
    
    private int prepareCallback(int width, int height, int fps){
    	Log.d(TAG, "prepareCallback("+width+","+height+","+fps+")");
    	
    	// Update video stream parameters with real values (negotiated)
		mWidth = width;
		mHeight = height;
		mFps = fps;
		
		//mRGB565Bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
		mRGB565Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		//mVideoFrame = ByteBuffer.allocateDirect((mWidth * mHeight) << 1);
		mVideoFrame = ByteBuffer.allocateDirect((width * height) << 1);
		mConsumer.setConsumeBuffer(mVideoFrame, mVideoFrame.capacity());
		
		if(isH264passThrough){

			//decoder.setFrameParams(width, height);
			hiDecoder=HWHiDecoder.getDecoder(mFps, mWidth, mHeight, mWidth, mHeight, 0, 0, 0);
			int ret=hiDecoder.prepare();
			if(ret==HWHiDecoder.SUCCESS){
				ret=hiDecoder.start();
				if(ret!=HWHiDecoder.SUCCESS){
					Log.e(TAG, "lanunch HWHiDecoder failed!");//是否应该在此时停掉有待确认！
				}
			}else
				Log.e(TAG, "lanunch HWHiDecoder failed!");
		}
		
		super.mPrepared = true;
		return 0;
    }
    
    private int startCallback(){
    	Log.d(NgnProxyVideoConsumerSV.TAG, "startCallback");
    	super.mStarted = true;
    	return 0;
    }

    private int bufferCopiedCallback(long nCopiedSize, long nAvailableSize) {
    	if(!super.mValid || mRGB565Bitmap == null){
			Log.e(TAG, "Invalid state");
			return -1;
		}
		if(mPreview == null || mPreview.mHolder == null){
			// Not on the top
			return 0;
		}
		
		if(isH264passThrough){
			/*int ret=decoder.decode(mVideoFrame, (int)nCopiedSize,(int)nAvailableSize);

			if(ret>=0){
				mHandler.obtainMessage(HARD_DECODE, ret, -1).sendToTarget();
			}*/

			//Log.i(TAG, "buffercopied call back!!");
			//hiDecoder.putBuffer(mVideoFrame.array(),(int)nCopiedSize, (int)nAvailableSize);
			
			if((hiDecoder!=null)&&(hiDecoder.getStatus()==HWHiDecoder.STARTED))
				hiDecoder.putBuffer(mVideoFrame, (int)nCopiedSize, (int)nAvailableSize);
			
			/*try {
				os.write(mVideoFrame.array(), 0, (int) nCopiedSize);
				os.flush();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}*/
			return 0;
		}
		
		if(mHandler != null){
			final Message message = mHandler.obtainMessage();
			message.what=SOFT_DECODE;
			message.arg1 = (int)nCopiedSize;
			message.arg2 = (int)nAvailableSize;
			mHandler.sendMessage(message);
			
		}
		
		return 0;
    }
    
    private int consumeCallback(ProxyVideoFrame _frame){    	
		if(!super.mValid || mRGB565Bitmap == null){
			Log.e(TAG, "Invalid state");
			return -1;
		}
		if(mPreview == null || mPreview.mHolder == null){
			// Not on the top
			return 0;
		}
		//TODO is the following code processed?
		// Get video frame content from native code
		_frame.getContent(mVideoFrame, mVideoFrame.capacity());
		mRGB565Bitmap.copyPixelsFromBuffer(mVideoFrame);
		drawFrame();
	    
		return 0;
    }

    private int pauseCallback(){
    	Log.d(TAG, "pauseCallback");
    	super.mPaused = true;
    	return 0;
    }
    
    private synchronized int stopCallback(){
    	Log.d(TAG, "stopCallback");
    	super.mStarted = false;
    	if (isH264passThrough) {
    		Log.i(TAG, "prepare to stop hiDecoder...");
    		if(hiDecoder!=null){    			
				new Thread() {
					public void run() {
						//decoder.close();
						Log.i(TAG, "stopping hiDecoder ....");
						if(hiDecoder.getStatus()==HWHiDecoder.STARTED)
							hiDecoder.stop();
						Log.i(TAG, "hiDecoder is stopped.. prepare to finish......");
						if(hiDecoder.getStatus()==HWHiDecoder.STOPPED)
							hiDecoder.finish();
						Log.i(TAG, "hiDecoder is finished....");
						hiDecoder=null;
					}
				}.start();
    		}else{
    			Log.i(TAG, "hiDecoder already destroyed!..");
    		}
    		/*try {
				os.close();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}*/
		}
    	if(mLooper != null){
			mLooper.quit();
			mLooper = null;
		}
    	
		mPreview = null;
    	return 0;
    }
	
    private synchronized void drawFrame(){
		if (mPreview != null && mPreview.mHolder != null){
			final Canvas canvas = mPreview.mHolder.lockCanvas();
			if(canvas == null){
				return;
			}
			if(isH264passThrough){
				mDecodedFrame.rewind();
				Log.i(TAG, "draw frame :"+mDecodedFrame.position()+" "+mDecodedFrame.limit());
				mRGB565Bitmap.copyPixelsFromBuffer(mDecodedFrame);
			}
			else
				mRGB565Bitmap.copyPixelsFromBuffer(mVideoFrame);
			if(super.mFullScreenRequired){
				// destroy cropped bitmap if surface has changed
				if(mPreview.isSurfaceChanged()){
					mPreview.setSurfaceChanged(false);
					if(mRGBCroppedBitmap != null){
						mRGBCroppedBitmap.recycle();
						mRGBCroppedBitmap = null;
					}
				}
				
				// create new cropped image if doesn't exist yet
				if(mRGBCroppedBitmap == null){
					float ratio = Math.max(
							(float)mPreview.mSurfFrame.width() / (float)mRGB565Bitmap.getWidth(), 
							(float)mPreview.mSurfFrame.height() / (float)mRGB565Bitmap.getHeight());
					
					mRGBCroppedBitmap = Bitmap.createBitmap(
							(int)(mPreview.mSurfFrame.width()/ratio), 
							(int)(mPreview.mSurfFrame.height()/ratio), 
							Bitmap.Config.RGB_565);
				}
				
				// crop the image
				Canvas _canvas = new Canvas(mRGBCroppedBitmap);
				Bitmap copyOfOriginal = Bitmap.createBitmap(mRGB565Bitmap,
						Math.abs((mRGBCroppedBitmap.getWidth() - mRGB565Bitmap.getWidth())/2),
						Math.abs((mRGBCroppedBitmap.getHeight() - mRGB565Bitmap.getHeight())/2),
						mRGBCroppedBitmap.getWidth(),
						mRGBCroppedBitmap.getHeight(),
						null, 
						true);//FIXME: translate the original image instead of creating new one
				_canvas.drawBitmap(copyOfOriginal, 0.f, 0.f, null);
				copyOfOriginal.recycle();
				// draw the cropped image
				canvas.drawBitmap(mRGBCroppedBitmap, null, mPreview.mSurfFrame, null);
			}
			else{
				// display while keeping the ratio
				// canvas.drawBitmap(mRGB565Bitmap, null, mPreview.mSurfDisplay, null);
				// Or display "as is"
				canvas.drawBitmap(mRGB565Bitmap, 0, 0, null);
			}
			if(mPreview != null){// yep it's possible (not synchronized)
				mPreview.mHolder.unlockCanvasAndPost(canvas);
			}
		}
    }
    
	
	/**
	 * MyProxyVideoConsumerCallback
	 */
	static class MyProxyVideoConsumerCallback extends ProxyVideoConsumerCallback
    {
        final NgnProxyVideoConsumerSV myConsumer;

        public MyProxyVideoConsumerCallback(NgnProxyVideoConsumerSV consumer){
        	super();
            myConsumer = consumer;
        }
        
        @Override
        public int prepare(int width, int height, int fps){
            int ret = myConsumer.prepareCallback(width, height, fps);
            NgnMediaPluginEventArgs.broadcastEvent(new NgnMediaPluginEventArgs(myConsumer.mId, NgnMediaType.Video, 
            		ret == 0 ? NgnMediaPluginEventTypes.PREPARED_OK : NgnMediaPluginEventTypes.PREPARED_NOK));
            return ret;
        }
        
        @Override
        public int start(){
            int ret = myConsumer.startCallback();
            NgnMediaPluginEventArgs.broadcastEvent(new NgnMediaPluginEventArgs(myConsumer.mId, NgnMediaType.Video, 
            		ret == 0 ? NgnMediaPluginEventTypes.STARTED_OK : NgnMediaPluginEventTypes.STARTED_NOK));
            return ret;
        }

        @Override
        public int consume(ProxyVideoFrame frame){
            return myConsumer.consumeCallback(frame);
        }        
        
        @Override
		public int bufferCopied(long nCopiedSize, long nAvailableSize) {
			return myConsumer.bufferCopiedCallback(nCopiedSize, nAvailableSize);
		}

		@Override
        public int pause(){
            int ret = myConsumer.pauseCallback();
            NgnMediaPluginEventArgs.broadcastEvent(new NgnMediaPluginEventArgs(myConsumer.mId, NgnMediaType.Video, 
            		ret == 0 ? NgnMediaPluginEventTypes.PAUSED_OK : NgnMediaPluginEventTypes.PAUSED_NOK));
            return ret;
        }
        
        @Override
        public int stop(){
            int ret = myConsumer.stopCallback();
            NgnMediaPluginEventArgs.broadcastEvent(new NgnMediaPluginEventArgs(myConsumer.mId, NgnMediaType.Video, 
            		ret == 0 ? NgnMediaPluginEventTypes.STOPPED_OK : NgnMediaPluginEventTypes.STOPPED_NOK));
            return ret;
        }
    }
	
	/**
	 * MyProxyVideoConsumerPreview
	 */
	static class MyProxyVideoConsumerPreview extends SurfaceView implements SurfaceHolder.Callback {
		private final SurfaceHolder mHolder;
		private Rect mSurfFrame;
		@SuppressWarnings("unused")
		private Rect mSurfDisplay;
		private final float mRatio;
		private boolean mSurfaceChanged;
		
		MyProxyVideoConsumerPreview(Context context, int width, int height, int fps) {
			super(context);
			
			mHolder = getHolder();
			mHolder.addCallback(this);
			// You don't need to enable GPU or Hardware acceleration by yourself
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_HARDWARE);
			mRatio = (float)width/(float)height;
			
			if(mHolder != null){
				mSurfFrame = mHolder.getSurfaceFrame();
			}
			else{
				mSurfFrame = null;
			}
			mSurfDisplay = mSurfFrame;
		}
		
		public synchronized void setSurfaceChanged(boolean surfaceChanged){
			mSurfaceChanged = surfaceChanged;
		}
		
		public synchronized boolean isSurfaceChanged(){
			return mSurfaceChanged;
		}
	
		public void surfaceCreated(SurfaceHolder holder) {
			//decoder.setSurface(this.getHolder().getSurface());
		}
	
		public void surfaceDestroyed(SurfaceHolder holder) {
			//decoder.setSurface(null);
		}
	
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			if(holder != null){
				mSurfFrame = holder.getSurfaceFrame();
				
				// (w/h)=ratio => 
				// 1) h=w/ratio 
				// and 
				// 2) w=h*ratio
				int newW = (int)(w/mRatio) > h ? (int)(h * mRatio) : w;
				int newH = (int)(newW/mRatio) > h ? h : (int)(newW/mRatio);
				
				mSurfDisplay = new Rect(0, 0, newW, newH);
				setSurfaceChanged(true);
			}
		}
	}
	
	public static byte[] getBytes(Object obj) throws IOException {  
        ByteArrayOutputStream bout = new ByteArrayOutputStream();  
        ObjectOutputStream out = new ObjectOutputStream(bout);  
        out.writeObject(obj);  
        out.flush();  
        byte[] bytes = bout.toByteArray();  
        bout.close();  
        out.close();  
        return bytes;  
    }  
}
