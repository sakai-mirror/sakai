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

package org.sakaiproject.component.legacy.event;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.legacy.event.Event;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id$
 */
public class EventImpl implements Event, Serializable
{
  private static final Log LOG = LogFactory.getLog(EventImpl.class);

  /**
   * Be a good Serializable citizen
   */
  private static final long serialVersionUID = 3906364913784862001L;

  private String event;
  private String resource;
  private String sessionId;
  private String userId;
  private boolean modify;
  private int priority;
  private long seq = 0;

  public EventImpl(long seq, String event, String resource, boolean modify,
      int priority)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("new EventImpl(long " + seq + ", String " + event + ", String "
          + resource + ", boolean " + modify + ", int " + priority + ")");
    }

    this.seq = seq;
    this.event = event;
    this.resource = resource;
    this.modify = modify;
    this.priority = priority;
  }

  public EventImpl(String event, String resource, boolean modify, int priority)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("EventImpl(String " + event + ", String " + resource
          + ", boolean " + modify + ", int " + priority + ")");
    }

    this.event = event;
    this.resource = resource;
    this.modify = modify;
    this.priority = priority;
  }

  public String getEvent()
  {
    return event;
  }

  public String getResource()
  {
    return resource;
  }

  public String getSessionId()
  {
    return sessionId;
  }

  public String getUserId()
  {
    return userId;
  }

  public boolean getModify()
  {
    return modify;
  }

  public int getPriority()
  {
    return priority;
  }

  /**
   * @return Returns the seq.
   */
  long getSeq()
  {
    return seq;
  }

  /**
   * @param seq The seq to set.
   */
  void setSeq(long seq)
  {
    this.seq = seq;
  }

  /**
   * @param event The event to set.
   */
  void setEvent(String event)
  {
    this.event = event;
  }

  /**
   * @param modify The modify to set.
   */
  void setModify(boolean modify)
  {
    this.modify = modify;
  }

  /**
   * @param priority The priority to set.
   */
  void setPriority(int priority)
  {
    this.priority = priority;
  }

  /**
   * @param resource The resource to set.
   */
  void setResource(String resource)
  {
    this.resource = resource;
  }

  /**
   * @param sessionId The sessionId to set.
   */
  void setSessionId(String sessionId)
  {
    this.sessionId = sessionId;
  }

  /**
   * @param userId The userId to set.
   */
  void setUserId(String userId)
  {
    this.userId = userId;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
    if (obj == this) return true;
    if (!(obj instanceof EventImpl)) return false;
    EventImpl ei = (EventImpl) obj;
    return this.businessKey().equals(ei.businessKey());
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    return businessKey().hashCode();
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("{event=");
    sb.append(event);
    sb.append(", resource=");
    sb.append(resource);
    sb.append(", modify=");
    sb.append(modify);
    sb.append(", priority=");
    sb.append(priority);
    sb.append("}");
    return sb.toString();
  }

  private String businessKey()
  {
    return event + resource + modify + priority + seq;
  }

}



