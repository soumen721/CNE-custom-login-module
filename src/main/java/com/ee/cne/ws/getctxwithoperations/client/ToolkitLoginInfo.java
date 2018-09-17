package com.ee.cne.ws.getctxwithoperations.client;

import java.util.List;

import lombok.Data;

@Data
public class ToolkitLoginInfo {

	private String Uid;
	
	private String Msisdn;
	
	private List<String> roleList;
	
}
