/**********************************************************************************
* $HeadURL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.tool.assessment.osid.authz.impl;

import java.util.Date;

import org.osid.authorization.Authorization;
import org.osid.authorization.Function;
import org.osid.authorization.Qualifier;
import org.osid.shared.Id;

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