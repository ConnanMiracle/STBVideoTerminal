package org.imshello.droid.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


public class Scanner extends Thread {
	private final String SCAN_REQUEST_MSG="##CTL_SCAN_DEVICE_REQUEST_MSG##";
	private final String SCAN_REPLY_MSG="##CTL_SCAN_DEVICE_REPLY_MSG_#";
	private final String SCAN_REPLY_ACK="##CTL_SCAN_DEVICE_REPLY_ACK##";
	private final String TAG=Scanner.class.getCanonicalName();
	private static final int MAX_DATA_PACKET_LENGTH = 40;
	private boolean isListening;
	private DatagramSocket udpSendSocket;
	private DatagramSocket udpRecvSocket;
	private byte[] sendBuffer = new byte[MAX_DATA_PACKET_LENGTH];
	private byte[] recvBuffer = new byte[MAX_DATA_PACKET_LENGTH];
	private int localPort_send;
	private int localPort_recv;
	private int remotePort;
	private String ID,status;
	private Handler mHandler;
	
	
	public Scanner(int localPort_send, int localPort_recv, int remotePort,
			Handler mHandler,String ID,String status) {
		super();
		this.localPort_send = localPort_send;
		this.localPort_recv = localPort_recv;
		this.remotePort = remotePort;
		this.mHandler = mHandler;
		this.ID=ID;
		this.status=status;
	}

	@Override
	public void run() {
		DatagramPacket reply = new DatagramPacket(sendBuffer, MAX_DATA_PACKET_LENGTH);
		DatagramPacket request = new DatagramPacket(recvBuffer, MAX_DATA_PACKET_LENGTH);
		try {
			udpSendSocket = new DatagramSocket(localPort_send);
			udpRecvSocket = new DatagramSocket(localPort_recv);
			
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			Log.e(TAG, e.toString());
		}
		if(udpRecvSocket==null||udpSendSocket==null){
			Log.e(TAG, "error creating udp socket...check your port setting...");
			return;
		}
		Bundle extras=null;
		while (isListening) {
			//request
			try {				
				
				//try to get a request until the timeout expires
				//udpRecvSocket.setSoTimeout(3000);
				udpRecvSocket.receive(request);
				String content=new String(request.getData());
				Log.i(TAG,"scanner receive: "+content);
				if(content.contains(SCAN_REQUEST_MSG)){
					Log.i(TAG, "detect request received...");
					//reply
					String sendData=SCAN_REPLY_MSG+ID+"*"+status+"##";
					byte[] data = sendData.getBytes();
					reply.setData(data);
					reply.setLength(data.length);
					reply.setPort(remotePort);
					reply.setAddress(request.getAddress());
					try {
						Log.i(TAG, "sending reply of detect message...");
						udpSendSocket.send(reply);
					} catch (Exception e) {
						Log.e(TAG, e.toString());
					}

					extras=new Bundle();
					extras.putString(ControllerService.KEY_REMOTE_IP,request.getAddress().toString().substring(1) );//字符串化后的InetAddress: "/192.168.139.178"
					extras.putInt(ControllerService.KEY_REMOTE_PORT, request.getPort());
					//收到确认后再向controllerService主线程发送数据
					//mHandler.obtainMessage(ControllerService.SCAN_DEVICE_FOUND,extras).sendToTarget();
					//break;
				}else if(content.contains(SCAN_REPLY_ACK)){
					Log.i(TAG, "incoming ack!");
					if(extras!=null){
						//当前ACK的发送者和请求的发送者一致（保证数据不会对应错）
						if(request.getAddress().toString().substring(1).equals(extras.getString(ControllerService.KEY_REMOTE_IP))){
							mHandler.obtainMessage(ControllerService.SCAN_DEVICE_FOUND,extras).sendToTarget();
							Log.i(TAG, "send message to controller service!");
						}
					}
				}
			}
			catch (IOException e) {

				e.printStackTrace();
			}
		}
		udpSendSocket.close();
		udpRecvSocket.close();
		
	}
	
	public void startListen() {		
		isListening=true;
		super.start();
		Log.i(TAG, "Scanner is started!");
	}
	
	public void stopListen() {
		isListening=false;
	}
	
	@Override
	public synchronized void start() {
		startListen();
	}
	
}
