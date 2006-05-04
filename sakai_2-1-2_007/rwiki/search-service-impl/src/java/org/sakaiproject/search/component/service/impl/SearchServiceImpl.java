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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.sakaiproject.search.SearchIndexBuilder;
import org.sakaiproject.search.SearchList;
import org.sakaiproject.search.SearchService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.notification.NotificationEdit;
import org.sakaiproject.service.legacy.notification.cover.NotificationService;

/**
 * The search service
 * 
 * @author ieb
 * 
 */
public class SearchServiceImpl implements SearchService {

	private static Log dlog = LogFactory.getLog(SearchServiceImpl.class);

	private Logger logger = null;

	/**
	 * Optional dependencies
	 */
	private List triggerFunctions;

	/**
	 * Location of index, required dependency
	 */
	private String indexDirectory;

	/**
	 * the notification object
	 */
	private NotificationEdit notification = null;

	/**
	 * init completed
	 */
	private boolean initComplete = false;

	/**
	 * the currently running index searcher
	 */
	private IndexSearcher runningIndexSearcher;

	/**
	 * The index builder dependency
	 */
	private SearchIndexBuilder searchIndexBuilder;

	/**
	 * Register a notification action to listen to events and modify the search
	 * index
	 * 
	 */
	public void init() {
		try {
			dlog.debug("init start");
			

			dlog.debug("checking setup");
			if (indexDirectory == null) {
				logger.error(" indexDirectory must be set");
				throw new RuntimeException("Must set indexDirectory");

			}
			if (searchIndexBuilder == null) {
				logger.error(" searchIndexBuilder must be set");
				throw new RuntimeException("Must set searchIndexBuilder");
			}
			

			// register a transient notification for resources
			notification = NotificationService.addTransientNotification();

			// add all the functions that are registered to trigger search index
			// modification

			notification.setFunction(SearchService.EVENT_TRIGGER_SEARCH);
			if (triggerFunctions != null) {
				for (Iterator ifn = triggerFunctions.iterator(); ifn.hasNext();) {
					String function = (String) ifn.next();
					notification.addFunction(function);
					dlog.debug("Adding Search Register "+function);
				}
			}

			// set the filter to any site related resource
			notification.setResourceFilter("/");

			// set the action
			notification.setAction(new SearchNotificationAction(
					searchIndexBuilder));

						

			// register a transient notification for resources
			NotificationEdit sbnotification = NotificationService
					.addTransientNotification();

			// add all the functions that are registered to trigger search index
			// modification

			sbnotification.setFunction(SearchService.EVENT_TRIGGER_INDEX_RELOAD);

			// set the action
			sbnotification.setAction(new SearchReloadNotificationAction(
					this));

			initComplete = true;
			dlog.debug("init end");
		} catch (Throwable t) {
			dlog.error("Failed to start ", t);
		}

	}

	/**
	 * @return Returns the triggerFunctions.
	 */
	public List getTriggerFunctions() {
		return triggerFunctions;
	}

	/**
	 * @param triggerFunctions
	 *            The triggerFunctions to set.
	 */
	public void setTriggerFunctions(List triggerFunctions) {
		if (initComplete)
			throw new RuntimeException(
					" use register function at runtime, setTriggerFucntions is for Spring IoC only");
		this.triggerFunctions = triggerFunctions;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void registerFunction(String function) {
		notification.addFunction(function);
		dlog.debug("Adding Function "+function);
	}

	/**
	 * {@inheritDoc}
	 */
	public SearchList search(String searchTerms, List contexts, int start, int end) {
		try {
			BooleanQuery query = new BooleanQuery();
			BooleanQuery contextQuery = new BooleanQuery();
			for (Iterator i = contexts.iterator(); i.hasNext();) {
				contextQuery.add(new TermQuery(new Term("siteid", (String) i
						.next())), true, false);
			}

			Query textQuery = QueryParser.parse(searchTerms, "contents",
					new StandardAnalyzer());
			query.add(contextQuery, true, false);
			query.add(textQuery, true, false);
			logger.info("Query is " + query.toString());
			if (runningIndexSearcher == null) {
				reload();
			}
			if (runningIndexSearcher != null) {
				Hits h = runningIndexSearcher.search(query);
				logger.info("Got " + h.length() + " hits");

				return new SearchListImpl(h,textQuery, start, end);
			} else {
				throw new RuntimeException(
						"Failed to start the Lucene Searche Engine");
			}

		} catch (ParseException e) {
			throw new RuntimeException("Failed to parse Query ", e);
		} catch (IOException e) {
			throw new RuntimeException("Failed to run Search ", e);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void reload() {
		dlog.info("Reload");

		long reloadStart = System.currentTimeMillis();
		try {
			File indexDirectoryFile = new File(indexDirectory);
			indexDirectoryFile.mkdirs();
			
			IndexSearcher indexSearcher = new IndexSearcher(indexDirectory);
			if (indexSearcher != null) {
				runningIndexSearcher = indexSearcher;
			}
			long reloadEnd = System.currentTimeMillis();
			dlog.info("Reload Complete " + indexSearcher.maxDoc() + " in "
					+ (reloadEnd - reloadStart));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * required dependency
	 * 
	 * @return Returns the indexDirectory.
	 */
	public String getIndexDirectory() {
		return indexDirectory;
	}

	/**
	 * required dependency
	 * 
	 * @param indexDirectory
	 *            The indexDirectory to set.
	 */
	public void setIndexDirectory(String indexDirectory) {
		this.indexDirectory = indexDirectory;
	}

	/**
	 * required dependency
	 * 
	 * @return Returns the logger.
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * required dependency
	 * 
	 * @param logger
	 *            The logger to set.
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @return Returns the searchIndexBuilder.
	 */
	public SearchIndexBuilder getSearchIndexBuilder() {
		return searchIndexBuilder;
	}

	/**
	 * @param searchIndexBuilder
	 *            The searchIndexBuilder to set.
	 */
	public void setSearchIndexBuilder(SearchIndexBuilder searchIndexBuilder) {
		this.searchIndexBuilder = searchIndexBuilder;
	}

}
