<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.01//EN"
 doctype-system="http://www.w3.org/TR/html4/strict.dtd"/>
<xsl:template match="/stxx">
    <html>
        <head>
            <title>Review Results</title>
            <style type="text/css">
body, td { font-family: verdana; font-size: 10pt; }
td { text-align: left; }
div.center { text-align: center; }
table { border-style: solid; border-color: black; border-width: 2px; }
h3 { background-color: #ccccff; padding: 5px; border-style: solid; border-color: black; border-width: 1px; }
div.item {margin-top: 10px; margin-bottom: 10px; border-top: 2px solid black; }
div.item_number { float: left; margin-left: 30px; }
div.item_content { margin-left: 100px; }
</style>
        </head>
        <body>
        	<h3>Review Results - <xsl:value-of select="/stxx/questestinterop/assessment/@title"/></h3>
        	<div class="center">
        	<table>
        		<tr>
        			<td>Name:</td>
        			<td>Alpha Tester</td>
        		</tr>
        		<tr>
        			<td>Points Earned:</td>
        			<td><xsl:value-of select="format-number(sum(/stxx/qti_result_report/result//item_result/outcomes/score/score_value), '0.00')"/></td>
        		</tr>
        		<tr>
        			<td>Points Possible:</td>
        			<td><xsl:value-of select="format-number(sum(/stxx/qti_result_report/result//item_result/outcomes/score/max_score), '0.00')"/></td>
        		</tr>
        	</table>
        	</div>
        	<form method="post" action="gradeResult.do">
            <xsl:apply-templates select="/stxx/questestinterop//item"/>
            </form>
        </body>
    </html>
</xsl:template>

<xsl:template match="item">
	<div class="item">
	<div class="item_number">
		<strong><xsl:number value="position()" format="1."/></strong>
	</div>
    <div class="item_content">
        <xsl:apply-templates select="presentation"/>
        <xsl:variable name="item_ident" select="@ident"/>
        <xsl:apply-templates select="/stxx/qti_result_report//item_result[attribute::ident_ref=$item_ident]"/>
    </div>
    </div>
</xsl:template>

<xsl:template match="item_result">
	<xsl:variable name="item_ident" select="@ident_ref"/>
	<p>
		<xsl:apply-templates select="response" mode="response_type">
			<xsl:with-param name="item_ident" select="$item_ident"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="outcomes">
			<xsl:with-param name="item_ident" select="$item_ident"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="feedback_displayed">
			<xsl:with-param name="item_ident" select="$item_ident"/>
		</xsl:apply-templates>
	</p>
</xsl:template>

<xsl:template match="response">
	<xsl:param name="item_ident"/>
	<xsl:variable name="response_ident" select="@ident_ref"/>
	<xsl:variable name="render_type" select="response_form/@render_type"/>
	<xsl:variable name="cardinality" select="response_form/@cardinality"/>
	<strong>Response: </strong>
	<xsl:apply-templates select="response_value">
	    <xsl:with-param name="item_ident" select="$item_ident"/>
		<xsl:with-param name="response_ident" select="$response_ident"/>
	</xsl:apply-templates><br/>
	<strong>Correct: </strong>
	<xsl:apply-templates select="response_form/correct_response">
		<xsl:with-param name="item_ident" select="$item_ident"/>
		<xsl:with-param name="response_ident" select="$response_ident"/>
	</xsl:apply-templates><br/>
</xsl:template>

<xsl:template match="response[child::response_form/attribute::response_type='lid']" mode="response_type">
    <xsl:param name="item_ident"/>
    <xsl:variable name="response_ident" select="@ident_ref"/>
    <xsl:variable name="response_labels" select="/stxx/questestinterop/assessment//item[@ident=$item_ident]//response_lid[@ident=$response_ident]//response_label"></xsl:variable>
    <xsl:apply-templates select="current()" mode="cardinality">
        <xsl:with-param name="response_labels" select="$response_labels"/>
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="response[child::response_form/attribute::response_type='grp']" mode="response_type">
    <xsl:param name="item_ident"/>
    <xsl:variable name="response_ident" select="@ident_ref"/>
    <xsl:variable name="response_labels" select="/stxx/questestinterop/assessment//item[@ident=$item_ident]//response_grp[@ident=$response_ident]//response_label"></xsl:variable>
    <xsl:apply-templates select="current()" mode="cardinality">
        <xsl:with-param name="response_labels" select="$response_labels"/>
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="response[child::response_form/attribute::response_type='str']" mode="response_type">
    <xsl:param name="item_ident"/>
    <xsl:variable name="response_ident" select="@ident_ref"/>
    <xsl:variable name="response_labels" select="/stxx/questestinterop/assessment//item[@ident=$item_ident]//response_str[@ident=$response_ident]//response_label"></xsl:variable>
    <xsl:apply-templates select="current()" mode="cardinality">
        <xsl:with-param name="response_labels" select="$response_labels"/>
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="response[child::response_form/attribute::cardinality='Single']" mode="cardinality">
    <xsl:param name="response_labels"/>
    <strong>Response: </strong>
    <xsl:apply-templates select="response_value" mode="cardinality">
        <xsl:sort select="current()"/>
        <xsl:with-param name="response_labels" select="$response_labels"/>
    </xsl:apply-templates><br/>
    <strong>Correct: </strong>
    <xsl:apply-templates select="response_form/correct_response" mode="cardinality">
        <xsl:with-param name="response_labels" select="$response_labels"/>
    </xsl:apply-templates><br/>
</xsl:template>

<xsl:template match="response[child::response_form/attribute::cardinality='Multiple']" mode="cardinality">
    <xsl:param name="response_labels"/>
    <strong>Response: </strong>
    <xsl:apply-templates select="response_value" mode="cardinality">
        <xsl:sort select="current()"/>
        <xsl:with-param name="response_labels" select="$response_labels"/>
    </xsl:apply-templates><br/>
    <strong>Correct: </strong>
    <xsl:apply-templates select="response_form/correct_response" mode="cardinality">
        <xsl:with-param name="response_labels" select="$response_labels"/>
    </xsl:apply-templates><br/>
</xsl:template>

<xsl:template match="response[child::response_form/attribute::cardinality='Ordered']" mode="cardinality">
    <xsl:param name="response_labels"/>
    <strong>Response: </strong>
    <xsl:apply-templates select="response_value" mode="cardinality">
        <xsl:with-param name="response_labels" select="$response_labels"/>
    </xsl:apply-templates><br/>
    <strong>Correct: </strong>
    <xsl:apply-templates select="response_form/correct_response" mode="cardinality">
        <xsl:with-param name="response_labels" select="$response_labels"/>
    </xsl:apply-templates><br/>
</xsl:template>

<xsl:template name="single_value" match="response_value[parent::response/child::response_form/attribute::cardinality='Single'] | correct_response[parent::response_form/attribute::cardinality='Single']" mode="cardinality">
	<xsl:param name="response_labels"/>
	<xsl:apply-templates select="current()" mode="response_type">
	    <xsl:with-param name="response_labels" select="$response_labels"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template name="multiple_value" match="response_value[parent::response/child::response_form/attribute::cardinality='Multiple'] | correct_response[parent::response_form/attribute::cardinality='Multiple']" mode="cardinality">
	<xsl:param name="response_labels"/>
	<xsl:apply-templates select="current()" mode="response_type">
	    <xsl:with-param name="response_labels" select="$response_labels"/>
	</xsl:apply-templates>
	<xsl:if test="position()!=last()">, </xsl:if>
</xsl:template>

<xsl:template name="ordered_value" match="response_value[parent::response/child::response_form/attribute::cardinality='Ordered'] | correct_response[parent::response_form/attribute::cardinality='Ordered']" mode="cardinality">
	<xsl:param name="response_labels"/>
	<xsl:number value="position()" format="1:"/>
	<xsl:apply-templates select="current()" mode="response_type">
	    <xsl:with-param name="response_labels" select="$response_labels"/>
	</xsl:apply-templates>
	<xsl:if test="position()!=last()">, </xsl:if>
</xsl:template>

<xsl:template name="lid_value" match="response_value[parent::response/child::response_form/attribute::response_type='lid'] | correct_response[parent::response_form/attribute::response_type='lid']" mode="response_type">
    <xsl:param name="response_labels"/>
    <xsl:variable name="lid" select="current()"/>
    <xsl:for-each select="$response_labels">
        <xsl:if test="@ident=$lid">
            <xsl:number value="position()" format="A"/>
        </xsl:if>
    </xsl:for-each>
</xsl:template>

<xsl:template name="grp_value" match="response_value[parent::response/child::response_form/attribute::response_type='grp'] | correct_response[parent::response_form/attribute::response_type='grp']" mode="response_type">
    <xsl:param name="response_labels"/>
    <xsl:call-template name="lid_value">
        <xsl:with-param name="response_labels" select="$response_labels"/>
    </xsl:call-template>
</xsl:template>

<xsl:template name="str_value" match="response_value[parent::response/child::response_form/attribute::response_type='str'] | correct_response[parent::response_form/attribute::response_type='str']" mode="response_type">
    <xsl:param name="response_labels"/>
    <xsl:value-of select="current()"/>
</xsl:template>

<xsl:template match="outcomes">
	<xsl:param name="item_ident"/>
	<strong>Points Earned: </strong>
	<input type="text" size="3">
	    <xsl:attribute name="name">/qti_item_result//item_result[attribute::ident_ref='<xsl:value-of select="$item_ident"/>']/outcomes/score[attribute::varname='SCORE']/score_value</xsl:attribute>
	    <xsl:attribute name="value">
	        <xsl:value-of select="format-number(sum(score/score_value[text()&gt;'0']), '0.00')"/>
	    </xsl:attribute><br/>
	</input>
	<strong>Points Possible: </strong><xsl:value-of select="format-number(sum(score/max_score), '0.00')"/><br/>
</xsl:template>

<xsl:template match="feedback_displayed">
	<xsl:param name="item_ident"/>
	<xsl:variable name="feedback_ident" select="@ident_ref"/>
	<strong>Feedback: </strong><xsl:value-of select="/stxx/questestinterop/assessment//item[@ident=$item_ident]/itemfeedback[attribute::ident=@ident_ref]/descendant::mattext"/>
	<xsl:value-of select="/stxx/questestinterop/assessment//item[@ident=$item_ident]/itemfeedback[@ident=$feedback_ident]/descendant::mattext"/>
</xsl:template>

<xsl:template match="presentation">
	<xsl:value-of select="descendant::mattext/text()"/>
    <xsl:apply-templates select="descendant::response_lid | descendant::response_str | descendant::response_grp"/>
</xsl:template>

<xsl:template match="response_lid">
    <ol type="A" class="choice">
        <xsl:apply-templates select="descendant::response_label"/>
    </ol>
    <xsl:variable name="ident" select="@ident"/>
</xsl:template>

<xsl:template match="response_str">
    <xsl:variable name="ident" select="@ident"/>
</xsl:template>

<xsl:template match="response_grp">
	<ol type="A" class="choice">
		<xsl:apply-templates select="descendant::response_label[attribute::match_group]"/>
	</ol>
	<ol type="1" class="choice">
		<xsl:apply-templates select="descendant::response_label[not(attribute::match_group)]"/>
	</ol>
</xsl:template>

<xsl:template match="response_label">
	<li><xsl:value-of select="descendant::mattext/text()"/></li>
</xsl:template>

<xsl:template match="response_label" mode="response">
	<xsl:apply-templates select="descendant::mattext"/>
	<xsl:apply-templates select="descendant::matimage"/>
</xsl:template>

<xsl:template match="mattext">
    <xsl:value-of select="normalize-space(current())"/>
</xsl:template>

<xsl:template match="matimage">
    <xsl:if test="attribute::uri!=''">
        <br/><img src="attribute::uri"/>
    </xsl:if>
</xsl:template>

</xsl:stylesheet>
