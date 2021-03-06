package com.ee.cne.gui.authentication;

import static com.ee.cne.util.LoginUtil.HTTP_SM_ROLES;
import static com.ee.cne.util.LoginUtil.HTTP_SM_UID;
import static com.ee.cne.util.LoginUtil.HTTP_TK_MSISDN;
import static com.ee.cne.util.LoginUtil.HTTP_TK_ROLES;
import static com.ee.cne.util.LoginUtil.HTTP_TK_UID;
import static com.ee.cne.util.LoginUtil.ROLES_SEPARATOR;
import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.connector.Request;
import org.apache.catalina.deploy.LoginConfig;
import org.jboss.logging.Logger;
import com.ee.cne.util.AuthenticationException;
import com.ee.cne.util.LoginTypeEnum;
import com.ee.cne.ws.getctxwithoperations.client.GetCtxWithOperationsClient;
import com.ee.cne.ws.getctxwithoperations.client.ToolkitLoginInfo;

/**
 * @author esonchy
 *
 */
public class SMToolkitAuthenticator extends AuthenticatorBase {
  private static final Logger log = Logger.getLogger(SMToolkitAuthenticator.class);

  private String httpHeaderForSSOAuth = null;
  private String httpHeaderForUserRole = null;
  @SuppressWarnings("unused")
  private String sessionCookieForSSOAuth = null;
  private String contextKeyParamName = null;
  private String httpHeaderToolkitUserId = null;
  private String httpHeaderToolkitUserRole = null;
  private String httpHeaderToolkitMSISDN = null;

  /*
   * This method is being use as Gateway of Authentication module
   */
  @Override
  public boolean authenticate(Request request, HttpServletResponse response, LoginConfig config)
      throws IOException {

    try {
      String userName = null;
      String userPass = "";
      LoginTypeEnum loginType = null;
      Principal principal = request.getUserPrincipal();

      if (principal != null) {
        log.info("User is already authenticated");
        return true;
      }

      // Retrieve SM user Details
      retriveSMRequestAttributes(request);
      // Toolkit auth
      if ((httpHeaderForSSOAuth == null || "".equals(httpHeaderForSSOAuth.trim()))
          && (contextKeyParamName != null && !"".equals(contextKeyParamName.trim()))) {

        ToolkitLoginInfo toolkitLoginInfo =
            GetCtxWithOperationsClient.fetchToolkitAuthenticationDetails(contextKeyParamName);
        populateToolkitRequestAttributes(request, toolkitLoginInfo);
      }

      if ((httpHeaderForSSOAuth == null || "".equals(httpHeaderForSSOAuth.trim()))
          && (contextKeyParamName == null || "".equals(contextKeyParamName.trim()))) {

        throw new AuthenticationException("SM user ID and ContextParam both can not be null");
      }

      if (httpHeaderForSSOAuth != null && !"".equals(httpHeaderForSSOAuth)) {

        userName = httpHeaderForSSOAuth;
        loginType = LoginTypeEnum.SM_LOGIN;
      } else if (httpHeaderToolkitUserId != null && !"".equals(httpHeaderToolkitUserId.trim())) {

        userName = httpHeaderToolkitUserId;
        loginType = LoginTypeEnum.TOOLKIT_LOGIN;
      }
      log.info("Login Type :: " + loginType);
      if (userName == null || "".equals(userName)) {
        throw new AuthenticationException("User Id can not be null or blank");
      }

      request.setAttribute("LOGIN_TYPE", loginType);
      final Realm realm = context.getRealm();
      principal = realm.authenticate(userName, userPass);

      resetHeaderValues();
      if (principal == null) {
        return false;
      }

      Session session = request.getSessionInternal(true);
      session.setNote(Constants.SESS_USERNAME_NOTE, userName);
      session.setNote(Constants.SESS_PASSWORD_NOTE, userPass);
      request.setUserPrincipal(principal);

      register(request, response, principal, HttpServletRequest.FORM_AUTH, userName, userPass);
    } catch (AuthenticationException exc) {

      log.error("Exception details :: " + exc.getMessage());
      request.setAttribute(RequestDispatcher.ERROR_EXCEPTION, exc);
      response.sendRedirect("loginError");
      // response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exc.getMessage());
    }

    return true;
  }

  /**
   * @param request
   * @param response
   * @param config
   * @throws IOException
   */
  protected void forwardToErrorPage(Request request, HttpServletResponse response,
      LoginConfig config) throws IOException {
    RequestDispatcher disp =
        context.getServletContext().getRequestDispatcher(config.getLoginPage());
    try {
      disp.forward(request.getRequest(), response);
    } catch (Exception exc) {
      String msg = exc.getMessage();
      log.warn(msg, exc);
      request.setAttribute(RequestDispatcher.ERROR_EXCEPTION, exc);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
    }
  }

  /**
   * reset header details to null
   */
  private void resetHeaderValues() {
    this.httpHeaderForSSOAuth = null;
    this.httpHeaderForUserRole = null;
    this.sessionCookieForSSOAuth = null;
    this.contextKeyParamName = null;

    this.httpHeaderToolkitUserId = null;
    this.httpHeaderToolkitUserRole = null;
    this.httpHeaderToolkitMSISDN = null;
  }

  /**
   * @param request
   */
  private void retriveSMRequestAttributes(Request request) {
    this.httpHeaderForSSOAuth = request.getHeader(HTTP_SM_UID);
    this.httpHeaderForUserRole = request.getHeader(HTTP_SM_ROLES);
    this.sessionCookieForSSOAuth = request.getHeader("SMSESSION");
    this.contextKeyParamName = request.getParameter("context");

    log.info("SM USER ID :" + httpHeaderForSSOAuth + "\t|httpHeaderForUserRole : "
        + httpHeaderForUserRole + "\t|contextKeyParamName : " + contextKeyParamName);

    // TODO need to remove later
    @SuppressWarnings("unchecked")
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      System.out.print("HeaderName: " + headerName + "|\t");
      @SuppressWarnings("unchecked")
      Enumeration<String> headers = request.getHeaders(headerName);
      while (headers.hasMoreElements()) {
        String headerValue = headers.nextElement();
        System.out.println("HeaderValue: " + headerValue);
      }
    }

  }

  /**
   * @param request
   * @param toolkitLoginInfo
   */
  private void populateToolkitRequestAttributes(Request request,
      ToolkitLoginInfo toolkitLoginInfo) {

    this.httpHeaderToolkitUserId = toolkitLoginInfo.getuId();
    this.httpHeaderToolkitUserRole = toolkitLoginInfo.getRoleList() != null
        ? String.join(ROLES_SEPARATOR, toolkitLoginInfo.getRoleList())
        : null;
    this.httpHeaderToolkitMSISDN = toolkitLoginInfo.getMsisdn();

    request.setAttribute(HTTP_TK_UID, httpHeaderToolkitUserId);
    request.setAttribute(HTTP_TK_ROLES, httpHeaderToolkitUserRole);
    request.setAttribute(HTTP_TK_MSISDN, httpHeaderToolkitMSISDN);
  }

}
