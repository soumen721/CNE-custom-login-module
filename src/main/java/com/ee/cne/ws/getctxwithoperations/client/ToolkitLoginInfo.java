package com.ee.cne.ws.getctxwithoperations.client;

import java.io.Serializable;
import java.util.List;

public class ToolkitLoginInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String Uid;
	private String Msisdn;
	private List<String> roleList;

	public String getUid() {
		return Uid;
	}

	public void setUid(String uid) {
		Uid = uid;
	}

	public String getMsisdn() {
		return Msisdn;
	}

	public void setMsisdn(String msisdn) {
		Msisdn = msisdn;
	}

	public List<String> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}

}
