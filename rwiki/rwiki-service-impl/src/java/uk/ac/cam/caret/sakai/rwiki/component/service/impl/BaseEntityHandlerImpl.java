/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.component.service.impl;

import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.Reference;

import uk.ac.cam.caret.sakai.rwiki.service.api.EntityHandler;
import uk.ac.cam.caret.sakai.rwiki.utils.SimpleCoverage;

/**
 * 
 * Provides a regex base entity handler where the matching uses MessageFormats
 * and Regex patterns
 * 
 * @author ieb
 * 
 */
public abstract class BaseEntityHandlerImpl implements EntityHandler {


	/** 
	 * The start of the URL endign in a /
	 */
	private String accessURLStart = null;

	/**
	 * The sub type of this handler, but be unique in the context of the major
	 * type
	 */
	private String minorType = null;

	/**
	 * {@inheritDoc}
	 * 
	 * TODO
	 */
	public void setReference(String majorType, Reference ref, String reference) {
		SimpleCoverage.cover("Setting reference for "+reference);
		Decoded decoded = decode(reference);
		if ( decoded != null ) {
			ref.set(majorType,minorType,decoded.getId(),decoded.getContainer(),decoded.getContext());
		} else {
			SimpleCoverage.cover();
			throw new RuntimeException(this
					+ " Failed to setReference in EntityHelper " + majorType
					+ ":" + minorType
					+ " reference not for this EntityHandler ");
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * TODO
	 */
	public boolean matches(String reference) {
		return (decode(reference)!= null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * TODO
	 */
	public int getRevision(Reference reference) {
		Decoded decode = decode(reference.getReference());
		return Integer.parseInt(decode.getVersion());
	}



	/**
	 * @return Returns the minorType.
	 */
	public String getMinorType() {
		return minorType;
	}

	/**
	 * @param minorType
	 *            The minorType to set.
	 */
	public void setMinorType(String minorType) {
		this.minorType = minorType;
	}
	

	/**
	 * 
	 * @param s the full reference 
	 * @return A Decoded object contianing the values, or null if not this handler
	 */
	public Decoded decode(String s) {
		if (s.startsWith(accessURLStart) && s.endsWith(minorType) && s.indexOf("//") == -1
				&& s.indexOf("/,") == -1 && s.indexOf("/.") == -1) {
			Decoded decoded = new Decoded();
			s = s.substring(accessURLStart.length() - 1);
			int lastslash = s.lastIndexOf(Entity.SEPARATOR);
			int firstslash = s.indexOf(Entity.SEPARATOR, 1);
			int nextslash = s.indexOf(Entity.SEPARATOR,firstslash+1);
			if ( nextslash == -1 ) {
				nextslash = firstslash;
			}
			decoded.setContext(s.substring(0, nextslash));
			if (nextslash == lastslash) {
				decoded.setContainer(Entity.SEPARATOR);
			} else {
				decoded.setContainer(s.substring(nextslash, lastslash));
			}

			String filename = s.substring(lastslash + 1);
			filename = filename.substring(0, filename.length() - minorType.length());
			int comma = filename.indexOf(",");
			if (comma != -1) {
				decoded.setPage(filename.substring(0, comma));
				decoded.setVersion(filename.substring(comma + 1));
			} else {
				decoded.setPage(filename);
				decoded.setVersion("-1");
			}
			
			return decoded;
		}
		return null;
	}

	/**
	 * @return Returns the accessURLStart.
	 */
	public String getAccessURLStart() {
		return accessURLStart;
	}

	/**
	 * @param accessURLStart The accessURLStart to set.
	 */
	public void setAccessURLStart(String accessURLStart) {
		this.accessURLStart = accessURLStart;
	}


}
