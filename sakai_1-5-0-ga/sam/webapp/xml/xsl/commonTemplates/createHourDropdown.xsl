<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: createHourDropdown.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template name="hourDropdown">
    <xsl:param name="hSelected"/>
  	<option value='0'><xsl:if test="$hSelected=0" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>0</option>
	<option value='1'><xsl:if test="$hSelected=1" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>1</option>
	<option value='2'><xsl:if test="$hSelected=2" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>2</option>
	<option value='3'><xsl:if test="$hSelected=3" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>3</option>
	<option value='4'><xsl:if test="$hSelected=4" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>4</option>
	<option value='5'><xsl:if test="$hSelected=5" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>5</option>
	<option value='6'><xsl:if test="$hSelected=6" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>6</option>
	<option value='7'><xsl:if test="$hSelected=7" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>7</option>
	<option value='8'><xsl:if test="$hSelected=8" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>8</option>
	<option value='9'><xsl:if test="$hSelected=9" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>9</option>
	<option value='10'><xsl:if test="$hSelected=10" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>10</option>
	<option value='11'><xsl:if test="$hSelected=11" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>11</option>
	<option value='12'><xsl:if test="$hSelected=12" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>12</option>
	<option value='13'><xsl:if test="$hSelected=13" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>13</option>
	<option value='14'><xsl:if test="$hSelected=14" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>14</option>
	<option value='15'><xsl:if test="$hSelected=15" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>15</option>
	<option value='16'><xsl:if test="$hSelected=16" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>16</option>
	<option value='17'><xsl:if test="$hSelected=17" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>17</option>
	<option value='18'><xsl:if test="$hSelected=18" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>18</option>
	<option value='19'><xsl:if test="$hSelected=19" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>19</option>
	<option value='20'><xsl:if test="$hSelected=20" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>20</option>
	<option value='21'><xsl:if test="$hSelected=21" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>21</option>
	<option value='22'><xsl:if test="$hSelected=22" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>22</option>
	<option value='23'><xsl:if test="$hSelected=23" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>23</option>
  </xsl:template>
</xsl:stylesheet>
				