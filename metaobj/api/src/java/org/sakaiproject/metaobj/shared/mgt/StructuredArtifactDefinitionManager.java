package org.sakaiproject.metaobj.shared.mgt;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
 * $Header
 * $Revision
 * $Date
 */

public interface StructuredArtifactDefinitionManager {
   public final static String TOOL_GLOBAL_SAD = "theospi.share.sad.global";
   public final static String GLOBAL_SAD_QUALIFIER = "theospi.share.sad.global";

   public Map getHomes();

   /**
    *
    * @param worksiteId
    * @return a map with all worksite and global homes
    */
   public Map getWorksiteHomes(Id worksiteId);

   public List findHomes();

   /**
    *
    * @return list of all published globals or global sad owned by current user
    */
   public List findGlobalHomes();

   /**
    * @param currentWorksiteId
    * @return list of globally published sads or published sad in currentWorksiteId or sads in
    *         currentWorksiteId owned by current user
    */
   public List findHomes(Id currentWorksiteId);
         
   public StructuredArtifactDefinitionBean loadHome(String type);

   public StructuredArtifactDefinitionBean loadHome(Id id);

   public StructuredArtifactDefinitionBean loadHomeByExternalType(String externalType, Id worksiteId);

   public StructuredArtifactDefinitionBean save(StructuredArtifactDefinitionBean sad);

   /**
    *
    * @return true if user is in a SAD tool that is configured to manipulate globals SADs
    */
   public boolean isGlobal();

   public Collection getRootElements(StructuredArtifactDefinitionBean sad);

   public void validateSchema(StructuredArtifactDefinitionBean sad);

   public StructuredArtifactHomeInterface convertToHome(StructuredArtifactDefinitionBean sad);

}
