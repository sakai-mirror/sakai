package org.sakaiproject.component.app.scheduler;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.sakaiproject.api.app.scheduler.SchedulerManager;
import org.sakaiproject.service.framework.sql.SqlService;

public class SchedulerManagerImpl implements SchedulerManager
{

  private BasicDataSource dataSource;
  private String serverId;
  private Set qrtzJobs;
  private Map qrtzQualifiedJobs = new TreeMap(); // map for SelectItems
  private String qrtzPropFile;
  private Properties qrtzProperties;
  private TriggerListener globalTriggerListener;
  private Boolean autoDdl;

  private static final String JOB_INTERFACE = "org.quartz.Job";

  private SchedulerFactory schedFactory;
  private Scheduler scheduler;
  private static final Log LOG = LogFactory.getLog(SchedulerManagerImpl.class);

public void init()
  {

    try
    {
      
      SqlService sqlService = org.sakaiproject.service.framework.sql.cover.SqlService
      .getInstance();

      // load quartz properties file
      InputStream propertiesInputStream = this.getClass().getResourceAsStream(
          qrtzPropFile);
      qrtzProperties = new Properties();
      qrtzProperties.load(propertiesInputStream);

      // now replace properties from those loaded in from components.xml
      qrtzProperties.setProperty("org.quartz.dataSource.myDS.driver",
          dataSource.getDriverClassName());
      qrtzProperties.setProperty("org.quartz.dataSource.myDS.URL", dataSource
          .getUrl());
      qrtzProperties.setProperty("org.quartz.dataSource.myDS.user", dataSource
          .getUsername());
      qrtzProperties.setProperty("org.quartz.dataSource.myDS.password",
          dataSource.getPassword());
      qrtzProperties.setProperty("org.quartz.scheduler.instanceId", serverId);
      
      if ("hsqldb".equalsIgnoreCase(sqlService.getVendor())){
        qrtzProperties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.HSQLDBDelegate"); 
      }
      else if ("mysql".equalsIgnoreCase(sqlService.getVendor())){
        qrtzProperties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
      }
      else if ("oracle".equalsIgnoreCase(sqlService.getVendor())){
        qrtzProperties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.oracle.OracleDelegate");
      }
      else{
        LOG.warn("sakai vendor not supported");
      }

      // note: becuase job classes are jarred , it is impossible to iterate
      // through a directory by calling listFiles on a file object.
      // Therefore, we need the class list list from spring.

      // find quartz jobs from specified 'qrtzJobs' and verify they
      // that these jobs implement the Job interface
      Iterator qrtzJobsIterator = qrtzJobs.iterator();
      while (qrtzJobsIterator.hasNext())
      {
        String className = (String) qrtzJobsIterator.next();
        Class cl = null;
        try
        {
          cl = Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
          LOG.warn("Could not locate class: " + className + " on classpath");
        }
        if (cl != null)
        {
          // check that each class implements the Job interface           
          if (doesImplementJobInterface(cl))
          {
            qrtzQualifiedJobs.put(cl.getName(), cl.getName());
          }
          else
          {
            LOG.warn("Class: " + className
                + " does not implement quartz Job interface");
          }
        }
      }

      // run ddl            
      if (autoDdl.booleanValue()){
        try
        {                        
           sqlService.ddl(this.getClass().getClassLoader(), "quartz");
        }       
        catch (Throwable t)
        {
          LOG.warn(this + ".init(): ", t);
        }
      }

      // start scheduler and load jobs                 
      schedFactory = new StdSchedulerFactory(qrtzProperties);
      scheduler = schedFactory.getScheduler();

      // loop through persisted jobs removing both the job and associated
      // triggers for jobs where the associated job class is not found
      String[] arrJobs = scheduler.getJobNames(Scheduler.DEFAULT_GROUP);
      for (int i = 0; i < arrJobs.length; i++)
      {
        try
        {
          JobDetail jd = scheduler.getJobDetail(arrJobs[i],
              Scheduler.DEFAULT_GROUP);
        }
        catch (SchedulerException e)
        {
          LOG.warn("scheduler cannot load class for persistent job:"
              + arrJobs[i]);
          scheduler.deleteJob(arrJobs[i], Scheduler.DEFAULT_GROUP);
        }
      }

      scheduler.addGlobalTriggerListener(globalTriggerListener);
      scheduler.start();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new Error("Scheduler cannot start!");
    }

  }  private boolean doesImplementJobInterface(Class cl)
  {
    Class[] classArr = cl.getInterfaces();
    for (int i = 0; i < classArr.length; i++)
    {
      if (classArr[i].getName().equals(JOB_INTERFACE))
      {
        return true;
      }
    }
    return false;
  }
  
  
  /**
   * @see org.sakaiproject.api.app.scheduler.SchedulerManager#destroy()
   */
  public void destroy()
  {   
    try{
      if (!scheduler.isShutdown()){
        scheduler.shutdown();
      }
    }       
    catch (Throwable t){
      LOG.error("An error occurred while stopping the scheduler", t);
    }    
  }
  
  
  /**
   * @return Returns the globalTriggerListener.
   */
  public TriggerListener getGlobalTriggerListener()
  {
    return globalTriggerListener;
  }

  /**
   * @param globalTriggerListener The globalTriggerListener to set.
   */
  public void setGlobalTriggerListener(TriggerListener globalTriggerListener)
  {
    this.globalTriggerListener = globalTriggerListener;
  }

  /**
   * @return Returns the serverId.
   */
  public String getServerId()
  {
    return serverId;
  }

  /**
   * @param serverId The serverId to set.
   */
  public void setServerId(String serverId)
  {
    this.serverId = serverId;
  }

  /**
   * @return Returns the dataSource.
   */
  public BasicDataSource getDataSource()
  {
    return dataSource;
  }

  /**
   * @param dataSource The dataSource to set.
   */
  public void setDataSource(BasicDataSource dataSource)
  {
    this.dataSource = dataSource;
  }

  /**
   * @return Returns the qrtzQualifiedJobs.
   */
  public Map getQrtzQualifiedJobs()
  {
    return qrtzQualifiedJobs;
  }

  /**
   * @param qrtzQualifiedJobs The qrtzQualifiedJobs to set.
   */
  public void setQrtzQualifiedJobs(Map qrtzQualifiedJobs)
  {
    this.qrtzQualifiedJobs = qrtzQualifiedJobs;
  }

  /**
   * @return Returns the qrtzJobs.
   */
  public Set getQrtzJobs()
  {
    return qrtzJobs;
  }

  /**
   * @param qrtzJobs The qrtzJobs to set.
   */
  public void setQrtzJobs(Set qrtzJobs)
  {
    this.qrtzJobs = qrtzJobs;
  }

  /**
   * @return Returns the qrtzPropFile.
   */
  public String getQrtzPropFile()
  {
    return qrtzPropFile;
  }

  /**
   * @param qrtzPropFile The qrtzPropFile to set.
   */
  public void setQrtzPropFile(String qrtzPropFile)
  {
    this.qrtzPropFile = qrtzPropFile;
  }

  /**
   * @return Returns the scheduler.
   */
  public Scheduler getScheduler()
  {
    return scheduler;
  }

  /**
   * @param sched The sched to set.
   */
  public void setScheduler(Scheduler scheduler)
  {
    this.scheduler = scheduler;
  }

  /**
   * @see org.sakaiproject.api.app.scheduler.SchedulerManager#setAutoDdl(java.lang.Boolean)
   */
  public void setAutoDdl(Boolean b)
  {
    autoDdl = b;
  }
}