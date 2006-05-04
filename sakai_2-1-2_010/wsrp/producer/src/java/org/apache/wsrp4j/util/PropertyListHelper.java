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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import oasis.names.tc.wsrp.v1.types.ModelDescription;
import oasis.names.tc.wsrp.v1.types.Property;
import oasis.names.tc.wsrp.v1.types.PropertyDescription;
import oasis.names.tc.wsrp.v1.types.PropertyList;
import oasis.names.tc.wsrp.v1.types.ResetProperty;

import org.apache.axis.message.MessageElement;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 *
 * @author Julie MacNaught jmacna@us.ibm.com
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class PropertyListHelper
{
    DocumentBuilderFactory factory = null;
    DocumentBuilder docBuilder = null;
    Document doc = null;

    public PropertyListHelper()
    {
        try
        {
            factory = new DocumentBuilderFactoryImpl();
            docBuilder = factory.newDocumentBuilder();
            doc = docBuilder.newDocument();
        } catch (Exception ex)
        {
            ex.printStackTrace();

        }

    };

    public PropertyList makeSample()
    {
        try
        {

            //property list in PropertyList format
            Property foo = new Property();
            Property bar = new Property();
            Property zot = new Property();
            foo.setName("foo");
            bar.setName("bar");
            zot.setName("zot");

            // make a foo property by constructing an Element
            Element fooElement = doc.createElement("foo");
            Text fooText = doc.createTextNode("This some content for foo");
            fooElement.appendChild(fooText);
            MessageElement fooMsg = MessageElementHelper.create(fooElement);
            MessageElement[] fooMsgs = new MessageElement[] { fooMsg };
            foo.set_any(fooMsgs);

            //make a bar property by constructing Text
            Element barElement = doc.createElement("bar");
            Text barText = doc.createTextNode("This some content for bar");
            barElement.appendChild(barText);
            MessageElement barMsg = MessageElementHelper.create(barElement);
            MessageElement[] barMsgs = new MessageElement[] { barMsg };
            bar.set_any(barMsgs);

            //make a zot property by setting object value

            Element zotElement = doc.createElement("zotBlock");
            zotElement.setAttribute("name", "zot");
            Text zotText = doc.createTextNode("This some content for zot");
            zotElement.appendChild(zotText);
            MessageElement zotMsg = MessageElementHelper.create(zotElement);
            //zotMsg.setObjectValue(zotElement);
            MessageElement[] zotMsgs = new MessageElement[] { zotMsg };
            zot.set_any(zotMsgs);

            Property[] propertyArray = new Property[] { foo, bar, zot };
            PropertyList propertyList = new PropertyList();
            propertyList.setProperties(propertyArray);
            propertyList.setResetProperties(new ResetProperty[0]);
            return propertyList;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    public void writePropertyAsString(StringWriter sw, Property prop)
    {
        // get array of message elements
        if (prop != null)
        {
            sw.write("<Property name=\"" + prop.getName() + "\">");
            MessageElement[] msgs = prop.get_any();
            if (msgs != null)
            {
                for (int jj = 0; jj < msgs.length; jj++)
                {
                    MessageElementHelper helper = new MessageElementHelper();
                    sw.write(helper.getAsString(msgs[jj]));

                }
            }
            sw.write("</Property>\n");
        }
    }
    public String convertToXMLString(PropertyList plist)
    {

        if (plist != null)
        {
            Property[] props = plist.getProperties();
            if (props != null)
            {
                StringWriter sw = new StringWriter();
                sw.write("<PropertyList>");
                for (int ii = 0; ii < props.length; ii++)
                {
                    writePropertyAsString(sw, props[ii]);
                }
                sw.write("</PropertyList>");
                sw.flush();
                return sw.toString();
            }
        }
        return null;
    }
    public String convertToXMLString(ModelDescription md)
    {
        StringWriter sw = new StringWriter();
        PropertyDescription[] props = md.getPropertyDescriptions();

        sw.write("<ModelDescription>");
        for (int ii = 0; ii < props.length; ii++)
        {
            writePropertyDescriptionAsString(sw, props[ii]);
        }
        sw.write("</ModelDescription>");
        sw.flush();
        return sw.toString();
    }
    private String quote(String ss)
    {
        return new String("\"" + ss + "\"");
    }

    /**
     * Method writePropertyDescriptionAsString.
     * @param sw
     * @param prop
     */
    private void writePropertyDescriptionAsString(StringWriter sw, PropertyDescription prop)
    {

        /* TODO
        sw.write(
            "<propertDescriptions"
                + " name="
                + quote(prop.getName())
                + " type="
                + quote(prop.getType().toString())
                + ">");
        LocalizedString label = prop.getLabel();
        sw.write(
            "<label"
                + " resourceName="
                + quote(label.getResourceName())
                + " lang="
                + quote(label.getLang())
                + " value="
                + quote(label.getValue())
                + "</label>");
        LocalizedString hint = prop.getHint();
        sw.write(
            "<hint"
                + " resourceName="
                + quote(hint.getResourceName())
                + " lang="
                + quote(hint.getLang())
                + " value="
                + quote(hint.getValue())
                + "</hint>");
        sw.write("</propertyDescriptions>\n");
        */

    }
    public String getValue(PropertyList plist, String propName)
    {
        Property[] props = plist.getProperties();
        StringWriter sw = new StringWriter();
        try
        {
            for (int ii = 0; ii < props.length; ii++)
            {
                if (props[ii].getName().equals(propName))
                {

                    writePropertyAsString(sw, props[ii]);
                    sw.flush();
                    sw.close();
                    return sw.toString();

                }

            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
    public String getValueOfChild(PropertyList plist, String propName)
    {
        if (plist != null)
        {
            Property[] props = plist.getProperties();
            if (props != null)
            {
                StringWriter sw = new StringWriter();
                try
                {
                    for (int ii = 0; ii < props.length; ii++)
                    {
                        if (props[ii].getName().equals(propName))
                        {
                            MessageElement[] msgs = props[ii].get_any();
                            if (msgs != null)
                            {
                                for (int jj = 0; jj < msgs.length; jj++)
                                {
                                    MessageElementHelper helper = new MessageElementHelper();
                                    sw.write(helper.getAsString(msgs[jj]));

                                }
                            }

                            sw.flush();
                            sw.close();
                            Document doc = docBuilder.parse(new InputSource(new StringReader(sw.toString())));
                            Element element = doc.getDocumentElement();
                            Node node = element.getFirstChild();
                            if (node != null)
                            {
                                return node.getNodeValue();
                            } else
                            {
                                return null;
                            }

                            //return sw.toString();

                        }

                    }
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
    public Property updateProperty(Property prop, String xmlStringValue) throws InvalidXMLStringException
    {
        //String temp = new String("<" + name + ">" + xmlStringValue + "</" + name + ">");

        Document doc = docBuilder.newDocument();
        Element element = doc.createElement(prop.getName());
        Node node = doc.createTextNode(xmlStringValue);
        element.appendChild(node);

        /*try {
            InputSource is = new InputSource(new StringReader(xmlStringValue));
            Document doc = docBuilder.parse(is);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InvalidXMLStringException("Cannot parse property value");
        }*/
        MessageElement msg = MessageElementHelper.create(element);
        MessageElement[] msgs = new MessageElement[1];
        msgs[0] = msg;
        prop.set_any(msgs);
        return prop;

    }
}