/**********************************************************************************
 *
 * $Header$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
 * Copyright (c) 2005 University of Cambridge
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

package uk.ac.cam.caret.sakai.rwiki.model;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Stack;

import org.apache.xerces.impl.dv.util.Base64;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.util.resource.BaseResourceProperties;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.ac.cam.caret.sakai.rwiki.service.api.dao.RWikiObjectContentDao;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObjectContent;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiPermissions;
import uk.ac.cam.caret.sakai.rwiki.utils.NameHelper;

/**
 * <p>
 * CrudObjectImpl implements the CrudObject for the CrudServiceImpl.
 * </p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision$
 */
// FIXME: Component
public abstract class RWikiObjectImpl implements RWikiObject {

	public RWikiObjectImpl() {
		// EMPTY
	}

	/**
	 * DAO Object for the content
	 */
	protected RWikiObjectContentDao codao = null;

	/**
	 * The lazy loaded content object
	 */
	protected RWikiObjectContent co = null;

	/** The id. */
	protected String m_id = null;

	/** The version. */
	protected Date m_version = null;

	/** The "name". */
	protected String m_name = "";

	/** The "rank". */
	protected String m_realm = "";

	/** The "serial number". */
	// Now lazy loaded protected String m_content = "";
	protected String m_referenced = "";

	protected String m_user = "";

	protected String m_owner = null;

	protected boolean m_ownerread = true;

	protected boolean m_ownerwrite = true;

	protected boolean m_owneradmin = true;

	protected boolean m_groupread = true;

	protected boolean m_groupwrite = true;

	protected boolean m_groupadmin = true;

	protected boolean m_publicread = false;

	protected boolean m_publicwrite = false;

	protected Integer m_revision = new Integer(0);

	// iebdelete protected List m_history = null;

	/**
	 * @return Returns the history.
	 */
	/*
	 * iebdelete public List getXHistory() { return m_history; }
	 */
	/**
	 * @param history
	 *            The history to set.
	 */
	/*
	 * iebdelete public void setXHistory(List history) { this.m_history =
	 * history; }
	 */
	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return m_id;
	}

	/**
	 * Set the id - should be used only by the storage layer, not by end users!
	 * 
	 * @param id
	 *            The object id.
	 */
	public void setId(String id) {
		m_id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	public Date getVersion() {
		return m_version;
	}

	/**
	 * Set the version - should be used only by the storage layer, not by end
	 * users!
	 * 
	 * @param version
	 *            The object version.
	 */
	public void setVersion(Date version) {
		m_version = version;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setName(String name) {
		m_name = name;
		if (m_name == null)
			m_name = "";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRealm() {
		return m_realm;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setRealm(String realm) {
		m_realm = realm;
		if (m_realm == null)
			m_realm = "";
	}

	/**
	 * {@inheritDoc}
	 */
	// public String getContent()
	// {
	// return m_content;
	// }
	/**
	 * {@inheritDoc}
	 */
	// public void setContent(String content)
	// {
	// m_content = content;
	// if (m_content == null)
	// m_content = "";
	// }
	/**
	 * {@inheritDoc}
	 */
	public int compareTo(Object o) {
		if (!(o instanceof RWikiObject))
			throw new ClassCastException();

		// if the object are the same, say so
		if (o == this)
			return 0;

		// start the compare by comparing their names
		int compare = getName().compareTo(((RWikiObject) o).getName());

		// if these are the same
		if (compare == 0) {
			// compare rank
			compare = getRealm().compareTo(((RWikiObject) o).getRealm());

			if (compare == 0) {
				compare = (getRevision().compareTo(((RWikiObject) o)
						.getRevision()));
				if (compare == 0) {
					// compare serial number
					compare = getContent().compareTo(
							((RWikiObject) o).getContent());
				}
			}
			// if these are the same
		}

		return compare;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof RWikiObject))
			return false;

		return ((RWikiObject) obj).getId().equals(getId());
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return getId().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#getReferenced()
	 */
	public String getReferenced() {
		return m_referenced;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#setReferenced()
	 */
	public void setReferenced(String referenced) {
		m_referenced = referenced;
		// SAK-2470
		if (m_referenced == null)
			m_referenced = "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#getUser()
	 */
	public String getUser() {
		return m_user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#getUser()
	 */
	public String getOwner() {
		return m_owner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#getGroupadmin()
	 */
	public boolean getGroupAdmin() {
		return m_groupadmin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#getGroupread()
	 */
	public boolean getGroupRead() {
		return m_groupread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#getGroupwrite()
	 */
	public boolean getGroupWrite() {
		return m_groupwrite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#getPublicread()
	 */
	public boolean getPublicRead() {
		return m_publicread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#getPublicwrite()
	 */
	public boolean getPublicWrite() {
		return m_publicwrite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#getUseradmin()
	 */
	public boolean getOwnerAdmin() {
		return m_owneradmin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#getUserread()
	 */
	public boolean getOwnerRead() {
		return m_ownerread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#getUserwrite()
	 */
	public boolean getOwnerWrite() {
		return m_ownerwrite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#setGroupadmin(boolean)
	 */
	public void setGroupAdmin(boolean groupadmin) {
		m_groupadmin = groupadmin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#setGroupread(boolean)
	 */
	public void setGroupRead(boolean groupread) {
		m_groupread = groupread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#setGroupwrite(boolean)
	 */
	public void setGroupWrite(boolean groupwrite) {
		m_groupwrite = groupwrite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#setPublicread(boolean)
	 */
	public void setPublicRead(boolean publicread) {
		m_publicread = publicread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#setPublicwrite(boolean)
	 */
	public void setPublicWrite(boolean publicwrite) {
		m_publicwrite = publicwrite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#setUser(java.lang.String)
	 */
	public void setUser(String user) {
		m_user = user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#setUser(java.lang.String)
	 */
	public void setOwner(String owner) {
		m_owner = owner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#setUseradmin(boolean)
	 */
	public void setOwnerAdmin(boolean useradmin) {
		m_owneradmin = useradmin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#setUserread(boolean)
	 */
	public void setOwnerRead(boolean userread) {
		m_ownerread = userread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject#setUserwrite(boolean)
	 */
	public void setOwnerWrite(boolean userwrite) {
		m_ownerwrite = userwrite;

	}

	public void copyAllTo(RWikiObject rwo) {
		rwo.setName(this.getName());
		rwo.setOwner(this.getOwner());
		rwo.setRealm(this.getRealm());
		rwo.setRevision(this.getRevision());
		rwo.setUser(this.getUser());
		rwo.setVersion(this.getVersion());
		rwo.setSha1(this.getSha1());

		rwo.setContent(this.getContent());
		rwo.setGroupAdmin(this.getGroupAdmin());
		rwo.setGroupRead(this.getGroupRead());
		rwo.setGroupWrite(this.getGroupWrite());
		rwo.setPublicRead(this.getPublicRead());
		rwo.setPublicWrite(this.getPublicWrite());
		rwo.setReferenced(this.getReferenced());
		rwo.setOwnerAdmin(this.getOwnerAdmin());
		rwo.setOwnerRead(this.getOwnerRead());
		rwo.setOwnerWrite(this.getOwnerWrite());
	}

	public void copyTo(RWikiObject rwo) {
		rwo.setContent(this.getContent());
		rwo.setGroupAdmin(this.getGroupAdmin());
		rwo.setGroupRead(this.getGroupRead());
		rwo.setGroupWrite(this.getGroupWrite());
		rwo.setPublicRead(this.getPublicRead());
		rwo.setPublicWrite(this.getPublicWrite());
		rwo.setReferenced(this.getReferenced());
		rwo.setOwnerAdmin(this.getOwnerAdmin());
		rwo.setOwnerRead(this.getOwnerRead());
		rwo.setOwnerWrite(this.getOwnerWrite());
		rwo.setSha1(this.getSha1());
	}

	protected String m_source = null;

	/**
	 * The name of the source used for loading the object content when injected
	 * 
	 * @param source
	 */
	public void setSource(String source) {
		m_source = source;
	}

	public String getSource() {
		return m_source;
	}

	public String toString() {
		return this.getClass().toString() + " ID:" + this.getId() + " Name: "
				+ this.getName();
	}

	/**
	 * @param content
	 * @returns true if the contents was updated
	 */
	/*
	 * iebdelete public boolean updateContent(String content) { if
	 * (content.equals(this.m_content)) { return false; } // Copy current object
	 * to History. RWikiHistoryObject newHistoryObject = new
	 * RWikiHistoryObjectImpl(); newHistoryObject.setContent(this.getContent());
	 * newHistoryObject.setVersion(this.getVersion());
	 * newHistoryObject.setUser(this.getUser());
	 * 
	 * 
	 * List list = this.getHistory(); if (list == null) { list = new
	 * ArrayList(); this.setHistory(list); } list.add(newHistoryObject); //
	 * finally set the new content this.setContent(content); return true; }
	 */

	/*
	 * public RWikiHistoryObject getRevision(int revision) { int
	 * numberOfRevisions = this.getNumberOfRevisions();
	 * 
	 * if (revision >= 0 && revision < numberOfRevisions) { // This needs to be
	 * a finder return ((RWikiHistoryObject) this.getHistory().get(revision)); }
	 * else if (revision == numberOfRevisions) { RWikiHistoryObjectImpl mock =
	 * new RWikiHistoryObjectImpl(); mock.setContent(this.getContent());
	 * mock.setRevision(numberOfRevisions); mock.setUser(this.getUser());
	 * mock.setVersion(this.getVersion()); return mock; } else { throw new
	 * IllegalArgumentException( "Invalid version number: " + revision); } }
	 */
	/*
	 * public int getNumberOfRevisions() { // this needs to be a finder if
	 * (this.getHistory() != null) return this.getHistory().size(); return 0; }
	 */

	public void setPermissions(boolean[] permissions) {
		if (permissions.length != 8) {
			// yuck
			throw new IllegalArgumentException(
					"Must be given an array of length 8");
		}
		this.setOwnerRead(permissions[0]);
		this.setOwnerWrite(permissions[1]);
		this.setOwnerAdmin(permissions[2]);
		this.setGroupRead(permissions[3]);
		this.setGroupWrite(permissions[4]);
		this.setGroupAdmin(permissions[5]);
		this.setPublicRead(permissions[6]);
		this.setPublicWrite(permissions[7]);

	}

	public void setPermissions(RWikiPermissions permissions) {
		setOwnerRead(permissions.isOwnerRead());
		setOwnerWrite(permissions.isOwnerWrite());
		setOwnerAdmin(permissions.isOwnerAdmin());
		setGroupRead(permissions.isGroupRead());
		setGroupWrite(permissions.isGroupWrite());
		setGroupAdmin(permissions.isGroupAdmin());
		setPublicRead(permissions.isPublicRead());
		setPublicWrite(permissions.isPublicWrite());
	}

	public RWikiPermissions getPermissions() {
		RWikiPermissions permissions = new RWikiPermissionsImpl();
		permissions.setOwnerRead(getOwnerRead());
		permissions.setOwnerWrite(getOwnerWrite());
		permissions.setOwnerAdmin(getOwnerAdmin());
		permissions.setGroupRead(getGroupRead());
		permissions.setGroupWrite(getGroupWrite());
		permissions.setGroupAdmin(getGroupAdmin());
		permissions.setPublicRead(getPublicRead());
		permissions.setPublicWrite(getPublicWrite());
		return permissions;
	}

	public Integer getRevision() {
		return m_revision;
	}

	public void setRevision(Integer revision) {
		this.m_revision = revision;
		// SAK-2470
		if (m_revision == null)
			m_revision = new Integer(0);
	}

	/*
	 * Lazy loading of content.
	 */
	private RWikiObjectContentDao getRwikiObjectContentDao() {
		return codao;
	}

	public void setRwikiObjectContentDao(RWikiObjectContentDao codao) {
		this.codao = codao;
	}

	public RWikiObjectContent getRWikiObjectContent() {
		lazyLoadContentObject();
		return co;
	}

	public void setRWikiObjectContent(RWikiObjectContent co) {
		this.co = co;
	}

	private void lazyLoadContentObject() {
		if (codao == null) {
			// Exception ex = new RuntimeException("TRACE: Content Object DAO is
			// null");
			// System.err.println("Problem with loading Lazy Content, this is
			// Ok, just means lazyLoadContent was called by Hibernate");
			// ex.printStackTrace();
			return;
		}
		if (co == null) {
			co = codao.getContentObject(this);
			if (co == null) {
				co = codao.createContentObject(this);
			}
			// this will cause the Sha1 to be recomputed if its is not present
			// It MUST be done here, outside this if will generate recursion
		}
	}

	public void setContent(String content) {
		lazyLoadContentObject();
		if (content == null)
			content = "";
		if (co != null) // could be null if triggered during a hibernate
			// template load
			co.setContent(content);
		// recompute the Sha1
		sha1 = computeSha1(content);
	}

	public String getContent() {
		lazyLoadContentObject();

		String content = null;
		if (co != null)
			content = co.getContent(); // could be null if triggerd during a
		// template load
		if (content == null)
			content = "";
		return content;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	public String getSha1() {
		return sha1;
	}

	private String sha1;

	private static MessageDigest shatemplate = null;

	public static String computeSha1(String content) {
		String digest = "";
		try {
			if (shatemplate == null) {
				shatemplate = MessageDigest.getInstance("SHA");
			}

			MessageDigest shadigest = (MessageDigest) shatemplate.clone();
			byte[] bytedigest = shadigest.digest(content.getBytes("UTF8"));
			digest = byteArrayToHexStr(bytedigest);
		} catch (Exception ex) {
			System.err.println("Unable to create SHA hash of content");
			ex.printStackTrace();
		}
		return digest;
	}

	private static String byteArrayToHexStr(byte[] data) {
		String output = "";
		String tempStr = "";
		int tempInt = 0;
		for (int cnt = 0; cnt < data.length; cnt++) {
			// Deposit a byte into the 8 lsb of an int.
			tempInt = data[cnt] & 0xFF;
			// Get hex representation of the int as a
			// string.
			tempStr = Integer.toHexString(tempInt);
			// Append a leading 0 if necessary so that
			// each hex string will contain two
			// characters.
			if (tempStr.length() == 1)
				tempStr = "0" + tempStr;
			// Concatenate the two characters to the
			// output string.
			output = output + tempStr;
		}// end for loop
		return output.toUpperCase();
	}// end byteArrayToHexStr

	public ResourceProperties getProperties() {
		ResourceProperties rp = new BaseResourceProperties();
		rp.addProperty("id", this.getId());
		// I dont think that content is a propertyrp.addProperty("content",
		// this.getContent());
		rp.addProperty("name", this.getName());
		rp.addProperty("owner", this.getOwner());
		rp.addProperty("realm", this.getRealm());
		rp.addProperty("referenced", this.getReferenced());
		rp.addProperty("rwid", this.getRwikiobjectid());
		rp.addProperty("sha1", this.getSha1());
		rp.addProperty("source", this.getSource());
		rp.addProperty("user", this.getUser());
		rp.addProperty("group-admin", String.valueOf(this.getGroupAdmin()));
		rp.addProperty("group-read", String.valueOf(this.getGroupRead()));
		rp.addProperty("group-write", String.valueOf(this.getGroupWrite()));
		rp.addProperty("owner-admin", String.valueOf(this.getOwnerAdmin()));
		rp.addProperty("owner-read", String.valueOf(this.getOwnerRead()));
		rp.addProperty("owner-write", String.valueOf(this.getOwnerWrite()));
		rp.addProperty("public-read", String.valueOf(this.getPublicRead()));
		rp.addProperty("public-write", String.valueOf(this.getPublicWrite()));
		rp.addProperty("revision", String.valueOf(this.getRevision()));
		rp.addProperty("version", String.valueOf(this.getVersion().getTime()));
		return rp;
	}

	public String getReference() {
		return this.getName();
	}

	public String getUrl() {
		return this.getName();
	}

	public Element toXml(Document doc, Stack stack) {
		Element wikipage = doc.createElement("wikipage");

		if (stack.isEmpty()) {
			doc.appendChild(wikipage);
		} else {
			((Element) stack.peek()).appendChild(wikipage);
		}

		stack.push(wikipage);

		wikipage.setAttribute("id", m_id);
		wikipage.setAttribute("page-name", m_name);
		wikipage.setAttribute("revision", String.valueOf(m_revision));
		wikipage.setAttribute("user", m_user);
		wikipage.setAttribute("owner", m_owner);

		// I would like to be able to render this, but we cant... because its a
		// pojo !
		getProperties().toXml(doc, stack);
		Element content = doc.createElement("wikicontent");
		stack.push(content);
		wikipage.appendChild(content);
		content.setAttribute("enc", "BASE64");
		try {
			String b64Content = Base64.encode(getContent().getBytes("UTF-8"));
			CDATASection t = doc.createCDATASection(b64Content);
			stack.push(t);
			content.appendChild(t);
			stack.pop();
		} catch (UnsupportedEncodingException usex) {
			// if UTF-8 isnt available, we are in big trouble !
		}
		stack.pop();

		stack.pop();

		return wikipage;
	}

	public void fromXml(Element el, String defaultRealm) throws Exception {
		NodeList nl = el.getElementsByTagName("properties");
		if (nl == null || nl.getLength() != 1)
			throw new Exception("Cant find a properties element in "
					+ el.getNodeName() + " id: " + el.getAttribute("id")
					+ " pagename: " + el.getAttribute("page-name"));
		// only take the first properties
		Element properties = (Element) nl.item(0);
		ResourceProperties rp = new BaseResourceProperties(properties);

		nl = el.getElementsByTagName("wikicontent");
		if (nl == null || nl.getLength() != 1)
			throw new Exception("Cant find a  wikiproperties element in "
					+ el.getNodeName() + " id: " + el.getAttribute("id")
					+ " pagename: " + el.getAttribute("page-name"));
		// only accpet the first
		Element wikiContents = (Element) nl.item(0);

		nl = wikiContents.getChildNodes();
		StringBuffer content = new StringBuffer();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n instanceof CharacterData) {
				CharacterData cdnode = (CharacterData) n;
				try {
					content.append(new String(Base64.decode(cdnode.getData()),
							"UTF-8"));
				} catch (Throwable t) {
					Log.warn("","Cant decode node content for " + cdnode);
				}
			}
		}

		String realm = rp.getProperty("realm");
		setId(rp.getProperty("id"));

		setName(NameHelper.globaliseName(NameHelper.localizeName(rp
				.getProperty("name"), realm), defaultRealm));
		setOwner(rp.getProperty("owner"));
		setRealm(defaultRealm);
		setReferenced(rp.getProperty("referenced"));
//		setRwikiobjectid(rp.getProperty("rwid"));
		setContent(content.toString());

		if (!getSha1().equals(rp.getProperty("sha1")))
			throw new Exception("Sha Checksum Missmatch on content "
					+ rp.getProperty("sha1") + " != " + getSha1());
		setSource(rp.getProperty("source"));
		setUser(rp.getProperty("user"));
		setGroupAdmin(rp.getBooleanProperty("group-admin"));
		setGroupRead(rp.getBooleanProperty("group-read"));
		setGroupWrite(rp.getBooleanProperty("group-write"));
		setOwnerAdmin(rp.getBooleanProperty("group-admin"));
		setOwnerRead(rp.getBooleanProperty("owner-read"));
		setOwnerWrite(rp.getBooleanProperty("owner-write"));
		setGroupAdmin(rp.getBooleanProperty("owner-admin"));
		setPublicRead(rp.getBooleanProperty("public-read"));
		setPublicWrite(rp.getBooleanProperty("public-write"));
		setRevision(new Integer(rp.getProperty("revision")));
		setVersion(new Date(rp.getLongProperty("version")));

	}
}

/*******************************************************************************
 * 
 * $Header$
 * 
 ******************************************************************************/
