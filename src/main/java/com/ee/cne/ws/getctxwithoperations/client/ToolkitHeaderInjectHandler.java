package com.ee.cne.ws.getctxwithoperations.client;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.jboss.logging.Logger;

import com.ee.cne.util.LoginUtil;

public class ToolkitHeaderInjectHandler implements SOAPHandler<SOAPMessageContext> {
	private static final Logger log = Logger.getLogger(ToolkitHeaderInjectHandler.class);

	@Override
	public boolean handleMessage(SOAPMessageContext context) {

		System.out.println("Client : handleMessage()......");
		Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (isRequest) {

			try {
				SOAPMessage soapMsg = context.getMessage();
				SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
				SOAPHeader soapHeader = soapEnv.getHeader();

				// if no header, add one
				if (soapHeader == null) {
					soapHeader = soapEnv.addHeader();
				}
				/*
				 * TrackingHeader header = new TrackingHeader();
				 * header.setRequestId(correlationId);
				 * header.setTimestamp(LoginUtil.toXMLCalender(LocalDateTime.now(ZoneId.of("UTC"
				 * ))));
				 */

				SOAPElement trackingHeader = soapEnv.addChildElement("trackingHeader", "v1");
				// usernameToken.addAttribute(new QName("xmlns:wsu"),
				// "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

				SOAPBody soapBody = soapEnv.getBody();
				@SuppressWarnings("unchecked")
				Iterator<SOAPBodyElement> elements = soapBody.getChildElements();

				String requestId = null;
				while (elements.hasNext()) {
					SOAPBodyElement element = elements.next();

					@SuppressWarnings("unchecked")
					Iterator<SOAPBodyElement> params = element.getChildElements();

					while (params.hasNext()) {
						SOAPBodyElement param = params.next();

						if ("correlationId".equals(param.getNodeName())) {
							requestId = param.getNodeValue();
						}
					}
				}

				SOAPElement username = trackingHeader.addChildElement("requestId");
				username.addTextNode(requestId);

				SOAPElement password = trackingHeader.addChildElement("timestamp");
				password.addTextNode(LoginUtil.toXMLCalender(LocalDateTime.now(ZoneId.of("UTC"))).toString());

				// Printing Header
				soapMsg.writeTo(System.out);

			} catch (SOAPException | IOException | DatatypeConfigurationException e) {
				log.error("Error ::" + e.getMessage());
			}

		}

		return true;

	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		System.out.println("Client : handleFault()......");
		return true;
	}

	@Override
	public void close(MessageContext context) {
		System.out.println("Client : close()......");
	}

	@Override
	public Set<QName> getHeaders() {
		System.out.println("Client : getHeaders()......");
		return null;
	}

}