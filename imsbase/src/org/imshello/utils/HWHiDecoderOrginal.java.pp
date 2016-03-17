package org.imshello.utils;
/*package org.imshello.utils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import android.os.SystemClock;
import android.util.Log;

public class HWHiDecoder {
		public static final String TAG=HWHiDecoder.class.getCanonicalName();
	//Constants
		public static final int AUDIO_DATA=0;
		public static final int VIDEO_DATA=1;
		
		public static final int IDLE=0;
		public static final int INITED=1;
		public static final int PREPARED=2;
		public static final int STARTED=3;
		public static final int STOPPED=4;

		public static final int SUCCESS=0;
		public static final int FAILED=-1;
		private int status;
		
		//native function declare:
		private native int DeviceInit();
		private native int ParamsInit(int audio_dec_type, int audio_decode_mode, int video_decode_type,int frame_rate, int display_channel,int virtual_screen_width, int virtual_screen_height, int virtual_screen_offset_top, int virtual_screen_offset_left);
		private native int PrepareNative();
		private native int StartNative();
		private native int StopNative();
		private native int DeviceDeInit();
		private native int AudioDataCallback(byte[] data, int size);
		private native int VideoDataCallback(byte[] data, int size);
		
		private List<DataBufferItem> bufferQueue=new ArrayList<DataBufferItem>();
		private ConsumerThread mConsumerThread=new ConsumerThread();
		
		static{
			try {

				System.loadLibrary("player");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e(TAG, e.toString());
				
			}
			
		}
		
		public HWHiDecoder(int audio_dec_type, int audio_decode_mode, int video_decode_type,int frame_rate, int display_channel,int virtual_screen_width, int virtual_screen_height, int virtual_screen_offset_top, int virtual_screen_offset_left){
			this.status=IDLE;
			int ret=this.DeviceInit();
			if(ret==0){
				ret=this.ParamsInit(audio_dec_type, audio_decode_mode, video_decode_type, frame_rate, display_channel, virtual_screen_width, virtual_screen_height, virtual_screen_offset_top, virtual_screen_offset_left);

				if(ret==0)
					this.status=INITED;
			}
		}
		
		public static HWHiDecoder getDefaultDecoder(){
			return new HWHiDecoder(0, 1, 0, 30, 1, 1280, 720, 0, 0);
		}
		
		public int prepare(){
			if(this.status==INITED){
				int ret=this.PrepareNative();
				Log.i(TAG, "prepare return "+ret);
				if(ret==0){
					this.status=PREPARED;
					return SUCCESS;
				}
				else
					return FAILED;
			}
			else
				throw new IllegalStateException();
		}
		
		public int start() throws IllegalStateException{
			if(this.status==PREPARED){
				int ret=this.StartNative();
				Log.i(TAG, "start return "+ret);
				if(ret==0){
					this.status=STARTED;
					mConsumerThread.start();
					Log.i(TAG, "consumer thread is started...");
					return SUCCESS;
				}
				else
					return FAILED;
			}
			else
				throw new IllegalStateException();		
		}
		
		public void stop() throws IllegalStateException{
			if(this.status==STARTED){
				int ret=this.StopNative();
				if(ret==0){
					this.status=STOPPED;
					bufferQueue.clear();
					mConsumerThread=null;
				}
			}
			else
				throw new IllegalStateException();		
		}
		
		public void finish()throws IllegalStateException{
			if(this.status==STOPPED){
				int ret=this.DeviceDeInit();
				if(ret==0){
					this.status=IDLE;
				}
			}
			else
				throw new IllegalStateException();
		}
		
		private int handleData(byte[] data, int size, int flag) throws IllegalStateException,IllegalArgumentException{
			if(this.status!=STARTED)
				throw new IllegalStateException();
			if(flag==AUDIO_DATA)
				return this.AudioDataCallback(data,size);
			else if(flag==VIDEO_DATA)
				return this.VideoDataCallback(data,size);
			else
				throw new IllegalArgumentException();
		}
		
		public int getStatus(){
			return this.status;
		}
		
		public void putBuffer(ByteBuffer buffer,int validSize, int maxSize){
			bufferQueue.add(bufferQueue.size(),new DataBufferItem(buffer, validSize, maxSize));
		}
		
		public void putBuffer(byte[]data, int validSize,int maxSize){
			bufferQueue.add(new DataBufferItem(validSize, maxSize, data));
		}
		
		class DataBufferItem{
			ByteBuffer buffer;
			public DataBufferItem(ByteBuffer buffer, int validSize, int maxSize) {
				super();
				this.buffer = buffer;
				//this.data=buffer.array();
				this.validSize = validSize;
				this.maxSize = maxSize;
			}
			int validSize;
			public DataBufferItem(int validSize, int maxSize, byte[] data) {
				super();
				this.validSize = validSize;
				this.maxSize = maxSize;
				this.data = data;
			}
			int maxSize;
			byte[] data;
			public ByteBuffer getBuffer() {
				return buffer;
			}
			public void setBuffer(ByteBuffer buffer) {
				this.buffer = buffer;
			}
			
			public byte[] getData() {
				return data;
			}
			public int getValidSize() {
				return validSize;
			}
			public void setValidSize(int validSize) {
				this.validSize = validSize;
			}
			public int getMaxSize() {
				return maxSize;
			}
			public void setMaxSize(int maxSize) {
				this.maxSize = maxSize;
			}
		}
		
		class ConsumerThread extends Thread{
			@Override
			public void run() {
				byte[] mdata=null;
				while (status==STARTED) {
					if (bufferQueue != null) {
						if(bufferQueue.isEmpty()){
							SystemClock.sleep(50);
							continue;
						}
						try {
							//ByteBuffer buffer=bufferQueue.get(0).getBuffer();
							//mdata = buffer.array();
							mdata=bufferQueue.get(0).getData();
							if(mdata==null){
								Log.e(TAG, "mdata is null, mVideoFrame do not has a array");
								continue;
							}
							int r=handleData(mdata,bufferQueue.get(0).getValidSize(), HWHiDecoder.VIDEO_DATA);
							//if(r==0){
								bufferQueue.remove(0);
								Log.i(TAG, "success put buffer..."+bufferQueue.size());
							//}else
							//	Log.e(TAG, "error r="+r);
						} catch (Exception e1) {
							// TODO �Զ����ɵ� catch ��
							e1.printStackTrace();
						}
						SystemClock.sleep(50);
					}
					
				}
				Log.i(TAG, "exit consumer thread...");
			}
		}
}
*/