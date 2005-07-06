package org.sakaiproject.metaobj.utils.mvc.intf;

import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * implement this interface if you have work that needs to be done after a controller.
 * In the case of forms this method is called after formBackingObject() is called or after
 * handleRequest is called.  For non forms it is simply called after handleRequest.
 *
 * Note implementations of this interface are invoked for each request that goes through a controller, so only
 * use this for things that can't be handled directly in the controller.
 */
public interface ControllerFilter {
   /**
    * this method is called after the handleRequest method of the controller is called
    * @param request
    * @param session
    * @param application
    * @param modelAndView
    * @param screenMapping
    */
   public void doFilter(Map request,
                        Map session,
                        Map application,
                        ModelAndView modelAndView,
                        String screenMapping);
}
