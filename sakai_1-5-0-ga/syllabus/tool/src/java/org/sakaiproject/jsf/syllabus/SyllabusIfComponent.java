package org.sakaiproject.jsf.syllabus;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

public class SyllabusIfComponent extends UIComponentBase
{
	public SyllabusIfComponent()
	{
		super();
		this.setRendererType("SakaiSyllabusIfRender");
	}

	public String getFamily()
	{
		return "SakaiSyllabusIf";
	}
	
	public boolean getRendersChildren()
	{
	  return true;
	}	
}
