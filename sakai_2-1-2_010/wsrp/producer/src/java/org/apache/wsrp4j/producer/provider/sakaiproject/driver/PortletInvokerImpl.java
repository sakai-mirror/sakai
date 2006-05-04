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

package org.apache.wsrp4j.producer.provider.sakaiproject.driver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oasis.names.tc.wsrp.v1.types.BlockingInteractionResponse;
import oasis.names.tc.wsrp.v1.types.GetMarkup;
import oasis.names.tc.wsrp.v1.types.MarkupContext;
import oasis.names.tc.wsrp.v1.types.MarkupParams;
import oasis.names.tc.wsrp.v1.types.MarkupResponse;
import oasis.names.tc.wsrp.v1.types.NamedString;
import oasis.names.tc.wsrp.v1.types.PerformBlockingInteraction;
import oasis.names.tc.wsrp.v1.types.PortletContext;
import oasis.names.tc.wsrp.v1.types.RuntimeContext;
import oasis.names.tc.wsrp.v1.types.UpdateResponse;

import org.apache.wsrp4j.exception.ErrorCodes;
import org.apache.wsrp4j.exception.WSRPException;
import org.apache.wsrp4j.exception.WSRPXHelper;
import org.apache.wsrp4j.log.LogManager;
import org.apache.wsrp4j.log.Logger;
import org.apache.wsrp4j.producer.provider.PortletInvoker;
import org.apache.wsrp4j.producer.provider.Provider;
import org.apache.wsrp4j.producer.provider.sakaiproject.WSRPDynamicInformationProvider;
import org.apache.wsrp4j.producer.util.Base64;
import org.apache.wsrp4j.producer.util.ServletAccess;
import org.apache.wsrp4j.util.Constants;
import org.apache.wsrp4j.util.LocaleHelper;
import org.apache.wsrp4j.util.WindowStates;
import org.sakaiproject.api.kernel.session.ContextSession;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.thread_local.cover.ThreadLocalManager;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.ToolURL;


/**
 * <p>This class implements the PortletInvoker interface. It should only be
 * instantiated within an implementation of the
 * Provider interface.</p>
 * <p>The provided invokePortlet method ...</p>
 *
 * @see org.apache.wsrp4j.producer.provider.PortletInvoker  
 **/
public class PortletInvokerImpl implements PortletInvoker
{

    // for logging and exception support
    private Logger logger = LogManager.getLogManager().getLogger(this.getClass());

    private Provider provider;
    
    /**
     * private constructor
     */
    private PortletInvokerImpl(Provider provider)
    {
        this.provider = provider;
    }

    /**
     * Creates a new PortletInvokerImpl-object. To be called within
     * an implementation of the abstract factory Provider.
     * @param provider
     */
    public static PortletInvokerImpl create(Provider provider)
    {
        PortletInvokerImpl portletInvoker = new PortletInvokerImpl(provider);
        return portletInvoker;
    }

    /**
     * 
     @throws WSRPException 
     * @see org.apache.wsrp4j.producer.provider.PortletInvoker#invokeGetMarkup 
     */
    public MarkupResponse invokeGetMarkup(GetMarkup request) throws WSRPException
    {
        String MN = "invokeGetMarkup";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }
        HttpServletRequest servletRequestOrig = ServletAccess.getServletRequest();
        MarkupParams markupParams = request.getMarkupParams();
        Locale usedLocale = LocaleHelper.getLocale(markupParams.getLocales()[0]);

        HttpServletRequest servletRequest =
            new WSRPServletRequestWrapperImpl(
                servletRequestOrig,
                usedLocale,
                markupParams.getMimeTypes()[0],
                parseMarkupParams(markupParams),
                getPathInfo(markupParams.getNavigationalState()));


        // All WSRP requests are POSTs, if we don't change this to GET, the target servlet's POST will be
        // invoked rather than GET!
        ((WSRPServletRequestWrapperImpl)servletRequest).setMethod("GET");
        servletRequest.setAttribute(WSRPDynamicInformationProvider.REQUEST, request);        
        servletRequest.setAttribute(WSRPDynamicInformationProvider.PROVIDER, this.provider);
        WSRPDynamicInformationProvider infoProvider = new DynamicInformationProviderImpl(servletRequest);
        servletRequest.setAttribute(WSRPDynamicInformationProvider.INFO_PROVIDER, infoProvider);        
        
        StoredResponse servletResponse = new StoredResponse(servletRequestOrig, this.provider);

        // need to have a PortalEnvironment in request
        // the constructor does this
        //PortalEnvironment env =
        //     new PortalEnvironment(servletRequestOrig, servletResponse, ServletAccess.getServlet().getServletConfig());

        // Check Window State
        String windowState = markupParams.getWindowState();
        if (!WindowStates.solo.equals(WindowStates.fromString(windowState)))
        {
            // Unless it is "solo" we should render fragment, rather than full page
            servletRequest.setAttribute(SakaiPortlet.FRAGMENT, Boolean.TRUE);
            servletRequest.setAttribute(SakaiPortlet.PORTLET, Boolean.TRUE);
        }
        
        try
        {
            // set char encoding, default to UTF-8
            String[] charSets = markupParams.getMarkupCharacterSets();
            String charEncoding = null;
            if (charSets != null)
            {
                charEncoding = charSets[0];
            } else
            {
                charEncoding = Constants.UTF_8;
            }

            servletRequest.setCharacterEncoding(charEncoding);
        } catch (java.io.UnsupportedEncodingException e) { }

        servletResponse.setLocale(usedLocale);
        servletResponse.setContentType(markupParams.getMimeTypes()[0]);


        String markup = "Error occured while invoking portlet service!";
        String locale = Constants.LOCALE_EN_US;

        Object prev = ThreadLocalManager.get("sakai.wsrp.producer.servletResponse");
        try
        {
            invokePortlet(request.getPortletContext(), request.getRuntimeContext(), servletRequest, servletResponse);

            markup = servletResponse.getOutputBufferAsString();

            locale = servletResponse.getLocale().toString();

        } catch (IOException e) {
            if (logger.isLogging(Logger.ERROR))
            {
                logger.text(Logger.ERROR, MN, e, "Call of portletService() failed!");
            }
            WSRPXHelper.throwX(ErrorCodes.OPERATION_FAILED, e);

        }
        finally {
            ThreadLocalManager.set("sakai.wsrp.producer.servletResponse", prev);
        }
        
        // build MarkupResponse
        MarkupResponse markupResponse = new MarkupResponse();

        // set markupContext in markupResponse
        if (markup != null)
        {
            MarkupContext markupContext = new MarkupContext();
            markupContext.setMarkupString(markup);
            markupContext.setLocale(locale);
            markupContext.setMimeType(servletRequest.getContentType());
            markupResponse.setMarkupContext(markupContext);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return markupResponse;
    }

    /**
     * Get the PathInfo specific to the Portlet from the navigationalState.
     * If the navigationalState contains fully qualified URL, we need to strip that out!
     * 
     * @param req
     * @param navigationalState
     * @return
     */
    private String getPathInfo(String navigationalState) 
    {
        String url = null;
        try {
            if (navigationalState != null)
                url = new String(Base64.decode(navigationalState));
        } catch (Exception e) {
            logger.text(Logger.ERROR, "getPathInfo", e, navigationalState);
        }
        if (url == null) return null;
        
        int pos = url.indexOf('?'); 
        if (pos != -1)
        {
            // Remove any query parameters, if there are any
            url = url.substring(0, pos);
        }
        
        return url;
    }

    protected void invokePortlet(PortletContext portletContext, RuntimeContext runtimeContext,
            HttpServletRequest req, HttpServletResponse res)
        throws IOException, WSRPException
    {
        // get PortletID from PortletContext
        String portletID = portletContext.getPortletHandle();
        SakaiPortlet portlet = (SakaiPortlet)provider.getPortletPool().get(portletID);

        if (portlet == null)
        {
            WSRPXHelper.throwX(ErrorCodes.INVALID_PORTLET_HANDLE);
        }
        else 
        {
            // Set URL Handlers           
            req.setAttribute(ToolURL.MANAGER, new SakaiURLManager(req));
            ThreadLocalManager.set(ToolURL.HTTP_SERVLET_REQUEST, req);
            
            // find / make the placement id for this tool - a Placement is stored keyed by context+toolId
            String instanceKey = runtimeContext.getPortletInstanceKey();
            Placement p = portlet.getPlacement(instanceKey);
            portlet.render(req, res, p);
            
            ThreadLocalManager.set(ToolURL.HTTP_SERVLET_REQUEST, null);
        } 
    }
   
    /**
     * 
     @throws WSRPException 
     * @see org.apache.wsrp4j.producer.provider.PortletInvoker#invokePerformBlockingInteraction 
     */
    public BlockingInteractionResponse invokePerformBlockingInteraction(
        PerformBlockingInteraction request) throws WSRPException
    {
        
        String MN = "invokePerformBlockingInteraction";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        HttpServletRequest servletRequestOrig = ServletAccess.getServletRequest();
        MarkupParams markupParams = request.getMarkupParams();
        Locale usedLocale = LocaleHelper.getLocale(markupParams.getLocales()[0]);

        HttpServletRequest servletRequest =
            new WSRPServletRequestWrapperImpl(
                servletRequestOrig,
                usedLocale,
                markupParams.getMimeTypes()[0],
                parseInteractionParams(request),
                getPathInfo(markupParams.getNavigationalState()));

        servletRequest.setAttribute(WSRPDynamicInformationProvider.REQUEST, request);        
        servletRequest.setAttribute(WSRPDynamicInformationProvider.PROVIDER, this.provider);
        WSRPDynamicInformationProvider infoProvider = new DynamicInformationProviderImpl(servletRequest);
        servletRequest.setAttribute(WSRPDynamicInformationProvider.INFO_PROVIDER, infoProvider);        

        // TODO: Use a factory method to create this!
        StoredResponse servletResponse = new StoredResponse(servletRequestOrig, this.provider);

        // Check Window State
        String windowState = markupParams.getWindowState();
        if (!WindowStates.solo.equals(WindowStates.fromString(windowState)))
        {
            // Unless it is "solo" we should render fragment, rather than full page
            servletRequest.setAttribute(SakaiPortlet.FRAGMENT, Boolean.TRUE);
            servletRequest.setAttribute(SakaiPortlet.PORTLET, Boolean.TRUE);
        }

        try
        {
            // set char encoding, default to UTF-8
            String[] charSets = markupParams.getMarkupCharacterSets();
            String charEncoding = null;
            if (charSets != null)
            {
                charEncoding = charSets[0];
            } else
            {
                charEncoding = Constants.UTF_8;
            }
            servletRequest.setCharacterEncoding(charEncoding);
        } catch (java.io.UnsupportedEncodingException e) { }

        servletResponse.setLocale(usedLocale);
        servletResponse.setContentType(markupParams.getMimeTypes()[0]);

        String markup = "Error occured while invoking portlet service!";
        String locale = Constants.LOCALE_EN_US;
        Object prev = ThreadLocalManager.get("sakai.wsrp.producer.servletResponse");

        try {
            invokePortlet(request.getPortletContext(), request.getRuntimeContext(), servletRequest, servletResponse);

            markup = servletResponse.getOutputBufferAsString();

            locale = servletResponse.getLocale().toString();

        } catch (IOException e) {
            if (logger.isLogging(Logger.ERROR))
            {
                logger.text(Logger.ERROR, MN, e, "Call of portletService() failed!");
            }
            WSRPXHelper.throwX(ErrorCodes.OPERATION_FAILED, e);
        }
        finally {
            ThreadLocalManager.set("sakai.wsrp.producer.servletResponse", prev);
        }
        
        // create new BlockingInteractionResponse
        BlockingInteractionResponse blockingInteractionResponse = new BlockingInteractionResponse();

        // new UpdateResponse
        UpdateResponse updateResponse = new UpdateResponse();

		// set the nav state
		// get render params from servlet request and serialize the map
        /*
		String navState = null;
		Map renderParams = (Map) servletRequest.getAttribute(WSRPDynamicInformationProvider.RENDER_PARAMS);
		if (renderParams != null)
		{
			try
			{
				navState = Base64.encode(MapSerializer.serialize(renderParams));
			} catch (IOException e)
			{
				if (logger.isLogging(Logger.ERROR))
				{
					logger.text(Logger.ERROR, MN, e, "Could not serialize and encode render params");
				}
			}
		}
		
        updateResponse.setNavigationalState(navState);
        */

        // set markupContext in interactionResponse
        if ((markup != null) && (markup.length() != 0))
        {
            MarkupContext markupContext = new MarkupContext();
            markupContext.setLocale(locale);
            markupContext.setMimeType(servletRequest.getContentType());
            markupContext.setMarkupString(markup);
            updateResponse.setMarkupContext(markupContext);
            blockingInteractionResponse.setRedirectURL(null);
        }
        else if (servletResponse.getRedirectURI() != null){
            blockingInteractionResponse.setRedirectURL(servletResponse.getRedirectURI());                
        }
        
        // set the updateResponse of the blockingInteraction
        blockingInteractionResponse.setUpdateResponse(updateResponse);
        blockingInteractionResponse.setExtensions(null);

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return blockingInteractionResponse;
    }


    private Map parseMarkupParams(MarkupParams markupParams)
    {
        Map navParams = new HashMap();
        String naviStateStr = markupParams.getNavigationalState();
        if (naviStateStr != null)
        {
            byte [] desMap = null;
            try {
                desMap = Base64.decode(naviStateStr);
            } catch (Exception e) {
                logger.text(Logger.ERROR, "getMarkupParams", e, naviStateStr);
                return navParams;
            }
            Map naviState = null;
            naviState = parseURLParameters(new String(desMap));
            if (naviState != null)
            {
                navParams.putAll(naviState);
            }
        }
        return navParams;
    }

    private Map parseInteractionParams(PerformBlockingInteraction blockingInteractionRequest)
    {
        Map actionParams = parseMarkupParams(blockingInteractionRequest.getMarkupParams());
        
        String actionStateStr = blockingInteractionRequest.getInteractionParams().getInteractionState();
        if (actionStateStr != null)
        {
            byte [] desMap = null;
            try {
                desMap = Base64.decode(actionStateStr);
            } catch (Exception e) {
                logger.text(Logger.ERROR, "getInteractionParams", e, actionStateStr);
                return actionParams;
            }

            Map actionState = null;
            actionState = parseURLParameters(new String(desMap));
            if (actionState != null)
            {
                actionParams.putAll(actionState);
            }
        }
        actionParams.putAll(getFormParameters(blockingInteractionRequest.getInteractionParams().getFormParameters()));

        return actionParams;
    }

    /**
      Internal method.
      Return the FORM parameters as Map
      @param params array of NamedString objects
      @return java.util.Map containing the FORM parameters and values
    */
    private static Map getFormParameters(NamedString[] params)
    {

        HashMap tmp = new HashMap();

        if (params != null)
        {
            for (int i = 0; i < params.length; i++)
            {
				String parameterName = params[i].getName();
				if (tmp.containsKey(parameterName))
				{
					// Handle case in which parameter name has multiple values
					String[] currentValues = (String[])tmp.get(parameterName);
					String[] newValues = new String[currentValues.length+1];
					System.arraycopy(currentValues, 0, newValues, 0, currentValues.length);
					newValues[currentValues.length] = params[i].getValue();
					tmp.put(parameterName, newValues);
				}
				else
				{
					String[] values = { params[i].getValue()};
					tmp.put(parameterName, values);
				}
            }
        }

        return tmp;
    }


    /**
     *
     * Parses a query string passed from the client to the
     * server and builds a <code>HashMap</code> object
     * with key-value pairs. 
     * The query string should be in the form of a string
     * packaged by the GET or POST method, that is, it
     * should have key-value pairs in the form <i>key=value</i>,
     * with each pair separated from the next by a &amp; character.
     *
     * <p>A key can appear more than once in the query string
     * with different values. However, the key appears only once in 
     * the hashmap, with its value being
     * an array of strings containing the multiple values sent
     * by the query string.
     * 
     * <p>The keys and values in the hashmap are stored in their
     * decoded form, so
     * any + characters are converted to spaces, and characters
     * sent in hexadecimal notation (like <i>%xx</i>) are
     * converted to ASCII characters.
     *
     * @param s         a string containing the query to be parsed
     *
     * @return          a <code>HashMap</code> object built
     *                  from the parsed key-value pairs
     *
     * @exception IllegalArgumentException      if the query string 
     *                                          is invalid
     *                                          
     * Adapted from javax.servlet.HttpUtils#parseQueryString
     */

    private Map parseURLParameters(String url)
    {
        Map params = null;
        String queryStr = url;
        if ((url != null) && (url.indexOf('?') != -1))
        {
            queryStr = url.substring(url.indexOf('?') + 1);
        }
        if ((queryStr == null) || (queryStr.length() == 0))
            return params;
           
        String valArray[] = null;
        params = new HashMap();
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(queryStr, "&");
        while (st.hasMoreTokens()) {
            String pair = (String)st.nextToken();
            int pos = pair.indexOf('=');
            if (pos == -1) {
                // This is actually illegal, but we will ignore it!!
                continue;
            }
            String key = parseName(pair.substring(0, pos), sb);
            String val = parseName(pair.substring(pos+1, pair.length()), sb);
            if (params.containsKey(key)) {
                String oldVals[] = (String []) params.get(key);
                valArray = new String[oldVals.length + 1];
                for (int i = 0; i < oldVals.length; i++) 
                    valArray[i] = oldVals[i];
                valArray[oldVals.length] = val;
            } else {
                valArray = new String[1];
                valArray[0] = val;
            }
            params.put(key, valArray);
        }
        return params;
    }


    /*
     * Parse a name in the query string.
     * Adapted from javax.servlet.http.HttpUtil#parseName
     */
    private String parseName(String s, StringBuffer sb) {
        sb.setLength(0);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i); 
            switch (c) {
            case '+': sb.append(' ');
                      break;
            case '%':
                try {
                    sb.append((char) Integer.parseInt(s.substring(i+1, i+3), 16));
                    i += 2;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Malformed Query String in Navigational State: " + s);
                } catch (StringIndexOutOfBoundsException e) {
                    String rest  = s.substring(i);
                    sb.append(rest);
                    if (rest.length()==2) i++;
                }
                break;
            default:
                sb.append(c);
                break;
            }
        }
        return sb.toString();
    }

}
