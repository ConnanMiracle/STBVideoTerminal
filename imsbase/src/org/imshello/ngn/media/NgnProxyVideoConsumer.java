/* Copyright (C) 2012, imshello Telecom <http://www.imshello.org>
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
package org.imshello.ngn.media;

import java.math.BigInteger;

import org.imshello.ngn.NgnApplication;
import org.imshello.ngn.NgnEngine;
import org.imshello.ngn.utils.NgnConfigurationEntry;
import org.imshello.baseWRAP.ProxyPlugin;
import org.imshello.baseWRAP.ProxyVideoConsumer;

import android.content.Context;
import android.util.Log;
import android.view.View;

public abstract class NgnProxyVideoConsumer extends NgnProxyPlugin{
	protected boolean mFullScreenRequired;
	
	public NgnProxyVideoConsumer(BigInteger id, ProxyPlugin plugin) {
		super(id, plugin);
		
		mFullScreenRequired = NgnEngine.getInstance().getConfigurationService().getBoolean(
				NgnConfigurationEntry.GENERAL_FULL_SCREEN_VIDEO, 
				NgnConfigurationEntry.DEFAULT_GENERAL_FULL_SCREEN_VIDEO);
	}
	
	public static NgnProxyVideoConsumer createInstance(BigInteger id, ProxyVideoConsumer consumer){
		return new NgnProxyVideoConsumerSV(id, consumer);
		//Log.e("NgnProxyVideoConsumer","isGlEs2Supported() ? "+ NgnApplication.isGlEs2Supported());
		//return NgnApplication.isGlEs2Supported() ? new NgnProxyVideoConsumerGL2(id, consumer) : new NgnProxyVideoConsumerSV(id, consumer);
	}
	
	public abstract void setContext(Context context);
	public abstract View startPreview(Context context);
	public abstract View startPreview();
}
