package com.ee.cne.ws.getctxwithoperations.client;

import javax.xml.ws.Endpoint;


//Endpoint publisher
public class PublishService {

	public static void main(String[] args) {
		Endpoint.publish("http://localhost:8081/service/getContextWithOperations-1_0", new GetContextWithOperationsImpl());
		System.out.println("Published ...........");
	}

}