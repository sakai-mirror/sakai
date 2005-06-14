/*
 * Created on Oct 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.tool.help;

 
import org.sakaiproject.service.framework.email.EmailService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.framework.cluster.cover.ClusterService;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.help.HelpManager;
 
/**
 * @author casong
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QuestionTool {
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
     * @return Returns the helpManager.
     */
    public HelpManager getHelpManager()
    {  
      return helpManager;
    }
    /**
     * @param helpManager The helpManager to set.
     */
    public void setHelpManager(HelpManager helpManager)
    {
      this.helpManager = helpManager;
    }
    /**
     * @return Returns the emailAddress.
     */
    public String getEmailAddress() {
        return emailAddress;
    }
    /**
     * @param emailAddress The emailAddress to set.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    /**
     * @return Returns the firstName.
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * @param firstName The firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * @return Returns the lastName.
     */
    public String getLastName() {
        return lastName;
    }
    /**
     * @param lastName The lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * @return Returns the subject.
     */
    public String getSubject() {
        return subject;
    }
    /**
     * @param subject The subject to set.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    /**
     * @return Returns the userName.
     */
    public String getUserName() {
        return userName;
    }
    /**
     * @param userName The userName to set.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    /**
     * @return Returns the emailService.
     */
    public EmailService getEmailService() {
        return emailService;
    }
    /**
     * @param emailService The emailService to set.
     */
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
    /**
     * @return Returns the toEmailAddress.
     */
    public String getToEmailAddress() {
      if(toEmailAddress == null)
      {
        toEmailAddress = helpManager.getSupportEmailAddress();
      }
        return toEmailAddress;
    }
    /**
     * @param toEmailAddress The toEmailAddress to set.
     */
    public void setToEmailAddress(String toEmailAddress) {
        this.toEmailAddress = toEmailAddress;
    }
    
   	public String getDetailedContent()
	{
		
		String UNAVAILABLE = "~unavailable~";
		
		String IP = UNAVAILABLE;
		String agent = UNAVAILABLE;
		String sessionId = UNAVAILABLE;
		String serverName =UNAVAILABLE;
		
	 	if (UsageSessionService.getSession() != null)
			{
	 	  
			  	IP = UsageSessionService.getSession().getIpAddress();
				agent = UsageSessionService.getSession().getUserAgent();
				sessionId = UsageSessionService.getSession().getId();
				serverName = ServerConfigurationService.getServerName();
			}  
	 
		String detailedContent = "\n\n"
		+ "Sender's name: " + this.firstName + " "+this.lastName + "\n"
		+ "Sender's UserName: " + userName + "\n"
		+ "Sender's IP: " + IP + "\n"
		+ "Sender's Browser/Agent: " + agent + "\n"
		+ "Sender's SessionID: " + sessionId + "\n"
		+ "Server Name: " + serverName + "\n"
		+ "Comments or questions: \n"
		+ this.getContent() + "\n\n"
		+ "Sender's (reply-to) email: " + emailAddress + "\n\n"
		+ "Site: Help Tool"  + "\n"
		+ "Site Id: " + PortalService.getCurrentSiteId() + "\n";
		
		return detailedContent;
		 
	}
      
    public String submitQuestion()
    {
        this.sendEmail();
        return "display";
    }
    
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
    
    public String submitQuestionCancel()
    {
      return this.reset();
    }
    
    private void sendEmail()
    {
      try
      {
          String detailedContent=getDetailedContent();
          emailService.send(emailAddress, this.getToEmailAddress(), subject, detailedContent, null, null, null);
      }
      catch(Exception e)
      {
          logger.error("email service is not set up correctly, can't send user question to support consultant!", e);
      }
    }
    /**
     * @return Returns the content.
     */
    public String getContent() {
        return content;
    }
    /**
     * @param content The content to set.
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * @return Returns the logger.
     */
    public Logger getLogger()
    {
      return logger;
    }
    /**
     * @param logger The logger to set.
     */
    public void setLogger(Logger logger)
    {
      this.logger = logger;
    }
}
