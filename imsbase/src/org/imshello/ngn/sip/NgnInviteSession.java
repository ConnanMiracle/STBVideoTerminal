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
* 
* @contributors: See $(imshello_HOME)\contributors.txt
*/
package org.imshello.ngn.sip;

import java.nio.ByteBuffer;
import java.util.Date;

import org.imshello.ngn.NgnEngine;
import org.imshello.ngn.media.NgnMediaType;
import org.imshello.ngn.model.NgnDeviceInfo;
import org.imshello.ngn.model.NgnHistoryEvent;
import org.imshello.ngn.model.NgnHistoryEvent.StatusType;
import org.imshello.baseWRAP.ActionConfig;
import org.imshello.baseWRAP.InviteSession;
import org.imshello.baseWRAP.MediaSessionMgr;


/**
 * Generic INVITE session. Could be either audio/video or MSRP session.
 * This is an abstract class and you should only used it if you want to define
 * you own session.
 */
public abstract class NgnInviteSession extends NgnSipSession{
	protected NgnMediaType mMediaType;
    protected MediaSessionMgr mMediaSessionMgr = null;
    protected InviteState mState;
    protected boolean mRemoteHold;
    protected boolean mLocalHold;
    private boolean mEventAdded;
    private boolean mEventIncoming;
    private final NgnDeviceInfo mRemoteDeviceInfo;

    public enum InviteState{
        NONE,
        INCOMING,
        INPROGRESS,
        REMOTE_RINGING,
        EARLY_MEDIA,
        INCALL,
        TERMINATING,
        TERMINATED,
    }

    /**
     * Creates new Invite session
     * @param sipStack the stack to use
     */
	public NgnInviteSession(NgnSipStack sipStack) {
		super(sipStack);
		
		mRemoteDeviceInfo = new NgnDeviceInfo();
		mState = InviteState.NONE;
	}
	
	protected abstract NgnHistoryEvent getHistoryEvent();
	
	/**
	 * Gets the media type
	 * @return the media type
	 */
	 public NgnMediaType getMediaType(){
         return mMediaType;
     }

	 /**
	  * Gets the session state
	  * @return the session state
	  */
     public InviteState getState(){
         return mState;
     }
     
     /**
      * Sets the session state
      * @param state the new session state
      */
     public void setState(InviteState state){
		mState = state;
		NgnHistoryEvent historyEvent = getHistoryEvent();
		switch (state) {
		case INCOMING:
			mEventIncoming = true;
			break;

		case INPROGRESS:
			mEventIncoming = false;
			break;

		case INCALL:
			if(historyEvent != null){
				historyEvent.setStartTime(new Date().getTime());
				historyEvent.setEndTime(historyEvent.getEndTime());
				historyEvent.setStatus(mEventIncoming ? StatusType.Incoming : StatusType.Outgoing);
			}
			break;

		case TERMINATED:
		case TERMINATING:
			if(historyEvent != null && !mEventAdded){
				mEventAdded = true;
				if(historyEvent.getStatus() != StatusType.Missed){
					historyEvent.setEndTime(new Date().getTime());
				}
				historyEvent.setRemoteParty(getRemotePartyUri());
				NgnEngine.getInstance().getHistoryService().addEvent(historyEvent);
			}
			break;
		default:
			{
				break;
			}
		}
     }

     /**
      * Checks whether the session is active or not
      * @return
      */
     public boolean isActive(){
    	 return mState != InviteState.NONE
         && mState != InviteState.TERMINATING 
         && mState != InviteState.TERMINATED;
     }

	public boolean isLocalHeld() {
		return mLocalHold;
	}
	
	public void setLocalHold(boolean localHold){
 		mLocalHold = localHold;
 	}
	
	public boolean isRemoteHeld(){
		return mRemoteHold;
	}
	
	public void setRemoteHold(boolean remoteHold){
		mRemoteHold = remoteHold;
	}
	
	public NgnDeviceInfo getRemoteDeviceInfo(){
		return mRemoteDeviceInfo;
	}
	
	public boolean sendInfo(ByteBuffer content, String contentType){
		if(content != null){
			ActionConfig config = new ActionConfig();
			config.addHeader("Content-Type", contentType);
			boolean ret = ((InviteSession)this.getSession()).sendInfo(content, content.capacity(), config);
			config.delete();
			return ret;
		}
		return false;
	}
	
	public boolean sendInfo(String content, String contentType){
		if(content != null){
			ActionConfig config = new ActionConfig();
			config.addHeader("Content-Type", contentType);
			final byte[] bytes = content.getBytes();
	        ByteBuffer payload = ByteBuffer.allocateDirect(bytes.length);
	        payload.put(bytes);
			boolean ret = ((InviteSession)this.getSession()).sendInfo(payload, payload.capacity(), config);
			config.delete();
			return ret;
		}
		return false;
	}
     
     /**
      * Gets the media session manager associated to this session
      * @return the media session manager
      */
     public MediaSessionMgr getMediaSessionMgr(){
    	 if (mMediaSessionMgr == null){
    		 mMediaSessionMgr = ((InviteSession)getSession()).getMediaMgr();
         }
         return mMediaSessionMgr;
     }
}
