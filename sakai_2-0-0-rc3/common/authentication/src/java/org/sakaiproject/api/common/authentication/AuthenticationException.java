/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/authentication/src/java/org/sakaiproject/api/common/authentication/AuthenticationException.java,v 1.3 2005/05/12 16:36:40 lance.indiana.edu Exp $
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

package org.sakaiproject.api.common.authentication;

/**
 * <p>
 * AuthenticationException models errors in authentication.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.3 $
 */
public class AuthenticationException extends Exception
{
	/**
   * 
   */
  private static final long serialVersionUID = 3977014054785135157L;

  public AuthenticationException()
	{
		super();
	}

	public AuthenticationException(String msg)
	{
		super(msg);
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/common/authentication/src/java/org/sakaiproject/api/common/authentication/AuthenticationException.java,v 1.3 2005/05/12 16:36:40 lance.indiana.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
