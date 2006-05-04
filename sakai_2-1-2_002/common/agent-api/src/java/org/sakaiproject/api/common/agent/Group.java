/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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

package org.sakaiproject.api.common.agent;

import java.util.Set;

/**
 * Group extends Agent so that it too can be a target to which permissions
 * are assigned. Groups add to the Agent model by including a collection of
 * Agents.
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 * @since Sakai 2.0
 */
public interface Group extends Agent
{
  /**
   * This method gives the caller "raw" access directly to the underlying 
   * collection which backs the Group's membership.
   * 
   * @return Returns the members.
   */
  public Set getMembers();

  /**
   * This method would be used in edge cases where the caller would want to 
   * completely replace an existing Group's membership with a new Set. Normally,
   * the caller would get the Set from {@link #getMembers()} method and mutate
   * that Set, then persist the changes to the Group's membership by calling
   * {@link AgentGroupManager#save(Agent)}.
   *  
   * @param members
   */
  public void setMembers(Set members);
}



