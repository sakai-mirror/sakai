package uk.ac.cam.caret.sakai.rwiki.tool.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;

public class AuthZGroupCollectionBean {

    private ViewBean vb;
    private Collection currentRealms;
    
    public List getRealms() {
        if (currentRealms == null) {
            return new ArrayList();
        }
        
        ArrayList returnable = new ArrayList(currentRealms.size());
        
        for (Iterator it = currentRealms.iterator(); it.hasNext();) {
            AuthzGroup realm = (AuthzGroup) it.next();
            
            AuthZGroupBean ab = new AuthZGroupBean(vb.getPageName(), vb.getLocalSpace());
            ab.setCurrentRealm(realm);
            ab.setRealmId(realm.getId());
            
            returnable.add(ab);
        }
        
        return returnable;
    }

    public void setCurrentRealms(Collection currentRealms) {
        this.currentRealms = currentRealms;
    }

    public void setVb(ViewBean vb) {
        this.vb = vb;
    }
    
    
}
