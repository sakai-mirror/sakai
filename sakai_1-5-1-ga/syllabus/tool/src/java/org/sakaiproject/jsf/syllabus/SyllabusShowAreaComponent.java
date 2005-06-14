package org.sakaiproject.jsf.syllabus;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

public class SyllabusShowAreaComponent extends UIComponentBase
{
	public SyllabusShowAreaComponent()
	{
		super();
		this.setRendererType("SyllabusShowAreaRender");
	}

	public String getFamily()
	{
		return "SyllabusShowArea";
	}
}
