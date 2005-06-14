<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="../result/identity.xsl"/>
    <xsl:template match="assessment_result">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="asi_metadata"/>
            <outcomes>
                <score>
                    <score_value>
                        <xsl:value-of select="sum(section_result/outcomes/score/score_value)"/>
                    </score_value>
                    <score_max>
                        <xsl:value-of select="sum(section_result/outcomes/score/score_max)"/>
                    </score_max>
                </score>
            </outcomes>
            <xsl:apply-templates select="section_result"/>
            <xsl:apply-templates select="extension_assessment_result"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>