package org.imshello.utils;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.imshello.ngn.media.NgnProxyVideoConsumerSV;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;

public class AVCDecoder {
	private MediaCodec mediaCodec;
	ByteBuffer decodedDataBuffer = null;
	Handler mHandler = null;
	private MediaFormat mFormat=null;
	private Surface mSurface = null;
	private ByteBuffer[] mCodecInputBuffers;
	private ByteBuffer[] mCodecOutputBuffers;

	private int width, height;
	private boolean isFrameParamsSet=false;
	private boolean isSurfaceSet=false;

	public AVCDecoder(int width, int height, Surface surface, Handler handler) {
		this.mSurface = surface;
		this.width = width;
		this.height = height;
		this.mHandler = handler;
		createDecoder();
	}

	public AVCDecoder() {

	}

	public void createDecoder() {

		mediaCodec = MediaCodec.createDecoderByType("video/avc");
		mFormat = MediaFormat.createVideoFormat("video/avc", width, height);
		mediaCodec.configure(mFormat, mSurface, null, 0);
		//mediaCodec..setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
		mediaCodec.start();
		mCodecInputBuffers = mediaCodec.getInputBuffers();
		//mCodecOutputBuffers = mediaCodec.getOutputBuffers();
		Log.e("avc decoder", "decoder created!"+width+"*"+height+ " surface: "+String.valueOf(mSurface==null));
	}

	public AVCDecoder(Handler handler) {
		this.mHandler = handler;
	}

	public void open() {
		mediaCodec.start();
	}

	public void close() {
		try {
			mediaCodec.stop();
			mediaCodec.release();
			isSurfaceSet=false;
			isFrameParamsSet=false;
			width=0;
			height=0;
			Log.e("avc decoder", "decoder closed!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized int decode(ByteBuffer input, int size, int maxlength) {
		int flag=-1;
		int index=-1;
		try {
			MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

			int outputBufferIndex = -1;
			int inputBufferIndex = mediaCodec.dequeueInputBuffer(0);
			if (inputBufferIndex >= 0) {
				ByteBuffer inputBuffer = mCodecInputBuffers[inputBufferIndex];
				inputBuffer.clear();
				// TODO check the put() whether the ByteBuffer inputBuffer
				// copies all valid bytes in ByteBuffer input
				inputBuffer.put(input.array(),0,size);
				//Log.i("avc decoder ", "array :"+" "+String.valueOf(input.array()==null)+input.array().length);
				//Log.i("avc decoder ", "size :"+" "+size+"maxlength: "+maxlength);
				Log.i("AVCDECODER","input.limit " + input.limit() + " input.position " + input.position());
				Log.i("AVCDECODER", "buffer.limit " + inputBuffer.limit() + " buffer.position "+ inputBuffer.position());
				mediaCodec.queueInputBuffer(inputBufferIndex, 0, size, 0, 0);
				/*outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,10000);
				if (outputBufferIndex < 0) {
					return -1;
				}*/
				
				flag=1;
			}
			//if (outputBufferIndex < 0) {
				outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,0);
				//Log.i("TAG", "outputBufferIndex = " + outputBufferIndex);
			//}
			int indexToRelease=-1;
			while(outputBufferIndex >= 0) {
		    	
				//ByteBuffer outBuffer = mCodecOutputBuffers[outputBufferIndex];

				//Log.i("MYAVCDecoder", "buffer info-->" + bufferInfo.offset+ "--" + bufferInfo.size + "--" + bufferInfo.flags
				//		+ "--" + bufferInfo.presentationTimeUs);

				//decodedDataBuffer.clear();// position=0; limit=capacity;
				//outBuffer.flip();// limit=current_position; position=0;
				//decodedDataBuffer.put(outBuffer);
				/*Log.i("AVCDECODER"," " + outBuffer.limit() + " " + outBuffer.position());
				Log.i("AVCDECODER", " " + decodedDataBuffer.limit() + " "
						+ decodedDataBuffer.position());
				Log.i("avc decoder ", String.valueOf(mHandler==null));*/
				/*if (mHandler!=null) {
					
					mHandler.obtainMessage(NgnProxyVideoConsumerSV.HARD_DECODE,
							bufferInfo.size - bufferInfo.offset,
							decodedDataBuffer.capacity()).sendToTarget();
				}*/
				if(indexToRelease!=-1){//one more output buffer available, ignore all except the last one
					mediaCodec.releaseOutputBuffer(indexToRelease, false);
				}
				indexToRelease=outputBufferIndex;
				index=outputBufferIndex;
				//mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
				// outputBufferIndex =
				// mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
				flag++;
				outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,0);
				
				//return 0;
			} 
			return index;
			/*
				if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) 
				{
			     	mCodecOutputBuffers = mediaCodec.getOutputBuffers();
			    } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
				    final MediaFormat format = mediaCodec.getOutputFormat();
				    getColorFormat(format);	
			    }
		    */

		} catch (Throwable t) {
			t.printStackTrace();
		}
		return flag;
	}
	
	public void updateView(int outputBufferindex){
		mediaCodec.releaseOutputBuffer(outputBufferindex, true);
	}
	
	
	public void setDeocdeBuffer(ByteBuffer buffer) {
		this.decodedDataBuffer = buffer;
	}

	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}

	public void setFrameParams(int width, int height) {
		this.width = width;
		this.height = height;
		Log.e("avc decoder", "set codec params!"+width+"*"+height);
		isFrameParamsSet=true;
		if(isSurfaceSet&&isFrameParamsSet)
			createDecoder();
	}

	public interface OnFrameAvailabkeListener {
		public void onFrameAvailable(long timestamp, int index, boolean EOS);
	};

	public void getColorFormat(MediaFormat format) {
		int colorFormat = format.getInteger(MediaFormat.KEY_COLOR_FORMAT);

		int QOMX_COLOR_FormatYUV420PackedSemiPlanar64x32Tile2m8ka = 0x7FA30C03;

		String formatString = "";
		if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format12bitRGB444) {
			formatString = "COLOR_Format12bitRGB444";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format16bitARGB1555) {
			formatString = "COLOR_Format16bitARGB1555";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format16bitARGB4444) {
			formatString = "COLOR_Format16bitARGB4444";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format16bitBGR565) {
			formatString = "COLOR_Format16bitBGR565";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format16bitRGB565) {
			formatString = "COLOR_Format16bitRGB565";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format18bitARGB1665) {
			formatString = "COLOR_Format18bitARGB1665";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format18BitBGR666) {
			formatString = "COLOR_Format18BitBGR666";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format18bitRGB666) {
			formatString = "COLOR_Format18bitRGB666";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format19bitARGB1666) {
			formatString = "COLOR_Format19bitARGB1666";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format24BitABGR6666) {
			formatString = "COLOR_Format24BitABGR6666";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format24bitARGB1887) {
			formatString = "COLOR_Format24bitARGB1887";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format24BitARGB6666) {
			formatString = "COLOR_Format24BitARGB6666";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format24bitBGR888) {
			formatString = "COLOR_Format24bitBGR888";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format24bitRGB888) {
			formatString = "COLOR_Format24bitRGB888";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format25bitARGB1888) {
			formatString = "COLOR_Format25bitARGB1888";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format32bitARGB8888) {
			formatString = "COLOR_Format32bitARGB8888";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format32bitBGRA8888) {
			formatString = "COLOR_Format32bitBGRA8888";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format8bitRGB332) {
			formatString = "COLOR_Format8bitRGB332";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatCbYCrY) {
			formatString = "COLOR_FormatCbYCrY";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatCrYCbY) {
			formatString = "COLOR_FormatCrYCbY";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatL16) {
			formatString = "COLOR_FormatL16";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatL2) {
			formatString = "COLOR_FormatL2";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatL24) {
			formatString = "COLOR_FormatL24";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatL32) {
			formatString = "COLOR_FormatL32";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatL4) {
			formatString = "COLOR_FormatL4";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatL8) {
			formatString = "COLOR_FormatL8";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatMonochrome) {
			formatString = "COLOR_FormatMonochrome";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatRawBayer10bit) {
			formatString = "COLOR_FormatRawBayer10bit";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatRawBayer8bit) {
			formatString = "COLOR_FormatRawBayer8bit";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatRawBayer8bitcompressed) {
			formatString = "COLOR_FormatRawBayer8bitcompressed";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYCbYCr) {
			formatString = "COLOR_FormatYCbYCr";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYCrYCb) {
			formatString = "COLOR_FormatYCrYCb";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV411PackedPlanar) {
			formatString = "COLOR_FormatYUV411PackedPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV411Planar) {
			formatString = "COLOR_FormatYUV411Planar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar) {
			formatString = "COLOR_FormatYUV420PackedPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar) {
			formatString = "COLOR_FormatYUV420PackedSemiPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422PackedPlanar) {
			formatString = "COLOR_FormatYUV422PackedPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422PackedSemiPlanar) {
			formatString = "COLOR_FormatYUV422PackedSemiPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422Planar) {
			formatString = "COLOR_FormatYUV422Planar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422PackedSemiPlanar) {
			formatString = "COLOR_FormatYUV422PackedSemiPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422Planar) {
			formatString = "COLOR_FormatYUV422Planar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422SemiPlanar) {
			formatString = "COLOR_FormatYUV422SemiPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV444Interleaved) {
			formatString = "COLOR_FormatYUV444Interleaved";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_QCOM_FormatYUV420SemiPlanar) {
			formatString = "COLOR_QCOM_FormatYUV420SemiPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar) {
			formatString = "COLOR_TI_FormatYUV420PackedSemiPlanar";
		} else if (colorFormat == QOMX_COLOR_FormatYUV420PackedSemiPlanar64x32Tile2m8ka) {
			formatString = "QOMX_COLOR_FormatYUV420PackedSemiPlanar64x32Tile2m8ka";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar) {
			formatString = "COLOR_FormatYUV420Planar";
		}

		Log.i("TAG", formatString);
	}
	
	public void setSurface(Surface surface){
		this.mSurface=surface;
		Log.e("acv docoder", "surface set! "+String.valueOf(surface==null));
		isSurfaceSet=true;
		if(isSurfaceSet&&isFrameParamsSet)
			createDecoder();
		
	}
}
