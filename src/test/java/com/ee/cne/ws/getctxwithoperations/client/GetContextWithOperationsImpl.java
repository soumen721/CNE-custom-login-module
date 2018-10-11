package com.ee.cne.ws.getctxwithoperations.client;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.jws.WebService;

import com.ee.cne.ws.getctxwithoperations.generated.BusinessFault;
import com.ee.cne.ws.getctxwithoperations.generated.ContextField;
import com.ee.cne.ws.getctxwithoperations.generated.ContextFields;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperations;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsRequest;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsResponse;
import com.ee.cne.ws.getctxwithoperations.generated.Operations;
import com.ee.cne.ws.getctxwithoperations.generated.TechnicalFault;

@WebService(endpointInterface = "com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperations", targetNamespace = "http://www.everythingeverywhere.com/crm/customer/service/GetContextWithOperations/v1.0", name = "GetContextWithOperationsSoapBinding", portName = "GetContextWithOperations_1.0", serviceName = "getContextWithOperationsService")
public class GetContextWithOperationsImpl implements GetContextWithOperations {

	@Override
	public GetContextWithOperationsResponse getContextWithOperations(GetContextWithOperationsRequest parameters)
			throws BusinessFault, TechnicalFault {

		System.out.println("In Service::::::");
		return GetContextWithOperationsImpl.populateDummyResponse();
	}

	private static GetContextWithOperationsResponse populateDummyResponse() {

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

	private static int generateRandomIntIntRange(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
}
