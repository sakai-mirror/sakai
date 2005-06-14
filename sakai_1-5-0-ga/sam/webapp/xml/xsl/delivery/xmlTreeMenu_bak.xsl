<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:import href="../layout/header.xsl" />
  <xsl:output method="html"/>
  <xsl:template match="/">
 <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
    <html>
      <head>
        <title>XML Tree Control</title>
        <link rel="stylesheet" type="text/css" href="xmlTree.css"/>
        <script type="text/javascript" src="{$base}js/xmlTree.js"/>
      </head>
      <xsl:apply-templates/>
    </html>
  </xsl:template>
  <xsl:template match="assessment">
    <body> Assessment: <xsl:value-of select="@title"/>
      <xsl:apply-templates select="section"/>
    </body>
  </xsl:template>
  <xsl:template match="section">
    <span class="trigger">
      <xsl:attribute name="onClick"> showBranch ('<xsl:value-of select="@id"/>'); </xsl:attribute>
      <img src="closed.gif">
        <xsl:attribute name="id">I <xsl:value-of select="@ident"/>
        </xsl:attribute>
      </img>
      <xsl:value-of select="@title"/>
      <br/>
    </span>
    <span class="section">
      <xsl:attribute name="id">
        <xsl:value-of select="@ident"/>
      </xsl:attribute>
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  <xsl:template match="item">
    <img src="doc.gif"/>
    <a>
      <xsl:attribute name="href">
        <xsl:value-of select="link"/>
      </xsl:attribute>
      <xsl:value-of select="@title"/>
    </a>
    <br/>
  </xsl:template>
</xsl:stylesheet>
