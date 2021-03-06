/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.imshello.baseWRAP;

public class RegistrationSession extends SipSession {
  private long swigCPtr;

  protected RegistrationSession(long cPtr, boolean cMemoryOwn) {
    super(baseWRAPJNI.RegistrationSession_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(RegistrationSession obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        baseWRAPJNI.delete_RegistrationSession(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public RegistrationSession(SipStack pStack) {
    this(baseWRAPJNI.new_RegistrationSession(SipStack.getCPtr(pStack), pStack), true);
  }

  public boolean register_(ActionConfig config) {
    return baseWRAPJNI.RegistrationSession_register___SWIG_0(swigCPtr, this, ActionConfig.getCPtr(config), config);
  }

  public boolean register_() {
    return baseWRAPJNI.RegistrationSession_register___SWIG_1(swigCPtr, this);
  }

  public boolean unRegister(ActionConfig config) {
    return baseWRAPJNI.RegistrationSession_unRegister__SWIG_0(swigCPtr, this, ActionConfig.getCPtr(config), config);
  }

  public boolean unRegister() {
    return baseWRAPJNI.RegistrationSession_unRegister__SWIG_1(swigCPtr, this);
  }

  public boolean accept(ActionConfig config) {
    return baseWRAPJNI.RegistrationSession_accept__SWIG_0(swigCPtr, this, ActionConfig.getCPtr(config), config);
  }

  public boolean accept() {
    return baseWRAPJNI.RegistrationSession_accept__SWIG_1(swigCPtr, this);
  }

  public boolean reject(ActionConfig config) {
    return baseWRAPJNI.RegistrationSession_reject__SWIG_0(swigCPtr, this, ActionConfig.getCPtr(config), config);
  }

  public boolean reject() {
    return baseWRAPJNI.RegistrationSession_reject__SWIG_1(swigCPtr, this);
  }

}
