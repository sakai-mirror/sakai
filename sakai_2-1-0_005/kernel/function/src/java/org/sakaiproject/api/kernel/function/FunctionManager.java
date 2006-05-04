/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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

package org.sakaiproject.api.kernel.function;

import java.util.List;

/**
 * <p>
 * FunctionManager is the API for the service that manages security function registrations from the various Sakai applications.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public interface FunctionManager
{
	/**
	 * Allocate a unique Id.
	 * 
	 * @return A unique Id.
	 */
	void registerFunction(String function);

	/**
	 * Access all the registered functions.
	 * 
	 * @return A List (String) of registered functions.
	 */
	List getRegisteredFunctions();

	/**
	 * Access all the registered functions that begin with the string.
	 * 
	 * @param prefix
	 *        The prefix pattern to find.
	 * @return A List (String) of registered functions that begin with the string.
	 */
	List getRegisteredFunctions(String prefix);
}