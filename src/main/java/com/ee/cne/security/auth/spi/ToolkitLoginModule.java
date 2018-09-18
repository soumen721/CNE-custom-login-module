package com.ee.cne.security.auth.spi;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.AbstractServerLoginModule;

public class ToolkitLoginModule extends AbstractServerLoginModule {
	
	private static final Logger log = Logger.getLogger(ToolkitLoginModule.class);
	
	private static final String HEADER_USER_NAME = "headerUserName";
	private static final String HEADER_ROLE = "headerRole";
	private static final String[] ALL_VALID_OPTIONS = { HEADER_USER_NAME, HEADER_ROLE };

	private Principal principal;
	private String name = null;
	private String roles = null;

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

			this.name = request.getAttribute("HTTP_TK_UID").toString();
			this.roles = request.getAttribute("HTTP_TK_ROLES").toString();
			String MSISDN = request.getAttribute("HTTP_TK_MSISDN").toString();

			log.debug("Request User :: " + name);
			log.debug("Request Role:: " + roles);
			log.debug("Request MSISDN :: " + MSISDN);

			if ((name != null && !"".equals(name.trim())) && (roles != null && !"".equals(roles.trim())) && (MSISDN != null
					&& !"".equals(MSISDN.trim()))) {

				super.loginOk = true;
				return true;
			}

			return true;
		} catch (PolicyContextException e) {
			super.loginOk = false;

			log.info("In Exception Inside ToolkitLoginModule -login method--> PolicyContextException" + e.getMessage());
		}

		return false;
	}

	@Override
	protected Principal getIdentity() {

		try {
			principal = super.createIdentity(name);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return principal;
	}

	@Override
	protected Group[] getRoleSets() {

		Group roleGroup = new SimpleGroup("Roles");
		Group callerPrincipal = new SimpleGroup("CallerPrincipal");
		Group[] groups = { roleGroup, callerPrincipal };

		Arrays.asList(roles.split(",")).stream().filter(e -> !Objects.isNull(e)).forEach(ar -> {
			roleGroup.addMember(new SimplePrincipal(ar));
		});

		callerPrincipal.addMember(this.principal);
		return groups;
	}
}
