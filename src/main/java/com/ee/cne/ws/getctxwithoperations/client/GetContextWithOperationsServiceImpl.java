package com.ee.cne.ws.getctxwithoperations.client;

import java.net.URL;

import javax.jws.HandlerChain;

import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsService;

@HandlerChain(file = "handler-chain.xml")
public class GetContextWithOperationsServiceImpl extends GetContextWithOperationsService {

	public GetContextWithOperationsServiceImpl(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
        System.out.println("In GetContextWithOperationsServiceImpl");
    }

}
