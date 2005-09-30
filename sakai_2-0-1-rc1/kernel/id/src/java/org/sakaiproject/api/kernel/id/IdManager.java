/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/id/src/java/org/sakaiproject/api/kernel/id/IdManager.java,v 1.3 2005/03/11 02:10:55 ggolden.umich.edu Exp $
 *
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

package org.sakaiproject.api.kernel.id;

/**
 * <p>
 * IdManager is the work interface for the Sakai Id Manager API to generates unique ids.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface IdManager
{
	/**
	 * Allocate a unique Id.
	 * 
	 * @return A unique Id.
	 */
	String createUuid();
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/id/src/java/org/sakaiproject/api/kernel/id/IdManager.java,v 1.3 2005/03/11 02:10:55 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
