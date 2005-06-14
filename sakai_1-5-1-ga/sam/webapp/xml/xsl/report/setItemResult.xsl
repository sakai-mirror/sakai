<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="../result/identity.xsl"/>
    <xsl:param name="item_id"/>
    <xsl:param name="item_result"/>
    <xsl:template match="item_result[@ident_ref=$item_id]">
        <xsl:copy-of select="$item_result"/>
    </xsl:template>
</xsl:stylesheet>
