package com.ee.cne.security.auth.spi;

import java.math.BigDecimal;
import java.security.Principal;
import java.security.acl.Group;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.AbstractServerLoginModule;

public class CustomLoginModule extends AbstractServerLoginModule {

	private Principal principal;

	private String authSql;

	private String rolesSql;

	private String name = null;

	private String password = null;

	@SuppressWarnings("unused")
	private String ssn = null;

	@SuppressWarnings("unchecked")
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);
		this.authSql = (String) options.get("authSql");
		this.rolesSql = (String) options.get("rolesSql");
	}

	public boolean login() throws LoginException {

		// this is a protected boolean in Super class
		loginOk = false;
		if (this.callbackHandler == null) {
			throw new LoginException("No callback handler is available");
		}

		try {
			@SuppressWarnings("unused")
			HttpServletRequest request = (HttpServletRequest) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
			System.out.println("Request User :: "+ request.getHeader("HTTP_SM_UID"));
			System.out.println("Request ROle:: "+ request.getHeader("HTTP_SM_ROLES"));
			
		} catch (PolicyContextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Callback callbacks[] = new Callback[2];

		callbacks[0] = new NameCallback("Name :");
		callbacks[1] = new PasswordCallback("Password :", false);

		try {
			this.callbackHandler.handle(callbacks);
			name = ((NameCallback) callbacks[0]).getName().trim();
			password = new String(((PasswordCallback) callbacks[1]).getPassword());

			/*Object[] results = (Object[]) getQueryRunner().query(authSql, new Object[] { name, password },
					new ArrayHandler());

			ssn = ((BigDecimal) results[3]).toString();
			principal = new CustomPrincipal((String) results[0], (String) results[1],
					((BigDecimal) results[2]).toString(), ((BigDecimal) results[3]).toString());*/

			loginOk = true;

		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
			throw new LoginException(ioe.toString());
		} catch (UnsupportedCallbackException ce) {
			ce.printStackTrace();
			throw new LoginException("Error: " + ce.getCallback().toString());
		} /*catch (SQLException ex) {
			ex.printStackTrace();
		}*/
		return false;
	}

	@Override
	protected Principal getIdentity() {
		return this.principal;
	}

	@Override
	protected Group[] getRoleSets() {

		Group roleGroup = new SimpleGroup("Roles");
		Group callerPrincipal = new SimpleGroup("CallerPrincipal");
		Group[] groups = { roleGroup, callerPrincipal };

		/*try {
			Object[] grps = (Object[]) getQueryRunner().query(rolesSql, new Object[] { name }, new ArrayHandler());
			for (int i = 0; i < grps.length; i++) {
				roleGroup.addMember(new SimplePrincipal(((String) grps).trim()));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}*/
		callerPrincipal.addMember(this.principal);
		return groups;
	}

	


}
