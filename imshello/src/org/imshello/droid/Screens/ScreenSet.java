package org.imshello.droid.Screens;

import org.imshello.droid.R;
import org.imshello.ngn.services.INgnConfigurationService;
import org.imshello.ngn.services.INgnSipService;
import org.imshello.ngn.sip.NgnSipSession.ConnectionState;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ScreenSet extends BaseScreen{
	private static String TAG = ScreenSet.class.getCanonicalName();
	private final INgnSipService mSipService;
	private Button login;
	private final INgnConfigurationService mConfigurationService;
	private BroadcastReceiver mSipBroadCastRecv;
	private ImageView back;
	
		public ScreenSet() {
			super(SCREEN_TYPE.ME_T, TAG);
			mSipService = getEngine().getSipService();
			// added 
				
				mConfigurationService = getEngine().getConfigurationService();
				
		}
		private View rootView;
		private String tabTextviewArray[];
		
		//定义Setting的文字
		private String setTextviewArray[];
		
		
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.screen_set);
			setTextviewArray = new String[]{this.getString(R.string.identity),this.getString(R.string.codes),
					this.getString(R.string.change),"Nat设置","网络设置","USB摄像头设置","Qos设置","控制器设置","缓冲队列设置"};
			View add = findViewById(R.id.add);
			View personalData = findViewById(R.id.personal_data);
			View codec_setting = findViewById(R.id.codec_setting);
			View change_pwd = findViewById(R.id.change_pwd);
			View network_setting = findViewById(R.id.network_setting);
			View natt_setting=findViewById(R.id.natt_setting);
			View usb_camera_setting=findViewById(R.id.usb_camera_setting);
			View qos_setting=findViewById(R.id.qos_setting);
			View controller_setting = findViewById(R.id.controller_setting);
			View bufferqueue_setting = findViewById(R.id.bufferqueue_setting);
			login = (Button)findViewById(R.id.login);
			back = (ImageView)findViewById(R.id.back);
			init();
			//返回按钮监听
              back.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					mScreenService.show(ScreenMain.class);
				}
			});
			login.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(mSipService.getRegistrationState() == ConnectionState.CONNECTING || mSipService.getRegistrationState() == ConnectionState.TERMINATING){
						mSipService.stopStack();
							login.setText(ScreenSet.this.getString(R.string.login));
						
					}
					else if(mSipService.isRegistered()){
						mSipService.unRegister();
						login.setText(ScreenSet.this.getString(R.string.login));
						
					}
					else{
						mSipService.register(ScreenSet.this);
					
						login.setText(ScreenSet.this.getString(R.string.logined));
					    
					}
					
				}
							
				}
			);
	
			//初始化设置
			initSettingItem(personalData, 0);
			initSettingItem(codec_setting, 1);
			initSettingItem(change_pwd, 2);
			initSettingItem(natt_setting,3);
			initSettingItem(network_setting, 4);
			initSettingItem(usb_camera_setting, 5);
			initSettingItem(qos_setting, 6);
			initSettingItem(controller_setting, 7);
			initSettingItem(bufferqueue_setting, 8);
			
			ScreenSplash.count = 0;
			
	    }   
        private void init()
        {
        	if(mSipService.getRegistrationState() == ConnectionState.CONNECTING || mSipService.getRegistrationState() == ConnectionState.TERMINATING){
				login.setText(ScreenSet.this.getString(R.string.login));
			}
			else if(mSipService.isRegistered()){
				login.setText(ScreenSet.this.getString(R.string.unlogin));
			}
			else{
				login.setText(ScreenSet.this.getString(R.string.login));
			}
        }
		/**
		 * 初始化设置
		 * @param view
		 * @param index
		 */
		private void initSettingItem(View view,int index){
			TextView text = (TextView) view.findViewById(R.id.text);		
			text.setText(setTextviewArray[index]);
			view.setOnClickListener(new MySettingOnClickListener(index));
			
		}
		
		
		private class MySettingOnClickListener implements View.OnClickListener { 

		   	 private int index = 0;
		   	 
		   	 public MySettingOnClickListener(int i) { 
		   		 index = i; 
		   	 } 
		   	    	 
		   	 @Override 
		   	 public void onClick(View v) { 
		   		 switch(index){
			   		 case 0:
			   			mScreenService.show(SettingsIdentity.class);
			   			 break;
			   		 case 1: 
			   			mScreenService.show(SettingsCodecs.class);
			   			 break;
			   		 case 2: 
			   			mScreenService.show(ChangePassword.class);
			   			 break;
			   		 case 3: 
			   			 mScreenService.show(SettingsNatt.class);
			   			 break;
			   		 case 4:
			   			 mScreenService.show(SettingsNetwork.class);
			   			 break;
			   		 case 5:
			   			mScreenService.show(SettingsUSBCamera.class);
			   			break;
			   		 case 6:
			   			mScreenService.show(SettingsQoS.class);
			   			break;
			   		case 7:
						mScreenService.show(SettingsController.class);
						break;
			   		case 8:
						mScreenService.show(SettingsBufferQueue.class);
						break;
			   		 default:
			   			 break;
		   		 }
		   	}
		}
		
		@Override
		public boolean back()
		{
			 mScreenService.show(ScreenMain.class);
			boolean ret = mScreenService.show(ScreenMain.class);
			if(ret){
				mScreenService.destroy(getId());
			}
			this.finish();
			return ret;
		}
		

	}



