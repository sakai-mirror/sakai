<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
-->
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="assessment.xsl" />
<xsl:import href="section.xsl" />
<xsl:import href="item.xsl" />
<xsl:import href="../layout/header.xsl" />
<xsl:import href="../layout/footer.xsl" />
<xsl:import href="../layout/menu.xsl" />
<xsl:import href="../layout/defaultLayout.xsl" />

<xsl:output method="html"/>

<!-- This template processes the root node ("/") -->
<xsl:template match="/">
 
    <xsl:variable name="base">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>


<frameset rows="100%,*">
   <frame name="mainFrame" src="{$base}asi/delivery/xmlAction.do?frame=autosave&amp;beginTest=beginTest" frameborder="0" >
<xsl:attribute name="noresize"/>
   </frame>
<!-- This is a dummy frame, the src can be anything -->
   <frame name="dummyFrame" src="{$base}Login.do" frameborder="0" >
   </frame>
</frameset>

</xsl:template>

</xsl:stylesheet>
