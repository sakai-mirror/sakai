<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:template match="/">
        <html>
            <head></head>
            <body><ul>
                <xsl:apply-templates select="stxx/form/selectAssessmentForm/reviewAssessments/value"/>
            </ul></body>
        </html>
    </xsl:template>
    <xsl:template match="value">
        <xsl:variable name="ident" select="ident"/>
        <li><xsl:value-of select="title"/>/<a href="/sam-stg/asi/grading/xmlSelectAction.do?assessmentId={$ident}">grade</a>/<a href="/sam-stg/asi/grading/xmlSelectAction.do?assessmentId={$ident}&amp;debug=true">debug</a></li>
    </xsl:template>
</xsl:stylesheet>