package com.ee.cne.security.auth.spi;

import static com.ee.cne.util.LoginUtil.HTTP_TK_UID;
import static com.ee.cne.util.LoginUtil.HTTP_TK_ROLES;
import static com.ee.cne.util.LoginUtil.HTTP_TK_MSISDN;
import java.security.Principal;
import java.security.acl.Group;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;
import org.jboss.logging.Logger;
import org.jboss.security.auth.spi.AbstractServerLoginModule;
import com.ee.cne.util.LoginTypeEnum;
import com.ee.cne.util.LoginUtil;

/**
 * @author esonchy
 *
 */
public class ToolkitLoginModule extends AbstractServerLoginModule {

  private static final Logger logger = Logger.getLogger(ToolkitLoginModule.class);

  private static final String HEADER_USER_NAME = "headerUserName";
  private static final String HEADER_ROLE = "headerRole";
  private static final String[] ALL_VALID_OPTIONS = {HEADER_USER_NAME, HEADER_ROLE};

  private Principal principal;
  private String userName = null;
  private List<String> userRoles = null;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jboss.security.auth.spi.AbstractServerLoginModule#initialize(javax.security.auth.Subject,
   * javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
   */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState,
      Map options) {
    addValidOptions(ALL_VALID_OPTIONS);

    super.initialize(subject, callbackHandler, sharedState, options);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jboss.security.auth.spi.AbstractServerLoginModule#login()
   */
  @Override
  public boolean login() throws LoginException {
    logger.info("Inside ToolkitLoginModule >> login");
    super.loginOk = false;

    try {
      HttpServletRequest request =
          (HttpServletRequest) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");

      if (LoginTypeEnum.TOOLKIT_LOGIN != LoginTypeEnum
          .valueOf(request.getAttribute("LOGIN_TYPE").toString())) {
        return false;
      }

      String roles = request.getAttribute(HTTP_TK_ROLES) != null
          ? request.getAttribute(HTTP_TK_ROLES).toString()
          : null;

      this.userName = request.getAttribute(HTTP_TK_UID).toString();
      this.userRoles = LoginUtil.getValidRoles(LoginTypeEnum.TOOLKIT_LOGIN, roles);
      
      final String MSISDN = request.getAttribute(HTTP_TK_MSISDN) != null
          ? request.getAttribute(HTTP_TK_MSISDN).toString()
          : null;

      logger.info("Inside ToolkitModule Request User : " + userName + "\t|Request Role : "
          + userRoles + "\t|Request MSISDN : " + MSISDN);

      if ((userName != null && !"".equals(userName.trim()))
          && (userRoles != null && !userRoles.isEmpty())) {

        super.loginOk = true;
        return true;
      }

    } catch (PolicyContextException e) {
      logger.info("In Exception Inside ToolkitLoginModule -login method--> PolicyContextException"
          + e.getMessage());
    }

    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jboss.security.auth.spi.AbstractServerLoginModule#getIdentity()
   */
  @Override
  protected Principal getIdentity() {
    try {
      principal = super.createIdentity(userName);
    } catch (Exception e) {
      logger.error("In getIdentity , Exception : " + e);
    }

    return principal;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jboss.security.auth.spi.AbstractServerLoginModule#getRoleSets()
   */
  @Override
  protected Group[] getRoleSets() {

    return LoginUtil.getGroups(this.principal, userRoles);
  }

  @Override
  public boolean logout() throws LoginException {
    super.logout();
    return true;
  }
}
