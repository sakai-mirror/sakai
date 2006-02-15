/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.service.legacy.content.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;
import org.sakaiproject.exception.IdInvalidException;

/**
* <p>ContentHostingService is a static Cover for the {@link org.sakaiproject.service.legacy.content.ContentHostingService ContentHostingService};
* see that interface for usage details.</p>
* 
* @author Sakai Software Development Team
*/
public class ContentHostingService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.content.ContentHostingService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.content.ContentHostingService) ComponentManager.get(org.sakaiproject.service.legacy.content.ContentHostingService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.content.ContentHostingService) ComponentManager.get(org.sakaiproject.service.legacy.content.ContentHostingService.class);
		}
	}

	private static org.sakaiproject.service.legacy.content.ContentHostingService m_instance = null;

	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.content.ContentHostingService.SERVICE_NAME;
	public static java.lang.String REFERENCE_ROOT = org.sakaiproject.service.legacy.content.ContentHostingService.REFERENCE_ROOT;
	public static java.lang.String EVENT_RESOURCE_ADD = org.sakaiproject.service.legacy.content.ContentHostingService.EVENT_RESOURCE_ADD;
	public static java.lang.String EVENT_RESOURCE_READ = org.sakaiproject.service.legacy.content.ContentHostingService.EVENT_RESOURCE_READ;
	public static java.lang.String EVENT_RESOURCE_WRITE = org.sakaiproject.service.legacy.content.ContentHostingService.EVENT_RESOURCE_WRITE;
	public static java.lang.String EVENT_RESOURCE_REMOVE = org.sakaiproject.service.legacy.content.ContentHostingService.EVENT_RESOURCE_REMOVE;
	public static java.lang.String EVENT_DROPBOX_OWN = org.sakaiproject.service.legacy.content.ContentHostingService.EVENT_DROPBOX_OWN;
	public static java.lang.String EVENT_DROPBOX_MAINTAIN = org.sakaiproject.service.legacy.content.ContentHostingService.EVENT_DROPBOX_MAINTAIN;
	public static java.lang.String PROP_ALTERNATE_REFERENCE = org.sakaiproject.service.legacy.content.ContentHostingService.PROP_ALTERNATE_REFERENCE;

	public static java.lang.String getUrl(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.getUrl(param0);
	}

   /**
    *
    * @param id id of the resource to set the UUID for
    * @param uuid the new UUID of the resource
    * @throws org.sakaiproject.exception.IdInvalidException if the given resource already has a UUID set
    */
   public static void setUuid(String id, String uuid) throws IdInvalidException {
      org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
      if (service == null)
         return;

      service.setUuid(id, uuid);
   }

	public static java.lang.String getReference(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.getReference(param0);
	}

	public static org.sakaiproject.service.legacy.content.ContentCollection getCollection(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.TypeException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.getCollection(param0);
	}

	public static boolean allowAddCollection(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddCollection(param0);
	}

	public static org.sakaiproject.service.legacy.content.ContentCollection addCollection(java.lang.String param0, org.sakaiproject.service.legacy.entity.ResourceProperties param1) throws org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InconsistentException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.addCollection(param0, param1);
	}

	public static org.sakaiproject.service.legacy.content.ContentCollectionEdit addCollection(java.lang.String param0) throws org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InconsistentException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.addCollection(param0);
	}

	public static boolean allowGetCollection(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.allowGetCollection(param0);
	}

	public static void checkCollection(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.TypeException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return;

		service.checkCollection(param0);
	}

	public static java.util.List getAllResources(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.getAllResources(param0);
	}

	public static boolean allowUpdateCollection(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdateCollection(param0);
	}

	public static org.sakaiproject.service.legacy.content.ContentCollectionEdit editCollection(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.TypeException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.editCollection(param0);
	}

	public static boolean allowRemoveCollection(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveCollection(param0);
	}

	public static void removeCollection(org.sakaiproject.service.legacy.content.ContentCollectionEdit param0) throws org.sakaiproject.exception.TypeException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InconsistentException, org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return;

		service.removeCollection(param0);
	}

	public static void removeCollection(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.TypeException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InUseException, org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return;

		service.removeCollection(param0);
	}

	public static void commitCollection(org.sakaiproject.service.legacy.content.ContentCollectionEdit param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return;

		service.commitCollection(param0);
	}

	public static void cancelCollection(org.sakaiproject.service.legacy.content.ContentCollectionEdit param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return;

		service.cancelCollection(param0);
	}

	public static boolean allowAddResource(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddResource(param0);
	}

	public static org.sakaiproject.service.legacy.content.ContentResource addResource(java.lang.String param0, java.lang.String param1, byte[] param2, org.sakaiproject.service.legacy.entity.ResourceProperties param3, int param4) throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.InconsistentException, org.sakaiproject.exception.OverQuotaException, org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.addResource(param0, param1, param2, param3, param4);
	}

	public static org.sakaiproject.service.legacy.content.ContentResource addResource(java.lang.String param0, java.lang.String param1, int param2, java.lang.String param3, byte[] param4, org.sakaiproject.service.legacy.entity.ResourceProperties param5, int param6) throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.InconsistentException, org.sakaiproject.exception.OverQuotaException, org.sakaiproject.exception.ServerOverloadException, org.sakaiproject.exception.IdUniquenessException, org.sakaiproject.exception.IdLengthException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.addResource(param0, param1, param2, param3, param4, param5, param6);
	}

	public static org.sakaiproject.service.legacy.content.ContentResourceEdit addResource(java.lang.String param0) throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.InconsistentException, org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.addResource(param0);
	}

	public static boolean allowAddAttachmentResource()
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddAttachmentResource();
	}
	
	public static boolean isAttachmentResource(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.isAttachmentResource(param0);
	}
	
	public static org.sakaiproject.service.legacy.content.ContentResource addAttachmentResource(java.lang.String param0, java.lang.String param1, byte[] param2, org.sakaiproject.service.legacy.entity.ResourceProperties param3) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.InconsistentException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.OverQuotaException, org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.addAttachmentResource(param0, param1, param2, param3);
	}

	public static org.sakaiproject.service.legacy.content.ContentResource addAttachmentResource(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, byte[] param4, org.sakaiproject.service.legacy.entity.ResourceProperties param5) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.InconsistentException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.OverQuotaException, org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.addAttachmentResource(param0, param1, param2, param3, param4, param5);
	}

	public static org.sakaiproject.service.legacy.content.ContentResourceEdit addAttachmentResource(java.lang.String param0) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.InconsistentException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.addAttachmentResource(param0);
	}

	public static boolean allowUpdateResource(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdateResource(param0);
	}

	public static org.sakaiproject.service.legacy.content.ContentResource updateResource(java.lang.String param0, java.lang.String param1, byte[] param2) throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.TypeException, org.sakaiproject.exception.InUseException, org.sakaiproject.exception.OverQuotaException, org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.updateResource(param0, param1, param2);
	}

	public static org.sakaiproject.service.legacy.content.ContentResourceEdit editResource(java.lang.String param0) throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.TypeException, org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.editResource(param0);
	}

	public static boolean allowGetResource(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.allowGetResource(param0);
	}

	public static boolean allowRemoveResource(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveResource(param0);
	}

	public static void removeResource(org.sakaiproject.service.legacy.content.ContentResourceEdit param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return;

		service.removeResource(param0);
	}

	public static void removeResource(java.lang.String param0) throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.TypeException, org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return;

		service.removeResource(param0);
	}

	public static boolean allowRename(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.allowRename(param0, param1);
	}

	public static boolean allowCopy(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.allowCopy(param0, param1);
	}

	public static void commitResource(org.sakaiproject.service.legacy.content.ContentResourceEdit param0) throws org.sakaiproject.exception.OverQuotaException, org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return;

		service.commitResource(param0);
	}

	public static void commitResource(org.sakaiproject.service.legacy.content.ContentResourceEdit param0, int param1) throws org.sakaiproject.exception.OverQuotaException, org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return;

		service.commitResource(param0, param1);
	}

	public static void cancelResource(org.sakaiproject.service.legacy.content.ContentResourceEdit param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return;

		service.cancelResource(param0);
	}

	public static boolean allowGetProperties(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.allowGetProperties(param0);
	}

	public static boolean allowAddProperty(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddProperty(param0);
	}

	public static org.sakaiproject.service.legacy.entity.ResourceProperties addProperty(java.lang.String param0, java.lang.String param1, java.lang.String param2) throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.TypeException, org.sakaiproject.exception.InUseException, org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.addProperty(param0, param1, param2);
	}

	public static boolean allowRemoveProperty(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveProperty(param0);
	}

	public static org.sakaiproject.service.legacy.entity.ResourceProperties removeProperty(java.lang.String param0, java.lang.String param1) throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.TypeException, org.sakaiproject.exception.InUseException, org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.removeProperty(param0, param1);
	}

	public static java.lang.String getContainingCollectionId(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.getContainingCollectionId(param0);
	}

	public static boolean isRootCollection(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.isRootCollection(param0);
	}

	public static org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit newResourceProperties()
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.newResourceProperties();
	}

	public static java.lang.String getSiteCollection(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.getSiteCollection(param0);
	}

	public static java.lang.String archiveResources(java.util.List param0, org.w3c.dom.Document param1, java.util.Stack param2, java.lang.String param3)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.archiveResources(param0, param1, param2, param3);
	}

	public static org.sakaiproject.service.legacy.content.ContentResource getResource(java.lang.String param0) throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.TypeException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.getResource(param0);
	}

	public static org.sakaiproject.service.legacy.entity.ResourceProperties getProperties(java.lang.String param0) throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.getProperties(param0);
	}

	public static String copy(java.lang.String param0, java.lang.String param1) throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.TypeException, org.sakaiproject.exception.InUseException, org.sakaiproject.exception.OverQuotaException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.copy(param0, param1);
	}
	
	public static String copyIntoFolder(java.lang.String param0, java.lang.String param1) 
		throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.IdLengthException, org.sakaiproject.exception.IdUniquenessException, org.sakaiproject.exception.TypeException, org.sakaiproject.exception.InUseException, org.sakaiproject.exception.OverQuotaException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.ServerOverloadException, org.sakaiproject.exception.InconsistentException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;
	
		return service.copyIntoFolder(param0, param1);
	}

	public static void moveIntoFolder(java.lang.String param0, java.lang.String param1) 
		throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.TypeException, org.sakaiproject.exception.InUseException, org.sakaiproject.exception.OverQuotaException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.InconsistentException, org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return;
	
		service.moveIntoFolder(param0, param1);
	}

	public static void rename(java.lang.String param0, java.lang.String param1) throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.TypeException, org.sakaiproject.exception.InUseException, org.sakaiproject.exception.OverQuotaException, org.sakaiproject.exception.InconsistentException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return;

		service.rename(param0, param1);
	}

	public static void checkResource(java.lang.String param0) throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.TypeException
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return;

		service.checkResource(param0);
	}

	public static int getDepth(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return 0;

		return service.getDepth(param0, param1);
	}

	public static java.lang.String merge(java.lang.String param0, org.w3c.dom.Element param1, java.lang.String param2, java.lang.String param3, java.util.Map param4, java.util.HashMap param5, java.util.Set param6)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.merge(param0, param1, param2, param3, param4, param5, param6);
	}

	public static void importResources(java.lang.String param0, java.lang.String param1, java.util.List param2)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return;

		service.importEntities(param0, param1, param2);
	}

	public static java.lang.String getLabel()
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.getLabel();
	}

	public static java.lang.String archive(java.lang.String param0, org.w3c.dom.Document param1, java.util.Stack param2, java.lang.String param3, java.util.List param4)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return null;

		return service.archive(param0, param1, param2, param3, param4);
	}

	public static boolean isPubView(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.isPubView(param0);
	}

	public static boolean isInheritingPubView(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return false;

		return service.isInheritingPubView(param0);
	}

	public static void setPubView(java.lang.String param0, boolean param1)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null)
			return;

		service.setPubView(param0, param1);
	}

   public static String getUuid(String id)
   {
      org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
      if (service == null)
         return null;

      return service.getUuid(id);
   }

   public static String resolveUuid(String uuid)
   {
      org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
      if (service == null)
         return null;

      return service.resolveUuid(uuid);
   }

   public static java.util.Collection getLocks(String id)
   {
      org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
      if (service == null)
         return null;

      return service.getLocks(id);
   }

   public static void lockObject(String id, String lockId, String subject, boolean system)
   {
      org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
      if (service == null)
         return;

      service.lockObject(id, lockId, subject, system);
   }

   public static void removeLock(String id, String lockId)
   {
      org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
      if (service == null)
         return;

      service.removeLock(id, lockId);
   }

   public static boolean isLocked(String id)
   {
      org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
      if (service == null)
         return false;

      return service.isLocked(id);
   }

   public static boolean containsLockedNode(String id)
   {
      org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
      if (service == null)
         return false;

      return service.containsLockedNode(id);
   }

   public static void removeAllLocks(String id)
   {
      org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
      if (service == null)
         return;

      service.removeAllLocks(id);
   }

   public static java.util.List findResources(String type, String primaryMimeType, String subMimeType) {
      org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
      if (service == null)
         return null;

      return service.findResources(type, primaryMimeType, subMimeType);
   }

   public static java.util.Map getCollectionMap()
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null) return null;

		return service.getCollectionMap();
	}

	public static void eliminateDuplicates(java.util.Collection param)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null) return;

		service.eliminateDuplicates(param);
	}

	public static void createDropboxCollection()
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null) return;

		service.createDropboxCollection();
	}

	public static void createDropboxCollection(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null) return;

		service.createDropboxCollection(param0);
	}

	public static java.lang.String getDropboxCollection()
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null) return null;

		return service.getDropboxCollection();
	}

	public static java.lang.String getDropboxCollection(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null) return null;

		return service.getDropboxCollection(param0);
	}

	public static boolean isDropboxMaintainer()
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null) return false;

		return service.isDropboxMaintainer();
	}

	public static boolean isDropboxMaintainer(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null) return false;

		return service.isDropboxMaintainer(param0);
	}

	public static java.lang.String getDropboxDisplayName()
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null) return null;

		return service.getDropboxDisplayName();
	}

	public static java.lang.String getDropboxDisplayName(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService service = getInstance();
		if (service == null) return null;

		return service.getDropboxDisplayName(param0);
	}
}
