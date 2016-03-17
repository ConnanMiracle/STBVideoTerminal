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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NgnDateTimeUtils {
	static final DateFormat sDefaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String now(String dateFormat) {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	    return sdf.format(cal.getTime());
	}
	
	public static String now() {
	    Calendar cal = Calendar.getInstance();
	    return sDefaultDateFormat.format(cal.getTime());
	}
	
	public static Date parseDate(String date, DateFormat format){
		if(!NgnStringUtils.isNullOrEmpty(date)){
			try {
				return format == null ? sDefaultDateFormat.parse(date) : format.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return new Date();
	}
	
	public static Date parseDate(String date){
		return parseDate(date, null);
	}
	
	public static boolean isSameDay(Date d1, Date d2){
		return d1.getDay() == d2.getDay() && d1.getMonth() == d2.getMonth() && d1.getYear() == d2.getYear();
	}
}