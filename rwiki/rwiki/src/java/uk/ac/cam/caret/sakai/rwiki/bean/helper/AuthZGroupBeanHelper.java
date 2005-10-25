package uk.ac.cam.caret.sakai.rwiki.bean.helper;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroupService;

import uk.ac.cam.caret.sakai.rwiki.bean.ErrorBean;
import uk.ac.cam.caret.sakai.rwiki.bean.AuthZGroupBean;
import uk.ac.cam.caret.sakai.rwiki.bean.ViewBean;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiObject;

public class AuthZGroupBeanHelper {

	public static AuthZGroupBean createRealmBean(AuthzGroupService realmService, RWikiObject rwikiObject, ErrorBean errorBean, ViewBean vb) {
		AuthZGroupBean rb = new AuthZGroupBean(vb.getPageName(), vb.getLocalSpace());
		
		String realmId = rwikiObject.getRealm();
		rb.setRealmId(realmId);
		try {
			AuthzGroup realm = realmService.getAuthzGroup(realmId);
			rb.setCurrentRealm(realm);
		} catch (IdUnusedException e) {
			// FIXME localise!
			errorBean.addError("Realm: " + realmId + " is not recognised in the system.");
		}
        
        rb.setSiteUpdateAllowed(realmService.allowUpdate(realmId));
		
		return rb;
	}
	
}
