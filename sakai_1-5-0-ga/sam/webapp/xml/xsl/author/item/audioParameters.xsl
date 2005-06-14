<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="itemAuthorQtimetadata.xsl"/>
  <xsl:template name="timeallowed">
    <xsl:variable name="timeallowed">
      <xsl:value-of select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='TIMEALLOWED']/following-sibling::fieldentry"/>
    </xsl:variable>
    <xsl:variable name="timeallowedXpath">
        <xsl:apply-templates select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield">
          <xsl:with-param name="keyName" select="'TIMEALLOWED'"/>
          <xsl:with-param name="return" select="'xpath'"/>
        </xsl:apply-templates>
    </xsl:variable>
    <tr>
      <td>
        <table border="0" width="100%">
          <tr>
            <td width="23">
              <xsl:call-template name="questionCircle"/>
            </td>
            <td>
              <span class="bold"> Time allowed (seconds): Indicate how long student has to record answer</span>
            </td>
          </tr>
          <tr>
            <td><br></br></td>
            <td>
              <input name="{$timeallowedXpath}" size="1" type="text" value="{$timeallowed}"></input>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="num_of_attempts">
    <xsl:variable name="num_of_attempts">
      <xsl:value-of select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='NUM_OF_ATTEMPTS']/following-sibling::fieldentry"/>
    </xsl:variable>
    <xsl:variable name="num_entryXpath">
        <xsl:apply-templates select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield">
          <xsl:with-param name="keyName" select="'NUM_OF_ATTEMPTS'"/>
          <xsl:with-param name="return" select="'xpath'"/>
        </xsl:apply-templates>
    </xsl:variable>
    <tr>
      <td>
        <table border="0" width="100%">
          <tr>
            <td width="23">
              <xsl:call-template name="questionCircle"/>
            </td>
            <td>
              <span class="bold"> Number of attempts: Indicate number of times students are allowed to re-record answer</span>
            </td>
          </tr>
          <tr>
            <td><br></br></td>
            <td>
              <select name="{$num_entryXpath}">
                 <option>
                   <xsl:attribute name="value">9999</xsl:attribute>
                   <xsl:if test="number($num_of_attempts) &gt; 10">
                     <xsl:attribute name="selected">true</xsl:attribute>
                   </xsl:if>
                   Unlimited
                 </option>
                 <option>
                   <xsl:attribute name="value">1</xsl:attribute>
                   <xsl:if test="$num_of_attempts='1'">
                     <xsl:attribute name="selected">true</xsl:attribute>
                   </xsl:if>
                   1
                 </option>
                 <option>
                   <xsl:attribute name="value">2</xsl:attribute>
                   <xsl:if test="$num_of_attempts='2'">
                     <xsl:attribute name="selected">true</xsl:attribute>
                   </xsl:if>
                   2
                 </option>
                 <option>
                   <xsl:attribute name="value">3</xsl:attribute>
                   <xsl:if test="$num_of_attempts='3'">
                     <xsl:attribute name="selected">true</xsl:attribute>
                   </xsl:if>
                   3
                 </option>
                 <option>
                   <xsl:attribute name="value">4</xsl:attribute>
                   <xsl:if test="$num_of_attempts='4'">
                     <xsl:attribute name="selected">true</xsl:attribute>
                   </xsl:if>
                   4
                 </option>
                 <option>
                   <xsl:attribute name="value">5</xsl:attribute>
                   <xsl:if test="$num_of_attempts='5'">
                     <xsl:attribute name="selected">true</xsl:attribute>
                   </xsl:if>
                   5
                 </option>
                 <option>
                   <xsl:attribute name="value">6</xsl:attribute>
                   <xsl:if test="$num_of_attempts='6'">
                     <xsl:attribute name="selected">true</xsl:attribute>
                   </xsl:if>
                   6
                 </option>
                 <option>
                   <xsl:attribute name="value">7</xsl:attribute>
                   <xsl:if test="$num_of_attempts='7'">
                     <xsl:attribute name="selected">true</xsl:attribute>
                   </xsl:if>
                   7
                 </option>
                 <option>
                   <xsl:attribute name="value">8</xsl:attribute>
                   <xsl:if test="$num_of_attempts='8'">
                     <xsl:attribute name="selected">true</xsl:attribute>
                   </xsl:if>
                   8
                 </option>
                 <option>
                   <xsl:attribute name="value">9</xsl:attribute>
                   <xsl:if test="$num_of_attempts='9'">
                     <xsl:attribute name="selected">true</xsl:attribute>
                   </xsl:if>
                   9
                 </option>
                 <option>
                   <xsl:attribute name="value">10</xsl:attribute>
                   <xsl:if test="$num_of_attempts='10'">
                     <xsl:attribute name="selected">true</xsl:attribute>
                   </xsl:if>
                   10
                 </option>
              </select>
              <!--input name="{$num_entryXpath}" size="1" type="text" value="{$num_of_attempts}"></input-->
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </xsl:template>
 <!--****************************************************************************** -->
</xsl:stylesheet>
