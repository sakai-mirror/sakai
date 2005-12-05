/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.component.message;

import java.util.Date;
import java.util.List;

import org.sakaiproject.service.framework.log.Logger;

import uk.ac.cam.caret.sakai.rwiki.service.message.api.MessageService;
import uk.ac.cam.caret.sakai.rwiki.service.message.api.dao.MessageDao;
import uk.ac.cam.caret.sakai.rwiki.service.message.api.dao.PagePresenceDao;
import uk.ac.cam.caret.sakai.rwiki.service.message.api.dao.PreferenceDao;
import uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Message;
import uk.ac.cam.caret.sakai.rwiki.service.message.api.model.PagePresence;
import uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Preference;

/**
 * @author ieb
 *
 */
public class MessageServiceImpl implements MessageService {
    private Logger log;

    private MessageDao messageDao;
    private PagePresenceDao pagePresenceDao;
    private PreferenceDao preferenceDao;
    
    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.MessageService#updatePresence(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void updatePresence(String session, String user, String page,
            String space) {
        PagePresence pp = pagePresenceDao.findBySession(session);
        if ( pp != null ) {
            pp.setUser(user);
            pp.setPagename(page);
            pp.setPagespace(space);
            pp.setLastseen(new Date());
            pagePresenceDao.update(pp);
        } else {
            pp = pagePresenceDao.createPagePresence(page, space, session,user);
            pagePresenceDao.update(pp);
        }
        log.info("Page Presence "+space+":"+page+":"+user+":"+session);

    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.MessageService#updatePreference(java.lang.String, java.lang.String)
     */
    public void updatePreference(String user, String preference) {
        Preference pref = preferenceDao.findByUser(user);
        if ( pref != null ) {
            pref.setPreference(preference);
            pref.setLastseen(new Date());
            preferenceDao.update(pref);
        } else {
            pref = preferenceDao.createPreference(user,preference);
            preferenceDao.update(pref);
        }
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.MessageService#addMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void addMessage(String session, String user, String page,
            String space, String message) {
        Message messageobj = messageDao.createMessage(space,page,session,user,message);
        messageDao.update(messageobj);
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.MessageService#getSessionMessages(java.lang.String)
     */
    public List getSessionMessages(String session) {
        return messageDao.findBySession(session);
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.MessageService#getMessagesInSpace(java.lang.String)
     */
    public List getMessagesInSpace(String space) {
        return messageDao.findBySpace(space);
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.MessageService#getMessagesInPage(java.lang.String, java.lang.String)
     */
    public List getMessagesInPage(String space, String page) {
        return messageDao.findByPage(space,page);
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.MessageService#getUsersInSpace(java.lang.String)
     */
    public List getUsersInSpace(String space) {
        List l =  pagePresenceDao.findBySpace(space);
        log.info("Found "+l.size()+" users in "+space);
        return l;
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.MessageService#getUsersOnPage(java.lang.String, java.lang.String)
     */
    public List getUsersOnPage(String space, String page) {
        List l =  pagePresenceDao.findByPage(space,page);
        log.info("Found "+l.size()+" users in "+space+" on "+page);
        return l;
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.MessageService#getMessagesInSpaceOnly(java.lang.String, java.lang.String)
     */
    public List getUsersInSpaceOnly(String pageSpace, String pageName) {
        log.info("Searching for users in "+pageSpace+" but not "+pageName);
        return pagePresenceDao.findBySpaceOnly(pageSpace, pageName);
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    /**
     * @return Returns the messageDao.
     */
    public MessageDao getMessageDao() {
        return messageDao;
    }

    /**
     * @param messageDao The messageDao to set.
     */
    public void setMessageDao(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    /**
     * @return Returns the pagePresenceDao.
     */
    public PagePresenceDao getPagePresenceDao() {
        return pagePresenceDao;
    }

    /**
     * @param pagePresenceDao The pagePresenceDao to set.
     */
    public void setPagePresenceDao(PagePresenceDao pagePresenceDao) {
        this.pagePresenceDao = pagePresenceDao;
    }

    /**
     * @return Returns the preferenceDao.
     */
    public PreferenceDao getPreferenceDao() {
        return preferenceDao;
    }

    /**
     * @param preferenceDao The preferenceDao to set.
     */
    public void setPreferenceDao(PreferenceDao preferenceDao) {
        this.preferenceDao = preferenceDao;
    }



}
