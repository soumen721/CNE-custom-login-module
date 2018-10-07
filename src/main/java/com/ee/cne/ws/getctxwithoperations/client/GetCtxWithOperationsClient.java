package com.ee.cne.ws.getctxwithoperations.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.jboss.logging.Logger;

import com.ee.cne.util.LoginUtil;
import com.ee.cne.ws.getctxwithoperations.generated.BusinessFault;
import com.ee.cne.ws.getctxwithoperations.generated.ContextField;
import com.ee.cne.ws.getctxwithoperations.generated.ContextFields;
import com.ee.cne.ws.getctxwithoperations.generated.EIMessageContext2;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperations;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsRequest;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsRequest.Message;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsResponse;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsService;
import com.ee.cne.ws.getctxwithoperations.generated.ObjectFactory;
import com.ee.cne.ws.getctxwithoperations.generated.Operations;
import com.ee.cne.ws.getctxwithoperations.generated.TechnicalFault;

public class GetCtxWithOperationsClient {
	private static final Logger log = Logger.getLogger(GetCtxWithOperationsClient.class);

	// TODO remove this method one actual web service implementation is ready
	public static ToolkitLoginInfo fetchToolkitAuthenticationDetails(String contextKeyParamName)
			throws MalformedURLException {

		// call web-service
		ToolkitLoginInfo toolkitLoginInfo = null;
		String correlationId = UUID.randomUUID().toString();
		GetContextWithOperationsResponse serviceResponse = null;
		GetContextWithOperationsRequest serviceRequest = new ObjectFactory().createGetContextWithOperationsRequest();

		// Set the request parameters
		Message message = new Message();
		message.setContextToken(contextKeyParamName);
		serviceRequest.setMessage(message);

		EIMessageContext2 messageContext = new EIMessageContext2();
		messageContext.setCorrelationId(correlationId);
		messageContext.setRequestId(correlationId);
		messageContext.setSender("EEA-JBOSS"); // TODO need actual sender name

		try {

			serviceResponse = populateDummyResponse();

			URL wsdlURL = new URL(LoginUtil.getProperties().getProperty(LoginUtil.TOOLKIT_WS_URL));
			log.info("WSDL URL :: "+ wsdlURL.getHost()+"/"+ wsdlURL.getPath());
			GetContextWithOperationsService service = new GetContextWithOperationsService(wsdlURL);
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

		} catch (BusinessFault bex) {

			log.error("An error occured while calling service getGetContextWithOperations", bex);
		} /*
			 * catch (TechnicalFault tex) {
			 * 
			 * // logr.error("An error occured while calling service //
			 * getGetContextWithOperations", tex); }
			 */ catch (TechnicalFault e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return toolkitLoginInfo;

	}

	public static GetContextWithOperationsResponse populateDummyResponse() {

		GetContextWithOperationsResponse response = new GetContextWithOperationsResponse();
		com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsResponse.Message message = new com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsResponse.Message();

		ContextFields fields = new ContextFields();
		ContextField field1 = new ContextField();
		field1.setFieldName("customer.customerDetails.msisdn");
		field1.setFieldValue(UUID.randomUUID().toString().toUpperCase());
		fields.getContextField().add(field1);
		ContextField field2 = new ContextField();
		fields.getContextField().add(field2);
		field2.setFieldName("user.username");
		field2.setFieldValue("ccluser" + generateRandomIntIntRange(1, 5));
		message.setContextFields(fields);

		List<String> list = Arrays.asList("cc-user", "ccl-user", "soc-user");

		Operations operations = new Operations();
		operations.getOperation().add(list.get(generateRandomIntIntRange(0, 2)).toString());

		message.setOperations(operations);
		response.setMessage(message);

		return response;
	}

	public static int generateRandomIntIntRange(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
}
