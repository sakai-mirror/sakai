/**********************************************************************************
 * $URL$
 * $Id$
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
package org.sakaiproject.component.app.help;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.app.help.RestConfiguration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import sun.misc.BASE64Encoder;

/**
 * @author <a href="mailto:jlannan.iupui.edu">Jarrod Lannan</a>
 * @version $Id$
 * 
 */
public class RestConfigurationImpl implements RestConfiguration
{

  /** user:pass as string ... will be converted to Base64 **/
  private String restCredentials;

  private String organization;
  private String restDomain;
  private String restUrl;
  private long cacheInterval;

  private static String REST_DOMAIN_URL;
  private static String REST_CORPUS_URL;

  private final static Log LOG = LogFactory.getLog(RestConfigurationImpl.class);

  /**
   * @see org.sakaiproject.api.app.help.RestConfiguration#getOrganization()
   */
  public String getOrganization()
  {
    return organization;
  }

  /**
   * @see org.sakaiproject.api.app.help.RestConfiguration#setOrganization(java.lang.String)
   */
  public void setOrganization(String organization)
  {
    this.organization = organization;
  }

  /**
   * @see org.sakaiproject.api.app.help.RestConfiguration#getRestCredentials()
   */
  public String getRestCredentials()
  {
    return restCredentials;
  }

  /**
   * @see org.sakaiproject.api.app.help.RestConfiguration#setRestCredentials(java.lang.String)
   */
  public void setRestCredentials(String restCredentials)
  {
    this.restCredentials = restCredentials;
  }

  /**
   * @see org.sakaiproject.api.app.help.RestConfiguration#getRestDomain()
   */
  public String getRestDomain()
  {
    return restDomain;
  }

  /**
   * @see org.sakaiproject.api.app.help.RestConfiguration#setRestDomain(java.lang.String)
   */
  public void setRestDomain(String restDomain)
  {
    this.restDomain = restDomain;
  }

  /**
   * @see org.sakaiproject.api.app.help.RestConfiguration#getRestUrl()
   */
  public String getRestUrl()
  {
    return restUrl;
  }

  /**
   * @see org.sakaiproject.api.app.help.RestConfiguration#setRestUrl(java.lang.String)
   */
  public void setRestUrl(String restUrl)
  {
    this.restUrl = restUrl;
  }

  /**
   * @see org.sakaiproject.api.app.help.RestConfiguration#getCacheInterval()
   */
  public long getCacheInterval()
  {
    return cacheInterval;
  }

  /**
   * @see org.sakaiproject.api.app.help.RestConfiguration#setCacheInterval(long)
   */
  public void setCacheInterval(long cacheInterval)
  {
    this.cacheInterval = cacheInterval;
  }

  /**
   * @see org.sakaiproject.api.app.help.RestConfiguration#getRestUrlInDomain()
   */
  public String getRestUrlInDomain()
  {
    if (REST_DOMAIN_URL != null)
    {
      return REST_DOMAIN_URL;
    }
    else
    {
      return REST_DOMAIN_URL = getRestUrl() + "/" + getRestDomain() + "/"
          + "document/" + getRestDomain() + "/";
    }
  }

  /**
   * @see org.sakaiproject.api.app.help.RestConfiguration#getRestCorpusUrl()
   */
  public String getRestCorpusUrl()
  {
    if (REST_DOMAIN_URL != null)
    {
      return REST_CORPUS_URL;
    }
    else
    {
      return REST_CORPUS_URL = getRestUrl() + "/" + getRestDomain() + "/"
          + "documents";
    }
  }

  /**
   * @see org.sakaiproject.api.app.help.RestConfiguration#getCorpusDocument()
   */
  public String getCorpusDocument()
  {

    if (LOG.isDebugEnabled())
    {
      LOG.debug("getCorpusDocument()");
    }

    URL url = null;
    StringBuffer sBuffer = new StringBuffer();
    BufferedReader br = null;
    try
    {
      url = new URL(getRestCorpusUrl());
      URLConnection urlConnection = url.openConnection();

      String basicAuthUserPass = getRestCredentials();
      String encoding = new BASE64Encoder()
          .encode(basicAuthUserPass.getBytes());

      urlConnection.setRequestProperty("Authorization", "Basic " + encoding);

      br = new BufferedReader(new InputStreamReader(urlConnection
          .getInputStream()), 512);
      int readReturn = 0;
      char[] cbuf = new char[512];
      while ((readReturn = br.read(cbuf, 0, 512)) != -1)
      {
        sBuffer.append(cbuf, 0, readReturn);
      }

    }
    catch (MalformedURLException e)
    {
      LOG.error("Malformed URL in REST document: " + url.getPath(), e);
    }
    catch (IOException e)
    {
      LOG.error("Could not open connection to REST document: " + url.getPath(),
          e);
    }
    finally
    {
      try
      {
        if (br != null)
        {
          br.close();
        }
      }
      catch (IOException e)
      {
        LOG.error("error closing corpus doc", e);
      }
    }

    return sBuffer.toString();
  }

  /**
   * @see org.sakaiproject.api.app.help.RestConfiguration#getResourceNameFromCorpusDoc(java.lang.String)
   */
  public String getResourceNameFromCorpusDoc(String xml)
  {
    try
    {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      DocumentBuilder builder = dbf.newDocumentBuilder();
      StringReader sReader = new StringReader(xml);
      InputSource inputSource = new org.xml.sax.InputSource(sReader);
      org.w3c.dom.Document xmlDocument = builder.parse(inputSource);
      sReader.close();
      
      NodeList nodeList = xmlDocument.getElementsByTagName("kbq");
      
      int nodeListLength = nodeList.getLength();
      for (int i = 0; i < nodeListLength; i++){
        Node currentNode = nodeList.item(i);
        
        NodeList nlChildren = currentNode.getChildNodes();
        
        for (int j = 0; j < nlChildren.getLength(); j++){
          if (nlChildren.item(j).getNodeType() == Node.TEXT_NODE){
            return nlChildren.item(j).getNodeValue();
          }
        }
      }
      return null;
    }            
    catch (Exception e)
    {
      LOG.error(e.getMessage(), e);
    }     
    
    return null;
  }

}
