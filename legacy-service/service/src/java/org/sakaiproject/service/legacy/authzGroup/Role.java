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

// package
package org.sakaiproject.service.legacy.authzGroup;

// imports
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * <p>
 * Role is part of an AuthzGroup, to which users can be assingned, and which is given permissions to various functions.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public interface Role extends Comparable, Serializable
{
	/**
	 * Access the Role id.
	 * 
	 * @return The role id.
	 */
	String getId();

	/**
	 * Access the Role description.
	 * 
	 * @return The role description.
	 */
	String getDescription();

	/**
	 * Test if users with this role are allowed to perform this named function.
	 * 
	 * @param function
	 *        The function name.
	 * @return true if users with this role are allowed to perform this named function, false if not.
	 */
	boolean isAllowed(String function);

	/**
	 * Access the set of functions that users with this role are allowed to perform.
	 * 
	 * @return The Set of function names (String) that users with this role are allowed to perform.
	 */
	Set getAllowedFunctions();

	/**
	 * Set the role description.
	 * 
	 * @param description
	 *        The role description.
	 */
	void setDescription(String description);

	/**
	 * Add this function to the set of functions that users with this role are allowed to perform.
	 * 
	 * @param function
	 *        The function name to add to the allowed set.
	 */
	void allowFunction(String lock);

	/**
	 * Add these functions to the set of functions that users with this role are allowed to perform.
	 * 
	 * @param functions
	 *        The Collection (String) of function names to add to the allowed set.
	 */
	void allowFunctions(Collection functions);

	/**
	 * Remove this function from the set of functions that users with this role are allowed to perform.
	 * 
	 * @param function
	 *        The function name to disallow.
	 */
	void disallowFunction(String lock);

	/**
	 * Remove these functions from the set of functions that users with this role are allowed to perform.
	 * 
	 * @param function
	 *        The Collection (String) of function names to remove from the allowed set.
	 */
	void disallowFunctions(Collection functions);

	/**
	 * Remove all functions from the set of functions that users with this role are allowed to perform.
	 */
	void disallowAll();

	/**
	 * Check if the Role has no functons in the set of functions that users with this role are allowed to perform.
	 * 
	 * @return true if the role has no allowed functions, false if it does.
	 */
	boolean allowsNoFunctions();
}
