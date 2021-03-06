/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.imshello.baseWRAP;

public class XcapMessage {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected XcapMessage(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(XcapMessage obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        baseWRAPJNI.delete_XcapMessage(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public byte[] getXcapContent() {
    final int clen = (int)this.getXcapContentLength();
    if(clen>0){
		final java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocateDirect(clen);
        final int read = (int)this.getXcapContent(buffer, clen);
        final byte[] bytes = new byte[read];
        buffer.get(bytes, 0, read);
        return bytes;
    }
    return null;
  }

  public XcapMessage() {
    this(baseWRAPJNI.new_XcapMessage(), true);
  }

  public short getCode() {
    return baseWRAPJNI.XcapMessage_getCode(swigCPtr, this);
  }

  public String getPhrase() {
    return baseWRAPJNI.XcapMessage_getPhrase(swigCPtr, this);
  }

  public String getXcapHeaderValue(String name, long index) {
    return baseWRAPJNI.XcapMessage_getXcapHeaderValue__SWIG_0(swigCPtr, this, name, index);
  }

  public String getXcapHeaderValue(String name) {
    return baseWRAPJNI.XcapMessage_getXcapHeaderValue__SWIG_1(swigCPtr, this, name);
  }

  public String getXcapHeaderParamValue(String name, String param, long index) {
    return baseWRAPJNI.XcapMessage_getXcapHeaderParamValue__SWIG_0(swigCPtr, this, name, param, index);
  }

  public String getXcapHeaderParamValue(String name, String param) {
    return baseWRAPJNI.XcapMessage_getXcapHeaderParamValue__SWIG_1(swigCPtr, this, name, param);
  }

  public long getXcapContentLength() {
    return baseWRAPJNI.XcapMessage_getXcapContentLength(swigCPtr, this);
  }

  public long getXcapContent(java.nio.ByteBuffer output, long maxsize) {
    return baseWRAPJNI.XcapMessage_getXcapContent(swigCPtr, this, output, maxsize);
  }

}
