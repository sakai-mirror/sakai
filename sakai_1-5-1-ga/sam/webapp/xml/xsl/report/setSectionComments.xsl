<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="../result/identity.xsl"/>
    <xsl:param name="section_id"/>
    <xsl:param name="comments"/>
    <xsl:template match="section_result[@ident_ref=$section_id]/extension_section_result">
        <xsl:copy>
            <xsl:comment>
                <xsl:value-of select="$comments"/>
            </xsl:comment>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
