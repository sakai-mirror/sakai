<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan" version="1.0">
    <xsl:import href="../result/identity.xsl"/>
    <xsl:param name="section_id"/>
    <xsl:param name="section_result"/>
    <xsl:template match="extension_section_result[parent::section_result/@ident_ref=$section_id]">
        <xsl:copy-of select="$section_result/extension_section_result"/>
    </xsl:template>
</xsl:stylesheet>
