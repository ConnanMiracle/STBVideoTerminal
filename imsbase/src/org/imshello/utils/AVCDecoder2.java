package org.imshello.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import org.imshello.ngn.media.NgnProxyVideoConsumerSV;

import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;

public class AVCDecoder2 {
	private MediaCodec mediaCodec;
	ByteBuffer decodedDataBufferY = null;
	ByteBuffer decodedDataBufferU = null;
	ByteBuffer decodedDataBufferV = null;
	private MediaFormat mFormat = null;
	private Surface mSurface = null;
	private ByteBuffer[] mCodecInputBuffers;
	private ByteBuffer[] mCodecOutputBuffers;
	private boolean useSurface=false;
	private boolean isFrameParamsSet=false;
	private boolean isSurfaceSet=false;
	
	private int width, height;
	public AVCDecoder2(boolean useSurface) {
		this.useSurface=useSurface;
	}

	public void createDecoder() {

		mediaCodec = MediaCodec.createDecoderByType("video/avc");
		mFormat = MediaFormat.createVideoFormat("video/avc", width, height);
		//mFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
		mediaCodec.configure(mFormat, mSurface, null, 0);
		//mediaCodec.setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
		mediaCodec.start();
		mCodecInputBuffers = mediaCodec.getInputBuffers();
		mCodecOutputBuffers = mediaCodec.getOutputBuffers();
		Log.e("avc decoder", "decoder created!" + width + "*" + height
				+ " surface: " + String.valueOf(mSurface == null));
		//matchFormat();
		//Log.i("avc decoder"," "+mediaCodec.getOutputFormat().getInteger(MediaFormat.KEY_COLOR_FORMAT));
	}

	public void open() {
		mediaCodec.start();
	}

	public  void close() {
		try {
			synchronized(mediaCodec){
			mediaCodec.stop();
			mediaCodec.release();
			width = 0;
			height = 0;
			Log.e("avc decoder", "decoder closed!");}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public synchronized int decode(ByteBuffer input, int size, int maxlength) {
		//Log.e("avc decoder", "frame " + width + " * " + height);
		int flag=-1;
		try {
			synchronized (mediaCodec) {
				MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
				int outputBufferIndex = -1;
				int inputBufferIndex = mediaCodec.dequeueInputBuffer(0);
				if (inputBufferIndex >= 0) {
					ByteBuffer inputBuffer = mCodecInputBuffers[inputBufferIndex];
					//Log.i("avc decoder","size of input "+ input.capacity()+" size of input buffer "+ inputBuffer.capacity());
					inputBuffer.clear();
					// TODO check the put() whether the ByteBuffer inputBuffer
					// copies all valid bytes in ByteBuffer input
					// inputBuffer.put(input.array(),0,size);
					input.rewind();
					//inputBuffer.put(input);
					inputBuffer.put(input.array(),0,size);
					// Log.i("avc decoder ",
					// "array :"+" "+String.valueOf(input.array()==null)+input.array().length);
					//Log.i("avc decoder ", "size :" + " " + size + "maxlength: "	+ maxlength);
					// Log.i("AVCDECODER","input.limit " + input.limit() +
					// " input.position " + input.position());
					//Log.i("AVCDECODER", "inputbuffer.limit " + inputBuffer.limit()	+ " inputbuffer.position " + inputBuffer.position());
					mediaCodec.queueInputBuffer(inputBufferIndex, 0, size, 0, 0);
				}
				
				outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
				
				int indexOfLastAvailableData=-1;
				while (outputBufferIndex >= 0) {
					if(indexOfLastAvailableData!=-1){
						mediaCodec.releaseOutputBuffer(indexOfLastAvailableData, false);
					}
					indexOfLastAvailableData=outputBufferIndex;
					outputBufferIndex=mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
					flag++;
					if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
						mCodecOutputBuffers = mediaCodec.getOutputBuffers();
					} else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
						final MediaFormat format = mediaCodec.getOutputFormat();
						getColorFormat(format);
						Log.i("avc decoder"," "	+ format.getInteger(MediaFormat.KEY_COLOR_FORMAT));
					} 						
				} 
				if(indexOfLastAvailableData>=0){
					if ((mSurface == null) && (decodedDataBufferY != null)
							&& (decodedDataBufferU != null)
							&& (decodedDataBufferV != null)) {
						ByteBuffer outBuffer = mCodecOutputBuffers[indexOfLastAvailableData];
						//Log.i("AVCDECODER", "outBuffer.limit " + outBuffer.limit()	+ " outBuffer.position " + outBuffer.position());
						decodedDataBufferY.clear();
						decodedDataBufferU.clear();
						decodedDataBufferV.clear();
						//NV21: YYYYYYYY VUVU    
						//NV12: YYYYYYYY UVUV  

						outBuffer.rewind();
						decodedDataBufferY.put((ByteBuffer) outBuffer
								.limit(width * height));
						outBuffer.limit(outBuffer.capacity());
						for (int i = 0; i < (width * height) / 4; i++) {
							decodedDataBufferU.put(i,
									outBuffer.get(width * height + i * 2));
							decodedDataBufferV.put(i,
									outBuffer.get(width * height + i * 2 + 1));

						}
						/*Log.i("avc decoder", "i= "+ i + " j= " +j);
						Log.i("avc decoder", "y: pos= "+ decodedDataBufferY.position() + " limit= " +decodedDataBufferY.limit());
						Log.i("avc decoder", "u: pos= "+ decodedDataBufferU.position() + " limit= " +decodedDataBufferU.limit());
						Log.i("avc decoder", "v: pos= "+ decodedDataBufferV.position() + " limit= " +decodedDataBufferV.limit());*/
						decodedDataBufferY.rewind();
						decodedDataBufferU.rewind();
						decodedDataBufferV.rewind();
						mediaCodec.releaseOutputBuffer(indexOfLastAvailableData,false);
					}else if((mSurface!=null)&&(decodedDataBufferY == null)
							&& (decodedDataBufferU == null)
							&& (decodedDataBufferV == null)){
						mediaCodec.releaseOutputBuffer(indexOfLastAvailableData,true);
					}					
				}				
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}
		return flag;
	}

	public void setYUVBuffer(ByteBuffer bufferY,ByteBuffer bufferU,ByteBuffer bufferV) {
		if(useSurface)
			return;
		this.decodedDataBufferY = bufferY;
		this.decodedDataBufferU = bufferU;
		this.decodedDataBufferV = bufferV;
	}

	public void setFrameParams(int width, int height) {
		this.width = width;
		this.height = height;
		isFrameParamsSet=true;
		Log.e("avc decoder", "set codec params!" + width + "*" + height);
		if (useSurface) {
			if (isSurfaceSet && isFrameParamsSet)
				createDecoder();
		}else
			createDecoder();

	}


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

	public void setSurface(Surface surface) {
		if (!useSurface)
			return;
		this.mSurface = surface;
		Log.e("acv docoder", "surface set! is null?" + String.valueOf(surface == null));
		isSurfaceSet = true;
		if (isSurfaceSet && isFrameParamsSet)
			createDecoder();

	}

	private void matchFormat() {
		//MediaCodecInfo codecInfo =mediaCodec.getCodecInfo();

		// 获取解码器列表
		int numCodecs = MediaCodecList.getCodecCount();
		MediaCodecInfo codecInfo = null;
		for (int i = 0; i < numCodecs && codecInfo == null; i++) {
			MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
			if (!info.isEncoder()) {
				continue;
			}
			String[] types = info.getSupportedTypes();
			boolean found = false;
			// 轮询所要的解码器
			for (int j = 0; j < types.length && !found; j++) {
				if (types[j].equals("video/avc")) {
					System.out.println("found");
					found = true;
				}
			}
			if (!found)
				continue;
			codecInfo = info;
		}
		Log.d("avc decoder", "Found " + codecInfo.getName() + " supporting "
				+ "video/avc");

		// 检查所支持的colorspace
		int colorFormat = 0;
		MediaCodecInfo.CodecCapabilities capabilities = codecInfo
				.getCapabilitiesForType("video/avc");
		System.out.println("length-" + capabilities.colorFormats.length + "=="
				+ Arrays.toString(capabilities.colorFormats));
		for (int i = 0; i < capabilities.colorFormats.length; i++) {
			int format = capabilities.colorFormats[i];
			switch (format) {
			case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
				Log.i("avc decoder", "COLOR_FormatYUV420Planar "+ format+" "+i);
				colorFormat = format;
				break;
			case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
				Log.i("avc decoder", "OLOR_FormatYUV420PackedPlanar "+ format+" "+i);
				colorFormat = format;
				break;
			case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
				Log.i("avc decoder", "COLOR_FormatYUV420SemiPlanar "+ format+" "+i);
				colorFormat = format;
				break;
			case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
				Log.i("avc decoder", "COLOR_FormatYUV420PackedSemiPlanar "+ format+" "+i);
				colorFormat = format;
				break;
			case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
				colorFormat = format;
				Log.i("avc decoder", "COLOR_TI_FormatYUV420PackedSemiPlanar "+ format+" "+i);
				break;
			default:
				Log.i("avc decoder", "Skipping unsupported color format "
						+ format);
				break;
			}
		}
		Log.i("avc decoder", "color format " + colorFormat);

	}

}
