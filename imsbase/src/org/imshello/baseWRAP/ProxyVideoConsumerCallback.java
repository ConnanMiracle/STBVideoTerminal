/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.imshello.baseWRAP;

public class ProxyVideoConsumerCallback {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected ProxyVideoConsumerCallback(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ProxyVideoConsumerCallback obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        baseWRAPJNI.delete_ProxyVideoConsumerCallback(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  protected void swigDirectorDisconnect() {
    swigCMemOwn = false;
    delete();
  }

  public void swigReleaseOwnership() {
    swigCMemOwn = false;
    baseWRAPJNI.ProxyVideoConsumerCallback_change_ownership(this, swigCPtr, false);
  }

  public void swigTakeOwnership() {
    swigCMemOwn = true;
    baseWRAPJNI.ProxyVideoConsumerCallback_change_ownership(this, swigCPtr, true);
  }

  public ProxyVideoConsumerCallback() {
    this(baseWRAPJNI.new_ProxyVideoConsumerCallback(), true);
    baseWRAPJNI.ProxyVideoConsumerCallback_director_connect(this, swigCPtr, swigCMemOwn, true);
  }

  public int prepare(int nWidth, int nHeight, int nFps) {
    return (getClass() == ProxyVideoConsumerCallback.class) ? baseWRAPJNI.ProxyVideoConsumerCallback_prepare(swigCPtr, this, nWidth, nHeight, nFps) : baseWRAPJNI.ProxyVideoConsumerCallback_prepareSwigExplicitProxyVideoConsumerCallback(swigCPtr, this, nWidth, nHeight, nFps);
  }

  public int consume(ProxyVideoFrame frame) {
    return (getClass() == ProxyVideoConsumerCallback.class) ? baseWRAPJNI.ProxyVideoConsumerCallback_consume(swigCPtr, this, ProxyVideoFrame.getCPtr(frame), frame) : baseWRAPJNI.ProxyVideoConsumerCallback_consumeSwigExplicitProxyVideoConsumerCallback(swigCPtr, this, ProxyVideoFrame.getCPtr(frame), frame);
  }

  public int bufferCopied(long nCopiedSize, long nAvailableSize) {
    return (getClass() == ProxyVideoConsumerCallback.class) ? baseWRAPJNI.ProxyVideoConsumerCallback_bufferCopied(swigCPtr, this, nCopiedSize, nAvailableSize) : baseWRAPJNI.ProxyVideoConsumerCallback_bufferCopiedSwigExplicitProxyVideoConsumerCallback(swigCPtr, this, nCopiedSize, nAvailableSize);
  }

  public int start() {
    return (getClass() == ProxyVideoConsumerCallback.class) ? baseWRAPJNI.ProxyVideoConsumerCallback_start(swigCPtr, this) : baseWRAPJNI.ProxyVideoConsumerCallback_startSwigExplicitProxyVideoConsumerCallback(swigCPtr, this);
  }

  public int pause() {
    return (getClass() == ProxyVideoConsumerCallback.class) ? baseWRAPJNI.ProxyVideoConsumerCallback_pause(swigCPtr, this) : baseWRAPJNI.ProxyVideoConsumerCallback_pauseSwigExplicitProxyVideoConsumerCallback(swigCPtr, this);
  }

  public int stop() {
    return (getClass() == ProxyVideoConsumerCallback.class) ? baseWRAPJNI.ProxyVideoConsumerCallback_stop(swigCPtr, this) : baseWRAPJNI.ProxyVideoConsumerCallback_stopSwigExplicitProxyVideoConsumerCallback(swigCPtr, this);
  }

}
