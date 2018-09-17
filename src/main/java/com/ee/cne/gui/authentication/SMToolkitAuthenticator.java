package com.ee.cne.gui.authentication;

import java.io.IOException;
import java.security.Principal;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.connector.Request;
import org.apache.catalina.deploy.LoginConfig;

import com.ee.cne.ws.getctxwithoperations.client.GetCtxWithOperationsClient;
import com.ee.cne.ws.getctxwithoperations.client.ToolkitLoginInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SMToolkitAuthenticator extends AuthenticatorBase {

	private String httpHeaderForSSOAuth = null;
	@SuppressWarnings("unused")
	private String httpHeaderForUserRole = null;
	private String sessionCookieForSSOAuth = null;
	private String contextKeyParamName = null;
	private String httpHeaderToolkitUserId = null;
	private String httpHeaderToolkitUserRole = null;
	private String httpHeaderToolkitMSISDN = null;

	@Override
	protected boolean authenticate(Request request, HttpServletResponse response, LoginConfig config)
			throws IOException {

		String userName = null;
		String password = null;
		// Retrieve SM user Details
		retriveSMRequestAttributes(request);
		Principal principal = request.getUserPrincipal();

		if (principal != null) {
			log.info("User is already authenticated");
			return true;
		}

		// Toolkit auth
		if ((httpHeaderForSSOAuth == null || "".equals(httpHeaderForSSOAuth.trim()))
				&& (contextKeyParamName != null && !"".equals(contextKeyParamName.trim()))) {

			ToolkitLoginInfo toolkitLoginInfo = GetCtxWithOperationsClient.fetchToolkitAuthenticationDetails(request);
			populateToolkitRequestAttributes(request, toolkitLoginInfo);
		}

		if (httpHeaderForSSOAuth != null && !"".equals(httpHeaderForSSOAuth) && sessionCookieForSSOAuth != null
				&& !"".equals(sessionCookieForSSOAuth)) {

			userName = httpHeaderForSSOAuth;
			password = sessionCookieForSSOAuth;
		} else if (httpHeaderToolkitUserId != null && !"".equals(httpHeaderToolkitUserId.trim())) {

			userName = httpHeaderToolkitUserId;
			password = "";
		}

		final Realm realm = context.getRealm();

		principal = realm.authenticate(userName, password);

		if (principal == null) {
			return false;
		}

		Session session = request.getSessionInternal(true);
		session.setNote(Constants.SESS_USERNAME_NOTE, userName);
		session.setNote(Constants.SESS_PASSWORD_NOTE, password);
		request.setUserPrincipal(principal);

		register(request, response, principal, HttpServletRequest.FORM_AUTH, userName, password);

		return true;
	}

	private void retriveSMRequestAttributes(Request request) {

		this.httpHeaderForSSOAuth = request.getHeader("HTTP_SM_UID");
		this.httpHeaderForUserRole = request.getHeader("HTTP_SM_ROLES");
		this.sessionCookieForSSOAuth = request.getHeader("SMSESSION");
		this.contextKeyParamName = request.getParameter("context");
	}

	private void populateToolkitRequestAttributes(Request request, ToolkitLoginInfo toolkitLoginInfo) {
		
		this.httpHeaderToolkitUserId = toolkitLoginInfo.getUid();
		this.httpHeaderToolkitUserRole = toolkitLoginInfo.getRoleList().stream().map(e->e.toString()).collect(Collectors.joining(","));
		this.httpHeaderToolkitMSISDN = toolkitLoginInfo.getMsisdn();
		
		request.setAttribute("HTTP_TK_UID", httpHeaderToolkitUserId);
		request.setAttribute("HTTP_TK_ROLES", httpHeaderToolkitUserRole);
		request.setAttribute("HTTP_TK_MSISDN", httpHeaderToolkitMSISDN);
		request.addHeader("HTTP_TK_MSISDN", httpHeaderToolkitMSISDN);

		System.out.println("*********" + request.getAttribute("HTTP_TK_ROLES"));
	}

}