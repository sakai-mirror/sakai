package org.sakaiproject.component.junit.spring;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * You should extend this class to get the Spring bean factory (ApplicationContext) initialized.
 * Currently, it loads the components.xml from shared components and a "local" components.xml from the
 * root of the classpath. See maven's project.xml on how to include your local components.xml in the
 * root of the units tests' classpath through the use of <resources/>.
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id: ApplicationContextBaseTest.java,v 1.2 2005/01/05 18:57:10 jlannan.iupui.edu Exp $
 */
public class ApplicationContextBaseTest extends TestCase
{
  private static final String LOCAL_COMPONENTS_XML = "components.xml";
  private static final String SAKAI_BASEDIR_PROPERTY = "sakaiproject.basedir";
  private static final String SAKAI_SHARED_COMPONENTS = "shared/src/webapp/WEB-INF/components.xml";
  private static final String SAKAI_FRAMEWORK_COMPONENTS = "framework-component/src/webapp/WEB-INF/components.xml";
  private static final String FILE_SAKAI_SHARED_COMPONENTS;
  private static final String FILE_SAKAI_FRAMEWORK_COMPONENTS;
  private static final String DEFAULT_BASEDIR = "/java/projects/sakai";
  private static final String[] CTX_LOCATIONS;

  private static final Log LOG = LogFactory
      .getLog(ApplicationContextBaseTest.class);
  private ApplicationContext applicationContext;

  static
  {
      //Enable this line to get log4j output to STDOUT
      //BasicConfigurator.configure();

    String baseDir = System.getProperty(SAKAI_BASEDIR_PROPERTY);
    if (baseDir != null)
    {
      FILE_SAKAI_SHARED_COMPONENTS = baseDir + File.separator
          + SAKAI_SHARED_COMPONENTS;
      
      FILE_SAKAI_FRAMEWORK_COMPONENTS = baseDir + File.separator
          + SAKAI_FRAMEWORK_COMPONENTS;
    }
    else
    {
      FILE_SAKAI_SHARED_COMPONENTS = DEFAULT_BASEDIR + File.separator
          + SAKAI_SHARED_COMPONENTS;
      
      FILE_SAKAI_FRAMEWORK_COMPONENTS = DEFAULT_BASEDIR + File.separator
          + SAKAI_FRAMEWORK_COMPONENTS;
    }
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
    
    //  load shared first
    File fileSharedComponents = new File(FILE_SAKAI_SHARED_COMPONENTS);
    File fileFrameworkComponents = new File(FILE_SAKAI_FRAMEWORK_COMPONENTS);
    try
    {
      String[] appContexts = { fileSharedComponents.getAbsolutePath(),
          fileFrameworkComponents.getAbsolutePath() };
        
      // load external components.xml files
      applicationContext = new FileSystemXmlApplicationContext(appContexts);
      if (appContexts == null)
      {
        throw new IllegalStateException();
      }
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
}