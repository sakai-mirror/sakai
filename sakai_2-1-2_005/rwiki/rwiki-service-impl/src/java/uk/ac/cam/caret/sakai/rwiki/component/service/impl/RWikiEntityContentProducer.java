package uk.ac.cam.caret.sakai.rwiki.component.service.impl;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.search.EntityContentProducer;
import org.sakaiproject.search.model.SearchBuilderItem;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.event.Event;

import uk.ac.cam.caret.sakai.rwiki.component.model.impl.RWikiEntityImpl;
import uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService;
import uk.ac.cam.caret.sakai.rwiki.service.api.RenderService;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiEntity;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.utils.DigestHtml;
import uk.ac.cam.caret.sakai.rwiki.utils.NameHelper;

public class RWikiEntityContentProducer implements EntityContentProducer {

	private RenderService renderService = null;

	private RWikiObjectService objectService = null;

	public RWikiEntityContentProducer(RenderService renderService,
			RWikiObjectService objectService) {
		this.renderService = renderService;
		this.objectService = objectService;
	}

	public boolean isContentFromReader(Entity cr) {
		return false;
	}

	public Reader getContentReader(Entity cr) {
		return null;
	}

	public String getContent(Entity cr) {
		RWikiEntity rwe = (RWikiEntity) cr;
		RWikiObject rwo = rwe.getRWikiObject();
		String pageName = rwo.getName();
		String pageSpace = NameHelper.localizeSpace(pageName, rwo.getRealm());
		String renderedPage = renderService.renderPage(rwo, pageSpace,
				new ComponentPageLinkRenderImpl(pageSpace));

		return DigestHtml.digest(renderedPage);

	}

	public String getTitle(Entity cr) {
		RWikiEntity rwe = (RWikiEntity) cr;
		RWikiObject rwo = rwe.getRWikiObject();
		return rwo.getName();
	}

	public boolean matches(Reference ref) {
		EntityProducer ep = ref.getEntityProducer();
		return (ep instanceof RWikiObjectService);
	}

	public List getAllContent() {
		List allPages = objectService.findAllPageNames();
		List l = new ArrayList();
		for (Iterator i = allPages.iterator(); i.hasNext();) {
			String pageName = (String) i.next();
			String reference = RWikiEntityImpl.createReference(pageName);
			l.add(reference);
		}
		return l;
	}

	public Integer getAction(Event event) {
	    String eventName = event.getEvent();
	    if ( RWikiObjectService.EVENT_RESOURCE_ADD.equals(eventName) ||
	    		RWikiObjectService.EVENT_RESOURCE_WRITE.equals(eventName) ) {
	    		return SearchBuilderItem.ACTION_ADD;
	    }
	    if ( RWikiObjectService.EVENT_RESOURCE_REMOVE.equals(eventName) ) {
	    		return SearchBuilderItem.ACTION_DELETE;
	    }
		return SearchBuilderItem.ACTION_UNKNOWN;
	}

	public boolean matches(Event event) {
		return !SearchBuilderItem.ACTION_UNKNOWN.equals(getAction(event));
	}

	public String getTool() {
		return "Wiki";
	}

	public String getUrl(Entity entity) {
		return entity.getUrl()+"html";
	}
	
	

}