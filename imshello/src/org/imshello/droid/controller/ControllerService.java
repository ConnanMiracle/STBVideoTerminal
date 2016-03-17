package org.imshello.droid.controller;

import java.util.Map;

import org.imshello.baseWRAP.tmedia_pref_video_size_t;
import org.imshello.droid.Engine;
import org.imshello.droid.Main;
import org.imshello.droid.R;
import org.imshello.droid.Screens.ScreenAV;
import org.imshello.droid.Screens.ScreenMain;
import org.imshello.ngn.NgnEngine;
import org.imshello.ngn.media.NgnMediaType;
import org.imshello.ngn.services.INgnConfigurationService;
import org.imshello.ngn.services.INgnSipService;
import org.imshello.ngn.utils.NgnConfigurationEntry;
import org.imshello.ngn.utils.NgnStringUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class ControllerService extends Thread {
	public static final int SCANN_PORT_LOCAL=8888;
	public static final int SCANN_PORT_REMOTE=8888;
	
	public static final int SCAN_DEVICE_FOUND=0;
	
	public static final int RECV_CALL_REQUEST=1;
	public static final int RECV_CALL_REQUEST_ACK=2;
	public static final int RECV_CALL_REQUEST_CANCEL=3;
	public static final int RECV_CALL_REPLY_CALLING_ACK=4;
	
	public static final int RECV_CALLED_REPLY_PENDING=5;
	public static final int RECV_CALLED_REPLY_ACCEPT = 6;
	public static final int RECV_CALLED_REPLY_DECLINE = 7;
	
	public static final int RECV_BYE_REQUEST = 8;
	public static final int RECV_BYE_REPLY=9;
	public static final int RECV_BYE_ACK = 10;
	
	public static final int RECV_CALLED_REQUEST_TIMEOUT_ACK=11;
	
	public static final int FRAME_RATE_MSG=12;
	
	public static final int RESOLUTION_MSG=13;
	
	protected static final String KEY_REMOTE_IP = "remote_ip";
	protected static final String KEY_REMOTE_PORT = "remote_port";
	
	public static final String KEY_REMOTE_NAME = "remote_name";
	private Handler mHandler;
	protected String remoteIP;
	protected int remoteMessengerRecvPort;
	protected int localMessengerRecvPort;
	private String ID;
	private static String TAG=ControllerService.class.getCanonicalName();
	private Messenger mMessenger;
	private BroadcastReceiver mReceiver;
	private Scanner mScanner;
	private final INgnConfigurationService mConfigurationService;
	private final INgnSipService mSipService;
	
	public static final String ACTION_INCOMING_INVITE="org.imshello.droid.controller.ACTION_INCOMING_INVITE";
	public static final String ACTION_INCOMING_BYE="org.imshello.droid.controller.ACTION_INCOMING_BYE";//not used
	public static final String ACTION_ESTABLISHED="org.imshello.droid.controller.ACTION_ESTABLISHED";
	public static final String ACTION_INCOMING_non_2XX="org.imshello.droid.controller.ACTION_INCOMING_non_2XX";//not used
	public static final String ACTION_INCOMING_TERMINATED="org.imshello.droid.controller.ACTION_INCOMING_TERMINATED";

	public static final String KEY_PHRASE = "result_phrase";
	ControlState mState;
	{mState=ControlState.IDLE;
	Log.e(TAG,"set controller state to IDLE");}
	
	Context ctx;
	ScreenMain mainActivity;

	public enum ControlState{
		IDLE, CONNECTING,CALLING, CANCELED, SUCCESS, FAILED, CONNECTED, TERMINATED, RINGING, ACCEPT, DECLINE, TIMEOUT
	};
	
	String callNumber;
	
	static ControllerService instance;
	/*@Override
	public IBinder onBind(Intent intent) {
		// TODO 自动生成的方法存根
		return null;
	}*/
	
	/*@Override
	public void onCreate() {
		// TODO 启动scan线程，等待连接
		super.onCreate();
		initHandler();
		initReceiver();
		initScanner();
	}*/
	
	public ControlState getControllerState(){
		return mState;
	}

	public static ControllerService getInstance(Context context){
		if(instance==null)
			instance=new ControllerService(context);
		return instance;
		
	}
	
	public static ControllerService getInstance(){
		return instance;
	}
	
	private ControllerService(Context context) {
		super();
		Log.i(TAG,"ctx is null? "+String.valueOf(context==null));
		ctx=context;
		mainActivity=(ScreenMain) context;
		mConfigurationService= NgnEngine.getInstance().getConfigurationService();
		mSipService=NgnEngine.getInstance().getSipService();
		initHandler();
		initScanner();
		initReceiver();
		Messenger.invalidateMessenger();
		this.start();
	}
	
	@Override
	public void run() {
		Looper.prepare();
		initHandler();
		Looper.loop();
	}
	
	
	private void initScanner() {
		if(mScanner==null){
			String strPortSend,strPortRecv,status;
			
			 
			 strPortSend = mConfigurationService.getString(NgnConfigurationEntry.CONTROLLER_SCAN_PORT_SEND, NgnConfigurationEntry.DEFAULT_CONTROLLER_SCAN_PORT_SEND);
			 strPortRecv = mConfigurationService.getString(NgnConfigurationEntry.CONTROLLER_SCAN_PORT_RECV, NgnConfigurationEntry.DEFAULT_CONTROLLER_SCAN_PORT_RECV);
			 if(mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPU, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPU)!=null)
				{
				   String s = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPU, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPU);
				   String a1[] =s.split(":");
				   String a2[] = a1[1].split("@");
				   ID=new String(a2[0]);
					Log.i(TAG,ID);
				}
			  
			 if(mSipService.isRegistered()){
				 status="log";
			 }else{
				 status="unlog";
			 }
			try{
				int portSend= Integer.parseInt(strPortSend);	
				int portRecv=Integer.parseInt(strPortRecv);	
				Log.i(TAG,ID+status);
				mScanner=new Scanner(portSend, portRecv, portRecv, mHandler,ID,status);
				mScanner.startListen();
				Log.i(TAG, "scanner is started...send port: "+portSend+" recv port: "+portRecv);
			}catch(Exception e){
				Log.e(TAG, "wrong number input");
			
			}
		}
	}

	private void initReceiver() {
		if(mReceiver!=null)
			return;
		mReceiver=new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				if(mMessenger==null)
					return;
				Log.i(TAG, "incoming sip event broadcast: "+intent.toString());
				if(intent.getAction().equals(ACTION_INCOMING_INVITE)){//被叫
					if (mState==ControlState.IDLE) {
						String remoteNumber = intent.getExtras().getString(
								KEY_REMOTE_NAME);
						mMessenger.sendMessage(Messenger.DIAL_CALLED_REQUEST
								+ remoteNumber + "##");
						//mState = ControlState.RINGING;//收到PENDING消息时再迁移
						Log.i(TAG, "remote incoming invite...current state: "+mState);
					}
				}else if(intent.getAction().equals(ACTION_ESTABLISHED)){
					
					if (mState==ControlState.CALLING) {
						mMessenger
								.sendMessage(Messenger.DIAL_CALL_REPLY_SUCCESS);
						Log.i(TAG, "remote incoming 200ok...current State: "+mState);
						//mState = ControlState.SUCCESS;
						setState(ControlState.SUCCESS);
						
					}else if(mState==ControlState.ACCEPT){
						
						Log.i(TAG, "phone pick up...current State: "+mState);
						//mState = ControlState.CONNECTED;
						setState(ControlState.CONNECTED);
					}
				}else if(intent.getAction().equals(ACTION_INCOMING_TERMINATED)){
					
					if(mState==ControlState.CALLING){
						mMessenger.sendMessage(Messenger.DIAL_CALL_REPLY_FAILED);
						//mState= ControlState.FAILED;
						setState(ControlState.FAILED);
						Log.i(TAG,"remote incoming non 2XX...current State: "+mState);
					}else if(mState==ControlState.CANCELED){
						//手机取消拨打
						//mState=ControlState.IDLE;
						setState(ControlState.IDLE);//这里可能有问题。。。因为不一定通了啊
						Log.i(TAG, "remote cancelling confirmed...current State: "+mState);	
					}else if(mState==ControlState.CONNECTED){
						mMessenger.sendMessage(Messenger.DIAL_BYE_REQUEST);
						//mState= ControlState.TERMINATED;
						setState(ControlState.TERMINATED);
						Log.i(TAG,"remote incoming bye...current State: "+mState);
						
					}else if(mState==ControlState.DECLINE){
						//手机拒接
						//mState=ControlState.IDLE;
						setState(ControlState.IDLE);
						Log.i(TAG,"incoming decline...current State: "+mState);
					}
					
					else if(mState==ControlState.RINGING){
						//手机无响应，远端超时，或者远端取消
						//mState=ControlState.TIMEOUT;
						setState(ControlState.TIMEOUT);
						mMessenger.sendMessage(Messenger.DIAL_CALLED_REQUEST_TIMEOUT);
						//等待手机的回应，手机可能已经掉线（不会发送ACK），如果是这样，则自动回到正常状态
						new Thread(){
							public void run() {
								SystemClock.sleep(5000);
								if(mState==ControlState.TIMEOUT)
									setState(ControlState.IDLE);
							};
						}.start();
						Log.i(TAG, "remote timeout...current State: "+mState);
					}
					
				}
			}
		};
		IntentFilter filter =new IntentFilter();
		filter.addAction(ACTION_INCOMING_INVITE);
		//filter.addAction(ACTION_INCOMING_BYE);
		filter.addAction(ACTION_ESTABLISHED);
		//filter.addAction(ACTION_INCOMING_non_2XX);
		filter.addAction(ACTION_INCOMING_TERMINATED);
		Log.i(TAG,"ctx is null? "+String.valueOf(ctx==null));
		ctx.registerReceiver(mReceiver, filter);
		Log.i(TAG, "sip event broadcast receiver has been registered!");
	}

	private void initHandler() {
		mHandler = new Handler(){


			@Override
			public void handleMessage(Message msg) {
				Bundle data;
				switch (msg.what) {
				case SCAN_DEVICE_FOUND:
					Log.i(TAG, msg.obj.toString());
					showNotification("设备发现", "安卓的遥控设备已连接");
					data = (Bundle) msg.obj;
					remoteIP = data.getString(KEY_REMOTE_IP);
					//remotePort = data.getInt(KEY_REMOTE_PORT);//该端口是手机的发送端口，不应使用作为Messenger的初始化端口
					try {
						remoteMessengerRecvPort = Integer.parseInt(mConfigurationService.
								getString(NgnConfigurationEntry.CONTROLLER_MESSENGER_PORT_REMOTE_RECV,
										NgnConfigurationEntry.DEFAULT_CONTROLLER_MESSENGER_PORT_REMOTE_RECV));
						localMessengerRecvPort = Integer.parseInt(mConfigurationService.
								getString(NgnConfigurationEntry.CONTROLLER_MESSENGER_PORT_LOCAL_RECV,
										NgnConfigurationEntry.DEFAULT_CONTROLLER_MESSENGER_PORT_LOCAL_RECV));
					} catch (NumberFormatException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}					
					initMessenger(remoteIP,remoteMessengerRecvPort,localMessengerRecvPort);
					setState(ControlState.IDLE);
					break;
				case RECV_CALL_REQUEST:
					if (mState==ControlState.IDLE) {
						data = (Bundle) msg.obj;
						callNumber = data
								.getString(Messenger.KEY_CALL_NUMBER);
						/*mainActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								ScreenAV.makeCall(callNumber, NgnMediaType.AudioVideo);
								Log.i(TAG, "making call to: "+callNumber);
							}
						});*/
						
						mMessenger.sendMessage(Messenger.DIAL_CALL_REPLY_CALLING);
						//mState = ControlState.CALLING;
						//setState(ControlState.CALLING);
						setState(ControlState.CONNECTING);
						Log.i(TAG, "incoming call request...current state: "+mState);
					}
					break;
					
				case RECV_CALL_REPLY_CALLING_ACK:
					if(mState==ControlState.CONNECTING){
						setState(ControlState.CALLING);
						mainActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								ScreenAV.makeCall(callNumber, NgnMediaType.AudioVideo);
								Log.i(TAG, "making call to: "+callNumber);
							}
						});						
					}
					break;
				
				case RECV_CALL_REQUEST_CANCEL:
					if(mState==ControlState.CONNECTING||mState==ControlState.CALLING||mState==ControlState.SUCCESS){//考虑一下对方已经接了，也就是机顶盒已经到Connected，但是手机还没收到Connected，而发送的cancel这个时候来了（应该把它当做一个挂断BYE的处理）
						//mState=ControlState.CANCELED;
						setState(ControlState.CANCELED);
						mMessenger.sendMessage(Messenger.DIAL_CALL_REQUEST_CANCEL_ACK);
						if(ScreenAV.hangUpCurrentCall()){
							Log.i(TAG, "cancel call...current state: "+mState);
						}else{
							//mState=ControlState.IDLE;
							setState(ControlState.IDLE);
							Log.i(TAG, "error cancelling call...current state: "+mState);
						}
					}else if(mState==ControlState.FAILED){//FAILED是从CALLING状态由广播接收器迁移的，所以此时，电话已经被挂断了
						mMessenger.sendMessage(Messenger.DIAL_CALL_REQUEST_CANCEL_ACK);
						setState(ControlState.IDLE);
						
					}/*else if(mState==ControlState.CONNECTED){//再说，这个需要协商
						//mState=ControlState.CANCELED;
						setState(ControlState.CANCELED);
						mMessenger.sendMessage(Messenger.DIAL_CALL_REQUEST_CANCEL_ACK);
						if(ScreenAV.hangUpCurrentCall()){
							
						}else{
							//mState=ControlState.IDLE;
							setState(ControlState.IDLE);
							Log.i(TAG, "error cancelling call...current state: "+mState);
						}
					}*/
					mainActivity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							 ((Engine)Engine.getInstance()).getScreenService().show(ScreenMain.class);
						}
					});
					break;
				case RECV_CALL_REQUEST_ACK://ack to success or failed
					if(mState==ControlState.SUCCESS){
						//mState=ControlState.CONNECTED;
						setState(ControlState.CONNECTED);
						Log.i(TAG, "incoming ack to SUCCESS_REPLY message...current state: "+mState);
					}else if(mState==ControlState.FAILED){
						//mState=ControlState.IDLE;
						setState(ControlState.IDLE);
						Log.i(TAG, "incoming ack to FAILED_REPLY message ...current state: "+mState);
					}
					break;
				case RECV_CALLED_REPLY_PENDING:
					if (mState==ControlState.IDLE) {
						//mState = ControlState.RINGING;
						setState(ControlState.RINGING);
						Log.i(TAG, "phone is ringing...current state: "+mState);
					}
					break;
				case RECV_CALLED_REPLY_ACCEPT:			
					if (mState==ControlState.RINGING) {
						//mState = ControlState.ACCEPT;
						setState(ControlState.ACCEPT);
						mMessenger.sendMessage(Messenger.DIAL_CALLED_REQUEST_ACK);
						if (ScreenAV.accpetCurrentCall()){
							//mState = ControlState.CONNECTED;
							
						}
						else {
							//如果没有接听成功，则发送BYE结束当前会话
							//mState=ControlState.TERMINATED;
							setState(ControlState.TERMINATED);
							mMessenger.sendMessage(Messenger.DIAL_BYE_REQUEST);
							Log.e(TAG, "error accpeting call...");
						}
					}
					break;
				case RECV_CALLED_REPLY_DECLINE:
					if (mState==ControlState.RINGING) {
						//mState = ControlState.DECLINE;
						setState(ControlState.DECLINE);
						mMessenger.sendMessage(Messenger.DIAL_CALLED_REQUEST_ACK);
						if (ScreenAV.hangUpCurrentCall()) {
							//该状态会在SIP事件处理时迁移
							//mState=ControlState.IDLE;
						} else {
							//如果没有挂机成功，直接回到IDLE狀態，因為phone收到DECLINE的确认ACK后会认为挂机成功
							mState=ControlState.IDLE;
							//mMessenger.sendMessage(Messenger.DAIL_BYE_REQUEST);
							Log.e(TAG, "error declining call...");
						}
						
					}
					break;
				case RECV_BYE_REQUEST:
					if(mState==ControlState.CONNECTED){
						//mState=ControlState.TERMINATED;
						setState(ControlState.TERMINATED);
						ScreenAV.hangUpCurrentCall();
						mMessenger.sendMessage(Messenger.DIAL_BYE_REPLY);
						Log.i(TAG,"incoming BYE_REQUEST...current state: "+mState);
					}
					break;
				case RECV_BYE_REPLY:					
					if(mState==ControlState.TERMINATED){
						//mState=ControlState.IDLE;
						setState(ControlState.IDLE);
						mMessenger.sendMessage(Messenger.DIAL_BYE_ACK);
						Log.i(TAG,"incoming BYE_ACK...current state: "+mState);
					}
					break;
				case RECV_BYE_ACK:
					if(mState==ControlState.TERMINATED){
						//mState=ControlState.IDLE;
						setState(ControlState.IDLE);
						Log.i(TAG,"incoming BYE_ACK...current state: "+mState);
					}
					break;
				case RECV_CALLED_REQUEST_TIMEOUT_ACK:
					if(mState==ControlState.TIMEOUT){
						//mState=ControlState.IDLE;
						setState(ControlState.IDLE);
						Log.i(TAG, "incoming TIMEOUT_ACK...current state: "+mState);
					}
					break;
				case FRAME_RATE_MSG:
					//从消息中取得帧率
					String result1 = msg.obj.toString();
					Log.e(TAG,result1);
					int start1 = result1.indexOf("_#") + 2;
					int end1 = result1.indexOf("##",start1);
					final String frameRate = result1.substring(
							start1, end1);
					mConfigurationService.putInt(NgnConfigurationEntry.QOS_VIDEO_FPS, Integer.parseInt(frameRate));
					mConfigurationService.commit();
					mMessenger.sendMessage(Messenger.FRAME_RATE_MSG_ACK);
					break;
				case RESOLUTION_MSG:
					//从消息中取得分辨率
					String result2 = msg.obj.toString();
					Log.e(TAG, result2);
					int start2 =  result2.indexOf("_#") + 2;
					int end2 = result2.indexOf("##",start2);
					final String resolution = result2.substring(
							start2, end2);
					if(resolution.equals("SQCIF (128 x 98)"))
						mConfigurationService.putString(NgnConfigurationEntry.QOS_PREF_VIDEO_SIZE,
								(tmedia_pref_video_size_t.tmedia_pref_video_size_sqcif).toString());
					else if(resolution.equals("QCIF (176 x 144)"))
						mConfigurationService.putString(NgnConfigurationEntry.QOS_PREF_VIDEO_SIZE,
								(tmedia_pref_video_size_t.tmedia_pref_video_size_qcif).toString());
					else if(resolution.equals("QVGA (320 x 240)"))
						mConfigurationService.putString(NgnConfigurationEntry.QOS_PREF_VIDEO_SIZE,
								(tmedia_pref_video_size_t.tmedia_pref_video_size_qvga).toString());
					else if(resolution.equals("CIF (352 x 288)"))
						mConfigurationService.putString(NgnConfigurationEntry.QOS_PREF_VIDEO_SIZE,
								(tmedia_pref_video_size_t.tmedia_pref_video_size_cif).toString());
					else if(resolution.equals("HVGA (480 x 320)"))
						mConfigurationService.putString(NgnConfigurationEntry.QOS_PREF_VIDEO_SIZE,
								(tmedia_pref_video_size_t.tmedia_pref_video_size_hvga).toString());
					else if(resolution.equals("VGA (640 x 480)"))
						mConfigurationService.putString(NgnConfigurationEntry.QOS_PREF_VIDEO_SIZE,
								(tmedia_pref_video_size_t.tmedia_pref_video_size_vga).toString());
					else if(resolution.equals("4CIF (704 x 576)"))
						mConfigurationService.putString(NgnConfigurationEntry.QOS_PREF_VIDEO_SIZE,
								(tmedia_pref_video_size_t.tmedia_pref_video_size_4cif).toString());
					else if(resolution.equals("SVGA (800 x 600)"))
						mConfigurationService.putString(NgnConfigurationEntry.QOS_PREF_VIDEO_SIZE,
								(tmedia_pref_video_size_t.tmedia_pref_video_size_svga).toString());
					else if(resolution.equals("720P (1280 x 720)"))
						mConfigurationService.putString(NgnConfigurationEntry.QOS_PREF_VIDEO_SIZE,
								(tmedia_pref_video_size_t.tmedia_pref_video_size_720p).toString());
					else if(resolution.equals("16CIF (1408 x 1152)"))
						mConfigurationService.putString(NgnConfigurationEntry.QOS_PREF_VIDEO_SIZE,
								(tmedia_pref_video_size_t.tmedia_pref_video_size_16cif).toString());
					else if(resolution.equals("1080P (1920 x 1080)"))
						mConfigurationService.putString(NgnConfigurationEntry.QOS_PREF_VIDEO_SIZE,
								(tmedia_pref_video_size_t.tmedia_pref_video_size_1080p).toString());
					mConfigurationService.commit();
					mMessenger.sendMessage(Messenger.RESOLUTION_MSG_ACK);
				default:
					break;
				}
			}
		};
		
	}
	int id=10;
	private void setState(ControlState state){
		mState=state;
		/*mainActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				ScreenMain.mLable.setText(mState.toString());
			}
		});*/
		/*Notification.Builder builder = new Notification.Builder(ctx);
		Notification notification = builder
				.setSmallIcon(R.drawable.icon)
				.setContentTitle("连接状态改变")
				.setContentText(state.toString())
				.setTicker(state.toString())
				//.setVibrate(new long[] { 0, 500, 700, 500, 800 })
				.setWhen(System.currentTimeMillis())
				.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(),R.drawable.icon))
				.setAutoCancel(true)				
				.build();
		//notification.flags=Notification.FLAG_NO_CLEAR;
		NotificationManager notificationManager = (NotificationManager)ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(id++, notification);*/
	}

	protected void initMessenger(String remoteIP, int remoteRecvPort, int localRecvPort) {
		mMessenger = Messenger.getInstance(remoteIP, remoteRecvPort, localRecvPort,ctx, mHandler);
		Log.i(TAG, "Messenger has been initialized...");
	}

	protected void showNotification(String title, String text) {
		/*Notification notfi=new Notification(R.drawable.icon, "通知来了", System.currentTimeMillis());
		NotificationManager mNotificationManager= (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		notfi.setLatestEventInfo(ctx,title, text , null);
		mNotificationManager.notify(0, notfi);*/
		Notification.Builder builder = new Notification.Builder(ctx);
		Notification notification = builder
				.setSmallIcon(R.drawable.icon)
				.setContentTitle("设备连接通知")
				.setContentText("安卓的远程设备已经连接")
				.setTicker("通知")
				.setVibrate(new long[] { 0, 500, 700, 500, 800 })
				.setWhen(System.currentTimeMillis())
				.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(),R.drawable.icon))
				.setAutoCancel(true)				
				.build();
		//notification.flags=Notification.FLAG_NO_CLEAR;
		NotificationManager notificationManager = (NotificationManager)ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, notification);
	}

	
	
	/*@Override
	public void onDestroy() {
		// TODO 停止搜索，清理资源
		super.onDestroy();
		unregisterReceiver(mReceiver);
		mMessenger.destroy();
		mScanner.stopListen();
	}*/
	
	public void shutdown() {
		ctx.unregisterReceiver(mReceiver);
		if(mMessenger!=null){
			mMessenger.closeConnection();
			mMessenger=null;
		}
		if(mScanner!=null){
			mScanner.stopListen();
			mScanner=null;
		}

	}
	
	public void sendGoodBye(){
		if(mMessenger!=null){
			mMessenger.sendMessage(Messenger.SHUTDOWN_MSG);
			Log.i(TAG, "shut down message has been sent!");
		}
			
	}
}
