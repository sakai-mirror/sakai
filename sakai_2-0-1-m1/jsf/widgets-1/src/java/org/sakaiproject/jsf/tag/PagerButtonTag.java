/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets-1/src/java/org/sakaiproject/jsf/tag/PagerButtonTag.java,v 1.2 2005/05/10 03:11:04 esmiley.stanford.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/


package org.sakaiproject.jsf.tag;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

/**
 * This tag creates paging controls that allow for next X, previous X,
 * and changing the number of items per page.
 * This displays a set of labels but does not control the dataTable directly.
 * It posts to the form specified by formId, or to the enclosing form.
 * The item counting is 1-based (ie the first item is 1)</p>
 * <p>
 * Usage:
 Designed to get and set its parameters via value references in a backing bean from
 the back end.

required:
   firstItem the first item number (e.g. 1)
   lastItem the last item number (e.g. 10, the last item on the page)
   numItems number of items to show at one time (e.g. 5 at a time)
   totalItems total number of items (e.g. 100)

optional:
   formId If formId is not specified, the form enclosing this tag will be submitted.
   dataTableId this is a unique value for each UIData component -
   	if not specified, the paging controls will not directly manipulate the UIData
   prevDisabled if set to "true" will disable button
   nextDisabled if set to "true" will disable button
   prevText e.g "Previous" from a resource bundle
   nextText e.g. "Next" from a bundle (these are the only parts that can be localized)

 Note that if you are on "1" it will automatically disable the previous button,
 and if you are on totalItems it will automatically disable the next.
 Disabled buttons are greyed out.
</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id$
 */

public class PagerButtonTag
  extends UIComponentTag
{

  private String formId;
  private String firstItem;
  private String lastItem;
  private String dataTableId;
  private String prevText;
  private String nextText;
  private String numItems;
  private String prevDisabled;
  private String nextDisabled;
  private String totalItems;

  private TagUtil util;

  public String getComponentType()
  {
    return ("javax.faces.Output");
  }

  public String getRendererType()
  {
    return "PagerButton";
  }

  protected void setProperties(UIComponent component)
  {

    super.setProperties(component);

    if (!"true".equals(prevDisabled))
      prevDisabled = "false";
    if (!"true".equals(nextDisabled))
      nextDisabled = "false";
    component.getAttributes().put("dataTableId", dataTableId);
    component.getAttributes().put("formId", formId);
    TagUtil.setString(component, "name", "value");
	TagUtil.setString(component, "firstItem", firstItem);
	TagUtil.setString(component, "lastItem", lastItem);
	TagUtil.setString(component, "prevText", prevText);
	TagUtil.setString(component, "nextText", nextText);
	TagUtil.setString(component, "numItems", numItems);
	TagUtil.setString(component, "totalItems", totalItems);
    // we explicitly disable prev/next at end ranges
    if ("1".equals(firstItem)) prevDisabled = "true";
    if (("" + totalItems).equals(lastItem)) nextDisabled = "true";
	TagUtil.setString(component, "prevDisabled", prevDisabled);
	TagUtil.setString(component, "nextDisabled", nextDisabled);

  }

  /**
   * id of form to post to, placed in the name of the prev/next controls
   * @return the id
   */
  public String getFormId()
  {
    return formId;
  }

  /**
   * id of form to post to, placed in the name of the controls
   * @param formId id of form to post to
   */
  public void setFormId(String formId)
  {
    this.formId = formId;
  }

  /**
   * number of first item displayed
   * @return typically, a numeric string
   */
  public String getFirstItem()
  {
    return firstItem;
  }

  /**
   * number of first item displayed
   * @param firstItem number of first item displayed
   */
  public void setFirstItem(String firstItem)
  {
    this.firstItem = firstItem;
  }

  /**
   * number of last item displayed
   * @return number of first item displayed, typically numeric string
   */
  public String getLastItem()
  {
    return lastItem;
  }

  /**
   * number of first item displayed
   * @param lastItem number of first item displayed
   */

  public void setLastItem(String lastItem)
  {
    this.lastItem = lastItem;
  }

  /**
   * data table id, placed in the name of the controls for uniqueness
   * technically could be anything, but using the id of the dataTable is
   * best practice
   * @return data table id
   */
  public String getDataTableId()
  {
    return dataTableId;
  }

  /**
   * data table id, placed in the name of the controls for uniqueness
   * @param dataTableId data table id, placed in the name of the controls
   */
  public void setDataTableId(String dataTableId)
  {
    this.dataTableId = dataTableId;
  }

  /**
   * text for "Previous"
   * @return text for "Previous"
   */
  public String getPrevText()
  {
    return prevText;
  }

  /**
   * text for "Previous"
   * @param prevText text for "Previous"
   */
  public void setPrevText(String prevText)
  {
    this.prevText = prevText;
  }

  /**
   * text for "Next"
   * @return text for "Next"
   */
  public String getNextText()
  {
    return nextText;
  }

  /**
   * text for "Next"
   * @param nextText text for "Next"
   */
  public void setNextText(String nextText)
  {
    this.nextText = nextText;
  }

  /**
   * number of items string, how many iems in the datatable, selected option
   * @return number of items string
   */
  public String getNumItems()
  {
    return numItems;
  }

  /**
   * number of items string, how many iems in the datatable, selected option
   * @param numItems number of items string
   */
  public void setNumItems(String numItems)
  {
    this.numItems = numItems;
  }
  /**
   * "true" if previous control disabled
   * @return "true" if previous control disabled
   */
  public String getPrevDisabled()
  {
    return prevDisabled;
  }

  /**
   * "true" if previous control to be disabled, otehrwise ignored
   * @param prevDisabled "true" if previous control disabled
   */
  public void setPrevDisabled(String prevDisabled)
  {
    this.prevDisabled = prevDisabled;
  }

  /**
   * "true" if next control disabled
   * @return "true" if next control disabled
   */
  public String getNextDisabled()
  {
    return nextDisabled;
  }

  /**
   * "true" if next control disabled
   * @param nextDisabled "true" if next control disabled
   */
  public void setNextDisabled(String nextDisabled)
  {
    this.nextDisabled = nextDisabled;
  }

  /**
   * display total items
   * @return total items, typically a numeric string
   */
  public String getTotalItems()
  {
    return totalItems;
  }

  /**
   * total items to display as "out of n total items"
   * @param totalItems total items, typically a numeric string
   */
  public void setTotalItems(String totalItems)
  {
    this.totalItems = totalItems;
  }


}

/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets-1/src/java/org/sakaiproject/jsf/tag/PagerButtonTag.java,v 1.2 2005/05/10 03:11:04 esmiley.stanford.edu Exp $
*
**********************************************************************************/
