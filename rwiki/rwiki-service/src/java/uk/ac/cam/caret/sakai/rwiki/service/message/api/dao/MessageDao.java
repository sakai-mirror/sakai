/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.service.message.api.dao;

import java.util.List;

import uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Message;

/**
 * @author ieb
 *
 */
public interface MessageDao {
    Message createMessage(String pageSpace, String pageName, String sessionid, String user, String message);
    List findBySpace(String pageSpace);
    List findByPage(String pageSpace, String pageName);
    List findByUser(String user);
    List findBySession(String session);
    void update(Object o);
}
