package com.ee.cne.security.auth.spi;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.jboss.security.auth.spi.AbstractServerLoginModule;

import com.ee.cne.util.LoginTypeEnum;
import com.ee.cne.util.LoginUtil;

public class ToolkitLoginModule extends AbstractServerLoginModule {

	private static final Logger log = Logger.getLogger(ToolkitLoginModule.class);

	private static final String HEADER_USER_NAME = "headerUserName";
	private static final String HEADER_ROLE = "headerRole";
	private static final String[] ALL_VALID_OPTIONS = { HEADER_USER_NAME, HEADER_ROLE };

	private Principal principal;
	private String userName = null;
	private String userRoles = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		addValidOptions(ALL_VALID_OPTIONS);

		super.initialize(subject, callbackHandler, sharedState, options);
	}

	public boolean login() throws LoginException {
		log.debug("Inside ToolkitLoginModule >> login");
		super.loginOk = false;

		try {
			HttpServletRequest request = (HttpServletRequest) PolicyContext
					.getContext("javax.servlet.http.HttpServletRequest");

			if (LoginTypeEnum.TOOLKIT_LOGIN != LoginTypeEnum.valueOf(request.getAttribute("LOGIN_TYPE").toString())) {
				return false;
			}

			this.userName = request.getAttribute("HTTP_TK_UID").toString();
			this.userRoles = request.getAttribute("HTTP_TK_ROLES") != null
					? request.getAttribute("HTTP_TK_ROLES").toString()
					: null;
			final String MSISDN = request.getAttribute("HTTP_TK_MSISDN") != null
					? request.getAttribute("HTTP_TK_MSISDN").toString()
					: null;

			log.debug("Request User :: " + userName);
			log.debug("Request Role:: " + userRoles);
			log.debug("Request MSISDN :: " + MSISDN);

			if ((userName != null && !"".equals(userName.trim()))
					&& (userRoles != null && !"".equals(userRoles.trim()))) {

				super.loginOk = true;
				return true;
			}

		} catch (PolicyContextException e) {
			super.loginOk = false;

			log.info("In Exception Inside ToolkitLoginModule -login method--> PolicyContextException" + e.getMessage());
		}

		return false;
	}

	@Override
	protected Principal getIdentity() {

		try {
			principal = super.createIdentity(userName);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return principal;
	}

	@Override
	protected Group[] getRoleSets() {

		return LoginUtil.getGroups(this.principal, userRoles);
	}
}
