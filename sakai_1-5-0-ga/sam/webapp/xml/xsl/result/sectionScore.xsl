<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="../result/identity.xsl"/>
    <xsl:template match="section_result">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <outcomes>
                <score>
                    <score_value>
                        <xsl:value-of select="sum(item_result/outcomes/score/score_value)"/>
                    </score_value>
                    <score_max>
                        <xsl:value-of select="sum(item_result/outcomes/score/score_max)"/>
                    </score_max>
                </score>
            </outcomes>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
 
