package com.ee.cne.ws.getctxwithoperations.client;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
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

public class ToolkitHeaderInjectHandler implements SOAPHandler<SOAPMessageContext> {
	private static final Logger log = Logger.getLogger(ToolkitHeaderInjectHandler.class);

	@Override
	public boolean handleMessage(SOAPMessageContext context) {

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

				QName bodyName = new QName("http://www.everythingeverywhere.com/common/message/SoapHeader/v1.0",
						"trackingHeader");
				SOAPElement trackingHeader = soapHeader.addChildElement(bodyName);

				Node node = (Node) soapBody
						.getElementsByTagNameNS("http://messaging.ei.tmobile.net/datatypes", "requestId").item(0);
				String requestId = node.getChildNodes().item(0).getNodeValue();
				SOAPElement requestIdNode = trackingHeader.addChildElement("requestId");
				requestIdNode.setValue(requestId);

				GregorianCalendar gcal = GregorianCalendar.from(LocalDateTime.now().atZone(ZoneId.systemDefault()));
				XMLGregorianCalendar dateTimeNow = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);

				SOAPElement timestampNode = trackingHeader.addChildElement("timestamp");
				timestampNode.setValue(dateTimeNow.toString());

				soapMsg.saveChanges();
				System.out.println("\n\n************* VALUE :: " + node.getChildNodes().item(0).getNodeValue());

			}

			// Printing Request/Response
			soapMsg.writeTo(System.out);
			System.out.println("\n");
		} catch (SOAPException | IOException | DatatypeConfigurationException e) {
			log.error("Exception adding SOAP Header :: " + e.getMessage());
		}

		return true;

	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		log.debug("Client : handleFault()......");
		return true;
	}

	@Override
	public void close(MessageContext context) {
		log.debug("\nClient : close()......");
	}

	@Override
	public Set<QName> getHeaders() {
		log.debug("Client : getHeaders()......");
		return null;
	}

}