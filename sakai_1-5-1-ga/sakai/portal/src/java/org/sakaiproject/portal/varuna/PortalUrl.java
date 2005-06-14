/*
 * Created on Nov 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.portal.varuna;

import javax.servlet.http.HttpServletRequest;

//import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.StringUtil;

/**
 * @author haines
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
class PortalUrl
{

    protected String pathPortalId = null;

    protected String pathGalleryId = null;

    protected String pathSiteId = null;

    protected String pathPageId = null;

    protected String pathTId = null;

    // displayLevel is the level that should be seen
    protected int displayLevel = 1;

    protected boolean direct = false;

    protected String displayPrefix = null;

    protected String errorSiteId = null;
    
    private Logger m_logger = null;

    /**
     * 
     * @return Returns the logger implementation.  This will
     * self-inject from the static cover if the logger is not yet set.  
     * This approach makes testing easier by avoiding the dependence on
     * static covers.
     * 
     */
    public Logger getLogger()
    {
        if (m_logger == null) 
        {
            setLogger((Logger)
                    org.sakaiproject.service.framework.log.cover.Logger.getInstance());
        }
        return m_logger;
    }
    
    /**
     * @param m_logger The m_logger to set.
     */
    public void setLogger(Logger logger)
    {
        m_logger = logger;
    }
    
    
    /**
     * @return Returns the displayPrefix.
     */
    public String getDisplayPrefix()
    {
        return displayPrefix;
    }

    /**
     * @param displayPrefix
     *            The displayPrefix to set.
     */
    public void setDisplayPrefix(String displayPrefix)
    {
        this.displayPrefix = displayPrefix;
    }

    /**
     * @return Returns the pathGalleryId.
     */
    public String getPathGalleryId()
    {
        return pathGalleryId;
    }

    /**
     * @param pathGalleryId
     *            The pathGalleryId to set.
     */
    public void setPathGalleryId(String pathGalleryId)
    {
        this.pathGalleryId = pathGalleryId;
    }

    /**
     * @return Returns the pathPortalId.
     */
    public String getPathPortalId()
    {
        return pathPortalId;
    }

    /**
     * @param pathPortalId
     *            The pathPortalId to set.
     */
    public void setPathPortalId(String pathPortalId)
    {
        this.pathPortalId = pathPortalId;
    }

    public PortalUrl(String errorSiteId)
    {
        this.errorSiteId = errorSiteId;
    }

    /**
     * @param pathSite
     *            The pathSite to set.
     */
    protected void setPathSiteId(String pathSite)
    {
        this.pathSiteId = pathSite;
    }

    /**
     * @return Returns the pathSite.
     */
    protected String getPathSiteId()
    {
        return pathSiteId;
    }

    /**
     * @param pathPage
     *            The pathPage to set.
     */
    protected void setPathPageId(String pathPage)
    {
        //        if (getPathSiteId() == null) {
        //            SitePage sitePage = SiteService.findPage(pathPage);
        //            Site site = sitePage.getContainingSite();
        //            setPathSiteId(site.getId());
        //        }
        this.pathPageId = pathPage;
    }

    /**
     * @return Returns the pathPage.
     */
    protected String getPathPageId()
    {

        return pathPageId;
    }

    /**
     * @param pathTool
     *            The pathTool to set.
     */
    protected void setPathToolId(String pathTool)
    {
        //        if (getPathPageId() == null) {
        //            ToolConfiguration tc = VarunaServlet.getToolConfigFromPid(pathTool);
        //            setPathPageId(tc.getPageId());
        //        }
        this.pathTId = pathTool;
    }

    /**
     * @return Returns the pathTool.
     */
    protected String getPathToolId()
    {
        return pathTId;
    }

    protected String normalize(String oldUrl)
    {
        //StringBuffer newUrl = new StringBuffer(oldUrl);
        //   String regex = "[&?](site\\|page\\|tool)=";
        String js_pane_regex = "js_pane";
        String js_pane_replace = "page";
        String level_regex = "[&?](site|page|tool)=";
        String level_replace = "/$1/";
        String intermediate = null;

        // there might be some with a js_pane in them.
        intermediate = oldUrl.trim().replaceAll(js_pane_regex, js_pane_replace);
        // translate all the levels
        return (String) intermediate.replaceAll(level_regex, level_replace);
    }

    /*
     * Take out the pieces of the path.
     */
    int parseDisplayPath(HttpServletRequest req, ParameterParser params)
    {

        String path = params.getPath();
        if (getLogger().isDebugEnabled())
        { 
            getLogger().debug("pDP: path: [" + path + "]");
        }

        // make some thing sensibile (out of whole cloth if necessary).
        if (path == null)
        {
            path = "/";
        }
        if ("".equals(path))
        {
            path = "/";
        }

        path = normalize(path);
        //        if (Logger.isDebugEnabled()) {
        //            Logger.debug("pDP: normalized path: ["+path+"]");
        //        }
        // Describe the level of the portal to display.
        // 0 is default value. In general will choose to see more context
        // rather than less if there is a choice to be made.
        displayLevel = Integer.MAX_VALUE;

        try
        {
            // split up the elements
            String[] parts = StringUtil.split(path, "/");
            // the starting offset to look at the array. We
            // can look at the first element to see if there is
            // something special to either ignore or to
            // the environment. E.g. ignore varuna and use
            // 'direct' to indicate the proper display format.
            int off = 0;
            String useId = null;

            // treat an empty path as /portal
            if (path.length() == 0)
            {
                if (1 < displayLevel)
                {
                    displayLevel = 1;
                    setDisplayPrefix("/portal");
                }
                setPathPortalId("/portal");
            }

            // for all the elements of the path
            for (int i = 1; i < parts.length; i++)
            {
                // get a 'more' boolean so intent of checks below are clearer
                // (and perhaps the code more efficient).
                //                    
                useId = "";

                if ("portal".equalsIgnoreCase(parts[i]))
                {
                    if (1 < displayLevel)
                    {
                        displayLevel = 1;
                        setDisplayPrefix("/portal");
                    }
                    setPathPortalId("/portal");
                }
                else if ("gallery".equalsIgnoreCase(parts[i]))
                {
                    if (2 < displayLevel)
                    {
                        displayLevel = 2;
                        setDisplayPrefix("/gallery");
                    }
                    setPathGalleryId("/gallery");
                }
                else if ("site".equalsIgnoreCase(parts[i])
                        || "group".equalsIgnoreCase(parts[i]))
                {
                    if (3 < displayLevel)
                    {
                        displayLevel = 3;
                        // use a null prefix you just recognized that
                        // the prefix is already in the url and will
                        // automatically
                        // be generated.
                        setDisplayPrefix("");
                    }
                    if ((i + 1 < parts.length)
                            && (!"page".equals(parts[i + 1])))
                    {
                        useId = parts[i + 1];
                        i++;
                    }
                    setPathSiteId(useId);
                }
                else if ("page".equalsIgnoreCase(parts[i])
                //                      || "js_pane".equalsIgnoreCase(parts[i])
                )
                {
                    if (4 < displayLevel)
                    {
                        displayLevel = 4;
                        // use a null prefix you just recognized that
                        // the prefix is already in the url and will
                        // automatically
                        // be generated.
                        setDisplayPrefix("");
                    }

                    if ((i + 1 < parts.length)
                            && (!"tool".equals(parts[i + 1])))
                    {
                        useId = parts[i + 1];
                        i++;
                    }
                    setPathPageId(useId);
                }
                else if ("tool".equalsIgnoreCase(parts[i]))
                {
                    if (5 < displayLevel)
                    {
                        displayLevel = 5;
                        // use a null prefix you just recognized that
                        // the prefix is already in the url and will
                        // automatically
                        // be generated.
                        setDisplayPrefix("");
                    }
                    if (i + 1 < parts.length)
                    {
                        useId = parts[i + 1];
                        i++;
                    }
                    setPathToolId(useId);
                }
                else
                {
                    // If we can't make any sense of it
                    // it is an exception.
                    throw new Exception("url has unknown element: [" + parts[i]
                            + "]");
                }
            } // for
        }
        catch (Throwable t)
        {
            getLogger().warn("error parsing url: path: [" + path + "]");
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("url parsing error", t);
            }
            displayLevel = 1;
            setPathSiteId(errorSiteId);
        } // end try

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("pDP: getPathSite: [" + getPathSiteId() + "]");
            getLogger().debug("pDP: getPathPage: [" + getPathPageId() + "]");
            getLogger().debug("pDP: getPathTool: [" + getPathToolId() + "]");
        }

        return displayLevel;
    }

}