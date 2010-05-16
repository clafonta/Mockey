package com.mockey.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class ServiceMergeServlet extends HttpServlet {
	private Log log = LogFactory.getLog(ServiceMergeServlet.class);

	private static final long serialVersionUID = 5503460488900643184L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		req.setAttribute("services", store.getServices());
		req.setAttribute("plans", store.getServicePlans());

		RequestDispatcher dispatch = req
				.getRequestDispatcher("service_merge.jsp");

		dispatch.forward(req, resp);
	}

	/**
	 * 
	 * 
	 * @param req
	 *            basic request
	 * @param resp
	 *            basic resp
	 * @throws ServletException
	 *             basic
	 * @throws IOException
	 *             basic
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String[] serviceMergeIdList = req
				.getParameterValues("serviceIdMergeSource[]");
		Enumeration<String> names = req.getParameterNames();
		while(names.hasMoreElements()){
			log.debug(names.nextElement());
		}
		Long serviceIdMergeSource = null;
		Long serviceIdMergeDestination = null;
		ServiceMergeResults mergeResults = null;
		Map<String, String> responseMap = new HashMap<String, String>();
		try {
			for (int i = 0; i < serviceMergeIdList.length; i++) {

				serviceIdMergeSource = new Long(serviceMergeIdList[i]);
				serviceIdMergeDestination = new Long(req
						.getParameter("serviceIdMergeDestination"));
				if (!serviceIdMergeSource.equals(serviceIdMergeDestination)) {

					Service serviceMergeSource = store
							.getServiceById(serviceIdMergeSource);
					Service serviceMergeDestination = store
							.getServiceById(serviceIdMergeDestination);
					ConfigurationReader configurationReader = new ConfigurationReader();
					mergeResults = configurationReader.mergeServices(
							serviceMergeSource, serviceMergeDestination,
							mergeResults);

				}
				responseMap.put("additions", mergeResults.getAdditionMsg());
				responseMap.put("conflicts", mergeResults.getConflictMsg());
			}
			
		} catch (Exception e) {
			// Do nothing
			log.error("Something wrong with merging services.", e);
		}
		
		// IF NO CONFLICTS, THEN DELETE OLD SOURCE SERVICES
		if(mergeResults!=null && (mergeResults.getConflictMsgs()==null || mergeResults.getConflictMsgs().isEmpty())){
			for (int i = 0; i < serviceMergeIdList.length; i++) {
				serviceIdMergeSource = new Long(serviceMergeIdList[i]);
				Service service = store.getServiceById(serviceIdMergeSource);
				store.deleteService(service);
			}
			
		}

		PrintWriter out = resp.getWriter();

		String resultingJSON = Util.getJSON(responseMap);
		out.println(resultingJSON);

		out.flush();
		out.close();

		return;
		// AJAX thing. Return nothing at this time.
	}
}
