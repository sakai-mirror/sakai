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


package org.apache.wsrp4j.producer.provider.sakaiproject.driver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wsrp4j.producer.provider.Provider;
import org.apache.wsrp4j.producer.provider.sakaiproject.WSRPDynamicInformationProvider;
import org.apache.wsrp4j.util.Constants;

/**
 * The <CODE>StoredResponse</CODE> is a
 * <CODE>HttpServletResponse</CODE> that does not
 * write to the output stream directly but buffers
 * the markup.
 * Required to return the markup produced by a 
 * <CODE>servlet</CODE> (e.g. a <CODE>WSRPlet</CODE>)
 * as a <CODE>String</CODE>.
 * 
 * @author <a href="mailto:peter.fischer@de.ibm.com">Peter Fischer</a>
   @see javax.servlet.http.HttpServletResponse
 */
public class StoredResponse implements HttpServletResponse
{

    //default status code
    private static final int DEFAULT_STATUS_CODE = 200;
    
    //writer
    private PrintWriter _writer;

    //finished
    private boolean _isFinished;

    //status code
    private int _statusCode;

    //status message
    private String _statusMessage;

    //redirect URL 
    private String _redirectURI;

    //string writer
    private StringWriter _stringWriter;

    //locale
    private Locale _locale;

    // Servlet Output Stream
    private ServletOutputStream _servletOutputStream;
    
    // Dynamic information provider
    private Provider _provider;
      
    // Corresponding Servlet Request
    private HttpServletRequest _request;
    
    /**
      Default constructor
     * @param request 
     * @param infoProvider 
    */
    public StoredResponse(HttpServletRequest request, Provider provider)
    {
        _statusCode = DEFAULT_STATUS_CODE;
        _locale = new Locale("en", "US");
        _request = request;
        _provider = provider;
        setContentType("text/html");
    }

    /**
      @see javax.servlet.http.HttpServletResponse#addCookie
    */
    public void addCookie(Cookie cookie)
    {
    }

    /**
      @see javax.servlet.http.HttpServletResponse#addDateHeader
    */
    public void addDateHeader(String a, long b)
    {
    }

    /**
      @see javax.servlet.http.HttpServletResponse#addHeader
    */
    public void addHeader(String a, String b)
    {
    }

    /**
      @see javax.servlet.http.HttpServletResponse#addIntHeader
    */
    public void addIntHeader(String a, int b)
    {
    }

    /**
      @see javax.servlet.http.HttpServletResponse#containsHeader
    */
    public boolean containsHeader(String s)
    {
        return false;
    }

    /**
      @see javax.servlet.http.HttpServletResponse#encodeRedirectURL
    */
    public String encodeRedirectURL(String s)
    {
        return rewriteURL(s);
    }

    /**
      @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl
    */
    public String encodeRedirectUrl(String s)
    {
        return rewriteURL(s);
    }

    /**
      @see javax.servlet.http.HttpServletResponse#encodeURL
    */
    public String encodeURL(String s)
    {
        return rewriteURL(s);
    }

    /**
      @see javax.servlet.http.HttpServletResponse#encodeUrl
    */
    public String encodeUrl(String s)
    {
        return rewriteURL(s);
    }
    
    protected String rewriteURL(String url)
    {
        return _provider.getPortletURLProvider(_request).encodeURL(url);        
    }


    /**
      Internal method
      Finish the response.
    */
    private void finish() throws IOException
    {
        synchronized (this)
        {
            if (_isFinished)
                return;
            if (_writer != null)
            {
                _writer.flush();
                _stringWriter.flush();
            }

            _isFinished = true;
            setContentLength(_stringWriter.getBuffer().length());
            _writer = null;
        }
    }

    /**
      @see javax.servlet.http.HttpServletResponse#flushBuffer()
    */
    public void flushBuffer()
    {
    }

    /**
      @see javax.servlet.http.HttpServletResponse#getBufferSize()
    */
    public int getBufferSize()
    {
        return 4096;
    }

    /**
      @see javax.servlet.http.HttpServletResponse#getCharacterEncoding()
    */
    public String getCharacterEncoding()
    {
        return System.getProperty("default.client.encoding");
    }

    /**
      @see javax.servlet.http.HttpServletResponse#getLocale()
    */
    public java.util.Locale getLocale()
    {
        return _locale;
    }

    /**
      Returns the output buffer content as String
      @return java.lang.String
    */
    public String getOutputBufferAsString() throws IOException
    {
        if (_stringWriter == null)
        {
            return "";
        }
        return _stringWriter.getBuffer().toString();
    }

    /**
      @see javax.servlet.http.HttpServletResponse#getOutputStream()
    */
    public ServletOutputStream getOutputStream()
    {
        if (_servletOutputStream == null) {
            _servletOutputStream = new PrintWriterServletOutputStream(getWriter());
        }
        return _servletOutputStream;
    }

    /**
      @see javax.servlet.http.HttpServletResponse#getWriter()
    */
    public PrintWriter getWriter()
    {
        if ((_stringWriter == null) || (_writer == null))
        {
            _stringWriter = new StringWriter();
            _writer = new PrintWriter(_stringWriter);
        }

        return _writer;
    }

    /**
      @see javax.servlet.http.HttpServletResponse#isCommitted()
    */
    public boolean isCommitted()
    {
        return false;
    }

    /**
      @see javax.servlet.http.HttpServletResponse#reset()
    */
    public void reset()
    {
    }

    /**
      @see javax.servlet.http.HttpServletResponse#sendError(int)
    */
    public void sendError(int i) throws IOException
    {
    }

    /**
      @see javax.servlet.http.HttpServletResponse#sendError(int, String)
    */
    public void sendError(int i, String s) throws IOException
    {
    }

    /**
      @see javax.servlet.http.HttpServletResponse#sendRedirect(String)
    */
    public void sendRedirect(String s)
    {
        _redirectURI = s;
    }

    /**
      @see javax.servlet.http.HttpServletResponse#setBufferSize(int)
    */
    public void setBufferSize(int i)
    {
    }

    /**
      @see javax.servlet.http.HttpServletResponse#setContentLength(int)
    */
    public void setContentLength(int i)
    {
        setIntHeader("content-length", i);
    }

    /**
      @see javax.servlet.http.HttpServletResponse#setContentType(String)
    */
    public void setContentType(String s)
    {
        setHeader("content-type", s);
    }

    /**
      @see javax.servlet.http.HttpServletResponse#setDateHeader(String, long)
    */
    public void setDateHeader(String s, long l)
    {
    }

    /**
      @see javax.servlet.http.HttpServletResponse#setHeader(String, String)
    */
    public void setHeader(String s, String s1)
    {
    }

    /**
      @see javax.servlet.http.HttpServletResponse#setIntHeader
    */
    public void setIntHeader(String s, int i)
    {
    }

    /**
      @see javax.servlet.http.HttpServletResponse#setLocale(Locale)
    */
    public void setLocale(Locale locale)
    {
        _locale = locale;
    }

    /**
      @see javax.servlet.http.HttpServletResponse#setStatus(int)
    */
    public void setStatus(int i)
    {
        _statusCode = i;
    }

    /**
      @see javax.servlet.http.HttpServletResponse#setStatus(int, String)
    */
    public void setStatus(int i, String s)
    {
        _statusCode = i;
        _statusMessage = s;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponse#resetBuffer()
     */
    public void resetBuffer()
    {
        // TODO Auto-generated method stub

    }

    public String getContentType() {
        return Constants.MIME_TYPE_HTML;
    }

    public void setCharacterEncoding(String arg0) {
        // TODO Auto-generated method stub
        
    }
    
    protected String getRedirectURI()
    {
        return _redirectURI;
    }
    
    /**
     * This class implements a ServletOutputStream that delegates to a PrintWriter. 
     */
    public class PrintWriterServletOutputStream extends ServletOutputStream
    {

      /**
       * The PrintWriter that is wrapped underneath the input stream
       */
      PrintWriter _writer;

      /**
       * Construct a ServletOutputStream that coordinates output using a base
       * ServletOutputStream and a PrintWriter that is wrapped on top of that
       * OutputStream.
       */
      public PrintWriterServletOutputStream(PrintWriter pw)
      {
        super();
        _writer = pw;
      }

      /**
       * Writes an array of bytes
       * 
       * @param pBuf the array to be written
       * @exception IOException if an I/O error occurred
       */
      public void write(byte[] pBuf) throws IOException
      {
        char[] cbuf = new char[pBuf.length];
        for (int i = 0; i < cbuf.length; i++)
          cbuf[i] = (char)(pBuf[i] & 0xff);
        _writer.write(cbuf, 0, pBuf.length);
      }

      /**
       * Writes a single byte to the output stream
       */
      public void write(int pVal) throws IOException
      {
        _writer.write(pVal);
      }

      /**
       * Writes a subarray of bytes
       * 
       * @param pBuf the array to be written
       * @param pOffset the offset into the array
       * @param pLength the number of bytes to write
       * @exception IOException if an I/O error occurred
       */
      public void write(byte[] pBuf, int pOffset, int pLength) throws IOException
      {
        char[] cbuf = new char[pLength];
        for (int i = 0; i < pLength; i++)
          cbuf[i] = (char)(pBuf[i + pOffset] & 0xff);
        _writer.write(cbuf, 0, pLength);
      }

      /**
       * Flushes the stream, writing any buffered output bytes
       * 
       * @exception IOException if an I/O error occurred
       */
      public void flush() throws IOException
      {
        _writer.flush();
      }

      /**
       * Closes the stream
       * 
       * @exception IOException if an I/O error occurred
       */
      public void close() throws IOException
      {
        _writer.close();
      }

      /**
       * 
       * Prints a string.
       * 
       * @param pVal the String to be printed
       * @exception IOException if an I/O error has occurred
       */
      public void print(String pVal) throws IOException
      {
        _writer.print(pVal);
      }

      /**
       * 
       * Prints an string followed by a CRLF.
       * 
       * @param pVal the String to be printed
       * @exception IOException if an I/O error has occurred
       */
      public void println(String pVal) throws IOException
      {
        _writer.println(pVal);
      }

      /**
       * 
       * Prints a CRLF
       * 
       * @exception IOException if an I/O error has occurred
       *  
       */
      public void println() throws IOException
      {
        _writer.println();
      }

    }
}
