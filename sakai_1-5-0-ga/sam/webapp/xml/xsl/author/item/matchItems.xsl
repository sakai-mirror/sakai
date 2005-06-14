<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="response_grp">
    <table align="right" border="0" width="98%">
      <xsl:variable name="resps">
        <xsl:apply-templates mode="matching" select="render_choice[1]"/>
      </xsl:variable>
      <input name="responseNo" type="hidden">
        <xsl:attribute name="value">
          <xsl:value-of select="$resps"/>
        </xsl:attribute>
      </input>
      <xsl:variable name="noOfLastSource" select="count(render_choice/response_label/@match_group)"/>
      <input name="noOfLastSource" type="hidden" value="{$noOfLastSource}"/>
      <input name="parent" type="hidden" value="item/presentation/flow/response_grp/render_choice[1]"/>
      <xsl:variable name="resp_ident" select="stxx/item/presentation/flow/response_grp/@ident"/>
      <input name="respident" type="hidden" value="{$resp_ident}"/>
      <xsl:variable name="sourceLastResponseXpath" select="concat('stxx/item/presentation/flow/response_grp/render_choice[1]/response_label[',number($noOfLastSource),']/material/mattext')"/>
      <xsl:variable name="itemLastResponseXpath" select="concat('stxx/item/presentation/flow/response_grp/render_choice[1]/response_label[',number($resps),']/material/mattext')"/>
      <tr>
        <td colspan="2" valign="top">
          <table border="0" width="100%">
            <tr>
              <td valign="top" width="23">
                <xsl:call-template name="questionCircle"/>
              </td>
              <td valign="top"> &#160; Add Items: &#160; &#160;<input name="{$sourceLastResponseXpath}" size="60"/> &#160; &#160; <input name="AddT" type="submit" value="Add"/>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <!-- Display all sources = item first -->
      <xsl:for-each select="render_choice/response_label">
        <xsl:if test="(@match_group)">
          <xsl:variable name="xpath_mattext" select="concat('stxx/item/presentation/flow/response_grp/render_choice/response_label[',position(),']/material/mattext')"/>
          <tr>
            <td align="right" width="10%"/>
            <td align="left">
              <xsl:number format="1" level="single"/>. <xsl:value-of select="material/mattext"/>&#160; &#160;
              <xsl:variable name="actionname" select="concat('Remove', position())"/>
              <input name="{$actionname}" onClick="javascript:onSubmitFn();" type="submit" value="Remove"/>
            </td>
          </tr>
        </xsl:if>
      </xsl:for-each>
      <tr>
        <td colspan="2" valign="top">
          <table border="0" width="100%">
            <tr>
              <td valign="top" width="23">
                <xsl:call-template name="questionCircle"/>
              </td>
              <td valign="top"> &#160; Add Matches: &#160; <input name="{$itemLastResponseXpath}" size="60"/> &#160; &#160;<input name="action" type="submit" value="Add"/>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <xsl:for-each select="render_choice/response_label">
        <xsl:if test="not(@match_group)">
          <xsl:variable name="xpath_mattext" select="concat('stxx/item/presentation/flow/response_grp/render_choice/response_label[',position(),']/material/mattext')"/>
          <tr>
            <td align="right" width="10%"/>
            <td>
              <xsl:number format="1" level="single"/>. <xsl:value-of select="material/mattext"/>&#160; &#160;
              <xsl:variable name="actionname" select="concat('Remove', position())"/>
              <input name="{$actionname}" onClick="javascript:onSubmitFn();" type="submit" value="Remove"/>
            </td>
          </tr>
        </xsl:if>
      </xsl:for-each>

    <!--FROM matching continued -->

      <tr>
        <td colspan="2">
          <table border="0" width="100%">
            <tr>
              <td width="23">
                <xsl:call-template name="questionCircle"/>
              </td>
              <td> Answers: Select answer below. </td>
            </tr>
          </table>
        </td>
      </tr>
      <tr>
        <td class="form_label" colspan="2">
          <table align="right" border="0" width="95%">
            <tr>
              <td width="2%">&#160;</td>
              <td align="left" class="form_label"> Items:</td>
              <xsl:for-each select="/stxx/item/presentation/flow/response_grp/render_choice/response_label">
                <xsl:if test="(@match_group)">
                  <tr>
                    <td width="2%">&#160;</td>
                    <td>&#160; <xsl:number count="response_label" format="A" level="single"/>. &#160; <xsl:value-of select="material/mattext"/>
                    </td>
                  </tr>
                </xsl:if>
              </xsl:for-each>
              <tr>
                <td>
                  <br/>
                </td>
                <td/>
              </tr>
              <xsl:apply-templates mode="notMatch" select="/stxx/item/presentation/flow/response_grp/render_choice/response_label[not(@match_group)]"/>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </xsl:template>
  <!--******************************************************************************** -->
  <xsl:template match="render_choice" mode="matching">
    <xsl:value-of select="count(child::response_label)+1"/>
  </xsl:template>
  <!--******************************************************************************** -->
  <xsl:template match="response_label" mode="notMatch">
    <tr>
      <td width="2%">
        <xsl:number format="1" level="single" value="position()"/>.</td>
      <td>
        <span class="form_label">Match</span> &#160;&#160;&#160;&#160;&#160; <xsl:value-of select="material/mattext"/>
      </td>
    </tr>
    <tr>
      <td>
        <br/>
      </td>
      <td/>
    </tr>
    <tr>
      <td/>
      <td>
        <span class="form_label">Item </span>&#160; &#160;&#160;&#160; &#160; <xsl:variable name="pos">
          <xsl:number format="1" level="single" value="position()"/>
        </xsl:variable>
        <xsl:variable name="resp" select="concat('stxx/item/resprocessing/respcondition[',$pos,']/conditionvar/varequal')"/>
        <xsl:variable name="selectedChoice">
          <xsl:apply-templates select="/stxx/item/resprocessing/respcondition">
            <xsl:with-param name="pos" select="$pos"/>
          </xsl:apply-templates>
        </xsl:variable>
        <select name="{$resp}">
          <xsl:for-each select="/stxx/item/presentation/flow/response_grp/render_choice/response_label">
            <xsl:if test="(@match_group)">
              <xsl:variable name="respIdent" select="@ident"/>
              <option value="{$respIdent}">
                <xsl:if test="$respIdent=$selectedChoice">
                  <xsl:attribute name="selected">true</xsl:attribute>
                </xsl:if>
                <xsl:number format="A" level="single" value="position()"/>
              </option>
            </xsl:if>
          </xsl:for-each>
        </select>
      </td>
    </tr>
    <xsl:apply-templates select="/stxx/item/itemfeedback">
      <xsl:with-param name="position" select="position()"/>
    </xsl:apply-templates>
    <tr>
      <td>
        <br/>
      </td>
      <td/>
    </tr>
  </xsl:template>
  <!--******************************************************************************** -->
  <xsl:template match="respcondition">
    <xsl:param name="pos"/>
    <xsl:if test="$pos=position()">
      <xsl:value-of select="conditionvar/varequal"/>
    </xsl:if>
  </xsl:template>
  <!--******************************************************************************** -->
  <xsl:template match="itemfeedback">
    <xsl:param name="position"/>
    <xsl:if test="$position = position()">
      <xsl:variable name="name" select="concat('stxx/item/itemfeedback[',position(),']/flow_mat[1]/material[1]/mattext[1]')"/>
      <xsl:variable name="uri" select="concat('stxx/item/itemfeedback[',position(),']/flow_mat[1]/material[2]/matimage[1]/@uri')"/>
      <xsl:variable name="mattext" select="flow_mat[1]/material[1]/mattext[1]"/>
      <xsl:variable name="matimage" select="flow_mat[1]/material[2]/matimage[1]/@uri"/>
      <tr>
        <td/>
        <td>
          <table border="0" width="100%">
            <tr>
              <td width="6%">
                <span class="form_label"> Feedback </span> (optional)</td>
              <td width="45">
                <textarea cols="45" id="{$name}" name="{$name}" rows="10">
                  <xsl:value-of select="$mattext"/>
                </textarea>
              </td>
              <td valign="top">
                <xsl:variable name="base">
                  <xsl:call-template name="baseHREF"/>
                </xsl:variable>
                <!--<img alt="Toggle Toolbar" src="{$base}htmlarea/images/htmlarea_editor.gif">
                  <xsl:attribute name="onClick">javascript:hideUnhide("<xsl:value-of select="$name"/>");</xsl:attribute>
                </img>-->
                <a>
                  <xsl:attribute name="href">javascript:hideUnhide("<xsl:value-of select="$name"/>");</xsl:attribute>
                  Show/Hide <br />Editor
                </a>
              </td>
            </tr>
            <!-- <tr><td width= "6%"> Image: </td><td>  <input name="{$uri}" size = "38"  type="text" value="{$matimage}"/>
            </td></tr>-->
          </table>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>
  <!--******************************************************************************** -->
</xsl:stylesheet>
