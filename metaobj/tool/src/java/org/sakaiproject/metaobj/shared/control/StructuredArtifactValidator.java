/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/metaobj/shared/control/StructuredArtifactValidator.java,v 1.2 2005/06/30 20:32:40 jellis Exp $
 * $Revision: 1.2 $
 * $Date: 2005/06/30 20:32:40 $
 */
package org.sakaiproject.metaobj.shared.control;

import org.springframework.validation.Errors;
import org.sakaiproject.metaobj.shared.model.ElementBean;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 19, 2004
 * Time: 3:31:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredArtifactValidator extends XmlValidator {


   public boolean supports(Class clazz) {
      if (super.supports(clazz)) {
         return true;
      }
      //return (StructuredArtifact.class.isAssignableFrom(clazz));
      return true;
   }

   public void validate(Object obj, Errors errors) {
      validateInternal(obj, errors);
      super.validate(obj, errors);
   }

   protected void validateInternal(Object obj, Errors errors) {
//      if (obj instanceof StructuredArtifact) {
//         StructuredArtifact artifact = (StructuredArtifact) obj;
//
//         if (artifact.getDisplayName() == null ||
//            artifact.getDisplayName().length() == 0) {
//            errors.rejectValue("displayName", "required value {0}", new Object[]{"displayName"},
//               "required value displayName");
//         }
//      }
   }

   public void validate(Object obj, Errors errors, boolean checkListNumbers) {
      validateInternal(obj, errors);
      super.validate(obj, errors, checkListNumbers);
   }

   protected void validateDisplayName(ElementBean elementBean, Errors errors) {
//      if (elementBean instanceof StructuredArtifact) {
//
//         String displayName = (String)elementBean.get("displayName");
//
//         if (getFileNameValidator() != null && displayName != null) {
//            if (!getFileNameValidator().validFileName(displayName)) {
//               errors.rejectValue("displayName", "Invalid display name {0}",
//                  new Object[]{displayName},
//                  MessageFormat.format("Invalid display name {0}", new Object[]{displayName}));
//            }
//         }
//      }
   }
}
