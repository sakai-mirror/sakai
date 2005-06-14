<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="../result/identity.xsl"/>
    <xsl:param name="item_id"></xsl:param>
    <xsl:param name="score"/>
    <xsl:template match="item_result[@ident_ref=$item_id]/outcomes/score[@varname='SCORE']/score_value">
        <xsl:copy>
            <xsl:value-of select="$score"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
