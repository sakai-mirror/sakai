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

package org.sakaiproject.api.common.type;

/**
 * If a Manager does not support a given Type, this would be an appropriate
 * exception to throw.
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @see {@link java.lang.Error}
 * @version $Id$
 */
public class UnsupportedTypeException extends Error
{
  private static final long serialVersionUID = 3258132466203242544L;

  /**
   * 
   */
  public UnsupportedTypeException()
  {
    super();
  }

  /**
   * @param message
   * @param cause
   */
  public UnsupportedTypeException(String message, Throwable cause)
  {
    super(message, cause);
  }

  /**
   * @param message
   */
  public UnsupportedTypeException(String message)
  {
    super(message);
  }

  /**
   * @param cause
   */
  public UnsupportedTypeException(Throwable cause)
  {
    super(cause);
  }
}



