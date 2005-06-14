package org.sakaiproject.jsf.syllabus;

import javax.faces.component.html.HtmlDataTable;

public class SyllabusTableComponent extends HtmlDataTable
{
	public SyllabusTableComponent()
	{
		super();

		setStyleClass("syllabusTable");
		setColumnClasses("item,move,move,status,status");
	}
}
