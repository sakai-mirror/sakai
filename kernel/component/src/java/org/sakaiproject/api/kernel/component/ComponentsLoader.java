/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/component/src/java/org/sakaiproject/api/kernel/component/ComponentsLoader.java,v 1.3 2005/03/07 03:15:26 ggolden.umich.edu Exp $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.api.kernel.component;

/**
 * <p>
 * Load the available Sakai components into the shared component manager
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface ComponentsLoader
{
	/**
	 * Load components into this manager.
	 * @param mgr The component manager instance.
	 * @param componentsRoot The file system canonical path to the directory where components packages are found.
	 */
	void load(ComponentManager mgr, String componentsRoot);
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/component/src/java/org/sakaiproject/api/kernel/component/ComponentsLoader.java,v 1.3 2005/03/07 03:15:26 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
