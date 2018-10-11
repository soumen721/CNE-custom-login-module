package com.ee.cne.ws.getctxwithoperations.client;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import org.jboss.logging.Logger;

import com.ee.cne.util.AuthenticationException;
import com.ee.cne.util.LoginUtil;
import com.ee.cne.ws.getctxwithoperations.generated.BusinessFault;
import com.ee.cne.ws.getctxwithoperations.generated.ContextField;
import com.ee.cne.ws.getctxwithoperations.generated.EIMessageContext2;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperations;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsRequest;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsRequest.Message;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsResponse;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsService;
import com.ee.cne.ws.getctxwithoperations.generated.ObjectFactory;
import com.ee.cne.ws.getctxwithoperations.generated.TechnicalFault;

public class GetCtxWithOperationsClient {
	private static final Logger log = Logger.getLogger(GetCtxWithOperationsClient.class);

	public static ToolkitLoginInfo fetchToolkitAuthenticationDetails(String contextKeyParamName) throws AuthenticationException {

		ToolkitLoginInfo toolkitLoginInfo = null;
		try {
			String correlationId = UUID.randomUUID().toString().toUpperCase();
			GetContextWithOperationsResponse serviceResponse = null;
			GetContextWithOperationsRequest serviceRequest = new ObjectFactory()
					.createGetContextWithOperationsRequest();

			// Set the request parameters
			Message message = new Message();
			message.setContextToken(contextKeyParamName);
			serviceRequest.setMessage(message);

			EIMessageContext2 messageContext = new EIMessageContext2();
			messageContext.setCorrelationId(correlationId);
			messageContext.setRequestId(correlationId);
			messageContext.setSender("EEA-JBOSS");
			serviceRequest.setEiMessageContext2(messageContext);

			URL wsdlURL = new URL(LoginUtil.getProperties().getProperty(LoginUtil.TOOLKIT_WS_URL));
			log.info("WSDL URL :: " + wsdlURL.toURI().toString());
			GetContextWithOperationsService service = new GetContextWithOperationsServiceImpl(wsdlURL);
			GetContextWithOperations ctxport = service.getGetContextWithOperations10();
			serviceResponse = ctxport.getContextWithOperations(serviceRequest);

			if (serviceResponse != null && serviceResponse.getMessage() != null) {
				toolkitLoginInfo = new ToolkitLoginInfo();

				if (serviceResponse.getMessage().getContextFields() != null
						&& serviceResponse.getMessage().getContextFields().getContextField() != null) {

					ContextField uId = serviceResponse.getMessage().getContextFields().getContextField().stream()
							.filter(e -> "user.username".equals(e.getFieldName())).findFirst().orElseGet(null);

					ContextField msisdn = serviceResponse.getMessage().getContextFields().getContextField().stream()
							.filter(e -> "customer.customerDetails.msisdn".equals(e.getFieldName())).findFirst()
							.orElseGet(null);

					toolkitLoginInfo.setUid(uId.getFieldValue());
					toolkitLoginInfo.setMsisdn(msisdn.getFieldValue());
				}

				if (serviceResponse.getMessage().getContextFields() != null
						&& serviceResponse.getMessage().getContextFields().getContextField() != null) {

					toolkitLoginInfo.setRoleList(serviceResponse.getMessage().getOperations().getOperation());
				}
			} else {
				throw new BusinessFault("No valid Response Found");
			}

		} catch (BusinessFault | TechnicalFault | URISyntaxException exc) {

			log.error("An error occured while calling service getGetContextWithOperations", exc);
			throw new AuthenticationException(exc);
		} catch (Exception exc) {
			log.error("An error occured while calling service getGetContextWithOperations", exc);
			throw new AuthenticationException("Generic Exception", exc);
		}

		return toolkitLoginInfo;
	}

}
