<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="../layout/defaultLayout.xsl"/>
    <xsl:import href="../layout/header.xsl"/>
    <xsl:import href="../layout/footer.xsl"/>
    <xsl:import href="../layout/menu.xsl"/>
    <xsl:template match="/">
        <xsl:call-template name="defaultLayout"/>
    </xsl:template>
    <xsl:template name="body">
        <xsl:variable name="CONTEXT_PATH">
            <xsl:call-template name="baseHREF"/>
        </xsl:variable>
        <table border="0" cellpadding="5">
            <tr>
                <td>
                    <form name="uploadForm" method="post"
                        action="{$CONTEXT_PATH}asi/select/upload.do?queryParam=Successful"
                        enctype="multipart/form-data">Select a file to upload/import:<br/>
                        <input type="file" name="theFile" value=""/>
                        <br/>
                        <br/>
                        <input type="submit" value="Import Assessment"/>
                    </form>
                </td>
            </tr>
        </table>
    </xsl:template>
</xsl:stylesheet>
