package com.ee.cne.gui.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jboss.logging.Logger;
import com.ee.cne.util.LoginUtil;

/**
 * @author esonchy
 *
 */
public class ToolkitLoginServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static final Logger log = Logger.getLogger(ToolkitLoginServlet.class);
private static String HTTP_TK_MSISDN = "HTTP_TK_MSISDN";

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("context");
    log.info("User contextKey = " + id);

    final String MSISDN = request.getAttribute(HTTP_TK_MSISDN) == null
        || "".equals(request.getAttribute(HTTP_TK_MSISDN)) ? null
            : request.getAttribute(HTTP_TK_MSISDN).toString();

    request.removeAttribute("HTTP_TK_UID");
    request.removeAttribute("");
    request.getSession(true).invalidate();  //TODO remove later 
    String redirecURL = LoginUtil.getProperties().getProperty(LoginUtil.TOOLKIT_REDIRECT_URL);

    if (MSISDN != null) {
      redirecURL = redirecURL + "choiceMSISDN,msisdn=" + MSISDN;
    }

    log.info("Redirected URL : " + redirecURL);
    response.sendRedirect(redirecURL);
  }

}
