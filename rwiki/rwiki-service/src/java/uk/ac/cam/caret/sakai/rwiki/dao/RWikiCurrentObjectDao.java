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
package uk.ac.cam.caret.sakai.rwiki.dao;

import java.util.Date;
import java.util.List;

import uk.ac.cam.caret.sakai.rwiki.model.RWikiCurrentObject;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiHistoryObject;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiObject;

public interface RWikiCurrentObjectDao extends RWikiObjectDao {

    /**
     * Finds an RWikiObject which matches the given name and realm exactly
     * 
     * @param name The globalised name of the object
     * @param realm The realm of control of the object 
     * @return the rwikiObject
     */
    RWikiCurrentObject findByGlobalName(String name);

    /**
     * Find a page by search, user and realm
     * @param criteria
     * @param user
     * @param realm
     * @return
     */
    List findByGlobalNameAndContents(String criteria, String user, String realm);

    /**
     * Update an RWikiCurrentObject
     * @param rwo
     */
    void update(RWikiCurrentObject rwo, RWikiHistoryObject rwho);

    /**
     * Create a new Current Object
     * @param name
     * @param realm
     * @return
     */
    RWikiCurrentObject createRWikiObject(String name, String realm);

    /**
     * Find current objects that have changed since
     * @param since
     * @param realm
     * @return
     */
    List findChangedSince(Date since, String realm);

    /**
     * Find pages references this one
     * @param name
     * @return
     */
    List findReferencingPages(String name);
    
    /**
     * Get the current oject base on a child (or the curent object)
     * @param reference
     * @return
     */
    RWikiCurrentObject getRWikiCurrentObject(RWikiObject reference);

    /**
     * Does the global name exist
     * @param name
     * @return
     */
    boolean exists(String name);

}
