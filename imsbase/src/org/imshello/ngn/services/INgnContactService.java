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

import org.imshello.ngn.model.NgnContact;
import org.imshello.ngn.utils.NgnCallbackFunc;
import org.imshello.ngn.utils.NgnObservableList;

public interface INgnContactService extends INgnBaseService{
	void setOnBeginLoadCallback(NgnCallbackFunc<Object> callback);
	void setOnNewPhoneNumberCallback(NgnCallbackFunc<String> callback);
	void setOnEndLoadCallback(NgnCallbackFunc<Object> callback);
	boolean load();
	boolean isLoading();
	boolean isReady();
	
	NgnContact newContact(int id, String displayName);
	NgnContact getContactByUri(String uri);
	NgnContact getContactByPhoneNumber(String anyPhoneNumber);
	
	NgnObservableList<NgnContact> getObservableContacts();
}
