<logic:iterate name="question" id="answerItem" property="answers" type="org.navigoproject.osid.assessment.model.Answer" indexId="ctr"> 
<tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
  <td class="tdEditSide"><span class="heading2">Correct Answer</span></td>
  <td>
  <html:radio name="question" property='<%= "answerArray[" + ctr + "].isCorrect" %>' value="true" />True
  <html:radio name="question" property='<%= "answerArray[" + ctr + "].isCorrect" %>' value="false" />False
  </td>
</tr>
</logic:iterate>
<logic:equal name="question" property="feedback_isInstructorEditable"
        value="true">
<tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Solution/Feedback</span><BR>
      <span class="heading3">(Shown as feedback for any response)</span></td>
    <td colspan=2><html:textarea cols="50" rows="2" property="feedback" /><BR>
<!-- No text/html choice in beta2 
<html:radio property="a_type" value="text" />Text
<html:radio property="a_type" value="html" />HTML
-->
  </td></tr> 
</logic:equal>
