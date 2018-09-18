package com.ee.cne.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jboss.logging.Logger;

public class PropertyReaderUtil {
	private static final Logger log = Logger.getLogger(PropertyReaderUtil.class);
	
	public static Properties getProperties() {
		Properties prop = new Properties();
		InputStream input = null;
		try {

			input = PropertyReaderUtil.class.getClassLoader().getResourceAsStream("config.properties");
			prop.load(input);
			// get the property value and print it out
			log.debug("Redirect URL:: "+ prop.getProperty("redirect.utl"));

		} catch (IOException ex) {

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

}
