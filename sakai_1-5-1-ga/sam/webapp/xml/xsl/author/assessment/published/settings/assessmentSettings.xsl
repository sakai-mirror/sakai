<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: assessmentSettings.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="qtimetadata.xsl"/>
  <xsl:import href="assessmentIntroduction.xsl"/>
  <xsl:import href="assessmentOrganization.xsl"/>
  <xsl:import href="assessmentReleasedTo.xsl"/>
  <xsl:import href="settingsRecordingData.xsl"/>
  <xsl:import href="deliveryDates.xsl"/>
  <xsl:import href="feedback.xsl"/>
  <xsl:import href="grading.xsl"/>
  <xsl:import href="graphics.xsl"/>
  <xsl:import href="highSecurity.xsl"/>
  <xsl:import href="metadata.xsl"/>
  <xsl:import href="submissionMessage.xsl"/>
  <xsl:import href="submissions.xsl"/>
  <xsl:import href="timedAssessment.xsl"/>
  <xsl:import href="qtimetadata.xsl"/>
  <xsl:import href="../../../../layout/header.xsl"/>
  <xsl:import href="../../../../commonTemplates/createHourDropdown.xsl"/>
  <xsl:import href="../../../../commonTemplates/createMinuteDropdown.xsl"/>
  <xsl:import href="../../../../commonTemplates/createDateDropdown.xsl"/>
  <xsl:import href="../../../../commonTemplates/createMonthDropdown.xsl"/>
  <xsl:import href="../../../../commonTemplates/createYearDropdown.xsl"/>
  <!-- This template processes the root node ("/") -->
  <xsl:template match="/">
    <html>
      <head>
        <title>Sakai Assessment Setting Display - Author mode</title>
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
        <script src="{$base}js/navigo.js" type="text/javascript"/>
        <!--Tree Menu imports -->
        <script src="{$base}js/tree.js" type="text/javascript"/>
        <script src="{$base}js/tree_items.js" type="text/javascript"/>
        <script src="{$base}js/tree_tpl.js" type="text/javascript"/>
        <!--color picker imports -->
        <script src="{$base}js/picker.js" type="text/javascript"/>
        <!--Calendar imports -->
        <script src="{$base}js/calendar2.js" type="text/javascript"/>
        <!-- HTML AREA SETTINGS -->
        <link href="{$base}htmlarea/htmlarea.css" rel="STYLESHEET" type="TEXT/CSS"/>
        <!-- load the main HTMLArea files  -->
        <script src="{$base}htmlarea/htmlarea.js" type="text/javascript"/>
        <script src="{$base}htmlarea/lang/en.js" type="text/javascript"/>
        <script src="{$base}htmlarea/dialog.js" type="text/javascript"/>
        <script src="{$base}htmlarea/popups/popup.js" type="text/javascript"/>
        <!-- <script type="text/javascript" src="popupdiv.js"></script> -->
        <script src="{$base}htmlarea/popupwin.js" type="text/javascript"/>
        <script src="{$base}htmlarea/navigo_js/navigo_editor.js" type="text/javascript"/>
        <script src="{$base}js/navigo.js" type="text/javascript"/>
        <script src="{$base}js/selectbox.js" type="text/javascript"/>
        <!-- load the plugins -->
        <xsl:variable name="root" select="concat($base,'htmlarea/')"/>
         <xsl:variable name="descXpath">
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName">DESCRIPTION</xsl:with-param>
            <xsl:with-param name="return_xpath">xpath</xsl:with-param>
          </xsl:apply-templates>
        </xsl:variable>
        <xsl:variable name="ipXpath">
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'ALLOW_IP'"/>
            <xsl:with-param name="return_xpath" select="'xpath'"/>
          </xsl:apply-templates>
        </xsl:variable>
        <xsl:variable name="temp">
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
              <xsl:with-param name="keyName" select="'CONSIDER_DURATION'"/>
              <xsl:with-param name="return_xpath" select="'xpath'"/>
            </xsl:apply-templates>
          </xsl:variable>
        <xsl:variable name="xpathDuration" select="substring($temp,6)"/>
         <xsl:variable name="temp1">
         <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
              <xsl:with-param name="keyName" select="'AUTO_SUBMIT'"/>
              <xsl:with-param name="return_xpath" select="'xpath'"/>
            </xsl:apply-templates></xsl:variable>           
          <xsl:variable name="xpathAutoSubmit" select="substring($temp1,6)"/>
        <script type="text/javascript"> 
        //HTMLArea.loadPlugin("TableOperations","<xsl:value-of select="$root"/>" );
       //  HTMLArea.loadPlugin("SpellChecker", "<xsl:value-of select="$root"/>"); 
         ta_editor = []; 
         var hidden = []; 
         var textAreaNames = []; 
         var runHide=true;
         function startup() 
           { 
            // loadEditor("<xsl:value-of select="$root"/>",1,0,"two"); 
             hideUnhideAllDivs(12,"none");
             sortTextarea("<xsl:value-of select="$ipXpath"/>", "  ");
            /* disableFieldDependingOn("<xsl:value-of select="$xpathAutoSubmit"/>","<xsl:value-of select="$xpathDuration"/>",false);
             disableDuration=getelm("<xsl:value-of select="$xpathDuration"/>");
              if(disableDuration)
              	    {
              	       disableDuration.disabled=true;
              	    }
              */	    
             
           }
           function sortTextarea(fld,  demiliter) {
             fldObj=getelm(fld);
             if(fldObj) 
             {
              string  = " " +fldObj.value;
              splitArray = string.split(demiliter);
              splitIndex = splitArray.length;
          
          	  var temp = '';
              for (var i=0; i&lt;splitIndex; i++)
                  temp += splitArray[i] + '\n';
              if (escape(temp.substring(0,1) == '%0A'))
                  fldObj.value = temp.substring(1,temp.length);
              else
                  fldObj.value = temp;
            }      
          }          
           function collectHtmlAreasAndUpdateAssignedTo(AssignedTo)
            {
              //updateAssignedTo
              changeListToCVS(AssignedTo, "selectedList");
              //collectHTMLAREAS
                for (var i = 1; i &lt; ta_editor.length; i++)
                {
                   var editor = ta_editor[i];
                   editor._textArea.value = editor.getHTML();
//                   alert(editor._textArea.value); 
                }
            }
          
          function changeListToCVS(field,listName)
             {
             obj=getelm(listName);
             var valueOfSelect="";
             for (var i=0; i&lt; obj.options.length; i++) 
             {
    							valueOfSelect = valueOfSelect + obj.options[i].value ;
    							if(i != (obj.options.length - 1))
    								{valueOfSelect = valueOfSelect + ",";}
    				  }
    		 		  var assigned_to = getelm(field);
    				  assigned_to.value = valueOfSelect;
    				}
            function hideUnhideAllDivs(maxDivs,action)
            {
              if(runHide==true)
              {
                runHide=false;
              	for(i=1; i &lt;(maxDivs + 1); i++)
              	{
              		tmpdiv = "div"+i;
              		var divisionNo = getelm(tmpdiv);
              	  if(divisionNo)
                  {
              	     divisionNo.style.display=action;
              	  }
              	}
              }
            };
             function showHideDiv(divNo)
            {  
                var tmpdiv = "div" + divNo;
                var tmpimg = "img" + divNo;
             		var divisionNo = getelm(tmpdiv);
            		var imgNo = getelm(tmpimg);
                if(divisionNo)
                {
              		if(divisionNo.style.display =="block")
              		    {
               	      	 divisionNo.style.display="none";
               	      	 imgNo.src ="<xsl:value-of select="$base"/>"+"images/right_arrow.gif";
              	 	    }
              		    else
              		    {
              	 	 	    divisionNo.style.display ="block"; 
              	      	imgNo.src ="<xsl:value-of select="$base"/>"+"images/down_arrow.gif";
                       } 
                 }    
            	 };
            	 function disableFieldDependingOn(fieldDependent, mainField, setDependantFieldValue)
            	 {
            	 var mainFld = getelm(mainField);
            	 var fld = getelm(fieldDependent);
            	   if(fld &amp;&amp; mainFld)
            	   {
            	     if(mainFld.checked==true)
              	    {
              	       fld.disabled=false;
              	    }
              	     else
              	     {
              	       fld.disabled=true;
              	        fld.checked =false;
              	     }
            	   }
            	 };
        
     </script>
     </head>
      <body onload="javascript:startup();" onFocus="javascript:hideUnhideAllDivs(12,'none');">
<!--        <xsl:variable name="title" select="stxx/questestinterop/assessment/@title"/>-->
        <xsl:variable name="title" select="/stxx/form/publishedAssessmentActionForm/publishedName"/>
        <xsl:call-template name="header">
          <xsl:with-param name="displayText" select="concat('Test Settings: -', $title)"/>
        </xsl:call-template>
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
        <xsl:variable name="formname1" select="concat($base,'asi/author/assessment/assessmentAction.do')"/>
        <xsl:variable name="formname" select="concat($base,'asi/author/assessment/published/publishedAssessmentAction.do')"/>
        <table align="center" border="0" cellpadding="4" cellspacing="0" width="90%">
          <tr>
            <td align="left"><br/>
              <h3>Test Settings: <xsl:value-of select="$title"/> </h3>
            </td> </tr>
            <form action="{$formname}" method="post" name="assessmentSettingAction">
                  <xsl:variable name="ident" select="/stxx/questestinterop/assessment/@ident"/>
                  <input name="assessmentID" type="hidden" value="{$ident}"/>
                 <input name="publishedID" type="hidden">
                   <xsl:attribute name="value"><xsl:value-of select="/stxx/form/publishedAssessmentActionForm/publishedID"/></xsl:attribute>
                   </input>
                  <xsl:variable name="subTableWidth">97%</xsl:variable>
                  <xsl:variable name="subTableFirstColWidth">30%</xsl:variable>
                  <xsl:variable name="subTableAlign">center</xsl:variable>
                  <!-- *********************************************************************** -->
                  <tr>
                    <td>
                      <br/>
                    </td>
                  </tr>
                  <!-- **************************Assessment Introduction ********************************************* -->
                   <tr>
                    <td class="navigo_border" style="cursor:pointer;" onClick="javascript:showHideDiv(1);">
                      <img id="img1"  alt="" src="{$base}images/right_arrow.gif" style="cursor:pointer;"/>
                         Assessment Introduction 
                    </td>
                  </tr>    
                  
                   <tr>
                    <td>
                      <div id="div1">
                         <xsl:call-template name="assessmentIntroduction">
                          <xsl:with-param name="tableWidth"><xsl:value-of select="$subTableWidth"/></xsl:with-param>
                          <xsl:with-param name="firstColWidth"><xsl:value-of select="$subTableFirstColWidth"/></xsl:with-param>
                          <xsl:with-param name="tableAlign"><xsl:value-of select="$subTableAlign"/></xsl:with-param>
                        </xsl:call-template>  
                       </div>
                    </td>
                  </tr>
                  <!-- **************************** Delivery Dates  ******************************************* -->
                  <tr>
                    <td class="navigo_border" style="cursor:pointer;" onClick="javascript:showHideDiv(2);">
                      <img id="img2" alt="" src="{$base}images/right_arrow.gif"/>
                           Delivery Dates                
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <div id="div2">
                          <xsl:call-template name="deliveryDates">
                          <xsl:with-param name="tableWidth"><xsl:value-of select="$subTableWidth"/></xsl:with-param>
                          <xsl:with-param name="firstColWidth"><xsl:value-of select="$subTableFirstColWidth"/></xsl:with-param>
                          <xsl:with-param name="tableAlign"><xsl:value-of select="$subTableAlign"/></xsl:with-param>
                        </xsl:call-template>  
                      </div>
                    </td>
                  </tr>
                  <!-- *********************************Assessment released to:************************************** -->
                  <tr>
                    <td class="navigo_border" style="cursor:pointer;" onClick="javascript:showHideDiv(3);">
                      <img id="img3" alt="" src="{$base}images/right_arrow.gif"/> Assessment released to:                 
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <div id="div3" name="div3">
                        <xsl:call-template name="assessmentReleasedTo"/>
                      </div>
                    </td>
                  </tr>
                  <!-- *************************** High Security******************************************** -->
                      <!-- Show IP -->
                      <xsl:variable name="showIP">
                        <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                          <xsl:with-param name="req_fieldlabel">EDIT_ALLOW_IP</xsl:with-param>
                        </xsl:apply-templates>
                      </xsl:variable>
                      <!-- Show UserID-->
                      <xsl:variable name="showUsername">
                        <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                          <xsl:with-param name="req_fieldlabel">EDIT_USERID</xsl:with-param>
                        </xsl:apply-templates>
                      </xsl:variable>
                       <xsl:variable name="showHighSecurity">
                        <xsl:choose>
                          <xsl:when test="$showUsername='SHOW'">SHOW</xsl:when>
                          <xsl:when test="$showIP='SHOW'">SHOW</xsl:when>
                          <xsl:otherwise>HIDE</xsl:otherwise>                        
                        </xsl:choose>
                      </xsl:variable>
                    <xsl:if test="$showHighSecurity='SHOW'">
                  <tr>
                    <td class="navigo_border" style="cursor:pointer;" onClick="javascript:showHideDiv(4);">
                      <img id="img4"  alt="" src="{$base}images/right_arrow.gif"/> High Security
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <div id="div4">
                        <xsl:call-template name="highSecurity">
                          <xsl:with-param name="tableWidth"><xsl:value-of select="$subTableWidth"/></xsl:with-param>
                          <xsl:with-param name="firstColWidth"><xsl:value-of select="$subTableFirstColWidth"/></xsl:with-param>
                          <xsl:with-param name="tableAlign"><xsl:value-of select="$subTableAlign"/></xsl:with-param>
                        </xsl:call-template>  
                      </div>
                    </td>
                  </tr></xsl:if>
                  <!-- ********************************Timed Assessment************************************** -->
                  <!-- Show Duration  -->
                    <xsl:variable name="showDuration">
                      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                        <xsl:with-param name="req_fieldlabel">EDIT_DURATION</xsl:with-param>
                      </xsl:apply-templates>
                    </xsl:variable>
                   <xsl:if test="$showDuration='SHOW'">
                  <tr>
                    <td class="navigo_border" style="cursor:pointer;" onClick="javascript:showHideDiv(5);">
                      <img id="img5" alt="" src="{$base}images/right_arrow.gif"/>  Timed Assessment
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <div id="div5">
                          <xsl:call-template name="timedAssessment">
                          <xsl:with-param name="tableWidth"><xsl:value-of select="$subTableWidth"/></xsl:with-param>
                          <xsl:with-param name="firstColWidth"><xsl:value-of select="$subTableFirstColWidth"/></xsl:with-param>
                          <xsl:with-param name="tableAlign"><xsl:value-of select="$subTableAlign"/></xsl:with-param>
                        </xsl:call-template>  
                      </div>
                    </td>
                  </tr>
                  </xsl:if>
                  <!-- ************************* Assessment Organization ********************************************** -->
                  <!-- Show Navigation -->
                  <xsl:variable name="showNavigation">
                    <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                      <xsl:with-param name="req_fieldlabel">EDIT_NAVIGATION</xsl:with-param>
                    </xsl:apply-templates>
                  </xsl:variable>
                  <!-- Show Question Layout -->
                  <xsl:variable name="showQuestionLayout">
                    <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                      <xsl:with-param name="req_fieldlabel">EDIT_QUESTION_LAYOUT</xsl:with-param>
                    </xsl:apply-templates>
                  </xsl:variable>
                  <!-- Show QUESTION_NUMBERING -->
                  <xsl:variable name="showQuestionNumbering">
                    <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                      <xsl:with-param name="req_fieldlabel">EDIT_QUESTION_NUMBERING</xsl:with-param>
                    </xsl:apply-templates>
                  </xsl:variable>    
                  <xsl:variable name="showAssessmentOrganization">
                  <xsl:choose>
                    <xsl:when test="$showNavigation='SHOW'">SHOW</xsl:when>
                    <xsl:when test="$showQuestionLayout='SHOW'">SHOW</xsl:when>
                    <xsl:when test="$showQuestionNumbering='SHOW'">SHOW</xsl:when>
                    <xsl:otherwise>HIDE</xsl:otherwise>                        
                  </xsl:choose>
                  </xsl:variable>
                  <xsl:if test="$showAssessmentOrganization='SHOW'">
                  <tr>
                    <td class="navigo_border" style="cursor:pointer;" onClick="javascript:showHideDiv(6);">
                      <img id="img6"  alt="" src="{$base}images/right_arrow.gif"/> Assessment Organization               
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <div id="div6">
                          <xsl:call-template name="assessmentOrganization">
                          <xsl:with-param name="tableWidth"><xsl:value-of select="$subTableWidth"/></xsl:with-param>
                          <xsl:with-param name="firstColWidth"><xsl:value-of select="$subTableFirstColWidth"/></xsl:with-param>
                          <xsl:with-param name="tableAlign"><xsl:value-of select="$subTableAlign"/></xsl:with-param>
                        </xsl:call-template>  
                      </div>
                    </td>
                  </tr>
                  </xsl:if>
                  <!-- *****************************************Submissions  ****************************** -->
                      <!-- Show Due Date -->
                      <xsl:variable name="showMaxAttempts">
                        <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                          <xsl:with-param name="req_fieldlabel">EDIT_MAX_ATTEMPS</xsl:with-param>
                        </xsl:apply-templates>
                      </xsl:variable>
                      <!-- Show Retract Date -->
                      <xsl:variable name="showLateHandling">
                        <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                          <xsl:with-param name="req_fieldlabel">EDIT_LATE_HANDLING</xsl:with-param>
                        </xsl:apply-templates>
                      </xsl:variable>
                   <xsl:variable name="showSubmissions">
                    <xsl:choose>
                      <xsl:when test="$showMaxAttempts='SHOW'">SHOW</xsl:when>
                      <xsl:when test="$showLateHandling='SHOW'">SHOW</xsl:when>
                      <xsl:otherwise>HIDE</xsl:otherwise>                        
                    </xsl:choose>
                    </xsl:variable>
                  <xsl:if test="$showSubmissions='SHOW'">
                  <tr>
                    <td class="navigo_border" style="cursor:pointer;" onClick="javascript:showHideDiv(7);">
                      <img id="img7"  alt="" src="{$base}images/right_arrow.gif"/> Submissions                
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <div id="div7">
                         <xsl:call-template name="submissions">
                          <xsl:with-param name="tableWidth"><xsl:value-of select="$subTableWidth"/></xsl:with-param>
                          <xsl:with-param name="firstColWidth"><xsl:value-of select="$subTableFirstColWidth"/></xsl:with-param>
                          <xsl:with-param name="tableAlign"><xsl:value-of select="$subTableAlign"/></xsl:with-param>
                        </xsl:call-template>  
                      </div>
                    </td>
                  </tr>
                   </xsl:if>
                  <!-- ******************************Submission Message ***************************************** -->
                   <!-- Show CONSIDER_ASSESSFEEDBACK -->
                    <xsl:variable name="showConsiderAssessFeedback">
                      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                        <xsl:with-param name="req_fieldlabel">EDIT_ASSESSFEEDBACK</xsl:with-param>
                      </xsl:apply-templates>
                    </xsl:variable>
                    <!-- Show FINISH_URL -->
                    <xsl:variable name="showFinishURL">
                      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                        <xsl:with-param name="req_fieldlabel">EDIT_FINISH_URL</xsl:with-param>
                      </xsl:apply-templates>
                    </xsl:variable>
                    <xsl:variable name="showSubmissionsMsg">
                    <xsl:choose>
                      <xsl:when test="$showConsiderAssessFeedback='SHOW'">SHOW</xsl:when>
                      <xsl:when test="$showFinishURL='SHOW'">SHOW</xsl:when>
                      <xsl:otherwise>HIDE</xsl:otherwise>                        
                    </xsl:choose>
                    </xsl:variable>
                  <xsl:if test="$showSubmissionsMsg='SHOW'">
                  <tr>
                    <td class="navigo_border" style="cursor:pointer;" onClick="javascript:showHideDiv(8);">
                      <img  id="img8"  alt="" src="{$base}images/right_arrow.gif"/> Submission Message             
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <div id="div8">
                       <xsl:call-template name="submissionMessage">
                          <xsl:with-param name="tableWidth"><xsl:value-of select="$subTableWidth"/></xsl:with-param>
                          <xsl:with-param name="firstColWidth"><xsl:value-of select="$subTableFirstColWidth"/></xsl:with-param>
                          <xsl:with-param name="tableAlign"><xsl:value-of select="$subTableAlign"/></xsl:with-param>
                        </xsl:call-template>  
                      </div>
                    </td>
                  </tr>
                  </xsl:if>
                  <!-- *******************************Feedback**************************************** -->
                    <xsl:variable name="showFeedbkDelivery">
                      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                        <xsl:with-param name="req_fieldlabel">EDIT_FEEDBACK_DELIVERY</xsl:with-param>
                      </xsl:apply-templates>
                    </xsl:variable>
                    <!-- Show -->
                    <xsl:variable name="showFeedbkComponents">
                      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                        <xsl:with-param name="req_fieldlabel">EDIT_FEEDBACK_COMPONENTS</xsl:with-param>
                      </xsl:apply-templates>
                   </xsl:variable>
                   <xsl:variable name="showFeedback">
                    <xsl:choose>
                      <xsl:when test="$showFeedbkDelivery='SHOW'">SHOW</xsl:when>
                      <xsl:when test="$showFeedbkComponents='SHOW'">SHOW</xsl:when>
                      <xsl:otherwise>HIDE</xsl:otherwise>                        
                    </xsl:choose>
                    </xsl:variable>
                  <xsl:if test="$showFeedback='SHOW'">
                  <tr>
                    <td class="navigo_border" style="cursor:pointer;" onClick="javascript:showHideDiv(9);">
                      <img  id="img9"  alt="" src="{$base}images/right_arrow.gif"/>  Feedback           
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <div id="div9">
                         <xsl:call-template name="feedback">
                          <xsl:with-param name="tableWidth"><xsl:value-of select="$subTableWidth"/></xsl:with-param>
                          <xsl:with-param name="firstColWidth"><xsl:value-of select="$subTableFirstColWidth"/></xsl:with-param>
                          <xsl:with-param name="tableAlign"><xsl:value-of select="$subTableAlign"/></xsl:with-param>
                        </xsl:call-template> 
                      </div>
                    </td>
                  </tr></xsl:if>
                  <!-- ******************************Grading***************************************** -->
                  <!-- Show Annonymous Grading-->
                    <xsl:variable name="showAnonymous">
                      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                        <xsl:with-param name="req_fieldlabel">EDIT_ANONYMOUS_GRADING</xsl:with-param>
                      </xsl:apply-templates>
                    </xsl:variable>
                    <!-- Show Gradebook Options-->
                    <xsl:variable name="showGradebookOptions">
                      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                        <xsl:with-param name="req_fieldlabel">EDIT_GRADEBOOK_OPTIONS</xsl:with-param>
                      </xsl:apply-templates>
                    </xsl:variable>
                    <!-- Show Grade Score -->
                    <xsl:variable name="showGradebookScore">
                      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                        <xsl:with-param name="req_fieldlabel">EDIT_GRADE_SCORE</xsl:with-param>
                      </xsl:apply-templates>
                    </xsl:variable>
                 <xsl:variable name="showGrading">
                  <xsl:choose>
                    <xsl:when test="$showAnonymous='SHOW'">SHOW</xsl:when>
                    <xsl:when test="$showGradebookOptions='SHOW'">SHOW</xsl:when>
                    <xsl:when test="$showGradebookScore='SHOW'">SHOW</xsl:when>
                    <xsl:otherwise>HIDE</xsl:otherwise>                        
                  </xsl:choose>
                  </xsl:variable>
                  <xsl:if test="$showGrading='SHOW'">
                  <tr>
                    <td class="navigo_border" style="cursor:pointer;" onClick="javascript:showHideDiv(10);">
                      <img id="img10"   alt="" src="{$base}images/right_arrow.gif"/>  Grading             
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <div id="div10">
                       <xsl:call-template name="grading">
                          <xsl:with-param name="tableWidth"><xsl:value-of select="$subTableWidth"/></xsl:with-param>
                          <xsl:with-param name="firstColWidth"><xsl:value-of select="$subTableFirstColWidth"/></xsl:with-param>
                          <xsl:with-param name="tableAlign"><xsl:value-of select="$subTableAlign"/></xsl:with-param>
                        </xsl:call-template> 
                      </div>
                    </td>
                  </tr>
                  </xsl:if>
                <!-- *****************************Graphics****************************************** -->
                   <tr>
                    <td class="navigo_border" style="cursor:pointer;" onClick="javascript:showHideDiv(11);">
                      <img id="img11"   alt="" src="{$base}images/right_arrow.gif"/>  Graphics             
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <div id="div11">
                          <xsl:call-template name="graphics">
                          <xsl:with-param name="tableWidth"><xsl:value-of select="$subTableWidth"/></xsl:with-param>
                          <xsl:with-param name="firstColWidth"><xsl:value-of select="$subTableFirstColWidth"/></xsl:with-param>
                          <xsl:with-param name="tableAlign"><xsl:value-of select="$subTableAlign"/></xsl:with-param>
                        </xsl:call-template> 
                      </div>
                    </td>
                  </tr>
                  <!-- ********************************Metadata*************************************** -->
                      <!-- Show COLLECT_ASSESSMENT_METADATA-->
                      <xsl:variable name="showAssessmentMetadata">
                        <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                          <xsl:with-param name="req_fieldlabel">EDIT_ASSESSMENT_METADATA</xsl:with-param>
                        </xsl:apply-templates>
                      </xsl:variable>
                      <!-- Show COLLECT_SECTION_METADATA -->
                      <xsl:variable name="showSectionMetadata">
                        <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                          <xsl:with-param name="req_fieldlabel">EDIT_COLLECT_SECTION_METADATA</xsl:with-param>
                        </xsl:apply-templates>
                      </xsl:variable>
                      <!-- Show COLLECT_ITEM_METADATA-->
                      <xsl:variable name="showItemMetadata">
                        <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                          <xsl:with-param name="req_fieldlabel">EDIT_COLLECT_ITEM_METADATA</xsl:with-param>
                        </xsl:apply-templates>
                      </xsl:variable>
                     <xsl:variable name="showMetadata">
                    <xsl:choose>
                      <xsl:when test="$showAssessmentMetadata='SHOW'">SHOW</xsl:when>
                      <xsl:when test="$showSectionMetadata='SHOW'">SHOW</xsl:when>
                      <xsl:when test="$showItemMetadata='SHOW'">SHOW</xsl:when>
                      <xsl:otherwise>HIDE</xsl:otherwise>                        
                    </xsl:choose>
                    </xsl:variable>
                  <xsl:if test="$showMetadata='SHOW'">
                  <tr>
                    <td class="navigo_border" style="cursor:pointer;" onClick="javascript:showHideDiv(12);">
                    <img  id="img12" alt="" src="{$base}images/right_arrow.gif" /> Metadata            
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <div id="div12">
                          <xsl:call-template name="metadata">
                          <xsl:with-param name="tableWidth"><xsl:value-of select="$subTableWidth"/></xsl:with-param>
                          <xsl:with-param name="firstColWidth"><xsl:value-of select="$subTableFirstColWidth"/></xsl:with-param>
                          <xsl:with-param name="tableAlign"><xsl:value-of select="$subTableAlign"/></xsl:with-param>
                        </xsl:call-template>  
                      </div>
                    </td>
                  </tr></xsl:if>
                  <!-- *********************************************************************** -->
                  <tr>
                    <td>
                      <br/>
                    </td>
                  </tr>
                  <!-- **********************************Publish************************************* -->
                  <xsl:variable name="assignToXpath">
                    <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                      <xsl:with-param name="keyName" select="'ASSESSMENT_RELEASED_TO'"/>
                      <xsl:with-param name="return_xpath" select="'xpath'"/>
                    </xsl:apply-templates>
                  </xsl:variable>
                   <input type="hidden" name="assignedTo" />
                 <xsl:variable name="isActive" select="/stxx/form/publishedAssessmentActionForm/isActive"/>
                  <!--only active assessments can be edited -->
                  <tr>
                    <td align="center"><xsl:if test="$isActive='true'">
                      <input align="left" name="action" type="submit" value="Change Settings">
                        <xsl:attribute name="onclick">javascript:collectHtmlAreasAndUpdateAssignedTo("assignedTo");onSubmitFn();</xsl:attribute>
                      </input> </xsl:if><xsl:if test="$isActive!='true'"><span class="alert"> Only Published and Active assessments can be edited.</span>
                         </xsl:if> &#160;&#160;&#160;&#160;
                    
                      <input align="left" name="action" type="submit" value="Cancel"/>
                    </td>
                  </tr>
                 </form>
                <script type="text/javascript"> 
                     var stDay = getelm("publish_start_day");
                     if(stDay)
                     {
                       var cal1 = new calendar2(stDay); 
                       cal1.year_scroll = true; 
                       cal1.time_comp = false; 
                     }
                     
                     var endDay = getelm("publish_end_day");
                     if(endDay)
                     {
                       var cal2 = new calendar2(endDay); 
                       cal2.year_scroll = true; 
                       cal2.time_comp = false; 
                     }
                     
                     var retractDay = getelm("publish_retract_day");
                     if(retractDay)
                     {
                       var cal3 = new calendar2(retractDay); 
                       cal3.year_scroll = true; 
                       cal3.time_comp = false; 
                     }
                     
                     var deliveryDay = getelm("feedback_delivery_day");
                     if(deliveryDay)
                     {
                       var cal4 = new calendar2(deliveryDay); 
                       cal4.year_scroll = true; 
                       cal4.time_comp = false;   
                     }
                </script>
              </table>
       </body>
    </html>
  </xsl:template>
  <!--****************************************************************************** -->
</xsl:stylesheet>
