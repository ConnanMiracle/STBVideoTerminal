
package org.imshello.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.imshello.baseWRAP.MediaSessionMgr;
import org.imshello.baseWRAP.ProxyVideoProducer;
import org.imshello.baseWRAP.tmedia_chroma_t;
import org.imshello.ngn.NgnApplication;
import org.imshello.ngn.NgnEngine;
import org.imshello.ngn.media.NgnProxyVideoProducer;
import org.imshello.ngn.media.NgnUSBCameraProducer;
import org.imshello.ngn.media.NgnUSBCameraProducer.MyUSBCameraCallback;
import org.imshello.ngn.utils.NgnConfigurationEntry;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;



public class USBCamera
{  
	public final String TAG=this.getClass().getCanonicalName();
	/*Constants for capture pixel format*/
	public final static int CAMERA_CAPTURE_H264   =1;
	public final static int CAMERA_CAPTURE_YUYV   =2;
	public final static int CAMERA_CAPTURE_YUV420 =3;
	public final static int CAMERA_CAPTURE_NV21   =4;
	public final static int CAMERA_CAPTURE_RGB565 =5;
	
	public static final int STREAM_FMT_RAW       =0;

    public static final int STREAM_FMT_YUV420P   =1;

    public static final int STREAM_FMT_ARGB      =2;
    
	/*Constants whether we use jni to generate bitmap for drawing*/
	public final static int CALLBACK_REQUEST_BITMAP=1;	
	public final static int CALLBACK_NO_REQUEST_BITMAP=0;
	
	public final static int CALLBACK_REQUEST_420P=1;	
	public final static int CALLBACK_NO_REQUEST_420P=0;
	
	
	public String sCameraDevName;
	public int fmt_pix_width;	    //= 640; 1920
	public int fmt_pix_height;	//= 480; 1080
	public int fmt_frame_rate;	//= 15|30
	public int fmt_pix_format;	//= CAMERA_YUV_CAPTURE|C920_H264_CAPTURE	
	public int stream_fmt;
	private int requestBitmap=0;
	  
	public static byte[] byteFrameData = new byte[1920*1080*4];
	
	private Thread caputureThread=null;
	private boolean shouldStop=false;
	ByteBuffer mVideoFrame;
	
	private Handler producerHandler;
	NgnProxyVideoProducer proxyproducer;
	SurfaceView surfaceView;
	SurfaceHolder surfaceholder;
	MyUSBCameraCallback dataCallback;
	
	FileOutputStream fos=null;
	File outputFile=null;
	
	int[] rgb_output;
	
	private DataBufferQueue bufferQueue;
	private Thread sendThread=null;
/*	public USBCamera(NgnProxyVideoProducer producer)
	{
	    sCameraDevName="/dev/video0";
		fmt_pix_width =640;
	    fmt_pix_height=480;	
	    fmt_frame_rate=15;	//30fps
	    fmt_pix_format=CAMERA_CAPTURE_YUYV;
	    
	    //mVideoFrame=ByteBuffer.wrap(byteFrameData);
		mVideoFrame = ByteBuffer.allocateDirect((fmt_pix_width*fmt_pix_height*4));
		proxyproducer=producer;
		ProxyVideoProducer.setDefaultChroma(tmedia_chroma_t.tmedia_chroma_yuv422p);
		proxyproducer.getProducer().setActualCameraOutputSize(fmt_pix_width, fmt_pix_height);
		
		
	}*/
	
	public USBCamera(){
		/*default values*/
		//sCameraDevName="/dev/video0";
		sCameraDevName="/dev/"+NgnEngine.getInstance().getConfigurationService().getString(NgnConfigurationEntry.USB_CAMERA_DEVICE_NAME,NgnConfigurationEntry.DEFAULT_USB_CAMERA_DEVICE_NAME);
		fmt_pix_width =640;
		fmt_pix_height=480;	
		fmt_frame_rate=15;	//30fps
		fmt_pix_format=CAMERA_CAPTURE_YUYV;
		
		/*ByteBuffer used to wrap capture callback data, not useful now.*/
		//mVideoFrame = ByteBuffer.allocateDirect((fmt_pix_width*fmt_pix_height*4));
		
		/*@see NgnProxyPluginMgr.Initialize() yet I don't know whether it's correct to set default chroma here.*/
		ProxyVideoProducer.setDefaultChroma(tmedia_chroma_t.tmedia_chroma_yuv420p);
		
		/*write h.264 capture into a file. do not need it now.*/
		/*outputFile=new File("/mnt/sda/sda1/testusbcapture2016.h264");
		try {
			fos=new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		rgb_output=new int[fmt_pix_width*fmt_pix_height];Log.e(TAG, "By pass...   "+MediaSessionMgr.defaultsGetByPassEncoding());
		int cameraMaxQueueSize=NgnEngine.getInstance().getConfigurationService().getInt(NgnConfigurationEntry.USB_CAMERA_BUFFERQUEUE_SIZE, NgnConfigurationEntry.DEFAULT_USB_CAMERA_BUFFERQUEUE_SIZE);
		int cameraMaxBufferSize=NgnEngine.getInstance().getConfigurationService().getInt(NgnConfigurationEntry.USB_CAMERA_BUFFER_SIZE, NgnConfigurationEntry.DEFAULT_USB_CAMERA_BUFFER_SIZE);
		Log.i(TAG, "Camera buffer queue size: "+cameraMaxQueueSize+"\nCamera buffer size: "+cameraMaxBufferSize);
		bufferQueue=new DataBufferQueue(new USBCameraConsumer(),cameraMaxQueueSize,cameraMaxBufferSize);
	}
	
	class USBCameraConsumer implements DataBufferQueue.DataBufferQueueConsumer{
		@Override
		public int handleData(byte[] data, int validSize) {
			// TODO 自动生成的方法存根
			/*if (fmt_pix_format==CAMERA_CAPTURE_H264) {
				try {
					//Log.i(TAG, "writing file.."+size_byteFrameData);
					fos.write(data,0,validSize);
					fos.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
			dataCallback.onFrame(data, validSize);
			return 0;
		}
	}	

	    
	//JNI 
	private native int SetDevName(String sDevName);
	private native int SetCaptureParameters(int pix_width, int pix_height, int frame_rate, int pix_format,int stream_fmt, int do_pixels2bmp);
	private native int StartCapture(byte[] buffer);
	private native int StopCapture();	
	private native int NV21toARGB(byte[] rgb_buf, byte[] nv21_buf, int width, int height);
	private native int YUYVtoARGB(int[] rgb_buf, byte[] yuyv_buf, int width, int height);
	private native int Pixels2BMP(Bitmap bmp);
	/*
	 * Call this before you want to start capture.
	 * Parameters must be set before call @function Init().  
	 */
	public int Init()
	{
	    SetDevName(sCameraDevName);
	    //SetCaptureParameters() will be done in external call to @function  setParams();
		//SetCaptureParameters(fmt_pix_width, fmt_pix_height, fmt_frame_rate, fmt_pix_format,stream_fmt,requestBitmap);
		//Log.i(TAG, "Init()...");
		return 0;
	}
	  
	// JNI Callback
	public int OnNewVideoFrameCallback(int size_byteFrameData)
	{
	    // To process the captured frame: byteFrameData/size_byteFrameData
	    //TODO
		//Log.i(TAG, "new Frame...   "+size_byteFrameData+" "+fmt_pix_width+" "+ fmt_pix_height+" "+ fmt_pix_format+" "+ fmt_frame_rate);
		//dataCallback.onFrame(byteFrameData, size_byteFrameData);
		bufferQueue.produce(byteFrameData, size_byteFrameData);
		/*if(surfaceView!=null){
			Bitmap bmp;
			if(requestBitmap==CALLBACK_NO_REQUEST_BITMAP){				
				YUYVtoARGB(rgb_output, byteFrameData, fmt_pix_width, fmt_pix_height);
				bmp=Bitmap.createBitmap((int [])rgb_output, fmt_pix_width, fmt_pix_height, Config.ARGB_8888);
			}
			else{
				bmp=Bitmap.createBitmap(fmt_pix_width, fmt_pix_height, Config.ARGB_8888);
				Pixels2BMP(bmp);
			}
			drawFrame(bmp);
		}*/
		
		/*if (fmt_pix_format==CAMERA_CAPTURE_H264) {
			try {
				//Log.i(TAG, "writing file.."+size_byteFrameData);
				fos.write(byteFrameData,0,size_byteFrameData);
				fos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		

	    return 0;
	}
	    
    private void drawFrame(Bitmap bmp) {
		// TODO Auto-generated method stub
    	Canvas canvas = this.surfaceholder.lockCanvas();
        if (canvas != null)
        {
        	// draw camera bmp on canvas
        	canvas.drawBitmap(bmp,null,new Rect(0,0,surfaceView.getWidth(),surfaceView.getHeight()),null);

        	this.surfaceholder.unlockCanvasAndPost(canvas);
        }
	}

	public int Start()
	{
	    //TODO: Create a new Thread
		// to run "StartCapture();" 
    	caputureThread=new Thread(){
    		@Override
			public void run(){
    			try {
					StartCapture(byteFrameData);
					if(shouldStop)
						return;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	};
    	//add 20160127 
    	caputureThread.setPriority(Thread.MAX_PRIORITY);
    	
    	
    	
    	sendThread=new Thread(){
    		@Override
    		public void run() {
    			while(true){
    				if (bufferQueue!=null) {
						if (shouldStop)
							return;
						boolean ret = bufferQueue.consume();
						if(ret==false)
							SystemClock.sleep(10);
					}
    			}
    		}
    	};
    	
    	sendThread.start();
    	caputureThread.start();
	   return 0;
	}
    
	public int Stop()
	{
	    
	    StopCapture();
		//TODO: Kill the capture Thread...
	    if(caputureThread.isAlive())
	    	caputureThread.interrupt();
	   // Log.i(TAG, "thread interrupted...");
		shouldStop=true;
		/*try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return 0;
	}
	public void setDisplayView(SurfaceView view){
		this.surfaceView=view;
		this.surfaceholder=view.getHolder();
	};
	
	public int setParams(int pix_width, int pix_height, int frame_rate, int pix_format,int stream_fmt,int request_bitmap){
		this.fmt_pix_width=pix_width;
		this.fmt_pix_height=pix_height;
		this.fmt_frame_rate=frame_rate;
		this.fmt_pix_format=pix_format;
		this.fmt_pix_width=pix_width;
		this.stream_fmt=stream_fmt;
		this.requestBitmap=request_bitmap;
		//mVideoFrame = ByteBuffer.allocateDirect((fmt_pix_width*fmt_pix_height*3)>>1);
		Log.e(TAG, "set capture params in setParams from startPreview() surfaceCreated()"+fmt_pix_width+"*"+fmt_pix_height+" "+ fmt_frame_rate);
		return SetCaptureParameters(pix_width, pix_height, frame_rate, pix_format,stream_fmt,request_bitmap);
	}
	
	public void setDataCallback(MyUSBCameraCallback callback){
		this.dataCallback=callback;
	}
	public List<Size2> getSupportedPreviewSizes(){
		List<Size2> list=new ArrayList<Size2>();
		int w=NgnEngine	.getInstance().getConfigurationService().getInt(NgnConfigurationEntry.FRAME_WIDTH,NgnConfigurationEntry.DEFAULT_FRAME_WIDTH);
		int h=NgnEngine	.getInstance().getConfigurationService().getInt(NgnConfigurationEntry.FRAME_HEIGHT,NgnConfigurationEntry.DEFAULT_FRAME_HEIGHT);
		Size2 s=new Size2(w,h);
		list.add(s);
		return list;
		
	}
	public class Size2{
		public int width;
		public int height;
		public Size2(int w,int h){
			this.width=w;
			this.height=h;
		}
	}
	
/*	public void setPrevSize(Size2 size){
		this.fmt_pix_width=size.width;
		this.fmt_pix_height=size.height;
		SetCaptureParameters(fmt_pix_width, fmt_pix_height, fmt_frame_rate, fmt_pix_format,stream_fmt,requestBitmap);
	}*/
	
	public void setPrevSize2(int width,int height){
		this.fmt_pix_width=width;
		this.fmt_pix_height=height;
		Log.e(TAG, "set capture params! in setPrevSize2() from startCallback() "+fmt_pix_width+"*"+fmt_pix_height+" "+ fmt_frame_rate);
		SetCaptureParameters(fmt_pix_width, fmt_pix_height, fmt_frame_rate, fmt_pix_format,stream_fmt,requestBitmap);
	}
}
		
