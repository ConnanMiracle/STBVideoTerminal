package org.imshello.droid.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class Messenger {

	protected static final String KEY_CALL_NUMBER = "call_number";
	private String remoteIp;
	private int remoteRecvPort;
	private int localRecvPort;
	private Socket mClientSocket;
	private OutputStream mOutputStream;

	private ServerSocket mServerSocket;
	private ServerSocket mServerSocket1;
	

	static Messenger instance;
	private Context ctx;
	private Handler mHandler;
	private boolean shouldRunning;

	// 主动拨号请求
	public static final String DIAL_CALL_REQUEST = "##CTL_DIAL_CALL_REQUEST_#";// ##CTL_DIAL_CALL_REQUEST_#NUM##
	public static final String DIAL_CALL_REPLY_CALLING = "##CTL_DIAL_CALL_REPLY_CALLING##";// ##CTL_DIAL_CALL_REPLY_CALLING##
	public static final String DIAL_CALL_REPLY_CALLING_ACK = "##CTL_DIAL_CALL_REPLY_CALLING_ACK##";// ##CTL_DIAL_CALL_REPLY_CALLING_ACK##
	public static final String DIAL_CALL_REPLY_SUCCESS = "##CTL_DIAL_CALL_REPLY_SUCCESS##";// ##CTL_DIAL_CALL_REPLY_SUCCESS##
	public static final String DIAL_CALL_REPLY_FAILED = "##CTL_DIAL_CALL_REPLY_FAILED##";// ##CTL_DIALCALL_REPLY_FAILED##
	public static final String DIAL_CALL_REQUEST_ACK = "##CTL_DIAL_CALL_REQUEST_ACK##";
	public static final String DIAL_CALL_REQUEST_CANCEL = "##CTL_DIAL_CALL_REQUEST_CANCEL##";
	public static final String DIAL_CALL_REQUEST_CANCEL_ACK = "##CTL_DIAL_CALL_REQUEST_CANCEL_ACK##";

	// 来电
	public static final String DIAL_CALLED_REQUEST = "##CTL_DIAL_CALLED_REQUEST_#";// ##CTL_DAIL_CALLED_REQUEST_#NUM##
	public static final String DIAL_CALLED_REPLY_PENDING = "##CTL_DIAL_CALLED_REPLY_PENDING##";// ##CTL_DAIL_CALLED_REPLY_PENDING##
	public static final String DIAL_CALLED_REPLY_ACCEPT = "##CTL_DIAL_CALLED_REPLY_ACCEPT##";// "##CTL_DAIL_CALLED_REPLY_ACCEPT##"
	public static final String DIAL_CALLED_REPLY_DECLINE = "##CTL_DIAL_CALLED_REPLY_DECLINE##";// ##CTL_DAIL_CALLED_REPLY_DECLINE##
	public static final String DIAL_CALLED_REQUEST_ACK = "##CTL_DIAL_CALLED_REQUEST_ACK##";

	// 挂机
	public static final String DIAL_BYE_REQUEST = "##CTL_DIAL_BYE_REQUEST##";
	public static final String DIAL_BYE_REPLY = "##CTL_DIAL_BYE_REPLY##";
	public static final String DIAL_BYE_ACK = "##CTL_DIAL_BYE_ACK##";
 
	//超时
	public static final String DIAL_CALLED_REQUEST_TIMEOUT = "##CTL_DIAL_CALLED_REQUEST_TIMEOUT##";
	public static final String DIAL_CALLED_REQUEST_TIMEOUT_ACK = "##CTL_DIAL_CALLED_REQUEST_TIMEOUT_ACK##";
   
	//机顶盒下线
	public static final String SHUTDOWN_MSG = "##CTL_SHUTDOWN_MSG##";
	
	//帧速率设置
	public static final String FRAME_RATE_MSG="##FRAME_RATE_#";
	public static final String  FRAME_RATE_MSG_ACK="##FRAME_RATE_ACK##";
	
	//分辨率设置
		public static final String RESOLUTION_MSG="##RESOLUTION_#";
		public static final String RESOLUTION_MSG_ACK="##RESOLUTION_ACK##";

	protected static final String TAG = Messenger.class.getCanonicalName();
	private String result = "";

	private List<Socket> socketQueue = new ArrayList<Socket>();
	
	Thread serverThread0;
	//Thread serverThread1;
	Thread listenThread;
	void initServerSocket() {
		Log.e(TAG,"COME INTO initServerSocket！"+localRecvPort);
		serverThread0=new Thread() {

			public void run() {
				try {
					// mServerSocket = new ServerSocket(10002);
					mServerSocket = new ServerSocket();
					mServerSocket.setReuseAddress(true);
					InetSocketAddress address = new InetSocketAddress(
							localRecvPort);
					mServerSocket.bind(address);
					Log.e(TAG,Boolean.toString(shouldRunning));
					// mServerSocket.setSoTimeout(10000);//设置accept()方法的阻塞时间，否则一直阻塞线程不会停掉
					// Socket acceptedSocket = null;
					if (mServerSocket == null) {
						Log.e(TAG, "can not create server socket!!!!");
						return;
						
					}
					while (shouldRunning) {

						final Socket acceptedSocket = mServerSocket.accept();
						Log.i(TAG, "incoming socket connect..."
								+ acceptedSocket.toString());
						// InputStream inputStream =
						// acceptedSocket.getInputStream();
						if (acceptedSocket != null) {
							if (!socketQueue.contains(acceptedSocket)) {
								socketQueue.add(acceptedSocket);
								listen(acceptedSocket);
								Log.e(TAG,
										"start new thread to listen on socket: "
												+ acceptedSocket.toString());
							}
						}

						/*
						 * // byte[] buffer=new byte[1024]; // StringBuilder
						 * builder =new StringBuilder(); BufferedReader in;
						 * 
						 * //while(!acceptedSocket.isClosed()){
						 * 
						 * int len=inputStream.read(buffer); if(len==-1) break;
						 * builder.append(new String(buffer,0,len));
						 * 
						 * // }
						 * 
						 * if(acceptedSocket.isConnected()) break;
						 * 
						 * in = new BufferedReader(new InputStreamReader(
						 * inputStream, "UTF-8")); result = in.readLine(); if
						 * (result == null || result.equals("")) {
						 * acceptedSocket.close(); continue; } Log.i(TAG,
						 * "client" + acceptedSocket + "incoming message =" +
						 * result); mHandler.post(new Runnable() {
						 * 
						 * @Override public void run() { // TODO 自动生成的方法存根
						 * Toast.makeText(ctx, "receive: " +result,
						 * Toast.LENGTH_SHORT).show(); } }); // String result =
						 * builder.toString(); if
						 * (result.contains(DAIL_CALL_REQUEST)) {// 手机的主动拨号请求
						 * String callNumber = result.substring(
						 * result.indexOf("_#" + 2), result.indexOf("##"));
						 * Bundle data = new Bundle();
						 * data.putString(KEY_CALL_NUMBER, callNumber);
						 * mHandler.obtainMessage(
						 * ControllerService.RECV_CALL_REQUEST, data)
						 * .sendToTarget(); // 拨打号码并发送CALLING消息 } else if
						 * (result.contains(DAIL_CALL_REQUEST_ACK)) {//
						 * 对于SUCCESS或FAILED消息的确认ACK消息 // 停止结果消息重传（SUCCESS
						 * FAILED） mHandler.obtainMessage(
						 * ControllerService.RECV_CALL_REQUEST_ACK,
						 * null).sendToTarget(); } else if
						 * (result.contains(DAIL_CALLED_REPLY_PENDING)) { //
						 * 提示用户手机端正等待处理 mHandler.obtainMessage(
						 * ControllerService.RECV_CALLED_REPLY_PENDING,
						 * null).sendToTarget(); } else if
						 * (result.contains(DAIL_CALLED_REPLY_ACCEPT)) { //
						 * 提示手机端已经接听，并接听 mHandler.obtainMessage(
						 * ControllerService.RECV_CALLED_REPLY_ACCEPT,
						 * null).sendToTarget(); } else if
						 * (result.contains(DAIL_CALLED_REPLY_DECLINE)) { //
						 * 提示手机端已经拒接，并拒接 mHandler.obtainMessage(
						 * ControllerService.RECV_CALLED_REPLY_DECLINE,
						 * null).sendToTarget(); } else if
						 * (result.contains(DAIL_BYE_REQUEST)) { //
						 * 提示手机端已经挂机，并挂机 mHandler.obtainMessage(
						 * ControllerService.RECV_BYE_REQUEST, null)
						 * .sendToTarget(); } else if
						 * (result.contains(DAIL_BYE_REPLY)) { //
						 * 停止挂机BYE_REQUEST消息重传 mHandler.obtainMessage(
						 * ControllerService.RECV_BYE_REPLY, null)
						 * .sendToTarget(); } else if
						 * (result.contains(DAIL_BYE_ACK)) { //
						 * 停止挂机响应消息重传BYE_REPLY mHandler.obtainMessage(
						 * ControllerService.RECV_BYE_ACK, null)
						 * .sendToTarget(); } acceptedSocket.close(); // }
						 */}
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		};
		serverThread0.start();
	}
	/*void initServerSocket1() {
		serverThread1=new Thread() {

			public void run() {
				try {
					// mServerSocket = new ServerSocket(10002);
					mServerSocket1 = new ServerSocket();
					mServerSocket1.setReuseAddress(true);
					InetSocketAddress address1 = new InetSocketAddress(
							8000);
					mServerSocket1.bind(address1);
					// mServerSocket.setSoTimeout(10000);//设置accept()方法的阻塞时间，否则一直阻塞线程不会停掉
					// Socket acceptedSocket = null;
					if (mServerSocket1 == null) {
						Log.e(TAG, "can not create server socket!!!!");
						return;
						
					}
					while (shouldRunning) {

						final Socket acceptedSocket = mServerSocket1.accept();
						Log.i(TAG, "incoming socket connect..."
								+ acceptedSocket.toString());
						// InputStream inputStream =
						// acceptedSocket.getInputStream();
						if (acceptedSocket != null) {
							if (!socketQueue.contains(acceptedSocket)) {
								socketQueue.add(acceptedSocket);
								listen(acceptedSocket);
								Log.e(TAG,
										"start new thread to listen on socket: "
												+ acceptedSocket.toString());
							}
						}
				}
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		};
		serverThread1.start();
	}*/

	protected void listen(final Socket socket) {
		Log.e(TAG,"COME INTO listen");
	     listenThread=	new Thread() {
			public void run() {
				BufferedReader in;
				try {
					while (!socket.isClosed()) {
						in = new BufferedReader(new InputStreamReader(
								socket.getInputStream(), "UTF-8"));
						String str = in.readLine();
						if (str == null || str.equals("")) {
							break;
						}
						if (str.contains(DIAL_CALL_REQUEST)) {// 手机的主动拨号请求
							int numStart = str.indexOf("_#") + 2;
							int numEnd = str.indexOf("##", numStart);
							Log.i(TAG, str + " " + numStart + " " + numEnd);
							String callNumber = str.substring(numStart, numEnd);
							Bundle data = new Bundle();
							data.putString(KEY_CALL_NUMBER, callNumber);
							mHandler.obtainMessage(
									ControllerService.RECV_CALL_REQUEST, data)
									.sendToTarget();
							// 拨打号码并发送CALLING消息
						} else if (str.contains(DIAL_CALL_REPLY_CALLING_ACK)) {
							mHandler.obtainMessage(
									ControllerService.RECV_CALL_REPLY_CALLING_ACK,
									null).sendToTarget();
						} else if (str.contains(DIAL_CALL_REQUEST_CANCEL)) {
							// 取消当前拨打的电话
							mHandler.obtainMessage(
									ControllerService.RECV_CALL_REQUEST_CANCEL,
									null).sendToTarget();
						} else if (str.contains(DIAL_CALL_REQUEST_ACK)) {// 对于SUCCESS或FAILED消息的确认ACK消息
							// 停止结果消息重传（SUCCESS FAILED）
							mHandler.obtainMessage(
									ControllerService.RECV_CALL_REQUEST_ACK,
									null).sendToTarget();
						} else if (str.contains(DIAL_CALLED_REPLY_PENDING)) {
							// 提示用户手机端正等待处理
							mHandler.obtainMessage(
									ControllerService.RECV_CALLED_REPLY_PENDING,
									null).sendToTarget();
						} else if (str.contains(DIAL_CALLED_REPLY_ACCEPT)) {
							// 提示手机端已经接听，并接听
							mHandler.obtainMessage(
									ControllerService.RECV_CALLED_REPLY_ACCEPT,
									null).sendToTarget();
						} else if (str.contains(DIAL_CALLED_REPLY_DECLINE)) {
							// 提示手机端已经拒接，并拒接
							mHandler.obtainMessage(
									ControllerService.RECV_CALLED_REPLY_DECLINE,
									null).sendToTarget();
						} else if (str.contains(DIAL_BYE_REQUEST)) {
							// 提示手机端已经挂机，并挂机
							mHandler.obtainMessage(
									ControllerService.RECV_BYE_REQUEST, null)
									.sendToTarget();
						} else if (str.contains(DIAL_BYE_REPLY)) {
							// 停止挂机BYE_REQUEST消息重传
							mHandler.obtainMessage(
									ControllerService.RECV_BYE_REPLY, null)
									.sendToTarget();
						} else if (str.contains(DIAL_BYE_ACK)) {
							// 停止挂机响应消息重传BYE_REPLY
							mHandler.obtainMessage(
									ControllerService.RECV_BYE_ACK, null)
									.sendToTarget();
						} else if (str
								.contains(DIAL_CALLED_REQUEST_TIMEOUT_ACK)) {
							mHandler.obtainMessage(
									ControllerService.RECV_CALLED_REQUEST_TIMEOUT_ACK,
									null).sendToTarget();
						}else if (str
								.contains(FRAME_RATE_MSG)) {
							mHandler.obtainMessage(
									ControllerService.FRAME_RATE_MSG,
									str).sendToTarget();
						}else if (str.contains(RESOLUTION_MSG)) {
							mHandler.obtainMessage(
									ControllerService.RESOLUTION_MSG,str).sendToTarget();
						}
						Log.i(TAG, "client" + socket + "str =" + str);
						final String str2 = str;
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(ctx, "receive: " + str2,
										Toast.LENGTH_SHORT).show();
							}
						});
					}
					socketQueue.remove(socket);
					Log.e(TAG,
							"client socket closed, remove from socketQueue: "
									+ socket.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		};
		listenThread.start();
	}

	private void initClientSocket() {
		Log.e(TAG,"COME INTO initClientSocket！");
		new Thread() {
			public void run() {
				try {
					mClientSocket = new Socket(remoteIp, remoteRecvPort);
					mOutputStream = mClientSocket.getOutputStream();
					PrintWriter out = new PrintWriter(mOutputStream);
					out.println("hello");
					out.flush();
					Log.i(TAG,"send hello");
				} catch (UnknownHostException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static Messenger getInstance(String remoteIp, int remoteRecvPort, int localRecvPort,
			Context context, Handler handler) {
		if (instance == null){
			instance = new Messenger(remoteIp, remoteRecvPort,localRecvPort, context, handler);
			Log.i(TAG, "new a Messenger instance!");
		/*
		 * else{
		 * if((!remoteIp.equals(instance.remoteIp))||(remotePort!=instance.
		 * remotePort)){//收到Scanner的消息后，应判断是否是新的设备接入，如果是，则重新初始化Messenger
		 * Log.i(TAG,
		 * "remote control device changed, reinit messenger..."+remoteIp
		 * +": "+remotePort); instance.closeConnection(); instance=null;
		 * instance=new Messenger(remoteIp, remotePort, context, handler); } }
		 */
		    return instance;
		  }
		  else{
			Log.i(TAG, "a Messenger instance already exists, re-new it...");
			instance.closeConnection();
			instance = null;
			instance = new Messenger(remoteIp, remoteRecvPort,localRecvPort, context, handler);
		}

		return instance;
	}

	public void sendMessage(final String message) {
		Log.e(TAG,"COME INTO sendMessenger！"+remoteRecvPort);
		new Thread(new Runnable() {
			@Override
			public void run() {

				/*if (mClientSocket != null && mClientSocket.isConnected()) {
					synchronized (mClientSocket) {
						if (!mClientSocket.isOutputShutdown()) {
							PrintWriter out = new PrintWriter(mOutputStream);
							out.println(message);
							// out.println(JsonUtil.obj2Str(msg));
							out.flush();
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(ctx, "send: " + message,
											Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				} else {*/
					try {
						mClientSocket = new Socket(remoteIp, remoteRecvPort);
						//synchronized (mClientSocket) {
							mOutputStream = mClientSocket.getOutputStream();
							//sendMessage(message);
							PrintWriter out = new PrintWriter(mOutputStream);
							out.println(message);
							// out.println(JsonUtil.obj2Str(msg));
							out.flush();
						//}
						Log.i(TAG, "conncetion is down, retry send message: "
								+ message);
						mClientSocket.close();
						mClientSocket=null;
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				//}
				/*
				 * //int count = 10; //while (true) { try { //mClientSocket =
				 * new Socket(remoteIp, 8000); mOutputStream =
				 * mClientSocket.getOutputStream(); //if
				 * (!mClientSocket.isOutputShutdown()) {
				 * 
				 * PrintWriter out = new PrintWriter(mOutputStream);
				 * out.println(message); out.flush(); out.close();
				 * 
				 * 
				 * PrintWriter out = new PrintWriter(mOutputStream,true); //
				 * while(count>0){ // mOutputStream.write(message.getBytes());
				 * // mOutputStream.flush();
				 * 
				 * out.println(message); out.flush(); //out.close(); Log.i(TAG,
				 * "message has been sent: " + message); Log.i(TAG,
				 * "message has been sent to" + mClientSocket.toString());
				 * mHandler.post(new Runnable() {
				 * 
				 * @Override public void run() { // TODO 自动生成的方法存根
				 * Toast.makeText(ctx, "send: " +message,
				 * Toast.LENGTH_SHORT).show(); } }); //}
				 * //mClientSocket.close(); } catch (UnknownHostException e) {
				 * // TODO 自动生成的 catch 块 e.printStackTrace(); } catch
				 * (IOException e) { // TODO 自动生成的 catch 块 e.printStackTrace();
				 * }
				 * 
				 * if (mClientSocket != null && mClientSocket.isConnected()) {
				 * if (!mClientSocket.isOutputShutdown()) { PrintWriter out =
				 * new PrintWriter(mOutputStream); out.println(message);
				 * out.flush(); Log.i(TAG, "message has been sent: "+message);
				 * Log.i(TAG,
				 * "message has been sent to"+mClientSocket.toString()); } }
				 * 
				 * //count--; //if(count==0) // break; //}
				 */
			}

		}).start();
	}

	private Messenger(String remoteIp, int remoteRecvPort, int localRecvPort, Context ctx,
			Handler handler) {
		super();
		Log.e("TAG","make Messenger!");
		this.remoteIp = remoteIp;
		this.remoteRecvPort = remoteRecvPort;
		this.localRecvPort=localRecvPort;
		this.ctx = ctx;
		mHandler = handler;
		shouldRunning = true;
		//this.initClientSocket();
		this.initServerSocket();
		//this.initServerSocket1();
		Log.i(TAG, "Messenger has been initialized!");
	}

	public void closeConnection() {
		Log.e(TAG,"closeConnection");
		shouldRunning = false;
		try {
			if (mClientSocket != null) {
				//synchronized (mClientSocket) {
					mClientSocket.close();
				//}
				mClientSocket = null;
			}
			serverThread0.interrupt();
			if (mServerSocket != null) {
				mServerSocket.close();
				mServerSocket = null;
			}
			//serverThread1.interrupt();
			if (mServerSocket1 != null) {
				mServerSocket1.close();
				mServerSocket1 = null;
			}
			//listenThread.interrupt();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		instance = null;
		Log.i(TAG, "messenger has been closed!");
	}
	
	public static void invalidateMessenger(){
		if(instance!=null){
			instance.closeConnection();
			instance=null;
		}
	}

}
