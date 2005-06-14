<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: matchRandomize.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="itemImages.xsl"/>
    <xsl:template name="matchRandomize">
       <tr>
            <td class="form_label">
                <table border="0" width="100%">
                    <tr>
                        <td width="23">
                            <xsl:call-template name="questionCircle"/>
                        </td>
                        <td align="left"> <span class ="bold">Randomize Items:</span> <xsl:variable
                                name="shuffle" select="stxx/item/presentation/flow/response_grp/render_choice/@shuffle"/>
                            <input
                                name="stxx/item/presentation/flow/response_grp/render_choice/@shuffle"
                                type="radio" value="Yes">
                                <xsl:if test="$shuffle='Yes'">
                                    <xsl:attribute name="checked">true</xsl:attribute>
                                </xsl:if>
                            </input>Yes &#160; <input
                                name="stxx/item/presentation/flow/response_grp/render_choice/@shuffle"
                                type="radio" value="No">
                                <xsl:if test="$shuffle='No'">
                                    <xsl:attribute name="checked">true</xsl:attribute>
                                </xsl:if>
                            </input>No &#160; </td>
                    </tr>
                </table>
            </td>
        </tr>           
    </xsl:template>
    <!--****************************************************************************** -->
</xsl:stylesheet>
