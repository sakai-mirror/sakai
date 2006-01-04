/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* 

 */


package org.apache.wsrp4j.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.util.BitSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class DOMSupport
{
    public static final short OUTPUT_DOM_TREE_IGNORE_TEXT_NODES = 1;
    public static final short OUTPUT_DOM_TREE_OUTPUT_TEXT_NODES_HIDE_CONTENT = 2;
    public static final short OUTPUT_DOM_TREE_OUTPUT_TEXT_NODES_FULLY = 3;

    public static final short OUTPUT_DOM_TREE_IGNORE_COMMENT_NODES = 1;
    public static final short OUTPUT_DOM_TREE_OUTPUT_COMMENT_NODES_HIDE_CONTENT = 2;
    public static final short OUTPUT_DOM_TREE_OUTPUT_COMMENT_NODES_FULLY = 3;

    private static final String XML_OUTPUT_INDENTATION_SPACE = "   ";
    private static final int MAX_PRINTABLE_TREE_DEPTH = 256;
    public static void dumpDOMNode(
        PrintWriter outputTarget,
        Node node,
        short textNodeOutputOption,
        short commentNodeOutputOption,
        boolean terseOutput,
        boolean displayHashcodes,
        int depth,
        BitSet siblingsAtDepth)
    {
        if ((node == null) || (depth > MAX_PRINTABLE_TREE_DEPTH))
        {
            return;
        }

        short nodeType = node.getNodeType();

        if (((nodeType == Node.TEXT_NODE) && (textNodeOutputOption == OUTPUT_DOM_TREE_IGNORE_TEXT_NODES))
            || ((nodeType == Node.COMMENT_NODE) && (commentNodeOutputOption == OUTPUT_DOM_TREE_IGNORE_COMMENT_NODES)))
        {
            return;
        }

        for (int currentDepth = 0; currentDepth < depth; currentDepth++)
        {
            if (siblingsAtDepth.get(currentDepth) == true)
            {
                outputTarget.print("|  ");
            } else
            {
                outputTarget.print("   ");
            }
        }

        outputTarget.print("+--");

        if (displayHashcodes == true)
        {
            outputTarget.print("(" + node.getClass().getName() + "@" + Integer.toHexString(node.hashCode()) + ") ");
        }

        switch (nodeType)
        {
            case Node.ELEMENT_NODE :
                outputTarget.print("ELEMENT: ");
                break;

            case Node.ATTRIBUTE_NODE :
                outputTarget.print("ATTRIBUTE: ");
                break;

            case Node.TEXT_NODE :
                outputTarget.print("TEXT: ");
                break;

            case Node.CDATA_SECTION_NODE :
                outputTarget.print("CDATA_SECTION: ");
                break;

            case Node.ENTITY_REFERENCE_NODE :
                outputTarget.print("ENTITY_REFERENCE: ");
                break;

            case Node.ENTITY_NODE :
                outputTarget.print("ENTITY: ");
                break;

            case Node.PROCESSING_INSTRUCTION_NODE :
                outputTarget.print("PROCESSING_INSTRUCTION: ");
                break;

            case Node.COMMENT_NODE :
                outputTarget.print("COMMENT: ");
                break;

            case Node.DOCUMENT_NODE :
                outputTarget.print("DOCUMENT: ");
                break;

            case Node.DOCUMENT_TYPE_NODE :
                outputTarget.print("DOCUMENT_TYPE: ");
                break;

            case Node.DOCUMENT_FRAGMENT_NODE :
                outputTarget.print("DOCUMENT_FRAGMENT: ");
                break;

            case Node.NOTATION_NODE :
                outputTarget.print("NOTATION: ");
                break;
        }

        if ((terseOutput == false)
            || ((nodeType != Node.CDATA_SECTION_NODE)
                || (nodeType != Node.COMMENT_NODE)
                || (nodeType != Node.DOCUMENT_FRAGMENT_NODE)
                || (nodeType != Node.DOCUMENT_NODE)
                || (nodeType != Node.TEXT_NODE)))
        {
            outputTarget.print("name=");

            String nodeName = node.getNodeName();

            if (nodeName != null)
            {
                outputTarget.print("\"" + nodeName + "\"");
            } else
            {
                outputTarget.print("null");
            }
        }

        String nodeValue = node.getNodeValue();

        if (nodeValue != null)
        {
            outputTarget.print(", value=");

            if (((nodeType == Node.TEXT_NODE)
                && (textNodeOutputOption == OUTPUT_DOM_TREE_OUTPUT_TEXT_NODES_HIDE_CONTENT))
                || ((nodeType == Node.COMMENT_NODE)
                    && (commentNodeOutputOption == OUTPUT_DOM_TREE_OUTPUT_COMMENT_NODES_HIDE_CONTENT)))
            {
                outputTarget.println("(not displayed)");
            } else
            {
                outputTarget.print("\"");

                for (int nodeValueIndex = 0; nodeValueIndex < nodeValue.length(); nodeValueIndex++)
                {
                    char ch = nodeValue.charAt(nodeValueIndex);

                    switch (ch)
                    {
                        case '\t' :
                            outputTarget.print("\\t");
                            break;

                        case '\n' :
                            outputTarget.print("\\n");
                            break;

                        case '\f' :
                            outputTarget.print("\\f");
                            break;

                        case '\r' :
                            outputTarget.print("\\r");
                            break;

                        default :
                            outputTarget.print(ch);
                    }
                }

                outputTarget.println("\"");
            }
        } else
        {
            if (terseOutput == false)
            {
                outputTarget.print(", value=null");
            }

            outputTarget.println();
        }

        if (node.hasChildNodes())
        {
            siblingsAtDepth.set(depth + 1);
        }

        NamedNodeMap nnm = node.getAttributes();

        if (nnm != null)
        {
            for (int attributeIndex = 0; attributeIndex < nnm.getLength(); attributeIndex++)
            {
                Node attributeNode = nnm.item(attributeIndex);

                for (int currentDepth = 0; currentDepth <= (depth + 1); currentDepth++)
                {
                    if (siblingsAtDepth.get(currentDepth) == true)
                    {
                        outputTarget.print("|  ");
                    } else
                    {
                        outputTarget.print("   ");
                    }
                }

                outputTarget.println(
                    "   (Attribute: name=\""
                        + attributeNode.getNodeName()
                        + "\", value=\""
                        + attributeNode.getNodeValue()
                        + "\")");
            }
        }

        Node child = node.getFirstChild();

        while (child != null)
        {
            Node nextChild = child.getNextSibling();
            short nextChildNodeType;

            while (nextChild != null)
            {
                nextChildNodeType = nextChild.getNodeType();

                if (((nextChildNodeType == Node.TEXT_NODE)
                    && (textNodeOutputOption == OUTPUT_DOM_TREE_IGNORE_TEXT_NODES))
                    || ((nextChildNodeType == Node.COMMENT_NODE)
                        && (commentNodeOutputOption == OUTPUT_DOM_TREE_IGNORE_COMMENT_NODES)))
                {
                    nextChild = nextChild.getNextSibling();
                } else
                {
                    break;
                }
            }

            if (nextChild == null)
            {
                siblingsAtDepth.clear(depth + 1);
            }

            dumpDOMNode(
                outputTarget,
                child,
                textNodeOutputOption,
                commentNodeOutputOption,
                terseOutput,
                displayHashcodes,
                depth + 1,
                siblingsAtDepth);

            child = nextChild;
        }
    }
    public static void dumpDOMTree(
        PrintWriter outputTarget,
        Node rootNode,
        short textNodeOutputOption,
        short commentNodeOutputOption,
        boolean terseOutput,
        boolean displayHashcodes)
    {
        dumpDOMNode(
            outputTarget,
            rootNode,
            textNodeOutputOption,
            commentNodeOutputOption,
            terseOutput,
            displayHashcodes,
            0,
            new BitSet(MAX_PRINTABLE_TREE_DEPTH));
    }
    public static void exportDOMNode(Writer outputTarget, Node node)
    {
        exportDOMNode(outputTarget, node, -1);
    }

    public static void exportDOMNode(Writer outputTarget, Node node, int indentLevel)
    {
        if (node == null)
        {
            return;
        }

        String nodeName = null;
        NamedNodeMap nnm = null;
        int attributeIndex;
        int numberAttributes;
        Node attributeNode = null;
        Node child = null;
        String piData = null;
        String commentData = null;
        String endLine = (indentLevel == -1 ? "" : "\n");
        String indentS = "";

        for (int i = indentLevel; i > 0; i--)
        {
            indentS += "  ";
        }

        switch (node.getNodeType())
        {
            case Node.DOCUMENT_NODE :
                try
                {
                    outputTarget.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + endLine);
                } catch (IOException e)
                {
                }
                break;

            case Node.DOCUMENT_TYPE_NODE :
                try
                {
                    outputTarget.write("<!DOCTYPE " + node.getNodeName() + ">" + endLine);
                } catch (IOException e)
                {
                }
                break;

            case Node.ELEMENT_NODE :
                nodeName = node.getNodeName();

                try
                {
                    outputTarget.write(indentS + "<" + nodeName);
                } catch (IOException e)
                {
                }

                nnm = node.getAttributes();

                if (nnm != null)
                {
                    numberAttributes = nnm.getLength();
                    for (attributeIndex = 0; attributeIndex < numberAttributes; attributeIndex++)
                    {
                        attributeNode = nnm.item(attributeIndex);

                        try
                        {
                            outputTarget.write(
                                " " + attributeNode.getNodeName() + "=\"" + attributeNode.getNodeValue() + "\"");
                        } catch (IOException e)
                        {
                        }
                    }
                }

                if (node.hasChildNodes())
                {
                    try
                    {
                        outputTarget.write(">" + endLine);
                    } catch (IOException e)
                    {
                    }

                    child = node.getFirstChild();

                    while (child != null)
                    {
                        exportDOMNode(outputTarget, child, (indentLevel == -1 ? -1 : 1 + indentLevel));
                        child = child.getNextSibling();
                    }

                    try
                    {
                        outputTarget.write(indentS + "</" + nodeName + ">" + endLine);
                    } catch (IOException e)
                    {
                    }
                } else
                {
                    try
                    {
                        outputTarget.write("/>" + endLine);
                    } catch (IOException e)
                    {
                    }
                }
                break;

            case Node.ENTITY_REFERENCE_NODE :
                try
                {
                    outputTarget.write("&" + node.getNodeName() + ";");
                } catch (IOException e)
                {
                }
                break;

            case Node.CDATA_SECTION_NODE :
                try
                {
                    outputTarget.write(indentS + "<![CDATA[" + node.getNodeValue() + "]]>" + endLine);
                } catch (IOException e)
                {
                }
                break;

            case Node.TEXT_NODE :
                try
                {
                    outputTarget.write(normalize(node.getNodeValue()));
                } catch (IOException e)
                {
                }
                break;

            case Node.PROCESSING_INSTRUCTION_NODE :
                try
                {
                    outputTarget.write(indentS + "<?" + node.getNodeName());
                } catch (IOException e)
                {
                }

                piData = node.getNodeValue();

                if ((piData != null) && (piData.length() > 0))
                {
                    try
                    {
                        outputTarget.write(" " + piData);
                    } catch (IOException e)
                    {
                    }
                }

                try
                {
                    outputTarget.write("?>" + endLine);
                } catch (IOException e)
                {
                }
                break;

            case Node.DOCUMENT_FRAGMENT_NODE :
                exportDOMNode(outputTarget, node.getFirstChild());
                break;

            case Node.COMMENT_NODE :
                try
                {
                    outputTarget.write(indentS + "<!--");
                } catch (IOException e)
                {
                }

                commentData = node.getNodeValue();

                if ((commentData != null) && (commentData.length() > 0))
                {
                    try
                    {
                        outputTarget.write(" " + commentData);
                    } catch (IOException e)
                    {
                    }
                }
                try
                {
                    outputTarget.write(" -->" + endLine);
                } catch (IOException e)
                {
                }
                break;

        }
    }
    public static void exportDOMTree(Writer outputTarget, Node rootNode)
    {
        exportDOMNode(outputTarget, rootNode);
    }
    /**
     * Insert the method's description here.
     * Creation date: (4/18/00 8:04:58 PM)
     * @return org.w3c.dom.Element
     * @param url java.net.URL
     */
    public static Element fetchURL(URL url) throws IOException
    {
        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        DocumentBuilder docBuilder;
        try
        {
            docBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e)
        {
            throw (new IOException(e.getMessage()));
        }

        BufferedReader in = null;
        InputStream file = url.openStream();
        in = new BufferedReader(new InputStreamReader(file));

        // 4/27 PJD - added the following preprocessing to remove ignorable
        // whitespace in the file before it gets parsed by the XML parser.
        // This should be considered a work-around that needs to be replaced with
        // something better later.
        StringBuffer docBuff = new StringBuffer();
        String currentLine = in.readLine();
        while (currentLine != null)
        {
            docBuff.append(currentLine.trim());
            currentLine = in.readLine();
        }
        in.close();

        in = new BufferedReader(new StringReader(docBuff.toString()));
        Document doc = null;
        try
        {
            doc = docBuilder.parse(new InputSource(in));
        } catch (SAXException e)
        {
            throw (new IOException(e.getMessage()));
        }
        if (doc != null)
        {
            return doc.getDocumentElement();
        } else
        {
            return null;
        }
    }
    public static Document getOwnerDocument(Node node)
    {
        Document doc = node.getOwnerDocument();
        if (doc == null)
        {
            doc = (Document)node;
        }
        return doc;
    }
    /** Normalizes the given string. */
    private static String normalize(String s)
    {
        StringBuffer str = new StringBuffer();
        int len = (s != null) ? s.length() : 0;
        for (int i = 0; i < len; i++)
        {
            char ch = s.charAt(i);
            switch (ch)
            {

                case 0x2d :
                    str.append("&#x2d;");
                    break;

                case 0xc4 :
                    str.append("&#xe4;");
                    break;

                case 0xe0 :
                    str.append("&#xE0;");
                    break;
                case 0xe1 :
                    str.append("&#xE1;");
                    break;
                case 0xe2 :
                    str.append("&#xE2;");
                    break;
                case 0xe3 :
                    str.append("&#xE3;");
                    break;
                case 0xe4 :
                    str.append("&#xE4;");
                    break;
                case 0xe5 :
                    str.append("&#xE5;");
                    break;
                case 0xe6 :
                    str.append("&#xE6;");
                    break;
                case 0xe7 :
                    str.append("&#xE7;");
                    break;
                case 0xe8 :
                    str.append("&#xE8;");
                    break;
                case 0xe9 :
                    str.append("&#xE9;");
                    break;
                case 0xed :
                    str.append("&#xED;");
                    break;
                case 0xef :
                    str.append("&#xEF;");
                    break;

                case 0xf0 :
                    str.append("&#xF0;");
                    break;
                case 0xf1 :
                    str.append("&#xF1;");
                    break;
                case 0xf2 :
                    str.append("&#xF2;");
                    break;
                case 0xf3 :
                    str.append("&#xF3;");
                    break;
                case 0xf4 :
                    str.append("&#xF4;");
                    break;
                case 0xfC :
                    str.append("&#xFC;");
                    break;
                case 0xfF :
                    str.append("&#xFF;");
                    break;

                case '<' :
                    {
                        str.append("&lt;");
                        break;
                    }
                case '>' :
                    {
                        str.append("&gt;");
                        break;
                    }
                case '&' :
                    {
                        str.append("&amp;");
                        break;
                    }
                case '"' :
                    {
                        str.append("&quot;");
                        break;
                    }
                case '\r' :
                case '\n' :
                    {
                        //   if ( canonical ) {
                        //      str.append("&#");
                        //      str.append(Integer.toString(ch));
                        //      str.append(';');
                        //      break;
                        //   }
                        // else, default append char
                    }
                default :
                    {
                        str.append(ch);
                    }
            }
        }

        return (str.toString());

    } // normalize(String):String
    public static String translateDOMException(DOMException e)
    {
        String translation = null;

        switch (e.code)
        {
            case DOMException.INDEX_SIZE_ERR :
                translation = "Index size error";
                break;

            case DOMException.DOMSTRING_SIZE_ERR :
                translation = "String size error";
                break;

            case DOMException.HIERARCHY_REQUEST_ERR :
                translation = "Hierarchy request error";
                break;

            case DOMException.WRONG_DOCUMENT_ERR :
                translation = "Wrong document";
                break;

            case DOMException.INVALID_CHARACTER_ERR :
                translation = "Invalid character";
                break;

            case DOMException.NO_DATA_ALLOWED_ERR :
                translation = "No data allowed";
                break;

            case DOMException.NO_MODIFICATION_ALLOWED_ERR :
                translation = "No modification allowed";
                break;

            case DOMException.NOT_FOUND_ERR :
                translation = "Not found error";
                break;

            case DOMException.NOT_SUPPORTED_ERR :
                translation = "Not supported";
                break;

            case DOMException.INUSE_ATTRIBUTE_ERR :
                translation = "Attribute in-use";
                break;

            default :
                translation = "UNKNOWN DOM EXCEPTION";
        }

        return translation;
    }
}
