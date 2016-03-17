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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NgnListUtils {
	public static <T> List<T> filter(Collection<T>list, NgnPredicate<T> predicate) {
		List<T> result = new ArrayList<T>();
		if(list != null){
		    for (T element: list) {
		        if (predicate.apply(element)) {
		            result.add(element);
		        }
		    }
		}
	    return result;
	}
	
	public static <T> T getFirstOrDefault(Collection<T>list, NgnPredicate<T> predicate) {
		if(list != null){
		    for (T element: list) {
		        if (predicate.apply(element)) {
		            return element;
		        }
		    }
		}
		return null;
	}
}
