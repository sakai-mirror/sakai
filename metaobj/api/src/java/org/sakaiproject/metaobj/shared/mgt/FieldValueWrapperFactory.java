package org.sakaiproject.metaobj.shared.mgt;

import org.sakaiproject.metaobj.utils.mvc.intf.FieldValueWrapper;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 5, 2005
 * Time: 2:28:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface FieldValueWrapperFactory {
   boolean checkWrapper(Class clazz);

   FieldValueWrapper wrapInstance(Class clazz);

   FieldValueWrapper wrapInstance(Object obj);
}
