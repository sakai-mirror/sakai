/**********************************************************************************
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
package org.sakaiproject.tool.OSIDRepository;

import org.sakaiproject.tool.search.*;
import org.sakaiproject.tool.util.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

import org.w3c.dom.*;

import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;

public class SearchSource {
  /**
   * This source is enabled (available for use)
   */
  private static final int  	ENABLED							= (1 << 0);
	/**
	 * Alternate form (selected via an anchor, not the pulldown menu)
	 */
	private static final int 		ALTERNATE_FORM			= (1 << 1);
  /*
   * Display name, handlers, flags
   */
  private String  _name;

  private String  _queryClassName;
  private Class   _queryClass;

  private String  _searchResultClassName;
  private Class   _searchResultClass;

  private String  _resultPageClassName;
  private Class   _resultPageClass;

  private int     _flags;

  /**
   * SearchSource instance
   */
  private static ArrayList	_sourceList 	= null;
  /**
   * SearchSource synchronization
   */
  private static Object			_sourceSync 	= new Object();
	/**
	 * Alternate forms in use?
	 */
	private static boolean		_formsInUse		= false;

  /**
   * Private constructor
   */
  private SearchSource() {
  }

	/**
	 * Constructor
	 * @param name Search source name (used internally and for the pulldown menu)
	 * @param queryClassName Query handler
	 * @param searchResultClassName Search response handler
	 * @param resultPageClassName User result renderer
	 * @param flags Enabled, disabled, etc.
	 */
	private SearchSource(String name,
											 String queryClassName,
											 String searchResultClassName,
											 String resultPageClassName,
											 int 		flags) {

		_name 									= name;
		_queryClassName 				= queryClassName;
		_searchResultClassName 	= searchResultClassName;
		_resultPageClassName 		= resultPageClassName;
		_flags 									= flags;

		if (isAlternateForm() && isEnabled()) {
			_formsInUse = true;
		}
	}

  /**
   * Return the search source name
   * @return The name of this source (eg Academic Search, ERIC)
   */
  public String getName() {
    return _name;
  }

  /**
   * Is this source available?
   * @return true (if available)
   */
  public boolean isEnabled() {
    return (_flags & ENABLED) == ENABLED;
  }

  /**
   * Return a new QueryBase object for the specified search source.
   * Class loading is defered until request time.
   * @return A QueryBase object for this source
   */
  public QueryBase getQueryHandler() throws java.lang.ClassNotFoundException,
  																					java.lang.InstantiationException,
  																					java.lang.IllegalAccessException {
    synchronized (this) {
      if (_queryClass == null) {
        _queryClass = Class.forName(_queryClassName);
      }
    }
    return (QueryBase) _queryClass.newInstance();
  }

  /**
   * Return a new SearchResultBase object for the specified search source.
   * Class loading is defered until request time.
   * @return A SearchResultBase object for this source
   */
  public SearchResultBase getSearchResultHandler()
  																	throws 	java.lang.ClassNotFoundException,
  																					java.lang.InstantiationException,
  																					java.lang.IllegalAccessException {
    synchronized (this) {
      if (_searchResultClass == null) {
        _searchResultClass = Class.forName(_searchResultClassName);
      }
    }
    return (SearchResultBase) _searchResultClass.newInstance();
  }

  /**
   * Return a new ResultPageBase object for the specified search source.
   * Class loading is defered until request time.
   * @return A ResultPageBase object for this source
   */
  public ResultPageBase getResultPageHandler()
  																	throws 	java.lang.ClassNotFoundException,
  																					java.lang.InstantiationException,
  																					java.lang.IllegalAccessException {
    synchronized (this) {
      if (_resultPageClass == null) {
        _resultPageClass = Class.forName(_resultPageClassName);
      }
    }
    return (ResultPageBase) _resultPageClass.newInstance();
  }

  /**
   * Lookup a search source by name
   * @param name Source name
   * @return SearchSource object
   */
  public static SearchSource getSourceByName(String name) {

		verifyList();

		synchronized (_sourceSync) {
	    for (Iterator i = _sourceList.iterator(); i.hasNext(); ) {
				SearchSource source = (SearchSource) i.next();

				if (source.getName().equalsIgnoreCase(name)) {
        	return source;
      	}
      }
	  }
    throw new IllegalArgumentException("Unknown search source: " + name);
  }

	/**
	 * Get the default search source
	 * @return The search source name
	 */
	public static String getDefaultSourceName() {
		verifyList();

		synchronized (_sourceSync) {
			return ((SearchSource) _sourceList.get(0)).getName();
		}
	}

	/**
	 * Return an Iterator to the source list
	 * @return Source list Iterator
	 */
	public static Iterator getSearchListIterator() {
		verifyList();

		synchronized (_sourceSync) {
			return _sourceList.iterator();
		}
	}

	/*
	 * Form handling
	 */

	/**
	 * Are alternate forms available?
	 * @return true if so
	 */
	public static boolean alternateFormsEnabled() {
		return _formsInUse;
	}

  /**
   * Does this source require an alternate form?
   * @return true if so
   */
  public boolean isAlternateForm() {
    return (_flags & ALTERNATE_FORM) == ALTERNATE_FORM;
  }

	/**
	 * Get the "next form" name
	 * @param currentSourceName Name of the active search source
	 * @return Form name for the "next form" link
	 */
	public static String getNextFormName(String currentSourceName) {
		SearchSource	source;
		int	index, pass;

		/*
		 * Find the location of the current source in the SearchSource array
		 */
		source 	= getSourceByName(currentSourceName);
		index 	= _sourceList.indexOf(source);

		if (++index == _sourceList.size()) {
			index = 0;
		}
		/*
		 * Find the next enabled form name
		 */
		pass = 0;
		while (true) {

			synchronized (_sourceSync) {
				source = (SearchSource) _sourceList.get(index);
				/*
				 * Is this an alternate (or our default) form?  If so, use it
				 */
				if ((source.isEnabled()) &&
				    ((source.isAlternateForm()) ||
					   (source.getName().equals(getDefaultSourceName())))) {
						break;
				}
				if (++index == _sourceList.size()) {
					/*
					 * Make sure we don't try this forever
					 */
					if (pass++ == 1) {
						throw new RuntimeException("No \"next form\" is configured");
					}
					index = 0;
				}
			}
		}
		return source.getName();
	}
  
  /**
   * Create a populated <code>SearchSource</code> list.  We actually construct
   * two lists, one for sources which use the standard form, and another for
   * any sources that require an alternate form.  These lists are eventaully
   * combined to create one list with the alternate form sources at the end.
   * @param xmlStream Configuration file as an InputStream
   */
//  public static void populate(InputStream xmlStream) throws DomException, SearchException {
	  public static void populate() throws SearchException {
		SearchSource 	source;
		ArrayList			alternateList;
		NodeList			sourceNodeList;
		Document			document;
		int						length;

		/*
		 * Only populate the list once
		 */
		synchronized (_sourceSync) {
			if (_sourceList != null) {
				return;
			}

			/*
			 * Establish an empty search source list
			 */
			_sourceList = new ArrayList();
			
			java.util.Vector nameVector = new java.util.Vector();
			
			try {
				ComponentManager.getInstance();		
				Object o = ComponentManager.get("org.osid.repository.RepositoryManager");
				if (o != null) {
					// initialize the repository manager and get its repositories
					org.osid.repository.RepositoryManager repositoryManager = (org.osid.repository.RepositoryManager)o;
					repositoryManager.assignOsidContext(new org.osid.OsidContext());
					repositoryManager.assignConfiguration(new java.util.Properties());
					org.osid.repository.RepositoryIterator repositoryIterator = repositoryManager.getRepositories();
					while (repositoryIterator.hasNextRepository()) {
						// display each repository name in a line and store away the repository for a search
						org.osid.repository.Repository nextRepository = repositoryIterator.nextRepository();
						nameVector.addElement(nextRepository.getDisplayName());
					}
				}
			} catch(Throwable t) {
				t.printStackTrace();
				// TODO: throw(t);
			}		
			
			alternateList	= new ArrayList();
			length			= nameVector.size();

			for (int i = 0; i < length; i++) {
				String	sourceName;
				String	queryHandler, searchResultHandler, resultPageHandler;
				int			flags;

				sourceName 		= (String)nameVector.elementAt(i);

				if (StringUtils.isNull(sourceName)) {
					continue;
				}
				queryHandler = "org.sakaiproject.tool.search.Query";
				searchResultHandler = "org.sakaiproject.tool.search.Response";
				resultPageHandler = "org.sakaiproject.tool.search.Result";
				/*
				 * Set options:
				 *	source enabled = [true | false]
				 *	form type = [standard | custom]
				 */
				flags	= 0;
				flags |= ENABLED;
				/*
				 * Save this source
				 */
				addSource(new SearchSource(sourceName,
																	 queryHandler,
																	 searchResultHandler,
																	 resultPageHandler, flags),
																	_sourceList, alternateList);
			}
		}
		/*
		 * Place alternate form sources at the end
		 */
		_sourceList.addAll(alternateList);

		if (_sourceList.size() == 0) {
			throw new SearchException("No search source handlers were configured");
		}
  }

	/**
	 * Has source list has been populated?
	 * @return true if so
	 */
	public static boolean isSourceListPopulated() {
		synchronized (_sourceSync) {
			return !((_sourceList == null) || (_sourceList.isEmpty()));
		}
	}

	/*
	 * Helpers
	 */

	/**
	 * Locate a handler specification
	 * @param parent Parent element for this search
	 * @param handlerName Handler to look up
	 * @return Class name for this handler
	 */
	 private static String parseHandler(Element parent, String handlerName) {
	 	Element element;
	 	String	handler;

		if ((element = DomUtils.getElement(parent, handlerName)) == null) {
			return null;
		}

		handler = element.getAttribute("name");
		return (StringUtils.isNull(handler)) ? null : handler;
	}

	/**
	 * Verify the source list has been populated
	 */
	private static void verifyList() {
		if (!isSourceListPopulated()) {
			throw new SearchException("The handler list has not been populated");
		}
	}

	/**
	 * Add a Search source to the appropriate list
	 * @param source SearceSource object
	 * @param standardList Standard forms
	 * @param alternateList Alternate (custom) forms
	 */
	private static void addSource(SearchSource source,
												 			  ArrayList standardList, ArrayList alternateList) {

		if (source.isAlternateForm()) {
			alternateList.add(source);
			return;
		}
		standardList.add(source);
	}
}
