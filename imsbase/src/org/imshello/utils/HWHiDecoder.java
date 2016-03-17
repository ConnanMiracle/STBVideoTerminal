package org.imshello.utils;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.imshello.baseWRAP.tmedia_pref_video_size_t;
import org.imshello.ngn.NgnEngine;
import org.imshello.ngn.services.INgnConfigurationService;
import org.imshello.ngn.utils.NgnConfigurationEntry;

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
		//private native int ParamsInit(int audio_dec_type, int audio_decode_mode, int video_decode_type,int frame_rate, int display_channel,int virtual_screen_width, int virtual_screen_height, int virtual_screen_offset_top, int virtual_screen_offset_left);
		private native int ParamsInit(int frame_rate, int virtual_screen_width, int virtual_screen_height, int window_width, int window_height, int window_offset_horizontal, int window_offset_vertical,int is_small_window);
		private native int PrepareNative();
		private native int StartNative();
		private native int StopNative();
		private native int DeviceDeInit();
		private native int AudioDataCallback(byte[] data, int size);
		private native int VideoDataCallback(byte[] data, int size);
		
		//private List<DataBufferItem> bufferQueue=new ArrayList<DataBufferItem>();
		private DataBufferQueue bufferQueue=null;
		private ConsumerThread2 mConsumerThread=new ConsumerThread2();
		
		private int frameWidth;
		private int frameHeight;
		private int frameRate;
		
		//private static HWHiDecoder instance;
		
		static{
			try {

				System.loadLibrary("player");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e(TAG, e.toString());
				
			}
			
		}
		
/*		private HWHiDecoder(int audio_dec_type, int audio_decode_mode, int video_decode_type,int frame_rate, int display_channel,int virtual_screen_width, int virtual_screen_height, int virtual_screen_offset_top, int virtual_screen_offset_left){
			this.status=IDLE;
			int ret=this.DeviceInit();
			if(ret==0){
				ret=this.ParamsInit(audio_dec_type, audio_decode_mode, video_decode_type, frame_rate, display_channel, virtual_screen_width, virtual_screen_height, virtual_screen_offset_top, virtual_screen_offset_left);

				if(ret==0)
					this.status=INITED;
			}
			bufferQueue=new DataBufferQueue(new HWHiDecoderConsumer());
		}*/
		
		private HWHiDecoder(){
			this.status=IDLE;
		}
		
		class HWHiDecoderConsumer implements DataBufferQueue.DataBufferQueueConsumer{
			@Override
			public int handleData(byte[] data, int validSize) {
				// TODO 自动生成的方法存根
				return HWHiDecoder.this.decodeData(data, validSize, HWHiDecoder.VIDEO_DATA);
			}
		}
		
		/**
		 * 默认的HWHiDecoder是远端播放的大屏，即窗口大小和虚拟屏幕大小一致，和视频的宽高一致。
		 * @return
		 */
		public static HWHiDecoder getDefaultDecoder(){
			HWHiDecoder	instance=new HWHiDecoder();
			INgnConfigurationService mConfigurationService = NgnEngine.getInstance().getConfigurationService();
			instance.parseQosSettingsToFrameSetting(tmedia_pref_video_size_t.valueOf(
						mConfigurationService.getString(
								NgnConfigurationEntry.QOS_PREF_VIDEO_SIZE,
								NgnConfigurationEntry.DEFAULT_QOS_PREF_VIDEO_SIZE)));
			instance.frameRate=mConfigurationService.getInt(NgnConfigurationEntry.QOS_VIDEO_FPS,
		        		NgnConfigurationEntry.DEFAULT_QOS_VIDEO_FPS);
			instance.setParamsAndInit(instance.frameWidth, instance.frameHeight, 0, 0, 0);
				
			return instance;
			//return new HWHiDecoder(0, 1, 0, 30, 1, 1280, 720, 0, 0);
		}
		
		public static  HWHiDecoder getDecoder(int frameRate, int frameWidth, int frameHeight, int winWidth, int winHeight, int winOffHor, int winOffVer, int isSmallWindow){

			HWHiDecoder		instance=new HWHiDecoder();
			//远端播放，会设置Virtual Screen，frameWidth frameHeight有效
			if(isSmallWindow==0){
				if((frameWidth>480)&&(frameWidth<3840)&&(frameHeight>480)&&(frameHeight<3840)){//虚拟屏幕宽度，取值范围为[480, 3840]. 虚拟屏幕高度，取值范围为[480, 3840]. 
					if((frameWidth<1280)&&(frameHeight<720)){
						instance.frameWidth=1280;
						instance.frameHeight=720;
					}else{
						instance.frameWidth=frameWidth;
						instance.frameHeight=frameHeight;
					}
				}else{
					instance.frameWidth=1280;
					instance.frameHeight=720;
				}
			}else {//这两个参数没用，先设为默认吧
				instance.frameWidth=1280;
				instance.frameHeight=720;
			}
			instance.frameRate=frameRate;
			//instance.frameWidth=frameWidth;
			//instance.frameHeight=frameHeight;
			
			if((winWidth<1280)&&(winHeight<720)){
				instance.setParamsAndInit( 1280, 720, winOffHor, winOffVer, isSmallWindow);
			}else{
				instance.setParamsAndInit( winWidth, winHeight, winOffHor, winOffVer, isSmallWindow);
			}
			//instance.setParamsAndInit( winWidth, winHeight, winOffHor, winOffVer, isSmallWindow);
			return instance;
		}
		
		private void setParamsAndInit(int winWidth, int winHeight, int winOffHor, int winOffVer, int isSmallWindow){
			Log.e(TAG, "HWHiDecoder setParamsAndInit: "+frameRate+" "+frameWidth+"*"+frameHeight+" "+winWidth+"*"+winHeight+" "+winOffHor+" "+winOffVer+" "+isSmallWindow);
			int ret=this.DeviceInit();
			if(ret==0){
				ret=this.ParamsInit(frameRate, frameWidth, frameHeight, winWidth, winHeight, winOffHor, winOffVer, isSmallWindow);

				if(ret==0)
					this.status=INITED;
			}
			int decoderMaxQueueSize=NgnEngine.getInstance().getConfigurationService().getInt(NgnConfigurationEntry.HI_DECODER_BUFFERQUEUE_SIZE, NgnConfigurationEntry.DEFAULT_HI_DECODER_BUFFERQUEUE_SIZE);
			int decoderMaxBufferSize=NgnEngine.getInstance().getConfigurationService().getInt(NgnConfigurationEntry.HI_DECODER_BUFFERSIZE, NgnConfigurationEntry.DEFAULT_HI_DECODER_BUFFERSIZE);
			bufferQueue=new DataBufferQueue(new HWHiDecoderConsumer(),decoderMaxQueueSize,decoderMaxBufferSize);
			Log.i(TAG, "Decoder buffer queue size: "+decoderMaxQueueSize+"\nDecoder buffer size: "+decoderMaxBufferSize);
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
				Log.e(TAG,"try to prepare HWHiDecoder in non-INITED state!");
			return FAILED;
		}
		
		public int start(){
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
				Log.e(TAG,"try to start HWHiDecoder in non-PREPARED state!");	
			return FAILED;
		}
		
		public void stop(){
			if(this.status==STARTED){
				int ret=this.StopNative();
				if(ret==0){
					this.status=STOPPED;
					//bufferQueue.clear();
					mConsumerThread=null;
				}
			}
			else
				Log.e(TAG,"try to stop HWHiDecoder in non-STARTED state!");		
		}
		
		public void finish(){
			if(this.status==STOPPED){
				int ret=this.DeviceDeInit();
				if(ret==0){
					this.status=IDLE;
				}
			}
			else
				Log.e(TAG,"try to finish HWHiDecoder in non-STOPPED state!");
		}
		
		private int decodeData(byte[] data, int size, int flag){
			if(this.status!=STARTED)
				Log.e(TAG,"try to decode HWHiDecoder in non-STARTED state!");
			if(flag==AUDIO_DATA)
				return this.AudioDataCallback(data,size);
			else if(flag==VIDEO_DATA)
				return this.VideoDataCallback(data,size);
			else
				Log.e(TAG,"Invalid decode data type!");
			return -1;
		}
		
		public int getStatus(){
			return this.status;
		}
		
		public void putBuffer(ByteBuffer buffer,int validSize, int maxSize){
			//bufferQueue.add(bufferQueue.size(),new DataBufferItem(buffer, validSize, maxSize));
			if(this.status!=STARTED){
				Log.e(TAG,"try to decode HWHiDecoder in non-STARTED state!");
				return;
			}
			bufferQueue.produce(buffer, validSize);
		}
		
		public void putBuffer(byte[]data, int validSize,int maxSize){
			//bufferQueue.add(new DataBufferItem(validSize, maxSize, data));
			//Log.i(TAG, "put buffer");
			if(this.status!=STARTED){
				Log.e(TAG,"try to decode HWHiDecoder in non-STARTED state!");
				return;
			}
			bufferQueue.produce(data, validSize);
			
		}
		
		/*class DataBufferItem{
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
		}*/
		
		/*class ConsumerThread extends Thread{
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
							// TODO 自动生成的 catch 块
							e1.printStackTrace();
						}
						SystemClock.sleep(50);
					}
					
				}
				Log.i(TAG, "exit consumer thread...");
			}
		}*/
		
		class ConsumerThread2 extends Thread {
			@Override
			public void run() {
				while (status == STARTED) {
					if (bufferQueue != null) {
						boolean ret = bufferQueue.consume();
						if (ret == false) {
							SystemClock.sleep(10);
							continue;
						}
					}

				}
				Log.i(TAG, "exit consumer thread2...");
			}
		}
		
		
		//虚拟屏幕宽度，取值范围为[480, 3840]. 
		//虚拟屏幕高度，取值范围为[480, 3840].
		private void parseQosSettingsToFrameSetting(tmedia_pref_video_size_t value){
			switch (value) {
			case tmedia_pref_video_size_sqcif:
				//frameWidth=128;
				//frameHeight=98;
				//break;
			case tmedia_pref_video_size_qcif:
				//frameWidth=176;
				//frameHeight=144;
				//break;
			case tmedia_pref_video_size_qvga:
				//frameWidth=320;
				//frameHeight=240;
				//break;
			case tmedia_pref_video_size_cif:
				//frameWidth=352;
				//frameHeight=288;
				//break;
			case tmedia_pref_video_size_hvga:
				//frameWidth=480;
				//frameHeight=320;
				//break;
			case tmedia_pref_video_size_vga:
				frameWidth=640;
				frameHeight=480;
				break;
			case tmedia_pref_video_size_4cif:
				frameWidth=704;
				frameHeight=576;
				break;
			case tmedia_pref_video_size_svga:
				frameWidth=800;
				frameHeight=600;
				break;
			case tmedia_pref_video_size_720p:
				frameWidth=1280;
				frameHeight=720;
				break;
			case tmedia_pref_video_size_16cif:
				frameWidth=1408;
				frameHeight=1152;
				break;
			case tmedia_pref_video_size_1080p:
				frameWidth=1920;
				frameHeight=1080;
				break;
			default:
				frameWidth=1280;
				frameHeight=720;
				break;
			}
		}

/*		class MyDataQueue {
			private int consumerPtr;
			private int producerPtr;
			private MyDataItem[] dataQueue;
			private final int maxQueueSize = 16;

			public MyDataQueue() {
				this.consumerPtr = 0;
				this.producerPtr = 0;
				Log.i(TAG, "new data queue...");
				dataQueue=new MyDataItem[maxQueueSize];
				Log.i(TAG, "data queue initialized!...");
				for (int i = 0; i < maxQueueSize; i++) {
					dataQueue[i] = new MyDataItem();
				}
				Log.i(TAG, "data queue constructor over!...");
			}

			public boolean produce(byte[] data, int validSize) {
				//Log.i(TAG, "producing..."+producerPtr);
				if (dataQueue[producerPtr] != null) {
					Log.i(TAG, "produce 0");
					if(data==null)
						throw new NullPointerException("data is null");
					//Log.i(TAG, "produce 1");
					dataQueue[producerPtr].updateData(data, validSize);
					//Log.i(TAG, "produce 2");
					this.producerPtr++;
					producerPtr %= maxQueueSize;
					//Log.i(TAG, "produce 3");
					return true;
				}
				//Log.i(TAG, "produced..."+producerPtr);
				return false;
			}

			public boolean consume() {
				//Log.i(TAG, "consuming..."+consumerPtr+"  "+producerPtr);
				if (dataQueue[consumerPtr] != null) {
					if (dataQueue[consumerPtr].isValid()) {
						Log.i(TAG, "consuming..."+consumerPtr+dataQueue[consumerPtr].getData()+dataQueue[consumerPtr].getValidSize());
						handleData(dataQueue[consumerPtr].getData(),
								dataQueue[consumerPtr].getValidSize(),
								HWHiDecoder.VIDEO_DATA);
						dataQueue[consumerPtr].setVailid(false);
						consumerPtr++;
						consumerPtr %= maxQueueSize;
						return true;
					}
				}
				Log.i(TAG, "consumed..."+consumerPtr+"  "+producerPtr);
				return false;
			}

		}
		
		
		class MyDataItem {
			public byte[] getData() {
				return data;
			}

			public int getValidSize() {
				return validSize;
			}

			public int getMaxSize() {
				return maxSize;
			}

			public void updateData(byte[] data, int validSize) {
				//Log.i(TAG, "data size "+validSize);
				System.arraycopy(data, 0, this.data, 0, validSize);
				this.validSize = validSize;
				this.isValid = true;
			}

			public MyDataItem(ByteBuffer buffer, int validSize) {
				if (validSize > this.validSize)
					throw new ArrayIndexOutOfBoundsException(
							"The buffer size too big!");
				else {
					byte[] tmp = buffer.array();
					if (tmp != null) {
						System.arraycopy(tmp, 0, this.data, 0, validSize);
						this.validSize = validSize;
						this.isValid = true;
					}

				}
			}

			public MyDataItem() {
				this.validSize = 0;
				this.isValid = false;
			}

			private byte[] data = new byte[1280*720*3/2];
			private int validSize;
			private final int maxSize = 1280*720*3/2;
			private boolean isValid;

			public boolean isValid() {
				return isValid;
			}

			public void setVailid(boolean b) {
				isValid = b;
			}
		}*/
}
