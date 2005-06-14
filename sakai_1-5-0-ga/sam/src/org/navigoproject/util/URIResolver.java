/*
 * Created on May 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.util;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;


/**
 * @author palcasi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class URIResolver implements javax.xml.transform.URIResolver
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(URIResolver.class);
    
    public URIResolver()
    {
    }

    /* (non-Javadoc)
     * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String)
     */
    public Source resolve(String href, String base) throws TransformerException
    {   
        Source source = null;
        String path = null;
        try
        {
            URI uri = new URI(base);
            path = uri.resolve(href).toString();
            source = new StreamSource(path);
        }
        catch (URISyntaxException e)
        {
            LOG.error(e); throw new Error(e);
        }
        
        return source;
    }

}
