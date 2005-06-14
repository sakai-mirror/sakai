<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: item.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:preserve-space elements="flow"/>
    <xsl:output method="html"/>
    <!-- This template processes the root node ("/") -->
    <xsl:template match="/">
        <html>
            <head>
                <title>Navigo Assessment Item Display</title>
            </head>
            <body>
                <xsl:apply-templates select="stxx"/>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="stxx">
        <xsl:apply-templates select="item"/>
    </xsl:template>
    <xsl:template match="item">
        <xsl:variable name="itemIdentRef" select="@ident"/>
        <table align="center" bgcolor="#ccccff" border="0" class="border" height="30" width="90%">
            <tr>
                <td>
                    <b>
                        <xsl:value-of select="/stxx/applicationResources/key[@name='grading.question']"/>:<xsl:value-of
                            select="@continueNumber"/>/<xsl:value-of select="/stxx/form/xmlDeliveryActionForm/size"/>
                    </b>
                </td>
                <td align="center">
                    <b/>
                </td>
                <td align="right">
                    <input>
                        <xsl:attribute name="type">hidden</xsl:attribute>
                        <xsl:attribute name="name">itemIds</xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="$itemIdentRef"/>
                        </xsl:attribute>
                    </input>
                    <input>
                        <xsl:attribute name="type">hidden</xsl:attribute>
                        <xsl:attribute name="name"><xsl:value-of select="$itemIdentRef"/>.assessmentResultId</xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="/stxx/qti_result_report/result/assessment_result/@ident_ref"/>
                        </xsl:attribute>
                    </input>
                    <input>
                        <xsl:attribute name="type">text</xsl:attribute>
                        <xsl:attribute name="name">
                            <xsl:value-of select="$itemIdentRef"/>.score</xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="/stxx/qti_result_report/result//item_result[@ident_ref=$itemIdentRef]/outcomes/score/score_value"/>
                        </xsl:attribute>
                        <xsl:attribute name="size">2</xsl:attribute>
                    </input>
                    <b> / </b>
                    <xsl:if test="resprocessing/outcomes/decvar/@maxvalue != ''">
                        <b>
                            <xsl:value-of select="format-number(resprocessing/outcomes/decvar/@maxvalue, '0.00')"/>&#160;<xsl:value-of select="/stxx/applicationResources/key[@name='grading.points']"/>
                        </b>
                    </xsl:if>
                </td>
            </tr>
        </table>
        <br/>
        <table align="center" width="80%">
            <tr>
                <td rowspan="3" valign="top" width="10">
                    <b>
                        <xsl:value-of select="@displayIndex"/>
                    </b>
                </td>
                <td align="left" colspan="2" valign="top">
                    <p>
                        <xsl:apply-templates select="rubric"/>
                        <xsl:apply-templates select="presentation/flow | presentation"/>
                    </p>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <b>
                        <xsl:value-of select="/stxx/applicationResources/key[@name='grading.question.comments']"/>
                    </b>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:element name="textarea">
                        <xsl:attribute name="name">
                            <xsl:value-of select="$itemIdentRef"/>.comments</xsl:attribute>
                        <xsl:attribute name="id">
                            <xsl:value-of select="$itemIdentRef"/>.comments</xsl:attribute>
                        <xsl:attribute name="rows">10</xsl:attribute>
                        <xsl:attribute name="cols">80</xsl:attribute>
                        <xsl:value-of select="/stxx/qti_result_report/result//item_result[@ident_ref=$itemIdentRef]/extension_item_result/comment()"/>
                    </xsl:element>
                </td>
                <td align="left" valign="top">
                      <a>
                        <xsl:attribute name="href">javascript:toggleToolbar('<xsl:value-of select="$itemIdentRef"/>.comments')</xsl:attribute>
                        Show/Hide <br />Editor
                      </a>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="presentation">
        <xsl:apply-templates select="response_str |response_lid | response_grp | material"/>
    </xsl:template>
    <xsl:template match="rubric">
        <b>
            <xsl:value-of select="flow_mat/material"/>
        </b>
    </xsl:template>
    <xsl:template match="flow_mat">
        <xsl:apply-templates select="flow_mat | material"/>
    </xsl:template>
    <xsl:template match="flow">
        <xsl:apply-templates select="response_str |response_lid | response_grp | material | flow"/>
    </xsl:template>
    <xsl:template match="material">
        <xsl:apply-templates select="mattext | matimage"/>
    </xsl:template>
    <xsl:template match="mattext">
        <xsl:value-of select="self::*"/>
        <xsl:value-of disable-output-escaping="yes" select="descendant::comment()"/>
    </xsl:template>
    <xsl:template match="matimage">
        <xsl:if test="@imagtype = 'text/html'">
            <xsl:if test="@uri != ''">
                <br/>
                <br/>
                <img>
                    <xsl:attribute name="src">
                        <xsl:value-of select="@uri"/>
                    </xsl:attribute>
                </img>
                <br/>
                <br/>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    <xsl:template match="response_lid">
        <input>
            <xsl:attribute name="type">HIDDEN</xsl:attribute>
            <xsl:attribute name="name">response_type</xsl:attribute>
            <xsl:attribute name="value">lid</xsl:attribute>
        </input>
        <xsl:variable name="cardinalityType" select="@rcardinality"/>
        <xsl:apply-templates select="render_choice/response_label"/>
        <p/>
    </xsl:template>
    <xsl:template match="response_label">
        <xsl:variable name="rspIdent" select="../../@ident"/>
        <xsl:variable name="itemIdent" select="ancestor::item/@ident"/>
        <br/>
        <xsl:choose>
            <xsl:when test="@ident=/stxx/qti_result_report/result//item_result[@ident_ref=$itemIdent]/response[@ident_ref=$rspIdent]/response_value">
                <img src="../../images/delivery/green.gif" height="16" width="16"/>
            </xsl:when>
            <xsl:otherwise>
                <img src="../../images/delivery/spacer.gif" height="16" width="16"/>
            </xsl:otherwise>
        </xsl:choose>
        <input>
            <xsl:choose>
                <xsl:when test="../../@rcardinality = 'Multiple'">
                    <xsl:attribute name="name">item_result_ident_ref=<xsl:value-of
                            select="ancestor::item/@ident"/>&#38;response_ident_ref=<xsl:value-of
                            select="../../@ident"/>&#38;response_value=<xsl:value-of select="@ident"/>
                    </xsl:attribute>
                    <xsl:attribute name="type">CHECKBOX</xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@ident"/>
                    </xsl:attribute>
                    <xsl:attribute name="disabled">true</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="name">item_result_ident_ref=<xsl:value-of
                            select="ancestor::item/@ident"/>&#38;response_ident_ref=<xsl:value-of select="../../@ident"/>
                    </xsl:attribute>
                    <xsl:attribute name="type">RADIO</xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@ident"/>
                    </xsl:attribute>
                    <xsl:attribute name="disabled">true</xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:variable name="valueIdent" select="@ident"/>
            <xsl:for-each select="/stxx/qti_result_report/result/assessment_result//item_result[@ident_ref=$itemIdent]/response[@ident_ref=$rspIdent]/response_value">
                <xsl:variable name="responseValue" select="."/>
                <xsl:if test="$responseValue = $valueIdent">
                    <xsl:attribute name="checked"/>
                </xsl:if>
            </xsl:for-each>
        </input>
        <xsl:apply-templates select="descendant::material"/>
        <!-- Put selection level feedback here
    <xsl:for-each select="/stxx/qti_result_report/result/assessment_result//item_result[@ident_ref=$itemIdent2]/response[@ident_ref=$rspIdent]/response_value">
    <xsl:variable name="responseValue" select="." />
       <xsl:if test="$responseValue = $valueIdent" >
       <font  color="blue" >
     <xsl:apply-templates select="//item[@ident=$itemIdent2]/itemfeedback[@ident=$valueIdent]/flow_mat/material "/>
       </font>
    </xsl:if>
    </xsl:for-each>
 -->
    </xsl:template>
    <xsl:template match="itemfeedback">
        <xsl:apply-templates select="/flow_mat/material |material"/>
    </xsl:template>
    <xsl:template match="response_value">
        <xsl:value-of select="."/>
    </xsl:template>
    <xsl:template match="response_str">
        <input>
            <xsl:attribute name="type">HIDDEN</xsl:attribute>
            <xsl:attribute name="name">response_type</xsl:attribute>
            <xsl:attribute name="value">fib</xsl:attribute>
        </input>
        <xsl:apply-templates select="render_fib"/>
        <!-- Audio and File Upload -->
        <xsl:variable name="qmd_itemtype" select="ancestor::item/qtimetadata/qtimetadatafield[fieldlabel='qmd_itemtype']/fieldentry"/>
        <xsl:variable name="item_ident" select="ancestor::item/@ident"/>
        <xsl:choose>
            <xsl:when test="$qmd_itemtype='Audio Recording'">
                <xsl:call-template name="render_audio">
                    <xsl:with-param name="item_ident" select="$item_ident"/>
                </xsl:call-template> mode="audio"/>
            </xsl:when>
            <xsl:when test="$qmd_itemtype='File Upload'">
                <xsl:call-template name="render_upload">
                    <xsl:with-param name="item_ident" select="$item_ident"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="render_audio">
        <xsl:param name="item_ident"/>
        <embed>
            <xsl:attribute name="src">
                <xsl:value-of select="/stxxx/qti_result_report/result//item_result[@ident_ref=$item_ident]/response[@ident_ref='audio']/response_value"/>
            </xsl:attribute>
        </embed>
    </xsl:template>
    <xsl:template name="render_upload">
        <xsl:param name="item_ident"/>
        <a>
            <xsl:attribute name="href">
                <xsl:value-of select="/stxxx/qti_result_report/result//item_result[@ident_ref=$item_ident]/response[@ident_ref='upload']/response_value"/>
            </xsl:attribute>
        </a>
    </xsl:template>
    <xsl:template match="render_fib">
        <xsl:choose>
            <xsl:when test="@rows &gt; 1">
                <br/>
                <textarea WRAP="virtual">
                    <xsl:attribute name="cols">
                        <xsl:value-of select="@columns"/>
                    </xsl:attribute>
                    <xsl:attribute name="rows">
                        <xsl:value-of select="@rows"/>
                    </xsl:attribute>
                    <xsl:attribute name="name">item_result_ident_ref=<xsl:value-of
                            select="ancestor::item/@ident"/>&#38;response_ident_ref=<xsl:value-of select="../@ident"/>
                    </xsl:attribute>
                    <xsl:attribute name="id">item_result_ident_ref=<xsl:value-of
                            select="ancestor::item/@ident"/>&#38;response_ident_ref=<xsl:value-of select="../@ident"/>
                    </xsl:attribute>
                    <xsl:attribute name="disabled">true</xsl:attribute>
                    <xsl:variable name="rspIdent" select="../@ident"/>
                    <xsl:variable name="itemIdent" select="ancestor::item/@ident"/>
                    <xsl:value-of select="/stxx/qti_result_report/result/assessment_result//item_result[@ident_ref=$itemIdent]/response[@ident_ref=$rspIdent]/response_value"/>
                </textarea>
            </xsl:when>
            <xsl:otherwise>
                <input type="text">
                    <xsl:attribute name="size">
                        <xsl:value-of select="@columns"/>
                    </xsl:attribute>
                    <xsl:attribute name="name">item_result_ident_ref=<xsl:value-of
                            select="ancestor::item/@ident"/>&#38;response_ident_ref=<xsl:value-of select="../@ident"/>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:variable name="rspIdent" select="../@ident"/>
                        <xsl:variable name="itemIdent" select="ancestor::item/@ident"/>
                        <xsl:value-of select="/stxx/qti_result_report/result/assessment_result//item_result[@ident_ref=$itemIdent]/response[@ident_ref=$rspIdent]/response_value"/>
                    </xsl:attribute>
                    <xsl:attribute name="disabled">true</xsl:attribute>
                </input>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- *****************where response Type is Group.***************** -->
    <!--	<xsl:template match="response_grp">
		<xsl:apply-templates mode = "target_label" select="render_choice/response_label"/>
	</xsl:template>
-->
    <!-- *****************Seperating Targets from Sources***************** -->
    <xsl:template match="response_label" mode="source_label">
        <xsl:param name="target_x"/>
        <xsl:param name="position"/>
        <xsl:if test="contains(@match_group,$target_x)">
            <option>
                <xsl:attribute name="value">
                    <xsl:value-of select="@ident"/>
                </xsl:attribute>
                <xsl:variable name="ident" select="@ident"/>
                <xsl:variable name="itemIdent" select="ancestor::item/@ident"/>
                <xsl:if test="/stxx/qti_result_report/result/assessment_result//item_result[@ident_ref=$itemIdent]/response/response_value[$position] = $ident">
                    <xsl:attribute name="selected"/>
                </xsl:if>
                <xsl:number count="response_label" format="A" level="single"/>
            </option>
        </xsl:if>
    </xsl:template>
    <xsl:template match="response_label" mode="target_label">
        <xsl:if test="not(@match_group)">
            <table>
                <tr>
                    <td>
                        <xsl:attribute name="value">
                            <xsl:value-of select="@ident"/>
                        </xsl:attribute>
                        <xsl:value-of select="material"/>
                    </td>
                    <td>
                        <input>
                            <xsl:attribute name="type">HIDDEN</xsl:attribute>
                            <xsl:attribute name="name">response_type</xsl:attribute>
                            <xsl:attribute name="value">grp</xsl:attribute>
                        </input>
                        <select>
                            <xsl:attribute name="name">item_result_ident_ref=<xsl:value-of
                                    select="ancestor::item/@ident"/>&#38;response_ident_ref=<xsl:value-of
                                    select="../../@ident"/>&#38;response_value=<xsl:value-of select="@ident"/>
                            </xsl:attribute>
                            <option> [Select]</option>
                            <xsl:variable name="target" select="@ident"/>
                            <xsl:apply-templates mode="source_label" select="../response_label">
                                <xsl:with-param name="target_x" select="$target"/>
                            </xsl:apply-templates>
                        </select>
                    </td>
                </tr>
            </table>
        </xsl:if>
    </xsl:template>
    <!-- *********This template can handle the response group matching case result display.
			   The condition is the response_label elements has to be grouped together
			   and presented in "Source" first "Target" second order. **********-->
    <xsl:template match="response_grp">
        <br/>
        <!-- Display all sources first -->
        <xsl:for-each select="render_choice/response_label">
            <xsl:if test="(@match_group)">
                <xsl:number count="response_label" format="A" level="single"/>. <xsl:value-of select="material"/>
                <br/>
            </xsl:if>
        </xsl:for-each>
        <br/>
        <xsl:variable name="base" select="count(render_choice/response_label[@match_group])"/>
        <xsl:for-each select="render_choice/response_label">
            <xsl:if test="not(@match_group)">
                <table>
                    <tr>
                        <td>
                            <input>
                                <xsl:attribute name="type">HIDDEN</xsl:attribute>
                                <xsl:attribute name="name">response_type</xsl:attribute>
                                <xsl:attribute name="value">grp</xsl:attribute>
                            </input>
                            <select>
                                <xsl:attribute name="name">item_result_ident_ref=<xsl:value-of
                                        select="ancestor::item/@ident"/>&#38;response_ident_ref=<xsl:value-of
                                        select="../../@ident"/>&#38;response_value=<xsl:value-of select="position()-$base"/>
                                </xsl:attribute>
                                <option value="null"> [Select]</option>
                                <xsl:variable name="target" select="@ident"/>
                                <xsl:apply-templates mode="source_label" select="../response_label">
                                    <xsl:with-param name="target_x" select="$target"/>
                                    <xsl:with-param name="position" select="position()-$base"/>
                                </xsl:apply-templates>
                            </select>
                        </td>
                        <td>
                            <xsl:attribute name="value">
                                <xsl:value-of select="@ident"/>
                            </xsl:attribute>
                            <xsl:value-of select="material"/>
                        </td>
                    </tr>
                </table>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    <!-- *****************qtimetadatafield *****************-->
    <xsl:template name="fieldlabel">
        <xsl:param name="fl"/>
        <xsl:for-each select="itemmetadata/qtimetadata/qtimetadatafield/fieldlabel">
            <xsl:variable name="fieldlabel" select="."/>
            <xsl:if test="$fieldlabel= $fl">
                <xsl:variable name="fieldentry" select="../fieldentry"/>
                <xsl:value-of select="$fieldentry"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
