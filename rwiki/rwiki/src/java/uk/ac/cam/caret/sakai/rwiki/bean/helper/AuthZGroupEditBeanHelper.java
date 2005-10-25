package uk.ac.cam.caret.sakai.rwiki.bean.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import uk.ac.cam.caret.sakai.rwiki.bean.AuthZGroupEditBean;
import uk.ac.cam.caret.sakai.rwiki.bean.ViewBean;

public class AuthZGroupEditBeanHelper {

    public static final String REALM_EDIT_BEAN_ATTR = "realmEditBean";
    
    public static AuthZGroupEditBean createRealmEditBean(HttpServletRequest request, ViewBean vb) {
        HttpSession session = request.getSession();
        AuthZGroupEditBean rb = (AuthZGroupEditBean) session.getAttribute(REALM_EDIT_BEAN_ATTR);
        
        if (rb == null) {
            rb = new AuthZGroupEditBean(vb.getPageName(), vb.getLocalSpace());
            session.setAttribute(REALM_EDIT_BEAN_ATTR, rb);
        }
        
        return rb;
    }
    
}
