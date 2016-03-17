/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.imshello.baseWRAP;

public class InfoSession extends SipSession {
  private long swigCPtr;

  protected InfoSession(long cPtr, boolean cMemoryOwn) {
    super(baseWRAPJNI.InfoSession_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(InfoSession obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        baseWRAPJNI.delete_InfoSession(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public InfoSession(SipStack pStack) {
    this(baseWRAPJNI.new_InfoSession(SipStack.getCPtr(pStack), pStack), true);
  }

  public boolean send(java.nio.ByteBuffer payload, long len, ActionConfig config) {
    return baseWRAPJNI.InfoSession_send__SWIG_0(swigCPtr, this, payload, len, ActionConfig.getCPtr(config), config);
  }

  public boolean send(java.nio.ByteBuffer payload, long len) {
    return baseWRAPJNI.InfoSession_send__SWIG_1(swigCPtr, this, payload, len);
  }

  public boolean accept(ActionConfig config) {
    return baseWRAPJNI.InfoSession_accept__SWIG_0(swigCPtr, this, ActionConfig.getCPtr(config), config);
  }

  public boolean accept() {
    return baseWRAPJNI.InfoSession_accept__SWIG_1(swigCPtr, this);
  }

  public boolean reject(ActionConfig config) {
    return baseWRAPJNI.InfoSession_reject__SWIG_0(swigCPtr, this, ActionConfig.getCPtr(config), config);
  }

  public boolean reject() {
    return baseWRAPJNI.InfoSession_reject__SWIG_1(swigCPtr, this);
  }

}
