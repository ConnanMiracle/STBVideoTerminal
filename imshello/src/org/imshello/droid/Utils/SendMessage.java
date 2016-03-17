package org.imshello.droid.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class SendMessage extends Thread{

	//�Ƿ�������־
	private boolean judge= true;
	//����ʱ����
	private int interval = 0;
	public SendMessage(int time, boolean switchs)
	{
		this.judge = switchs;
		this.interval = time;
	}
	
	@Override
	public void run()
	{
		 Timer timer= new Timer() ;
		if(judge)
		{
			TimerTask task = new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					send();
				}
			};
			timer.schedule(task, interval*1000, interval*1000);
		}
    }
	
	//���ݰ��Ĺ��켰����
	public void send() {
		try {
			String des = "114.215.176.27";
			String str = "0000";
			DatagramSocket ds = new DatagramSocket(5060);
			// ����udp���ݱ�
			DatagramPacket dp = new DatagramPacket(str.getBytes(),
					str.getBytes().length,
					InetAddress.getByName(des), 5060);
			// ����udp���ݱ�
			ds.send(dp);

		} catch (SocketException s) {
			// TODO Auto-generated catch block
			s.printStackTrace();
		}catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
}
