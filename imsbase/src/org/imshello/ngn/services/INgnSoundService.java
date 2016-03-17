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
package org.imshello.ngn.services;

import android.media.Ringtone;

public interface INgnSoundService extends INgnBaseService {
	void startDTMF(int number);
	void stopDTMF();
	
	void startRingTone();
	void stopRingTone();
	
	void startRingBackTone();
	void stopRingBackTone();
	
	void startNewEvent();
	void stopNewEvent();
	
	void startConnectionChanged(boolean connected);
	void stopConnectionChanged(boolean connected);
	
	public Ringtone getRingTonePlayer();//added by wangy
}