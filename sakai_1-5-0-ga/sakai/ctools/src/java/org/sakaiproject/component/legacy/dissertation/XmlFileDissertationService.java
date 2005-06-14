/**********************************************************************************
*
* $Header: /cvs/sakai/ctools/src/java/org/sakaiproject/component/legacy/dissertation/XmlFileDissertationService.java,v 1.13 2004/12/08 18:41:43 rwellis.umich.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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

// package
package org.sakaiproject.component.legacy.dissertation;

// import
import java.util.List;
import org.sakaiproject.service.legacy.dissertation.BlockGrantGroup;
import org.sakaiproject.service.legacy.dissertation.BlockGrantGroupEdit;
import org.sakaiproject.service.legacy.dissertation.CandidateInfo;
import org.sakaiproject.service.legacy.dissertation.CandidateInfoEdit;
import org.sakaiproject.service.legacy.dissertation.CandidatePath;
import org.sakaiproject.service.legacy.dissertation.CandidatePathEdit;
import org.sakaiproject.service.legacy.dissertation.Dissertation;
import org.sakaiproject.service.legacy.dissertation.DissertationEdit;
import org.sakaiproject.service.legacy.dissertation.DissertationStep;
import org.sakaiproject.service.legacy.dissertation.DissertationStepEdit;
import org.sakaiproject.service.legacy.dissertation.StepStatus;
import org.sakaiproject.service.legacy.dissertation.StepStatusEdit;
import org.sakaiproject.util.storage.BaseXmlFileStorage;

/**
* <p>XmlFileDissertationService is the xml-file-storing service class for Dissertations.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.13 $
* @see org.chefproject.core.Dissertation
*/
public class XmlFileDissertationService extends BaseDissertationService
{
	/** A full path and file name to the storage file. */
	protected String m_groupStoragePath = "db/dissertation_group.xml";
	
	/** A full path and file name to the storage file. */
	protected String m_dissertationStoragePath = "db/dissertation_dissertation.xml";

	/** A full path and file name to the storage file. */
	protected String m_stepStoragePath = "db/dissertation_step.xml";

	/** A full path and file name to the storage file. */
	protected String m_pathStoragePath = "db/dissertation_path.xml";

	/** A full path and file name to the storage file. */
	protected String m_statusStoragePath = "db/dissertation_status.xml";

	/** A full path and file name to the storage file. */
	protected String m_infoStoragePath = "db/dissertation_info.xml";

	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/
	
	/**
	 * Set the storage path for the BlockGrantGroup file.
	 * @param path The storage path.
	 */
	public void setGroupStoragePath(String path)
	{
		
		m_groupStoragePath = path;
	}
	
	/**
	 * Set the storage path for the dissertation file.
	 * @param path The storage path.
	 */
	public void setDissertationStoragePath(String path)
	{
		m_dissertationStoragePath = path;
	}

	/**
	 * Set the storage path for the step file.
	 * @param path The storage path.
	 */
	public void setStepStoragePath(String path)
	{
		m_stepStoragePath = path;
	}

	/**
	 * Set the storage path for the path file.
	 * @param path The storage path.
	 */
	public void setPathStoragePath(String path)
	{
		m_pathStoragePath = path;
	}

	/**
	 * Set the storage path for the status file.
	 * @param path The storage path.
	 */
	public void setStatusStoragePath(String path)
	{
		m_statusStoragePath = path;
	}

	/**
	 * Set the storage path for the info file.
	 * @param path The storage path.
	 */
	public void setInfoStoragePath(String path)
	{
		m_infoStoragePath = path;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			super.init();

			m_logger.info(this +".init(RunData): dissertation storage path: " + m_dissertationStoragePath);
			m_logger.info(this +".init(RunData): step storage path: " + m_stepStoragePath);
			m_logger.info(this +".init(RunData): path storage path: " + m_pathStoragePath);
			m_logger.info(this +".init(RunData): status storage path: " + m_statusStoragePath);
			m_logger.info(this +".init(RunData): info storage path: " + m_infoStoragePath);
			m_logger.info(this +".init(RunData): group storage path: " + m_groupStoragePath);

			if (m_caching)
			{
				// we don't need the cache, since we hold everything in memory already
				m_dissertationCache.disable();
				m_stepCache.disable();
				m_pathCache.disable();
				m_statusCache.disable();
				m_infoCache.disable();
				m_groupCache.disable();
			}

		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}
	}

	/*******************************************************************************
	* BaseDissertationService extensions
	*******************************************************************************/

	/**
	 * Construct a Storage object for Dissertations.
	 * @return The new storage object for Dissertations.
	 */
	protected DissertationStorage newDissertationStorage()
	{
		return new XmlFileDissertationStorage(new DissertationStorageUser());

	} // newDissertationStorage

	/**
	 * Construct a Storage object for DissertationSteps.
	 * @return The new storage object for DissertationSteps.
	 */
	protected DissertationStepStorage newDissertationStepStorage()
	{
		return new XmlFileDissertationStepStorage(new DissertationStepStorageUser());

	} // newDissertationStepStorage

	/**
	 * Construct a Storage object for CandidatePaths.
	 * @return The new storage object for CandidatePaths.
	 */
	protected CandidatePathStorage newCandidatePathStorage()
	{
		return new XmlFileCandidatePathStorage(new CandidatePathStorageUser());

	} // newCandidatePathStorage

	/**
	 * Construct a Storage object for StepStatus.
	 * @return The new storage object for StepStatus.
	 */
	protected StepStatusStorage newStepStatusStorage()
	{
		return new XmlFileStepStatusStorage(new StepStatusStorageUser());

	} // newStepStatusStorage
	
	/**
	 * Construct a Storage object for BlockGrantGroups.
	 * @return The new storage object for BlockGrantGroups.
	 */
	protected BlockGrantGroupStorage newBlockGrantGroupStorage()
	{
		return new XmlFileBlockGrantGroupStorage(new BlockGrantGroupStorageUser());
	} // newBlockGrantGroupStorage

	/**
	 * Construct a Storage object for CandidateInfos.
	 * @return The new storage object for CandidateInfos.
	 */
	protected CandidateInfoStorage newCandidateInfoStorage()
	{
		return new XmlFileCandidateInfoStorage(new CandidateInfoStorageUser());

	} // newCandidateInfoStorage

	/*******************************************************************************
	* Storage implementations
	*******************************************************************************/

	/*******************************************************************************
	* DissertationStorage implementation
	*******************************************************************************/

	/**
	* Covers for the BaseXmlFileStorage, providing Dissertation and DissertationEdit parameters
	*/
	protected class XmlFileDissertationStorage extends BaseXmlFileStorage implements DissertationStorage
	{
		/**
		* Construct.
		* @param dissertation The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public XmlFileDissertationStorage(DissertationStorageUser dissertation)
		{
			//super(m_storagePath, "dissertations", null, "dissertation", dissertation);
			super(m_dissertationStoragePath, "dissertations", null, "dissertation", dissertation);

		} // XmlFileDissertationStorage

		public boolean check(String id)
		{
			return super.checkResource(null, id);
		}

		public Dissertation get(String id)
		{
			return (Dissertation) super.getResource(null, id);
		}

		public List getAll()
		{
			return super.getAllResources(null);
		}
		
		public List getAllOfType(String type)
		{
			//%% this is only a stub
			List retVal = null;
			return retVal;
		}
		
		public Dissertation getForSite(String id)
		{
			//%% this is only a stub
			Dissertation retVal = null;
			return retVal;
		}
		
		public Dissertation getForSiteOfType(String id, String type)
		{
			//%% this is only a stub
			Dissertation retVal = null;
			return retVal;
		}

		public DissertationEdit put(String id, String context)
		{
			Object[] other = new Object[1];
			other[0] = context;
			return (DissertationEdit) super.putResource(null, id, other);
		}

		public DissertationEdit edit(String id)
		{
			return (DissertationEdit) super.editResource(null, id);
		}

		public void commit(DissertationEdit edit)
		{
			super.commitResource(null, edit);
		}

		public void cancel(DissertationEdit edit)
		{
			super.cancelResource(null, edit);
		}

		public void remove(DissertationEdit edit)
		{
			super.removeResource(null, edit);
		}

	} // XmlFileDissertationStorage

	/*******************************************************************************
	* DissertationStepStorage implementation
	*******************************************************************************/

	/**
	* Covers for the BaseXmlFileStorage, providing DissertationStep and DissertationStepEdit parameters
	*/
	protected class XmlFileDissertationStepStorage extends BaseXmlFileStorage implements DissertationStepStorage
	{
		/**
		* Construct.
		* @param step The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public XmlFileDissertationStepStorage(DissertationStepStorageUser step)
		{
			super(m_stepStoragePath, "steps", null, "step", step);

		} // XmlFileDissertationStepStorage

		public boolean check(String id)
		{
			return super.checkResource(null, id);
		}

		public DissertationStep get(String id)
		{
			return (DissertationStep) super.getResource(null, id);
		}

		public List getAll()
		{
			return super.getAllResources(null);
		}

		public DissertationStepEdit put(String id, String context)
		{
			Object[] other = new Object[1];
			other[0] = context;
			return (DissertationStepEdit) super.putResource(null, id, other);
		}

		public DissertationStepEdit edit(String id)
		{
			return (DissertationStepEdit) super.editResource(null, id);
		}

		public void commit(DissertationStepEdit edit)
		{
			super.commitResource(null, edit);
		}

		public void cancel(DissertationStepEdit edit)
		{
			super.cancelResource(null, edit);
		}

		public void remove(DissertationStepEdit edit)
		{
			super.removeResource(null, edit);
		}

	} // XmlFileDissertationStepStorage

	/*******************************************************************************
	* CandidatePathStorage implementation
	*******************************************************************************/

	/**
	* Covers for the BaseXmlFileStorage, providing CandidatePath and CandidatePathEdit parameters
	*/
	protected class XmlFileCandidatePathStorage extends BaseXmlFileStorage implements CandidatePathStorage
	{
		/**
		* Construct.
		* @param path The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public XmlFileCandidatePathStorage(CandidatePathStorageUser path)
		{
			//super(m_storagePath, "dissertations", null, "path", path);
			super(m_pathStoragePath, "paths", null, "path", path);
		} // XmlFileCandidatePathStorage

		public boolean check(String id)
		{
			return super.checkResource(null, id);
		}

		public CandidatePath get(String id)
		{
			return (CandidatePath) super.getResource(null, id);
		}

		public List getAll()
		{
			return super.getAllResources(null);
		}
		
		public List getAllOfType(String type)
		{
			//%%% this is only a stub
			List retVal = null;
			return retVal;
		}
		
		public List getAllOfTypeForLetter(String type, String letter)
		{
			//%%% this is only a stub
			List retVal = null;
			return retVal;
		}
		
		public List getAllOfParentForLetter(String parentSite, String letter)
		{
			//%%% this is only a stub
			List retVal = null;
			return retVal;
		}
		
		public List getAllForParent(String site)
		{
			//%%% this is only a stub
			List retVal = null;
			return retVal;
		}
		
		public boolean existsPathOfType(String type)
		{
			//%%% this is only a stub
			boolean retVal = false;
			return retVal;
		}
		
		public boolean existsPathForParent(String siteId)
		{
			//%%% this is only a stub
			boolean retVal = false;
			return retVal;
		}
		
		public CandidatePath getForCandidate(String id)
		{
			//%%% this is only a stub
			CandidatePath retVal = null;
			return retVal;
		}

		public CandidatePath getForSite(String id)
		{
			//%%% this is only a stub
			CandidatePath retVal = null;
			return retVal;
		}


		public CandidatePathEdit put(String id, String context)
		{
			Object[] other = new Object[1];
			other[0] = context;
			return (CandidatePathEdit) super.putResource(null, id, other);
		}

		public CandidatePathEdit edit(String id)
		{
			return (CandidatePathEdit) super.editResource(null, id);
		}

		public void commit(CandidatePathEdit edit)
		{
			super.commitResource(null, edit);
		}

		public void cancel(CandidatePathEdit edit)
		{
			super.cancelResource(null, edit);
		}

		public void remove(CandidatePathEdit edit)
		{
			super.removeResource(null, edit);
		}
		
		public boolean isEmpty()
		{
			return super.isEmpty(null);
		}

	} // XmlFileCandidatePathStorage

	/*******************************************************************************
	* StepStatusStorage implementation
	*******************************************************************************/

	/**
	* Covers for the BaseXmlFileStorage, providing StepStatus and StepStatusEdit parameters
	*/
	protected class XmlFileStepStatusStorage extends BaseXmlFileStorage implements StepStatusStorage
	{
		/**
		* Construct.
		* @param status The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public XmlFileStepStatusStorage(StepStatusStorageUser status)
		{
			super(m_statusStoragePath, "statusi", null, "status", status);

		} // XmlFileStepStatusStorage

		public boolean check(String id)
		{
			return super.checkResource(null, id);
		}

		public StepStatus get(String id)
		{
			return (StepStatus) super.getResource(null, id);
		}

		public List getAll()
		{
			return super.getAllResources(null);
		}

		public StepStatusEdit put(String id, String context)
		{
			Object[] other = new Object[1];
			other[0] = context;
			return (StepStatusEdit) super.putResource(null, id, other);
		}

		public StepStatusEdit edit(String id)
		{
			return (StepStatusEdit) super.editResource(null, id);
		}

		public void commit(StepStatusEdit edit)
		{
			super.commitResource(null, edit);
		}

		public void cancel(StepStatusEdit edit)
		{
			super.cancelResource(null, edit);
		}

		public void remove(StepStatusEdit edit)
		{
			super.removeResource(null, edit);
		}

	} // XmlFileStepStatusStorage

	/*******************************************************************************
	* CandidateInfoStorage implementation
	*******************************************************************************/
	
	/*******************************************************************************
	* BlockGrantGroupStorage implementation
	*******************************************************************************/

	/**
	* Covers for the BaseXmlFileStorage, providing BlockGrantGroup and BlockGrantGroupEdit parameters
	*/
	protected class XmlFileBlockGrantGroupStorage 
		extends BaseXmlFileStorage 
		implements BlockGrantGroupStorage
	{
		/**
		* Construct.
		* @param group The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public XmlFileBlockGrantGroupStorage(BlockGrantGroupStorageUser group)
		{
			super(m_groupStoragePath, "groups", null, "group", group);

		} // XmlFileBlockGrantGroupStorage

		public boolean check(String id)
		{
			return super.checkResource(null, id);
		}

		public BlockGrantGroup get(String id)
		{
			return (BlockGrantGroup) super.getResource(null, id);
		}

		public List getAll()
		{
			return super.getAllResources(null);
		}

		public BlockGrantGroupEdit put(String id, String context)
		{
			Object[] other = new Object[1];
			other[0] = context;
			return (BlockGrantGroupEdit) super.putResource(null, id, other);
		}

		public BlockGrantGroupEdit edit(String id)
		{
			return (BlockGrantGroupEdit) super.editResource(null, id);
		}

		public void commit(BlockGrantGroupEdit edit)
		{
			super.commitResource(null, edit);
		}

		public void cancel(BlockGrantGroupEdit edit)
		{
			super.cancelResource(null, edit);
		}

		public void remove(BlockGrantGroupEdit edit)
		{
			super.removeResource(null, edit);
		}

	} // XmlFileBlockGrantGroupStorage
	
	/**
	* Covers for the BaseXmlFileStorage, providing CandidateInfo and CandidateInfoEdit parameters
	*/
	protected class XmlFileCandidateInfoStorage extends BaseXmlFileStorage implements CandidateInfoStorage
	{
		/**
		* Construct.
		* @param info The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public XmlFileCandidateInfoStorage(CandidateInfoStorageUser info)
		{
			super(m_infoStoragePath, "infos", null, "info", info);
		} // XmlFileCandidateInfoStorage

		public boolean check(String id)
		{
			return super.checkResource(null, id);
		}

		public CandidateInfo get(String id)
		{
			return (CandidateInfo) super.getResource(null, id);
		}
		
		public String getParent(String id)
		{
			//%% this is only a stub
			String retVal = null;
			return retVal;
		}
		
		public boolean checkCandidate(String id)
		{
			//%% this is only a stub
			boolean retVal = false;
			return retVal;
		}
		
		public boolean checkMusic(String id)
		{
			//%% this is only a stub
			boolean retVal = false;
			return retVal;
		}
		
		public CandidateInfo getForCandidate(String id)
		{
			//%% this is only a stub
			CandidateInfo retVal = null;
			return retVal;
		}
		
		public CandidateInfo getForEmplid(String id)
		{
			//%% this is only a stub
			CandidateInfo retVal = null;
			return retVal;
		}
		
		public String getEmplid(String id)
		{
			//%% this is only a stub
			String retVal = null;
			return retVal;
		}

		public List getAll()
		{
			return super.getAllResources(null);
		}

		public CandidateInfoEdit put(String id, String context)
		{
			Object[] other = new Object[1];
			other[0] = context;
			return (CandidateInfoEdit) super.putResource(null, id, other);
		}

		public CandidateInfoEdit edit(String id)
		{
			return (CandidateInfoEdit) super.editResource(null, id);
		}

		public void commit(CandidateInfoEdit edit)
		{
			super.commitResource(null, edit);
		}

		public void cancel(CandidateInfoEdit edit)
		{
			super.cancelResource(null, edit);
		}

		public void remove(CandidateInfoEdit edit)
		{
			super.removeResource(null, edit);
		}

		public boolean isEmpty()
		{
			return super.isEmpty(null);
		}

	} // XmlFileCandidateInfoStorage
}

/**********************************************************************************
*
* $Header: /cvs/sakai/ctools/src/java/org/sakaiproject/component/legacy/dissertation/XmlFileDissertationService.java,v 1.13 2004/12/08 18:41:43 rwellis.umich.edu Exp $
*
**********************************************************************************/
