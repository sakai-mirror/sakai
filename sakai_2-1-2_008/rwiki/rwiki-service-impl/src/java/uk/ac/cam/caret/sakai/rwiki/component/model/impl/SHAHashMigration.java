/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.component.model.impl;

import java.util.Iterator;
import java.util.List;

import org.sakaiproject.service.framework.log.Logger;

import uk.ac.cam.caret.sakai.rwiki.model.RWikiObjectImpl;
import uk.ac.cam.caret.sakai.rwiki.service.api.dao.RWikiObjectDao;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.DataMigrationAgent;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;

/**
 * Generates SHA1 hashes for the object type
 * @author ieb
 *
 */
//FIXME: Component

public class SHAHashMigration implements DataMigrationAgent {
	
	private String from;
	private String to;
	private RWikiObjectDao codao;
	private Logger log;
	private boolean background = false;
	
	/* (non-Javadoc)
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.api.model.DataMigrationAgent#migrate(java.lang.String, java.lang.String)
	 */
	public String migrate(String current, String target) throws Exception {
		if (( current != null && from == null ) ||
				( current != null && !current.equals(from) )) {
			log.info("Skipping Migration for "+from+" to "+to);
			return current;
		}
		List rwikiObjects = codao.getAll();
		if ( rwikiObjects != null ) {
			long start = System.currentTimeMillis();
			for( Iterator i = rwikiObjects.iterator(); i.hasNext(); ) {
				RWikiObject rwo = (RWikiObject) i.next();
				long start2  = System.currentTimeMillis();
				String hash = rwo.getSha1();
				if ( hash == null || hash.length() == 0 ) {
					hash = RWikiObjectImpl.computeSha1(rwo.getContent());
					rwo.setSha1(hash);
					log.info(" Hash took "+(System.currentTimeMillis()-start2)+" ms for "+rwo.getName()+" revision "+rwo.getRevision());
					codao.updateObject(rwo);
				}
			}
			log.info(" Sha1 Conversion took "+(System.currentTimeMillis()-start)+" ms for "+rwikiObjects.size()+" items");
		}
		log.info("Done Migration for "+from+" to "+to);
		return to;
	}

	/**
	 * @return Returns the codao.
	 */
	public RWikiObjectDao getRwikiObjectDao() {
		return codao;
	}

	/**
	 * @param codao The codao to set.
	 */
	public void setRwikiObjectDao(RWikiObjectDao codao) {
		this.codao = codao;
	}

	/**
	 * @return Returns the from.
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from The from to set.
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @return Returns the log.
	 */
	public Logger getLog() {
		return log;
	}

	/**
	 * @param log The log to set.
	 */
	public void setLog(Logger log) {
		this.log = log;
	}

	/**
	 * @return Returns the to.
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param to The to to set.
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * @return Returns the background.
	 */
	public boolean isBackground() {
		return background;
	}

	/**
	 * @param background The background to set.
	 */
	public void setBackground(boolean background) {
		this.background = background;
	}
	
}
