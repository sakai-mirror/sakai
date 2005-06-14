<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: lidRandomize.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="itemImages.xsl"/>
  <xsl:template name="randomizeAnswers">
    <tr>
<!--      <td align="center" class="bold"> Add more answer section:&#160;-->
<!--      <td align="center"> Add more answer sections:&#160;-->
      <td align="center"> Insert additional answers
       <select name="answerNumbers">
          <option value="0">[Select]</option>
          <option>1</option>
          <option>2</option>
          <option>3</option>
          <option>4</option>
          <option>5</option>
          <option>6</option>
          <option>7</option>
          <option>8</option>
          <option>9</option>
          <option>10</option>
        </select> &#160; <input name="responseNo" type="hidden">
          <xsl:attribute name="value">
            <xsl:apply-templates select="stxx/item/presentation/flow/response_lid/render_choice"/>
          </xsl:attribute>
        </input>
        <input name="parent" type="hidden" value="item/presentation/flow/response_lid/render_choice[1]"/>
        <xsl:variable name="resp_ident" select="stxx/item/presentation/flow/response_lid/@ident"/>
        <input name="respident" type="hidden" value="{$resp_ident}"/>
<!--        <input name="action" type="submit" value="Add" onClick="javascript:onSubmitFn();"/>-->
        <input name="insert" type="submit" value="Insert" onClick="javascript:onSubmitFn();"/>
      </td>
    </tr>
    <tr>
      <td>
        <br/>
      </td>
    </tr>
    <tr>
      <td class="form_label">
        <table border="0" width="100%">
          <tr>
            <td width="23">
              <xsl:call-template name="questionCircle"/>
            </td>
            <td align="left"> <span  class="bold">Randomize Answers:</span> <xsl:variable name="shuffle" select="stxx/item/presentation/flow/response_lid/render_choice/@shuffle"/>
              <input name="stxx/item/presentation/flow/response_lid/render_choice/@shuffle" type="radio" value="Yes">
                <xsl:if test="$shuffle='Yes'">
                  <xsl:attribute name="checked">true</xsl:attribute>
                </xsl:if>
              </input>Yes &#160; <input name="stxx/item/presentation/flow/response_lid/render_choice/@shuffle" type="radio" value="No">
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
