/* Copyright (C) 2010-2014, IMSHello R&D Group
*  
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.imshello.baseWRAP.ProxyVideoProducer;
import org.imshello.baseWRAP.tmedia_chroma_t;
import org.imshello.ngn.NgnApplication;
import org.imshello.ngn.NgnEngine;
import org.imshello.ngn.services.INgnConfigurationService;
import org.imshello.ngn.utils.NgnConfigurationEntry;
import org.imshello.utils.USBCamera;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class NgnUSBCameraProducer {
	private static final String TAG = NgnUSBCameraProducer.class.getCanonicalName();
	private static USBCamera instance;
	//private static boolean useFrontFacingCamera;
	
	// Default values
	private static int fps = 15;
	private static int width = 176;
	private static int height = 144;
	private static SurfaceHolder holder = null;
	private static SurfaceView surfaceView = null;
	private static MyUSBCameraCallback callback = null;
	
	private static final int MIN_SDKVERSION_addCallbackBuffer = 7;
	private static final int MIN_SDKVERSION_setPreviewCallbackWithBuffer = 7;
	private static final int MIN_SDKVERSION_setDisplayOrientation = 8;
	//private static final int MIN_SDKVERSION_getSupportedPreviewSizes = 5;
	
	/*private static Method addCallbackBufferMethod = null;
	private static Method setDisplayOrientationMethod = null;
	private static Method setPreviewCallbackWithBufferMethod = null;*/
	
	/*static{
		NgnUSBCameraProducer.useFrontFacingCamera = NgnEngine
				.getInstance().getConfigurationService().getBoolean(NgnConfigurationEntry.GENERAL_USE_FFC,
						NgnConfigurationEntry.DEFAULT_GENERAL_USE_FFC);
	}*/
	
	/*static{
		if(NgnApplication.getSDKVersion() >= NgnUSBCameraProducer.MIN_SDKVERSION_addCallbackBuffer){
			// According to http://developer.android.com/reference/android/hardware/Camera.html both addCallbackBuffer and setPreviewCallbackWithBuffer
			// are only available starting API level 8. But it's not true as these functions exist in API level 7 but are hidden.
			try {
				NgnUSBCameraProducer.addCallbackBufferMethod = Camera.class.getMethod("addCallbackBuffer", byte[].class);
			} catch (Exception e) {
				Log.e(NgnUSBCameraProducer.TAG, e.toString());
			} 
		}
		
		if(NgnApplication.getSDKVersion() >= NgnUSBCameraProducer.MIN_SDKVERSION_setPreviewCallbackWithBuffer){
			try {
				NgnUSBCameraProducer.setPreviewCallbackWithBufferMethod = Camera.class.getMethod(
					"setPreviewCallbackWithBuffer", PreviewCallback.class);
			}  catch (Exception e) {
				Log.e(NgnUSBCameraProducer.TAG, e.toString());
			}
		}
				
		if(NgnApplication.getSDKVersion() >= NgnUSBCameraProducer.MIN_SDKVERSION_setDisplayOrientation){
			try {
				NgnUSBCameraProducer.setDisplayOrientationMethod = Camera.class.getMethod("setDisplayOrientation", int.class);
			} catch (Exception e) {
				Log.e(NgnUSBCameraProducer.TAG, e.toString());
			} 
		}
	}*/
	
	public static USBCamera getCamera(){
		return NgnUSBCameraProducer.instance;
	}
	
	public static USBCamera openCamera(int fps, int width, int height, SurfaceView surfaceView, MyUSBCameraCallback callback){
		if(NgnUSBCameraProducer.instance == null){
			try{
				/*if(NgnUSBCameraProducer.useFrontFacingCamera){
					NgnUSBCameraProducer.instance = NgnUSBCameraProducer.openFrontFacingCamera();
				}*/
				//else{
					NgnUSBCameraProducer.instance = new USBCamera();
				//}
				
				NgnUSBCameraProducer.fps = fps;
				NgnUSBCameraProducer.width = width;
				NgnUSBCameraProducer.height = height;
				NgnUSBCameraProducer.holder = surfaceView.getHolder();
				NgnUSBCameraProducer.surfaceView = surfaceView;
				NgnUSBCameraProducer.callback = callback;
				
				//Camera.Parameters parameters = NgnUSBCameraProducer.instance.getParameters();
				/*设置camera参数 像素格式、帧率、宽、高*/
				/*
				 * http://developer.android.com/reference/android/graphics/ImageFormat.html#NV21
				 * YCrCb format used for images, which uses the NV21 encoding format. 
				 * This is the default format for camera preview images, when not otherwise set with setPreviewFormat(int). 
				 */
				/*parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);
				parameters.setPreviewFrameRate(NgnUSBCameraProducer.fps);
				NgnUSBCameraProducer.instance.setParameters(parameters);*/
				INgnConfigurationService configureService=NgnEngine	.getInstance().getConfigurationService();
				NgnUSBCameraProducer.instance.Init();
				//NgnUSBCameraProducer.instance.setParams(width, height, fps, USBCamera.CAMERA_CAPTURE_YUYV,USBCamera.STREAM_FMT_YUV420P,USBCamera.CALLBACK_REQUEST_BITMAP);
				ProxyVideoProducer.setDefaultChroma(tmedia_chroma_t.tmedia_chroma_yuv420p);
				Log.e("2414", "capture fmt: "+configureService.getInt(NgnConfigurationEntry.CAPTURE_FMT,NgnConfigurationEntry.DEFAULT_CAPTURE_FMT));
				NgnUSBCameraProducer.instance.setParams(width, height, fps, 
						configureService.getInt(NgnConfigurationEntry.CAPTURE_FMT,NgnConfigurationEntry.DEFAULT_CAPTURE_FMT),
						configureService.getInt(NgnConfigurationEntry.STREAM_FMT,NgnConfigurationEntry.DEFAULT_STREAM_FMT),
						configureService.getInt(NgnConfigurationEntry.LOCAL_VIEW,NgnConfigurationEntry.DEFAULT_LOCAL_VIEW));
				/*try{
					parameters.setPictureSize(NgnUSBCameraProducer.width , NgnUSBCameraProducer.height);
					NgnUSBCameraProducer.instance.setParameters(parameters);
				}
				catch(Exception e){
					// FFMpeg converter will resize the video stream
					Log.d(NgnUSBCameraProducer.TAG, e.toString());
				}*/
				
				NgnUSBCameraProducer.instance.setDisplayView(NgnUSBCameraProducer.surfaceView);
				NgnUSBCameraProducer.initializeCallbacks(NgnUSBCameraProducer.callback);
				
				
			}
			catch(Exception e){
				NgnUSBCameraProducer.releaseCamera();
				Log.e(NgnUSBCameraProducer.TAG, e.toString());
			}
		}
		return NgnUSBCameraProducer.instance;
	}
	
	public static void releaseCamera(USBCamera camera){
		if(camera != null){
			camera.Stop();
			NgnUSBCameraProducer.deInitializeCallbacks(camera);
			//camera.release();
			if(camera == NgnUSBCameraProducer.instance){
				NgnUSBCameraProducer.instance = null;
			}
		}
	}
	
	public static void releaseCamera(){
		if(NgnUSBCameraProducer.instance != null){
			NgnUSBCameraProducer.instance.Stop();
			NgnUSBCameraProducer.deInitializeCallbacks();
			//NgnUSBCameraProducer.instance.release();
			NgnUSBCameraProducer.instance = null;
		}
	}
/*	
	public static void setDisplayOrientation(int degrees){
		if(NgnUSBCameraProducer.instance != null && NgnUSBCameraProducer.setDisplayOrientationMethod != null){
			try {
				NgnUSBCameraProducer.setDisplayOrientationMethod.invoke(NgnUSBCameraProducer.instance, degrees);
			} catch (Exception e) {
				Log.e(NgnUSBCameraProducer.TAG, e.toString());
			}
		}
	}
	
	public static void setDisplayOrientation(Camera camera, int degrees){
		if(camera != null && NgnUSBCameraProducer.setDisplayOrientationMethod != null){
			try {
				NgnUSBCameraProducer.setDisplayOrientationMethod.invoke(camera, degrees);
			} catch (Exception e) {
				Log.e(NgnUSBCameraProducer.TAG, e.toString());
			}
		}
	}
	
	public static void addCallbackBuffer(Camera camera, byte[] buffer) {
		try {
			NgnUSBCameraProducer.addCallbackBufferMethod.invoke(camera, buffer);
		} catch (Exception e) {
			Log.e(NgnUSBCameraProducer.TAG, e.toString());
		}
	}
	
	public static void addCallbackBuffer(byte[] buffer) {
		try {
			NgnUSBCameraProducer.addCallbackBufferMethod.invoke(NgnUSBCameraProducer.instance, buffer);
		} catch (Exception e) {
			Log.e(NgnUSBCameraProducer.TAG, e.toString());
		}
	}

	public static boolean isAddCallbackBufferSupported(){
		return NgnUSBCameraProducer.addCallbackBufferMethod != null;
	}
	*/
	/*public static boolean isFrontFacingCameraEnabled(){
		return NgnUSBCameraProducer.useFrontFacingCamera;
	}*/
	
	/*public static void useRearCamera(){
		NgnUSBCameraProducer.useFrontFacingCamera = false;
	}*/
	
	/*public static void useFrontFacingCamera(){
		NgnUSBCameraProducer.useFrontFacingCamera = true;
	}*/
	
	/*public static Camera toggleCamera(){
		if(NgnUSBCameraProducer.instance != null){
			NgnUSBCameraProducer.useFrontFacingCamera = !NgnUSBCameraProducer.useFrontFacingCamera;
			NgnUSBCameraProducer.releaseCamera();
			NgnUSBCameraProducer.openCamera(NgnUSBCameraProducer.fps, 
					NgnUSBCameraProducer.width, 
					NgnUSBCameraProducer.height,
					NgnUSBCameraProducer.holder, 
					NgnUSBCameraProducer.callback);
		}
		return NgnUSBCameraProducer.instance;
	}*/
	
	private static void initializeCallbacks(MyUSBCameraCallback callback){
		initializeCallbacks(callback, NgnUSBCameraProducer.instance);
	}
	
	private static void initializeCallbacks(MyUSBCameraCallback callback, USBCamera camera){
		if(camera != null){
			/*if(NgnUSBCameraProducer.setPreviewCallbackWithBufferMethod != null){
				try {
					NgnUSBCameraProducer.setPreviewCallbackWithBufferMethod.invoke(camera, callback);
				} catch (Exception e) {
					Log.e(NgnUSBCameraProducer.TAG, e.toString());
				}
			}
			else{
				camera.setPreviewCallback(callback);
			}*/
			camera.setDataCallback(callback);
		}
	}
	
	private static void deInitializeCallbacks(){
		deInitializeCallbacks(NgnUSBCameraProducer.instance);
	}
	
	private static void deInitializeCallbacks(USBCamera camera){
		if(camera!= null){
			/*if(NgnUSBCameraProducer.setPreviewCallbackWithBufferMethod != null){
				try {
					NgnUSBCameraProducer.setPreviewCallbackWithBufferMethod.invoke(camera, new Object[]{ null });
				} catch (Exception e) {
					Log.e(NgnUSBCameraProducer.TAG, e.toString());
				}
			}
			else{
				camera.setDataCallback(null);
			}*/
			camera.setDataCallback(null);
		}
	}
	
	public static int getNumberOfCameras() {
		// 1. Android 2.3 or later
		if (NgnApplication.getSDKVersion() >= 9) {
			try {
				Method getNumberOfCamerasMethod = Camera.class.getDeclaredMethod("getNumberOfCameras");
				if (getNumberOfCamerasMethod != null) {
					return (Integer) getNumberOfCamerasMethod.invoke(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 1;
	}

	/*private static Camera openFrontFacingCamera() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Camera camera = null;
		
		// 1. Android 2.3 or later
		if(NgnApplication.getSDKVersion() >= 9){
			try {
				Method getNumberOfCamerasMethod = Camera.class.getDeclaredMethod("getNumberOfCameras");
				if(getNumberOfCamerasMethod != null){
					Integer numberOfCameras = (Integer)getNumberOfCamerasMethod.invoke(null);
					if(numberOfCameras > 1){
						Method openMethod = Camera.class.getDeclaredMethod("open", int.class);
						if((camera = (Camera)openMethod.invoke(null, (numberOfCameras - 1))) != null){
							return camera;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//2. From mapper
		if((camera = FrontFacingCameraMapper.getPreferredCamera()) != null){
			return camera;
		}
		
		//3. Use switcher
		if(FrontFacingCameraSwitcher.getSwitcher() != null){
			camera = Camera.open();
			FrontFacingCameraSwitcher.getSwitcher().invoke(camera, (int)1);
			return camera;
		}
		
		//4. Use parameters
		camera = Camera.open();
		Camera.Parameters parameters = camera.getParameters();
		parameters.set("camera-id", 2);
		camera.setParameters(parameters);
		return camera;
	}*/
	
	/***
	 * FrontFacingCameraSwitcher
	 * @author Mamadou Diop
	 *
	 */
	/*static class FrontFacingCameraSwitcher
	{
		private static Method DualCameraSwitchMethod;
		
		static{
			try{
				FrontFacingCameraSwitcher.DualCameraSwitchMethod = Class.forName("android.hardware.Camera").getMethod("DualCameraSwitch",int.class);
			}
			catch(Exception e){
				Log.d(NgnUSBCameraProducer.TAG, e.toString());
			}
		}
		
		static Method getSwitcher(){
			return FrontFacingCameraSwitcher.DualCameraSwitchMethod;
		}
	}*/
	
	/*static class FrontFacingCameraMapper
	{
		private static int preferredIndex = -1;
		
		static FrontFacingCameraMapper Map[] = {
			new FrontFacingCameraMapper("android.hardware.HtcFrontFacingCamera", "getCamera"),
			// Sprint: HTC EVO 4G and Samsung Epic 4G
			// DO not forget to change the manifest if you are using OS 1.6 and later
			new FrontFacingCameraMapper("com.sprint.hardware.twinCamDevice.FrontFacingCamera", "getFrontFacingCamera"),
			// Huawei U8230
            new FrontFacingCameraMapper("android.hardware.CameraSlave", "open"),
			// Default: Used for test reflection
			// new FrontFacingCameraMapper("android.hardware.Camera", "open"),
		};
		
		static{
			int index = 0;
			for(FrontFacingCameraMapper ffc: FrontFacingCameraMapper.Map){
				try{
					Class.forName(ffc.className).getDeclaredMethod(ffc.methodName);
					FrontFacingCameraMapper.preferredIndex = index;
					break;
				}
				catch(Exception e){
					Log.d(NgnUSBCameraProducer.TAG, e.toString());
				}
				
				++index;
			}
		}
		
		private final String className;
		private final String methodName;
		
		FrontFacingCameraMapper(String className, String methodName){
			this.className = className;
			this.methodName = methodName;
		}
		
		static Camera getPreferredCamera(){
			if(FrontFacingCameraMapper.preferredIndex == -1){
				return null;
			}
			
			try{				
				Method method = Class.forName(FrontFacingCameraMapper.Map[FrontFacingCameraMapper.preferredIndex].className)
				.getDeclaredMethod(FrontFacingCameraMapper.Map[FrontFacingCameraMapper.preferredIndex].methodName);
				return (Camera)method.invoke(null);
			}
			catch(Exception e){
				Log.e(NgnUSBCameraProducer.TAG, e.toString());
			}
			return null;
		}
	}*/
	
	public static interface MyUSBCameraCallback{
		abstract void onFrame(byte[]data, int size);
	}

}
