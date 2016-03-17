//
//package org.imshello.droid.Utils;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//
//import org.imshello.baseWRAP.ProxyVideoProducer;
//import org.imshello.baseWRAP.tmedia_chroma_t;
//import org.imshello.droid.Screens.ScreenAV.MySurfaceView;
//import org.imshello.ngn.NgnApplication;
//import org.imshello.ngn.NgnEngine;
//import org.imshello.ngn.media.NgnProxyVideoProducer;
//
//import android.graphics.Bitmap;
//import android.os.Environment;
//import android.os.Handler;
//import android.util.Log;
//
//
//
//public class USBCamera
//{  	
//	public final String TAG=this.getClass().getCanonicalName();
//	
//	public final static int CAMERA_CAPTURE_H264   =1;
//
//	public final static int CAMERA_CAPTURE_YUYV   =2;
//
//	public final static int CAMERA_CAPTURE_YUV420 =3;
//
//	public final static int CAMERA_CAPTURE_NV21   =4;
//
//	public final static int CAMERA_CAPTURE_RGB565 =5;
//	
//	
//	public String sCameraDevName;
//	public int fmt_pix_width;	    //= 640; 1920
//	public int fmt_pix_height;	//= 480; 1080
//	public int fmt_frame_rate;	//= 15|30
//	public int fmt_pix_format;	//= CAMERA_YUV_CAPTURE|C920_H264_CAPTURE
//	  
//	public static byte[] byteFrameData = new byte[1920*1080*4];
//	
//	private Thread caputureThread=null;
//	private boolean shouldStop=false;
//	ByteBuffer mVideoFrame;
//	
//	private Handler producerHandler;
//	NgnProxyVideoProducer proxyproducer;
//	MySurfaceView surfaceView;
//	
//	FileOutputStream fos=null;
//	File outputFile=null;
//	int[] rgb_output;
//	public USBCamera(NgnProxyVideoProducer producer)
//	{
//	    sCameraDevName="/dev/video0";
//		fmt_pix_width =640;
//	    fmt_pix_height=480;	
//	    fmt_frame_rate=15;	//30fps
//	    fmt_pix_format=CAMERA_CAPTURE_YUYV;
//	    
//	    //mVideoFrame=ByteBuffer.wrap(byteFrameData);
//		mVideoFrame = ByteBuffer.allocateDirect((fmt_pix_width*fmt_pix_height*4));
//		proxyproducer=producer;
//		ProxyVideoProducer.setDefaultChroma(tmedia_chroma_t.tmedia_chroma_yuv422p);
//		proxyproducer.getProducer().setActualCameraOutputSize(fmt_pix_width, fmt_pix_height);
//		
//		outputFile=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.h264");
//		try {
//			fos=new FileOutputStream(outputFile);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		rgb_output=new int[fmt_pix_width*fmt_pix_height];
//	}
//	
///*	public USBCamera(Handler handler)
//	{
//	    sCameraDevName="/dev/video0";
//		fmt_pix_width =640;
//	    fmt_pix_height=480;	
//	    fmt_frame_rate=30;	//30fps
//	    fmt_pix_format=CAMERA_CAPTURE_YUYV;
//	    this.producerHandler=handler;
//
//		mVideoFrame = ByteBuffer.allocateDirect(1920*1080*4);
//
//	}*/
//	
//	/*public USBCamera(MySurfaceView surfaceView)
//	{
//	    sCameraDevName="/dev/video0";
//		fmt_pix_width =640;
//	    fmt_pix_height=480;	
//	    fmt_frame_rate=30;	//30fps
//	    fmt_pix_format=CAMERA_CAPTURE_YUYV;   
//
//		mVideoFrame = ByteBuffer.allocateDirect(1920*1080*4);
//		this.surfaceView=surfaceView;
//
//	}*/
//	
//	 static {
//	      //  System.loadLibrary("libv_capture");
//	    }
//	    
//	//JNI 
//	private native int SetDevName(String sDevName);
//	private native int SetCaptureParameters(int pix_width, int pix_height, int frame_rate, int pix_format, int do_pixels2bmp);
//	private native int StartCapture(byte[] buffer);
//	private native int StopCapture();
//	
//	private native int NV21toARGB(byte[] rgb_buf, byte[] nv21_buf, int width, int height);
//
//	private native int YUYVtoARGB(int[] rgb_buf, byte[] yuyv_buf, int width, int height);
//
//	private native int Pixels2BMP(Bitmap bmp);
//	  
//	public int Init()
//	{
//	    SetDevName(sCameraDevName);
//		SetCaptureParameters(fmt_pix_width, fmt_pix_height, fmt_frame_rate, fmt_pix_format,0);
//		Log.i(TAG, "Init()...");
//		return 0;
//	}
//	  
//	// JNI Callback
//	public int OnNewVideoFrameCallback(int size_byteFrameData)
//	{
//	    // To process the captured frame: byteFrameData/size_byteFrameData
//	    //TODO
//		//mVideoFrame=ByteBuffer.wrap(byteFrameData);
//		mVideoFrame.clear();//把position设为0，把limit设为capacity，一般在把数据写入Buffer前调用。
//		mVideoFrame.put(byteFrameData,0,size_byteFrameData);
//		mVideoFrame.flip();//把limit设为当前position，把position设为0，一般在从Buffer读出数据前调用。
//		if(proxyproducer!=null)
//			proxyproducer.getProducer().push(mVideoFrame, size_byteFrameData);
//		/*if(producerHandler!=null)
//			producerHandler.obtainMessage(0, size_byteFrameData,-1,mVideoFrame).sendToTarget();*/
//		if(surfaceView!=null){
//			Bitmap bmp=Bitmap.createBitmap(fmt_pix_width, fmt_pix_height, Bitmap.Config.ARGB_8888);
//			YUYVtoARGB(rgb_output, byteFrameData, fmt_pix_width, fmt_pix_height);
//			//Pixels2BMP(bmp);//YUYVtoARGB(byte[] rgb_buf, byte[] yuyv_buf, int width, int height);
//			//mVideoFrame.rewind();
//			ByteBuffer bb=ByteBuffer.allocateDirect(bmp.getByteCount());
//			bmp=Bitmap.createBitmap((int [])rgb_output, fmt_pix_width, fmt_pix_height, Bitmap.Config.ARGB_8888);
//			//bb.put(byteFrameData,0,bmp.getByteCount());
//			//bb.rewind();
//			//bmp.copyPixelsFromBuffer(bb);
//			surfaceView.setBitmap(bmp);
//		}
//		
//		if (fmt_pix_format==CAMERA_CAPTURE_H264) {
//			try {
//				//Log.i(TAG, "writing file.."+size_byteFrameData);
//				fos.write(byteFrameData,0,size_byteFrameData);
//				fos.flush();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		//mVideoFrame.rewind();
//		Log.i(TAG, "new Frame...   "+size_byteFrameData+" "+fmt_pix_width+" "+ fmt_pix_height+" "+ fmt_pix_format+" "+ fmt_frame_rate);
//
//	    return 0;
//	}
//	    
//    public int Start()
//	{
//	    //TODO: Create a new Thread
//		// to run "StartCapture();" 
//    	caputureThread=new Thread(){
//    		@Override
//			public void run(){
//    			try {
//					StartCapture(byteFrameData);
//					if(shouldStop)
//						return;
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//    		}
//    	};
//    	caputureThread.start();
//	   return 0;
//	}
//    
//	public int Stop()
//	{
//	    
//	    StopCapture();
//		//TODO: Kill the capture Thread...
//	    if(caputureThread.isAlive())
//	    	caputureThread.interrupt();
//	    Log.i(TAG, "thread interrupted...");
//		shouldStop=true;
//		try {
//			fos.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return 0;
//	}
//	public void setDisplayView(MySurfaceView view){
//		this.surfaceView=view;
//	};
//	
//	public int setParams(int pix_width, int pix_height, int frame_rate, int pix_format){
//		this.fmt_pix_width=pix_width;
//		this.fmt_pix_height=pix_height;
//		this.fmt_frame_rate=frame_rate;
//		this.fmt_pix_format=pix_format;
//		this.fmt_pix_width=pix_width;
//		//mVideoFrame = ByteBuffer.allocateDirect((fmt_pix_width*fmt_pix_height*3)>>1);
//		return SetCaptureParameters(pix_width, pix_height, frame_rate, pix_format,0);
//	}
//}
//		
