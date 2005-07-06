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
 * $Header: /opt/CVS/osp2.x/HomesService/src/java/org/theospi/metaobj/shared/mgt/ReadableObjectHome.java,v 1.1 2005/06/28 13:09:17 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/28 13:09:17 $
 */
package org.sakaiproject.metaobj.shared.mgt;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.FinderException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.Type;

import java.util.Collection;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 8, 2004
 * Time: 5:16:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ReadableObjectHome {

   public Type getType();


   /**
    * Used to get an externally unique type to identify this home
    * across running osp instances
    * @return an externally unique type suitable for storage and later import
    */
   public String getExternalType();

   /**
    * Load the object from the
    * backing store.
    *
    * @param id Uniquely identifies the object.
    * @return The loaded object
    */
   public Artifact load(Id id) throws PersistenceException;

   /**
    * Creates an empty instance of this home's object
    *
    * @return An empty object instance
    */
   public Artifact createInstance();

   public void prepareInstance(Artifact object);

   /**
    * Creates a sample instance of the
    * object with each field filled in with some
    * representative data.
    *
    * @return An object instance with sample data filled in.
    */
   public Artifact createSample();

   /**
    * Find all the instances of this home's
    * objects that are owned by the supplied owner.
    * How do we handle permissions here?
    *
    * @param owner The owner in question.
    * @return A list of objects.
    */
   public Collection findByOwner(Agent owner) throws FinderException;

   /**
    * Determines if the supplied object is handled by this home.
    *
    * @param testObject the object to be tested.
    * @return true if the supplied object is handled by this home
    */
   public boolean isInstance(Artifact testObject);

   /**
    *  re-initialize any configuration
    */
   public void refresh();

   public String getExternalUri(Id artifactId, String name);

   public InputStream getStream(Id artifactId);

   public boolean isSystemOnly();

   public Class getInterface();
}
