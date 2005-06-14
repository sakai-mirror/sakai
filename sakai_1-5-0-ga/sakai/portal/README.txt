This is a replacement for the Sedna dispatcher.  It will recognize the partial urls 
and will allow pointing to particular sites, pages, or tools.
The URL levels recognized are:
/portal - Branding, site nav and all the rest
/gallery - Not branding, but the rest.
/site - just site and below, no sitenave
/page - page and tools
/tool - a single tool.

To activate use this varunaweb.xml as the dispatch web.xml and change 
sakai.properties so that skin.root is /sakai-portal/css/.  You can set
top.minimal=false to always suppress branding.
