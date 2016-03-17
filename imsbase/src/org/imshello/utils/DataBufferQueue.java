package org.imshello.utils;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.imshello.ngn.NgnEngine;
import org.imshello.ngn.utils.NgnConfigurationEntry;

import android.util.Log;

public class DataBufferQueue {
	private int consumerPtr;
	private int producerPtr;
	private MyDataItem[] dataQueue;
	private int maxQueueSize;
	private String TAG=null;
	private DataBufferQueueConsumer consumer;
	
	static interface DataBufferQueueConsumer{
		public abstract int handleData(byte[] data, int validSize);
	}

	public DataBufferQueue(DataBufferQueueConsumer consumer, int maxQueueSize, int maxBufferSize) {
		this.maxQueueSize=maxQueueSize;
		this.consumerPtr = 0;
		this.producerPtr = 0;
		Log.i(TAG, "new data queue...");
		dataQueue=new MyDataItem[maxQueueSize];
		Log.i(TAG, "data queue initialized!...");
		for (int i = 0; i < maxQueueSize; i++) {
			dataQueue[i] = new MyDataItem(maxBufferSize);
		}
		Log.i(TAG, "data queue constructor over!...");
		this.consumer=consumer;
		TAG=DataBufferQueue.class.getCanonicalName()+" of "+consumer.getClass().getName();
	}

	public boolean produce(byte[] data, int validSize) {
		//Log.i(TAG, "producing..."+validSize);
		if (dataQueue[producerPtr] != null) {
			//Log.i(TAG, "produce 0");
			if(data==null){
				//throw new NullPointerException("data is null");
				Log.e(TAG, "enqueue data is null!");
				return false;
			}
			//Log.i(TAG, "produce 1");
			dataQueue[producerPtr].updateData(data, validSize);
			//Log.i(TAG, "producing..."+consumerPtr+"  "+producerPtr+" "+dataQueue[consumerPtr].isValid+" "+dataQueue[producerPtr].isValid);
			//Log.i(TAG, "produce 2");
			this.producerPtr++;
			producerPtr %= maxQueueSize;
			//Log.i(TAG, "produce 3");
			return true;
		}
		//Log.i(TAG, "produced..."+producerPtr);
		return false;
	}
	
	public boolean produce(ByteBuffer buffer,int validSize){
		if (dataQueue[producerPtr] != null) {
			//Log.i(TAG, "produce 0");
			if(buffer==null){
				//throw new NullPointerException("data is null");
				Log.e(TAG, "enqueue data is null!");
				return false;
			}
			//Log.i(TAG, "produce 1");
			dataQueue[producerPtr].updateData(buffer, validSize);
			//Log.i(TAG, "producing..."+consumerPtr+"  "+producerPtr+" "+dataQueue[consumerPtr].isValid+" "+dataQueue[producerPtr].isValid);
			//Log.i(TAG, "produce 2");
			this.producerPtr++;
			producerPtr %= maxQueueSize;
			//Log.i(TAG, "produce 3");
			return true;
		}
		return false;
	}

	public boolean consume() {
		//Log.i(TAG, "consuming...");
		if (dataQueue[consumerPtr] != null) {
			if (dataQueue[consumerPtr].isValid) {
				//Log.i(TAG, "consuming..."+consumerPtr+dataQueue[consumerPtr].getData()+dataQueue[consumerPtr].getValidSize());
				//Log.i(TAG, "consuming..."+consumerPtr+"  "+producerPtr+" "+dataQueue[consumerPtr].isValid+" "+dataQueue[producerPtr].isValid);
				consumer.handleData(dataQueue[consumerPtr].data,
						dataQueue[consumerPtr].validSize);
				dataQueue[consumerPtr].isValid=false;
				consumerPtr++;
				consumerPtr %= maxQueueSize;
				return true;
			}
		}
		//Log.i(TAG, "consumed..."+consumerPtr+"  "+producerPtr);
		return false;
	}
	
	class MyDataItem {
		private byte[] data; ;
		private int validSize;
		private int maxSize;
		private boolean isValid;

		/*public MyDataItem(ByteBuffer buffer, int validSize) {
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
		}*/
		
		public MyDataItem(int maxBufferSize) {
			this.validSize = 0;
			this.isValid = false;
			maxSize=maxBufferSize;
			data= new byte[maxSize];
		}

		public void updateData(byte[] data, int validSize) {
			//Log.i(TAG, "data size "+validSize);
			int sizeToPut=validSize;
			if(validSize>maxSize){
				Log.e(TAG, TAG+"updateData(): enqueue data size is bigger than "+maxSize+" actucal size: "+validSize);
				//throw new BufferOverflowException();
				sizeToPut=maxSize;
			}
			System.arraycopy(data, 0, this.data, 0, sizeToPut);
			this.validSize = sizeToPut;
			this.isValid = true;
		}
		
		public void updateData(ByteBuffer buffer,int validSize){
			int sizeToPut=validSize;
			if(validSize>maxSize){
				Log.e(TAG, TAG+"updateData(): enqueue data size is bigger than "+maxSize+" actucal size: "+validSize);
				//throw new BufferOverflowException();
				sizeToPut=maxSize;
			}
			buffer.rewind();
			//Log.e(TAG,buffer.position()+" "+buffer.capacity()+""+buffer.remaining()+" "+buffer.limit());
			buffer.get(data,0,sizeToPut);
			this.validSize = sizeToPut;
			this.isValid = true;
		}
	}
}
