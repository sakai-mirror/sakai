/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.service.message.api.dao;

import java.util.List;

import uk.ac.cam.caret.sakai.rwiki.service.message.api.model.PagePresence;

/**
 * @author ieb
 *
 */
public interface PagePresenceDao {
    PagePresence createPagePresence(String pageName, String pageSpace, String sessionid, String user);
    List findBySpace(String pageSpace);
    List findByPage(String pageSpace, String pageName);
    List findByUser(String user);
    PagePresence findBySession(String sessionid);
    /**
     * @param pp
     */
    void update(Object o);
    /**
     * @param pageSpace
     * @param pageName
     * @return
     */
    List findBySpaceOnly(String pageSpace, String pageName);
}
