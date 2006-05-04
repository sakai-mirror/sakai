package org.sakaiproject.metaobj.shared.mgt;

import org.sakaiproject.metaobj.shared.model.ElementBean;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 18, 2005
 * Time: 2:49:14 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This interface allows for the validation of ElementBean objects
 */
public interface StructuredArtifactValidationService {

   /**
    * Validate this element from the root.
    *
    * @param element filled in element to be validated.
    * @return list of ValidationError objects.  If this list is
    *         returned empty, then the element validated successfully
    * @see org.sakaiproject.metaobj.shared.model.ValidationError
    */
   public List validate(ElementBean element);

   /**
    * Validate this element from the root.
    *
    * @param element    filled in element to be validated.
    * @param parentName this is the name of the parent of this object.
    *                   All fields that have errors will have this name prepended with a "."
    * @return list of ValidationError objects.  If this list is
    *         returned empty, then the element validated successfully
    * @see org.sakaiproject.metaobj.shared.model.ValidationError
    */
   public List validate(ElementBean element, String parentName);

}
