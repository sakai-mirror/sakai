<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="../result/identity.xsl"/>
    <xsl:param name="assessment_id"/>
    <xsl:param name="comments"/>
    <xsl:template match="assessment_result[@ident_ref=$assessment_id]/extension_assessment_result">
        <xsl:copy>
            <xsl:comment>
                <xsl:value-of select="$comments"/>
            </xsl:comment>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
