package com.ee.cne.ws.getctxwithoperations.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import com.ee.cne.util.LoginUtil;

/**
 * @author esonchy
 *
 */
public class ToolkitHeaderInjectHandler implements SOAPHandler<SOAPMessageContext> {
  private static final Logger log = Logger.getLogger(ToolkitHeaderInjectHandler.class);

  /* (non-Javadoc)
   * @see javax.xml.ws.handler.Handler#handleMessage(javax.xml.ws.handler.MessageContext)
   */
  @Override
  public boolean handleMessage(SOAPMessageContext context) {
    final QName _TrackingHeader_QNAME = new QName(
        "http://www.everythingeverywhere.com/common/message/SoapHeader/v1.0", "trackingHeader");

    log.info("Client : handleMessage()......");
    Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

    try {
      SOAPMessage soapMsg = context.getMessage();

      if (isRequest) {
        SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
        SOAPHeader soapHeader = soapEnv.getHeader();
        SOAPBody soapBody = soapEnv.getBody();
        if (soapHeader == null) {
          soapHeader = soapEnv.addHeader();
        }

        SOAPElement trackingHeader = soapHeader.addChildElement(_TrackingHeader_QNAME);

        Node node = (Node) soapBody
            .getElementsByTagNameNS("http://messaging.ei.tmobile.net/datatypes", "requestId")
            .item(0);
        String requestId = node.getChildNodes().item(0).getNodeValue();
        SOAPElement requestIdNode = trackingHeader.addChildElement("requestId");
        requestIdNode.setValue(requestId);

        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        SOAPElement timestampNode = trackingHeader.addChildElement("timestamp");
        timestampNode.setValue(utc.toInstant().toString());

        soapMsg.saveChanges();

        Document xmlDoc = LoginUtil.toXmlDocument(soapMessageToString(soapMsg));
        log.info("Request :: \n" + LoginUtil.prettyPrintXML(xmlDoc));
      } else {
        Document xmlDoc = LoginUtil.toXmlDocument(soapMessageToString(soapMsg));
        log.info("Response :: \n" + LoginUtil.prettyPrintXML(xmlDoc));
      }
    } catch (SOAPException | IOException e) {
      log.error("Exception adding SOAP Header :: " + e.getMessage());
    } catch (Exception e) {
      log.error("Exception adding SOAP Header :: " + e.getMessage());
    }

    return true;

  }

  /* (non-Javadoc)
   * @see javax.xml.ws.handler.Handler#handleFault(javax.xml.ws.handler.MessageContext)
   */
  @Override
  public boolean handleFault(SOAPMessageContext context) {
    log.debug("Client : handleFault()......");
    return true;
  }

  /* (non-Javadoc)
   * @see javax.xml.ws.handler.Handler#close(javax.xml.ws.handler.MessageContext)
   */
  @Override
  public void close(MessageContext context) {
    log.debug("\nClient : close()......");
  }

  @Override
  public Set<QName> getHeaders() {
    log.debug("Client : getHeaders()......");
    return Collections.emptySet();
  }

  /**
   * @param message
   * @return
   * @throws SOAPException
   * @throws IOException
   */
  public String soapMessageToString(SOAPMessage message) throws SOAPException, IOException  {
    String result = null;

    if (message != null) {
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        message.writeTo(baos);
        result = baos.toString();
      } catch (IOException e) {
        log.error("Error in soapMessageToString : " + e);
        throw e;
      }
    }
    return result;
  }
}
