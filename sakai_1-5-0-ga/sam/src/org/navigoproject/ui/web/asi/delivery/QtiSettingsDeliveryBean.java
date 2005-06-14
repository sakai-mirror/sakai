/*
 * Created on May 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.delivery;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class QtiSettingsDeliveryBean 
{
  private String maxAttempts;
  private String autoSubmit;
  private Character autoSave;
  private String feedback;
  private String dueDate;
  private String username;
  private String password;
  private String ipAddresses;

  /**
   * @return
   */
  public String getMaxAttempts()
  {
    return maxAttempts;
  }

  /**
   * @param string
   */
  public void setMaxAttempts(String maxAttempts)
  {
    this.maxAttempts = maxAttempts;
  }

  /**
   * @return
   */
  public String getAutoSubmit()
  {
    return autoSubmit;
  }

  /**
   * @return
   */
  public Character getAutoSave()
  {
    return autoSave;
  }

  /**
   * @return
   */
  public String getDueDate()
  {
    return dueDate;
  }

  /**
   * @return
   */
  public String getFeedback()
  {
    return feedback;
  }

  /**
   * @param string
   */
  public void setAutoSubmit(String string)
  {
    autoSubmit = string;
  }

  /**
   * @param string
   */
  public void setAutoSave(Character string)
  {
    autoSave= string;
  }

  /**
   * @param string
   */
  public void setDueDate(String string)
  {
    dueDate = string;
  }

  /**
   * @param string
   */
  public void setFeedback(String string)
  {
    feedback = string;
  }

  /**
   * @return
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * @return
   */
  public String getUsername()
  {
    return username;
  }

  /**
   * @param string
   */
  public void setPassword(String string)
  {
    password = string;
  }

  /**
   * @param string
   */
  public void setUsername(String string)
  {
    username = string;
  }

  /**
   * @return
   */
  public String getIpAddresses()
  {
    return ipAddresses;
  }

  /**
   * @param string
   */
  public void setIpAddresses(String string)
  {
    ipAddresses = string;
  }

}
