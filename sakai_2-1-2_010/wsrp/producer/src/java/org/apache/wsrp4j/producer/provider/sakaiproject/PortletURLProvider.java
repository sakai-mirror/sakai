
package org.apache.wsrp4j.producer.provider.sakaiproject;

import java.util.Map;

import org.apache.wsrp4j.util.Modes;
import org.apache.wsrp4j.util.WindowStates;


public interface PortletURLProvider {

    /**
      Set the portlet mode
      @param mode portlet mode
    */
    public void setPortletMode(Modes mode);

    /**
      Set the portlet window state
      @param state portlet window state
    */
    public void setWindowState(WindowStates state);

    /**
      Set a flag that an action should be encoded
    */
    public void setAction();

    /**
      Set a flag that security is turned on
    */
    public void setSecure();

    /**
      Set a flag that all render parameters should be cleared
    */
    public void clearParameters();

    /**
      Set action parameters
      @param parameters java.util.Map
    */
    public void setParameters(Map parameters);

    /**
      Returns a String representation of a URL
      @return java.lang.String
    */
    public String encodeURL(String url);
}
