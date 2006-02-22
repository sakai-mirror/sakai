/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy-service/service/src/java/org/sakaiproject/exception/InconsistentException.java $
 * $Id: InconsistentException.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.metaobj.utils.mvc.intf;

import java.util.Map;

/*
 * $URL: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/utils/mvc/intf/CustomCommandController.java,v 1.1 2005/06/28 13:31:53 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/28 13:31:53 $
 */

/**
 * Use this controller when you need to override the way the backing object is created.
 * If all you are doing is return new MyBackingObject() you won't need to implement this,
 * Spring will handle that for you.  Do not use this method to populate helping data
 * into the model.  This method does not get called if there is an error in validation.
 * If you rely on this information for putting stuff in the request, you will not have
 * it there in the case of validation error.  If you need to put helping data in the model,
 * implement FormController's referenceData method.
 *
 * @author John Ellis (john.ellis@rsmart.com)
 * @author John Bush (john.bush@rsmart.com)
 * @see Controller
 */
public interface CustomCommandController extends Controller {
   public Object formBackingObject(Map request,
                                   Map session,
                                   Map application);
}
