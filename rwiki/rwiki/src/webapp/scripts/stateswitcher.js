
function changeClass(oldclass, newclass) {

    var spantags = document.getElementsByTagName("SPAN");
    var oldclasses = getElementsByClass(spantags,oldclass);
   // alert("Changin from "+oldclass+" to "+newclass+" for "+oldclasses.length);
    for (i = 0; i < oldclasses.length; i++ ) { 
       oldclasses[i].className = newclass;
    }
}

function changeRoleState(cb,column,enable,disable) {
    if ( cb ) {
        changeClass(column+disable,column+enable);
    } else {
        changeClass(column+enable,column+disable);
    }
}



contractsymbol = '<span class="rwiki_collapse"><img title="hide" alt="hide" src="/sakai-rwiki/images/minus.gif"/><span>Hide </span></span>';
expandsymbol = '<span class="rwiki_expand"><img alt="show" src="/sakai-rwiki/images/plus.gif" title="show"/><span>Show </span></span>';

function getElementsByClass(ellist, classname) {
  var els = new Array();
  for (i=0; i<ellist.length; i++) {
    if (ellist[i].className == classname) {
      els.push(ellist[i]);
    }
  }
  return els;
}

function expandcontent(root, blockname) {
  var block = document.getElementById(blockname);
  var spantags = root.getElementsByTagName("SPAN");
  var showstatespans = getElementsByClass(spantags, "showstate");

  block.style.display = (block.style.display != "block") ? "block" : "none";
  showstatespans[0].innerHTML = block.style.display == "block" ? contractsymbol : expandsymbol;
  window.onload();
}

function hidecontent(rootname, blockname) {
  var root = document.getElementById(rootname);
  var block = document.getElementById(blockname);
  var spantags = root.getElementsByTagName("SPAN");
  var showstatespans = getElementsByClass(spantags, "showstate");

  block.style.display = "none";
  showstatespans[0].innerHTML = expandsymbol;
}

function onload() {
  var allels = document.all? document.all : document.getElementsByTagName("*");
  var expandableContent = getElementsByClass(allels, "expandablecontent");
  for (var i = 0; i < expandableContent.length; i++) {
    expandableContent[i].style.display = "none";
  }
  var allexpandable = getElementsByClass(allels, "expandable");
  var i = 0;
  for (var i = 0; i < allexpandable.length; i++) {
    var spantags = allexpandable[i].getElementsByTagName("SPAN");
    var showstatespans = getElementsByClass(spantags, "showstate");
    showstatespans[0].innerHTML = expandsymbol;
  }

}

function addMarkup(textareaid, contentMU, startMU, endMU) {
    var textarea;
        if ( document.all ) {
            textarea = document.all[textareaid];
		} else {
		    textarea = document.getElementById(textareaid);
		}    

	// Can a text range be created?
	if (typeof(textarea.caretPos) != "undefined" && textarea.createTextRange)
	{
		var caretPos = textarea.caretPos, repText = caretPos.text, temp_length = caretPos.text.length;
		if ( temp_length == 0 )
		    repText = contentMU;

		caretPos.text = caretPos.text.charAt(caretPos.text.length - 1) == ' ' ? startMU + repText + endMU + ' ' : startMU + repText + endMU;

		textarea.focus(caretPos);
	}
	// Mozilla text range wrap.
	else if (typeof(textarea.selectionStart) != "undefined")
	{
		var begin = textarea.value.substr(0, textarea.selectionStart);
		var selection = textarea.value.substr(textarea.selectionStart, textarea.selectionEnd - textarea.selectionStart);
		var end = textarea.value.substr(textarea.selectionEnd);
		var newCursorPos = textarea.selectionStart;
		var scrollPos = textarea.scrollTop;
		if ( selection.length == 0 )
		    selection = contentMU;

        if ( selection.length > 0 && selection.charAt(selection.length-1) == ' ' ) {
            selection = selection.substr(0,selection.length-1);
            end = " " + end;
        }
		textarea.value = begin + startMU + selection + endMU + end;

		if (textarea.setSelectionRange)
		{
			textarea.setSelectionRange(newCursorPos + startMU.length, newCursorPos + startMU.length + selection.length);
			textarea.focus();
		}
		textarea.scrollTop = scrollPos;
	}
	// Just put them on the end, then.
	else
	{
		textarea.value += startMU + contentMU + endMU;
		textarea.focus(textarea.value.length - 1);
	}
}

