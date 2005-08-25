/**********************************************************************************
*
* $Id$
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of California, The Regents of the University of Michigan,
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

package org.sakaiproject.tool.section.jsf.backingbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.section.coursemanagement.CourseSection;
import org.sakaiproject.api.section.coursemanagement.ParticipationRecord;
import org.sakaiproject.api.section.coursemanagement.User;
import org.sakaiproject.api.section.facade.Role;

public class EditManagersBean extends CourseDependentBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(EditManagersBean.class);
	
	// For the right-side list box
	private List selectedUsers;
	
	// For the left-side list box
	private List availableUsers;
	
	private String sectionUuid;
	private String sectionTitle;
	private String courseTitle;
	
	public void init() {
		// Get the section to edit
		String sectionUuidFromParam = (String)FacesContext.getCurrentInstance()
			.getExternalContext().getRequestParameterMap().get("sectionUuid");
		if(sectionUuidFromParam != null) {
			sectionUuid = sectionUuidFromParam;
		}
		CourseSection currentSection = getSectionManager().getSection(sectionUuid);
		sectionTitle = currentSection.getTitle();
		courseTitle = currentSection.getCourse().getTitle();
		
		// Get the current users in the manager role for this section
		List selectedManagers = getSectionManager().getSectionTeachingAssistants(currentSection.getUuid());
		
		// Build the list of items for the right-side list box
		selectedUsers = new ArrayList();
		for(Iterator iter = selectedManagers.iterator(); iter.hasNext();) {
			ParticipationRecord manager = (ParticipationRecord)iter.next();
			SelectItem item = new SelectItem(manager.getUser().getUserUuid(),
					manager.getUser().getDisplayName());
			selectedUsers.add(item);
		}

		// Build the list of items for the left-side box.  Since the selected (right-side)
		// participation records are linked to a section, while the available records
		// are linked to the course, we can not use collection manipulation on these
		// objects.  So, generate a set of user uuids to filter out the currently
		// selected users from the available (left side) list.
		Set selectedUserUuids = new HashSet();
		for(Iterator iter = selectedManagers.iterator(); iter.hasNext();) {
			ParticipationRecord manager = (ParticipationRecord)iter.next();
			selectedUserUuids.add(manager.getUser().getUserUuid());
		}

		List availableManagers = getSectionManager().getSiteTeachingAssistants(getSiteContext());

		availableUsers = new ArrayList();
		for(Iterator iter = availableManagers.iterator(); iter.hasNext();) {
			User manager = ((ParticipationRecord)iter.next()).getUser();
			if( ! selectedUserUuids.contains(manager.getUserUuid())) {
				availableUsers.add(new SelectItem(manager.getUserUuid(), manager.getDisplayName()));
			}
		}
	}
	
	public String update() {
		Set userUuids = getHighlightedUsers("memberForm:selectedUsers");
		getSectionManager().setSectionMemberships(userUuids, Role.TA, sectionUuid);

		return null;
		
		//return "overview";
	}
	
	private Set getHighlightedUsers(String componentId) {
		Set userUuids = new HashSet();
		
		String[] highlighted = (String[])FacesContext.getCurrentInstance()
		.getExternalContext().getRequestParameterValuesMap().get(componentId);

		if(highlighted != null) {
			for(int i=0; i < highlighted.length; i++) {
				userUuids.add(highlighted[i]);
			}
		}
		return userUuids;
	}
	
	public List getAvailableUsers() {
		return availableUsers;
	}

	public void setAvailableUsers(List availableUsers) {
		this.availableUsers = availableUsers;
	}

	public List getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(List selectedUsers) {
		this.selectedUsers = selectedUsers;
	}

	public String getSectionUuid() {
		return sectionUuid;
	}

	public void setSectionUuid(String sectionUuid) {
		this.sectionUuid = sectionUuid;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public String getCourseTitle() {
		return courseTitle;
	}
}



/**********************************************************************************
 * $Id$
 *********************************************************************************/
