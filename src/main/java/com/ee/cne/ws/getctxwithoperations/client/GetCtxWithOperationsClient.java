package com.ee.cne.ws.getctxwithoperations.client;

import java.util.Arrays;
import java.util.UUID;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.jboss.logging.Logger;
import com.ee.cne.util.AuthenticationException;
import com.ee.cne.util.LoginUtil;
import com.ee.cne.ws.getctxwithoperations.generated.BusinessFault;
import com.ee.cne.ws.getctxwithoperations.generated.ContextField;
import com.ee.cne.ws.getctxwithoperations.generated.EIMessageContext2;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperations;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsRequest;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsRequest.Message;
import com.ee.cne.ws.getctxwithoperations.generated.GetContextWithOperationsResponse;
import com.ee.cne.ws.getctxwithoperations.generated.ObjectFactory;
import com.ee.cne.ws.getctxwithoperations.generated.TechnicalFault;

/**
 * @author esonchy
 *
 */
public class GetCtxWithOperationsClient {
  private static final Logger log = Logger.getLogger(GetCtxWithOperationsClient.class);

  private GetCtxWithOperationsClient() {

  }

  /**
   * @param contextKeyParamName
   * @return
   * @throws AuthenticationException
   */
  public static ToolkitLoginInfo fetchToolkitAuthenticationDetails(String contextKeyParamName)
      throws AuthenticationException {

    ToolkitLoginInfo toolkitLoginInfo = null;
    try {
      String correlationId = UUID.randomUUID().toString().toUpperCase();
      GetContextWithOperationsResponse serviceResponse = null;
      GetContextWithOperationsRequest serviceRequest =
          new ObjectFactory().createGetContextWithOperationsRequest();

      // Set the request parameters
      Message message = new Message();
      message.setContextToken(contextKeyParamName);
      serviceRequest.setMessage(message);

      EIMessageContext2 messageContext = new EIMessageContext2();
      messageContext.setCorrelationId(correlationId);
      messageContext.setRequestId(correlationId);
      messageContext.setSender(LoginUtil.TOOLKIT_SENDER_NAME);
      serviceRequest.setEiMessageContext2(messageContext);

      String toolkitURL = LoginUtil.getProperties().getProperty(LoginUtil.TOOLKIT_WS_URL);
      log.info("WSDL URL :: " + toolkitURL);

      JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
      proxyFactory.setHandlers(Arrays.asList(new ToolkitHeaderInjectHandler()));
      ClientFactoryBean clientBean = proxyFactory.getClientFactoryBean();
      clientBean.setAddress(toolkitURL);
      clientBean.setServiceClass(GetContextWithOperations.class);
      GetContextWithOperations client = (GetContextWithOperations) proxyFactory.create();
      serviceResponse = client.getContextWithOperations(serviceRequest);

      if (serviceResponse != null && serviceResponse.getMessage() != null) {
        toolkitLoginInfo = new ToolkitLoginInfo();

        if (serviceResponse.getMessage().getContextFields() != null
            && serviceResponse.getMessage().getContextFields().getContextField() != null) {
          final ContextField uId =
              serviceResponse.getMessage().getContextFields().getContextField().stream()
                  .filter(e -> "user.username".equals(e.getFieldName())).findFirst().orElse(null);
          final ContextField msisdn =
              serviceResponse.getMessage().getContextFields().getContextField().stream()
                  .filter(e -> "customer.customerDetails.msisdn".equals(e.getFieldName()))
                  .findFirst().orElse(null);
          toolkitLoginInfo.setuId(uId != null ? uId.getFieldValue() : null);
          toolkitLoginInfo.setMsisdn(msisdn != null ? msisdn.getFieldValue() : null);
        }

        if (serviceResponse.getMessage() != null
            && serviceResponse.getMessage().getOperations() != null) {

          toolkitLoginInfo.setRoleList(serviceResponse.getMessage().getOperations().getOperation());
        }
      } else {
        throw new BusinessFault("No valid Response Found");
      }
    } catch (BusinessFault | TechnicalFault exc) {

      log.error("An error occured while calling service getGetContextWithOperations", exc);
      throw new AuthenticationException(exc);
    } catch (Exception exc) {
      log.error("An error occured while calling service getGetContextWithOperations", exc);
      throw new AuthenticationException("Generic Exception", exc);
    }

    return toolkitLoginInfo;
  }

}
