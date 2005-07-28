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
package org.sakaiproject.metaobj.shared.mgt.factories;

import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactDefinition;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;

import java.util.Map;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 9, 2004
 * Time: 12:51:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class DbXmlHomeFactoryImpl extends HomeFactoryBase implements HomeFactory {

   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;

   public boolean handlesType(String objectType) {
      return (getHome(objectType) != null);
   }

   public Map getHomes(Class requiredHomeType) {
      return super.getHomes(requiredHomeType);
   }

   public ReadableObjectHome findHomeByExternalId(String externalId, Id worksiteId) {
      return createHome(getStructuredArtifactDefinitionManager().loadHomeByExternalType(externalId, worksiteId));
   }

   public ReadableObjectHome getHome(String objectType) {
      return createHome(getStructuredArtifactDefinitionManager().loadHome(objectType));
   }

   public Map getWorksiteHomes(Id worksiteId) {
      return getStructuredArtifactDefinitionManager().getWorksiteHomes(worksiteId);
   }

   public Map getHomes() {
      return getStructuredArtifactDefinitionManager().getHomes();
   }

   protected Map createHomes(Map homeBeans) {
      for (Iterator i=homeBeans.entrySet().iterator();i.hasNext();) {
         Map.Entry entry = (Map.Entry)i.next();
         entry.setValue(createHome((StructuredArtifactDefinitionBean) entry.getValue()));
      }
      return homeBeans;
   }

   protected ReadableObjectHome createHome(StructuredArtifactDefinitionBean sadBean) {
      return new StructuredArtifactDefinition(sadBean);
   }

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

}
