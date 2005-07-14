/*
 * $URL: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/utils/ioc/web/WebContextLoaderListener.java,v 1.1 2005/06/28 13:31:53 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/28 13:31:53 $
 */
package org.sakaiproject.metaobj.utils.ioc.web;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;

public class WebContextLoaderListener extends ContextLoaderListener {

	/**
	 * Create the ContextLoader to use. Can be overridden in subclasses.
	 * @return the new ContextLoader
	 */
	protected ContextLoader createContextLoader() {
		return new WebContextLoader();
	}
}
