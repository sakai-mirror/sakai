package org.sakaiproject.jsf.help;

import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

/**
 * @author rshastri
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HelpSetDefaultActionTag extends  UIComponentTag
{
	public String getComponentType() {
		return "SetDefaultAction";
	}

	public String getRendererType() {
		return null;
	}

}