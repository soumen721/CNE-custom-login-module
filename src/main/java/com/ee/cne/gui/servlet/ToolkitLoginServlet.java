package com.ee.cne.gui.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

import com.ee.cne.util.LoginUtil;

public class ToolkitLoginServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ToolkitLoginServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = request.getParameter("context");
		log.info(id);

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

		request.removeAttribute("HTTP_TK_UID");
		request.removeAttribute("");
		request.getSession(true).invalidate();
		// return response
		String redirecURL = LoginUtil.getProperties().getProperty(LoginUtil.TOOLKIT_REDIRECT_URL);
		writer.println(htmlRespone);

		if (MSISDN != null) {
			redirecURL = redirecURL+"choiceMSISDN,msisdn=" + MSISDN;
		}

		log.info("Redirected URL : " + redirecURL);
		response.sendRedirect(redirecURL);
	}

}
