/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.tool.command;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.cover.SessionManager;

import uk.ac.cam.caret.sakai.rwiki.component.model.impl.NameHelper;
import uk.ac.cam.caret.sakai.rwiki.service.message.api.MessageService;
import uk.ac.cam.caret.sakai.rwiki.tool.RequestScopeSuperBean;
import uk.ac.cam.caret.sakai.rwiki.tool.api.HttpCommand;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.helper.ViewParamsHelperBean;

/**
 * @author ieb
 *
 */
public class SaveChatCommand implements HttpCommand {

    private MessageService messageService;
    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.tool.api.HttpCommand#execute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        RequestScopeSuperBean rssb = RequestScopeSuperBean
            .getFromRequest(request);

        ViewParamsHelperBean vphb = (ViewParamsHelperBean) rssb
            .getNameHelperBean();

        String user = rssb.getCurrentUser();

        String content = vphb.getContent();
        String save = vphb.getSaveType();
        String name = vphb.getGlobalName();
        String space = vphb.getLocalSpace();
        
        Session session = SessionManager.getCurrentSession();
        
        messageService.addMessage(session.getId(),user,name,space,content);
        
        do the request dispatch

    }

}
