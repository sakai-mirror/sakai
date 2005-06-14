<%--show only to assessment editor if one or more options is inst editable--%>
<% if (evaluation.getLength((String) session.getAttribute("editorRole")) > 0) { %>
<table class="tblMain">
  <tr>
    <td class="tdEditorSide" ><a name=evaluation></a><span class="heading2">Grading</span><br>
      <html:link page="/jsp/aam/edit/evaluation.jsp">Edit</html:link>
    </td>
    <td class="tdEditorTopFeature">Feature</td>
    <td class="tdEditorTop">Value</td>
    <logic:equal name="editorRole" value="templateEditor">
      <td class="tdEditorEditView"> Instructor<br>
        Editable</td>
    </logic:equal>
    <td class="tdEditorEditView"> Student<br>
      Viewable</td>
  </tr>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
      (evaluation.getEvaluationDistribution_isInstructorEditable())) { %>
  <% count=0; %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td><bean:message key="evaluation.distribution.to" bundle="option"/></td>
    <td>
      <logic:equal name="evaluation" property="evaluationDistribution_toInstructor" value="on">
        <bean:message key="evaluation.distribution.to.instructor" bundle="option"/>
      </logic:equal>
      <logic:equal name="evaluation" property="evaluationDistribution_toTAs" value="on">,
        <bean:message key="evaluation.distribution.to.tas" bundle="option"/>
      </logic:equal>
      <logic:equal name="evaluation" property="evaluationDistribution_toSectionGrader" value="on">,
        <bean:message key="evaluation.distribution.to.section.grader" bundle="option"/>
      </logic:equal>
      <logic:equal name="evaluation" property="evaluationDistribution_toReviewGroup" value="on">,
        <bean:message key="evaluation.distribution.to.review.group" bundle="option"/>
      </logic:equal>
      <logic:equal name="evaluation" property="evaluationDistribution_toTestee" value="on">,
        <bean:message key="evaluation.distribution.to.testee" bundle="option"/>
      </logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="evaluation" property="evaluationDistribution_isInstructorEditable"
          value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="evaluation" property="evaluationDistribution_isStudentViewable"
          value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
      (evaluation.getTesteeIdentity_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td><bean:message key="evaluation.testee.identity" bundle="option"/></td>
    <td>
    <bean:message key='<%= "evaluation.testee.identity." + evaluation.getTesteeIdentity() %>'
        bundle="option"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="evaluation" property="testeeIdentity_isInstructorEditable"
        value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="evaluation" property="testeeIdentity_isStudentViewable"
        value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
      (evaluation.getEvaluationComponents_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td><bean:message key="evaluation.components" bundle="option"/></td>
    <td>
      <logic:equal name="evaluation" property="evaluationComponents_scores" value="on">
        <bean:message key="evaluation.components.scores" bundle="option"/>
      </logic:equal>
      <logic:equal name="evaluation" property="evaluationComponents_commentsForQuestions"
        value="on">,
        <bean:message key="evaluation.components.comments.for.questions" bundle="option"/>
      </logic:equal>
      <logic:equal name="evaluation" property="evaluationComponents_commentsForParts"
        value="on">,
        <bean:message key="evaluation.components.comments.for.parts" bundle="option"/>
      </logic:equal>
      <logic:equal name="evaluation" property="evaluationComponents_commentForAssess"
        value="on">,
        <bean:message key="evaluation.components.comment.for.assess" bundle="option"/>
      </logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="evaluation" property="evaluationComponents_isInstructorEditable"
            value="true">x</logic:equal>
    </td>
    </logic:equal>
    <td><logic:equal name="evaluation" property="evaluationComponents_isStudentViewable"
          value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
      (evaluation.getAutoScoring_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>
      <bean:message key="evaluation.auto.scoring" bundle="option"/>
    </td>
    <td>
      <bean:message key='<%= "evaluation.auto.scoring." +
        evaluation.getAutoScoring() %>' bundle="option"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="evaluation" property="autoScoring_isInstructorEditable"
            value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="evaluation" property="autoScoring_isStudentViewable"
        value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
      (evaluation.getScoringType_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>
      <bean:message key="evaluation.scoring.type" bundle="option"/>
    </td>
    <!-- default (evaluation.scoring.type.0) is only value supported in beta 2,
    other values are already in options.properties and ready to go -->
    <td><bean:message key="evaluation.scoring.type.0" bundle="option"/></td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="evaluation" property="scoringType_isInstructorEditable"
          value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="evaluation" property="scoringType_isStudentViewable"
        value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
<%-- I don't think that this needs to be in beta 2 according to the mockup and
  text values document --%>
<%--
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
      (evaluation.getNumericModel_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Scoring Model</td>
    <td><bean:write name="evaluation" property="numericModel"/></td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="evaluation" property="numericModel_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="evaluation" property="numericModel_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
      (evaluation.getDefaultQuestionValue_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Default Question Value</td>
    <td><bean:write name="evaluation" property="defaultQuestionValue"/></td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="evaluation" property="defaultQuestionValue_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="evaluation" property="defaultQuestionValue_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
--%>
</table>
<a href="#top">Go to top of page</a>
<% } %>
