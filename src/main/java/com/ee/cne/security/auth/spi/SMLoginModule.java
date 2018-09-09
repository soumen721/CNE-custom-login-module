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

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.AbstractServerLoginModule;



public class SMLoginModule extends AbstractServerLoginModule {

	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		
		super.initialize(subject, callbackHandler, sharedState, options);
	}

	@Override
	protected Principal getIdentity() {
		Principal identity = null;

		try {
			String userid = "guest";
			identity = super.createIdentity(userid);
			

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return identity;

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

	public boolean login() throws LoginException {
		/*
		 * if( getUseFirstPass() == true ) { // Add the principal and password
		 * to the shared state map
		 * sharedState.put("javax.security.auth.login.name", identity);
		 * sharedState.put("javax.security.auth.login.password", credential); }
		 */
		Object a= sharedState.get("javax.security.auth.login.password");
		//String req=CustomAuthenticator.getActiveRespose();
		
		try {
			@SuppressWarnings("unused")
			HttpServletRequest request = (HttpServletRequest) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
			System.out.println("Request User :: "+ request.getHeader("HTTP_SM_UID"));
			System.out.println("Request ROle:: "+ request.getHeader("HTTP_SM_ROLES"));
			
		} catch (PolicyContextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(req);
		super.loginOk = true;
		return true;
	}

}
