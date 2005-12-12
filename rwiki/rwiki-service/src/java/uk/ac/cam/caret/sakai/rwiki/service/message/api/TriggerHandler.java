/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.service.message.api;

/**
 * @author ieb
 *
 */
public interface TriggerHandler {
    void fireOnSpace(String space);
    void fireOnPage(String space, String page);

}
