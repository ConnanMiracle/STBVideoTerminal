/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.imshello.baseWRAP;

public class SafeObject {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected SafeObject(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(SafeObject obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        baseWRAPJNI.delete_SafeObject(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public SafeObject() {
    this(baseWRAPJNI.new_SafeObject(), true);
  }

  public int Lock() {
    return baseWRAPJNI.SafeObject_Lock(swigCPtr, this);
  }

  public int UnLock() {
    return baseWRAPJNI.SafeObject_UnLock(swigCPtr, this);
  }

}