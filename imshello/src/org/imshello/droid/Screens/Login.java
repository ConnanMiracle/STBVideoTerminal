package org.imshello.droid.Screens;

import org.imshello.droid.Engine;
import org.imshello.droid.R;
import org.imshello.droid.QuickAction.TipPopupWindow;
import org.imshello.ngn.NgnEngine;
import org.imshello.ngn.services.INgnConfigurationService;
import org.imshello.ngn.services.INgnSipService;
import org.imshello.ngn.utils.NgnConfigurationEntry;
import org.imshello.ngn.utils.NgnStringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Login extends BaseScreen {
	
	public final String DEFAULT_NETWORK_REALM="imshello.org";
	public final String DEFAULT_PCSCF_HOST="202.118.18.8";

	private ProgressDialog pBar;
	private ImageView image;
	private TipPopupWindow menuWindow;
	private final INgnSipService mSipService;
	private final INgnConfigurationService mConfigurationService;
	private static String TAG = Login.class.getCanonicalName();
	SharedPreferences sharenew;
	
	private String domain="";
	private String pCscfIP="";

	private BroadcastReceiver receiver;
	// 判断是否联网
	ConnectivityManager connectivityManager;
	NetworkInfo networkInfo;
	// 设置sharepreference共享标志
	private final String SHARE_LOGIN_TAG = "IMS";
	// 是否记录密码标志
	int status = 1;
	EditText edit_name;
	EditText edit_password;
	CheckBox remember;
	Button btn_login;
	Button btn_register;
	Button other;
	String name = "";
	String pass = "";

	public Login() {

		super(SCREEN_TYPE.LOGIN_T, TAG);
		// TODO Auto-generated constructor stub
		mSipService = getEngine().getSipService();
		mConfigurationService = getEngine().getConfigurationService();
		pCscfIP=mConfigurationService.getString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_HOST);
		domain=mConfigurationService.getString(NgnConfigurationEntry.NETWORK_REALM, NgnConfigurationEntry.DEFAULT_NETWORK_REALM);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);

		edit_name = (EditText) findViewById(R.id.name);
		edit_password = (EditText) findViewById(R.id.password);
		remember = (CheckBox) findViewById(R.id.is_remember);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_register = (Button) findViewById(R.id.regist);
		image = (ImageView) findViewById(R.id.tiptext);
		other = (Button) findViewById(R.id.other);

		if (Register.user != "" && Register.pass != "") {
			edit_name.setText(Register.user);
			edit_password.setText(Register.pass);
			name = Register.user;
			pass = Register.pass;
		}

		// 提示按钮的点击
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				settingImageOnClick(Login.this);
			}
		});
		other.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mScreenService.show(SettingsIdentity.class);
			}
		});
		sharenew = this.getSharedPreferences(SHARE_LOGIN_TAG, 0);
		if (sharenew.contains("name") && sharenew.contains("pass")) {
			final String namestore = sharenew.getString("name", "");
			final String passstore = sharenew.getString("pass", "");
			edit_name.setText(namestore);
			edit_password.setText(passstore);
		}

		btn_login.setOnClickListener(btn_login_onClick);
		btn_register.setOnClickListener(btn_register_onClick);

	}

	// 登录按钮点击事件
	private OnClickListener btn_login_onClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			doLogin();
		}
	};

	// 注册按钮点击事件
	private OnClickListener btn_register_onClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			mScreenService.show(Register.class);
		}
	};

	// 执行登录操作
	public void doLogin() {
		connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		networkInfo = connectivityManager.getActiveNetworkInfo();
		name = edit_name.getText().toString().trim();
		pass = edit_password.getText().toString().trim();
		if (networkInfo == null || !networkInfo.isAvailable()) {
			Toast.makeText(getApplicationContext(), "无网络。。。。",
					Toast.LENGTH_SHORT).show();
			new AlertDialog.Builder(Login.this).setTitle("提示")
					.setMessage("无网络可用").setPositiveButton("确定", null).show();
		} else {
			if (name != "" && pass != "") {
				Toast.makeText(getApplicationContext(), name,
						Toast.LENGTH_SHORT).show();
				String impu= "sip:" + name + "@" +mConfigurationService.getString(NgnConfigurationEntry.NETWORK_REALM, NgnConfigurationEntry.DEFAULT_NETWORK_REALM);
				/*if(domain.equals(""))
					 impu = "sip:" + name + "@" + DEFAULT_NETWORK_REALM;
				else
					 impu = "sip:" + name + "@" + domain;*/
				mConfigurationService.putString(
						NgnConfigurationEntry.IDENTITY_IMPU, impu);
				mConfigurationService.putString(
						NgnConfigurationEntry.IDENTITY_IMPI, name);
				mConfigurationService.putString(
						NgnConfigurationEntry.IDENTITY_PASSWORD, pass);
				mConfigurationService.putString(
							NgnConfigurationEntry.NETWORK_PCSCF_HOST,
							mConfigurationService.getString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_HOST));
				mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT,
								NgnStringUtils.parseInt("5060",	NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_PORT));
				mConfigurationService.putBoolean(
						NgnConfigurationEntry.NETWORK_USE_EARLY_IMS, false);
				
				/*mConfigurationService.putString(
							NgnConfigurationEntry.NETWORK_REALM, DEFAULT_NETWORK_REALM);*/
				
				mConfigurationService.commit();
				if (mSipService.register(Login.this)) {

					if (remember.isChecked()) {
						Store();
					}
					if (getCurrentFocus() != null) {
						((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
								.hideSoftInputFromWindow(getCurrentFocus()
										.getWindowToken(),
										InputMethodManager.HIDE_NOT_ALWAYS);
					}
					mScreenService.show(ScreenMain.class);
				} else {
					Toast.makeText(getApplicationContext(), "登录失败",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	// 将用户数据存在本地
	public void Store() {
		final SharedPreferences share = this.getSharedPreferences(
				SHARE_LOGIN_TAG, 0);
		Runnable run = new Runnable() {
			@Override
			public void run() {
				Editor editor = share.edit();
				editor.putString("name", name);
				editor.putString("pass", pass);
				editor.commit();
			}
		};

		new Thread(run).start();
	}

	@Override
	public boolean hasBack() {
		return true;
	}

	@Override
	public boolean back() {
		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle("提示")
				.setMessage(R.string.exit_alert)
				.setIcon(R.drawable.icon)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								NgnEngine.getInstance().stop();// added by wangy
																// 20140711
								System.exit(0);
							}
						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}
						}).create();
		alertDialog.show();
		return true;
	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { // TODO
	 * Auto-generated method stub //按下键盘上返回按钮 if(keyCode ==
	 * KeyEvent.KEYCODE_BACK) { AlertDialog alertDialog = new
	 * AlertDialog.Builder(this). setTitle("提示").
	 * setMessage(R.string.exit_alert). setIcon(R.drawable.icon).
	 * setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { //
	 * TODO Auto-generated method stub NgnEngine.getInstance().stop();//added by
	 * wangy 20140711 System.exit(0); } }). setNegativeButton(R.string.no, new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { //
	 * TODO Auto-generated method stub } }). create(); alertDialog.show();
	 * //System.exit(0); return true; } else {
	 * 
	 * return super.onKeyDown(keyCode, event); } }
	 */

	@Override
	protected void onResume() {
		super.onResume();
		final Engine engine = getEngine();

		final Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (!engine.isStarted()) {
					Log.d(TAG, "Starts the engine from the splash screen");
					engine.start();
				}
			}
		});
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	// 弹出提示窗口的相应设置
	public void settingImageOnClick(final Activity context) {
		menuWindow = new TipPopupWindow(Login.this);
		menuWindow.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.tip_text2));
		// 显示窗口
		menuWindow.showAtLocation(Login.this.findViewById(R.id.tiptext),
				Gravity.TOP | Gravity.LEFT, 120, 90); // 设置layout在PopupWindow中显示的位置

	}
}
