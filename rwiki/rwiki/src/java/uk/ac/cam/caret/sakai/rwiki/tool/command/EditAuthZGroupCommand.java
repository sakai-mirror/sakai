/**********************************************************************************
 *
 * $Header$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 University of Cambridge
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
package uk.ac.cam.caret.sakai.rwiki.tool.command;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroupService;
import org.sakaiproject.service.legacy.authzGroup.Role;

import uk.ac.cam.caret.sakai.rwiki.service.api.RWikiSecurityService;
import uk.ac.cam.caret.sakai.rwiki.tool.RWikiServlet;
import uk.ac.cam.caret.sakai.rwiki.tool.RequestScopeSuperBean;
import uk.ac.cam.caret.sakai.rwiki.tool.api.HttpCommand;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.AuthZGroupEditBean;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.ErrorBean;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.ViewBean;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.helper.ViewParamsHelperBean;

/**
 * @author andrew
 * 
 */
//FIXME: Tool

public class EditAuthZGroupCommand implements HttpCommand {
	
	private String editRealmPath;
	private String cancelEditPath;
	private String successfulPath;
	private String permissionPath;
	private String unknownRealmPath;
	private String idInUsePath;
	private AuthzGroupService realmService;
	private Logger log;
	
	public Logger getLog() {
		return log;
	}
	
	
	public void setLog(Logger log) {
		this.log = log;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.HttpCommand#execute(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void execute(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		
		RequestScopeSuperBean rssb = RequestScopeSuperBean
		.getFromRequest(request);
		
		ViewParamsHelperBean vphb = rssb.getNameHelperBean();
		AuthZGroupEditBean realmEditBean = rssb.getRealmEditBean();
		String requestedRealmId = realmEditBean.getLocalSpace();
		
		String saveType = vphb.getSaveType();
		
		AuthzGroup realmEdit;
		try {
			if (realmEditBean.getRealmEdit() == null) {
				realmEdit = realmService.getAuthzGroup(requestedRealmId);
				realmEditBean.setRealmEdit(realmEdit);
				
			} else {
				// Check the current realm edit is the same as the requested one:
				realmEdit = realmEditBean.getRealmEdit();
				if (!requestedRealmId.equals(realmEdit.getId())) {
					// TODO: set errorBean and ignore current request i.e. go back to start
					ErrorBean errorBean = rssb.getErrorBean();
					errorBean.addError("You are currently editing: "  + realmEdit.getId() + ". You must finish editing this realm first");
					realmEditBean.setLocalSpace(realmEdit.getId());
					saveType = null;
				}
			}
			
			
			if (saveType == null || saveType.equals("")) {
				// Begin a realmEdit...
				editDispatch(request, response);
				return;
			} else if (saveType.equals(AuthZGroupEditBean.CANCEL_VALUE)) {
				// cancel a realmEdit...
				// TODO: CHECK We beleive that this is no longer needed since locking in authz group
				// is optimistic
				//realmService.cancelEdit(realmEdit);
				realmEditBean.setRealmEdit(null);
				cancelDispatch(request, response);

	            String pageName = vphb.getGlobalName();
	            String realm = vphb.getLocalSpace();
	            ViewBean vb = new ViewBean(pageName, realm);
	            String requestURL = request.getRequestURL().toString();
	            SessionManager.getCurrentToolSession().setAttribute(RWikiServlet.SAVED_REQUEST_URL,requestURL+vb.getInfoUrl());


				return;
			} else if (saveType.equals(AuthZGroupEditBean.SAVE_VALUE)) {
				// complete a realmEdit...
				Map requestMap = request.getParameterMap();
				
				for (Iterator it = realmEdit.getRoles().iterator(); it.hasNext(); ) {
					Role  roleEdit = (Role) it.next();
					updateRoleEdit(roleEdit, requestMap);
				}
				
				realmService.save(realmEdit);                
				realmEditBean.setRealmEdit(null);
				successfulDispatch(request, response);
				

	            String pageName = vphb.getGlobalName();
	            String realm = vphb.getLocalSpace();
	            ViewBean vb = new ViewBean(pageName, realm);
	            String requestURL = request.getRequestURL().toString();
	            SessionManager.getCurrentToolSession().setAttribute(RWikiServlet.SAVED_REQUEST_URL,requestURL+vb.getInfoUrl());

			}
		} catch (IdUnusedException e) {
			realmEditBean.setRealmEdit(null);
			unknownRealmDispatch(request, response);
			return;
		} catch (PermissionException e) {
			// redirect to permission denied page
			realmEditBean.setRealmEdit(null);
			permissionDeniedDispatch(request, response);
			return;
		}
		
		
		
		
	}
	
	
	public String getIdInUsePath() {
		return idInUsePath;
	}
	
	
	
	public void setIdInUsePath(String idInUsePath) {
		this.idInUsePath = idInUsePath;
	}
	
	
	
	public String getPermissionPath() {
		return permissionPath;
	}
	
	
	
	public void setPermissionPath(String permissionPath) {
		this.permissionPath = permissionPath;
	}
	
	
	
	public String getUnknownRealmPath() {
		return unknownRealmPath;
	}
	
	
	
	public void setUnknownRealmPath(String unknownRealmPath) {
		this.unknownRealmPath = unknownRealmPath;
	}
	
	
	
	public String getCancelEditPath() {
		return cancelEditPath;
	}
	
	
	
	public void setCancelEditPath(String cancelEditPath) {
		this.cancelEditPath = cancelEditPath;
	}
	
	
	
	public String getEditRealmPath() {
		return editRealmPath;
	}
	
	
	
	public void setEditRealmPath(String editRealmPath) {
		this.editRealmPath = editRealmPath;
	}
	
	
	
	public String getSuccessfulPath() {
		return successfulPath;
	}
	
	
	
	public void setSuccessfulPath(String successfulPath) {
		this.successfulPath = successfulPath;
	}
	
	public AuthzGroupService getRealmService() {
		return realmService;
	}
	
	public void setRealmService(AuthzGroupService realmService) {
		this.realmService = realmService;
	}
	
	private void successfulDispatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		dispatch(request, response, successfulPath);
	}
	
	
	
	private void dispatch(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher(path);
		rd.forward(request, response);
	}
	
	private void cancelDispatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		dispatch(request, response, cancelEditPath);
	}
	
	
	
	private void editDispatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		dispatch(request, response, editRealmPath);
	}
	
	private void permissionDeniedDispatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		dispatch(request, response, permissionPath);
	}
	
	private void unknownRealmDispatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		dispatch(request, response, unknownRealmPath);
	}
	
	
	private void updateRoleEdit(Role roleEdit, Map map) {
		String id = roleEdit.getId();
		if (map.get("create_" + id) != null) {
			if (!roleEdit.isAllowed(RWikiSecurityService.SECURE_CREATE)) {
				roleEdit.allowFunction(RWikiSecurityService.SECURE_CREATE);
			}
		} else {
			if (roleEdit.isAllowed(RWikiSecurityService.SECURE_CREATE)) {
				roleEdit.disallowFunction(RWikiSecurityService.SECURE_CREATE);
			}                    
		}
		
		if (map.get("read_" + id) != null) {
			
			if (!roleEdit.isAllowed(RWikiSecurityService.SECURE_READ)) {
				roleEdit.allowFunction(RWikiSecurityService.SECURE_READ);
			}
		} else {
			if (roleEdit.isAllowed(RWikiSecurityService.SECURE_READ)) {
				roleEdit.disallowFunction(RWikiSecurityService.SECURE_READ);
			}                    
		}
		
		if (map.get("update_" + id) != null) {
			if (!roleEdit.isAllowed(RWikiSecurityService.SECURE_UPDATE)) {
				roleEdit.allowFunction(RWikiSecurityService.SECURE_UPDATE);
			}
		} else {
			if (roleEdit.isAllowed(RWikiSecurityService.SECURE_UPDATE)) {
				roleEdit.disallowFunction(RWikiSecurityService.SECURE_UPDATE);
			}                    
		}
		
//		if (requestMap.get("delete_" + id) != null) {
//		if (!roleEdit.contains(RWikiSecurityServiceImpl.SECURE_DELETE)) {
//		roleEdit.add(RWikiSecurityServiceImpl.SECURE_DELETE);
//		}
//		} else {
//		if (roleEdit.contains(RWikiSecurityServiceImpl.SECURE_DELETE)) {
//		roleEdit.remove(RWikiSecurityServiceImpl.SECURE_DELETE);
//		}                    
//		}                
		
		if (map.get("admin_" + id) != null) {
			if (!roleEdit.isAllowed(RWikiSecurityService.SECURE_ADMIN)) {
				roleEdit.allowFunction(RWikiSecurityService.SECURE_ADMIN);
			}
		} else {
			if (roleEdit.isAllowed(RWikiSecurityService.SECURE_ADMIN)) {
				roleEdit.disallowFunction(RWikiSecurityService.SECURE_ADMIN);
			}                    
		}
		
		if (map.get("superadmin_" + id) != null) {
			if (!roleEdit.isAllowed(RWikiSecurityService.SECURE_SUPER_ADMIN)) {
				roleEdit.allowFunction(RWikiSecurityService.SECURE_SUPER_ADMIN);
			}
		} else {
			if (roleEdit.isAllowed(RWikiSecurityService.SECURE_SUPER_ADMIN)) {
				roleEdit.disallowFunction(RWikiSecurityService.SECURE_SUPER_ADMIN);
			}                    
		}        
	}
	
}
