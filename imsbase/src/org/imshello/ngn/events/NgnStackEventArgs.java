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
package org.imshello.ngn.events;

import android.os.Parcel;

/**
 * Event argument associated to the stack
 */
public class NgnStackEventArgs extends NgnEventArgs{
	private final static String TAG = NgnStackEventArgs.class.getCanonicalName();
	
	private final NgnStackEventTypes mType;
    private final String mPhrase;

    public static final String ACTION_STACK_EVENT = TAG + ".ACTION_STACK_EVENT";
    
    public static final String EXTRA_EMBEDDED = NgnEventArgs.EXTRA_EMBEDDED;
    
    public NgnStackEventArgs(NgnStackEventTypes type, String phrase){
    	super();
        mType = type;
        mPhrase = phrase;
    }

    public NgnStackEventTypes getEventType(){
        return mType;
    }

    public String getPhrase(){
        return mPhrase;
    }

	@Override
	protected void readFromParcel(Parcel in) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
}
