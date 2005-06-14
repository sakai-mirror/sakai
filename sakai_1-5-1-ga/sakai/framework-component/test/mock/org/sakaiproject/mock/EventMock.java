/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/test/mock/org/sakaiproject/mock/EventMock.java,v 1.1 2004/08/25 13:38:25 brettm.umich.edu Exp $
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

import org.sakaiproject.service.legacy.event.Event;

/**
 * Mock Object for Event
 */
public class EventMock implements Event
{
    private String event;
    private String resource;
    private String sessionId;
    private String userId;
    private boolean modify;
    private int priority;
    
    /**
     * @param event
     * @param resource
     * @param sessionId
     * @param userId
     * @param modify
     * @param priority
     */
    public EventMock(String event, String resource, String sessionId,
            String userId, boolean modify, int priority)
    {
        this.event = event;
        this.resource = resource;
        this.sessionId = sessionId;
        this.userId = userId;
        this.modify = modify;
        this.priority = priority;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sakaiproject.service.legacy.event.Event#getEvent()
     */
    public String getEvent()
    {
        return event;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.legacy.event.Event#getResource()
     */
    public String getResource()
    {
        return resource;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.legacy.event.Event#getSessionId()
     */
    public String getSessionId()
    {
        return sessionId;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.legacy.event.Event#getUserId()
     */
    public String getUserId()
    {
         return userId;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.legacy.event.Event#getModify()
     */
    public boolean getModify()
    {
        return modify;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.legacy.event.Event#getPriority()
     */
    public int getPriority()
    {
        return priority;
    }
}
