/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.imshello.baseWRAP;

public class ActionConfig {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected ActionConfig(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ActionConfig obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        baseWRAPJNI.delete_ActionConfig(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public ActionConfig() {
    this(baseWRAPJNI.new_ActionConfig(), true);
  }

  public boolean addHeader(String name, String value) {
    return baseWRAPJNI.ActionConfig_addHeader(swigCPtr, this, name, value);
  }

  public boolean addPayload(java.nio.ByteBuffer payload, long len) {
    return baseWRAPJNI.ActionConfig_addPayload(swigCPtr, this, payload, len);
  }

  public boolean setActiveMedia(twrap_media_type_t type) {
    return baseWRAPJNI.ActionConfig_setActiveMedia(swigCPtr, this, type.swigValue());
  }

  public ActionConfig setResponseLine(short code, String phrase) {
    long cPtr = baseWRAPJNI.ActionConfig_setResponseLine(swigCPtr, this, code, phrase);
    return (cPtr == 0) ? null : new ActionConfig(cPtr, false);
  }

  public ActionConfig setMediaString(twrap_media_type_t type, String key, String value) {
    long cPtr = baseWRAPJNI.ActionConfig_setMediaString(swigCPtr, this, type.swigValue(), key, value);
    return (cPtr == 0) ? null : new ActionConfig(cPtr, false);
  }

  public ActionConfig setMediaInt(twrap_media_type_t type, String key, int value) {
    long cPtr = baseWRAPJNI.ActionConfig_setMediaInt(swigCPtr, this, type.swigValue(), key, value);
    return (cPtr == 0) ? null : new ActionConfig(cPtr, false);
  }

}
