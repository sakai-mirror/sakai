/*
 *                       Navigo Software License
 *
 * Copyright 2003, Trustees of Indiana University, The Regents of the University
 * of Michigan, and Stanford University, all rights reserved.
 *
 * This work, including software, documents, or other related items (the
 * "Software"), is being provided by the copyright holder(s) subject to the
 * terms of the Navigo Software License. By obtaining, using and/or copying this
 * Software, you agree that you have read, understand, and will comply with the
 * following terms and conditions of the Navigo Software License:
 *
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose and without fee
 * or royalty is hereby granted, provided that you include the following on ALL
 * copies of the Software or portions thereof, including modifications or
 * derivatives, that you make:
 *
 *    The full text of the Navigo Software License in a location viewable to
 *    users of the redistributed or derivative work.
 *
 *    Any pre-existing intellectual property disclaimers, notices, or terms and
 *    conditions. If none exist, a short notice similar to the following should
 *    be used within the body of any redistributed or derivative Software:
 *    "Copyright 2003, Trustees of Indiana University, The Regents of the
 *    University of Michigan and Stanford University, all rights reserved."
 *
 *    Notice of any changes or modifications to the Navigo Software, including
 *    the date the changes were made.
 *
 *    Any modified software must be distributed in such as manner as to avoid
 *    any confusion with the original Navigo Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) and/or Indiana University,
 * The University of Michigan, Stanford University, or Navigo may NOT be used
 * in advertising or publicity pertaining to the Software without specific,
 * written prior permission. Title to copyright in the Software and any
 * associated documentation will at all times remain with the copyright holders.
 * The export of software employing encryption technology may require a specific
 * license from the United States Government. It is the responsibility of any
 * person or organization contemplating export to obtain such a license before
 * exporting this Software.
 */


/**
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author jlannan
 * @version $Id: QtiSettingsBean.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
package org.navigoproject.data;

import java.sql.Timestamp;

/**
 * @hibernate.class
 *            table="QTI_SETTINGS"
 */
public class QtiSettingsBean implements Comparable
{
	private static org.apache.log4j.Logger LOG =
			org.apache.log4j.Logger.getLogger(QtiSettingsBean.class);
			
  private String id;
  private String displayName;  
  private String autoSubmit;
  private Character autoSave;
  private String testDisabled;
  private String ipRestrictions;
  private String usernameRestriction;
  private String passwordRestriction;
  private String feedbackType;
  private Character lateHandling;
  private Timestamp startDate;
  private Timestamp endDate;
  private Timestamp createdDate;
  private Timestamp feedbackDate;
  private Timestamp retractDate;
  private Integer maxAttempts;
  

  /**
   * Creates a new QtiSettingsBean object.
   */
  public QtiSettingsBean()
  {
  }

  /**
   * Creates a new QtiSettingsBean object.
   *
   * @param id DOCUMENTATION PENDING
   * @param maxAttempts DOCUMENTATION PENDING
   * @param autoSubmit DOCUMENTATION PENDING
   * @param autoSave DOCUMENTATION PENDING
   * @param testDisabled DOCUMENTATION PENDING
   * @param startDate DOCUMENTATION PENDING
   * @param endDate DOCUMENTATION PENDING
   * @param createdDate DOCUMENTATION PENDING
   * @param feedbackDate DOCUMENTATION PENDING
   * @param retractDate DOCUMENTATION PENDING
   * @param ipRestrictions DOCUMENTATION PENDING
   * @param usernameRestriction DOCUMENTATION PENDING
   * @param passwordRestriction DOCUMENTATION PENDING
   */
  public QtiSettingsBean(
    String id, String displayName, Integer maxAttempts, String autoSubmit, Character autoSave,  String testDisabled,
    Timestamp startDate, Timestamp endDate, Timestamp createdDate,
    Timestamp feedbackDate, Timestamp retractDate, String ipRestrictions,
    String usernameRestriction, String passwordRestriction, String feedbackType,
    Character lateHandling)
  {
    this.id = id;
    this.displayName = displayName;
    this.maxAttempts = maxAttempts;
    this.autoSubmit = autoSubmit;
    this.autoSave = autoSave;
    this.testDisabled = testDisabled;
    this.startDate = startDate;
    this.endDate = endDate;
    this.createdDate = createdDate;
    this.feedbackDate = feedbackDate;
    this.retractDate = retractDate;
    this.ipRestrictions = ipRestrictions;
    this.usernameRestriction = usernameRestriction;
    this.passwordRestriction = passwordRestriction;
    this.feedbackType = feedbackType;
    this.lateHandling = lateHandling;    
  }

  /**
   * @hibernate.id
   *            generator-class="assigned"
   *            column="id"
   */
  public String getId()
  {
    return this.id;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pk DOCUMENTATION PENDING
   */
  public void setId(String pk)
  {
    this.id = pk;
  }
  
	/**
	 * @hibernate.property
	 *            column="DISPLAY_NAME"
	 */
	public String getDisplayName()
	{
		return this.displayName;
	}

	/**
	 * DOCUMENTATION PENDING
	 *
	 * @param autoSubmit DOCUMENTATION PENDING
	 */
	public void setDisplayName(String dn)
	{
		this.displayName = dn;
	}  

  /**
   * @hibernate.property
   *            column="MAX_ATTEMPTS"
   */
  public Integer getMaxAttempts()
  {
    return this.maxAttempts;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param maxAttempts DOCUMENTATION PENDING
   */
  public void setMaxAttempts(Integer maxAttempts)
  {
    this.maxAttempts = maxAttempts;
  }

  /**
   * @hibernate.property
   *            column="AUTO_SUBMIT"
   */
  public String getAutoSubmit()
  {
    return this.autoSubmit;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param autoSubmit DOCUMENTATION PENDING
   */
  public void setAutoSubmit(String autoSubmit)
  {
    this.autoSubmit = autoSubmit;
  }

  /**
   * @hibernate.property
   *            column="AUTO_SAVE"
   */
  public Character getAutoSave()
  {
    return this.autoSave;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param autoSave DOCUMENTATION PENDING
   */
  public void setAutoSave(Character autoSave)
  {
    this.autoSave = autoSave;
  }

  /**
   * @hibernate.property
   *            column="TEST_DISABLED"
   */
  public String getTestDisabled()
  {
    return this.testDisabled;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testDisabled DOCUMENTATION PENDING
   */
  public void setTestDisabled(String testDisabled)
  {
    this.testDisabled = testDisabled;
  }

  /**
   * @hibernate.property
   *            column="START_DATE"
   */
  public Timestamp getStartDate()
  {
    return this.startDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param startDate DOCUMENTATION PENDING
   */
  public void setStartDate(Timestamp startDate)
  {
    this.startDate = startDate;
  }

  /**
   * @hibernate.property
   *            column="END_DATE"
   */
  public Timestamp getEndDate()
  {
    return this.endDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param endDate DOCUMENTATION PENDING
   */
  public void setEndDate(Timestamp endDate)
  {
    this.endDate = endDate;
  }

  /**
   * @hibernate.property
   *            column="CREATED_DATE"
   */
  public Timestamp getCreatedDate()
  {
    return this.createdDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param cd DOCUMENTATION PENDING
   */
  public void setCreatedDate(Timestamp cd)
  {
    this.createdDate = cd;
  }

  /**
   * @hibernate.property
   *            column="FEEDBACK_DATE"
   */
  public Timestamp getFeedbackDate()
  {
    return this.feedbackDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param fd DOCUMENTATION PENDING
   */
  public void setFeedbackDate(Timestamp fd)
  {
    this.feedbackDate = fd;
  }

  /**
   * @hibernate.property
   *            column="RETRACT_DATE"
   */
  public Timestamp getRetractDate()
  {
    return this.retractDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param rd DOCUMENTATION PENDING
   */
  public void setRetractDate(Timestamp rd)
  {
    this.retractDate = rd;
  }

  /**
   * @hibernate.property
   *            column="IP_RESTRICTIONS"
   */
  public String getIpRestrictions()
  {
    return ipRestrictions;
  }

  
  public void setIpRestrictions(String string)
  {
    ipRestrictions = string;
  }

  /**
   * @hibernate.property
   *            column="PASSWORD_RESTRICTION"
   */
  public String getPasswordRestriction()
  {
    return passwordRestriction;
  }

  
  public void setPasswordRestriction(String string)
  {
    passwordRestriction = string;
  }

  /**
   * @hibernate.property
   *            column="USERNAME_RESTICTION"
   */
  public String getUsernameRestriction()
  {
    return usernameRestriction;
  }

  
  public void setUsernameRestriction(String string)
  {
    usernameRestriction = string;
  }
  
	/**
	 * @hibernate.property
	 *            column="FEEDBACK_TYPE"
	 */
	public String getFeedbackType()
	{
		return this.feedbackType;
	}

	/**
	 * DOCUMENTATION PENDING
	 *
	 * @param testDisabled DOCUMENTATION PENDING
	 */
	public void setFeedbackType(String feedbackType)
	{
		this.feedbackType = feedbackType;
	}
  
	/**
	 * @hibernate.property
	 *            column="LATE_HANDLING"
	 */
	public Character getLateHandling()
	{
		return this.lateHandling;
	}

	/**
	 * DOCUMENTATION PENDING
	 *
	 * @param testDisabled DOCUMENTATION PENDING
	 */
	public void setLateHandling(Character lateHandling)
	{
		this.lateHandling = lateHandling;
	}
	

	public int hashCode() {
		return id.hashCode();
	}

	public int compareTo(Object o) {		
		QtiSettingsBean qsb = (QtiSettingsBean) o;
		
		
		if (this.displayName == null && qsb.getDisplayName() == null){
		  return 0;
		}
		
		if (this.displayName == null){
		  return -1;
		}
		
		if (qsb.getDisplayName() == null){
		  return 1;
		}
				
				
		//if (this.displayName != null && qsb.getDisplayName() != null){
			return this.displayName.compareTo(qsb.getDisplayName());
		//}
		//else{		  
	  //  return 1; 
		//}
		
	}	
  
}
