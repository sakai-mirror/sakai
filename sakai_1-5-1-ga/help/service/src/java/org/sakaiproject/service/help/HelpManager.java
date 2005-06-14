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
 * $Header: /cvs/help/service/src/java/org/sakaiproject/service/help/HelpManager.java,v 1.5 2005/02/07 19:39:52 casong.indiana.edu Exp $
 * $Revision: 1.5 $
 * $Date: 2005/02/07 19:39:52 $
 */
package org.sakaiproject.service.help;

import org.osid.shared.Id;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.help.Glossary;
import org.sakaiproject.service.help.GlossaryEntry;
import org.sakaiproject.service.help.TableOfContents;
import org.sakaiproject.service.help.Resource;
import org.sakaiproject.service.help.Source;
import org.sakaiproject.service.help.Context;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Responsible for managing help activities.  This includes
 * managing contexts, resources, the glossary, and the
 * table of contents.
 *
 * @see org.theospi.portfolio.help.Glossary
 */
public interface HelpManager {
  
  public void init();
  
  public void destroy();
  
  public Category createCategory();
  
  public void storeCategory(Category category);
   /**
    * find all contexts associated with mappedView
    * @param mappedView
    * @return - list of contexts (String)
    */
   public List getContexts(String mappedView);

   /**
    * returns a list of all active contexts.  Active contexts
    * are created when the user navigates around the site.
    * @param session
    * @return
    */
   public List getActiveContexts(Map session);

   /**
    * adds a context to the active context list
    * @param session
    * @param mappedView
    */
   public void addContexts(Map session, String mappedView);

   /**
    *
    * @param context
    * @return set of resources associated with the supplied context
    */
   
   public Set getResources(Id context);

   public Resource getResource(Id id);
   
   public Resource createResource();
   
   public void storeResource(Resource resource);

   public void deleteResource(Id resourceId);

   public Source getSource(Id id);

   public void storeSource(Source source);

   public void deleteSource(Id sourceId);

   public Context getContext(Id id);

   public void storeContext(Context context);

   public void deleteContext(Id contextId);

   /**
    *
    * @param session
    * @return map of resources keyed by active contexts
    */
   public Map getResourcesForActiveContexts(Map session);

   /**
    *
    * @param query
    * @return set of resources found by searching with the supplied query.
    * @throws RuntimeException - if query can't be parsed
    */
   public Set searchResources(String query) throws RuntimeException;

   public TableOfContents getTableOfContents();

   /**
    * searches the glossary for the keyword.
    * Returns a GlossaryEntry for this keyword if found,
    * return null if no entry is found.
    * @param keyword
    * @return
    */
   public GlossaryEntry searchGlossary(String keyword);

   public Glossary getGlossary();

   public Resource getResourceByDocId(String helpDocIdString);

   public ServerConfigurationService getServerConfigurationService();
   
   public void setServerConfigurationService(
       ServerConfigurationService serverConfigurationService);
   
   public String getSupportEmailAddress();
}

