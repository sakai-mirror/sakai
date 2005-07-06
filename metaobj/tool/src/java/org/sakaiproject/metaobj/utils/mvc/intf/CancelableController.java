package org.sakaiproject.metaobj.utils.mvc.intf;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.Errors;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jbush
 * Date: Dec 15, 2004
 * Time: 9:36:16 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CancelableController {
   public boolean isCancel(Map request);
   public ModelAndView processCancel(Map request, Map session, Map application,
                                     Object command, Errors errors) throws Exception;
}
