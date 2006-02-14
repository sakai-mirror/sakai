/**********************************************************************************
 *
 * $Header$
 *
 ***********************************************************************************
 *
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

package uk.ac.cam.caret.sakai.rwiki.component.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import uk.ac.cam.caret.sakai.rwiki.service.api.RenderService;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.utils.DebugContentHandler;
import uk.ac.cam.caret.sakai.rwiki.utils.NameHelper;
import uk.ac.cam.caret.sakai.rwiki.utils.SchemaNames;
import uk.ac.cam.caret.sakai.rwiki.utils.SimpleCoverage;

/**
 * Provides a XSLT Based entity handler. It will serialise the an RWikiObject
 * into XML and then apply a XSLT to generate the Whole output.
 * 
 * @author ieb
 * 
 */
public class XSLTEntityHandler extends BaseEntityHandlerImpl {
	/**
	 * dependency
	 */
	private String anchorLinkFormat = null;

	/**
	 * dependency
	 */
	private String standardLinkFormat = null;

	/**
	 * dependency
	 */
	private String hrefTagFormat = null;

	/**
	 * dependency
	 */
	private RenderService renderService = null;

	/**
	 * dependency
	 */
	private String authZPrefix = "";

	/**
	 * Logger.
	 */
	private Logger logger = null;

	/**
	 * dependency The base name of the xslt file, relative to context root.
	 */
	private String xslt = null;

	/**
	 * dependency The default strack trace message to use if all else fails
	 * (pattern).
	 */
	private String defaultStackTrace;

	/**
	 * A format pattern for formatting a stack trace in the xml.@param tag for
	 * 'servletConfig'. 140
	 * 
	 * Expected
	 * 
	 * @throws tag
	 *             for 'ServletException'.
	 */
	private String errorFormat;


	/**
	 * Thread holder for the transformer 
	 */
	private ThreadLocal transformerHolder = new ThreadLocal();


	/**
	 * {@inheritDoc}
	 * 
	 * 
	 */
	public String getDescription(Entity entity) {
		if (!(entity instanceof RWikiObject))
			return null;
		return entity.getId();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * 
	 */
	public String getUrl(Entity entity) {
		if (!(entity instanceof RWikiObject))
			return null;
		return entity.getUrl();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * 
	 */
	public void outputContent(Entity entity, HttpServletRequest request,
			HttpServletResponse res) {
		if (!(entity instanceof RWikiObject))
			return;

		Document doc = Xml.createDocument();
		Stack stack = new Stack();
		Element root = doc.createElement("entity");
		Date now = new Date();
		doc.appendChild(root);
		stack.push(root);
		root.setAttribute("date", now.toString());
		String user = request.getRemoteUser();
		try {
				PrintWriter pw = res.getWriter();

				
				ContentHandler opch = getOutputHandler(pw);
				ContentHandler ch = null;
				if (false) {
					ch = new DebugContentHandler(opch);
				} else {
					ch = opch;
				}

				Attributes dummyAttributes = new AttributesImpl();
				
				ch.startDocument();
				ch.startElement(SchemaNames.NS_CONTAINER,
						SchemaNames.EL_ENTITYSERVICE,
						SchemaNames.EL_NSXMLSERVICE, dummyAttributes);
				ch.startElement(SchemaNames.NS_CONTAINER,
						SchemaNames.EL_ENTITY, SchemaNames.EL_NSXMLSERVICE,
						dummyAttributes);
				ch.startElement(SchemaNames.NS_CONTAINER,
						SchemaNames.EL_XMLPROPERTIES,
						SchemaNames.EL_NSXMLPROPERTIES, dummyAttributes);
				ResourceProperties rp = entity.getProperties();

				for (Iterator i = rp.getPropertyNames(); i.hasNext();) {
					Object key = i.next();
					String name = String.valueOf(key);
					String value = String.valueOf(rp.getProperty(name));
					AttributesImpl propA = new AttributesImpl();
					propA.addAttribute("", SchemaNames.ATTR_NAME,
							SchemaNames.ATTR_NAME, "string", name);
					addElement(ch, SchemaNames.NS_CONTAINER,
							SchemaNames.EL_XMLPROPERTY,
							SchemaNames.EL_NSXMLPROPERTY, propA, value);
				}
				{
					AttributesImpl propA = new AttributesImpl();
					propA.addAttribute("", SchemaNames.ATTR_NAME,
							SchemaNames.ATTR_NAME, "string", "_title");
					addElement(ch, SchemaNames.NS_CONTAINER,
							SchemaNames.EL_XMLPROPERTY,
							SchemaNames.EL_NSXMLPROPERTY, propA,
							" XSLTEntity Handler");
				}
				ch.endElement(SchemaNames.NS_CONTAINER,
						SchemaNames.EL_XMLPROPERTIES,
						SchemaNames.EL_NSXMLPROPERTIES);
				if (entity instanceof RWikiObject) {
					ch.startElement(SchemaNames.NS_CONTAINER,
							SchemaNames.EL_RENDEREDCONTENT,
							SchemaNames.EL_NSXMLPROPERTIES, dummyAttributes);
					renderToXML((RWikiObject) entity, ch, user);
					ch.endElement(SchemaNames.NS_CONTAINER,
							SchemaNames.EL_RENDEREDCONTENT,
							SchemaNames.EL_NSXMLSERVICE);
				}
				ch.endElement(SchemaNames.NS_CONTAINER, SchemaNames.EL_ENTITY,
						SchemaNames.EL_NSXMLSERVICE);
				ch.endElement(SchemaNames.NS_CONTAINER,
						SchemaNames.EL_ENTITYSERVICE,
						SchemaNames.EL_NSXMLSERVICE);

				ch.endDocument();


		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Failed to serialise "
					+ ex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * Adds an element to the content handler.
	 * 
	 * @param ch
	 *            the content handler
	 * @param ns
	 *            the name space of the element
	 * @param lname
	 *            the local name
	 * @param qname
	 *            the qname
	 * @param attr
	 *            the attribute list
	 * @param content
	 *            content of the element
	 * @throws SAXException
	 *             if the underlying sax chain has a problem
	 */
	private void addElement(final ContentHandler ch, final String ns,
			final String lname, final String qname, final Attributes attr,
			final Object content) throws SAXException {

		ch.startElement(ns, lname, qname, attr);
		try {
			if (content != null) {
				char[] c = content.toString().toCharArray();
				ch.characters(c, 0, c.length);
			}
		} finally {
			ch.endElement(ns, lname, qname);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * 
	 */
	public ResourceProperties getProperties(Entity entity) {
		return entity.getProperties();
	}

	/**
	 * Serialises the rendered content of the RWiki Object to SAX
	 * 
	 * @param rwo
	 * @param ch
	 */
	private void renderToXML(RWikiObject rwo, final ContentHandler ch,
			String user) throws SAXException, IOException {

		/**
		 * create a proxy for the stream, filtering out the start element and
		 * end element events
		 */
		ContentHandler proxy = new ContentHandler() {
			public void setDocumentLocator(Locator arg0) {
				ch.setDocumentLocator(arg0);
			}

			public void startDocument() throws SAXException {
				// ignore
			}

			public void endDocument() throws SAXException {
				// ignore
			}

			public void startPrefixMapping(String arg0, String arg1)
					throws SAXException {
				ch.startPrefixMapping(arg0, arg1);
			}

			public void endPrefixMapping(String arg0) throws SAXException {
				ch.endPrefixMapping(arg0);
			}

			public void startElement(String arg0, String arg1, String arg2,
					Attributes arg3) throws SAXException {
				ch.startElement(arg0, arg1, arg2, arg3);
			}

			public void endElement(String arg0, String arg1, String arg2)
					throws SAXException {
				ch.endElement(arg0, arg1, arg2);
			}

			public void characters(char[] arg0, int arg1, int arg2)
					throws SAXException {
				ch.characters(arg0, arg1, arg2);
			}

			public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
					throws SAXException {
				ch.ignorableWhitespace(arg0, arg1, arg2);
			}

			public void processingInstruction(String arg0, String arg1)
					throws SAXException {
				ch.processingInstruction(arg0, arg1);
			}

			public void skippedEntity(String arg0) throws SAXException {
				ch.skippedEntity(arg0);
			}

		};

		String localSpace = NameHelper.localizeSpace(rwo.getName(), rwo
				.getRealm());
		ComponentPageLinkRenderImpl plr = new ComponentPageLinkRenderImpl(
				localSpace);

		plr.setAnchorURLFormat(anchorLinkFormat);
		plr.setStandardURLFormat(standardLinkFormat);
		plr.setUrlFormat(hrefTagFormat);

		if (renderService == null) {
			// only for testing
			char[] c = rwo.getContent().toCharArray();
			ch.characters(c, 0, c.length);
		} else {
			String renderedPage = renderService.renderPage(rwo, user,
					localSpace, plr);

			renderedPage = "<content>"+renderedPage+"</content>";
			InputSource ins = new InputSource(new StringReader(renderedPage));
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(proxy);
			xmlReader.parse(ins);

		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * 
	 */
	public Collection getAuthzGroups(Reference ref) {
		SimpleCoverage.cover();
		// use the resources realm, all container (folder) realms

		Collection rv = new Vector();

		try {
			// try the resource, all the folders above it (don't include /)
			SimpleCoverage.cover();
			String paths[] = StringUtil.split(ref.getId(), Entity.SEPARATOR);
			boolean container = ref.getId().endsWith(Entity.SEPARATOR);
			if (paths.length > 1) {

				SimpleCoverage.cover();
				String root = authZPrefix + Entity.SEPARATOR + paths[1]
						+ Entity.SEPARATOR;
				rv.add(root);

				for (int next = 2; next < paths.length; next++) {
					SimpleCoverage.cover();
					root = root + paths[next];
					if ((next < paths.length - 1) || container) {
						SimpleCoverage.cover();
						root = root + Entity.SEPARATOR;
					}
					rv.add(root);
				}
			}

			// special check for group-user : the grant's in the user's My
			// Workspace site
			String parts[] = StringUtil.split(ref.getId(), Entity.SEPARATOR);
			if ((parts.length > 3) && (parts[1].equals("group-user"))) {
				SimpleCoverage.cover();
				rv.add(SiteService.siteReference(SiteService
						.getUserSiteId(parts[3])));
			}

			// site
			SimpleCoverage.cover();
			ref.addSiteContextAuthzGroup(rv);
		} catch (Throwable e) {
			SimpleCoverage.cover();
			logger.error(this + " Problem ", e);
		}

		return rv;
	}

	/**
	 * called by spring.
	 */
	public void init() {
		try {
				XSLTTransform xsltTransform = new XSLTTransform();
				xsltTransform.setXslt(new InputSource(this.getClass()
						.getResourceAsStream(xslt)));
				xsltTransform.getContentHandler();
		} catch (Exception ex) {
			logger.error("Failed to initialise XSLTTransformer context ", ex);
		}
	}

	/**
	 * called by spring.
	 */
	public void destroy() {
	}

	/**
	 * @see uk.co.tfd.sakai.xmlserver.api.OutputContentHandler#getOutputHandler(java.io.Writer)
	 */

	public ContentHandler getOutputHandler(Writer out) throws IOException {
		try {
			XSLTTransform xsltTransform = (XSLTTransform) transformerHolder
					.get();
			if (xsltTransform == null) {
				xsltTransform = new XSLTTransform();
				xsltTransform.setXslt(new InputSource(this.getClass()
						.getResourceAsStream(xslt)));
				transformerHolder.set(xsltTransform);
			}
			ContentHandler ch = xsltTransform.getOutputHandler(out);
			return ch;
		} catch (Exception ex) {
			String stackTrace = null;
			try {
				StringWriter exw = new StringWriter();
				PrintWriter pw = new PrintWriter(exw);
				ex.printStackTrace(pw);
				stackTrace = exw.toString();
			} catch (Exception ex2) {
				stackTrace = MessageFormat.format(defaultStackTrace,
						new Object[] { ex.getMessage() });
			}
			out.write(MessageFormat.format(errorFormat, new Object[] {
					ex.getMessage(), stackTrace }));
			return null;
		}
	}

	
//	Need to configure components correctly.
	
	
	
	/**
	 * @return Returns the xslt.
	 */
	public String getXslt() {
		return xslt;
	}

	/**
	 * @param xslt
	 *            The xslt to set.
	 */
	public void setXslt(final String xslt) {
		this.xslt = xslt;
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
	public void setLogger(final Logger logger) {
		this.logger = logger;
	}

	/**
	 * @return Returns the defaultStackTrace.
	 */
	public String getDefaultStackTrace() {
		return defaultStackTrace;
	}

	/**
	 * @param defaultStackTrace
	 *            The defaultStackTrace to set.
	 */
	public void setDefaultStackTrace(final String defaultStackTrace) {
		this.defaultStackTrace = defaultStackTrace;
	}

	/**
	 * @return Returns the errorFormat.
	 */
	public String getErrorFormat() {
		return errorFormat;
	}

	/**
	 * @param errorFormat
	 *            The errorFormat to set.
	 */
	public void setErrorFormat(final String errorFormat) {
		this.errorFormat = errorFormat;
	}

	/**
	 * @return Returns the authZPrefix.
	 */
	public String getAuthZPrefix() {
		return authZPrefix;
	}

	/**
	 * @param authZPrefix
	 *            The authZPrefix to set.
	 */
	public void setAuthZPrefix(String authZPrefix) {
		this.authZPrefix = authZPrefix;
	}

	/**
	 * @return Returns the anchorLinkFormat.
	 */
	public String getAnchorLinkFormat() {
		return anchorLinkFormat;
	}

	/**
	 * @param anchorLinkFormat The anchorLinkFormat to set.
	 */
	public void setAnchorLinkFormat(String anchorLinkFormat) {
		this.anchorLinkFormat = anchorLinkFormat;
	}

	/**
	 * @return Returns the hrefTagFormat.
	 */
	public String getHrefTagFormat() {
		return hrefTagFormat;
	}

	/**
	 * @param hrefTagFormat The hrefTagFormat to set.
	 */
	public void setHrefTagFormat(String hrefTagFormat) {
		this.hrefTagFormat = hrefTagFormat;
	}

	/**
	 * @return Returns the renderService.
	 */
	public RenderService getRenderService() {
		return renderService;
	}

	/**
	 * @param renderService The renderService to set.
	 */
	public void setRenderService(RenderService renderService) {
		this.renderService = renderService;
	}

	/**
	 * @return Returns the standardLinkFormat.
	 */
	public String getStandardLinkFormat() {
		return standardLinkFormat;
	}

	/**
	 * @param standardLinkFormat The standardLinkFormat to set.
	 */
	public void setStandardLinkFormat(String standardLinkFormat) {
		this.standardLinkFormat = standardLinkFormat;
	}

}
