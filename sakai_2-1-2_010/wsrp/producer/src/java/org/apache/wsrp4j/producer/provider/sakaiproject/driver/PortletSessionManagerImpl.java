/**
 * 
 */
package org.apache.wsrp4j.producer.provider.sakaiproject.driver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.AxisEngine;
import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.wsrp4j.exception.WSRPException;
import org.apache.wsrp4j.exception.WSRPXHelper;
import org.apache.wsrp4j.exception.ErrorCodes;
import org.apache.wsrp4j.log.LogManager;
import org.apache.wsrp4j.log.Logger;
import org.apache.wsrp4j.producer.provider.PortletSessionManager;
import org.apache.wsrp4j.producer.provider.Provider;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.cover.SessionManager;


/**
 * @author <a href="mailto:vgoenka@sungardsct.com">Vishal Goenka</a>
 *
 */
public class PortletSessionManagerImpl implements PortletSessionManager
{
    private static final String SESSION_COOKIE = "JSESSIONID";

    // log and trace support
    private Logger logger = LogManager.getLogManager().getLogger(this.getClass());
   
    // Keep old sessions around, so we can reset sessions back
    // We only need one level of session tracking, rather than multiple session changes in same request
    private ThreadLocal m_oldSession = new ThreadLocal();

    private Provider provider;

    
    /**
     * Final initialization, once all dependencies are set.
     */
    private PortletSessionManagerImpl()
    {
    }

    public static PortletSessionManagerImpl create(Provider provider) {
        PortletSessionManagerImpl sm = new PortletSessionManagerImpl();
        sm.provider = provider;
        return sm;
    }


    public boolean setCurrentSession(boolean create) throws WSRPException 
    {
        MessageContext msgContext = AxisEngine.getCurrentMessageContext();

        HttpServletRequest servletRequest =
            (HttpServletRequest)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
        
        HttpServletResponse servletResponse = 
            (HttpServletResponse)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE);

        Cookie c = findCookie(servletRequest, SESSION_COOKIE);
        
        Session s = null;
        
        if (c != null)
        {
            s = SessionManager.getSession(c.getValue());
        }
        
        if (s == null)
        {
            // if we don't have a session and create = true, create a new cookie, overriding any previous cookie
            if (create)
            {
                s = SessionManager.startSession();
                // set the cookie
                c = new Cookie(SESSION_COOKIE, s.getId());
                c.setPath("/");
                c.setMaxAge(0);
                servletResponse.addCookie(c);
            } else {
            // create == false => We are expected to have a valid session, but we don't ... throw an exception
                WSRPXHelper.throwX(ErrorCodes.INVALID_COOKIE);
            }
        }
        m_oldSession.set(SessionManager.getCurrentSession());        
        SessionManager.setCurrentSession(s);
        
        return (s != null);
    }
    
    /* (non-Javadoc)
     * @see org.apache.wsrp4j.producer.provider.sakaiproject.driver.WSRPSessionManager#setCurrentSession(org.apache.wsrp4j.producer.provider.sakaiproject.driver.PortletSessionManagerImpl.WSRPSession)
     */
    public void resetCurrentSession()
    {
        SessionManager.setCurrentSession((Session) m_oldSession.get());
        m_oldSession.set(null);
    }
    
    public void setCurrentUser(String userId)
    {
        Session s = SessionManager.getCurrentSession();
        if (s != null) {
            s.setUserId(userId);
            s.setUserEid(userId);            
        }
    }
    
    private Cookie findCookie(HttpServletRequest req, String name)
    {
        Cookie[] cookies = req.getCookies();
        if (cookies != null)
        {
            for (int i = 0; i < cookies.length; i++)
            {
                if (cookies[i].getName().equals(name))
                {
                    return cookies[i];
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.wsrp4j.producer.provider.sakaiproject.driver.WSRPSessionManager#releaseSession(java.lang.String)
     */
    public void releaseSession(String sessionId)
    {
        Session s = SessionManager.getSession(sessionId);
        if (s == SessionManager.getCurrentSession())
        {
            SessionManager.setCurrentSession(null);
        }
        if (s != null) s.invalidate();
    }    
}
