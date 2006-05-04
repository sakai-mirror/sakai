package uk.ac.cam.caret.sakai.rwiki.tool.bean.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroupService;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;

import uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.service.exception.PermissionException;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.AuthZGroupCollectionBean;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.ViewBean;

public class AuthZGroupCollectionBeanHelper {

    public static AuthZGroupCollectionBean createAuthZCollectionBean(AuthzGroupService realmService, RWikiObject currentRWikiObject, ViewBean viewBean, RWikiObjectService objectService) {
        Entity entity = objectService.getEntity(currentRWikiObject);
        
        Collection groupRefs = objectService.getEntityAuthzGroups(EntityManager.newReference(entity.getReference()));
        
        List groups = new ArrayList(groupRefs.size());
        
        for (Iterator it = groupRefs.iterator(); it.hasNext(); ) {
            Reference groupRef = (Reference) it.next();
            try {
                groups.add(realmService.getAuthzGroup(groupRef.getId()));
            } catch (IdUnusedException e) {
                throw new PermissionException("Id Unused: " + groupRef.getId() + " assuming only possible reason for this is bad permissions. ", e);
            }
        }
        
        AuthZGroupCollectionBean collectionBean = new AuthZGroupCollectionBean();
        collectionBean.setCurrentRealms(groups);
        collectionBean.setVb(viewBean);
        
        // TODO Auto-generated method stub
        return null;
    }

}
