/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.service.message.api.dao;

import uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Preference;

/**
 * @author ieb
 *
 */
public interface PreferenceDao {
    Preference createPreference(String user, String preference);
    Preference findByUser(String user);
    void update(Object o);
}
