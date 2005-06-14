<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns:xalan="http://xml.apache.org/xalan" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:param name="name"/>
    <xsl:param name="userId"/>
    <xsl:param name="section_report"/>
    <xsl:variable name="section_results" select="xalan:nodeset($section_report)"/>
    <xsl:param name="item_report"/>
    <xsl:variable name="item_results" select="xalan:nodeset($item_report)"/>
    <xsl:template match="/questestinterop">
        <qti_result_report>
            <result>
                <context>
                    <name>
                        <xsl:value-of select="$name"/>
                    </name>
                    <generic_identifier>
                        <type_label>userId</type_label>
                        <identifier_string>
                            <xsl:value-of select="$userId"/>
                        </identifier_string>
                    </generic_identifier>
                </context>
                <xsl:apply-templates select="assessment"/>
            </result>
        </qti_result_report>
    </xsl:template>
    <xsl:template match="assessment">
        <xsl:variable name="assessment_ident" select="@ident"/>
        <assessment_result>
            <xsl:attribute name="ident_ref">
                <xsl:value-of select="$assessment_ident"/>
            </xsl:attribute>
            <xsl:apply-templates select="section"/>
            <extenstion_assessment_result>
                <xsl:comment>Assessment Comments</xsl:comment>
            </extenstion_assessment_result>
        </assessment_result>
    </xsl:template>
    <xsl:template match="section">
        <xsl:variable name="section_ident" select="@ident"/>
        <section_result>
            <xsl:attribute name="ident_ref">
                <xsl:value-of select="$section_ident"/>
            </xsl:attribute>
            <xsl:apply-templates select="item"/>
            <extenstion_section_result>
                <xsl:comment>Part Comments</xsl:comment>
            </extenstion_section_result>
        </section_result>
    </xsl:template>
    <xsl:template match="item">
        <xsl:variable name="item_ident" select="@ident"/>
        <item_result>
            <xsl:attribute name="ident_ref">
                <xsl:value-of select="$item_ident"/>
            </xsl:attribute>
            <xsl:apply-templates select="//response_lid | //response_str | //response_grp">
                <xsl:with-param name="item" select="current()"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="resprocessing/outcomes">
                <xsl:with-param name="item" select="current()"/>
            </xsl:apply-templates>
            <xsl:variable name="item_comments" select="$item_results//item_result[@ident_ref=$item_ident]/extenstion_item_result"/>
            <xsl:choose>
                <xsl:when test="$item_comments">
                    <xsl:copy-of select="$item_comments"/>
                </xsl:when>
                <xsl:otherwise>
                    <extension_item_result>
                        <xsl:comment>Item Comments</xsl:comment>
                    </extension_item_result>
                </xsl:otherwise>
            </xsl:choose>
        </item_result>
    </xsl:template>
    <xsl:template match="response_lid | response_str | response_grp">
        <xsl:param name="item"/>
        <xsl:variable name="item_ident" select="$item/@ident"/>
        <xsl:variable name="response_ident" select="@ident"/>
        <xsl:variable name="response" select="$item_results//item_result[@ident_ref=$item_ident]/response[@ident=$response_ident]"/>
        <xsl:choose>
            <xsl:when test="$response">
                <xsl:copy-of select="$response"/>
            </xsl:when>
            <xsl:otherwise>
                <response>
                    <xsl:attribute name="ident_ref">
                        <xsl:value-of select="$response_ident"/>
                    </xsl:attribute>
                    <num_attempts>0</num_attempts>
                </response>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="outcomes">
        <xsl:param name="item"/>
        <xsl:variable name="item_ident" select="$item/@ident"/>
        <xsl:variable name="outcomes" select="$item_results//item_result[@ident_ref=$item_ident]/outcomes"/>
        <xsl:choose>
            <xsl:when test="$outcomes">
                <xsl:copy-of select="$outcomes"/>
            </xsl:when>
            <xsl:otherwise>
                <outcomes>
                    <xsl:apply-templates select="decvar"/>
                </outcomes>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="decvar">
        <score>
            <xsl:attribute name="varname">
                <xsl:value-of select="@varname"/>
            </xsl:attribute>
            <score_value>
                <xsl:value-of select="@defaultval"/>
            </score_value>
            <score_max>
                <xsl:value-of select="@maxvalue"/>
            </score_max>
        </score>
    </xsl:template>
</xsl:stylesheet>
