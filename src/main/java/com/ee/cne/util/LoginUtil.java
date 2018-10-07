package com.ee.cne.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.security.acl.Group;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.Properties;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jboss.logging.Logger;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;

public class LoginUtil {
	private static final Logger log = Logger.getLogger(LoginUtil.class);
	public static final String TOOLKIT_REDIRECT_URL = "toolkit.redirect.url";
	public static final String TOOLKIT_WS_URL = "toolkit.ws.url";
	
	public static Properties getProperties() {
		Properties prop = new Properties();
		InputStream input = null;
		try {

			input = LoginUtil.class.getClassLoader().getResourceAsStream("config.properties");
			prop.load(input);
			// get the property value and print it out
			log.debug("Redirect URL:: "+ prop.getProperty(TOOLKIT_REDIRECT_URL));
		} catch (IOException ex) {
			log.error("Exception in retriving value from property file ::"+ ex.getMessage());
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}
	
	public static XMLGregorianCalendar toXMLCalender(LocalDateTime date) throws DatatypeConfigurationException {

        GregorianCalendar gcal = GregorianCalendar.from(date.atZone(ZoneId.systemDefault()));
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
    }

	
	public static Group[] getGroups(Principal principal ,final String roles) {
		
		Group roleGroup = new SimpleGroup("Roles");
		Group callerPrincipal = new SimpleGroup("CallerPrincipal");
		Group[] groups = { roleGroup, callerPrincipal };

		Arrays.asList(roles.split(",")).stream().filter(e -> !Objects.isNull(e)).forEach(ar -> {
			roleGroup.addMember(new SimplePrincipal(ar));
		});

		callerPrincipal.addMember(principal);
		
		return groups;
	}

}
