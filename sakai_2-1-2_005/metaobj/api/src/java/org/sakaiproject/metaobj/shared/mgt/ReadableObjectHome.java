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
package org.sakaiproject.metaobj.shared.mgt;

import org.sakaiproject.metaobj.shared.model.*;

import java.io.InputStream;
import java.util.Collection;

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
    *
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
    * re-initialize any configuration
    */
   public void refresh();

   public String getExternalUri(Id artifactId, String name);

   public InputStream getStream(Id artifactId);

   public boolean isSystemOnly();

   public Class getInterface();
}
