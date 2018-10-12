package com.ee.cne.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jboss.logging.Logger;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
      log.debug("Redirect URL:: " + prop.getProperty(TOOLKIT_REDIRECT_URL));
    } catch (IOException ex) {
      log.error("Exception in retriving value from property file ::" + ex.getMessage());
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

  public static XMLGregorianCalendar toXMLCalender(LocalDateTime date)
      throws DatatypeConfigurationException {

    GregorianCalendar gcal = GregorianCalendar.from(date.atZone(ZoneId.systemDefault()));
    return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
  }

  public static Group[] getGroups(Principal principal, final String roles) {

    Group roleGroup = new SimpleGroup("Roles");
    Group callerPrincipal = new SimpleGroup("CallerPrincipal");
    Group[] groups = {roleGroup, callerPrincipal};

    Arrays.asList(roles.split(",")).stream().filter(e -> !Objects.isNull(e)).forEach(ar -> {
      roleGroup.addMember(new SimplePrincipal(ar));
    });

    callerPrincipal.addMember(principal);

    return groups;
  }

  public static final String prettyPrintXML(Document xml) throws Exception {
    Transformer tf = TransformerFactory.newInstance().newTransformer();
    tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    tf.setOutputProperty(OutputKeys.INDENT, "yes");
    Writer out = new StringWriter();
    tf.transform(new DOMSource(xml), new StreamResult(out));

    return out.toString();
  }

  public static Document toXmlDocument(String str)
      throws ParserConfigurationException, SAXException, IOException {

    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    Document document = docBuilder.parse(new InputSource(new StringReader(str)));

    return document;
  }

  public static String soapMessageToString(SOAPMessage message) throws Exception {
    String result = null;

    if (message != null) {
      ByteArrayOutputStream baos = null;
      try {
        baos = new ByteArrayOutputStream();
        message.writeTo(baos);
        result = baos.toString();
      } catch (IOException e) {
        throw e;
      } finally {
        if (baos != null) {
          try {
            baos.close();
          } catch (IOException ioe) {
          }
        }
      }
    }
    return result;
  }
}
