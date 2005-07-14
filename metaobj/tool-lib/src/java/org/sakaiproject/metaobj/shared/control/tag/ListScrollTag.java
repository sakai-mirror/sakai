/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.shared.control.tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.tag.el.core.ExpressionUtil;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public class ListScrollTag extends BodyTagSupport {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String listUrl;
   private ListScroll listScroll;
   private String listScrollExpression = DEFAULT_LIST_SCROLL;
   private static final String DEFAULT_LIST_SCROLL = "${listScroll}";
   private String className;

   /**
    * Default processing of the start tag returning EVAL_BODY_BUFFERED.
    *
    * @return EVAL_BODY_BUFFERED
    * @throws javax.servlet.jsp.JspException if an error occurred while processing this tag
    * @see javax.servlet.jsp.tagext.BodyTag#doStartTag
    */

   public int doStartTag() throws JspException {
      evaluateExpressions();
      return EVAL_BODY_BUFFERED;
   }

   /**
    * Default processing of the end tag returning EVAL_PAGE.
    *
    * @return EVAL_PAGE
    * @throws javax.servlet.jsp.JspException if an error occurred while processing this tag
    * @see javax.servlet.jsp.tagext.Tag#doEndTag
    */

   public int doEndTag() throws JspException {
      JspWriter writer = pageContext.getOut();


      try {

         writer.write("<div ");
         if (className != null) {
            writer.write("class=\""+className+"\"");
         }
         writer.write(">");

         //  <input type="button" value="Next" onclick="window.document.location='url'">

         writer.write("<input type=\"button\" value=\"First\" onclick=\"window.document.location=\'");
         writer.write(listUrl + "&"+ListScroll.STARTING_INDEX_TAG+"=0");
         writer.write("\'\"");
         if (listScroll.getPrevIndex() == -1) {
            writer.write(" disabled=\"disabled\" ");
         }
         writer.write(" >");

         writer.write("&nbsp;");

         writer.write("<input type=\"button\" value=\"Previous\" onclick=\"window.document.location=\'");
         writer.write(listUrl + "&"+ListScroll.STARTING_INDEX_TAG+"=" + listScroll.getPrevIndex());
         writer.write("\'\"");
         if (listScroll.getPrevIndex() == -1) {
            writer.write(" disabled=\"disabled\" ");
         }
         writer.write(" >");

         writer.write("&nbsp;");
         writer.write(" viewing " + listScroll.getFirstItem() +
                        " - " +
                        listScroll.getLastItem() + " of " + listScroll.getTotal() + " items ");
         writer.write("&nbsp;");

         writer.write("<input type=\"button\" value=\"Next\" onclick=\"window.document.location=\'");
         writer.write(listUrl + "&" + ListScroll.STARTING_INDEX_TAG + "=" + listScroll.getNextIndex());
         writer.write("\'\"");
         if (listScroll.getNextIndex() == -1) {
            writer.write(" disabled=\"disabled\" ");
         }
         writer.write(" >");

         writer.write("&nbsp;");

         writer.write("<input type=\"button\" value=\"Last\" onclick=\"window.document.location=\'");
         writer.write(listUrl + "&" + ListScroll.STARTING_INDEX_TAG + "=" + Integer.MAX_VALUE);
         writer.write("\'\"");
         if (listScroll.getNextIndex() == -1) {
            writer.write(" disabled=\"disabled\" ");
         }
         writer.write(" >");

         writer.write("</div>");

         writer.write("<br />");

      } catch (IOException e) {
         logger.error("", e);
         throw new JspException(e);
      }

      listScroll = null;
      listScrollExpression = DEFAULT_LIST_SCROLL;
      listUrl = null;

      return EVAL_PAGE;
   }

   /**
    * Release state.
    *
    * @see javax.servlet.jsp.tagext.Tag#release
    */

   public void release() {
      super.release();
      listScroll = null;
      listScrollExpression = DEFAULT_LIST_SCROLL;
      listUrl = null;
   }

   protected void evaluateExpressions() throws JspException {
      if (listScroll == null) {
         listScroll = (ListScroll)ExpressionUtil.evalNotNull(
	         "listScroll", "listScroll", listScrollExpression,
            ListScroll.class, this, pageContext);
      }

      listUrl = (String)ExpressionUtil.evalNotNull("listScroll",
         "listUrl", listUrl, String.class, this, pageContext);
   }

   public void setListScroll(String listScrollExpression) {
      this.listScrollExpression = listScrollExpression;
   }

   public void setListScroll(ListScroll listScroll) {
      this.listScroll = listScroll;
   }

   public void setListUrl(String listUrl) {
      this.listUrl = listUrl;
   }

   public void setClassName(String className) {
      this.className = className;
   }
}
