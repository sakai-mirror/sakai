<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <xsl:element name="xsl:stylesheet">
            <xsl:attribute name="version">1.0</xsl:attribute>
            <xsl:apply-templates select="//item"/>
            <xsl:element name="xsl:template">
                <xsl:attribute name="match">node() | @*</xsl:attribute>
                <xsl:element name="xsl:copy">
                    <xsl:element name="xsl:apply-templates">
                        <xsl:attribute name="select">node() | @*</xsl:attribute>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="questestinterop">
        <xsl:element name="xsl:template">
            <xsl:attribute name="match">qti_result_report</xsl:attribute>
            <xsl:element name="xsl:element">
                <xsl:attribute name="name">qti_result_report</xsl:attribute>
                <xsl:element name="xsl:copy-of">
                    <xsl:attribute name="select">qticomment</xsl:attribute>
                </xsl:element>
                <xsl:element name="xsl:apply-templates">
                    <xsl:attribute name="select">result</xsl:attribute>
                </xsl:element>
            </xsl:element>
        </xsl:element>
        <xsl:call-template name="result"/>
        <xsl:apply-templates select="assessment"/>
    </xsl:template>
    <xsl:template name="result">
        <xsl:element name="xsl:template">
            <xsl:attribute name="match">result</xsl:attribute>
            <xsl:element name="xsl:element">
                <xsl:attribute name="name">result</xsl:attribute>
                <xsl:element name="xsl:copy-of">
                    <xsl:attribute name="select">qticomment</xsl:attribute>
                </xsl:element>
                <xsl:element name="xsl:copy-of">
                    <xsl:attribute name="select">context</xsl:attribute>
                </xsl:element>
                <xsl:element name="xsl:apply-templates">
                    <xsl:attribute name="select">summary_result | assessment_result | section_result | item_result</xsl:attribute>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="assessment">
        <xsl:element name="xsl:template">
            <xsl:attribute name="match">assessment_result</xsl:attribute>
            <xsl:element name="xsl:element">
                <xsl:attribute name="name">assessment_result</xsl:attribute>
                <xsl:element name="xsl:copy-of">
                    <xsl:attribute name="select">attribute::*</xsl:attribute>
                </xsl:element>
                <xsl:element name="xsl:apply-templates">
                    <xsl:attribute name="select">section_result</xsl:attribute>
                </xsl:element>
                <xsl:element name="xsl:apply-templates">
                    <xsl:attribute name="select">item_result</xsl:attribute>
                </xsl:element>
            </xsl:element>
        </xsl:element>
        <xsl:apply-templates select="section"/>
    </xsl:template>
    <xsl:template match="section">
        <xsl:element name="xsl:template">
            <xsl:attribute name="match">section_result[@ident_ref='<xsl:value-of select="@ident"/>']</xsl:attribute>
            <xsl:element name="xsl:element">
                <xsl:attribute name="name">section_result</xsl:attribute>
                <xsl:element name="xsl:copy-of">
                    <xsl:attribute name="select">attribute::*</xsl:attribute>
                </xsl:element>
                <xsl:element name="xsl:apply-templates">
                    <xsl:attribute name="select">section_result | item_result</xsl:attribute>
                </xsl:element>
            </xsl:element>
        </xsl:element>
        <xsl:apply-templates select="section | item"/>
    </xsl:template>
    <xsl:template match="item">
        <xsl:element name="xsl:template">
            <xsl:attribute name="match">item_result[@ident_ref='<xsl:value-of select="@ident"/>']</xsl:attribute>
            <xsl:element name="xsl:copy">
                <xsl:element name="xsl:copy-of">
                    <xsl:attribute name="select">@*</xsl:attribute>
                </xsl:element>
                <xsl:element name="xsl:apply-templates">
                    <xsl:attribute name="select">response</xsl:attribute>
                </xsl:element>
                <xsl:apply-templates select="resprocessing"/>
                <xsl:element name="xsl:apply-templates">
                    <xsl:attribute name="select">extension_item_result</xsl:attribute>
                </xsl:element>
            </xsl:element>
        </xsl:element>
        <xsl:apply-templates select="resprocessing/respcondition">
            <xsl:with-param name="item_ident" select="@ident"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="resprocessing/outcomes">
            <xsl:with-param name="item_ident" select="@ident"/>
        </xsl:apply-templates>
    </xsl:template>
    <xsl:template match="response_grp">
        <xsl:element name="xsl:template">
            <xsl:attribute name="match">response[@ident_ref='<xsl:value-of select="@ident"/>']</xsl:attribute>
            <xsl:element name="xsl:element">
                <xsl:attribute name="name">response</xsl:attribute>
                <xsl:element name="xsl:copy-of">
                    <xsl:attribute name="select">attribute::*</xsl:attribute>
                </xsl:element>
                <xsl:element name="xsl:element">
                    <xsl:attribute name="name">response_form</xsl:attribute>
                    <xsl:element name="xsl:attribute">
                        <xsl:attribute name="name">response_type</xsl:attribute>
                        <xsl:text>grp</xsl:text>
                    </xsl:element>
                    <xsl:element name="xsl:attribute">
                        <xsl:attribute name="name">render_type</xsl:attribute>
                        <xsl:text>choice</xsl:text>
                    </xsl:element>
                    <xsl:apply-templates select="@rcardinality"/>
                    <xsl:apply-templates
                        select="ancestor::item/resprocessing/respcondition[setvar/@action='Add']/conditionvar/varequal" mode="correct_response"/>
                </xsl:element>
                <xsl:element name="xsl:copy-of">
                    <xsl:attribute name="select">response_value</xsl:attribute>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="response_lid">
        <xsl:element name="xsl:template">
            <xsl:attribute name="match">response[@ident_ref='<xsl:value-of select="@ident"/>']</xsl:attribute>
            <xsl:element name="xsl:element">
                <xsl:attribute name="name">response</xsl:attribute>
                <xsl:element name="xsl:copy-of">
                    <xsl:attribute name="select">attribute::*</xsl:attribute>
                </xsl:element>
                <xsl:element name="xsl:element">
                    <xsl:attribute name="name">response_form</xsl:attribute>
                    <xsl:element name="xsl:attribute">
                        <xsl:attribute name="name">response_type</xsl:attribute>
                        <xsl:text>lid</xsl:text>
                    </xsl:element>
                    <xsl:element name="xsl:attribute">
                        <xsl:attribute name="name">render_type</xsl:attribute>
                        <xsl:text>choice</xsl:text>
                    </xsl:element>
                    <xsl:apply-templates select="@rcardinality"/>
                    <xsl:apply-templates
                        select="ancestor::item/resprocessing/respcondition[setvar/@action='Add' and setvar&gt;'0']/conditionvar/varequal" mode="correct_response"/>
                </xsl:element>
                <xsl:element name="xsl:copy-of">
                    <xsl:attribute name="select">response_value</xsl:attribute>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="response_str">
        <xsl:element name="xsl:template">
            <xsl:attribute name="match">response[@ident_ref='<xsl:value-of select="@ident"/>']</xsl:attribute>
            <xsl:element name="xsl:element">
                <xsl:attribute name="name">response</xsl:attribute>
                <xsl:element name="xsl:copy-of">
                    <xsl:attribute name="select">attribute::*</xsl:attribute>
                </xsl:element>
                <xsl:element name="xsl:element">
                    <xsl:attribute name="name">response_form</xsl:attribute>
                    <xsl:element name="xsl:attribute">
                        <xsl:attribute name="name">response_type</xsl:attribute>
                        <xsl:text>str</xsl:text>
                    </xsl:element>
                    <xsl:element name="xsl:attribute">
                        <xsl:attribute name="name">render_type</xsl:attribute>
                        <xsl:text>fib</xsl:text>
                    </xsl:element>
                    <xsl:apply-templates select="@rcardinality"/>
                    <xsl:apply-templates
                        select="ancestor::item/resprocessing/respcondition[setvar/attribute::action='Add' and setvar&gt;'0']/conditionvar/varequal" mode="correct_response"/>
                </xsl:element>
                <xsl:element name="xsl:copy-of">
                    <xsl:attribute name="select">response_value</xsl:attribute>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="@rcardinality">
        <xsl:element name="xsl:attribute">
            <xsl:attribute name="name">cardinality</xsl:attribute>
            <xsl:value-of select="current()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="varequal" mode="correct_response">
        <xsl:element name="xsl:element">
            <xsl:attribute name="name">correct_response</xsl:attribute>
            <xsl:value-of select="current()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="resprocessing">
        <xsl:element name="xsl:apply-templates">
            <xsl:attribute name="select">current()</xsl:attribute>
            <xsl:attribute name="mode">respcondition1</xsl:attribute>
            <xsl:element name="xsl:with-param">
                <xsl:attribute name="name">SCORE</xsl:attribute>
                <xsl:value-of select="0"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="outcomes">
        <xsl:param name="item_ident"/>
        <xsl:element name="xsl:template">
            <xsl:attribute name="match">item_result[@ident_ref='<xsl:value-of select="$item_ident"/>']</xsl:attribute>
            <xsl:attribute name="mode">outcomes</xsl:attribute>
            <xsl:apply-templates mode="param" select="decvar"/>
            <xsl:element name="xsl:element">
                <xsl:attribute name="name">outcomes</xsl:attribute>
                <xsl:apply-templates mode="score" select="decvar"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="decvar" mode="param">
        <xsl:element name="xsl:param">
            <xsl:attribute name="name">
                <xsl:value-of select="@varname"/>
            </xsl:attribute>
        </xsl:element>
    </xsl:template>
    <xsl:template match="decvar" mode="with-param">
        <xsl:element name="xsl:with-param">
            <xsl:attribute name="name">
                <xsl:value-of select="@varname"/>
            </xsl:attribute>
            <xsl:attribute name="select">$<xsl:value-of select="@varname"/>
            </xsl:attribute>
        </xsl:element>
    </xsl:template>
    <xsl:template match="decvar" mode="score">
        <xsl:element name="xsl:element">
            <xsl:attribute name="name">score</xsl:attribute>
            <xsl:element name="xsl:attribute">
                <xsl:attribute name="name">varname</xsl:attribute>
                <xsl:value-of select="@varname"/>
            </xsl:element>
            <xsl:element name="xsl:element">
                <xsl:attribute name="name">score_value</xsl:attribute>
                <xsl:element name="xsl:value-of">
                    <xsl:attribute name="select">$<xsl:value-of select="@varname"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:element>
            <xsl:element name="xsl:element">
                <xsl:attribute name="name">score_max</xsl:attribute>
                <xsl:value-of select="@maxvalue"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="respcondition">
        <xsl:param name="item_ident"/>
        <xsl:element name="xsl:template">
            <xsl:attribute name="match">item_result[@ident_ref='<xsl:value-of select="$item_ident"/>']</xsl:attribute>
            <xsl:attribute name="mode">respcondition<xsl:value-of select="position()"/>
            </xsl:attribute>
            <xsl:apply-templates mode="param" select="../outcomes/decvar"/>
            <xsl:element name="xsl:choose">
                <xsl:element name="xsl:when">
                    <xsl:attribute name="test">
                        <xsl:apply-templates select="conditionvar/and"/>
                        <xsl:apply-templates select="conditionvar/or"/>
                        <xsl:apply-templates select="conditionvar/not"/>
                        <xsl:apply-templates select="conditionvar/varequal"/>
                        <xsl:apply-templates select="conditionvar/other"/>
                    </xsl:attribute>
                    <xsl:element name="xsl:apply-templates">
                        <xsl:attribute name="select">current()</xsl:attribute>
                        <xsl:attribute name="mode">
                            <xsl:choose>
                                <xsl:when
                                        test="following-sibling::respcondition[1]/@continue = 'Yes'">respcondition<xsl:value-of select="position() + 1"/>
                                </xsl:when>
                                <xsl:otherwise>outcomes</xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:apply-templates select="setvar"/>
                    </xsl:element>
                    <xsl:apply-templates select="displayfeedback"/>
                </xsl:element>
                <xsl:element name="xsl:otherwise">
                    <xsl:element name="xsl:apply-templates">
                        <xsl:attribute name="select">current()</xsl:attribute>
                        <xsl:attribute name="mode">
                            <xsl:choose>
                                <xsl:when
                                        test="count(following-sibling::respcondition) &gt; 0">respcondition<xsl:value-of select="position() + 1"/>
                                </xsl:when>
                                <xsl:otherwise>outcomes</xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:apply-templates mode="with-param" select="ancestor::resprocessing/outcomes/decvar"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="displayfeedback">
        <xsl:element name="xsl:element">
            <xsl:attribute name="name">feedback_displayed</xsl:attribute>
            <xsl:element name="xsl:attribute">
                <xsl:attribute name="name">ident_ref</xsl:attribute>
                <xsl:value-of select="@linkrefid"/>
            </xsl:element>
            <xsl:element name="xsl:attribute">
                <xsl:attribute name="name">asi_title</xsl:attribute>
            </xsl:element>
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="and">
        <xsl:text>(</xsl:text>
        <xsl:for-each select="and | or | not | varequal">
            <xsl:apply-templates select="."/>
            <xsl:if test="count(following-sibling::not) + count(following-sibling::and) + count(following-sibling::or) + count(following-sibling::varequal) != 0">
                <xsl:text> and </xsl:text>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>)</xsl:text>
    </xsl:template>
    <xsl:template match="or">
        <xsl:text>(</xsl:text>
        <xsl:for-each select="and | or | not | varequal">
            <xsl:apply-templates select="."/>
            <xsl:if test="count(following-sibling::not) + count(following-sibling::and) + count(following-sibling::or) + count(following-sibling::varequal) != 0">
                <xsl:text> or </xsl:text>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>)</xsl:text>
    </xsl:template>
    <xsl:template match="not">
        <xsl:text>not(</xsl:text>
        <xsl:for-each select="and | or | not | varequal">
            <xsl:apply-templates select="."/>
        </xsl:for-each>
        <xsl:text>)</xsl:text>
    </xsl:template>
    <xsl:template match="other">
        <xsl:text>true</xsl:text>
    </xsl:template>
    <xsl:template match="varequal">
        <xsl:choose>
            <xsl:when test="@case = 'No'">
                <xsl:text>(translate(response[@ident_ref='</xsl:text>
                <xsl:value-of select="@respident"/>
                <xsl:text>']/response_value, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ') ='</xsl:text>
                <xsl:value-of select="normalize-space(translate(text(), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'))"/>
                <xsl:text>')</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>(response[@ident_ref='</xsl:text>
                <xsl:value-of select="@respident"/>
                <xsl:text>']/response_value</xsl:text>
                <xsl:if test="ancestor::item//@rcardinality = 'Ordered'">[<xsl:value-of select="@index"/>]</xsl:if>
                <xsl:text>='</xsl:text>
                <xsl:value-of select="normalize-space(text())"/>
                <xsl:text>')</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="varlt">
        <xsl:text>(//response[@ident_ref='</xsl:text>
        <xsl:value-of select="@respident"/>
        <xsl:text>']/response_value &lt;'</xsl:text>
        <xsl:value-of select="text()"/>
        <xsl:text>')</xsl:text>
    </xsl:template>
    <xsl:template match="varlte">
        <xsl:text>(//response[@ident_ref='</xsl:text>
        <xsl:value-of select="@respident"/>
        <xsl:text>']/response_value &lt;='</xsl:text>
        <xsl:value-of select="text()"/>
        <xsl:text>')</xsl:text>
    </xsl:template>
    <xsl:template match="vargt">
        <xsl:text>(//response[@ident_ref='</xsl:text>
        <xsl:value-of select="@respident"/>
        <xsl:text>']/response_value &gt;'</xsl:text>
        <xsl:value-of select="text()"/>
        <xsl:text>')</xsl:text>
    </xsl:template>
    <xsl:template match="vargte">
        <xsl:text>(//response[@ident_ref='</xsl:text>
        <xsl:value-of select="@respident"/>
        <xsl:text>']/response_value &gt;='</xsl:text>
        <xsl:value-of select="text()"/>
        <xsl:text>')</xsl:text>
    </xsl:template>
    <xsl:template match="setvar">
        <xsl:element name="xsl:with-param">
            <xsl:attribute name="name">
                <xsl:value-of select="@varname"/>
            </xsl:attribute>
            <xsl:element name="xsl:value-of">
                <xsl:attribute name="select">
                    <xsl:choose>
                        <xsl:when test="@action='Add'">
                            <xsl:text>$</xsl:text>
                            <xsl:value-of select="@varname"/>
                            <xsl:text> + </xsl:text>
                            <xsl:value-of select="text()"/>
                        </xsl:when>
                        <xsl:when test="@action='Subtract'">
                            <xsl:text>$</xsl:text>
                            <xsl:value-of select="@varname"/>
                            <xsl:text> - </xsl:text>
                            <xsl:value-of select="text()"/>
                        </xsl:when>
                        <xsl:when test="@action='Multiply'">
                            <xsl:text>$</xsl:text>
                            <xsl:value-of select="@varname"/>
                            <xsl:text> * </xsl:text>
                            <xsl:value-of select="text()"/>
                        </xsl:when>
                        <xsl:when test="@action='Divide'">
                            <xsl:text>$</xsl:text>
                            <xsl:value-of select="@varname"/>
                            <xsl:text> div </xsl:text>
                            <xsl:value-of select="text()"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="0"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
            </xsl:element>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
