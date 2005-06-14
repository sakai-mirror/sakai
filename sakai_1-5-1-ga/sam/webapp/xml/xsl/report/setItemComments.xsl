<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="../result/identity.xsl"/>
    <xsl:param name="item_id"/>
    <xsl:param name="comments"/>
    <xsl:template match="item_result[@ident_ref=$item_id]/extension_item_result">
        <xsl:copy>
            <xsl:comment>
                <xsl:value-of select="$comments"/>
            </xsl:comment>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
