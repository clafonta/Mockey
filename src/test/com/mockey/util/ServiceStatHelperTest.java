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
package com.mockey.util;

import java.util.Calendar;
import java.util.Date;

import org.testng.annotations.Test;

import com.mockey.ui.ServiceStatHelper;

@Test
public class ServiceStatHelperTest {

	@Test
	public void testInBetween() {

		Calendar date = Calendar.getInstance();
		long t = date.getTimeInMillis();
		Date nowDatePlusMinute = new Date(t + 60000);
		Date nowDateMinuMinute = new Date(t - 60000);
		Date now = new Date();

		assert ServiceStatHelper.isTimeInBetweenStartAndEnd(now, nowDateMinuMinute, nowDatePlusMinute) : "expected "
				+ now + " is in between " + nowDatePlusMinute + " and " + nowDatePlusMinute;

		assert ServiceStatHelper.isTimeInBetweenStartAndEnd(now, now, now);

		assert !ServiceStatHelper.isTimeInBetweenStartAndEnd(nowDateMinuMinute, now, nowDatePlusMinute);

	}

	@Test
	public void testGetDateFromString() {
		String testString = null;
		Date defaultTime = new Date();
		assert defaultTime.equals(ServiceStatHelper.getDateFromString(testString, defaultTime));

	}

}
