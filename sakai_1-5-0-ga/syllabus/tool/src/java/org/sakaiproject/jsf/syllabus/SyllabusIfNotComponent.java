package org.sakaiproject.jsf.syllabus;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

public class SyllabusIfNotComponent extends UIComponentBase
{
	public SyllabusIfNotComponent()
	{
		super();
		this.setRendererType("SakaiSyllabusIfNotRender");
	}

	public String getFamily()
	{
		return "SakaiSyllabusIfNot";
	}
	
	public boolean getRendersChildren()
	{
	  return true;
	}	
}
