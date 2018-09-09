package com.ee.cne.security.auth.spi;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.jacc.PolicyContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.AbstractServerLoginModule;

public class ToolkitLoginModule extends AbstractServerLoginModule {
	private static final String HEADER_USER_NAME = "headerUserName";
	private static final String HEADER_ROLE = "headerRole";

	private static final String[] ALL_VALID_OPTIONS = { HEADER_USER_NAME, HEADER_ROLE };

	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		addValidOptions(ALL_VALID_OPTIONS);

		super.initialize(subject, callbackHandler, sharedState, options);
	}

	@Override
	protected Principal getIdentity() {
		try {
			HttpServletRequest request = (HttpServletRequest) PolicyContext
					.getContext("javax.servlet.http.HttpServletRequest");

			String userid = "guest";

			return super.createIdentity(userid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@Override
	protected Group[] getRoleSets() throws LoginException {
		SimpleGroup group = new SimpleGroup("Roles");

		try {

			group.addMember(new SimplePrincipal("Manager"));

		} catch (Exception e) {

			throw new LoginException("Failed to create group member for " + group);

		}

		return new Group[] { group };

	}

	@Override
	public boolean login() throws LoginException {
		/*
		 * //if the request header contains a user id value and the siteminder session
		 * cookie is present. try { HttpServletRequest request = (HttpServletRequest)
		 * PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
		 * if(request.getHeader("userID")!=null &&
		 * request.getHeader("siteMinder")!=null){ super.loginOk=true; return true; }
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * return false;
		 */
		super.loginOk = true;
		return true;
	}

	/*
	 * @Override protected String getUsersPassword() throws LoginException {
	 * System.out.format("MyLoginModule: authenticating user '%s'\n",
	 * getUsername());
	 * 
	 * String password = super.getUsername();
	 * 
	 * password = password.toUpperCase();
	 * 
	 * return password; }
	 * 
	 * protected boolean validatePassword(String inputPassword, String
	 * expectedPassword) {
	 * 
	 * return true;
	 * 
	 * }
	 */
}
