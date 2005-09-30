/**********************************************************************************
* $HeadURL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.tool.assessment.util;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 * <p>Copyright: Copyright (c) 2003-5</p>
 * <p>Organization: Sakai Project</p>
 * @author jlannan
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id$
 */
public class TextFormat
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(TextFormat.class);
  private static final String HTML;
  private static final String SMART;
  private static final String PLAIN;
  private static final Vector vProtocols;
  private String upperText;
  private StringBuffer returnText;
  private StringBuffer resource;
  private ArrayList arrLst;

  static
  {
    HTML = "HTML";
    SMART = "SMART";
    PLAIN = "PLAIN";

    vProtocols = new Vector();
    vProtocols.add("http://");
    vProtocols.add("https://");
    vProtocols.add("ftp://");
    vProtocols.add("www.");
    vProtocols.add("telent:");
    vProtocols.add("mailto:");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param text TEXT TO BE MODIFIED
   * @param texttype TYPE OF TEXT -- PLAIN, HTML, OR SMART
   * @param iconPath PATH TO ICON IMAGES IN APPLICATION
   *
   * @return DOCUMENTATION PENDING
   */
  public String formatText(String text, String texttype, String iconPath)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "formatText(String " + text + ", String " + texttype + ", String " +
        iconPath + ")");
    }

    returnText = new StringBuffer();

    if((texttype == null) || (text == null))
    {
      return text;
    }
    else if(texttype.equals(TextFormat.PLAIN))
    {
      return text;
    }
    else if(texttype.equals(TextFormat.HTML))
    {
      return text;
    }
    else if(texttype.equals(TextFormat.SMART))
    {
      int start = 0;
      int end = 0;

      while(true)
      {
        arrLst = new ArrayList();

        // traverse vector of protocol strings
        Iterator i = vProtocols.iterator();
        Integer retVal = new Integer(0);
        while(i.hasNext())
        {
          String str = (String) i.next();
          arrLst.add(retVal = indexOfIgnoreCase(text, str));
          if(retVal.intValue() == -1)
          {
            i.remove();
          }
        }

        start = minimum(arrLst);
        LOG.debug("start: " + String.valueOf(start));
        if((start == -1) || vProtocols.isEmpty())
        {
          break;
        }

        // find either the next space or the end of string whichever comes first
        if((end = text.indexOf(" ", start)) == -1)
        {
          end = text.length();
        }

        // extract text and resource text from StringBuffer
        if(start != 0)
        {
          returnText.append(text.substring(0, start));
          LOG.debug(
            "adding pre-resource text: " + text.substring(0, start));
        }

        LOG.debug("end: " + String.valueOf(end));

        resource = new StringBuffer();
        String upper = text.substring(start, end).toUpperCase();
        try
        {
          if(upper.startsWith("HTTPS://"))
          {
            resource.append("https://");
            resource.append(
              URLEncoder.encode(text.substring(start + 8, end), "UTF-8"));
            LOG.debug("hi" + resource);
          }
          else if(
            upper.startsWith("HTTP://") || upper.startsWith("MAILTO:") ||
              upper.startsWith("TELNET:"))
          {
            resource.append("http://");
            resource.append(
              URLEncoder.encode(text.substring(start + 7, end), "UTF-8"));
          }
          else if(upper.startsWith("FTP://"))
          {
            resource.append("ftp://");
            resource.append(
              URLEncoder.encode(text.substring(start + 6, end), "UTF-8"));
          }
          else if(upper.startsWith("WWW."))
          {
            resource.append("www.");
            resource.append(
              URLEncoder.encode(text.substring(start + 4, end), "UTF-8"));
          }
          else
          {
            ;
          }
        }
        catch(UnsupportedEncodingException e)
        {
          LOG.error(e.getMessage(), e);
        }

        String temp = resource.toString();
        resource.insert(resource.length(), "', target=_new>" + temp + "</a>");
        resource.insert(0, "<a href='");

        //stringParts.add(resource.toString());
        returnText.append(resource);
        LOG.debug("add ing resource: " + resource.toString());

        // delete resource string
        text = text.substring(end);
      }

      // add remaining characters to buffer
      if(text.length() != 0)
      {
        returnText.append(text);
      }

      int temp = 0;

      // replace emoticons with images
      while((temp = returnText.indexOf(":-)")) != -1)
      {
        returnText.replace(
          temp, temp + 3, "<img src='" + iconPath + "smile.gif'/>");
      }

      while((temp = returnText.indexOf(":-(")) != -1)
      {
        returnText.replace(
          temp, temp + 3, "<img src='" + iconPath + "frown.gif'/>");
      }

      while((temp = returnText.indexOf(":-o")) != -1)
      {
        returnText.replace(
          temp, temp + 3, "<img src='" + iconPath + "suprise.gif'/>");
      }

      while((temp = returnText.indexOf(";-)")) != -1)
      {
        returnText.replace(
          temp, temp + 3, "<img src='" + iconPath + "wink.gif'/>");
      }

      while((temp = returnText.indexOf(":)")) != -1)
      {
        returnText.replace(
          temp, temp + 2, "<img src='" + iconPath + "smile.gif'/>");
      }

      while((temp = returnText.indexOf(":(")) != -1)
      {
        returnText.replace(
          temp, temp + 2, "<img src='" + iconPath + "frown.gif'/>");
      }

      while((temp = returnText.indexOf(":o")) != -1)
      {
        returnText.replace(
          temp, temp + 2, "<img src='" + iconPath + "suprise.gif'/>");
      }

      while((temp = returnText.indexOf(";)")) != -1)
      {
        returnText.replace(
          temp, temp + 2, "<img src='" + iconPath + "wink.gif'/>");
      }

      if(returnText != null)
      {
        return returnText.toString();
      }
      else
      {
        return "";
      }
    }
    else
    {
      return "";
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param str STRING TO BE SEARCHED
   * @param searchString STRING TO SEARCH FOR WITHIN str
   *
   * @return INDEX LOCATION OF searchString within str
   */
  public Integer indexOfIgnoreCase(String str, String searchString)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "indexOfIgnoreCase(String " + str + ", String " + searchString + ")");
    }

    return new Integer(str.toUpperCase().indexOf(searchString.toUpperCase()));
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param a LIST OF INDICIES OF OCCURANCES OF HYPERLINKS WITHIN TEXT
   *
   * @return MINIMUM OF ALL OCCURANCES OR -1 IF NO OCCURANCES
   */
  public int minimum(ArrayList a)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("minimum(ArrayList " + a + ")");
    }

    boolean firstNumber = true;
    int tmp = 0;
    int min = -1;

    Iterator i = a.iterator();
    while(i.hasNext())
    {
      tmp = ((Integer) i.next()).intValue();

      if(firstNumber && (tmp != -1))
      {
        firstNumber = false;
        min = tmp;
      }

      if((tmp != -1) && (tmp < min))
      {
        min = tmp;
      }
    }

    return min;
  }

  /**
   * test
   *
   * @param args
   */
  public static void main(String[] args)
  {
    TextFormat tf = new TextFormat();
    LOG.debug(
      tf.formatText(
        "www.cs.iupui.edu ui.edu dd dd dd:):-) ff telnet:dd dddddupui.edu",
        "SMART", ("http://oncourse.iu.edu/images/icons/")));
    LOG.debug(
      tf.formatText(
        "http://www.iupui.edu:80 www.ui.edu :( ::-( ddd", "SMART",
        ("http://oncourse.iu.edu/images/icons/")));

    LOG.debug(String.valueOf(System.identityHashCode(tf)));
  }
}
