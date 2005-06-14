/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /cvs/help/component/src/java/org/sakaiproject/component/help/HelpManagerImpl.java,v 1.7 2005/02/07 19:39:52 casong.indiana.edu Exp $
 * $Revision: 1.7 $
 * $Date: 2005/02/07 19:39:52 $
 */
package org.sakaiproject.component.help;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.osid.id.IdException;
import org.osid.id.IdManager;
import org.osid.shared.Id;
import org.osid.shared.SharedException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.FileSystemResource;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.HibernateObjectRetrievalFailureException;
import org.sakaiproject.component.help.model.CategoryBean;
import org.sakaiproject.component.help.model.ResourceBean;
import org.sakaiproject.component.help.model.TableOfContentsBean;
import org.sakaiproject.component.help.model.ContextBean;
import org.sakaiproject.component.help.model.SourceBean;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.config.ToolRegistration;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.help.Category;
import org.sakaiproject.service.help.Context;
import org.sakaiproject.service.help.Glossary;
import org.sakaiproject.service.help.GlossaryEntry;
import org.sakaiproject.service.help.HelpManager;
import org.sakaiproject.service.help.Resource;
import org.sakaiproject.service.help.Source;
import org.sakaiproject.service.help.TableOfContents;
import org.sakaiproject.component.help.SizedList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

/**
 * This implementation uses the spring config to configure the system, and
 * uses as a database for indexing resources, and configuring which contexts
 * are associated with what resources.  Lucene is also responsible for
 * performing help searches.
 *
 * <br/><br/>
 *
 * Contexts are mapped to views in the spring config.  To do this, define
 * a bean of type, org.theospi.portfolio.help.model.HelpContextConfig.
 * Create a map of contexts which are keyed by the view name.  Contexts are
 * just string ids.  An example:
 * <br/><br/>
 *   &lt;bean id="presentationHelpContexts" class="org.theospi.portfolio.help.model.HelpContextConfig"&gt;<br/>
 *     &lt;constructor-arg&gt;<br/>
 *        &lt;map&gt;<br/>
 *          &lt;entry key="addPresentation1"&gt;<br/>
 *              &lt;list&gt;                          <br/>
 *                 &lt;value&gt;Creating a Presentation&lt;/value&gt;<br/>
 *              &lt;/list&gt;                                             <br/>
 *           &lt;/entry&gt;                                                    <br/>
 *  ...
 *  <br/><br/>
 * An explanation: what this means is that when a user navigates to the
 * addPresentation1 view a context called "Creating a Presentation" is created.
 * This context is just an identifier for possible actions the user might perform
 * from this page.
 *  <br/><br/>
 * To create resources define a bean of type, org.theospi.portfolio.help.model.Resource.
 * The name is the display name that is shown on jsp pages.  The location is
 * the url of the resource.  Configure all contexts associated with this resource.
 * An example,
 *   <br/><br/>
 *    &lt;bean id="pres_resource_2" class="org.theospi.portfolio.help.model.Resource"&gt; <br/>
 *     &lt;property name="name"&gt;&lt;value&gt;Creating a Presentation&lt;/value&gt;&lt;/property&gt;   <br/>
 *     &lt;property name="location"&gt;&lt;value&gt;${system.baseUrl}/help/creatingPresentations.html&lt;/value&gt;&lt;/property&gt;<br/>
 *     &lt;property name="contexts"&gt;<br/>
 *        &lt;list&gt;<br/>
 *           &lt;value&gt;Creating a Presentation&lt;/value&gt;<br/>
 *        &lt;/list&gt;<br/>
 *     &lt;/property&gt;<br/>
 *  &lt;/bean&gt;<br/>
 * <br/><br/>
 * If all this is configured correctly, when a user navigates to the addPresentation1
 * view a context of "Creating a Presentation" is created.  If the user navigates
 * to help, the user will be presented with links to all the resources associated with
 * this context.
 * <br/><br/>
 *
 * @see org.sakaiproject.component.help.model.ResourceBean
 * @see org.sakaiproject.component.help.model.SourceBean
 *
 */
public class HelpManagerImpl extends HibernateDaoSupport implements HelpManager, ApplicationContextAware {
  
   private static final String QUERY_GETRESOURCEBYDOCID = "query.getResourceByDocId";
   private static final String DOCID ="docId";
   private Map helpContextConfig = new HashMap();
   private int contextSize;
//   protected final Log logger = LogFactory.getLog(getClass());
   private boolean indexOnStartup;
   private TableOfContentsBean toc;
   private boolean initialized = false;
   private Glossary glossary;
   private IdManager idManager;
   private ServerConfigurationService serverConfigurationService;
   private Logger logger;
   private String supportEmailAddress;
   
   public void init()
   {
     System.out.println("**** init() helpManagerImpl");
   }

  public void destroy()
   {
     System.out.println("**** destroy() helpManagerImpl");
   }

   public List getContexts(String mappedView) {
      return (List) helpContextConfig.get(mappedView);
   }

   public List getActiveContexts(Map session) {
      List contexts = (List) session.get("help_contexts");
      if (contexts == null) {
         contexts = new SizedList(getContextSize());
         session.put("help_contexts", contexts);
      }
      return contexts;
   }

   public void addContexts(Map session, String mappedView) {
      List newContexts = getContexts(mappedView);
      List contexts = getActiveContexts(session);
      if (newContexts != null) {
         contexts.addAll(newContexts);
      }
   }

   /**
    * return list of resources matching context id
    *
    * @param contextId
    * @return
    */
   public Set getResources(Id contextId) {
      ContextBean context = (ContextBean)getContext(contextId);
      return searchResources(new TermQuery(new Term("context", "\"" + context.getId() + "\"")));
   }

   public void storeResource(Resource resource) {
      getHibernateTemplate().saveOrUpdate(resource);
   }

   public Resource getResource(Id id){
      try {
        Long idLong = new Long(id.getIdString());
         return (ResourceBean) getHibernateTemplate().load(ResourceBean.class, idLong);
      } catch (HibernateObjectRetrievalFailureException e){
         return null;
      } catch(SharedException e)
      {
        return null;
      }
       
   }

   public void deleteResource(Id resourceId) {
      Resource resource = getResource(resourceId);
      if (resource == null) return;
      getHibernateTemplate().delete(resource);
  }

   public Source getSource(Id id) {
      try {
        Long idLong = new Long(id.getIdString());
         return (SourceBean) getHibernateTemplate().load(SourceBean.class, idLong);
      } catch (HibernateObjectRetrievalFailureException e){
         return null;
      } catch(SharedException e)
      {
        return null;
      }
   }

   public void storeSource(Source source) {
      getHibernateTemplate().saveOrUpdate(source);
   }

   public void deleteSource(Id sourceId) {
      Source source = getSource(sourceId);
      if (source == null) return;
      getHibernateTemplate().delete(source);
   }

   public Context getContext(Id id) {
      try {
        Long idLong = new Long(id.getIdString());
         return (ContextBean) getHibernateTemplate().load(ContextBean.class, id);
      } catch (HibernateObjectRetrievalFailureException e){
         return null;
      } catch(SharedException e)
      {
        return null;
      }   
   }

   public void storeContext(Context context) {
      getHibernateTemplate().saveOrUpdate(context);
   }

   public void deleteContext(Id contextId) {
      Context context = getContext(contextId);
      if (context == null) return;
      getHibernateTemplate().delete(context);   }

   public Map getResourcesForActiveContexts(Map session) {
      Map resourceMap = new HashMap();
      List activeContexts = getActiveContexts(session);
      for (Iterator i = activeContexts.iterator(); i.hasNext();) {
         String context = (String) i.next();
         try {
            Set resources = searchResources(new TermQuery(new Term("context", "\"" + context + "\"")));
            if (resources != null && resources.size() > 0) {
               resourceMap.put(context, resources);
            }
         } catch (Exception e) {
            logger.error(e);
         }
      }
      return resourceMap;
   }

   public Set searchResources(String queryStr) {
      try {
         return searchResources(queryStr, "content");
      } catch (ParseException e) {
         throw new RuntimeException(e);
      }
   }

   public TableOfContents getTableOfContents() {
      return getToc();
   }

   public GlossaryEntry searchGlossary(String keyword) {
      return getGlossary().find(keyword);
   }

   protected Set searchResources(Query query){
      if (!initialized){
         try {
            initResources();
         } catch (IOException e) {
            logger.error("problem initializing help resources: " + e.getMessage());
         }
         catch (SharedException e) {
             logger.error("problem initializing help resources: " + e.getMessage());
          }
      }

      Set results = new HashSet();
      try {
         Searcher searcher = new IndexSearcher("index");
         logger.debug("Searching for: " + query.toString());

         Hits hits = searcher.search(query);
         logger.debug(hits.length() + " total matching documents");

         for (int i = 0; i < hits.length(); i++) {
            ResourceBean resource = getResourceFromDocument(hits.doc(i));
            resource.setScore(hits.score(i) * 100);
            results.add(resource);
         }

         searcher.close();

      } catch (IdException e) {
          logger.error(e);
       }catch (Exception e) {
         logger.error(e);
      }
      return results;


   }

   protected Set searchResources(String queryStr, String defaultField) throws ParseException {
      Analyzer analyzer = new StandardAnalyzer();
      Query query = QueryParser.parse(queryStr, defaultField, analyzer);
      return searchResources(query);
   }

   protected ResourceBean getResourceFromDocument(Document document) throws IdException {
      Id id = idManager.getId(document.getField("id").stringValue());
      return (ResourceBean) getResource(id);
   }

   protected Collection getResources(){
      return getHibernateTemplate().loadAll(ResourceBean.class);
   }

   public void setIdManager(IdManager idManager){
      this.idManager = idManager;
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
/*
      Map helpConfigs = applicationContext.getBeansOfType(HelpContextConfig.class, true, false);
      for (Iterator i = helpConfigs.keySet().iterator(); i.hasNext();) {
         helpContextConfig.putAll((Map) helpConfigs.get(i.next()));
      }

      resources = applicationContext.getBeansOfType(Resource.class, true, false);
*/
   }

   /**
    * indexes all the resources into the lucence search system, ideally this
    * should be a scheduled job
    * @throws IOException
    */
   protected void initResources() throws IOException, SharedException {
      IndexWriter writer = null;
      Date start = new Date();
      writer = new IndexWriter("index", new StandardAnalyzer(), getIndexOnStartup());

      for (Iterator i = getResources().iterator(); i.hasNext();) {
         ResourceBean resource = (ResourceBean) i.next();

         try {
            writer.addDocument(getDocument(resource));
            logger.info("added resource '" +  "' (" + resource.getName() + "), doc count=" + writer.docCount());
         } catch (IOException e) {
            logger.error("failed to add resource '" +  "' (" + resource.getName() + "): " + e.getMessage());
         }
      }

      writer.optimize();
      writer.close();

      Date end = new Date();
      initialized = true;
      logger.info("finished initializing lucene in " + (end.getTime() - start.getTime()) + " total milliseconds");
   }

   public int getContextSize() {
      return contextSize;
   }

   public void setContextSize(int contextSize) {
      this.contextSize = contextSize;
   }

   protected Document getDocument(ResourceBean resource)
         throws IOException, MalformedURLException, SharedException {

      Document doc = new Document();

      if (resource.getContexts() != null){
         for (Iterator i = resource.getContexts().iterator(); i.hasNext();) {
            doc.add(Field.Keyword("context", "\"" + ((String) i.next()) + "\""));
         }
      }

      doc.add(Field.Keyword("location", resource.getLocation()));
      doc.add(Field.Keyword("name", resource.getName()));
      doc.add(Field.Keyword("id", resource.getId().toString()));

      Reader reader = new BufferedReader(new InputStreamReader(resource.getUrl().openStream()));

      doc.add(Field.Text("content", reader));

      return doc;
   }

   public boolean getIndexOnStartup() {
      return indexOnStartup;
   }

   public void setIndexOnStartup(boolean indexOnStartup) {
      this.indexOnStartup = indexOnStartup;
   }

   public TableOfContentsBean getToc() {
      if(toc == null)
      {
        toc = new TableOfContentsBean();
        Collection categories = getHibernateTemplate().loadAll(CategoryBean.class);
        toc.setCategories(new ArrayList(categories));
      }
      return toc;
   }

   public void setToc(TableOfContentsBean toc) {
      this.toc = toc;
   }

   public Glossary getGlossary() {
      return glossary;
   }

   public void setGlossary(Glossary glossary) {
      this.glossary = glossary;
   }
   
   public void storeCategory(Category category) {
     getHibernateTemplate().saveOrUpdate(category);
  }
  
   public Category createCategory()
   {
     return new CategoryBean();
   }

  /* (non-Javadoc)
   * @see org.sakaiproject.service.help.HelpManager#createResource()
   */
  public Resource createResource()
  {
    return new ResourceBean();
  }

  public Resource getResourceByDocId(final String docId)
  {
    HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        net.sf.hibernate.Query q = session.getNamedQuery(QUERY_GETRESOURCEBYDOCID);
        q.setString(DOCID, docId);
        return q.uniqueResult();
      }
    };
    return (Resource) getHibernateTemplate().execute(hcb);
  }
  
  /**
   * 
   */
  private void registerHelpContent()
  {
    //register all help docs
    List toolRegistrations = serverConfigurationService.getToolRegistrations();
    List urlList = new ArrayList();
    for(int i=0; i<toolRegistrations.size(); i++)
    {
      ToolRegistration tr = (ToolRegistration)toolRegistrations.get(i);
      String toolUrl = tr.getUrl();
      int index = toolUrl.indexOf("/", 5);
      toolUrl = toolUrl.substring(0, index+1);
      String helpRegistrationUrl = "C:\\java\\projects\\help\\tool\\src\\webapp\\helpReg\\help.xml";
      urlList.add(helpRegistrationUrl);
    }
    
    for(int i=0; i<urlList.size(); i++)
    {

      try{
        String urlString = (String)urlList.get(i);
        org.springframework.core.io.Resource resource = 
          new FileSystemResource(urlString);
      BeanFactory beanFactory = new XmlBeanFactory(resource);
      Category category = (Category)beanFactory.getBean("category");
      this.storeCategory(category);
      }
      catch(Exception e)
      {
        e.printStackTrace();
        break;
      }
    }
    
  }
  
  /**
   * @return Returns the serverConfigurationService.
   */
  public ServerConfigurationService getServerConfigurationService()
  {
    return serverConfigurationService;
  }
  /**
   * @param serverConfigurationService The serverConfigurationService to set.
   */
  public void setServerConfigurationService(
      ServerConfigurationService serverConfigurationService)
  {
    this.serverConfigurationService = serverConfigurationService;
  }
  /**
   * @param logger The logger to set.
   */
  public void setLogger(Logger logger)
  {
    this.logger = logger;
  }

  /* (non-Javadoc)
   * @see org.sakaiproject.service.help.HelpManager#getSupportEmailAddress()
   */
  public String getSupportEmailAddress()
  {
    if(supportEmailAddress == null)
    {
      this.setSupportEmailAddres(serverConfigurationService.getString("support.email"));
    }
    return supportEmailAddress;
  }
  
  public void setSupportEmailAddres(String email){
    this.supportEmailAddress = email;
  }
}
