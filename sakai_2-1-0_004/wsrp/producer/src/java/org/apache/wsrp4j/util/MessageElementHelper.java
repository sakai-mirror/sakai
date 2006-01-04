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

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.message.MessageElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;

/**
 * @author Julie MacNaught jmacna@us.ibm.com
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MessageElementHelper implements javax.xml.rpc.encoding.SerializationContext
{
    StringWriter writer = null;
    SerializationContext serializer = null;

    public static MessageElement create(Node value)
    {
        MessageElement result = null;
        try
        {
            if (Class.forName("org.w3c.dom.Element").isAssignableFrom(value.getClass()))
            {
                result = new MessageElement((Element)value);
                return result;
            } else
            {
                throw new Exception("Trying to create a MessageElement with something other than Element");
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    public String getAsString(MessageElement msg)
    {
        try
        {
            writer = new StringWriter();
            serializer = new SerializationContext(writer);
            serializer.setSendDecl(false);
            msg.output(serializer);
            writer.flush();
            return writer.toString();
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public MessageElement clone(MessageElement msg)
    {
        try
        {
            String oldValue = getAsString(msg);
            Document doc =
                DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new InputSource(new StringReader(oldValue)));
            Element root = doc.getDocumentElement();
            MessageElement newMsg = create(root);
            return newMsg;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#serialize(QName, Attributes, Object)
     */
    public void serialize(QName elemQName, Attributes attributes, Object value) throws IOException
    {
        if (serializer != null)
        {
            serializer.serialize(elemQName, attributes, value);
        } else
        {
            throw new IOException("Out of sequence error -- no serializer");
        }
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#serialize(QName, Attributes, Object, QName, boolean, Boolean)
     */
    public void serialize(
        QName elemQName,
        Attributes attributes,
        Object value,
        QName xmlType,
        boolean sendNull,
        Boolean sendType)
        throws IOException
    {
        if (serializer != null)
        {
            serializer.serialize(elemQName, attributes, value, xmlType, sendNull, sendType);
        } else
        {
            throw new IOException("Out of sequence error -- no serializer");
        }
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#setTypeAttribute(Attributes, QName)
     */
    public Attributes setTypeAttribute(Attributes attributes, QName type)
    {
        if (serializer != null)
        {
            return serializer.setTypeAttribute(attributes, type);
        } else
        {
            return null;
        }
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#getSerializerForJavaType(Class)
     */
    public Serializer getSerializerForJavaType(Class javaType)
    {
        if (serializer != null)
        {
            return serializer.getSerializerForJavaType(javaType);
        } else
        {
            return null;
        }
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#getPretty()
     */
    public boolean getPretty()
    {
        if (serializer != null)
        {
            return serializer.getPretty();
        } else
        {

            return false;
        }
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#setPretty(boolean)
     */
    public void setPretty(boolean pretty)
    {
        if (serializer != null)
        {
            serializer.setPretty(pretty);
        }

    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#setDoMultiRefs(boolean)
     */
    public void setDoMultiRefs(boolean shouldDo)
    {
        if (serializer != null)
        {
            serializer.setDoMultiRefs(shouldDo);
        }
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#setSendDecl(boolean)
     */
    public void setSendDecl(boolean sendDecl)
    {
        if (serializer != null)
        {
            serializer.setSendDecl(sendDecl);
        }
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#shouldSendXSIType()
     */
    public boolean shouldSendXSIType()
    {
        if (serializer != null)
        {
            return serializer.shouldSendXSIType();
        }

        return false;
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#getTypeMapping()
     */
    public TypeMapping getTypeMapping()
    {
        if (serializer != null)
        {
            return serializer.getTypeMapping();
        }
        return null;
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#getTypeMappingRegistry()
     */
    public TypeMappingRegistry getTypeMappingRegistry()
    {
        if (serializer != null)
        {
            return serializer.getTypeMappingRegistry();
        }
        return null;
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#getPrefixForURI(String)
     */
    public String getPrefixForURI(String uri)
    {

        if (serializer != null)
        {
            return serializer.getPrefixForURI(uri);
        }
        return null;
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#getPrefixForURI(String, String)
     */
    public String getPrefixForURI(String uri, String defaultPrefix)
    {

        if (serializer != null)
        {
            return serializer.getPrefixForURI(uri, defaultPrefix);
        }
        return null;
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#registerPrefixForURI(String, String)
     */
    public void registerPrefixForURI(String prefix, String uri)
    {
        if (serializer != null)
        {
            serializer.registerPrefixForURI(prefix, uri);
        }
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#getCurrentMessage()
     */
    public Message getCurrentMessage()
    {
        if (serializer != null)
        {
            return serializer.getCurrentMessage();
        }

        return null;
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#getMessageContext()
     */
    public MessageContext getMessageContext()
    {
        if (serializer != null)
        {
            return serializer.getMessageContext();
        }

        return null;
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#qName2String(QName)
     */
    public String qName2String(QName qName)
    {
        if (serializer != null)
        {
            return serializer.qName2String(qName);
        }

        return null;
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#attributeQName2String(QName)
     */
    public String attributeQName2String(QName qName)
    {
        if (serializer != null)
        {
            return serializer.attributeQName2String(qName);
        }
        return null;
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#getQNameForClass(Class)
     */
    public QName getQNameForClass(Class cls)
    {
        if (serializer != null)
        {
            return serializer.getQNameForClass(cls);
        }
        return null;
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#isPrimitive(Object)
     */
    public boolean isPrimitive(Object value)
    {
        if (serializer != null)
        {
            return serializer.isPrimitive(value);
        }
        return false;
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#outputMultiRefs()
     */
    public void outputMultiRefs() throws IOException
    {
        if (serializer != null)
        {
            serializer.outputMultiRefs();
        } else
        {
            throw new IOException("Out of sequence error -- no serializer");
        }

    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#startElement(QName, Attributes)
     */
    public void startElement(QName qName, Attributes attributes) throws IOException
    {
        if (serializer != null)
        {
            serializer.startElement(qName, attributes);
        } else
        {
            throw new IOException("Out of sequence error -- no serializer");
        }

    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#endElement()
     */
    public void endElement() throws IOException
    {
        if (serializer != null)
        {
            serializer.endElement();
        } else
        {
            throw new IOException("Out of sequence error -- no serializer");
        }

    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#writeChars(char[], int, int)
     */
    public void writeChars(char[] p1, int p2, int p3) throws IOException
    {
        if (serializer != null)
        {
            serializer.writeChars(p1, p2, p3);
        } else
        {
            throw new IOException("Out of sequence error -- no serializer");
        }

    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#writeString(String)
     */
    public void writeString(String string) throws IOException
    {
        if (serializer != null)
        {
            serializer.writeString(string);
        } else
        {
            throw new IOException("Out of sequence error -- no serializer");
        }

    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#writeSafeString(String)
     */
    public void writeSafeString(String string) throws IOException
    {
        if (serializer != null)
        {
            serializer.writeSafeString(string);
        } else
        {
            throw new IOException("Out of sequence error -- no serializer");
        }
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#writeDOMElement(Element)
     */
    public void writeDOMElement(Element el) throws IOException
    {
        if (serializer != null)
        {
            serializer.writeDOMElement(el);
        } else
        {
            throw new IOException("Out of sequence error -- no serializer");
        }
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#getValueAsString(Object, QName)
     */
    public String getValueAsString(Object value, QName xmlType) throws IOException
    {
        if (serializer != null)
        {
            serializer.getValueAsString(value, xmlType);
        } else
        {
            throw new IOException("Out of sequence error -- no serializer");
        }
        return null;
    }

    /**
     * @see org.apache.axis.encoding.SerializationContext#getCurrentXMLType()
     */
    public QName getCurrentXMLType()
    {
        if (serializer != null)
        {
            return serializer.getCurrentXMLType();
        }
        return null;

    }

	/* (non-Javadoc)
	 * @see org.apache.axis.encoding.SerializationContext#getDoMultiRefs()
	 */
	public boolean getDoMultiRefs() {
		if (serializer != null) {
			return serializer.getDoMultiRefs();
		}
		
		return false;
	}

}
