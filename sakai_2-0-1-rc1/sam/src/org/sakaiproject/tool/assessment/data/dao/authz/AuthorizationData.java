/**********************************************************************************
* $HeadURL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2004-2005 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/
package org.sakaiproject.tool.assessment.data.dao.authz;

import org.sakaiproject.tool.assessment.data.ifc.authz.AuthorizationIfc;
import java.util.Date;
import java.io.Serializable;

public class AuthorizationData implements AuthorizationIfc, Serializable
{
  private String agentId;
  private String functionId;
  private String qualifierId;
  private Date effectiveDate;
  private Date expirationDate;
  private Date lastModifiedDate;
  private String lastModifiedBy;
  private Boolean isExplicit;

  private Long surrogateKey;
  private Integer lockId;


  /**
   * Creates a new AuthorizationBean object.
   */
  public AuthorizationData()
  {
  }

  /**
   * Creates a new AuthorizationBean object.
   *
   * @param pk DOCUMENTATION PENDING
   * @param effectiveDate DOCUMENTATION PENDING
   * @param expirationDate DOCUMENTATION PENDING
   * @param modifierId DOCUMENTATION PENDING
   * @param modifiedDate DOCUMENTATION PENDING
   * @param isExplicit DOCUMENTATION PENDING
   */
  public AuthorizationData(
    String agentId, String functionId, String qualifierId,
    Date effectiveDate, Date expirationDate,
    String lastModifiedBy, Date lastModifiedDate, Boolean isExplicit)
  {
    this.agentId = agentId;
    this.functionId = functionId;
    this.qualifierId = qualifierId;
    this.effectiveDate = effectiveDate;
    this.expirationDate = expirationDate;
    this.lastModifiedBy = lastModifiedBy;
    this.lastModifiedDate = lastModifiedDate;
    this.isExplicit = isExplicit;
  }

  public String getAgentIdString()
  {
    return this.agentId;
  }

  public void setAgentIdString(String id)
  {
    this.agentId = id;
  }

  public String getFunctionId()
  {
    return this.functionId;
  }

  public void setFunctionId(String id)
  {
    this.functionId = id;
  }

  public String getQualifierId()
  {
    return this.qualifierId;
  }

  public void setQualifierId(String id)
  {
    this.qualifierId = id;
  }

  public Date getAuthorizationEffectiveDate()
  {
    return this.effectiveDate;
  }

  public void setAuthorizationEffectiveDate(Date cal)
  {
    this.effectiveDate = cal;
  }

  public Date getAuthorizationExpirationDate()
  {
    return this.expirationDate;
  }

  public void setAuthorizationExpirationDate(Date cal)
  {
    this.expirationDate = cal;
  }

  public String getLastModifiedBy()
  {
    return this.lastModifiedBy;
  }

  public void setLastModifiedBy(String id)
  {
    this.lastModifiedBy = id;
  }

  public Date getLastModifiedDate()
  {
    return this.lastModifiedDate;
  }

  public void setLastModifiedDate(Date cal)
  {
    this.lastModifiedDate = cal;
  }

  public Boolean getIsExplicitBoolean()
  {
    return this.isExplicit;
  }

  public void setIsExplicitBoolean(Boolean type)
  {
    this.isExplicit = type;
  }

  public Boolean getIsActiveNowBoolean() {
    int effectiveVal = (getAuthorizationEffectiveDate() == null) ? 0 : 1;
    int expirationVal = (getAuthorizationExpirationDate() == null) ? 0 : 2;

    long nowMillis = (new Date()).getTime();
    boolean returnVal = false;

    switch(effectiveVal + expirationVal)
    {
      case 0: // both are null
        returnVal = true;
        break;

      case 1: // effectiveDate is not null
        if(nowMillis > getAuthorizationEffectiveDate().getTime())
          returnVal = true;
        else
          returnVal = false;
        break;

      case 2: // expirationDate is not null
        if(nowMillis < getAuthorizationExpirationDate().getTime())
          returnVal = true;
        else
          returnVal = false;
        break;

      case 3: // both effectiveDate and expirationDate are not null
        if((nowMillis > getAuthorizationEffectiveDate().getTime()) &&
            (nowMillis < getAuthorizationExpirationDate().getTime()))
          returnVal = true;
        else
          returnVal = false;
        break;
    }
    return new Boolean(returnVal);
  }

  /**
   * @return Returns the lockId.
   */
  public final Integer getLockId()
  {
    return lockId;
  }
  /**
   * @param lockId The lockId to set.
   */
  public final void setLockId(Integer lockId)
  {
    this.lockId = lockId;
  }
  /**
   * @return Returns the surrogateKey.
   */
  public final Long getSurrogateKey()
  {
    return surrogateKey;
  }
  /**
   * @param surrogateKey The surrogateKey to set.
   */
  public final void setSurrogateKey(Long surrogateKey)
  {
    this.surrogateKey = surrogateKey;
  }

  public boolean equals(Object authorization)
  {
    boolean returnValue = false;
    if (this == authorization)
      returnValue = true;
    if (authorization != null && authorization.getClass()==this.getClass()){
      AuthorizationData a = (AuthorizationData) authorization;
      if ((this.getAgentIdString()).equals(a.getAgentIdString())
         && (this.getFunctionId()).equals(a.getFunctionId())
         && (this.getQualifierId()).equals(a.getQualifierId()))
        returnValue = true;
    }
    return returnValue;
  }

  public int hashCode()
  {
    return (this.getAgentIdString() + ":"+ this.getFunctionId() +":"+ this.getQualifierId()).hashCode();
  }
}