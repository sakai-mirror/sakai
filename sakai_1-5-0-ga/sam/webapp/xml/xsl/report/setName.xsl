<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="../result/identity.xsl"/>
    <xsl:param name="name"/>
    <xsl:template match="name">
        <xsl:copy>
            <xsl:value-of select="$name"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
