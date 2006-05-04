/**********************************************************************************
* $URL$
* $Id$
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
package org.sakaiproject.cheftool.menu;

// imports
import java.util.List;

/**
* <p>MenuItem is the interface for all the objects that can live on a menu.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface MenuItem
{
	/** Checked status values. */
	final static int CHECKED_NA = 0;
	final static int CHECKED_FALSE = 1;
	final static int CHECKED_TRUE = 2;

	final static String STATE_MENU =  "menu";

	/**
	 * Does this item act as a container for other items?
	 * @return true if this MenuItem is a container for other items, false if not.
	 */
	boolean getIsContainer();

	/**
	 * Is this item a divider ?
	 * @return true if this MenuItem is a divider, false if not.
	 */
	boolean getIsDivider();

	/**
	 * Access the display title for the item.
	 * @return The display title for the item.
	 */
	String getTitle();

	/**
	 * Access the icon name for the item (or null if no icon).
	 * @return The icon name for the item (or null if no icon).
	 */
	String getIcon();

	/**
	 * Access the enabled flag for the item.
	 * @return True if the item is enabled, false if not.
	 */
	boolean getIsEnabled();

	/**
	 * Access the action string for this item; what to do when the user clicks.
	 * Note: if getIsMenu(), there will not be an action string (will return "").
	 * @return The action string for this item.
	 */
	String getAction();

	/**
	 * Access the full URL string for this item; what to do when the user clicks.
	 * Note: this if defined overrides getAction() which should be "".
	 * Note: if getIsMenu(), there will not be a  URL string (will return "").
	 * @return The full URL string for this item.
	 */
	String getUrl();

	/**
	 * Access the form name whose values will be used when this item is selected.
	 * @return The form name whose values will be used when this item is selected, or null if there is none.
	 */
	String getForm();

	/**
	 * Access the checked status of this item.
	 * Possible values are (see above) CHECKED_NA, CHECKED_FALSE, CHECKED_TRUE
	 * @return The the checked status of this item.
	 */
	int getChecked();

	/**
	 * Access the sub-items of the item.
	 * Note: if !isContainer(), there will be no sub-items (will return EmptyIterator).
	 * @return The sub-items of the item.
	 */
	List getItems();

	/**
	 * Count the sub-items of the item.
	 * Note: if !isContainer(), the count is 0.
	 * @return The count of sub-items of the item.
	 */
	int size();

	/**
	 * Check if there are any sub-items.
	 * Note: if !isContainer(), this is empty.
	 * @return true of there are no sub-items, false if there are.
	 */
	boolean isEmpty();

	/**
	 * Access one sub-items of the item.
	 * Note: if !isContainer(), there will be no sub-items (will return null).
	 * @param index The index position (0 based) for the sub-item to get.
	 * @return The sub-item of the item.
	 */
	MenuItem getItem(int index);

	/**
	 * Access the is-field (not a button) flag.
	 * @return True if the item is a field, false if not.
	 */
	boolean getIsField();
} 



