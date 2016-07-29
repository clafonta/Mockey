/**
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2009-2010  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.mockey.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.Service;
import com.mockey.model.ServiceStat;

/**
 * 
 * @author clafonta
 *
 */
public class ServiceStatHelper {

	// Example: 2014/11/01 00:34:44
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static Logger logger = Logger.getLogger(ServiceStatHelper.class);
	private static final long ONE_MINUTE_IN_MILLIS = 60000;// millisecs

	/**
	 * Returns NOW plus 1 minute.
	 * 
	 * @return date of now, plus a minute.
	 */
	public static Date getNowPlusOneMinute() {
		Calendar date = Calendar.getInstance();
		long t = date.getTimeInMillis();
		Date nowDatePlusMinute = new Date(t + ONE_MINUTE_IN_MILLIS);
		return nowDatePlusMinute;
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return date that is the oldest between A or B.
	 */
	public static Date getEarlierTime(Date a, Date b) {
		if (a.before(b)) {
			return a;
		} else if (b.before(a)) {
			return b;
		} else {
			return a;
		}
	}

	/**
	 * 
	 * @param date
	 * @return String in 'yyyy/MM/dd HH:mm:ss' format
	 */
	public static String getStringFromDate(Date date) {
		return ServiceStatHelper.DATE_FORMATTER.format(date);
	}

	/**
	 * 
	 * @param dateAsString
	 *            should be in yyyy/MM/dd HH:mm:ss format.
	 * @return Date object if string is in a valid format, otherwise, return's
	 *         defaultDate.
	 */
	public static Date getDateFromString(String dateAsString, Date defaultDate) {

		Date date = null;
		if (dateAsString == null || dateAsString.trim().length() == 0) {
			return defaultDate;
		}

		try {
			date = ServiceStatHelper.DATE_FORMATTER.parse(dateAsString);

		} catch (Exception e) {
			logger.info("Malformed date in string format:" + dateAsString, e);
			date = defaultDate;
		}
		return date;
	}

	/**
	 * 
	 * @param listOfServices
	 * @return
	 */
	public static Map<String, ServiceStat> getMapListOfAllServices(List<Service> listOfServices) {

		// #1 - Build a list of all possible Service names
		Map<String, ServiceStat> statMap = new HashMap<String, ServiceStat>();
		for (Service serv : listOfServices) {
			ServiceStat stat = statMap.get(serv.getServiceName()); //
			if (stat == null) {
				stat = new ServiceStat();
				stat.setServiceName(serv.getServiceName());
			}
			statMap.put(stat.getServiceName(), stat);
		}
		return statMap;
	}

	/**
	 * 
	 * @param statMap
	 * @param historyOfServiceRequests
	 * @param filterStartDate
	 * @param filterEndDate
	 * @return
	 */
	public static Map<String, ServiceStat> incrementServiceStatCount(Map<String, ServiceStat> statMap,
			List<FulfilledClientRequest> historyOfServiceRequests, Date filterStartDate, Date filterEndDate) {
		for (FulfilledClientRequest requestInstance : historyOfServiceRequests) {
			ServiceStat stat = statMap.get(requestInstance.getServiceName());
			if (stat == null) {
				// This could happen, if the Service request is new/unknown.
				stat = new ServiceStat();
				stat.setServiceName(requestInstance.getServiceName());
			}
			// We only update the count if in the timerange.
			Date timeOfRequest = requestInstance.getTime();

			if (ServiceStatHelper.isTimeInBetweenStartAndEnd(timeOfRequest, filterStartDate, filterEndDate)) {
				stat.setCount(stat.getCount() + 1);
				
				// Is this the earliest stat hit?
				Date earliestTimeSeenFromThisService = stat.getTime();
				Date timeOfThisRequest = new Date(timeOfRequest.getTime()); 
				
				if(earliestTimeSeenFromThisService==null){
					stat.setTime(timeOfThisRequest);
				}else if(earliestTimeSeenFromThisService.after(timeOfThisRequest)){
					stat.setTime(timeOfThisRequest);
				}else {
					// Do nothing, we have the earliest time.
				}
			}
			statMap.put(stat.getServiceName(), stat);
		}
		return statMap;
		
	}

	/**
	 * 
	 * @param time
	 * @param start
	 * @param end
	 * @return true if time is between start and end times, inclusive.
	 */
	public static boolean isTimeInBetweenStartAndEnd(Date time, Date start, Date end) {
		if (time == null || start == null || end == null) {
			return false;
		} else if ((time.after(start) && time.before(end)) || time.equals(start) || time.equals(end)) {
			return true;
		} else {
			return false;
		}
	}

}
