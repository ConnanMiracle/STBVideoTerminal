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
package org.imshello.ngn.utils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

public class NgnTimer extends Timer{
	private final static String TAG = NgnTimer.class.getCanonicalName();
	
	public NgnTimer(){
		super();
	}

	@Override
	public void cancel() {
		try{
			super.cancel();
		}
		catch(IllegalStateException ise){
			Log.w(TAG, ise.toString());
		}
	}

	@Override
	public void schedule(TimerTask task, Date when, long period) {
		try{
			super.schedule(task, when, period);
		}
		catch(IllegalStateException ise){
			Log.w(TAG, ise.toString());
		}
	}

	@Override
	public void schedule(TimerTask task, Date when) {
		try{
			super.schedule(task, when);
		}
		catch(IllegalStateException ise){
			Log.w(TAG, ise.toString());
		}
	}

	@Override
	public void schedule(TimerTask task, long delay, long period) {
		try{
			super.schedule(task, delay, period);
		}
		catch(IllegalStateException ise){
			Log.w(TAG, ise.toString());
		}
	}

	@Override
	public void schedule(TimerTask task, long delay) {
		try{
			super.schedule(task, delay);
		}
		catch(IllegalStateException ise){
			Log.w(TAG, ise.toString());
		}
	}
}
