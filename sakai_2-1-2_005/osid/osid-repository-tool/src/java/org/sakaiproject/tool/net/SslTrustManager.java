/**********************************************************************************
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
package org.sakaiproject.tool.net;


/**
 * Create a trust manager that always accepts the certificate chains.
 * Used for testing only.  See <code>Search.java</code> for use.
 *<p>
 * Accessing an HTTPS URL using the URL class results in an
 * exception if the server's certificate chain has not previously been
 * installed in the truststore. For testing, override the default trust
 * manager with one that trusts all certificates.
 */
public class SslTrustManager implements javax.net.ssl.X509TrustManager {

  /**
   * Return an array of certificate authority certificates
   * @return Certificate array (always null in this dummy version)
   */
  public java.security.cert.X509Certificate[] getAcceptedIssuers() {
    return null;
  }

  /**
   * Indicate trust for this client (don't throw an exception)
   */
  public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
                                 String authType) {
  }

  /**
   * Indicate trust for this server (don't throw an exception)
   */
  public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
                                 String authType) {
  }
}
