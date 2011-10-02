/*
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
package com.mockey.storage.file;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileInfo {

	private String filename;
	private long length;
	private long lastModified;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public String getSizeDesc() {

		return StorageUnit.of(this.length).format(this.length);
	}

	public String getLastModifiedDesc() {
		Date date = new Date(this.lastModified);
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String result = format.format(date);
		return result;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	private enum StorageUnit {
		BYTE("B", 1L), KILOBYTE("KB", 1L << 10), MEGABYTE("MB", 1L << 20), GIGABYTE(
				"GB", 1L << 30), TERABYTE("TB", 1L << 40), PETABYTE("PB",
				1L << 50), EXABYTE("EB", 1L << 60);
		private final String symbol;
		private final long divider; // divider of BASE unit

		StorageUnit(String name, long divider) {
			this.symbol = name;
			this.divider = divider;
		}

		// Source:
		// http://groups.google.com/group/comp.lang.java.help/browse_thread/thread/0db818517ca9de79/b0a55aa19f911204?pli=1
		public static StorageUnit of(final long number) {
			final long n = number > 0 ? -number : number;
			if (n > -(1L << 10)) {
				return BYTE;
			} else if (n > -(1L << 20)) {
				return KILOBYTE;
			} else if (n > -(1L << 30)) {
				return MEGABYTE;
			} else if (n > -(1L << 40)) {
				return GIGABYTE;
			} else if (n > -(1L << 50)) {
				return TERABYTE;
			} else if (n > -(1L << 60)) {
				return PETABYTE;
			} else { // n >= Long.MIN_VALUE
				return EXABYTE;
			}
		}

		public String format(long number) {
			return nf.format((double) number / divider) + " " + symbol;
		}

		private static java.text.NumberFormat nf = java.text.NumberFormat
				.getInstance();
		static {
			nf.setGroupingUsed(false);
			nf.setMinimumFractionDigits(0);
			nf.setMaximumFractionDigits(1);
		}
	}
}
