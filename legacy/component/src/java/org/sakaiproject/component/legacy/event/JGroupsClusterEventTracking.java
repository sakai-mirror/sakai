/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
import org.jgroups.Address;
import org.jgroups.blocks.NotificationBus;
import org.jgroups.blocks.NotificationBus.Consumer;
import org.sakaiproject.jgroups.EventImpl;
import org.sakaiproject.service.framework.session.UsageSession;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.EventTrackingService;
import org.sakaiproject.service.legacy.notification.NotificationService;
import org.sakaiproject.service.legacy.user.User;

/**
 * <p>ClusterEventTracking is the implmentation for the EventTracking event
 * tracking service for use in a clustered multi-app server configuration.  
 * Events are backed by a JGroups NotificationBus.</p>
 *
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id$
 */
public class JGroupsClusterEventTracking extends BaseEventTrackingService
{
  private String props;
  
  private static final Log LOG = LogFactory
      .getLog(JGroupsClusterEventTracking.class);
  private static final Log DECLOG = LogFactory
      .getLog(DefaultEventConsumer.class);

  private String channelName = EventTrackingService.class.getName();
  private NotificationBus bus;

  private Consumer eventConsumer = new DefaultEventConsumer();

  /**
   * This method is <b>required</b> to run at startup time.
   * 
   * @see org.sakaiproject.component.legacy.event.BaseEventTrackingService#init()
   */
  public void init()
  {
    super.init();

    LOG.debug("initit()");

    if (eventConsumer == null)
      throw new IllegalStateException("eventConsumer == null");

    try
    {
      // Create the bus and connect to the specified channel name
      if ("default".equals(props) || props == null){
        bus = new NotificationBus(channelName);  
      }
      else{
        bus = new NotificationBus(channelName, props);
      }
      
      
      // register the consumer (the event handler)
      bus.setConsumer(eventConsumer);
      // start the bus
      bus.start();
    }
    catch (Exception e)
    {
      LOG.fatal(e.getMessage(), e);
      throw new Error(e);
    }
  }

  /**
   * This method <i>should</i> be run at shutdown time.
   * 
   * @see org.sakaiproject.component.legacy.event.BaseEventTrackingService#destroy()
   */
  public void destroy()
  {
    super.destroy();

    LOG.debug("destroy()");

    // stop the bus
    if (bus != null) bus.stop();
  }

  /**
   * @see org.sakaiproject.component.legacy.event.BaseEventTrackingService#postEvent(org.sakaiproject.service.legacy.event.Event)
   */
  protected void postEvent(Event event)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("postEvent(Event " + event + ")");
    }
    if (event == null)
      throw new IllegalArgumentException("Illegal event argument!");

    // send the message to all members
    //notifyObservers(event, true);
    bus.sendNotification((Serializable) event);
  }

  /**
   * A systems integrator may want to consider changing the channel name if in
   * the unlikely event a conflict occurs. By default the channelName is set to
   * {@link EventTrackingService}.class.getName().
   * 
   * @param channelName The channelName to set.
   */
  public void setChannelName(String channelName)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setChannelName(String " + channelName + ")");
    }
    if (channelName == null || channelName.length() < 1)
      throw new IllegalArgumentException("Illegal channelName argument!");

    this.channelName = channelName;
  }

  /**
   * This method is used to override the default Consumer: 
   * {@link JGroupsClusterEventTracking.DefaultEventConsumer}. The specified
   * eventConsumer will be registered as a Consumer with the NotificationBus.
   * 
   * @param eventConsumer The eventConsumer to set.
   */
  public void setEventConsumer(Consumer eventConsumer)
  {
    if (eventConsumer == null)
      throw new IllegalArgumentException("Illegal eventConsumer argument!");

    this.eventConsumer = eventConsumer;
  }

  /**
   * @see org.sakaiproject.component.legacy.event.BaseEventTrackingService#newEvent(java.lang.String, java.lang.String, boolean, int)
   */
  public Event newEvent(String event, String resource, boolean modify,
      int priority)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("newEvent(String " + event + ", String " + resource
          + ", boolean " + modify + ", int " + priority + ")");
    }

    return new EventImpl(event, resource, modify, priority);
  }

  /**
   * @see org.sakaiproject.component.legacy.event.BaseEventTrackingService#newEvent(java.lang.String, java.lang.String, boolean)
   */
  public Event newEvent(String event, String resource, boolean modify)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("newEvent(String " + event + ", String " + resource
          + ", boolean " + modify + ")");
    }

    return new EventImpl(event, resource, modify,
        NotificationService.NOTI_OPTIONAL);
  }

  /**
   * @see org.sakaiproject.component.legacy.event.BaseEventTrackingService#post(org.sakaiproject.service.legacy.event.Event, org.sakaiproject.service.framework.session.UsageSession)
   */
  public void post(Event event, UsageSession session)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("post(Event " + event + ", UsageSession " + session + ")");
    }

    String id = "?";
    if (session != null) id = session.getId();

    ((EventImpl) event).setSessionId(id);
    postEvent(event);
  }

  /**
   * @see org.sakaiproject.component.legacy.event.BaseEventTrackingService#post(org.sakaiproject.service.legacy.event.Event, org.sakaiproject.service.legacy.user.User)
   */
  public void post(Event event, User user)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("post(Event " + event + ", User " + user + ")");
    }

    String id = "?";
    if (user != null) id = user.getId();

    ((EventImpl) event).setUserId(id);
    postEvent(event);
  }

  /**
   * @see org.sakaiproject.component.legacy.event.BaseEventTrackingService#post(org.sakaiproject.service.legacy.event.Event)
   */
  public void post(Event event)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("post(Event " + event + ")");
    }

    // get the session id or user id
    String id = UsageSessionService.getSessionId();
    if (id != null)
    {
      ((EventImpl) event).setSessionId(id);
      postEvent(event);
    }

    // post for the session "thread" user
    else
    {
      id = UsageSessionService.getSessionUserId();
      if (id == null)
      {
        id = "?";
      }

      ((EventImpl) event).setUserId(id);
      postEvent(event);
    }
  }
    
  /**
   * get properties for JGroups protocol stack
   * @return
   */
  public String getProps()
  {
    return props;
  }

  /**
   * set custom JGroups protocol stack
   * @param props
   */
  public void setProps(String props)
  {
    this.props = props;
  }

  public class DefaultEventConsumer implements Consumer
  {
    public DefaultEventConsumer()
    {
      LOG.debug("new DefaultEventConsumer()");
    }

    public void handleNotification(Serializable notification)
    {
      if (DECLOG.isDebugEnabled())
      {
        DECLOG.debug("handleNotification(Serializable " + notification + ")");
      }

      if (notification != null)
      {
        if (notification instanceof Event)
        {
          notifyObservers((Event) notification, false);
        }
        else
        {
          LOG.error("Unknown event type received!: " + notification);
        }
      }
    }

    public Serializable getCache()
    {
      DECLOG.trace("getCache()");

      return null; // per API
    }

    public void memberJoined(Address mbr)
    {
      if (DECLOG.isDebugEnabled())
      {
        DECLOG.debug("memberJoined(Address " + mbr + ")");
      }

      DECLOG.info("JGroups member joined: " + mbr);
    }

    public void memberLeft(Address mbr)
    {
      if (DECLOG.isDebugEnabled())
      {
        DECLOG.debug("memberLeft(Address " + mbr + ")");
      }

      DECLOG.info("JGroups member left: " + mbr);
    }

  } // DefaultEventConsumer

}



