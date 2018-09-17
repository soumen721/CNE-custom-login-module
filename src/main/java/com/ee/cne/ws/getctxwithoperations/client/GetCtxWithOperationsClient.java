package com.ee.cne.ws.getctxwithoperations.client;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.UUID;

import org.apache.catalina.connector.Request;

import com.ee.cne.ws.getctxwithoperations.generated.BusinessFault;
import com.ee.cne.ws.getctxwithoperations.generated.ContextField;
import com.ee.cne.ws.getctxwithoperations.generated.ContextFields;
import com.ee.cne.ws.getctxwithoperations.generated.EIMessageContext2;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsRequest;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsRequest.Message;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsResponse;
import com.ee.cne.ws.getctxwithoperations.generated.ObjectFactory;
import com.ee.cne.ws.getctxwithoperations.generated.Operations;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetCtxWithOperationsClient {
	
	// TODO remove this method one actual web service implentation is ready
	public static ToolkitLoginInfo fetchToolkitAuthenticationDetails(Request request) throws MalformedURLException {

		// call web-service
		ToolkitLoginInfo toolkitLoginInfo = null;
		String correlationId = UUID.randomUUID().toString();
		GetContextWithOperationsResponse serviceResponse = null;
		GetContextWithOperationsRequest serviceRequest = new ObjectFactory().createGetContextWithOperationsRequest();

		// Set the request parameters
		Message message = new Message();
		message.setContextToken("ABCDXYZ");
		serviceRequest.setMessage(message);

		EIMessageContext2 messageContext = new EIMessageContext2();
		messageContext.setCorrelationId(correlationId);
		messageContext.setRequestId(correlationId);
		messageContext.setSender("EEA-JBOSS");

		try {

			serviceResponse = populateDummyResponse();
			/*
			 * URL wsdlURL = new URL(""); GetContextWithOperationsService service = new
			 * GetContextWithOperationsService(wsdlURL); GetContextWithOperations ctxport =
			 * service.getGetContextWithOperations10(); serviceResponse =
			 * ctxport.getContextWithOperations(serviceRequest);
			 */

			if (serviceResponse != null && serviceResponse.getMessage() != null) {
				toolkitLoginInfo = new ToolkitLoginInfo();

				if (serviceResponse.getMessage().getContextFields() != null
						&& serviceResponse.getMessage().getContextFields().getContextField() != null) {

					ContextField uId = serviceResponse.getMessage().getContextFields().getContextField().stream()
					.filter(e->"user.username".equals(e.getFieldName())).findFirst().orElseGet(null);
					
					ContextField msisdn = serviceResponse.getMessage().getContextFields().getContextField().stream()
							.filter(e->"customer.customerDetails.msisdn".equals(e.getFieldName())).findFirst().orElseGet(null);
						
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

		} catch (BusinessFault bex) {

			log.error("An error occured while calling service getGetContextWithOperations", bex);
		} /*
			 * catch (TechnicalFault tex) {
			 * 
			 * // logr.error("An error occured while calling service //
			 * getGetContextWithOperations", tex); }
			 */
		return toolkitLoginInfo;

	}

	public static GetContextWithOperationsResponse populateDummyResponse() {

		GetContextWithOperationsResponse response = new GetContextWithOperationsResponse();
		com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsResponse.Message message = new com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsResponse.Message();

		ContextFields fields = new ContextFields();
		ContextField field1 = new ContextField();
		field1.setFieldName("customer.customerDetails.msisdn");
		field1.setFieldValue("ABC1232XYZ");
		fields.getContextField().add(field1);
		ContextField field2 = new ContextField();
		fields.getContextField().add(field2);
		field2.setFieldName("user.username");
		field2.setFieldValue("ccluser");
		message.setContextFields(fields);

		Operations operations = new Operations();
		operations.getOperation().addAll(Arrays.asList("cc-user", "ccl-user"));

		message.setOperations(operations);
		response.setMessage(message);

		return response;
	}
}