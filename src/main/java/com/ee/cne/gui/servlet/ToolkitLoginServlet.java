package com.ee.cne.gui.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ee.cne.gui.util.PropertyReaderUtil;

public class ToolkitLoginServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = request.getParameter("context");
		System.out.println(id);

		PrintWriter writer = response.getWriter();

		// build HTML code
		final String MSISDN = request.getAttribute("HTTP_TK_MSISDN") == null
				|| "".equals(request.getAttribute("HTTP_TK_MSISDN")) ? null
						: request.getAttribute("HTTP_TK_MSISDN").toString();

		String htmlRespone = "<html>";
		htmlRespone += "<h2>Your username is : " + request.getAttribute("HTTP_TK_UID") + "<br/>";
		htmlRespone += "And Your ROLEs are : " + request.getAttribute("HTTP_TK_ROLES") + "<br/>";
		htmlRespone += "And Your MSISDN is : " + MSISDN + "</h2>";
		htmlRespone += "</html>";

		request.getSession(true).invalidate();
		// return response
		String redirecURL = PropertyReaderUtil.getProperties().getProperty("redirect.utl");
		writer.println(htmlRespone);

		if (MSISDN != null) {
			redirecURL = redirecURL+"choiceMSISDN,msisdn=" + MSISDN;
		}

		System.out.println("Redirected URL : " + redirecURL);
		response.sendRedirect(redirecURL);
	}

}
