<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: contentTree.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="header.xsl"/>
    <xsl:import href="../layout/footer.xsl"/>
    <xsl:import href="../layout/menu.xsl"/>
    <xsl:import href="defaultLayout.xsl"/>
    <xsl:include href="common.xsl"/>
    <xsl:output method="html"/>
    <xsl:template match="/">
        <xsl:call-template name="defaultLayout"/>
    </xsl:template>
    <xsl:template name="head">
        <title>Assessment Table of Contents</title>
        <link type="text/css" rel="stylesheet" href="{$CONTEXT_PATH}/css/xmlTree.css"/>
        <script type="text/javascript" src="{$CONTEXT_PATH}/js/xmlTree.js"/>
    </xsl:template>
    <xsl:template name="body">
        <table align="center" width="90%">
            <tr>
                <td>
                    <b>
                        <xsl:value-of select="stxx/form/xmlDeliveryActionForm/assessmentTitle"/> -
                            <xsl:apply-templates select="stxx/qti_result_report/result/context/name"/>
                    </b>
                </td>
                <td align="right">
                    <form action="{$CONTEXT_PATH}/histogramScores.do" method="put">
                        <input type="hidden" name="id">
                            <xsl:attribute name="value">
                                <xsl:value-of select="/stxx/qti_result_report/result/context/generic_identifier[type_label='coreId']/identifier_string"/>
                            </xsl:attribute>
                        </input>
                        <input type="submit" name="statsView">
                            <xsl:attribute name="value">
                                <xsl:value-of select="/stxx/applicationResources/key[@name='grading.button.statsView']"/>
                            </xsl:attribute>
                        </input>
                    </form>
                    <form action="{$CONTEXT_PATH}/totalScores.do" method="put">
                        <input type="hidden" name="id">
                            <xsl:attribute name="value">
                                <xsl:value-of select="/stxx/qti_result_report/result/context/generic_identifier[type_label='coreId']/identifier_string"/>
                            </xsl:attribute>
                        </input>
                        <input type="submit" name="scoreView">
                            <xsl:attribute name="value">
                                <xsl:value-of select="/stxx/applicationResources/key[@name='grading.button.scoreView']"/>
                            </xsl:attribute>
                        </input>
                    </form>
                </td>
            </tr>
        </table>
        <form action="{$CONTEXT_PATH}/asi/grading/gradingAction.do" method="post" name="ASIDeliveryForm">
            <input type="hidden" name="id">
                <xsl:attribute name="value">
                    <xsl:value-of select="/stxx/qti_result_report/result/context/generic_identifier[type_label='coreId']/identifier_string"/>
                </xsl:attribute>
            </input>
            <table align="center" width="90%">
                <tr>
                    <td colspan="2">
                        <b>
                            <xsl:value-of select="/stxx/applicationResources/key[@name='grading.assessment.comments']"/>
                        </b>
                    </td>
                </tr>
                <tr>
                    <xsl:variable name="assessment_id" select="/stxx/qti_result_report/result/assessment_result/@ident_ref"/>
                    <td>
                        <input type="hidden" name="assessmentIds">
                            <xsl:attribute name="value">
                                <xsl:value-of select="$assessment_id"/>
                            </xsl:attribute>
                        </input>
                        <input type="hidden">
                            <xsl:attribute name="name">
                                <xsl:value-of select="$assessment_id"/>.publishedId</xsl:attribute>
                            <xsl:attribute name="value">
                                <xsl:value-of select="/stxx/qti_result_report/result/context/generic_identifier[type_label='publishedId']/identifier_string"/>
                            </xsl:attribute>
                        </input>
                        <input type="hidden">
                            <xsl:attribute name="name">
                                <xsl:value-of select="$assessment_id"/>.score</xsl:attribute>
                            <xsl:attribute name="value">
                                <xsl:value-of select="/stxx/qti_result_report/result/assessment_result/outcomes/score/score_value"/>
                            </xsl:attribute>
                        </input>
                        <textarea cols="90" rows="10">
                            <xsl:attribute name="name">
                                <xsl:value-of select="$assessment_id"/>.comments</xsl:attribute>
                            <xsl:attribute name="id">
                                <xsl:value-of select="$assessment_id"/>.comments</xsl:attribute>
                            <xsl:value-of select="/stxx/qti_result_report/result/assessment_result/extension_assessment_result/comment()"/>
                        </textarea>
                    </td>
                    <td align="left" valign="top">
                        <a>
                            <xsl:attribute name="href">javascript:toggleToolbar('<xsl:value-of
                            select="$assessment_id"/>.comments')</xsl:attribute> Show/Hide
                            <br/>Editor </a>
                    </td>
                </tr>
            </table>
            <p align="center">
                <table bgcolor="ccccff" class="border" width="90%">
                    <tr>
                        <td> Table of Contents </td>
                    </tr>
                </table>
                <table width="80%">
                    <tr>
                        <td>
                            <xsl:apply-templates/>
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                            <input>
                                <xsl:attribute name="type">submit</xsl:attribute>
                                <xsl:attribute name="name">beginTest</xsl:attribute>
                                <xsl:attribute name="value">
                                    <xsl:value-of select="/stxx/applicationResources/key[@name='grading.button.saveAndContinue']"/>
                                </xsl:attribute>
                            </input>
                            <input>
                                <xsl:attribute name="type">submit</xsl:attribute>
                                <xsl:attribute name="name">saveAndExit</xsl:attribute>
                                <xsl:attribute name="value">
                                    <xsl:value-of select="/stxx/applicationResources/key[@name='grading.button.saveAndExit']"/>
                                </xsl:attribute>
                            </input>
                            <input>
                                <xsl:attribute name="type">submit</xsl:attribute>
                                <xsl:attribute name="name">cancel</xsl:attribute>
                                <xsl:attribute name="value">
                                    <xsl:value-of select="/stxx/applicationResources/key[@name='grading.button.cancel']"/>
                                </xsl:attribute>
                            </input>
                        </td>
                    </tr>
                </table>
            </p>
        </form>
        <br/>
        <br/>
    </xsl:template>
    <xsl:template match="name">
        <xsl:choose>
            <xsl:when test="contains(/stxx/qti_result_report/result/assessment_result/asi_metadata/asi_metadatafield[field_name='ASSESSMENT_RELEASED_TO']/field_value, 'Anonymous') or /stxx/qti_result_report/result/assessment_result/asi_metadata/asi_metadatafield[field_name='ANONYMOUS_GRADING']/field_value='TRUE'">
                <xsl:value-of select="/stxx/qti_result_report/result/assessment_result/@ident_ref"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="assessment">
        <body>
            <table border="1">
                <tr>
                    <td>
                        <b>Total Score: <xsl:value-of
                                select="/stxx/qti_result_report/result/assessment_result/outcomes/score/score_value"/>/<xsl:value-of
                                select="/stxx/qti_result_report/result/assessment_result/outcomes/score/score_max"/>&#160;<xsl:value-of select="/stxx/applicationResources/key[@name='grading.points']"/>
                        </b>
                    </td>
                </tr>
                <tr>
                    <td>
                        <b>Submitted: </b>
                    </td>
                </tr>
            </table>
            <xsl:apply-templates select="section"/>
        </body>
    </xsl:template>
    <xsl:template match="section">
        <xsl:choose>
            <xsl:when test="@title = 'DEFAULT'">
                <xsl:apply-templates select="section | item"/>
            </xsl:when>
            <xsl:otherwise>
                <span class="trigger">
                    <xsl:attribute name="onClick"> showBranch ('<xsl:value-of select="@ident"/>'); </xsl:attribute>
                    <img src="../../images/tree/closed.gif" alt="{/stxx/applicationResources/key[@name='grading.image.twisty']}">
                        <xsl:attribute name="id">I<xsl:value-of select="@ident"/>
                        </xsl:attribute>
                    </img>
                    <xsl:value-of select="@title"/>
                    <br/>
                </span>
                <span class="branch">
                    <xsl:attribute name="id">
                        <xsl:value-of select="@ident"/>
                    </xsl:attribute>
                    <xsl:apply-templates/>
                </span>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="item">
        <xsl:variable name="valueIdent" select="@ident"/>
        <table border="0">
            <tr>
                <td width="16">
                    <!--
    <xsl:for-each select="/stxx/form/xmlDeliveryActionForm/blankItemIdents/value">
    <xsl:variable name="blankItemIdent" select="." />
      <xsl:if test="$blankItemIdent = $valueIdent" >
        <img src="../../images/tree/blank.gif"/>
       </xsl:if>
    </xsl:for-each>
    -->
                </td>
                <td width="16">
                    <xsl:if test="not(/stxx/qti_result_report/result//item_result[@ident_ref=$valueIdent]//response_value)">*</xsl:if>
                    <!--
    <xsl:for-each select="/stxx/form/xmlDeliveryActionForm/markedForReviewIdents/value">
    <xsl:variable name="markedItemIdent" select="." />
       <xsl:if test="$markedItemIdent = $valueIdent" >
        <img src="../../images/tree/marked.gif"/>
       </xsl:if>
    </xsl:for-each>
    -->
                </td>
                <td>
                    <b>
                        <xsl:value-of select="@displayIndex"/>. </b>
                    <a>
                        <xsl:attribute name="href">gradingAction.do?itemIndex=<xsl:number
                                count="item" format="1" level="any"/>
                        </xsl:attribute>
                        <xsl:apply-templates select="presentation/material/mattext | presentation/flow//material/mattext "/>
                    </a>
                    <xsl:apply-templates select="/stxx/qti_result_report/result//item_result[attribute::ident_ref=$valueIdent]/outcomes/score"/>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="mattext">
        <xsl:variable name="questionText1" select="self::*"/>
        <xsl:value-of select="substring($questionText1, 1, 50)"/>
        <xsl:variable name="questionText2" select="descendant::comment()"/>
        <xsl:value-of select="substring($questionText2, 1, 50)"/>
    </xsl:template>
    <xsl:template match="score"> (<xsl:value-of select="score_value"/>/<xsl:value-of
            select="score_max"/>&#160;<xsl:value-of
        select="/stxx/applicationResources/key[@name='grading.points']"/>) </xsl:template>
    <!-- avoid output of text node
        with default template -->
    <xsl:template match="duration"/>
    <xsl:template match="qticomment"/>
    <xsl:template match="qtimetadata"/>
    <xsl:template match="objectives"/>
    <xsl:template match="assessmentcontrol"/>
    <xsl:template match="rubric"/>
    <xsl:template match="presentation_material"/>
    <xsl:template match="outcomes_processing"/>
    <xsl:template match="assessfeedback"/>
    <xsl:template match="selection_ordering"/>
    <xsl:template match="reference"/>
    <xsl:template match="sectionref"/>
    <xsl:template match="form"/>
    <xsl:template match="applicationResources"/>
    <xsl:template match="request"/>
    <xsl:template match="qti_result_report"/>
</xsl:stylesheet>
