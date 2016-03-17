/* Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, imshello Telecom.
*
* Contact: Mamadou Diop <diopmamadou(at)imshello(dot)org>
*	
* This file is part of imsdroid Project (http://code.google.com/p/imsdroid)
*
* imsdroid is free software: you can redistribute it and/or modify it under the terms of 
* the GNU General Public License as published by the Free Software Foundation, either version 3 
* of the License, or (at your option) any later version.
*	
* imsdroid is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
* See the GNU General Public License for more details.
*	
* You should have received a copy of the GNU General Public License along 
* with this program; if not, write to the Free Software Foundation, Inc., 
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package org.imshello.droid.Screens;

import org.imshello.droid.Engine;
import org.imshello.droid.NativeService;
import org.imshello.droid.R;
import org.imshello.ngn.services.INgnSipService;
import org.imshello.ngn.utils.NgnConfigurationEntry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class ScreenSplash extends BaseScreen {
	
	private final String SHARE_LOGIN_TAG = "IMS";
	private static String TAG = ScreenSplash.class.getCanonicalName();
	public static int count = 0;
	public static int time = 0;
	private BroadcastReceiver mBroadCastRecv;
	private final INgnSipService mSipService;
	 SharedPreferences share;
	public ScreenSplash() {
		super(SCREEN_TYPE.SPLASH_T, TAG);
		mSipService = getEngine().getSipService();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);     
		setContentView(R.layout.screen_splash);
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		//获取本地存储的用户信息
		 share = this.getSharedPreferences(SHARE_LOGIN_TAG,0);
		 if(share.contains("name")&&share.contains("pass"))
		 {
		 final String name = share.getString("name", "");
		 final String pass = share.getString("pass", "");
		 }
		
		mBroadCastRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				Log.d(TAG, "onReceive()");
				
				if(NativeService.ACTION_STATE_EVENT.equals(action)){
					if(intent.getBooleanExtra("started", false)){
						if(share.contains("name")&&share.contains("pass"))
						{
							if(networkInfo == null || !networkInfo.isAvailable())
							{
								mScreenService.show(Login.class);
								Toast.makeText(getApplicationContext(), "无网络。。。。请连接网络后登录", Toast.LENGTH_SHORT).show();
							}
							else
							{
							//if(mSipService.register(getApplicationContext()))
							//{
							 mScreenService.show(ScreenMain.class);
							//}
							}
							
						}
						else
						{
						    mScreenService.show(Login.class);
						}
						getEngine().getConfigurationService().putBoolean(NgnConfigurationEntry.GENERAL_AUTOSTART, true);
						finish();
					}
				}
			}
		};
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NativeService.ACTION_STATE_EVENT);
	    registerReceiver(mBroadCastRecv, intentFilter);
	}
	
	@Override
	protected void onDestroy() {
		if(mBroadCastRecv != null){
			unregisterReceiver(mBroadCastRecv);
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		final Engine engine = getEngine();
			
		final Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				if(!engine.isStarted()){
					Log.d(TAG, "Starts the engine from the splash screen");
					engine.start();
				}
			}
		});
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}
}