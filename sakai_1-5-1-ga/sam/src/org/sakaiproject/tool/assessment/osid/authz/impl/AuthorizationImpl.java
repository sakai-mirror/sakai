package org.sakaiproject.tool.assessment.osid.authz.impl;

import org.osid.authorization.Authorization;
import org.osid.authorization.AuthorizationException;
import org.osid.shared.Id;
import org.osid.authorization.Function;
import org.osid.authorization.Qualifier;
import java.util.Date;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class AuthorizationImpl implements Authorization {
  private Id agentId;
  private Function function;
  private Qualifier qualifier;
  private long effectiveDate;
  private long expirationDate;
  private Id modifiedBy;
  private long modifiedDate;
  private boolean isExplicit;
  private boolean isActiveNow;

  public AuthorizationImpl() {
  }

  public Id getAgentId() {
    return null;
  }

  public Function getFunction() {
    return null;
  }

  public Qualifier getQualifier() {
    return null;
  }

  public long getEffectiveDate() {
    return 0L;
  }

  public long getExpirationDate() {
    return 0L;
  }

  public Id getModifiedBy() {
    return null;
  }

  public long getModifiedDate() {
    return 0L;
  }


  public boolean isExplicit() {
    return false;
  }

  public void updateExpirationDate(long long0) {
  }

  public void updateEffectiveDate(long long0) {
  }

  public boolean isActiveNow() {
    int effectiveVal = (getEffectiveDate() == 0) ? 0 : 1;
    int expirationVal = (getExpirationDate() == 0) ? 0 : 2;

    // current time in ms
    long nowMillis = (new Date()).getTime();
    boolean returnVal = false;

    switch(effectiveVal + expirationVal)
    {
      case 0: // both are 0
        returnVal = true;
        break;

      case 1: // effectiveDate is not 0
        if(nowMillis > getEffectiveDate())
          returnVal = true;
        else
          returnVal = false;

        break;
      case 2: // expirationDate is not null
        if(nowMillis < getExpirationDate())
          returnVal = true;
        else
          returnVal = false;

        break;

      case 3: // both effectiveDate and expirationDate are not null
        if((nowMillis > getEffectiveDate()) && (nowMillis < getExpirationDate()))
          returnVal = true;
        else
          returnVal = false;
    }
    return returnVal;
  }
}