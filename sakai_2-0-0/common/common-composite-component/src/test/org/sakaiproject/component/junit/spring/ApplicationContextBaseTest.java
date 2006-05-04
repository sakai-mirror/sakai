package org.sakaiproject.component.junit.spring;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import org.springframework.orm.hibernate.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * You should extend this class to get the Spring bean factory (ApplicationContext) initialized.
 * Currently, it loads the components.xml from shared components and a "local" components.xml from the
 * root of the classpath. See maven's project.xml on how to include your local components.xml in the
 * root of the units tests' classpath through the use of <resources/>.
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id: ApplicationContextBaseTest.java,v 1.1 2005/05/11 15:38:42 lance.indiana.edu Exp $
 */
public class ApplicationContextBaseTest extends TestCase
{
  private static final String LOCAL_COMPONENTS_XML = "components-test.xml";
  private static final String SAKAI_BASEDIR_PROPERTY = "sakaiproject.basedir";
  private static final String DEFAULT_BASEDIR = "/java/projects";
  private static final String[] CTX_LOCATIONS;

  private static final Log LOG = LogFactory
      .getLog(ApplicationContextBaseTest.class);
  private ApplicationContext applicationContext;

  static
  {
    String baseDir = System.getProperty(SAKAI_BASEDIR_PROPERTY);
    CTX_LOCATIONS = new String[] { LOCAL_COMPONENTS_XML };

  }

  public ApplicationContextBaseTest()
  {
    super();
    init();
  }

  public ApplicationContextBaseTest(String name)
  {
    super(name);
    init();
  }

  private void init()
  {
    try
    {
      // load local components and add parent      
      applicationContext = new ClassPathXmlApplicationContext(CTX_LOCATIONS,
          applicationContext);
      if (applicationContext == null)
      {
        throw new IllegalStateException();
      }
      LOG.debug(getApplicationContextInfo());
    }
    catch (BeansException e)
    {
      throw new Error(e);
    }
  }

  public String getApplicationContextInfo()
  {
    StringBuffer sb = new StringBuffer();
    appendApplicationContextInfo(sb, getApplicationContext());
    ApplicationContext ac = getApplicationContext().getParent();
    while (true)
    {
      if (ac != null)
      {
        sb.append("Parent ApplicationContext info:\n");
        appendApplicationContextInfo(sb, ac);
        ac = ac.getParent();
      }
      else
      {
        break;
      }
    }
    return sb.toString();
  }

  private void appendApplicationContextInfo(StringBuffer sb,
      ApplicationContext applicationContext)
  {
    sb.append("ApplicationContext=" + applicationContext + "\n");
    List l = Arrays.asList(applicationContext.getBeanDefinitionNames());
    Collections.sort(l);
    for (Iterator iter = l.iterator(); iter.hasNext();)
    {
      Object element = iter.next();
      sb.append("\t" + element + "\n");
    }
  }

  public static void main(String[] args)
  {
    ApplicationContextBaseTest acb = new ApplicationContextBaseTest();
    LOG.debug(acb.getApplicationContextInfo());
  }

  /**
   * @return Returns the applicationContext.
   */
  protected ApplicationContext getApplicationContext()
  {
    return applicationContext;
  }

  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
    SessionFactory sessionFactory = (SessionFactory) getApplicationContext()
        .getBean("org.springframework.orm.hibernate.SessionFactory");
    Session s = sessionFactory.openSession();
    TransactionSynchronizationManager.bindResource(sessionFactory,
        new SessionHolder(s));
  }

  /**
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
    SessionFactory sessionFactory = (SessionFactory) getApplicationContext()
        .getBean("org.springframework.orm.hibernate.SessionFactory");
    SessionHolder holder = (SessionHolder) TransactionSynchronizationManager
        .getResource(sessionFactory);
    Session s = holder.getSession();
    s.flush();
    TransactionSynchronizationManager.unbindResource(sessionFactory);
    SessionFactoryUtils.closeSessionIfNecessary(s, sessionFactory);
  }
}