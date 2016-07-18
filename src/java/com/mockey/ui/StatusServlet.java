/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2009-2010  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
 * neil.cronin (neil AT rackle DOT com) 
 * lorin.kobashigawa (lkb AT kgawa DOT com)
 * rob.meyer (rob AT bigdis DOT com)
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.storage.xml.MockeyXmlFileManager;

/**
 * Directs you to the "Hey, I'm alive" response, useful for automation to ensure
 * the status of Mockey, e.g. "up and running?"
 * 
 * @author clafonta
 * 
 */
public class StatusServlet extends HttpServlet {

	private static IMockeyStorage store = StorageRegistry.MockeyStorage;
	private static final SimpleDateFormat formatter = new SimpleDateFormat(
			"MM/dd/yyyy | hh:mm:ss");
	/**
	 * 
	 */
	private static final long serialVersionUID = -2712413420685133084L;

	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setHeader("Content-Encoding", "UTF-8");
		resp.setContentType("text/json; charset=UTF-8");
		
		File locationOfServicesBeingWritten = MockeyXmlFileManager.getInstance().getBasePathFile();
		RequestDispatcher dispatch = req.getRequestDispatcher("status.jsp");
		Long timeOfCreation = store.getTimeOfCreation();
		String timeOfCreationString = formatter.format(new Date(timeOfCreation)); // .parse(lastvisit);
		req.setAttribute("since", timeOfCreationString );
		req.setAttribute("repoPath", locationOfServicesBeingWritten.getAbsolutePath() );
		dispatch.forward(req, resp);
	}

}
