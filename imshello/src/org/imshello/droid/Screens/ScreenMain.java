package org.imshello.droid.Screens;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.imshello.baseWRAP.SipStack;
import org.imshello.baseWRAP.tdav_codec_id_t;
import org.imshello.droid.R;
import org.imshello.droid.Utils.CurrentVersion;
import org.imshello.droid.Utils.CustomDialog;
import org.imshello.droid.Utils.GetUpdateInfo;
import org.imshello.droid.controller.ControllerService;
import org.imshello.droid.QuickAction.SelectAddPopupWindow;
import org.imshello.droid.QuickAction.SelectOptionPopupWindow;
import org.imshello.droid.Screens.BaseScreen.SCREEN_TYPE;
import org.imshello.ngn.NgnEngine;
import org.imshello.ngn.services.INgnConfigurationService;
import org.imshello.ngn.services.INgnNetworkService;
import org.imshello.ngn.services.INgnSipService;
import org.imshello.ngn.utils.NgnConfigurationEntry;
import org.json.JSONArray;
import org.json.JSONObject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.TranslateAnimation;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class ScreenMain extends BaseTabScreen implements OnTouchListener {

	private int i = 0;// 指示当先的tabhost页面
	private String networkName="";
	private String networkType="";
	private static boolean isBroadcastReceiverRegistered=false;
	private long lastTime=0;
	private long backTime=0;
	public ScreenMain() {
		super(SCREEN_TYPE.MAIN_T, TAG);
		mSipService = getEngine().getSipService();
		mNetworkService=getEngine().getNetworkService();
		mConfigurationService= getEngine().getConfigurationService();
	}

	private static String TAG = ScreenMain.class.getCanonicalName();

	// tab栏相关
	private TabHost tabs = null;
	private TabWidget tabwidget = null;
	private ImageView set;
	private ImageView add;
	private TranslateAnimation mTranslateAnimation;
	private ImageView mImageView;
	private int mTabSpecWidth = 0;
	int startX = 0;
	int endX = 0;
	// 自定义的弹出框类
	SelectOptionPopupWindow menuWindow; // 弹出框
	SelectAddPopupWindow menuWindow2; // 弹出框

	LinearLayout layout;

	private BroadcastReceiver receiver;
	private final INgnSipService mSipService;
	private final INgnNetworkService mNetworkService;
	private final INgnConfigurationService mConfigurationService;
	// 检查更新相关
	private ProgressDialog pBar;
	private int newVerCode = 0;
	private String newVerName = "";
	private int fileLength = 0;
	int downedFileLength = 0;
	static Dialog updateDialog;
	// public static Config c = new Config();
	public static int qq = 7;
	public static Integer count;
	public static String test="abc";

	// 手势切换 added2014.5.14
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private int maxTabIndex = 2;

	static ControllerService controllerService=null;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			System.out.println("msg.what=" + msg.what);
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case 0:
					new Thread() {
						@Override
						public void run() {
							try {
								checkToUpdate();
							} catch (NameNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();

					break;
				case 2: // 提示是否安装
					pBar.cancel();
					CustomDialog.show(ScreenMain.this, R.drawable.icon, null,
							"是否立即安装？", "确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									installNewApk();
									finish();
								}
							}, "取消", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
					break;
				case 11:// 显示更新对话框
					try {
						showUpdateDialog();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 12:// 开始下载文件
					new Thread() {
						@Override
						public void run() {
							downAppFile(GetUpdateInfo.downPathString);
						}
					}.start();
					break;
				default:
					break;
				}

			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.screen_main);
		layout = (LinearLayout) findViewById(R.id.llayout);
		init();
		initListener();
		initReceiver();
		
		if(controllerService==null){
			//检查设置
			if(mConfigurationService.getBoolean(NgnConfigurationEntry.CONTROLLER_SWITCH, NgnConfigurationEntry.DEFAULT_CONTROLLER_SWITCH)){
				controllerService = ControllerService.getInstance(this);
				Log.i(TAG, "controller service is started!");
			}
	    	//controllerService.start();	        
		}
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};

		/*Thread thread = new SendMessage(30, true);
		thread.start();*/
		SipStack.setCodecPriority_2(
				tdav_codec_id_t.tdav_codec_id_opus.swigValue(), 0);
		SipStack.setCodecPriority_2(
				tdav_codec_id_t.tdav_codec_id_ilbc.swigValue(), 1);
		SipStack.setCodecPriority_2(
				tdav_codec_id_t.tdav_codec_id_g729ab.swigValue(), 2);
		SipStack.setCodecPriority_2(
				tdav_codec_id_t.tdav_codec_id_pcmu.swigValue(), 3);
		ConnectivityManager connectivityManager = (ConnectivityManager)

				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager
				.getActiveNetworkInfo();
		networkName=info.getExtraInfo();
		networkType=info.getTypeName();
		/*if(!isBroadcastReceiverRegistered){
			registerR();
			isBroadcastReceiverRegistered=true;
		}*/
	}

	// 注册监听器
	public void registerR() {
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(receiver, filter);

	}
	
	private void initReceiver(){
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO 网络状况改变后的重注册
				if (arg1.getAction().equals(
						"android.net.conn.CONNECTIVITY_CHANGE")) {
					
					ConnectivityManager connectivityManager = (ConnectivityManager)

					getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo info = connectivityManager
							.getActiveNetworkInfo();
					
					if (info != null && info.isAvailable()) {
						
						long time=System.currentTimeMillis();
						if(time-lastTime>1000){
							//mNetworkService.stop();
							//mNetworkService.forceConnectToNetwork();
							//mSipService.register(ScreenMain.this);
							mNetworkService.triggerSipRegistration();
							/*handler.postDelayed(new Runnable(){

								@Override
								public void run() {
									// TODO 自动生成的方法存根
									//mSipService.register(ScreenMain.this);
									
									Log.i("Broadcast","Re-Register");
								}
								
							}, 5000);*/
							lastTime=time;
							
						}
						else {
							lastTime=time;
							Log.i("Broadcast","NO");
							return;
						}
						/*
						String type = info.getTypeName();
						String name = info.getExtraInfo();
 						if(mSipService.getRegistrationState() == ConnectionState.CONNECTING || mSipService.getRegistrationState() == ConnectionState.TERMINATING){
							if((type.equals(networkType)&&(name.equals(networkName)))){
								return;
							}
							if (mSipService.register(ScreenMain.this)) {
								networkName=name;
								networkType=type;
								mScreenService.show(ScreenMain.class);
							}else
								mScreenService.show(Login.class);
						}						
						else if(mSipService.isRegistered()){
							if((type.equals(networkType)&&(name.equals(networkName)))){
								return;
							}else	{							
								mSipService.unRegister();
								mScreenService.show(Login.class);
								if (mSipService.register(ScreenMain.this)) {
									networkName=name;
									networkType=type;
								}else
									mScreenService.show(Login.class);
							}
						}
						else{
							if (mSipService.register(ScreenMain.this)) {
								networkName=name;
								networkType=type;
								mScreenService.show(ScreenMain.class);
							}else
								mScreenService.show(Login.class);
						}*/
						
						
						
					} /*else {
						mSipService.unRegister();
						Toast.makeText(getApplicationContext(),
								"没有可用网络,请设置网络后重新登录", Toast.LENGTH_SHORT).show();
						mScreenService.show(Login.class);
						//ScreenMain.this.finish();
					}*/
					
					/*if (!mSipService.register(ScreenMain.this)) {
						Toast.makeText(getApplicationContext(), "登录失败，请重试",
								Toast.LENGTH_SHORT).show();
						mScreenService.show(Login.class);

					}*/
				}
			}
		};
	}

	private void init() {
		adTab();
		onTabChange();
	}

	private void initListener() {
		// 为选项卡添加切换监听
		tabs.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				onTabChange();
			}
		});
	}

	/**
	 * 为选项卡添加item
	 */
	@SuppressLint("NewApi")
	private void adTab() {

		// 弹出框设置监听器
		set = (ImageView) findViewById(R.id.set);
		add = (ImageView) findViewById(R.id.add);
		set.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				settingImageOnClick(ScreenMain.this);
			}
		});
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				plusImageOnClick(ScreenMain.this);

			}
		});

		tabs = this.getTabHost();
		tabs.setup();

		tabwidget = tabs.getTabWidget();
		tabwidget.setStripEnabled(true);

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View tab_view1 = inflater.inflate(R.layout.screen_main_tab, null);
		TextView text1 = (TextView) tab_view1.findViewById(R.id.text);
		text1.setTextColor(Color.GRAY);
		text1.setText(getResources().getString(R.string.message));
		TabHost.TabSpec spec = tabs.newTabSpec("tab1");
		Intent intent1 = new Intent();
		intent1.setClass(this, TabMessages.class);
		spec.setIndicator(tab_view1);
		spec.setContent(intent1);
		tabs.addTab(spec);

		View tab_view2 = inflater.inflate(R.layout.screen_main_tab, null);
		TextView text2 = (TextView) tab_view2.findViewById(R.id.text);
		text2.setText(getResources().getString(R.string.address_book));
		text2.setTextColor(Color.BLACK);
		spec = tabs.newTabSpec("tab2");
		Intent intent2 = new Intent();
		intent2.setClass(this, TabContacts.class);
		spec.setIndicator(tab_view2);
		spec.setContent(intent2);
		tabs.addTab(spec);

		View tab_view3 = inflater.inflate(R.layout.screen_main_tab, null);
		TextView text3 = (TextView) tab_view3.findViewById(R.id.text);
		text3.setText(getResources().getString(R.string.service));
		text3.setTextColor(Color.BLACK);
		spec = tabs.newTabSpec("tab3");
		Intent intent3 = new Intent();
		intent3.setClass(this, TabLDAPContacts.class);
		spec.setIndicator(tab_view3);
		spec.setContent(intent3);
		tabs.addTab(spec);

		View tab_view4 = inflater.inflate(R.layout.screen_main_tab, null);
		TextView text4 = (TextView) tab_view4.findViewById(R.id.text);
		text4.setText(getResources().getString(R.string.set));
		text4.setTextColor(Color.BLACK);
		spec = tabs.newTabSpec("tab4");
		Intent intent4 = new Intent();
		intent4.setClass(this, ScreenSet.class);
		spec.setIndicator(tab_view4);
		spec.setContent(intent4);
		tabs.addTab(spec);

		onTabChange();
		tabs.setCurrentTab(ScreenSplash.count);
	}

	/**
	 * 切换选项卡时调用
	 */
	@SuppressLint("NewApi")
	private void onTabChange() {
		qq=tabs.getCurrentTab();
		count=Integer.valueOf(qq);
		test="hhh";
		// 切换选项卡的背景
		for (int i = 0; i < tabwidget.getChildCount(); i++) {
			View view = tabwidget.getChildAt(i);

			TextView tv = (TextView) view.findViewById(R.id.text);
			if (tabs.getCurrentTab() == i) {
				if (tabs.getCurrentTab() == 0) {
					tv.setTextColor(Color.GRAY);

					// view.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_cab_background_top_holo_light));
					view.setBackgroundColor(getResources().getColor(
							R.color.tab_background_selected));
				} else if (tabs.getCurrentTab() == 1) {
					tv.setTextColor(Color.GRAY);

					// view.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_cab_background_top_holo_light));
					view.setBackgroundColor(getResources().getColor(
							R.color.tab_background_selected));
				} else if (tabs.getCurrentTab() == 2) {
					tv.setTextColor(Color.GRAY);

					// view.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_cab_background_top_holo_light));
					view.setBackgroundColor(getResources().getColor(
							R.color.tab_background_selected));
				} else if (tabs.getCurrentTab() == 3) {
					tv.setTextColor(Color.GRAY);

					// view.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_cab_background_top_holo_light));
					view.setBackgroundColor(getResources().getColor(
							R.color.tab_background_selected));
				}

			} else if (i == 0) {
				// view.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_ab_share_pack_holo_dark));
				view.setBackgroundColor(getResources().getColor(
						R.color.tab_background));
				tv.setTextColor(Color.BLACK);
			} else if (i == 1) {
				// view.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_ab_share_pack_holo_dark));
				view.setBackgroundColor(getResources().getColor(
						R.color.tab_background));

				tv.setTextColor(Color.BLACK);
			} else if (i == 2) {
				// view.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_ab_share_pack_holo_dark));
				view.setBackgroundColor(getResources().getColor(
						R.color.tab_background));

				tv.setTextColor(Color.BLACK);
			} else if (i == 3) {
				tv.setTextColor(Color.BLACK);

				// view.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_ab_share_pack_holo_dark));
				view.setBackgroundColor(getResources().getColor(
						R.color.tab_background));
			}
		}
	}

	// 为弹出窗口设置监听响应
	public void settingImageOnClick(final Activity context) {
		int h = (int) (layout.getHeight() * 1.45);
		menuWindow = new SelectOptionPopupWindow(ScreenMain.this, itemsOnClick);
		// 显示窗口
		menuWindow.showAtLocation(ScreenMain.this.findViewById(R.id.set),
				Gravity.TOP | Gravity.RIGHT, 0, h); // 设置layout在PopupWindow中显示的位置

	}

	public void plusImageOnClick(final Activity context) {
		int h = (int) (layout.getHeight() * 1.45);
		menuWindow2 = new SelectAddPopupWindow(ScreenMain.this, itemsOnClick2);
		// 显示窗口
		menuWindow2.showAtLocation(ScreenMain.this.findViewById(R.id.add),
				Gravity.TOP | Gravity.RIGHT, 0, h); // 设置layout在PopupWindow中显示的位置
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// menuWindow.dismiss();
		}
	};

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick2 = new OnClickListener() {

		@Override
		public void onClick(View v) {
			menuWindow2.dismiss();
		}
	};

	//@SuppressWarnings("deprecation")
	@Override
	public void onResume() {
		super.onResume();

		// 进入时检查版本 2014.5.14
		if (ScreenSplash.time == 0) {
			if (isNetworkAvailable(this) == false) {
				// do nothing
				Log.e(TAG, "没有联网");
			} else {
				Message message = new Message();
				message.what = 0;
				handler.sendMessage(message);
				ScreenSplash.time = 1;
			}
		}
		//registerR();

	}
	

	/**
	 * 退出应用
	 */
	@Override
	public boolean back() {
		long time=System.currentTimeMillis();
		if(time-backTime>1000){
			Toast.makeText(getApplicationContext(), "再点击一次推出应用", Toast.LENGTH_SHORT).show();
			backTime=time;
			return true;
		}
		else{
			/*if(isBroadcastReceiverRegistered){
				unregisterReceiver(receiver);
				isBroadcastReceiverRegistered=false;
			}*/
			mSipService.unRegister();
			ControllerService instance = ControllerService.getInstance();
			if(instance!=null){
				instance.sendGoodBye();
				instance.shutdown();
				Log.i(TAG, "controller service is shut down!");
			}
			NgnEngine.getInstance().stop();//added by wangy 20140711
			System.exit(0);		
			
			return true;
		}
	}

	// 版本检查函数 added 2014.5.14
	private boolean isNetworkAvailable(Context context) {
		// TODO Auto-generated method stub

		try {

			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netWorkInfo = cm.getActiveNetworkInfo();
			return (netWorkInfo != null && netWorkInfo.isAvailable());

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// check new version and update
	private void checkToUpdate() throws NameNotFoundException {
		System.out.println("执行到检查了。。。");
		// TODO Auto-generated method stub
		if (getServerVersion()) {
			int currentCode = CurrentVersion.getVerCode(this);

			if (newVerCode > currentCode) {// Current Version is old
				Message msg1 = new Message();
				msg1.what = 11;
				handler.sendMessage(msg1);
			}
		}
	}

	// show Update Dialog
	private void showUpdateDialog() throws Exception {
		System.out.println("执行到提示更新了。。。");
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();

		sb.append("发现新版本：");
		sb.append(newVerName);
		sb.append("\n");
		AlertDialog alertDialog = CustomDialog.getUpdateDialog(ScreenMain.this,
				R.drawable.icon, null, sb.toString(), "立即更新",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// AudioPlayer.getInstance().stop();//added by yanglei
						showProgressBar();
					}
				}, "暂不更新", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		if (!isFinishing() && alertDialog.isShowing() == false) {
			System.out.println("alertDialog=" + alertDialog);
			alertDialog.show();
			GetUpdateInfo.setVerStringJSON("");// 使得再退出程序之前，不能再次弹出对话框
		}

	}

	protected void showProgressBar() {
		System.out.println("执行到显示下载了。。。");
		// TODO Auto-generated method stub
		pBar = new ProgressDialog(ScreenMain.this);
		pBar.setTitle("下载中");
		pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pBar.show();
		Message msg = new Message();
		msg.what = 12;// 开始下载
		handler.sendMessage(msg);

	}

	// Get ServerVersion from GetUpdateInfo.getUpdateVerJSON
	private boolean getServerVersion() {
		// TODO Auto-generated method stub

		try {
			String newVerJSON = GetUpdateInfo.getUpdataVerJSON();
			JSONArray jsonArray = new JSONArray(newVerJSON);
			if (jsonArray.length() > 0) {
				JSONObject obj = jsonArray.getJSONObject(0);
				try {
					newVerCode = Integer.parseInt(obj.getString("verCode"));
					newVerName = obj.getString("verName");
					System.out.println("服务器版本号：" + newVerCode);
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
					newVerCode = -1;
					newVerName = "";
					return false;
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() != null ? e.getMessage() : "");
			return false;
		}
		return true;
	}

	protected void downAppFile(final String url) {
		System.out.println("执行到下载文件了。。。");
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			fileLength = (int) entity.getContentLength();
			pBar.setMax(fileLength);
			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			if (is == null) {
				throw new RuntimeException("isStream is null");
			}
			File file = new File(Environment.getExternalStorageDirectory(),
					GetUpdateInfo.appName);
			fileOutputStream = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int ch = -1;
			downedFileLength = 0;
			do {
				if (this.isFinishing()) {
					pBar.cancel();
					return;
				}
				ch = is.read(buf);
				if (ch <= 0)
					break;
				fileOutputStream.write(buf, 0, ch);
				downedFileLength = downedFileLength + ch;
				System.out.println("downedFileLength=" + downedFileLength);
				pBar.setProgress(downedFileLength);
			} while (true);
			is.close();
			fileOutputStream.close();
			haveDownLoad();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// cancel progressBar and start new App
	protected void haveDownLoad() {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = 2;
		handler.sendMessage(msg);
	}

	protected void installNewApk() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), GetUpdateInfo.appName)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	// 版本检查函数end.
	// 滑动效果 added 2014.5.14
	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			System.out.println("************");
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Log.i("test", "right");
					if (i == maxTabIndex) {
						i = 0;
					} else {
						i++;
					}
					tabs.setCurrentTab(i);
					overridePendingTransition(R.anim.lefttoright,
							R.anim.righttoleft);
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Log.i("test", "left");
					if (i == 0) {
						i = maxTabIndex;
					} else {
						i--;
					}
					tabs.setCurrentTab(i);
					overridePendingTransition(R.anim.lefttoright,
							R.anim.righttoleft);

				}
			} catch (Exception e) {
			}
			return false;
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			event.setAction(MotionEvent.ACTION_CANCEL);
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
