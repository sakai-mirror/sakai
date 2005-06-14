<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="../result/identity.xsl"/>
    <xsl:param name="item_id"/>
    <xsl:param name="response_id"/>
    <xsl:param name="value"/>
    <xsl:template match="item_result[@ident_ref=$item_id]/response[@ident_ref=$response_id]">
        <xsl:copy>
            <xsl:apply-templates/>
            <response_value>
                <xsl:attribute name="response_status">Valid</xsl:attribute>
                <xsl:value-of select="$value"/>
            </response_value>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
