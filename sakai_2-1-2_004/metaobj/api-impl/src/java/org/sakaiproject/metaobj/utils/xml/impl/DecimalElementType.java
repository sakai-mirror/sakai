/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.metaobj.utils.xml.impl;

import org.jdom.Element;
import org.jdom.Namespace;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 19, 2004
 * Time: 10:09:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class DecimalElementType extends NumberElementType {

   private Format format = null;
   private int fractionDigits = -1;

   public DecimalElementType(String typeName, Element schemaElement,
                             SchemaNode parentNode, Namespace xsdNamespace) {
      super(typeName, schemaElement, parentNode, xsdNamespace);

      Element simpleType = schemaElement.getChild("simpleType", xsdNamespace);
      if (simpleType != null) {
         Element restrictions = simpleType.getChild("restriction", xsdNamespace);
         if (restrictions != null) {
            // process restrictions
            fractionDigits = processIntRestriction(restrictions, "fractionDigits", xsdNamespace, fractionDigits);

            if (fractionDigits != -1) {
               ((DecimalFormat) getFormatter()).setMaximumFractionDigits(fractionDigits);
               ((DecimalFormat) getFormatter()).setMinimumFractionDigits(fractionDigits);
            }
         }
      }

   }


   protected Format getFormatter() {
      if (format == null) {
         format = new DecimalFormat();
      }
      return format;
   }

   protected Object checkConstraints(Object o) {

      double i = ((Number) o).doubleValue();

      if (maxIncl != null && i > maxIncl.doubleValue()) {
         throw new NormalizationException("Invalid number",
               NormalizationException.TOO_LARGE_INCLUSIVE_ERROR_CODE, new Object[]{o, maxIncl});
      }

      if (minIncl != null && i < minIncl.doubleValue()) {
         throw new NormalizationException("Invalid number",
               NormalizationException.TOO_SMALL_INCLUSIVE_ERROR_CODE, new Object[]{o, minIncl});
      }

      if (maxExcl != null && i >= maxExcl.doubleValue()) {
         throw new NormalizationException("Invalid number",
               NormalizationException.TOO_LARGE_ERROR_CODE, new Object[]{o, maxExcl});
      }

      if (minExcl != null && i <= minExcl.doubleValue()) {
         throw new NormalizationException("Invalid number",
               NormalizationException.TOO_SMALL_ERROR_CODE, new Object[]{o, minExcl});
      }

      if (totalDigits != -1 && o.toString().length() > totalDigits) {
         throw new NormalizationException("Invalid number",
               NormalizationException.TOO_MANY_DIGITS_ERROR_CODE, new Object[]{o, new Integer(totalDigits)});
      }

      return o;
   }

   protected String parserException(String value, ParseException e) {
      throw new NormalizationException("Invalid number",
            NormalizationException.INVALID_DECIMAL_NUMBER_ERROR_CODE, new Object[]{value});
   }

   public Class getObjectType() {
      return Double.class;
   }

}
