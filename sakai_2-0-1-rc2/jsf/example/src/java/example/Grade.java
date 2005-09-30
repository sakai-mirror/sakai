/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/example/src/java/example/Grade.java,v 1.3 2005/05/10 03:36:58 esmiley.stanford.edu Exp $
*
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

package example;

import java.io.Serializable;

/**
 * <p>Data for example applications.</p>
 * <p> </p>
 * <p>Copyright: Copyright  Sakai (c) 2005</p>
 * <p> </p>
 * @author Ed Smiley
 * @version $Id$
 */

public class Grade implements Serializable {
  private String name;
  private String score;
  public Grade() {
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getScore() {
    return score;
  }
  public void setScore(String score) {
    this.score = score;
  }

}

/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/example/src/java/example/Grade.java,v 1.3 2005/05/10 03:36:58 esmiley.stanford.edu Exp $
*
**********************************************************************************/
