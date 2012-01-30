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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 
 * @author clafonta
 * 
 */
public class FileSystemManager {
	
	
	private static Logger logger = Logger.getLogger(FileSystemManager.class);
	private File IMAGE_LOCATION = new File("image_depot");

	private File[] FOLDER_LIST = new File[] { IMAGE_LOCATION };

	/**
	 * Basic constructor
	 */
	public FileSystemManager() {

		for (File folder : FOLDER_LIST) {
			if (!folder.exists()) {
				folder.mkdir();
				logger.debug("Created directory: " + folder.getAbsolutePath());
			} else {
				logger.debug("Directory: " + folder.getAbsolutePath());
			}
		}
	}

	public File getImageFile(String imageFileName) {

		if (!IMAGE_LOCATION.exists()) {
			IMAGE_LOCATION.mkdir();
			logger.debug("Created directory: "
					+ IMAGE_LOCATION.getAbsolutePath());
		}
		String cleanFileName = this.ensureCleanFilename(imageFileName);
		return new File(IMAGE_LOCATION.getAbsolutePath() + File.separatorChar
				+ cleanFileName);
	}

	public FileInfo[] getImageFileList() {
		return getFileList(IMAGE_LOCATION);
	}

	private FileInfo[] getFileList(File folder) {
		List<FileInfo> filteredInputFiles = new ArrayList<FileInfo>();
		String[] inputFiles = folder.list();
		// Remove Hidden files
		for (String fileName : inputFiles) {
			int i = fileName.indexOf(".");
			if (i != 0) {
				File f = this.getImageFile(fileName);
				FileInfo fi = new FileInfo();
				fi.setFilename(f.getName());
				fi.setLastModified(f.lastModified());
				fi.setLength(f.length());
				filteredInputFiles.add(fi);
			}
		}
		return filteredInputFiles.toArray(new FileInfo[filteredInputFiles
				.size()]);
	}

	public boolean deleteImageFile(String fileName) {
		String cleanFileName = this.ensureCleanFilename(fileName);
		return deleteMe(getImageFile(cleanFileName));

	}

	private boolean deleteMe(File f) {

		// Make sure the file or directory exists and isn't write protected
		if (!f.exists())
			throw new IllegalArgumentException(
					"Delete: no such file or directory: " + f.getName());

		if (!f.canWrite())
			throw new IllegalArgumentException("Delete: write protected: "
					+ f.getName());

		// If it is a directory, make sure it is empty
		if (f.isDirectory()) {
			String[] files = f.list();
			if (files.length > 0)
				throw new IllegalArgumentException(
						"Delete: directory not empty: " + f.getName());
		}

		// Attempt to delete it
		boolean success = f.delete();

		if (!success)
			throw new IllegalArgumentException("Delete: deletion failed");

		return success;
	}

	public byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}
	
	private String ensureCleanFilename(String filenameWithPath){
		int index = filenameWithPath.lastIndexOf(File.separatorChar);
		String name = filenameWithPath.substring(index+1);
		return name.trim();
	}
}
