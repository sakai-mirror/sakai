/**********************************************************************************
* $URL$
* $Id$
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

import org.sakaiproject.jsf.util.TagUtil;


public class PagerTag
  extends UIComponentTag
{

  private String totalItems;
  private String firstItem;
  private String pageSize;
  private String value;
  private String valueChangeListener;
  private String pageSizes;
  private String accesskeys;
  private String renderFirst;
  private String renderPrev;
  private String renderNext;
  private String renderLast;
  private String renderPageSize;
  private String textFirst;
  private String textPrev;
  private String textNext;
  private String textLast;
  private String textPageSize;
  private String textStatus;
  private String textItem;
  private String immediate;

  public String getComponentType()
  {
    return ("org.sakaiproject.Pager");
  }

  public String getRendererType()
  {
    return ("org.sakaiproject.Pager");
  }

  protected void setProperties(UIComponent component)
  {

    super.setProperties(component);

    TagUtil.setInteger(component, "totalItems", totalItems);
    TagUtil.setInteger(component, "firstItem", firstItem);
    TagUtil.setInteger(component, "pageSize", pageSize);
    TagUtil.setString(component, "value", value);
    TagUtil.setValueChangeListener(component, valueChangeListener);
    TagUtil.setString(component, "pageSizes", pageSizes);
    TagUtil.setBoolean(component, "accesskeys", accesskeys);
    TagUtil.setBoolean(component, "renderFirst", renderFirst);
    TagUtil.setBoolean(component, "renderPrev", renderPrev);
    TagUtil.setBoolean(component, "renderNext", renderNext);
    TagUtil.setBoolean(component, "renderLast", renderLast);
    TagUtil.setBoolean(component, "renderPageSize", renderPageSize);
    TagUtil.setString(component, "textFirst", textFirst);
    TagUtil.setString(component, "textPrev", textPrev);
    TagUtil.setString(component, "textNext", textNext);
    TagUtil.setString(component, "textLast", textLast);
    TagUtil.setString(component, "textPageSize", textPageSize);
    TagUtil.setString(component, "textStatus", textStatus);
    TagUtil.setString(component, "textItem", textItem);
    TagUtil.setBoolean(component, "immediate", immediate);
  }

  public void release()
  {
  	super.release();
    totalItems = null;
    firstItem = null;
    pageSize = null;
    value = null;
    valueChangeListener = null;
    pageSizes = null;
    accesskeys = null;
    renderFirst = null;
    renderPrev = null;
    renderNext = null;
    renderLast = null;
    renderPageSize = null;
    textFirst = null;
    textPrev = null;
    textNext = null;
    textLast = null;
    textPageSize = null;
    textStatus = null;
    textItem = null;
    immediate = null;
  }

/** Below: Automatically generated getters and setters */


public String getAccesskeys() {
	return accesskeys;
}
public void setAccesskeys(String accesskeys) {
	this.accesskeys = accesskeys;
}
public String getFirstItem() {
	return firstItem;
}
public void setFirstItem(String firstItem) {
	this.firstItem = firstItem;
}
public String getImmediate() {
	return immediate;
}
public void setImmediate(String immediate) {
	this.immediate = immediate;
}
public String getPageSize() {
	return pageSize;
}
public void setPageSize(String pageSize) {
	this.pageSize = pageSize;
}
public String getPageSizes() {
	return pageSizes;
}
public void setPageSizes(String pageSizes) {
	this.pageSizes = pageSizes;
}
public String getRenderFirst() {
	return renderFirst;
}
public void setRenderFirst(String renderFirst) {
	this.renderFirst = renderFirst;
}
public String getRenderLast() {
	return renderLast;
}
public void setRenderLast(String renderLast) {
	this.renderLast = renderLast;
}
public String getRenderNext() {
	return renderNext;
}
public void setRenderNext(String renderNext) {
	this.renderNext = renderNext;
}
public String getRenderPageSize() {
	return renderPageSize;
}
public void setRenderPageSize(String renderPageSize) {
	this.renderPageSize = renderPageSize;
}
public String getRenderPrev() {
	return renderPrev;
}
public void setRenderPrev(String renderPrev) {
	this.renderPrev = renderPrev;
}
public String getTextFirst() {
	return textFirst;
}
public void setTextFirst(String textFirst) {
	this.textFirst = textFirst;
}
public String getTextItem() {
	return textItem;
}
public void setTextItem(String textItem) {
	this.textItem = textItem;
}
public String getTextLast() {
	return textLast;
}
public void setTextLast(String textLast) {
	this.textLast = textLast;
}
public String getTextNext() {
	return textNext;
}
public void setTextNext(String textNext) {
	this.textNext = textNext;
}
public String getTextPageSize() {
	return textPageSize;
}
public void setTextPageSize(String textPageSize) {
	this.textPageSize = textPageSize;
}
public String getTextPrev() {
	return textPrev;
}
public void setTextPrev(String textPrev) {
	this.textPrev = textPrev;
}
public String getTextStatus() {
	return textStatus;
}
public void setTextStatus(String textStatus) {
	this.textStatus = textStatus;
}
public String getTotalItems() {
	return totalItems;
}
public void setTotalItems(String totalItems) {
	this.totalItems = totalItems;
}
public String getValue() {
	return value;
}
public void setValue(String value) {
	this.value = value;
}
public String getValueChangeListener() {
	return valueChangeListener;
}
public void setValueChangeListener(String valueChangeListener) {
	this.valueChangeListener = valueChangeListener;
}
}
