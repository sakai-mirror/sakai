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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.jdom.Element;

public interface StructuredArtifactDefinitionManager {
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

   public boolean importSADResource(Id worksiteId, String resourceId, boolean findExisting)
         throws IOException, ServerOverloadException;

   public void packageFormForExport(String formId, OutputStream os) throws IOException;

   public StructuredArtifactDefinitionBean importSad(Id worksiteId, InputStream in,
                                                     boolean findExisting, boolean publish)
         throws IOException;

   public Element createFormViewXml(String formId, String returnUrl);
}
