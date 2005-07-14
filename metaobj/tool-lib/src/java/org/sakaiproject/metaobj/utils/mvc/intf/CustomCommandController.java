package org.sakaiproject.metaobj.utils.mvc.intf;

import java.util.Map;

/*
 * $URL: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/utils/mvc/intf/CustomCommandController.java,v 1.1 2005/06/28 13:31:53 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/28 13:31:53 $
 */

/**
 * Use this controller when you need to override the way the backing object is created.
 * If all you are doing is return new MyBackingObject() you won't need to implement this,
 * Spring will handle that for you.  Do not use this method to populate helping data
 * into the model.  This method does not get called if there is an error in validation.
 * If you rely on this information for putting stuff in the request, you will not have
 * it there in the case of validation error.  If you need to put helping data in the model,
 * implement FormController's referenceData method.
 *
 * @see Controller
 * @author John Ellis (john.ellis@rsmart.com)
 * @author John Bush (john.bush@rsmart.com)
 */
public interface CustomCommandController extends Controller {
    public Object formBackingObject(Map request,
                                    Map session,
                                    Map application);
}
