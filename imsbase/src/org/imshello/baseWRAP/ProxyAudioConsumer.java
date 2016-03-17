/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.imshello.baseWRAP;

public class ProxyAudioConsumer extends ProxyPlugin {
  private long swigCPtr;

  protected ProxyAudioConsumer(long cPtr, boolean cMemoryOwn) {
    super(baseWRAPJNI.ProxyAudioConsumer_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ProxyAudioConsumer obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        baseWRAPJNI.delete_ProxyAudioConsumer(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public boolean setActualSndCardPlaybackParams(int nPtime, int nRate, int nChannels) {
    return baseWRAPJNI.ProxyAudioConsumer_setActualSndCardPlaybackParams(swigCPtr, this, nPtime, nRate, nChannels);
  }

  public boolean queryForResampler(int nInFreq, int nOutFreq, int nFrameDuration, int nChannels, int nResamplerQuality) {
    return baseWRAPJNI.ProxyAudioConsumer_queryForResampler(swigCPtr, this, nInFreq, nOutFreq, nFrameDuration, nChannels, nResamplerQuality);
  }

  public boolean setPullBuffer(java.nio.ByteBuffer pPullBufferPtr, long nPullBufferSize) {
    return baseWRAPJNI.ProxyAudioConsumer_setPullBuffer(swigCPtr, this, pPullBufferPtr, nPullBufferSize);
  }

  public long pull(java.nio.ByteBuffer pOutput, long nSize) {
    return baseWRAPJNI.ProxyAudioConsumer_pull__SWIG_0(swigCPtr, this, pOutput, nSize);
  }

  public long pull(java.nio.ByteBuffer pOutput) {
    return baseWRAPJNI.ProxyAudioConsumer_pull__SWIG_1(swigCPtr, this, pOutput);
  }

  public long pull() {
    return baseWRAPJNI.ProxyAudioConsumer_pull__SWIG_2(swigCPtr, this);
  }

  public boolean setGain(long nGain) {
    return baseWRAPJNI.ProxyAudioConsumer_setGain(swigCPtr, this, nGain);
  }

  public long getGain() {
    return baseWRAPJNI.ProxyAudioConsumer_getGain(swigCPtr, this);
  }

  public boolean reset() {
    return baseWRAPJNI.ProxyAudioConsumer_reset(swigCPtr, this);
  }

  public void setCallback(ProxyAudioConsumerCallback pCallback) {
    baseWRAPJNI.ProxyAudioConsumer_setCallback(swigCPtr, this, ProxyAudioConsumerCallback.getCPtr(pCallback), pCallback);
  }

  public java.math.BigInteger getMediaSessionId() {
    return baseWRAPJNI.ProxyAudioConsumer_getMediaSessionId(swigCPtr, this);
  }

  public static boolean registerPlugin() {
    return baseWRAPJNI.ProxyAudioConsumer_registerPlugin();
  }

}
