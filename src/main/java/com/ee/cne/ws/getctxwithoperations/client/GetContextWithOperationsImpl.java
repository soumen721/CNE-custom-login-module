package com.ee.cne.ws.getctxwithoperations.client;

import javax.jws.WebService;

import com.ee.cne.ws.getctxwithoperations.generated.BusinessFault;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperations;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsRequest;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsResponse;
import com.ee.cne.ws.getctxwithoperations.generated.TechnicalFault;


@WebService(endpointInterface = "com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperations" ,
targetNamespace = "http://www.everythingeverywhere.com/crm/customer/service/GetContextWithOperations/v1.0", 
name = "GetContextWithOperationsSoapBinding",portName = "GetContextWithOperations_1.0", 
serviceName = "getContextWithOperationsService"
)
public class GetContextWithOperationsImpl implements GetContextWithOperations {

	@Override
	public GetContextWithOperationsResponse getContextWithOperations(GetContextWithOperationsRequest parameters)
			throws BusinessFault, TechnicalFault {

		System.out.println("In Service::::::");
		return GetCtxWithOperationsClient.populateDummyResponse();
	}

}
