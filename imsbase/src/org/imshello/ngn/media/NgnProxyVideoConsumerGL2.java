/* Copyright (C) 2012, imshello Telecom <http://www.imshello.org>
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

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.imshello.ngn.events.NgnMediaPluginEventArgs;
import org.imshello.ngn.events.NgnMediaPluginEventTypes;
import org.imshello.utils.AVCDecoder2;
import org.imshello.utils.VideoSurfaceView;
import org.imshello.baseWRAP.ProxyVideoConsumer;
import org.imshello.baseWRAP.ProxyVideoConsumerCallback;
import org.imshello.baseWRAP.ProxyVideoFrame;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

/**
 * Video consumer using OpenGL ES 2.0
 */
public class NgnProxyVideoConsumerGL2 extends NgnProxyVideoConsumer{
	private static final String TAG = NgnProxyVideoConsumerGL2.class.getCanonicalName();
	private static final int DEFAULT_VIDEO_WIDTH = 176;
	private static final int DEFAULT_VIDEO_HEIGHT = 144;
	private static final int DEFAULT_VIDEO_FPS = 15;
	
	private final NgnProxyVideoConsumerGLCallback mCallback;
	private final ProxyVideoConsumer mConsumer;
	private ByteBuffer mVideoFrame;
	private Context mContext;
	private VideoSurfaceView mPreview;
	private int mWidth;
	private int mHeight;
	private int mFps;
	
    private boolean isH264passThrough=true;
    static AVCDecoder2 decoder=null;
	public static final int SOFT_DECODE=1;
	public static final int HARD_DECODE=2;
	private static boolean useSurface=true;

	protected NgnProxyVideoConsumerGL2(BigInteger id, ProxyVideoConsumer consumer){
    	super(id, consumer);
    	mConsumer = consumer;
    	mCallback = new NgnProxyVideoConsumerGLCallback(this);
    	mConsumer.setCallback(mCallback);

    	// Initialize video stream parameters with default values
    	mWidth = NgnProxyVideoConsumerGL2.DEFAULT_VIDEO_WIDTH;
    	mHeight = NgnProxyVideoConsumerGL2.DEFAULT_VIDEO_HEIGHT;
    	mFps = NgnProxyVideoConsumerGL2.DEFAULT_VIDEO_FPS;
    	
    	if(isH264passThrough){
    		decoder=new AVCDecoder2(useSurface);
    	}
    	Log.e(TAG, "GL created!");
    }
    
    @Override
    public void invalidate(){
    	super.invalidate();
    	mVideoFrame = null;
    	decoder=null;
    	System.gc();
    }
    
    @Override
    public void setContext(Context context){
    	mContext = context;
    }
    
    @Override
    public final View startPreview(Context context){
    	synchronized(this){
	    	mContext = context == null ? mContext : context;
	    	if(mContext != null){
		    	if(mPreview == null){
		    		//mPreview = new NgnProxyVideoConsumerGLPreview(mContext, super.mFullScreenRequired,  mVideoFrame, mWidth, mHeight, mFps);
		    		mPreview = new VideoSurfaceView(mContext, decoder);
		    		Log.e(TAG, "start preview: preview: "+String.valueOf(mPreview==null)+" decoder: "+String.valueOf(decoder==null));
		    	}
	    	}
			return mPreview;
    	}
    }
    
    @Override
	public final View startPreview(){
		return startPreview(null);
	}
    
    private int prepareCallback(int width, int height, int fps){
    	synchronized(this){
	    	Log.d(TAG, "prepareCallback("+width+","+height+","+fps+")");
	    	
	    	// Update video stream parameters with real values (negotiated)
			mWidth = width;
			mHeight = height;
			mFps = fps;
			mVideoFrame = ByteBuffer.allocateDirect((mWidth * mHeight * 3) >> 1);
			mConsumer.setConsumeBuffer(mVideoFrame, mVideoFrame.capacity());
			Log.e(TAG, "prepare call back: "+String.valueOf(mPreview==null));
			if(mPreview != null){
				if(isH264passThrough){
					Log.e(TAG, "set surface buffer  Buffer Y U V");
					//mPreview.setBuffer(mDeocodedFrame, mWidth, mHeight);
					//mPreview.setBuffer(BufferY,BufferU,BufferV, mWidth, mHeight);
				}else{
					Log.i(TAG, "set surface buffer  mVideoFrame");
					//mPreview.setBuffer(mVideoFrame, mWidth, mHeight);
				}
			}
			if(isH264passThrough){
				decoder.setFrameParams(width, height);
			}
			
			super.mPrepared = true;
			return 0;
    	}
    }
    
    private int startCallback(){
    	synchronized(this){
	    	Log.d(NgnProxyVideoConsumerGL2.TAG, "startCallback");
	    	super.mStarted = true;
	    	return 0;
    	}
    }

    private int bufferCopiedCallback(long nCopiedSize, long nAvailableSize) {
    	if(!super.mValid){
			Log.e(TAG, "Invalid state");
			return -1;
		}
		if(mPreview == null){
			// Not on the top
			return 0;
		}
		if(isH264passThrough){
			int ret=decoder.decode(mVideoFrame, (int)nCopiedSize,(int)nAvailableSize);
			//Log.i(TAG, "decoded return "+ret);
			/*if((ret>=0)&&(!useSurface))
				mPreview.requestRender();*/
			return 0;
		}
		//Log.i(TAG, "preview yuv buffer null?"+String.valueOf(mPreview.mBufferY==null)+String.valueOf(mPreview.mBufferU==null)+String.valueOf(mPreview.mBufferV==null));
		//mPreview.requestRender();
		
		return 0;
    }
    
    private int consumeCallback(ProxyVideoFrame _frame){
		if(!super.mValid){
			Log.e(TAG, "Invalid state");
			return -1;
		}
		if(mPreview == null){
			// Not on the top
			return 0;
		}
		
		// Get video frame content from native code
		//_frame.getContent(mVideoFrame, mVideoFrame.capacity());
		Log.i(TAG, "consume call back!");
		//mPreview.requestRender();
		
		return 0;
    }

    private int pauseCallback(){
    	synchronized(this){
	    	Log.d(TAG, "pauseCallback");
	    	super.mPaused = true;
	    	return 0;
    	}
    }
    
    private synchronized int stopCallback(){
    	synchronized(this){
	    	Log.d(TAG, "stopCallback");
	    	super.mStarted = false;
	    	
	    	mPreview = null;
	    	if (isH264passThrough) {
				decoder.close();
			}
	    	return 0;
    	}
    }
	
	/**
	 * NgnProxyVideoConsumerGLCallback
	 */
	static class NgnProxyVideoConsumerGLCallback extends ProxyVideoConsumerCallback
    {
        final NgnProxyVideoConsumerGL2 mConsumer;

        public NgnProxyVideoConsumerGLCallback(NgnProxyVideoConsumerGL2 consumer){
        	super();
        	mConsumer = consumer;
        }
        
        @Override
        public int prepare(int width, int height, int fps){
            int ret = mConsumer.prepareCallback(width, height, fps);
            NgnMediaPluginEventArgs.broadcastEvent(new NgnMediaPluginEventArgs(mConsumer.mId, NgnMediaType.Video, 
            		ret == 0 ? NgnMediaPluginEventTypes.PREPARED_OK : NgnMediaPluginEventTypes.PREPARED_NOK));
            return ret;
        }
        
        @Override
        public int start(){
            int ret = mConsumer.startCallback();
            NgnMediaPluginEventArgs.broadcastEvent(new NgnMediaPluginEventArgs(mConsumer.mId, NgnMediaType.Video, 
            		ret == 0 ? NgnMediaPluginEventTypes.STARTED_OK : NgnMediaPluginEventTypes.STARTED_NOK));
            return ret;
        }

        @Override
        public int consume(ProxyVideoFrame frame){
            return mConsumer.consumeCallback(frame);
        }        
        
        @Override
		public int bufferCopied(long nCopiedSize, long nAvailableSize) {
			return mConsumer.bufferCopiedCallback(nCopiedSize, nAvailableSize);
		}

		@Override
        public int pause(){
            int ret = mConsumer.pauseCallback();
            NgnMediaPluginEventArgs.broadcastEvent(new NgnMediaPluginEventArgs(mConsumer.mId, NgnMediaType.Video, 
            		ret == 0 ? NgnMediaPluginEventTypes.PAUSED_OK : NgnMediaPluginEventTypes.PAUSED_NOK));
            return ret;
        }
        
        @Override
        public int stop(){
            int ret = mConsumer.stopCallback();
            NgnMediaPluginEventArgs.broadcastEvent(new NgnMediaPluginEventArgs(mConsumer.mId, NgnMediaType.Video, 
            		ret == 0 ? NgnMediaPluginEventTypes.STOPPED_OK : NgnMediaPluginEventTypes.STOPPED_NOK));
            return ret;
        }
    }
	

}
