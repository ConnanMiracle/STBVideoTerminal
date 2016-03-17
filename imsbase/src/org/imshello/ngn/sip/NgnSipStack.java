/* Copyright (C) 2010-2014, IMSHello R&D Group
*  
*
* Contact:  <rd(at)imshello(dot)org>
*	
* This file is part of 'IMSHello for Android' Project 
*
* Notes: you can redistribute it and/or modify it under the terms of 
* the GNU General Public License as published by the Free Software Foundation, either version 3 
* of the License, or (at your option) any later version.
*	
* it is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
* See the GNU General Public License for more details.
*	
* You should have received a copy of the GNU General Public License along 
* with this program; if not, write to the Free Software Foundation, Inc., 
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package org.imshello.ngn.sip;

import org.imshello.ngn.NgnApplication;
import org.imshello.ngn.NgnEngine;
import org.imshello.ngn.R;
import org.imshello.ngn.services.INgnNetworkService;
import org.imshello.ngn.services.impl.NgnNetworkService.DNS_TYPE;
import org.imshello.baseWRAP.SipCallback;
import org.imshello.baseWRAP.SipStack;

import android.os.Build;
import android.util.Log;

/**
 * SIP/IMS Stack
 */
public class NgnSipStack extends SipStack{

	public enum STACK_STATE {
	     NONE, STARTING, STARTED, STOPPING, STOPPED, DISCONNECTED
	}
	
	private STACK_STATE mState = STACK_STATE.NONE;
	private String mCompId;
	private final INgnNetworkService mNetworkService;
	
	/**
	 * Creates new SIP/IMS Stack. You should use
	 * @param callback
	 * @param realmUri
	 * @param impiUri
	 * @param impuUri
	 */
	public NgnSipStack(SipCallback callback, String realmUri, String impiUri, String impuUri){
		super(callback, realmUri, impiUri, impuUri);
		
		// Services
		mNetworkService = NgnEngine.getInstance().getNetworkService();
		
		// Set first and second DNS servers (used for DNS NAPTR+SRV discovery and ENUM)
		String dnsServer;
		if((dnsServer = mNetworkService.getDnsServer(DNS_TYPE.DNS_1)) != null && !dnsServer.equals("0.0.0.0")){
			this.addDnsServer(dnsServer);
			if((dnsServer = mNetworkService.getDnsServer(DNS_TYPE.DNS_2)) != null && !dnsServer.equals("0.0.0.0")){
				this.addDnsServer(dnsServer);
			}
		}
		else{
			// On the emulator FIXME
			this.addDnsServer("212.27.40.241");
		}
		
	     // Sip headers
        super.addHeader("Allow", "INVITE, ACK, CANCEL, BYE, MESSAGE, OPTIONS, NOTIFY, PRACK, UPDATE, REFER");
       // super.addHeader("Privacy", "none");
       // super.addHeader("P-Access-Network-Info", "ADSL;utran-cell-id-3gpp=00000000");
       // super.addHeader("User-Agent", String.format("IM-client/OMA1.0 android-ngn-stack/v%s (imshello r%s - %s)", 
		//		NgnApplication.getVersionName(), 
		//		NgnApplication.getContext().getString(R.string.imshello_base_revision),
		//		Build.MODEL));
	}

	@Override
	public boolean start() {
		if(mNetworkService.acquire()){
			mState = STACK_STATE.STARTING;
			Log.e("NgnSipStack", "acquire return true");
			return super.start();
		}
		else{
			Log.e("NgnSipStack", "acquire return false");
			return false;
		}
	}

	@Override
	public boolean stop() {
		mNetworkService.release();
		mState = STACK_STATE.STOPPING;
		return super.stop();
	}
	
	public void setState(STACK_STATE state){
		mState = state;
	}
	
	public STACK_STATE getState(){
		return mState;
	}
	
	public String getSigCompId(){
		return mCompId;
	}
	
	public void setSigCompId(String compId){
		if(mCompId != null && mCompId != compId){
			super.removeSigCompCompartment(mCompId);
		}
		if((mCompId = compId) != null){
			super.addSigCompCompartment(mCompId);
		}
	}
}
