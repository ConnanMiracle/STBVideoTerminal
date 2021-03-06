/* Copyright (C) 2010-2014, IMSHello R&D Group
*  Contact:  <rd(at)imshello(dot)org>
*  This file is part of 'IMSHello for Android' Project 
*
*/
package org.imshello.ngn;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Android native service running in the background. This service is started but the engine.
 */
public class NgnNativeService extends Service {
	private final static String TAG = NgnNativeService.class.getCanonicalName();
	
	public NgnNativeService(){
		super();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(TAG, "onBind()");
		return null;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind()");
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate()");
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(TAG, "onStart()");
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy()");
		super.onDestroy();
	}
}
