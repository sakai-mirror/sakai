package org.sakaiproject.jsf.syllabus;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

public class SyllabusIframeComponent extends UIComponentBase
{
	public SyllabusIframeComponent()
	{
		super();
		this.setRendererType("SakaiSyllabusIframeRender");
	}

	public String getFamily()
	{
		return "SakaiSyllabusIframe";
	}
}
