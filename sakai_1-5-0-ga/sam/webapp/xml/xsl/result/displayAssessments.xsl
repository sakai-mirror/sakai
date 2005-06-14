<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: displayAssessments.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet exclude-result-prefixes="xalan" version="1.0" xmlns:xalan="http://xml.apache.org" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="../layout/header.xsl"/>
    <xsl:import href="../layout/footer.xsl"/>
    <xsl:import href="../layout/menu.xsl"/>
    <xsl:import href="../layout/defaultLayout.xsl"/>
    <xsl:output method="html"/>
    <!-- This template processes the root node ("/") -->
    <xsl:template match="/">
        <xsl:call-template name="defaultLayout"/>
    </xsl:template>
    <xsl:template name="body">
        <h1> Select Result </h1>
        <xsl:apply-templates select="stxx"/>
    </xsl:template>
    <xsl:template match="stxx">
        <p align="center">
            <b>Authorized assessments </b>
            <br/>
            <xsl:apply-templates select="authorizedAssessments/activeAssessments"/>
            <xsl:apply-templates select="authorizedAssessments/inActiveAssessments"/>
        </p>
    </xsl:template>
    <xsl:template match="assessmentElements">
        <form action="asi/realize/authorizeAssessmentAction.do" method="post">
            <table border="1" width="80%">
                <tr>
                    <td align="center">Assessment Name</td>
                </tr>
                <xsl:for-each select="value/assessment | assessment">
                    <tr>
                        <td>
                            <input type="radio">
                                <xsl:attribute name="name">assessmentId</xsl:attribute>
                                <xsl:attribute name="value">
                                    <xsl:value-of select="@ident"/>
                                </xsl:attribute>
                            </input>
                            <xsl:value-of select="@title"/>
                        </td>
                    </tr>
                </xsl:for-each>
                <tr>
                    <td>
                        <input name="submit" type="submit" value="Authorize"/>
                    </td>
                </tr>
            </table>
        </form>
    </xsl:template>
    <xsl:template match="activeAssessments">
       <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
        <table border="1" width="80%">
            <tr>
                <td align="center" colspan="3">Test Name</td>
                <td align="center">Available</td>
                <td align="center">Completed</td>
            </tr>
            <xsl:for-each select="assessment">
                <tr>
                    <td>
                        <a>
                            <xsl:attribute name="href"><xsl:value-of select="$base"/>asi/grading/xmlSelectAction.do?assessmentId=<xsl:value-of select="@ident"/>&amp;tableOfContents=toc&amp;debug=false</xsl:attribute>
                            View
                        </a>
                    </td>
                    <td>
                        <a>
                            <xsl:attribute name="href"><xsl:value-of select="$base"/>asi/grading/xmlSelectAction.do?assessmentId=<xsl:value-of select="@ident"/>&amp;tableOfContents=toc&amp;debug=false</xsl:attribute>
                            Grade
                        </a>
                    </td>
                    <td>
                        <xsl:value-of select="@title"/>
                    </td>
                    <td>
                        <xsl:value-of select="@effectiveDate"/>
                    </td>
                    <td/>
                </tr>
            </xsl:for-each>
        </table>
    </xsl:template>
    <xsl:template match="inActiveAssessments">
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
        <table border="1" width="80%">
            <xsl:for-each select="assessment">
                <tr>
                    <td>
                        <a>
                            <xsl:attribute name="href"><xsl:value-of select="$base"/>asi/grading/xmlSelectAction.do?assessmentId=<xsl:value-of select="@ident"/>&amp;tableOfContents=toc&amp;debug=false
                            </xsl:attribute>
                            View
                        </a>
                    </td>
                    <td>
                        <a>
                            <xsl:attribute name="href"><xsl:value-of select="$base"/>asi/grading/xmlSelectAction.do?assessmentId=<xsl:value-of select="@ident"/>&amp;tableOfContents=toc&amp;debug=false
                            </xsl:attribute>
                            Grade
                        </a>
                        <xsl:value-of select="@title"/>
                    </td>
                    <td>
                        <xsl:value-of select="@effectiveDate"/>
                    </td>
                    <td/>
                </tr>
            </xsl:for-each>
        </table>
    </xsl:template>
    <!-- *****************qtimetadatafield *****************-->
    <xsl:template name="fieldlabel">
        <xsl:param name="fl"/>
        <xsl:for-each select="qtimetadata/qtimetadatafield/fieldlabel">
            <xsl:variable name="fieldlabel" select="."/>
            <xsl:if test="$fieldlabel= $fl">
                <xsl:variable name="fieldentry" select="../fieldentry"/>
                <xsl:value-of select="$fieldentry"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
