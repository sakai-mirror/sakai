<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xalan="http://xml.apache.org/xalan">
    <xsl:import href="../result/identity.xsl"/>
    <xsl:param name="taken_assessment"/>
    <xsl:output doctype-system="http://www.imsglobal.org/question/qtiv1p2p1/XMLSchemav1p2p1/xmla/ims_qtiresv1p2p1schema/dtds/qtiresfullncdtd/ims_qtiresv1p2p1.dtd"/>
    <xsl:template match="item_result">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="response"/>
            <xsl:variable name="item_ident" select="@ident_ref"/>
            <xsl:apply-templates select="$taken_assessment//item[@ident=$item_ident]/resprocessing/respcondition[1]">
                <xsl:with-param name="item_result" select="."/>
                <xsl:with-param name="SCORE" select="0"/>
                <xsl:with-param name="previous_condition" select="false()"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="extension_item_result"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="respcondition">
        <xsl:param name="item_result"/>
        <xsl:param name="SCORE"/>
        <xsl:param name="previous_condition"/>
        <!--<xsl:param name="previous_feedback"/>-->
        <xsl:choose>
            <xsl:when test="$previous_condition=false() or ($previous_condition=true() and @continue='Yes')">
                <xsl:variable name="condition">
                    <xsl:apply-templates select="conditionvar">
                        <xsl:with-param name="item_result" select="$item_result"/>
                    </xsl:apply-templates>
                </xsl:variable>
                <xsl:variable name="condition_result" select="xalan:evaluate($condition)"/>
                <xsl:variable name="score_value">
                    <xsl:choose>
                        <xsl:when test="$condition_result=true() and ./setvar">
                            <xsl:apply-templates select="setvar[@varname='SCORE']"/>
                        </xsl:when>
                        <xsl:otherwise>$SCORE</xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <!--<xsl:variable name="feedback">
                    <xsl:value-of select="$previous_feedback"/>
                    <xsl:if test="$condition_result=true()">
                        <xsl:copy-of select="displayfeedback"/>
                    </xsl:if>
                </xsl:variable>-->
                <xsl:choose>
                    <xsl:when test="following-sibling::respcondition">
                        <xsl:apply-templates select="following-sibling::respcondition[1]">
                            <xsl:with-param name="item_result" select="$item_result"/>
                            <xsl:with-param name="SCORE" select="xalan:evaluate($score_value)"/>
                            <xsl:with-param name="previous_condition" select="$condition_result"/>
                            <!--<xsl:with-param name="previous_feedback" select="$feedback"/>-->
                        </xsl:apply-templates>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="ancestor::resprocessing/outcomes">
                            <xsl:with-param name="SCORE" select="xalan:evaluate($score_value)"/>
                            <!--<xsl:with-param name="displayed_feedback" select="$feedback"/>-->
                        </xsl:apply-templates>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="$condition_result=true()">
                    <xsl:apply-templates select="displayfeedback"/>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="ancestor::resprocessing/outcomes">
                    <xsl:with-param name="SCORE" select="$SCORE"/>
                    <!--<xsl:with-param name="displayed_feedback" select="$previous_feedback"/>-->
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="item/resprocessing/outcomes">
        <xsl:param name="SCORE"/>
        <!--<xsl:param name="displayed_feedback"/>-->
        <outcomes>
            <score varname="SCORE">
                <score_value>
                    <xsl:value-of select="$SCORE"/>
                </score_value>
                <score_max>
                    <xsl:value-of select="decvar[@varname='SCORE']/@maxvalue"/>
                </score_max>
            </score>
        </outcomes>
    </xsl:template>
    <xsl:template match="displayfeedback">
        <feedback_displayed>
            <xsl:attribute name="ident_ref">
                <xsl:value-of select="@linkrefid"/>
            </xsl:attribute>
        </feedback_displayed>
    </xsl:template>
    <xsl:template match="conditionvar">
        <xsl:param name="item_result"/>
        <xsl:apply-templates select="and | or | not | varequal | other">
            <xsl:with-param name="item_result" select="$item_result"/>
        </xsl:apply-templates>
    </xsl:template>
    <xsl:template match="and">
        <xsl:param name="item_result"/>
        <xsl:text>(</xsl:text>
        <xsl:for-each select="and | or | not | varequal | other">
            <xsl:apply-templates select=".">
                <xsl:with-param name="item_result" select="$item_result"/>
            </xsl:apply-templates>
            <xsl:if test="following-sibling::and or following-sibling::or or following-sibling::not or following-sibling::varequal">
                <xsl:text> and </xsl:text>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>)</xsl:text>
    </xsl:template>
    <xsl:template match="or">
        <xsl:param name="item_result"/>
        <xsl:text>(</xsl:text>
        <xsl:for-each select="and | or | not | varequal | other">
            <xsl:apply-templates select=".">
                <xsl:with-param name="item_result" select="$item_result"/>
            </xsl:apply-templates>
            <xsl:if test="following-sibling::and or following-sibling::or or following-sibling::not or following-sibling::varequal">
                <xsl:text> or </xsl:text>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>)</xsl:text>
    </xsl:template>
    <xsl:template match="not">
        <xsl:param name="item_result"/>
        <xsl:text>not(</xsl:text>
        <xsl:for-each select="and | or | not | varequal | other">
            <xsl:apply-templates select=".">
                <xsl:with-param name="item_result" select="$item_result"/>
            </xsl:apply-templates>
        </xsl:for-each>
        <xsl:text>)</xsl:text>
    </xsl:template>
    <xsl:template match="varequal">
        <xsl:param name="item_result"/>
        <xsl:variable name="response_ident" select="@respident"/>
        <xsl:variable name="response" select="$item_result/response[@ident_ref=$response_ident]"/>
        <xsl:variable name="case" select="@case"/>
        <xsl:variable name="response_values">
            <xsl:for-each select="$response/response_value">
                <xsl:copy>
                    <xsl:choose>
                        <xsl:when test="$case='No'">
                            <xsl:value-of select="translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="normalize-space(.)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:copy>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="correct_response">
            <xsl:choose>
                <xsl:when test="$case='No'">
                    <xsl:value-of select="translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="is_correct">
            <xsl:choose>
                <!--Ordered Response-->
                <xsl:when test="@index">
                    <xsl:variable name="index" select="@index"/>
                    <xsl:value-of select="xalan:nodeset($response_values)/response_value[position()=$index]=$correct_response"/>
                </xsl:when>
                <!--Single & Multiple Responses-->
                <xsl:otherwise>
                    <xsl:value-of select="xalan:nodeset($response_values)/response_value=$correct_response"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$is_correct='true'">true()</xsl:when>
            <xsl:otherwise>false()</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="other">true()</xsl:template>
    <xsl:template match="setvar">
        <xsl:text>$SCORE</xsl:text>
        <xsl:choose>
            <xsl:when test="@action='Add'"> + </xsl:when>
            <xsl:when test="@action='Subtract'"> - </xsl:when>
            <xsl:when test="@action='Multiply'"> * </xsl:when>
            <xsl:when test="@action='Divide'"> div </xsl:when>
        </xsl:choose>
        <xsl:value-of select="."/>
    </xsl:template>
</xsl:stylesheet>
