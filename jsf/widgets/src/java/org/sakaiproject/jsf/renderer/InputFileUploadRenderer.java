/*******************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/jsf/widgets/src/java/org/sakaiproject/jsf/renderer/InputFileUploadRenderer.java $ 
 * $Id: InputFileUploadRenderer.java 189 2005-06-23 17:37:18Z janderse@umich.edu $
 * **********************************************************************************
 * 
 * Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees
 * of Indiana University, Board of Trustees of the Leland Stanford, Jr.,
 * University, and The MIT Corporation
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you
 * have read, understand, and will comply with the terms and conditions of the
 * Educational Community License. You may obtain a copy of the License at:
 * 
 * http://cvs.sakaiproject.org/licenses/license_1_0.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *  
 ******************************************************************************/

package org.sakaiproject.jsf.renderer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.sakaiproject.jsf.util.RendererUtil;

public class InputFileUploadRenderer extends Renderer
{
    private static final String ID_INPUT_ELEMENT = ".uploadId";

    private static final String ID_HIDDEN_ELEMENT = ".hiddenId";

    private static final String ATTR_REQUEST_DECODED = ".decoded";

    private static final String[] PASSTHROUGH_ATTRIBUTES = { "accept", "accesskey",
            "align", "disabled", "maxlength", "readonly", "size", "style", "tabindex" };

    public static final String ATTR_UPLOADS_DONE = "sakai.uploads.done";

    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException
    {
        if (!component.isRendered()) return;
 
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        // check that the structure of the form is valid
        boolean atDecodeTime = false;
        String errorMessage = checkForErrors(context, component, clientId, atDecodeTime);
        if (errorMessage != null)
        {
            addFacesMessage(context, clientId, errorMessage);
        }

        // output field that allows user to upload a file
        writer.startElement("input", null);
        writer.writeAttribute("type", "file", null);
        writer.writeAttribute("name", clientId + ID_INPUT_ELEMENT, null);
        String styleClass = (String) RendererUtil.getAttribute(context, component, "styleClass");
        if (styleClass != null) writer.writeAttribute("class", styleClass, null);
        boolean writeNullPassthroughAttributes = false;
        RendererUtil.writePassthroughAttributes(PASSTHROUGH_ATTRIBUTES,
                writeNullPassthroughAttributes, context, component);
        writer.endElement("input");

        // comment
        // output hidden field that helps test that the filter is working right
        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("name", clientId + ID_HIDDEN_ELEMENT, null);
        writer.writeAttribute("value", "filter_is_functioning_properly", null);
        writer.endElement("input");
    }

    public void decode(FacesContext context, UIComponent comp)
    {
        UIInput component = (UIInput) comp;
        if (!component.isRendered()) return;

        ExternalContext external = context.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) external.getRequest();
        String clientId = component.getClientId(context);
        String directory = (String) RendererUtil.getAttribute(context, component, "directory");
        
        // mark that this component has had decode() called during request
        // processing
        request.setAttribute(clientId + ATTR_REQUEST_DECODED, "true");

        // check for user errors and developer errors
        boolean atDecodeTime = true;
        String errorMessage = checkForErrors(context, component, clientId, atDecodeTime);
        if (errorMessage != null)
        {
            addFacesMessage(context, clientId, errorMessage);
            return;
        }

        FileItem item = getFileItem(context, component);
        
        if (item.getName() == null || item.getName().length() == 0)
        {
            if (component.isRequired())
            {
                addFacesMessage(context, clientId, "Please specify a file.");
                component.setValid(false);
            }
            return;
        }
 
        //System.out.println("inputFileUpload: item: " + item + " name: " + item.getName() + " length: " + item.getSize());

        if (directory == null || directory.length() == 0)
        {
            // just passing on the FileItem as the value of the component, without persisting it.
            component.setSubmittedValue(item);
        }
        else
        {
            // persisting to a permenent file in a directory.
            // pass on the server-side filename as the value of the component.
            File dir = new File(directory); 
            String filename = item.getName();
            filename = filename.replace('\\','/'); // replaces Windows path seperator character "\" with "/"
            filename = filename.substring(filename.lastIndexOf("/")+1);
            File persistentFile = new File(dir, filename);
            try
            {
                item.write(persistentFile);
                component.setSubmittedValue(persistentFile.getPath());
            }
            catch (Exception ex)
            {
                throw new FacesException(ex);
            }
         }

    }


    /**
     * Check for errors (both developer errors and user errors) - return a
     * user-friendly error message describing the error, or null if there are no
     * errors.
     */
    private static String checkForErrors(FacesContext context, UIComponent component,
            String clientId, boolean atDecodeTime)
    {
        ExternalContext external = context.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) external.getRequest();

        // check form structure
        UIForm form = getForm(component);
        if (form == null || !"multipart/form-data".equals(RendererUtil.getAttribute(context, form, "enctype")))
        {
            return "DEVELOPER ERROR: The <inputFileUpload> tag must be enclosed in a <h:form enctype=\"multipart/form-data\"> tag.";
        }
        
        // check tag attributes
        String directory = (String) RendererUtil.getAttribute(context, component, "directory");
        if (directory != null && directory.length() != 0)
        {
            // the tag is configured to persist the uploaded files to a directory.
            // check that the specified directory exists, and is writeable
            File dir = new File(directory);
            if (!dir.isDirectory() || !dir.exists())
            {
                return "DEVELOPER ERROR: The directory specified on the <inputFileUpload> tag does not exist or is not writable.\n"
                + "Check the permissions on directory:\n"
                + dir;
            }
        }

        FileItem item = getFileItem(context, component);
        boolean isMultipartRequest = request.getContentType() != null && request.getContentType().startsWith("multipart/form-data");
        boolean wasMultipartRequestFullyParsed = request.getParameter(clientId + ID_HIDDEN_ELEMENT) != null;
        boolean didRequestFilterHandleUpload = request.getAttribute(ATTR_UPLOADS_DONE) != null;
        String requestFilterStatus = (String) request.getAttribute("upload.status");
        Object requestFilterUploadLimit = request.getAttribute("upload.limit");
        Exception requestFilterException = (Exception) request.getAttribute("upload.exception");
        boolean wasDecodeAlreadyCalledOnTheRequest = "true".equals(request.getAttribute(clientId + ATTR_REQUEST_DECODED));
        
        if (wasDecodeAlreadyCalledOnTheRequest && !atDecodeTime)
        {
            // decode() was already called on the request, and we're now at encode() time - so don't do further error checking
            // as the FileItem may no longer be valid.
            return null;
        }
        
        // at this point, if its not a multipart request, it doesn't have a file and there isn't an error.
        if (!isMultipartRequest) return null;

        // check for user errors
        if ("exception".equals(requestFilterStatus))
        {
            return "An error occured while processing the uploaded file.  The error was:\n"
                    + requestFilterException;
        }
        else if ("size_limit_exceeded".equals(requestFilterStatus))
        {
            return "The upload size limit of " + requestFilterUploadLimit + "MB has been exceeded.";
        }        
        else if (item != null && (item.getName() == null || "".equals(item.getName())))
        {
            // TODO: What to do in this case?  the form was submitted without a file.  
            return null;
            //return "USER ERROR: No filename?";
        }
        else if (item != null && item.getSize() == 0)
        {
            return "The filename '"+item.getName()+"' is invalid.  Please select a valid file.";
        }
        
        if (!wasMultipartRequestFullyParsed || item == null)
        {
            return "An error occured while processing the uploaded file.  The error was:\n"
            + "DEVELOPER ERROR: The <inputFileUpload> tag requires a <filter> in web.xml to parse the uploaded file.\n"
            + "Check that the Sakai RequestFilter is properly configured in web.xml.";
        }
        
        if (item.getName().indexOf("..") >= 0)
        {
            return "The filename '"+item.getName()+"' is invalid.  Please select a valid file.";
        }
        
        // at this point, if the FileItem is available and has a name and a length, then there isn't an error.
        if (item != null && item.getName() != null && item.getName().length() != 0 && item.getSize() > 0)
        {
            // everything checks out fine! The upload was parsed, and a FileItem
            // exists with a filename and non-zero length
            return null;
        }
        else
        {
            return "An unknown error occured while processing the uploaded file.  Please try again.";
        }
    }

    /**
     * Return the FileItem (if present) for the given component.  Subclasses
     * of this Renderer could get the FileItem in a different way.
     * First, try getting it from the request attributes (Sakai style).  Then
     * try getting it from a method called getFileItem() on the HttpServletRequest
     * (unwrapping the request if necessary).
     */
    private static FileItem getFileItem(FacesContext context, UIComponent component)
    {
        String clientId = component.getClientId(context);
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        FileItem item = null;
        String fieldName = clientId + ID_INPUT_ELEMENT;
        
        // first try just getting it from the request attributes,
        // where the Sakai RequestFilter puts it.
        item = (FileItem) request.getAttribute(fieldName);
        if (item != null) return item;
        
        // For custom filter compatibility (MyFaces for example), 
        // walk up the HttpServletRequestWrapper chain looking for a getFileItem() method.
        while (request != null)
        {
	        // call the getFileItem() method by reflection, so as to not introduce a dependency
	        // on MyFaces, and so the wrapper class that has getFileItem() doesn't have to
	        // implement an interface (as long as it has the right method signature it'll work).
	        try
	        {
	            Class reqClass = request.getClass();
	            Method getFileItemMethod = reqClass.getMethod("getFileItem", new Class[] {String.class});
	            Object returned = getFileItemMethod.invoke(request, new Object[] {fieldName});
	            if (returned instanceof FileItem) return (FileItem) returned;
	        }
	        catch (NoSuchMethodException nsme)
	        {
	        }
	        catch (InvocationTargetException ite)
	        {
	        }
	        catch (IllegalArgumentException iae)
	        {
	        }
	        catch (IllegalAccessException iaxe)
	        {
	        }
	        
	        // trace up the request wrapper classes, looking for a getFileItem() method
	        if (request instanceof HttpServletRequestWrapper)
	        {
	            request = (HttpServletRequest) ((HttpServletRequestWrapper) request).getRequest();
	        }
	        else
	        {
	            request = null;
	        }
        }
        
        return null;
    }
    
    private static void addFacesMessage(FacesContext context, String clientId,
            String message)
    {
        context.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                message, message));
        System.out.println(message);
    }

    /**
     * get containing UIForm from component hierarchy.
     */
    private static UIForm getForm(UIComponent component)
    {
        while (component != null)
        {
            if (component instanceof UIForm) return (UIForm) component;
            component = component.getParent();
        }

        return null;
    }
}