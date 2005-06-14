/*
 * Created on Jan 4, 2005 TODO To change the template for this generated file go to Window - Preferences -
 * Java - Code Style - Code Templates
 */
package org.sakaiproject.jsf.help;

/**
 * @author rshastri TODO To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates
 */

import java.io.IOException;

import javax.faces.component.UIOutput;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;

public class HelpSetDefaultActionComponent extends UIOutput{
	
	
public void encodeBegin(FacesContext context) throws IOException {
	return;
}
	public void decode(FacesContext context) {
	return;
}
public void encodeEnd(FacesContext context) throws IOException {
	ResponseWriter writer = context.getResponseWriter();
	UIComponent actionComponent=super.getParent();
	String acionElement  = actionComponent.getClientId(context); 
	UIForm form= getForm(actionComponent);
	if (form != null) {
			
		String formId = form.getClientId(context);

		writer.startElement("script", null);
		String functionBody = 
		   "{var keycode;" + 
		   "if (window.event) keycode = window.event.keyCode;" +
		   "else if (event) keycode = event.which;" +
		   "else return true;" + 
		   "if (keycode == 13) { " +
		   "document.getElementById('"+acionElement+"').click();return false; } " +
		   "else  return true; }";

		String functionCode = 
		   "document.forms['"+formId+"'].onkeypress ="+
		   "new Function(\""+ functionBody+"\");"; 
		writer.write(functionCode);
			
		writer.endElement("script");
	}
}
	
 private UIForm getForm(UIComponent component) {
     while (component != null) {
        if (component instanceof UIForm) {
             break;
         }
         component = component.getParent();
     }
     return (UIForm) component;
 }

	
}
