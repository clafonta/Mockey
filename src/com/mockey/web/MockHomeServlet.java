/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mockey.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.MockServiceStore;
import com.mockey.MockServiceStoreImpl;

public class MockHomeServlet extends HttpServlet {

	
	private static final long serialVersionUID = -5485332140449853235L;
	
	private static MockServiceStore store = MockServiceStoreImpl.getInstance();
	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setAttribute("services", store.getOrderedList());
		RequestDispatcher dispatch = req.getRequestDispatcher("home.jsp");

		dispatch.forward(req, resp);
	}

}
