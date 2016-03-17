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
import android.os.Parcelable;

/**
 * Generic event argument containing short string
 */
public class NgnStringEventArgs extends NgnEventArgs {
	private String mValue;
	
	public NgnStringEventArgs(String value){
		mValue = value;
	}
	
	public NgnStringEventArgs(Parcel in){
    	super(in);
    }

    public static final Parcelable.Creator<NgnStringEventArgs> CREATOR = new Parcelable.Creator<NgnStringEventArgs>() {
        public NgnStringEventArgs createFromParcel(Parcel in) {
            return new NgnStringEventArgs(in);
        }

        public NgnStringEventArgs[] newArray(int size) {
            return new NgnStringEventArgs[size];
        }
    };
    
	public String getValue(){
		return mValue;
	}
	
	
	@Override
	public String toString() {
		return mValue;
	}

	@Override
	protected void readFromParcel(Parcel in) {
		mValue = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mValue);
	}
}