package org.imshello.droid.Screens;

import org.imshello.droid.Engine;
import org.imshello.droid.R;
import org.imshello.ngn.services.INgnConfigurationService;
import org.imshello.ngn.services.INgnSipService;
import org.imshello.ngn.utils.NgnConfigurationEntry;
import org.imshello.ngn.utils.NgnStringUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SwitchUser extends BaseScreen{

	private ProgressDialog pBar;	
	private final INgnSipService mSipService;
	private final INgnConfigurationService mConfigurationService;
	private static String TAG = SwitchUser.class.getCanonicalName();
	 SharedPreferences sharenew;
	 //判断是否联网
	 ConnectivityManager connectivityManager;
		NetworkInfo networkInfo;
	//设置sharepreference共享标志
			private final String SHARE_LOGIN_TAG = "IMS";
	   //是否记录密码标志
			int status = 1;
			EditText edit_name;
			EditText edit_password;
			CheckBox remember;
	    	Button btn_login ;
	    	Button btn_register;
		    String name = "";	
	        String pass = "";
	
	public SwitchUser() {
		
		super(SCREEN_TYPE.LOGIN_T, TAG);
		// TODO Auto-generated constructor stub
		mSipService = getEngine().getSipService();
		mConfigurationService = getEngine().getConfigurationService();
	}
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		
		edit_name = (EditText)findViewById(R.id.name);
		edit_password = (EditText)findViewById(R.id.password);
		remember = (CheckBox)findViewById(R.id.is_remember);
		btn_login = (Button)findViewById(R.id.btn_login);
		btn_register = (Button)findViewById(R.id.regist);
		

		sharenew = this.getSharedPreferences(SHARE_LOGIN_TAG,0);
		 if(sharenew.contains("name")&&sharenew.contains("pass"))
		 {
		 final String namestore = sharenew.getString("name", "");
		 final String passstore = sharenew.getString("pass", "");
		 edit_name.setText(namestore);
		 edit_password.setText(passstore);
		 }
		 
		
	
			btn_login.setOnClickListener(btn_login_onClick);
			btn_register.setOnClickListener(btn_register_onClick);
		
	}

	
	//登录按钮点击事件
	private OnClickListener btn_login_onClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			 connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
			 networkInfo = connectivityManager.getActiveNetworkInfo();
			name = edit_name.getText().toString().trim();
			pass = edit_password.getText().toString().trim();
			if(networkInfo == null || !networkInfo.isAvailable())
			{
				Toast.makeText(getApplicationContext(), "无网络。。。。", Toast.LENGTH_SHORT).show();
				new AlertDialog.Builder(SwitchUser.this).setTitle("提示")
				                                                       .setMessage("无网络可用")
				                                                       .setPositiveButton("确定", null).show();
			}
			else 
			{
			if(name!=""&&pass!="")
			{
			Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
			
			String impu = "sip:"+name+"@"+"imshello.com";
			mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, 
					impu);
			mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, 
					name);
			mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, 
					pass);
			mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, 
					"114.215.176.27");
			mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, 
					NgnStringUtils.parseInt("5060", NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_PORT) );
			mConfigurationService.putBoolean(NgnConfigurationEntry.NETWORK_USE_EARLY_IMS, 
					false);
			mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, 
					"imshello.com");
			mConfigurationService.commit();
			if(mSipService.register(SwitchUser.this))
			 {
				
				if(remember.isChecked())
				{
					Store();
				}	
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				  imm.hideSoftInputFromWindow(SwitchUser.this .getCurrentFocus().getWindowToken(),  
                          InputMethodManager.HIDE_NOT_ALWAYS);  
			  mScreenService.show(ScreenMain.class);
			 }
			else
			{
				Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
			}
			}
		}
		}    
	   };  

	//注册按钮点击事件
	   private OnClickListener btn_register_onClick = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			mScreenService.show(Register.class);
		}
	};
	//将用户数据存在本地
	public void Store()
	{
		 SharedPreferences share = this.getSharedPreferences(SHARE_LOGIN_TAG,0);
		 Editor editor = share.edit();
		 editor.putString("name", name);
		 editor.putString("pass", pass);
		 editor.commit();
	}
	
	
	
	
	@Override
	public boolean hasBack() {
		return true;
	}
	 @Override
	 public boolean back()
	 {
		//System.exit(0);
		 mScreenService.show(ScreenMain.class);
	    return true;
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
