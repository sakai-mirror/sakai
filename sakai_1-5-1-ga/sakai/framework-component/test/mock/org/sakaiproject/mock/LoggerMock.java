/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/test/mock/org/sakaiproject/mock/LoggerMock.java,v 1.1 2004/08/25 13:38:25 brettm.umich.edu Exp $
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

package org.sakaiproject.mock;
import org.sakaiproject.service.framework.log.Logger;

/**
 * Mock object for logger
 */
public class LoggerMock implements Logger
{

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#isTraceEnabled()
     */
    public boolean isTraceEnabled()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#isDebugEnabled()
     */
    public boolean isDebugEnabled()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#isInfoEnabled()
     */
    public boolean isInfoEnabled()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#isWarnEnabled()
     */
    public boolean isWarnEnabled()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#isErrorEnabled()
     */
    public boolean isErrorEnabled()
    {
         return false;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#isFatalEnabled()
     */
    public boolean isFatalEnabled()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#trace(java.lang.Object)
     */
    public void trace(Object arg0)
    {
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#trace(java.lang.Object, java.lang.Throwable)
     */
    public void trace(Object arg0, Throwable arg1)
    {
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#debug(java.lang.Object)
     */
    public void debug(Object arg0)
    {
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#debug(java.lang.Object, java.lang.Throwable)
     */
    public void debug(Object arg0, Throwable arg1)
    {
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#info(java.lang.Object)
     */
    public void info(Object arg0)
    {
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#info(java.lang.Object, java.lang.Throwable)
     */
    public void info(Object arg0, Throwable arg1)
    {
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#warn(java.lang.Object)
     */
    public void warn(Object arg0)
    {
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#warn(java.lang.Object, java.lang.Throwable)
     */
    public void warn(Object arg0, Throwable arg1)
    {
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#error(java.lang.Object)
     */
    public void error(Object arg0)
    {
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#error(java.lang.Object, java.lang.Throwable)
     */
    public void error(Object arg0, Throwable arg1)
    {
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#fatal(java.lang.Object)
     */
    public void fatal(Object arg0)
    {
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.log.Logger#fatal(java.lang.Object, java.lang.Throwable)
     */
    public void fatal(Object arg0, Throwable arg1)
    {
   }

}
