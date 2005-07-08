package org.sakaiproject.metaobj.utils.mvc.intf;

import org.springframework.validation.Errors;

import java.util.Map;

/*
 * $Header: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/utils/mvc/intf/FormController.java,v 1.1 2005/06/28 13:31:53 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/28 13:31:53 $
 */

/**
 * This controller is useful for handling form submissions.  In a normal form submission
 * formBackingObject is called to create the backing object.  Next the system binds
 * request params into this object, and performs validation.  If validation errors are
 * detected the system re-renders the form view.  This flow creates a problem if you need
 * to populate the model with something for the form, because if you try to do this
 * work in formBackingObject, you notice the system doesn't call this again in the case
 * of validation erros.  The referenceData methods provides an convenient place to do this
 * work.  This method will be called before rendering the form the first time, and before
 * rendering the form after validation errors.
 * @author John Ellis (john.ellis@rsmart.com)
 * @author John Bush (john.bush@rsmart.com)
 */
public interface FormController extends Controller {
   /**
    * Create a map of all data the form requries.
    * Useful for building up drop down lists, etc.
    * @param request
    * @param command
    * @param errors
    * @return
    */
   public Map referenceData(Map request, Object command, Errors errors);
}
