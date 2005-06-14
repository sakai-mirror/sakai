/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/FormattedText.java,v 1.19 2004/09/29 00:19:33 janderse.umich.edu Exp $
*
***********************************************************************************
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

// package
package org.sakaiproject.util;

//import java.io.StringReader;
import java.util.Stack;
import java.util.regex.Pattern;

//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.SAXParserFactory;
//import javax.xml.parsers.SAXParser;
//import org.xml.sax.helpers.DefaultHandler;
//import org.xml.sax.Attributes;

//import org.w3c.dom.Document;
//import org.xml.sax.InputSource;


/**
* FormattedText provides support for formatted text.
* This includes text formatting in user input such as bold, underline, and fonts.  
* TODO: Go over all methods carefully, and make them operate in a more standard and more effient manner.
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.19 $
*/
public class FormattedText
{
	
	/** This list of good and evil tags was extracted from:
	 * @link http://www.blooberry.com/indexdot/html/tagindex/all.htm
	 */
	private static String[] M_goodTags = {"a", "abbr", "acronym", "address", "b", "big", "blockquote", "br", "center", "cite", "code", 
											"dd", "del", "dir", "div", "dl", "dt", "em", "font", "hr", "h1", "h2", "h3", "h4", "h5", "h6", "i", "ins",
											"kbd", "li", "marquee", "menu", "nobr", "ol", "p", "pre", "q", "rt", "ruby", "rbc", "rb", "rtc", "rp",
											"s", "samp", "small", "span", "strike", "strong", "sub", "sup", "tt", "u", "ul", "var", "xmp" 
											};
	
	/** These evil HTML tags are disallowed when the user inputs formatted text; this protects
	 * the system from broken pages as well as Cross-Site Scripting (XSS) attacks.
	 */										
	private static String[] M_evilTags = { "applet", "base", "body", "bgsound", "button", "col", "colgroup", "comment", "embed", 
											"dfn", "embed", "fieldset", "form", "frame", "frameset", "head", "html",
											"iframe", "ilayer", "img", "inlineinput", "isindex", "input", "keygen", "label", "layer", "legend",
											"link", "listing", "map", "meta", "multicol", "nextid", "noembed", "noframes", "nolayer", "noscript",
											"object", "optgroup", "option", "param", "plaintext", "script", "select", "sound", "spacer",
											"spell", "submit", "textarea", "title", "wbr"
											};										
											
	/** TODO: Decide which of these HTML tags to allow, and which to disallow in formatted text */
	private static String[] M_maybeTags = {"img", "style", "span", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "xml"
	};

	/** An array of regular expression pattern-matchers, that will match the tags given in M_evilTags */
	private static Pattern[] M_evilTagsPatterns;
	
	static { init(); }
	
	private static void init()
	{
		M_evilTagsPatterns = new Pattern[M_evilTags.length];
		for (int i=0; i<M_evilTags.length; i++)
		{
			// matches the start of the particular evil tag "<" followed by whitespace, 
			// followed by the tag name, followed by anything, followed by ">", case insensitive, 
			// allowed to match over multiple lines.
			M_evilTagsPatterns[i] = Pattern.compile(".*<\\s*" + M_evilTags[i] + ".*>.*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
		}
	}

	private static String MSG_NO_HTML_COMMENTS = "HTML-style comments like <!-- comment --> are not allowed in formatted text.";
		
	/** Matches HTML-style line breaks like &lt;br&gt; */
	private static Pattern M_patternTagBr = Pattern.compile("<\\s*br.*?>");
	
	/** Matches HTML-style comments like &lt;!-- comment --&gt; */
	private static Pattern M_patternTagComment = Pattern.compile(".*<\\s*!.*-->.*", Pattern.DOTALL);

	/** Matches any HTML-style tag, like &lt;anything&gt; */
	private static Pattern M_patternTag = Pattern.compile("<.*?>", Pattern.DOTALL);
	
	
	/**
	 * Processes and validates formatted text received from the web browser (from the formatted text widget).
	 * Validates that the user input follows the Sakai formatted text specification;
	 * disallows dangerous stuff such as &lt;SCRIPT&gt; JavaScript tags.  Encodes the text
	 * according to the formatted text specification, for the rest of the system to use.
	 * @param strFromBrowser The formatted text as sent from the web browser (from the formatted text widget)
	 * @param errorMessages User-readable error messages will be returned here.
	 * @return The validated processed formatted text, ready for use by the system.
	 */
	public static String processFormattedText(final String strFromBrowser, StringBuffer errorMessages)
	{
		boolean checkForEvilTags = true;
		boolean replaceWhitespaceTags = true;
		return processFormattedText(strFromBrowser, errorMessages, checkForEvilTags, replaceWhitespaceTags);
	}
	
	
	/**
	 * Process an HTML document that has been edited using the formatted text widget.
	 * The document can contain any valid HTML; it will NOT be checked to eliminate
	 * things like image tags, script tags, etc, because it is its own document.
	 * @param strFromBrowser
	 * @param errorMessages
	 * @return
	 */
	public static String processHtmlDocument(final String strFromBrowser, StringBuffer errorMessages)
	{
		return strFromBrowser;
		
		//boolean checkForEvilTags = false;
		//boolean replaceWhitespaceTags = false;
		//return processFormattedText(strFromBrowser, errorMessages, checkForEvilTags, replaceWhitespaceTags);
	}
	
	/**
	 * Processes and validates formatted text received from the web browser (from the formatted text widget).
	 * Validates that the user input follows the Sakai formatted text specification;
	 * can disallow dangerous stuff such as &lt;SCRIPT&gt; JavaScript tags.  Encodes the text
	 * according to the formatted text specification, for the rest of the system to use.
	 * @param strFromBrowser The formatted text as sent from the web browser (from the formatted text widget)
	 * @param errorMessages User-readable error messages will be returned here.
	 * @param checkForEvilTags If true, check for tags and attributes that shouldn't be in formatted text
	 * @param replaceWhitespaceTags If true, replace tags such as "<p>" "<br />" with newlines
	 * @return The validated processed formatted text, ready for use by the system.
	 */
	public static String processFormattedText(final String strFromBrowser, StringBuffer errorMessages, boolean checkForEvilTags, boolean replaceWhitespaceTags)
	{
		String val = strFromBrowser;
		if (val == null || val.length() == 0) return val;
		
		if (replaceWhitespaceTags)
		{
			// replace "<br>" HTML tags with newlines
			val = M_patternTagBr.matcher(val).replaceAll("\n");
			
			// replace "<p>" with nothing.  Replace "</p>" and "<p />" HTML tags with newlines
			val = val.replaceAll("<p>", "");
			val = val.replaceAll("</p>", "\n");
			val = val.replaceAll("<p />", "\n");
		}

		if (checkForEvilTags)
		{
			// don't allow HTML comments
			if (M_patternTagComment.matcher(strFromBrowser).matches())
			{
				errorMessages.append(MSG_NO_HTML_COMMENTS+"\n");
			}
	
			// don't allow evil tags
			for (int i=0; i<M_evilTags.length; i++)
			{
				if (M_evilTagsPatterns[i].matcher(strFromBrowser).matches())
				{
					errorMessages.append("The HTML tag '<"+M_evilTags[i]+">' is not allowed in formatted text.\n");
				}
			}
		}
		
		// close any open HTML tags (that the user may have accidentally left open)
		StringBuffer buf = new StringBuffer();
		trimFormattedText(val, Integer.MAX_VALUE, buf);
		val = buf.toString();
		
		//TODO: Fully parse and validate the formatted text against
		// the formatted text specification.  Perhaps this could be 
		// done by treating the text as an XML document and validating
		// the XML document against a Document-Type-Definition (DTD) for
		// formatted text.  This would allow for validating the 
		// attributes of allowed tags, for example.
		
		return val;
	}

	
	/** 
	 * Escapes the given formatted text for output as part of an HTML document.
	 * @param value The formatted text to escape
	 * @return The string to include in an HTML document.
	 */
	public static String escapeHtmlFormattedText(String value)
	{
		if (value == null) return "";
		
		// transform new-lines into "<br />" for proper HTML display
		value = value.replaceAll("\\n", "<br />");
		
		return value;	
	}

	
	public static String escapeHtmlFormattedTextPreserveNewlines(String value)
	{
		if (value == null) return "";
		return value;
	}
	
	
	/**
	 * Escapes the given formatted text for editing within a the formatted text area editing widget.
	 * All HTML meta-characters in the string must be escaped.  However, new lines are NOT turned
	 * into "<br />" at this stage, since that won't display properly in a normal textarea.
	 * @param value The formatted text to escape
	 * @return The string to use as the value of the formatted textarea widget
	 */
	public static String escapeHtmlFormattedTextarea(String value)
	{
		return Validator.escapeHtmlPreserveNewlines(value);
	}
	
	/**
	 * Converts the given plain text into formatted text.
	 * Conversion to formatted text involves escaping characters that are used for formatting (such as the '<' character)
	 * @param value The plain text to convert to formatted text
	 * @return The converted plain text, now as formatted text
	 */
	public static String convertPlaintextToFormattedText(String value)
	{
		// TODO: Implement fully!
		return Validator.escapeHtmlPreserveNewlines(value);
		
	}
	
	/**
	 * Converts the given formatted text to plain text - loses formatting information.
	 * For example, The formatted text "Hello <b>World!</b>" becomes plain text "Hello World!"
	 * Strips all formatting information from the formatted text
	 * @param value The formatted text to convert
	 * @return The plain text (all formatting removed)
	 */
	public static String convertFormattedTextToPlaintext(String value)
	{
		if (value == null) return null;
		
		// convert "<br />" to newline
		value = M_patternTagBr.matcher(value).replaceAll("\n");		
		
		// strip out all the HTML-style tags so that:
		// <font face="verdana">Something</font> <b>Something else</b>
		// becomes:
		// Something Something else		
		value = M_patternTag.matcher(value).replaceAll("");
		
		// Replace HTML character entity references (like &gt;)
		// with the plain Unicode characters to which they refer.
		String ref;
		char val;
		for (int i=0; i<M_htmlCharacterEntityReferences.length; i++)
		{
			ref = M_htmlCharacterEntityReferences[i];
			if (value.indexOf(ref) >= 0)
			{
				val = M_htmlCharacterEntityReferencesUnicode[i];
				//System.out.println("REPLACING "+ref+" WITH UNICODE CHARACTER #"+val+" WHICH IN JAVA IS "+Character.toString(val));
				value = value.replaceAll(ref, Character.toString(val));
			}
		}
		
		// Replace HTML numeric character entity references (like &#nnnn; or &#xnnnn;)
		// with the plain Unicode characters to which they refer.
		value = decodeNumericCharacterReferences(value);
		
		return value;
	}
	
	
	/**
	 * Trims a formatted text string to the given maximum number of displayed characters, preserving formatting.
	 * For example, trim("Hello &amp; <b>World</b>!", 9) returns "Hello &amp; <b>W</b>"
	 * Ignores HTML comments like "<!-- comment -->"
	 * @param formattedText The formatted text to trim
	 * @param maxNumOfChars The maximum number of displayed characters in the returned trimmed formatted text.
	 * @param strTrimmed A StringBuffer to hold the trimmed formatted text
	 * @return true If the formatted text was trimmed
	 */
	public static boolean trimFormattedText(String formattedText, final int maxNumOfChars, StringBuffer strTrimmed)
	{
		// This should return a formatted text substring which contains formatting, but which
		// isn't broken in the middle of formatting, eg, "<strong>Hi there</stro"  It also shouldn't
		// break HTML character entities such as "&gt;".
		
		String str = formattedText;
		strTrimmed.setLength(0);
		strTrimmed.append(str);
		if (str == null) return false;
		
		int count = 0; // number of displayed characters seen so far
		int pos = 0; // raw position within the formatted text string
		int len = str.length();
		Stack tags = new Stack(); // currently open tags (may need to be closed at the end)
		while (pos < len && count < maxNumOfChars)
		{
			while (pos < len && str.charAt(pos) == '<')
			{
				// currently parsing a tag
				pos++;
				
				if (pos < len && str.charAt(pos) == '!')
				{
					// parsing an HTML comment
					if (pos+2 < len)
					{
						if (str.charAt(pos+1) == '-' && str.charAt(pos+2) == '-')
						{
							// skip past the close of the comment tag
							int close = str.indexOf("-->", pos);
							if (close != -1)
							{
								pos = close+3;
								continue;
							}
						}
					}
				}
				
				if (pos < len && str.charAt(pos) == '/')
				{
					// currently parsing an closing tag
					if (!tags.isEmpty()) tags.pop();
					while (pos < len && str.charAt(pos) != '>') pos++;
					pos++;
					continue;
				}
				// capture the name of the opening tag and put it on the stack of open tags
				int taglen = 0;
				String tag;
				while (pos < len && str.charAt(pos) != '>' && !Character.isWhitespace(str.charAt(pos)))
				{
					pos++;
					taglen++;
				}
				tag = str.substring(pos - taglen, pos);
				tags.push(tag);
				
				while (pos < len && str.charAt(pos) != '>') pos++;
				
				if (tag.length() == 0) 
				{
					if (!tags.isEmpty()) tags.pop();
					continue;
				}
				if (str.charAt(pos-1) == '/') if (!tags.isEmpty()) tags.pop(); // singleton tag like "<br />" has no closing tag
				if (tag.charAt(0) == '!') if (!tags.isEmpty()) tags.pop(); // comment tag like "<!-- comment -->", so just ignore it
				if ("br".equalsIgnoreCase(tag)) if (!tags.isEmpty()) tags.pop();
				if ("hr".equalsIgnoreCase(tag)) if (!tags.isEmpty()) tags.pop();
				if ("meta".equalsIgnoreCase(tag)) if (!tags.isEmpty()) tags.pop();
				if ("link".equalsIgnoreCase(tag)) if (!tags.isEmpty()) tags.pop();
				pos++;
			}
			
			if (pos < len && str.charAt(pos) == '&')
			{
				// HTML character entity references, like "&gt;"
				// count this as one single character
				while (pos < len && str.charAt(pos) != ';') pos++;
			}
			
			if (pos < len)
			{
				count++;
				pos++;
			}
		}
		
		// close any unclosed tags
		strTrimmed.setLength(0);
		strTrimmed.append(str.substring(0, pos));
		while (tags.size() > 0)
		{
			strTrimmed.append("</");
			strTrimmed.append(tags.pop());
			strTrimmed.append(">");
		}
		
		boolean didTrim = (count == maxNumOfChars);
		return didTrim;
		
	} // trimFormattedText()
	
	/**
	 *  decode any HTML Numeric Character References
	 * of the style: &#Xhexnumber; or &#decimalnumber;
	 * or of our own special style: ^^Xhexnumber^ or ^^decimalnumber^
	 */
	public static String decodeNumericCharacterReferences(String value)
	{
		// lazily allocate StringBuffer only if needed
		// buf is not null ONLY when a numeric character reference
		// is found - otherwise, buf is not used at all	
		StringBuffer buf = null;
		final int valuelength = value.length();
		for (int i = 0; i < valuelength; i++)
		{
			if (	(value.charAt(i) == '&' || value.charAt(i) == '^') && (i+2 < valuelength)
				&&	(value.charAt(i+1) == '#' || value.charAt(i+1) == '^') )
			{
				int pos = i+2;
				boolean hex = false;
				if ((value.charAt(pos) == 'x') || (value.charAt(pos) == 'X'))
				{
					pos++;
					hex = true;
				}
				StringBuffer num = new StringBuffer(6);
				while (pos < valuelength && value.charAt(pos) != ';' && value.charAt(pos) != '^')
				{
					num.append(value.charAt(pos));
					pos++;
				}
				if (pos < valuelength)
				{
					try
					{
						int val = Integer.parseInt(num.toString(), (hex ? 16 : 10));
						// Found an HTML numeric character reference!
						if (buf == null) 
						{
							buf = new StringBuffer();
							buf.append(value.substring(0, i));
						}
						
						buf.append((char)val);
						i = pos;
					}
					catch (Exception ignore)
					{
						if (buf != null) buf.append(value.charAt(i));
					}
				}
				else
				{
					if (buf != null) buf.append(value.charAt(i));
				}
			}
			else
			{
				if (buf != null) buf.append(value.charAt(i));
			}
		}
		
		if (buf != null) value = buf.toString();
		
		return value;
	}
	
	/*
	public static void main(String[] args)
	throws Exception
	{
		System.out.println("Press enter to begin testing...");
		System.in.read();
		test();
	}

	private static void test()
	{
		PrintStream out = System.out;
		String str = "Hello <font face='verdana; times'><b> There <br />World!</b></font><i><u> &amp; Hello</u> there Mr. &lt;&gt;</i>";
		String ret;
		out.println("Original: "+str);
		for (int i=str.length(); i>=0; i--)
		{
			StringBuffer trimmed = new StringBuffer();
			FormattedText.trimFormattedText(str, i, trimmed);
			out.println("trim(str, "+i+") = \""+trimmed+"\"");
		}
	}
	
	/*		
	public FormattedText()
	{
	}


	private String m_string = "";

	public void setUnescapedPlainText(String plainTextString)
	{
		m_string = escapePlainText(plainTextString);
	}
	
	public void setFormattedText(String formattedString)
	{
		m_string = formattedString;
	}
	
	public String toString()
	{
		return toHTML();
	}
	
	public String toHTML()
	{
		// TODO: Implement
		return m_string;
	}
	
*/	
	
	
	/**
	 * Returns an unescaped plaintext representation.
	 * For example, if the less-than or greater-than symbols are represented in the formatted text,
	 * the unescaped text will contain the less-than and greater than symbols in plain text (not HTML escaped).
	 * @return The unescaped plaintext representation
	 
	public String toUnescapedPlainText()
	{
		return unescapePlainText(m_string);
	}
	

	private static String escapePlainText(String plainText)
	{
		//TODO: Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	

	private static String unescapePlainText(String plainText)
	{
		//TODO: Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	


	private static String getLocalizedMessage(String msgInfo)
	{
		// TODO: Make this do real localization
		return msgInfo;
	}
	
	
	// TODO: Implement this properly!
	// Perhaps use an XML DTD to validate that the user input matches what we allow?
	public static String processFormattedTextAsXml(String strFromBrowser, StringBuffer errorMessage)
	{
		// treat the formatted text as an XML document, and process it
		
		if (strFromBrowser == null) return null;
		
		try
		{
			Document xmlDoc = parseXml(strFromBrowser);
			
			
		}
		catch (Exception e)
		{
			errorMessage.append("The formatted text you entered is invalid: " + e);
		}
		 
		throw new UnsupportedOperationException();

	}
	
	
	private static Document parseXml(String in)
		throws Exception
	{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			//dbf.setValidating(true);
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			InputSource inputSource = new InputSource(new StringReader(in));
			//Class clazz = this.getClass();
			//URL dtdURL = clazz.getResource("&/chef_services.dtd"&);
			//inputSource.setSystemId(dtdURL.toString());
			Document doc = docBuilder.parse(inputSource);
			return doc;		
	}
*/	
	
/** 
 * HTML character entity references.
 * These abreviations are used in HTML to escape certain Unicode characters,
 * including characters used in HTML markup.
 * These character entity references were taken directly from the HTML 4.0 specification at:
 * @link http://www.w3.org/TR/REC-html40/sgml/entities.html
 */	
private static final String[] M_htmlCharacterEntityReferences = {
	"&nbsp;",
	"&iexcl;",
	"&cent;",
	"&pound;",
	"&curren;",
	"&yen;",
	"&brvbar;",
	"&sect;",
	"&uml;",
	"&copy;",
	"&ordf;",
	"&laquo;",
	"&not;",
	"&shy;",
	"&reg;",
	"&macr;",
	"&deg;",
	"&plusmn;",
	"&sup2;",
	"&sup3;",
	"&acute;",
	"&micro;",
	"&para;",
	"&middot;",
	"&cedil;",
	"&sup1;",
	"&ordm;",
	"&raquo;",
	"&frac14;",
	"&frac12;",
	"&frac34;",
	"&iquest;",
	"&Agrave;",
	"&Aacute;",
	"&Acirc;",
	"&Atilde;",
	"&Auml;",
	"&Aring;",
	"&AElig;",
	"&Ccedil;",
	"&Egrave;",
	"&Eacute;",
	"&Ecirc;",
	"&Euml;",
	"&Igrave;",
	"&Iacute;",
	"&Icirc;",
	"&Iuml;",
	"&ETH;",
	"&Ntilde;",
	"&Ograve;",
	"&Oacute;",
	"&Ocirc;",
	"&Otilde;",
	"&Ouml;",
	"&times;",
	"&Oslash;",
	"&Ugrave;",
	"&Uacute;",
	"&Ucirc;",
	"&Uuml;",
	"&Yacute;",
	"&THORN;",
	"&szlig;",
	"&agrave;",
	"&aacute;",
	"&acirc;",
	"&atilde;",
	"&auml;",
	"&aring;",
	"&aelig;",
	"&ccedil;",
	"&egrave;",
	"&eacute;",
	"&ecirc;",
	"&euml;",
	"&igrave;",
	"&iacute;",
	"&icirc;",
	"&iuml;",
	"&eth;",
	"&ntilde;",
	"&ograve;",
	"&oacute;",
	"&ocirc;",
	"&otilde;",
	"&ouml;",
	"&divide;",
	"&oslash;",
	"&ugrave;",
	"&uacute;",
	"&ucirc;",
	"&uuml;",
	"&yacute;",
	"&thorn;",
	"&yuml;",
	"&fnof;",
	"&Alpha;",
	"&Beta;",
	"&Gamma;",
	"&Delta;",
	"&Epsilo;",
	"&Zeta;",
	"&Eta;",
	"&Theta;",
	"&Iota;",
	"&Kappa;",
	"&Lambda;",
	"&Mu;",
	"&Nu;",
	"&Xi;",
	"&Omicro;",
	"&Pi;",
	"&Rho;",
	"&Sigma;",
	"&Tau;",
	"&Upsilo;",
	"&Phi;",
	"&Chi;",
	"&Psi;",
	"&Omega;",
	"&alpha;",
	"&beta;",
	"&gamma;",
	"&delta;",
	"&epsilo;",
	"&zeta;",
	"&eta;",
	"&theta;",
	"&iota;",
	"&kappa;",
	"&lambda;",
	"&mu;",
	"&nu;",
	"&xi;",
	"&omicro;",
	"&pi;",
	"&rho;",
	"&sigmaf;",
	"&sigma;",
	"&tau;",
	"&upsilo;",
	"&phi;",
	"&chi;",
	"&psi;",
	"&omega;",
	"&thetas;",
	"&upsih;",
	"&piv;",
	"&bull;",
	"&hellip;",
	"&prime;",
	"&Prime;",
	"&oline;",
	"&frasl;",
	"&weierp;",
	"&image;",
	"&real;",
	"&trade;",
	"&alefsy;",
	"&larr;",
	"&uarr;",
	"&rarr;",
	"&darr;",
	"&harr;",
	"&crarr;",
	"&lArr;",
	"&uArr;",
	"&rArr;",
	"&dArr;",
	"&hArr;",
	"&forall;",
	"&part;",
	"&exist;",
	"&empty;",
	"&nabla;",
	"&isin;",
	"&notin;",
	"&ni;",
	"&prod;",
	"&sum;",
	"&minus;",
	"&lowast;",
	"&radic;",
	"&prop;",
	"&infin;",
	"&ang;",
	"&and;",
	"&or;",
	"&cap;",
	"&cup;",
	"&int;",
	"&there4;",
	"&sim;",
	"&cong;",
	"&asymp;",
	"&ne;",
	"&equiv;",
	"&le;",
	"&ge;",
	"&sub;",
	"&sup;",
	"&nsub;",
	"&sube;",
	"&supe;",
	"&oplus;",
	"&otimes;",
	"&perp;",
	"&sdot;",
	"&lceil;",
	"&rceil;",
	"&lfloor;",
	"&rfloor;",
	"&lang;",
	"&rang;",
	"&loz;",
	"&spades;",
	"&clubs;",
	"&hearts;",
	"&diams;",
	"&quot;",
	"&amp;",
	"&lt;",
	"&gt;",
	"&OElig;",
	"&oelig;",
	"&Scaron;",
	"&scaron;",
	"&Yuml;",
	"&circ;",
	"&tilde;",
	"&ensp;",
	"&emsp;",
	"&thinsp;",
	"&zwnj;",
	"&zwj;",
	"&lrm;",
	"&rlm;",
	"&ndash;",
	"&mdash;",
	"&lsquo;",
	"&rsquo;",
	"&sbquo;",
	"&ldquo;",
	"&rdquo;",
	"&bdquo;",
	"&dagger;",
	"&Dagger;",
	"&permil;",
	"&lsaquo;",
	"&rsaquo;",
	"&euro;"
};


/** These character entity references were taken directly from the HTML 4.0 specification at:
 * @link http://www.w3.org/TR/REC-html40/sgml/entities.html
 */
private static final char[] M_htmlCharacterEntityReferencesUnicode = 
{
	160,
	161,
	162,
	163,
	164,
	165,
	166,
	167,
	168,
	169,
	170,
	171,
	172,
	173,
	174,
	175,
	176,
	177,
	178,
	179,
	180,
	181,
	182,
	183,
	184,
	185,
	186,
	187,
	188,
	189,
	190,
	191,
	192,
	193,
	194,
	195,
	196,
	197,
	198,
	199,
	200,
	201,
	202,
	203,
	204,
	205,
	206,
	207,
	208,
	209,
	210,
	211,
	212,
	213,
	214,
	215,
	216,
	217,
	218,
	219,
	220,
	221,
	222,
	223,
	224,
	225,
	226,
	227,
	228,
	229,
	230,
	231,
	232,
	233,
	234,
	235,
	236,
	237,
	238,
	239,
	240,
	241,
	242,
	243,
	244,
	245,
	246,
	247,
	248,
	249,
	250,
	251,
	252,
	253,
	254,
	255,
	402,
	913,
	914,
	915,
	916,
	917,
	918,
	919,
	920,
	921,
	922,
	923,
	924,
	925,
	926,
	927,
	928,
	929,
	931,
	932,
	933,
	934,
	935,
	936,
	937,
	945,
	946,
	947,
	948,
	949,
	950,
	951,
	952,
	953,
	954,
	955,
	956,
	957,
	958,
	959,
	960,
	961,
	962,
	963,
	964,
	965,
	966,
	967,
	968,
	969,
	977,
	978,
	982,
	8226,
	8230,
	8242,
	8243,
	8254,
	8260,
	8472,
	8465,
	8476,
	8482,
	8501,
	8592,
	8593,
	8594,
	8595,
	8596,
	8629,
	8656,
	8657,
	8658,
	8659,
	8660,
	8704,
	8706,
	8707,
	8709,
	8711,
	8712,
	8713,
	8715,
	8719,
	8721,
	8722,
	8727,
	8730,
	8733,
	8734,
	8736,
	8743,
	8744,
	8745,
	8746,
	8747,
	8756,
	8764,
	8773,
	8776,
	8800,
	8801,
	8804,
	8805,
	8834,
	8835,
	8836,
	8838,
	8839,
	8853,
	8855,
	8869,
	8901,
	8968,
	8969,
	8970,
	8971,
	9001,
	9002,
	9674,
	9824,
	9827,
	9829,
	9830,
	34,
	38,
	60,
	62,
	338,
	339,
	352,
	353,
	376,
	710,
	732,
	8194,
	8195,
	8201,
	8204,
	8205,
	8206,
	8207,
	8211,
	8212,
	8216,
	8217,
	8218,
	8220,
	8221,
	8222,
	8224,
	8225,
	8240,
	8249,
	8250,
	8364
};


}	// class FormattedText

/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/FormattedText.java,v 1.19 2004/09/29 00:19:33 janderse.umich.edu Exp $
*
**********************************************************************************/
