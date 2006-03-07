/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.component.service.impl;

import java.util.HashMap;

import org.apache.xml.serializer.ToXMLStream;
import org.xml.sax.SAXException;

/**
 * @author andrew
 * 
 */
public class XHTMLSerializer extends ToXMLStream {

	private static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";

	private static HashMap emptyTag = new HashMap();

	static {
		// inclusion els
		emptyTag.put("img", "img");
		emptyTag.put("area", "area");
		emptyTag.put("frame", "frame");
		// non-standard inclusion els
		emptyTag.put("layer", "layer");
		emptyTag.put("embed", "embed");
		// form el
		emptyTag.put("input", "input");
		// default els
		emptyTag.put("base", "base");
		// styling els
		emptyTag.put("col", "col");
		emptyTag.put("basefont", "basefont");
		// hidden els
		emptyTag.put("link", "link");
		emptyTag.put("meta", "meta");
		// separator els
		emptyTag.put("br", "br");
		emptyTag.put("hr", "hr");
		// here because our current p implementation is broken
		emptyTag.put("p", "p");
	}

	public void endElement(String namespaceURI, String localName, String name)
			throws SAXException {
		if ((namespaceURI != null && !namespaceURI.equals("") && !namespaceURI.equals(XHTML_NAMESPACE))
				|| emptyTag.containsKey(localName.toLowerCase())) {
			super.endElement(namespaceURI, localName, name);
			return;
		}

		this.characters("");
		
		super.endElement(namespaceURI, localName, name);

	}

}