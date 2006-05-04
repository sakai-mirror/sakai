/**********************************************************************************
 *
 * $Header: /cvs/sakai2/help/help-tool/src/java/org/sakaiproject/tool/help/QuestionTool.java,v 1.2 2005/05/18 15:14:22 jlannan.iupui.edu Exp $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.tool.help;

import org.sakaiproject.api.app.help.HelpManager;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.email.EmailService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;

/**
 * question tool
 * @version $Id$
 */
public class QuestionTool
{

  private String lastName;
  private String firstName;
  private String userName;
  private String emailAddress;
  private String subject;
  private String content;

  private String toEmailAddress;
  private EmailService emailService;
  private Logger logger;
  private HelpManager helpManager;

  /**
   * get help manager
   * @return Returns the helpManager.
   */
  public HelpManager getHelpManager()
  {
    return helpManager;
  }

  /**
   * set help manager
   * @param helpManager The helpManager to set.
   */
  public void setHelpManager(HelpManager helpManager)
  {
    this.helpManager = helpManager;
  }

  /**
   * get email address
   * @return Returns the emailAddress.
   */
  public String getEmailAddress()
  {
    return emailAddress;
  }

  /**
   * set email address
   * @param emailAddress The emailAddress to set.
   */
  public void setEmailAddress(String emailAddress)
  {
    this.emailAddress = emailAddress;
  }

  /**
   * get first name
   * @return Returns the firstName.
   */
  public String getFirstName()
  {
    return firstName;
  }

  /**
   * set first name
   * @param firstName The firstName to set.
   */
  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }

  /**
   * get last name
   * @return Returns the lastName.
   */
  public String getLastName()
  {
    return lastName;
  }

  /**
   * set last name
   * @param lastName The lastName to set.
   */
  public void setLastName(String lastName)
  {
    this.lastName = lastName;
  }

  /**
   * get subject
   * @return Returns the subject.
   */
  public String getSubject()
  {
    return subject;
  }

  /**
   * set subject
   * @param subject The subject to set.
   */
  public void setSubject(String subject)
  {
    this.subject = subject;
  }

  /**
   * get user name
   * @return Returns the userName.
   */
  public String getUserName()
  {
    return userName;
  }

  /**
   * set user name
   * @param userName The userName to set.
   */
  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  /**
   * get email service
   * @return Returns the emailService.
   */
  public EmailService getEmailService()
  {
    return emailService;
  }

  /**
   * set email service
   * @param emailService The emailService to set.
   */
  public void setEmailService(EmailService emailService)
  {
    this.emailService = emailService;
  }

  /**
   * get to email address
   * @return Returns the toEmailAddress.
   */
  public String getToEmailAddress()
  {
    if (toEmailAddress == null)
    {
      toEmailAddress = helpManager.getSupportEmailAddress();
    }
    return toEmailAddress;
  }

  /**
   * set to email address
   * @param toEmailAddress The toEmailAddress to set.
   */
  public void setToEmailAddress(String toEmailAddress)
  {
    this.toEmailAddress = toEmailAddress;
  }

  /**
   * get detailed content
   * @return content
   */
  public String getDetailedContent()
  {

    String UNAVAILABLE = "~unavailable~";

    String IP = UNAVAILABLE;
    String agent = UNAVAILABLE;
    String sessionId = UNAVAILABLE;
    String serverName = UNAVAILABLE;

    if (UsageSessionService.getSession() != null)
    {

      IP = UsageSessionService.getSession().getIpAddress();
      agent = UsageSessionService.getSession().getUserAgent();
      sessionId = UsageSessionService.getSession().getId();
      serverName = ServerConfigurationService.getServerName();
    }

    String detailedContent = "\n\n" + "Sender's name: " + this.firstName + " "
        + this.lastName + "\n" + "Sender's UserName: " + userName + "\n"
        + "Sender's IP: " + IP + "\n" + "Sender's Browser/Agent: " + agent
        + "\n" + "Sender's SessionID: " + sessionId + "\n" + "Server Name: "
        + serverName + "\n" + "Comments or questions: \n" + this.getContent()
        + "\n\n" + "Sender's (reply-to) email: " + emailAddress + "\n\n"
        + "Site: Help Tool" + "\n" + "Site Id: "
        + PortalService.getCurrentSiteId() + "\n";

    return detailedContent;

  }

  /**
   * submit question
   * @return view
   */
  public String submitQuestion()
  {
    this.sendEmail();
    return "display";
  }

  /**
   * reset
   * @return view
   */
  public String reset()
  {
    this.content = "";
    this.subject = "";
    this.firstName = "";
    this.lastName = "";
    this.emailAddress = "";
    this.userName = "";

    return "main";
  }

  /**
   * submit question cancel
   * @return
   */
  public String submitQuestionCancel()
  {
    return this.reset();
  }

  /**
   * send email
   */
  private void sendEmail()
  {
    try
    {
      String detailedContent = getDetailedContent();
      emailService.send(emailAddress, this.getToEmailAddress(), subject,
          detailedContent, null, null, null);
    }
    catch (Exception e)
    {
      logger
          .error(
              "email service is not set up correctly, can't send user question to support consultant!",
              e);
    }
  }

  /**
   * get content
   * @return Returns the content.
   */
  public String getContent()
  {
    return content;
  }

  /**
   * set content
   * @param content The content to set.
   */
  public void setContent(String content)
  {
    this.content = content;
  }

  /**
   * get logger
   * @return Returns the logger.
   */
  public Logger getLogger()
  {
    return logger;
  }

  /**
   * set logger
   * @param logger The logger to set.
   */
  public void setLogger(Logger logger)
  {
    this.logger = logger;
  }
}