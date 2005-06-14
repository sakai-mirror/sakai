<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template match="/">
    <table align="center"><tr><td>
      <br/>  <br/><b>Error Occured</b>
    
    <br/>   <br/>  <br/> <br/>
        The error mesassage is 
    <br/>  <br/>
    <xsl:value-of select="stxx/form/assessmentActionForm/errorMsg"/>
     
    </td></tr></table>
</xsl:template>
</xsl:stylesheet>