/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.tool.command;

import java.util.Date;


/**
 * @author ieb
 *
 */
public class CommentNewCommand extends CommentSaveCommand {
    protected void doUpdate(String name, String user, String realm, Date versionDate, String content) {
        objectService.updateNewComment(name, user, realm, new Date(), content);
    }
}
