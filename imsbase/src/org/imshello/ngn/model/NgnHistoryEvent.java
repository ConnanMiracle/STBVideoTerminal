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
package org.imshello.ngn.model;

import java.util.Date;

import org.imshello.ngn.media.NgnMediaType;
import org.imshello.ngn.utils.NgnUriUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public abstract class NgnHistoryEvent  implements Comparable<NgnHistoryEvent> {
	
	public enum StatusType{
		Outgoing,
		Incoming,
		Missed,
		Failed
	}
	
	// For performance reasons, do not use Date() class
	
	@Element(name = "type", required = true)
	protected NgnMediaType mMediaType;
	@Element(name = "start", required = true)
	protected long mStartTime;
	@Element(name = "end", required = true)
	protected long mEndTime;
	@Element(name = "remote", required = true)
	protected String mRemoteParty;
	@Element(name = "seen", required = true)
	protected boolean mSeen;
	@Element(name = "status", required = true)
	protected StatusType mStatus;
	
	private String mDisplayName;
	
	
	protected NgnHistoryEvent(NgnMediaType mediaType, String remoteParty){
		mMediaType = mediaType;
		mStartTime = new Date().getTime();
		mEndTime = mStartTime;
		mRemoteParty = remoteParty;
		mStatus = StatusType.Missed;
	}
	
	public void setStartTime(long time){
		mStartTime = time;
	}
	
	public long getStartTime(){
		return mStartTime;
	}
	
	public long getEndTime(){
		return mEndTime;
	}
	
	public void setEndTime(long time){
		mEndTime = time;
	}
	
	public NgnMediaType getMediaType(){
		return mMediaType;
	}
	
	public String getRemoteParty(){
		return mRemoteParty;
	}
	
	public void setRemoteParty(String remoteParty){
		mRemoteParty = remoteParty;
	}
	
	public boolean isSeen(){
		return mSeen;
	}
	
	public void setSeen(boolean seen){
		mSeen = seen;
	}
	
	public StatusType getStatus(){
		return mStatus;
	}
	
	public void setStatus(StatusType status){
		mStatus = status;
	}
	
	public void setDisplayName(String displayName){
		mDisplayName = displayName;
	}
	
	public String getDisplayName(){
		if(mDisplayName == null){
			mDisplayName = NgnUriUtils.getDisplayName(getRemoteParty());
		}
		return mDisplayName;
	}
	
	@Override
	public int compareTo(NgnHistoryEvent another) {
		return (int)(mStartTime - another.mStartTime);
	}
}
