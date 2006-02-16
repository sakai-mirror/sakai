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

package uk.ac.cam.caret.sakai.rwiki.component.model.impl;

import java.io.UnsupportedEncodingException;
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

import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiEntity;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.utils.NameHelper;

public class RWikiEntityImpl implements RWikiEntity {


	private RWikiObject rwo = null;
	
	public RWikiEntityImpl( RWikiObject rwo ) {
		this.rwo = rwo;
	}
	/**
	 * {@inheritDoc}
	 */
	public ResourceProperties getProperties() {
		ResourceProperties rp = new BaseResourceProperties();
		rp.addProperty("id", this.getId());
		// I dont think that content is a propertyrp.addProperty("content",
		// this.getContent());
		rp.addProperty("name", rwo.getName());
		rp.addProperty("owner", rwo.getOwner());
		rp.addProperty("realm", rwo.getRealm());
		rp.addProperty("referenced", rwo.getReferenced());
		rp.addProperty("rwid", rwo.getRwikiobjectid());
		rp.addProperty("sha1", rwo.getSha1());
		rp.addProperty("user", rwo.getUser());
		rp.addProperty("group-admin", String.valueOf(rwo.getGroupAdmin()));
		rp.addProperty("group-read", String.valueOf(rwo.getGroupRead()));
		rp.addProperty("group-write", String.valueOf(rwo.getGroupWrite()));
		rp.addProperty("owner-admin", String.valueOf(rwo.getOwnerAdmin()));
		rp.addProperty("owner-read", String.valueOf(rwo.getOwnerRead()));
		rp.addProperty("owner-write", String.valueOf(rwo.getOwnerWrite()));
		rp.addProperty("public-read", String.valueOf(rwo.getPublicRead()));
		rp.addProperty("public-write", String.valueOf(rwo.getPublicWrite()));
		rp.addProperty("revision", String.valueOf(rwo.getRevision()));
		rp.addProperty("version", String.valueOf(rwo.getVersion().getTime()));
		return rp;
	}
	/**
	 * {@inheritDoc}
	 */
	public String getReference() {
		return "/wiki"+rwo.getName()+".";
	}
	/**
	 * {@inheritDoc}
	 */
	public String getUrl() {
		return "/wiki"+rwo.getName()+".";
	}
	/**
	 * {@inheritDoc}
	 */
	public Element toXml(Document doc, Stack stack) {
		Element wikipage = doc.createElement("wikipage");

		if (stack.isEmpty()) {
			doc.appendChild(wikipage);
		} else {
			((Element) stack.peek()).appendChild(wikipage);
		}

		stack.push(wikipage);

		wikipage.setAttribute("id", rwo.getId());
		wikipage.setAttribute("page-name",rwo.getName());
		wikipage.setAttribute("revision", String.valueOf(rwo.getRevision()));
		wikipage.setAttribute("user", rwo.getUser());
		wikipage.setAttribute("owner", rwo.getOwner());

		// I would like to be able to render this, but we cant... because its a
		// pojo !
		getProperties().toXml(doc, stack);
		Element content = doc.createElement("wikicontent");
		stack.push(content);
		wikipage.appendChild(content);
		content.setAttribute("enc", "BASE64");
		try {
			String b64Content = Base64.encode(rwo.getContent().getBytes("UTF-8"));
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
	/**
	 * {@inheritDoc}
	 */
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
		rwo.setId(rp.getProperty("id"));

		rwo.setName(NameHelper.globaliseName(NameHelper.localizeName(rp
				.getProperty("name"), realm), defaultRealm));
		rwo.setOwner(rp.getProperty("owner"));
		rwo.setRealm(defaultRealm);
		rwo.setReferenced(rp.getProperty("referenced"));
//		rwo.setRwikiobjectid(rp.getProperty("rwid"));
		rwo.setContent(content.toString());

		if (!rwo.getSha1().equals(rp.getProperty("sha1")))
			throw new Exception("Sha Checksum Missmatch on content "
					+ rp.getProperty("sha1") + " != " + rwo.getSha1());
		rwo.setUser(rp.getProperty("user"));
		rwo.setGroupAdmin(rp.getBooleanProperty("group-admin"));
		rwo.setGroupRead(rp.getBooleanProperty("group-read"));
		rwo.setGroupWrite(rp.getBooleanProperty("group-write"));
		rwo.setOwnerAdmin(rp.getBooleanProperty("group-admin"));
		rwo.setOwnerRead(rp.getBooleanProperty("owner-read"));
		rwo.setOwnerWrite(rp.getBooleanProperty("owner-write"));
		rwo.setGroupAdmin(rp.getBooleanProperty("owner-admin"));
		rwo.setPublicRead(rp.getBooleanProperty("public-read"));
		rwo.setPublicWrite(rp.getBooleanProperty("public-write"));
		rwo.setRevision(new Integer(rp.getProperty("revision")));
		rwo.setVersion(new Date(rp.getLongProperty("version")));

	}
	/**
	 * @inheritDoc
	 */
	public String getReference(String rootProperty)
	{
		return getReference();
	}

	/**
	 * @inheritDoc
	 */
	public String getUrl(String rootProperty)
	{
		return getUrl();
	}
	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return rwo.getId();
	}
	/**
	 * {@inheritDoc}
	 */
	public RWikiObject getRWikiObject() {
		return rwo;
	}

}
