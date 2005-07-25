package org.sakaiproject.metaobj.utils.xml;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 25, 2004
 * Time: 3:26:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ElementType {
   Class getObjectType();

   int getLength();

   int getMaxLength();

   int getMinLength();

   String getDefaultValue();

   String getFixedValue();

   List getEnumeration();
}
