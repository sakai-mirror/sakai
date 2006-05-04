/**********************************************************************************
 *
 * $Header$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2006 University of Cambridge
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

package org.sakaiproject.search.component.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.search.EntityContentProducer;
import org.sakaiproject.search.SearchIndexBuilder;
import org.sakaiproject.search.SearchService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.notification.Notification;
import org.sakaiproject.service.legacy.notification.NotificationEdit;
import org.sakaiproject.service.legacy.notification.cover.NotificationService;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;

/**
 * Search index builder is expected to be registered in spring as
 * org.sakaiproject.search.SearchIndexBuilder as a singleton.
 * 
 * It receives resources which it adds to its list of pending documents to be
 * indexed. A seperate thread then runs thtough the list of entities to be
 * indexed, updating the index. Each time the index is updates an event is
 * posted to force the Search components that are using the index to reload.
 * 
 * Incremental updates to the Lucene index require that the searchers reload the
 * index once the idex writer has been built.
 * 
 * @author ieb
 * 
 */

public class SearchIndexBuilderImpl implements SearchIndexBuilder {

	private static Log dlog = LogFactory.getLog(SearchIndexBuilderImpl.class);

	private Logger logger = null;

	/**
	 * The currently running index Builder thread
	 */
	private Thread indexBuilderThread = null;

	/**
	 * the current list of pending documents
	 */
	private ArrayList toDoList = new ArrayList();

	/**
	 * a lock object
	 */
	private Object listLock = new Object();

	public long sleepTime = 30000L;

	private List producers = new ArrayList();

	private SearchService searchService = null;

	public void init() {
		try {
			dlog.debug("init start");

			// register a transient notification for resources
			NotificationEdit notification = NotificationService
					.addTransientNotification();

			// add all the functions that are registered to trigger search index
			// modification

			notification.setFunction(SearchService.EVENT_TRIGGER_INDEX_RELOAD);

			// set the action
			notification.setAction(new SearchReloadNotificationAction(
					searchService));
			

			dlog.debug("Checking Search Service");
			if (searchService == null) {
				logger.error(" searchService must be set ");
				throw new RuntimeException(" searchService must be set");

			}
		} catch (Throwable t) {
			dlog.error("Failed to init ", t);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void registerEntityContentProducer(EntityContentProducer ecp) {
		dlog.debug("register " + ecp);
		producers.add(ecp);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addResource(Notification notification, Event event) {
		dlog.debug("Add resource " + notification + "::" + event);
		List l = new ArrayList();
		l.add(event);
		addToList(l);
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	public void refreshIndex() {
		List l = getRegisteredContent();
		addToList(l);
	}


	/**
	 * 
	 * {@inheritDoc}
	 */
	public void rebuildIndex() {
		for ( Iterator i = producers.iterator(); i.hasNext();) {
			EntityContentProducer ecp = (EntityContentProducer) i.next();
			List l = ecp.getAllContent();
			addToList(l);
		}
		
	}


	/**
	 * This adds and event to the list and if necessary starts a processing
	 * thread The method is syncronised with removeFromList
	 * 
	 * @param e
	 */
	private void addToList(List add) {
		synchronized (listLock) {
			dlog.debug("Sync addToList " + add);
			toDoList.addAll(add);
			persistantAddToDoList(add);
			if (indexBuilderThread == null) {
				// no thread active, build a new one, could use a pool here
				// to improve performance
				indexBuilderThread = new Thread(new IndexBuilder());
				indexBuilderThread.start();
			}
		}

	}

	/**
	 * This removes a list of events fromt the todo list, if the todo list has
	 * none left it returns null, otherwise it returns a clone of the current to
	 * do list. The method is syncronized with addToList
	 * 
	 * @param remove
	 * @return null if the list is empty, a clone of the list if there are items
	 *         to process
	 */
	private List removeFromList(List remove) {
		synchronized (listLock) {
			dlog.debug("Remove From list " + remove);
			if (remove != null && remove.size() > 0) {
				toDoList.removeAll(remove);
				persistantRemoveToDoList(toDoList);
			}
			if (toDoList.size() == 0) {
				return null;
			} else {
				return (List) toDoList.clone();
			}
		}
	}

	/**
	 * This is the hook to persist additions to the todo list, in this class the
	 * todo list is not persisted, to make the todo list persist, override this
	 * method
	 * 
	 * @param addition
	 */
	protected void persistantAddToDoList(List addition) {

	}

	/**
	 * This is the hoock to persist removals fromt he todo list, in this class
	 * the todo list is not persisted, to make the todo list persistant,
	 * override this method
	 * 
	 * @param removal
	 */
	protected void persistantRemoveToDoList(List removal) {

	}
	
	/**
	 * This is a DAO method that fills the list with all the current regissterd content, as entity references
	 * @return
	 */
	protected List getRegisteredContent() {
		List l = new ArrayList();
		return l;
	}


	/**
	 * The IndexBuilder thread, runs untill there are no more documents in the
	 * list and then exits
	 * 
	 * @author ieb
	 * 
	 */
	public class IndexBuilder implements Runnable {

		public void run() {
			
			dlog.debug("Index Builder Run");
			Session s = null;
			try {
				s = SessionManager.startSession();
				User u = UserDirectoryService.getUser("admin");
				s.setUserId(u.getId());
				
				List runtimeToDo = null;
				// check to see if there is anytihng to do
				runtimeToDo = removeFromList(null);
				while (true) {
					dlog.debug("Run Processing Thread");
					while (runtimeToDo != null) {
						try {
							// process the list
							processToDoList(runtimeToDo);
							// remove contents and get a new copy of the todo
							// list
							runtimeToDo = removeFromList(runtimeToDo);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (sleepTime == 0) {
						break;
					} else {
						try {
							dlog.debug("Sleeping Processing Thread");
							Thread.sleep(sleepTime);
							dlog.debug("Wakey Wakey Processing Thread");
							runtimeToDo = removeFromList(runtimeToDo);
						} catch (InterruptedException e) {
							dlog.debug(" Exit From sleep "+e.getMessage());
							break;
						}
					}
				}
			} catch ( Throwable t ) {
				dlog.warn("Failed in IndexBuilder ",t);
			} finally {
				
				dlog.info("IndexBuilder run exit");
				indexBuilderThread = null;
			}
		}

	}

	/**
	 * This method processes the list of document modifications in the list
	 * 
	 * @param runtimeToDo
	 * @throws IOException
	 */
	protected void processToDoList(List runtimeToDo) throws IOException {
		long startTime = System.currentTimeMillis();
		String indexDirectory = ((SearchServiceImpl)searchService).getIndexDirectory();
		dlog.debug("Starting process List on "+indexDirectory);
		File f = new File(indexDirectory);
		if ( !f.exists() ) {
			f.mkdirs();
			dlog.debug("Indexing in "+f.getAbsolutePath());
		}
		
		
		IndexWriter indexWrite = null;
		try {
			indexWrite = new IndexWriter(indexDirectory,
				new StandardAnalyzer(), false);
		} catch ( IOException ex ) {
			indexWrite = new IndexWriter(indexDirectory,
					new StandardAnalyzer(), true);
		}
		// Open the index
		for (Iterator tditer = runtimeToDo.iterator(); tditer.hasNext();) {
			Object o = (Object) tditer.next();
			Reference ref = null;
			if ( o instanceof Event ) {
				Event e = (Event) o;
				dlog.debug("Indexing "+e.getResource());
				ref = EntityManager.newReference(e.getResource());
			} else if ( o instanceof String ) {
				String s = (String) o;
				dlog.debug("Indexing "+s);
				ref = EntityManager.newReference(s);				
			} 
			if ( ref == null ) {
				dlog.error("Unrecognised trigger object presented to index builder "+o);
			}
			// TODO: Add some logic to determine add, update, remove 
			Entity entity = ref.getEntity();
			Document doc = new Document();
			if ( ref.getContext() == null ) {
				dlog.warn("Context is null for "+o);
			}
			doc.add(Field.Keyword("context", ref.getContext()));
			doc.add(Field.Keyword("container", ref.getContainer()));
			doc.add(Field.UnIndexed("id", ref.getId()));
			doc.add(Field.Keyword("type", ref.getType()));
			doc.add(Field.Keyword("subtype", ref.getSubType()));
			doc.add(Field.Keyword("reference", ref.getReference()));
			Collection c = ref.getRealms();
			for (Iterator ic = c.iterator(); ic.hasNext();) {
				String realm = (String) ic.next();
				doc.add(Field.Keyword("realm", realm));
			}
			try {
				EntityContentProducer sep = newEntityContentProducer(ref);
				if (sep != null) {
					if (sep.isContentFromReader(entity)) {
						doc.add(Field.Text("contents", sep
								.getContentReader(entity), true));
					} else {
						doc.add(Field.Text("contents", sep.getContent(entity),
								true));
					}
					doc.add(Field.Text("title", sep.getTitle(entity), true));
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			dlog.debug("Indexing Document "+doc);
			indexWrite.addDocument(doc);
			dlog.debug("Done Indexing Document "+doc);

		}
		indexWrite.close();
		EventTrackingService.post(EventTrackingService.newEvent(
				SearchService.EVENT_TRIGGER_INDEX_RELOAD, "/searchindexreload",
				true, NotificationService.PREF_IMMEDIATE));
		long endTime = System.currentTimeMillis();
		float totalTime = endTime-startTime;
		float ndocs = runtimeToDo.size();
		float docspersec = 1000*ndocs/totalTime;
		dlog.info("Completed Process List at "+docspersec+" documents/per second");

	}

	/**
	 * Generates a SearchableEntityProducer
	 * 
	 * @param ref
	 * @return
	 * @throws PermissionException
	 * @throws IdUnusedException
	 * @throws TypeException
	 */
	protected EntityContentProducer newEntityContentProducer(Reference ref) {
		dlog.debug(" new entitycontent producer");
		for (Iterator i = producers.iterator(); i.hasNext();) {
			EntityContentProducer ecp = (EntityContentProducer) i.next();
			if (ecp.matches(ref)) {
				return ecp;
			}
		}
		return null;
	}

	/**
	 * @return Returns the logger.
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @param logger
	 *            The logger to set.
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @return Returns the searchService.
	 */
	public SearchService getSearchService() {
		return searchService;
	}

	/**
	 * @param searchService
	 *            The searchService to set.
	 */
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	/**
	 * @return Returns the sleepTime.
	 */
	public long getSleepTime() {
		return sleepTime;
	}

	/**
	 * @param sleepTime
	 *            The sleepTime to set.
	 */
	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	public boolean isBuildQueueEmpty() {
		dlog.debug("Build Queue has "+toDoList.size()+" entries");
		return (toDoList.size() == 0);
	}


}
