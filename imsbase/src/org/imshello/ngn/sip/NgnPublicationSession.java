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

import org.imshello.ngn.utils.NgnContentType;
import org.imshello.ngn.utils.NgnObservableHashMap;
import org.imshello.baseWRAP.ActionConfig;
import org.imshello.baseWRAP.PublicationSession;
import org.imshello.baseWRAP.SipSession;

import android.util.Log;

public class NgnPublicationSession extends NgnSipSession {
	private final static String TAG = NgnPublicationSession.class.getCanonicalName();

	private final PublicationSession mSession;

	private final static NgnObservableHashMap<Long, NgnPublicationSession> sSessions = new NgnObservableHashMap<Long, NgnPublicationSession>(
			true);

	public static NgnPublicationSession createOutgoingSession(
			NgnSipStack sipStack, String toUri) {
		synchronized (sSessions) {
			final NgnPublicationSession pubSession = new NgnPublicationSession(
					sipStack, toUri);
			sSessions.put(pubSession.getId(), pubSession);
			return pubSession;
		}
	}

	public static void releaseSession(NgnPublicationSession session) {
		synchronized (sSessions) {
			if (session != null && sSessions.containsKey(session.getId())) {
				long id = session.getId();
				session.decRef();
				sSessions.remove(id);
			}
		}
	}

	public static void releaseSession(long id) {
		synchronized (sSessions) {
			NgnPublicationSession session = NgnPublicationSession
					.getSession(id);
			if (session != null) {
				session.decRef();
				sSessions.remove(id);
			}
		}
	}

	public static NgnPublicationSession getSession(long id) {
		synchronized (sSessions) {
			if (sSessions.containsKey(id))
				return sSessions.get(id);
			else
				return null;
		}
	}

	public static int getSize() {
		synchronized (sSessions) {
			return sSessions.size();
		}
	}

	public static boolean hasSession(long id) {
		synchronized (sSessions) {
			return sSessions.containsKey(id);
		}
	}

	protected NgnPublicationSession(NgnSipStack sipStack, String toUri) {
		super(sipStack);
		mSession = new PublicationSession(sipStack);

		super.init();
		super.setSigCompId(sipStack.getSigCompId());
		super.setToUri(toUri);
		super.setFromUri(toUri);

		// default
		mSession.addHeader("Event", "presence");
		mSession.addHeader("Content-Type", NgnContentType.PIDF);
	}

	@Override
	protected SipSession getSession() {
		return mSession;
	}

	public boolean setEvent(String event) {
		return mSession.addHeader("Event", event);
	}

	public boolean setContentType(String contentType) {
		return mSession.addHeader("Content-Type", contentType);
	}

	public boolean publish(byte[] bytes, String event, String contentType) {
		if (bytes != null) {
			final java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.allocateDirect(bytes.length);
			byteBuffer.put(bytes);
			ActionConfig config = new ActionConfig();
			if(event != null){
				config.addHeader("Event", event);
			}
			if(contentType != null){
				config.addHeader("Content-Type", contentType);
			}
			final boolean ret = mSession.publish(byteBuffer, byteBuffer.capacity(), config);
			config.delete();
			return ret;
		}
		else{
			Log.e(TAG, "Null content");
		}
		return false;
	}
	
	public boolean publish(byte[] bytes) {
		return publish(bytes, null, null);
	}
}
