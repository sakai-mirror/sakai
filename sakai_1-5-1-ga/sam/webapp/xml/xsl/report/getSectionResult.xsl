<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="filter.xsl"/>
    <xsl:output omit-xml-declaration="yes"/>
    <xsl:param name="section_id"/>
    <xsl:template match="section_result[@ident_ref=$section_id]">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:copy-of select="extension_section_result"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
