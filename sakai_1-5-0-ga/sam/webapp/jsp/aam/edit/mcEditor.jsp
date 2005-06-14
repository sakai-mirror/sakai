  <logic:iterate name="question" id="answerItem" property="answers" type="org.navigoproject.osid.assessment.model.Answer" indexId="ctr">
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2"><%= new Character((char) ('A' + ctr.intValue())).toString() %>. Selection</span></td>
    <td> <html:textarea name="question" property='<%= "answerArray[" + ctr + "].text" %>' /></td>
      <td><html:radio name="question" property='<%= "answerArray[" + ctr + "].isCorrect" %>' value="true" />Correct
      <html:radio name="question" property='<%= "answerArray[" + ctr + "].isCorrect" %>' value="false" />Incorrect
    </td>
  </tr>
  <logic:equal name="question" property="afeedback_isInstructorEditable"
        value="true">
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading3">Feedback if Answer <%= new Character((char) ('A' + ctr.intValue())).toString() %> selected by student</span></td>
    <td colspan=2> <html:textarea name="question" property='<%= "answerArray[" + ctr + "].feedback" %>' />
    </td>
  </tr>
  </logic:equal>
  </logic:iterate>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>

  <td class="tdEditSide"><logic:equal name="question" property="itemType" value="Multiple Choice">
    <!-- this is replaced by the following, a fix for bug #385
  <html:link page="/startQuestionEditor.do" paramId="moreid" paramName="question" paramProperty="id">Add More <bean:write name="question" property="itemType" /> Answers</html:link>
-->
    <a href="#" onClick="javascript:document.forms[0].forwardAction.value='startQuestionEditor';document.forms[0].submit();">Add
    More <bean:write name="question" property="itemType" /> Answers</a><br>
      <br>
      </logic:equal> </td>
    <td class="tdEditTop"colspan=2>&nbsp;</td>
  </tr>
  <logic:equal name="question" property="feedback_isInstructorEditable"
        value="true">
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Solution/Feedback</span><BR>
      <span class="heading3">(Shown as feedback for any response)</span></td>
    <td colspan=2><html:textarea property="feedback" /><BR>
      <!-- No text/html choice in beta2
<html:radio property="a_type" value="text" />Text
<html:radio property="a_type" value="html" />HTML
-->
    </td>
  </tr>
  </logic:equal>
