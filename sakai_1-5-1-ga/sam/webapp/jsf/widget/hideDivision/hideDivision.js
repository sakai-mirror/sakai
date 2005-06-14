// $Id: hideDivision.js,v 1.11.4.1 2005/03/09 23:02:03 esmiley.stanford.edu Exp $
// alert("got here");
// does "hide" (hideUnhideAllDivs()) need to be run?
var runHide=true;
// should function be disabled?
var disableShowHideDiv=false;

// show/hide hideDivision tag with JSF id of "hideDivisionNo" with "context" path
function showHideDiv(hideDivisionNo, context)
  {
//  if (disableShowHideDiv==true) // alert ("disableShowHideDiv");
  if (disableShowHideDiv==true) return;

  var tmpdiv = "__hide_division_" + hideDivisionNo;
  var tmpimg = "__img_hide_division_" + hideDivisionNo;
  var divisionNo = getTheElement(tmpdiv);
  var imgNo = getTheElement(tmpimg);
  if(divisionNo)
    {
    if(divisionNo.style.display =="block")
    {
      divisionNo.style.display="none";
      if (imgNo)
      {
        imgNo.src = context + "/images/right_arrow.gif";
       }
    }
    else
      {
       divisionNo.style.display="block";
       if(imgNo)
       {
       imgNo.src = context + "/images/down_arrow.gif";
       }
      }
    }
  }

// if a DIV id has our special flag, toggle its visibility
function hideUnhideAllDivs(action)
{
  if(runHide==true)
  {
    runHide=false;
    myDocumentElements=document.getElementsByTagName("div");
    for (i=0;i<myDocumentElements.length;i++)
    {
        divisionNo = "" + myDocumentElements[i].id;
        if (divisionNo.indexOf("__hide_division_")==0)
        {
            elem = document.getElementById(divisionNo);
            if (elem){
            elem.style.display =action;
            }
        }
    }
  }
}

//special handling if page has WYSIWYG
//right now turns off for Mozilla
function hideUnhideAllDivsWithWysiwyg(action)
{
 // Mozilla fix, ignore
 if (navigator.product == "Gecko")
 {
  // alert("Mozilla");
  disableShowHideDiv=true;
  return;
 }
 hideUnhideAllDivs(action)
}


// getElementById with special handling of old browsers
function getTheElement(thisid){

  var thiselm = null;

  if (document.getElementById)
  {
    // browser implements part of W3C DOM HTML ( Gecko, Internet Explorer 5+, Opera 5+
    thiselm = document.getElementById(thisid);
  }
  else if (document.all){
    // Internet Explorer 4 or Opera with IE user agent
    thiselm = document.all[thisid];
  }
  else if (document.layers){
    // Navigator 4
    thiselm = document.layers[thisid];
  }

  if(thiselm)	{

    if(thiselm == null)
    {
      return;
    }
    else
    {
      return thiselm;
    }
  }
}
