package com.ee.cne.ws.getctxwithoperations.client;

import java.net.URL;
import javax.jws.HandlerChain;
import org.jboss.logging.Logger;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsService;

@HandlerChain(file = "handler-chain.xml")
public class GetContextWithOperationsServiceImpl extends GetContextWithOperationsService {
  private static final Logger log = Logger.getLogger(GetContextWithOperationsServiceImpl.class);
  
  public GetContextWithOperationsServiceImpl(URL wsdlLocation) {
    super(wsdlLocation, SERVICE);
    log.info("In GetContextWithOperationsServiceImpl");
  }

}
