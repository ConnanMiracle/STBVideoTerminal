/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.imshello.baseWRAP;

public class SMSData {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected SMSData(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(SMSData obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        baseWRAPJNI.delete_SMSData(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public SMSData() {
    this(baseWRAPJNI.new_SMSData(), true);
  }

  public twrap_sms_type_t getType() {
    return twrap_sms_type_t.swigToEnum(baseWRAPJNI.SMSData_getType(swigCPtr, this));
  }

  public int getMR() {
    return baseWRAPJNI.SMSData_getMR(swigCPtr, this);
  }

  public long getPayloadLength() {
    return baseWRAPJNI.SMSData_getPayloadLength(swigCPtr, this);
  }

  public long getPayload(java.nio.ByteBuffer output, long maxsize) {
    return baseWRAPJNI.SMSData_getPayload(swigCPtr, this, output, maxsize);
  }

  public String getOA() {
    return baseWRAPJNI.SMSData_getOA(swigCPtr, this);
  }

  public String getDA() {
    return baseWRAPJNI.SMSData_getDA(swigCPtr, this);
  }

}