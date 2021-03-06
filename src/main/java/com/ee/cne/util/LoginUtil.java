package com.ee.cne.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.Principal;
import java.security.acl.Group;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jboss.logging.Logger;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author esonchy
 *
 */
public class LoginUtil {

  private static final Logger log = Logger.getLogger(LoginUtil.class);
  public static final String TOOLKIT_REDIRECT_URL = "toolkit.redirect.url";
  public static final String TOOLKIT_WS_URL = "toolkit.ws.url";
  public static final String TOOLKIT_SENDER_NAME = "eea";
  public static final String SM_USER_ROLES = "sm.user.roles";
  public static final String TOOLKIT_USER_ROLES = "toolkit.user.roles";
  public static final String ROLES_SEPARATOR = "^";
  public static final String HTTP_SM_UID = "SM_UID";
  public static final String HTTP_SM_ROLES = "SM_ROLES";
  public static final String HTTP_TK_UID = "HTTP_TK_UID";
  public static final String HTTP_TK_ROLES = "HTTP_TK_ROLES";
  public static final String HTTP_TK_MSISDN = "HTTP_TK_MSISDN";
  private static Properties properties;

  private LoginUtil() {

  }

  /**
   * @return
   */
  public static synchronized Properties getProperties() {
    try {
      if (properties != null) {
        log.info("Config serve form previously loaded instance");
        return properties;
      }
      properties = new Properties();
      properties.load(LoginUtil.class.getClassLoader().getResourceAsStream("config.properties"));
      log.info("Redirect URL:: " + properties.getProperty(TOOLKIT_REDIRECT_URL));
      log.info("Config File loaded successfully");
    } catch (IOException ex) {
      log.error("Exception in retriving value from property file ::" + ex);
    }
    return properties;
  }

  /**
   * @return
   */
  public static List<String> getSMRoles() {
    String roles = getProperties().getProperty(SM_USER_ROLES);
    return roles != null ? Arrays.asList(roles.split(",")) : Collections.emptyList();
  }

  /**
   * @return
   */
  public static List<String> getToolkitRoles() {
    String roles = getProperties().getProperty(TOOLKIT_USER_ROLES);
    return roles != null ? Arrays.asList(roles.split(",")) : Collections.emptyList();
  }

  /**
   * @param date
   * @return
   * @throws DatatypeConfigurationException
   */
  public static XMLGregorianCalendar toXMLCalender(LocalDateTime date)
      throws DatatypeConfigurationException {

    GregorianCalendar gcal = GregorianCalendar.from(date.atZone(ZoneId.systemDefault()));
    return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
  }

  /**
   * @param principal
   * @param roles
   * @return
   */
  public static Group[] getGroups(Principal principal, final List<String> roles) {

    Group roleGroup = new SimpleGroup("Roles");
    Group callerPrincipal = new SimpleGroup("CallerPrincipal");
    Group[] groups = {roleGroup, callerPrincipal};

    roles.stream().filter(e -> !Objects.isNull(e))
        .forEach(ar -> roleGroup.addMember(new SimplePrincipal(ar)));

    callerPrincipal.addMember(principal);

    return groups;
  }

  /**
   * @param xml
   * @return
   * @throws TransformerException
   */
  public static final String prettyPrintXML(Document xml) throws TransformerException {

    TransformerFactory factory = TransformerFactory.newInstance();
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    Transformer tf = factory.newTransformer();
    tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    tf.setOutputProperty(OutputKeys.INDENT, "yes");
    Writer out = new StringWriter();
    tf.transform(new DOMSource(xml), new StreamResult(out));

    return out.toString();
  }

  /**
   * @param str
   * @return
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  public static Document toXmlDocument(String str)
      throws ParserConfigurationException, SAXException, IOException {

    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

    return docBuilder.parse(new InputSource(new StringReader(str)));
  }

  /**
   * @param message
   * @return
   * @throws SOAPException
   * @throws IOException
   */
  public static String soapMessageToString(SOAPMessage message) throws SOAPException, IOException {
    String result = null;

    if (message != null) {
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        message.writeTo(baos);
        result = baos.toString();
      } catch (IOException e) {
        log.error("In soapMessageToString Exception: " + e);
        throw e;
      }
    }
    return result;
  }

  public static List<String> getValidRoles(LoginTypeEnum loginType, String roles) {
    List<String> validRoles = Collections.emptyList();
    if (LoginTypeEnum.SM_LOGIN == loginType) {
      validRoles = getSMRoles();
    } else if (LoginTypeEnum.TOOLKIT_LOGIN == loginType) {
      validRoles = getToolkitRoles();
    }
    final List<String> rolesValid = validRoles;


    return roles != null ? Arrays.asList(roles.split("\\"+ROLES_SEPARATOR)).stream()
        .filter(rolesValid::contains).collect(Collectors.toList()) : Collections.emptyList();
  }
}
