package com.ee.cne.ws.getctxwithoperations.client;

import java.io.Serializable;
import java.util.List;

public class ToolkitLoginInfo implements Serializable {
  private static final long serialVersionUID = 1L;

  private String uId;
  private String msisdn;
  private List<String> roleList;
  
  public String getuId() {
    return uId;
  }
  public void setuId(String uId) {
    this.uId = uId;
  }
  public String getMsisdn() {
    return msisdn;
  }
  public void setMsisdn(String msisdn) {
    this.msisdn = msisdn;
  }
  public List<String> getRoleList() {
    return roleList;
  }
  public void setRoleList(List<String> roleList) {
    this.roleList = roleList;
  }

  
}
