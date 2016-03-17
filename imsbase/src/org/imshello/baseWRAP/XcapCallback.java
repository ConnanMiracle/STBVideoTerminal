/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.imshello.baseWRAP;

public class XcapCallback {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected XcapCallback(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(XcapCallback obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        baseWRAPJNI.delete_XcapCallback(swigCPtr);
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
    baseWRAPJNI.XcapCallback_change_ownership(this, swigCPtr, false);
  }

  public void swigTakeOwnership() {
    swigCMemOwn = true;
    baseWRAPJNI.XcapCallback_change_ownership(this, swigCPtr, true);
  }

  public XcapCallback() {
    this(baseWRAPJNI.new_XcapCallback(), true);
    baseWRAPJNI.XcapCallback_director_connect(this, swigCPtr, swigCMemOwn, true);
  }

  public int onEvent(XcapEvent e) {
    return (getClass() == XcapCallback.class) ? baseWRAPJNI.XcapCallback_onEvent(swigCPtr, this, XcapEvent.getCPtr(e), e) : baseWRAPJNI.XcapCallback_onEventSwigExplicitXcapCallback(swigCPtr, this, XcapEvent.getCPtr(e), e);
  }

}
