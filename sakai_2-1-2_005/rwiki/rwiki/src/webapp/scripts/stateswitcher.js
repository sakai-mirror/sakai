
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
function storeCaret(el) {
    if ( el.createTextRange ) 
        el.caretPos = document.selection.createRange().duplicate();        
}

function addAttachment(textareaid, formid, editcontrolid, type) {
  var textarea;
  var editcontrol;
  var form;
  var store;
  if ( document.all ) {
    textarea = document.all[textareaid];
    editcontrol = document.all[editcontrolid];
    form = document.all[formid];
  } else {
    textarea = document.getElementById(textareaid);
    editcontrol = document.getElementById(editcontrolid);
    form = document.getElementById(formid);
  }    

  if (typeof(textarea.caretPos) != "undefined" && typeof(textarea.createTextRange) != "undefined")
  {
    var duplicate = textarea.caretPos.duplicate();
    var textareaRange = textarea.createTextRange();

    textareaRange.select();
    textareaRange = document.selection.createRange().duplicate();
    duplicate.select();

    var length = duplicate.text.length;

    duplicate.setEndPoint("StartToStart", textareaRange); 	

    var endPoint = duplicate.text.length;
    var startPoint = endPoint - length;
    store = startPoint + ":" + endPoint;
  } else if (typeof(textarea.selectionStart) != "undefined") {
    store = textarea.selectionStart + ":" + textarea.selectionEnd;
  } else {
    store = (textarea.text.length - 1) + ":" + (textarea.text.length - 1)
  }

  editcontrol.innerHTML += "<input type='hidden' name='save' value='Attach" + type + "'/><input type='hidden' name='caretPosition' value='"+ store + "'/>";
  form.submit();
}


function addMarkup(textareaid, contentMU, startMU, endMU) {
    var textarea;
        if ( document.all ) {
            textarea = document.all[textareaid];
		} else {
		    textarea = document.getElementById(textareaid);
		}    

	if (typeof(textarea.caretPos) != "undefined" && textarea.createTextRange)
	{
        
		var caretPos = textarea.caretPos, repText = caretPos.text, temp_length = caretPos.text.length;
		var i=-1;
		while (++i < repText.length && /\s/.test("" + repText.charAt(i))) {
		}

		switch (i) {
		case 0: break;
		case repText.length: startMU = repText + startMU;
		repText = "";
		break;
		default: startMU = repText.substring(0, i) + startMU;
		         repText = repText.substr(i);
		}

		i = repText.length;
		while ( i > 0 && /\s/.test("" + repText.charAt(--i))) {
		}

		switch (i) {
		case repText.length - 1: break;
		case -1: endMU = endMU + repText; break;
		default: endMU = endMU + repText.substr(i + 1);
		         repText = repText.substring(0, i + 1);
		}

		
		if ( repText.length == 0 )
		    repText = contentMU;

		caretPos.text = startMU + repText + endMU;

		textarea.focus(caretPos);
	} 
	// Mozilla text range wrap.
	else if (typeof(textarea.selectionStart) != "undefined")
	{
		var begin = textarea.value.substr(0, textarea.selectionStart);
		var repText = textarea.value.substr(textarea.selectionStart, textarea.selectionEnd - textarea.selectionStart);
		var end = textarea.value.substr(textarea.selectionEnd);
		var newCursorPos = textarea.selectionStart;
		var scrollPos = textarea.scrollTop;
		
		var i=-1;
		while (++i < repText.length && /\s/.test("" + repText.charAt(i))) {
		}
		
		switch (i) {
		case 0: break;
		case repText.length: startMU = repText + startMU;
		repText = "";
		break;
		default: startMU = repText.substring(0, i) + startMU;
		         repText = repText.substr(i);
		}

		i = repText.length;
		while ( i > 0 && /\s/.test("" + repText.charAt(--i))) {
		}

		switch (i) {
		case repText.length - 1: break;
		case -1: endMU = endMU + repText; break;
		default: endMU = endMU + repText.substr(i + 1);
		         repText = repText.substring(0, i + 1);
		}

		
		if ( repText.length == 0 )
		    repText = contentMU;

		textarea.value = begin + startMU + repText + endMU + end;

		if (textarea.setSelectionRange)
		{
			textarea.setSelectionRange(newCursorPos + startMU.length, newCursorPos + startMU.length + repText.length);
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

/*
 * Content-seperated javascript tree widget
 * Copyright (C) 2005 SilverStripe Limited
 * Feel free to use this on your websites, but please leave this message in the fies
 * http://www.silverstripe.com/blog
*/

/*
 * Initialise all trees identified by <ul class="tree">
 */
function autoInit_trees() {
	var candidates = document.getElementsByTagName('ul');
	for(var i=0;i<candidates.length;i++) {
		if(candidates[i].className && candidates[i].className.indexOf('tree') != -1) {
			initTree(candidates[i]);
			candidates[i].className = candidates[i].className.replace(/ ?unformatted ?/, ' ');
		}
	}
}
 
/*
 * Initialise a tree node, converting all its LIs appropriately
 */
function initTree(el) {
	var i,j;
	var spanA, spanB, spanC;
	var startingPoint, stoppingPoint, childUL;
	
	// Find all LIs to process
	for(i=0;i<el.childNodes.length;i++) {
		if(el.childNodes[i].tagName && el.childNodes[i].tagName.toLowerCase() == 'li') {
			var li = el.childNodes[i];

			// Create our extra spans
			spanA = document.createElement('span');
			spanB = document.createElement('span');
			spanC = document.createElement('span');
			spanA.appendChild(spanB);
			spanB.appendChild(spanC);
			spanA.className = 'a ' + li.className.replace('closed','spanClosed');
			spanA.onMouseOver = function() {}
			spanB.className = 'b';
			spanB.onclick = treeToggle;
			spanC.className = 'c';
			
			
			// Find the UL within the LI, if it exists
			stoppingPoint = li.childNodes.length;
			startingPoint = 0;
			childUL = null;
			for(j=0;j<li.childNodes.length;j++) {
				if(li.childNodes[j].tagName && li.childNodes[j].tagName.toLowerCase() == 'div') {
					startingPoint = j + 1;
					continue;
				}

				if(li.childNodes[j].tagName && li.childNodes[j].tagName.toLowerCase() == 'ul') {
					childUL = li.childNodes[j];
					stoppingPoint = j;
					break;					
				}
			}
				
			// Move all the nodes up until that point into spanC
			for(j=startingPoint;j<stoppingPoint;j++) {
				spanC.appendChild(li.childNodes[startingPoint]);
			}
			
			// Insert the outermost extra span into the tree
			if(li.childNodes.length > startingPoint) li.insertBefore(spanA, li.childNodes[startingPoint]);
			else li.appendChild(spanA);
			
			// Process the children
			if(childUL != null) {
				if(initTree(childUL)) {
					addClass(li, 'children', 'closed');
					addClass(spanA, 'children', 'spanClosed');
				}
			}
		}
	}
	
	if(li) {
		// li and spanA will still be set to the last item

		addClass(li, 'last', 'closed');
		addClass(spanA, 'last', 'spanClosed');
		return true;
	} else {
		return false;
	}
		
}
 

/*
 * +/- toggle the tree, where el is the <span class="b"> node
 * force, will force it to "open" or "close"
 */
function treeToggle(el, force) {
	el = this;
	
	while(el != null && (!el.tagName || el.tagName.toLowerCase() != "li")) el = el.parentNode;
	
	// Get UL within the LI
	var childSet = findChildWithTag(el, 'ul');
	var topSpan = findChildWithTag(el, 'span');

	if( force != null ){
		
		if( force == "open"){
			treeOpen( topSpan, el )
		}
		else if( force == "close" ){
			treeClose( topSpan, el )
		}
		
	}
	
	else if( childSet != null) {
		// Is open, close it
		if(!el.className.match(/(^| )closed($| )/)) {		
			treeClose( topSpan, el )
		// Is closed, open it
		} else {			
			treeOpen( topSpan, el )
		}
	}
}


function treeOpen( a, b ){
	removeClass(a,'spanClosed');
	removeClass(b,'closed');
}
	
	
function treeClose( a, b ){
	addClass(a,'spanClosed');
	addClass(b,'closed');
}

/*
 * Find the a child of el of type tag
 */
function findChildWithTag(el, tag) {
	for(var i=0;i<el.childNodes.length;i++) {
		if(el.childNodes[i].tagName != null && el.childNodes[i].tagName.toLowerCase() == tag) return el.childNodes[i];
	}
	return null;
}

/*
 * Functions to add and remove class names
 * Mac IE hates unnecessary spaces
 */
function addClass(el, cls, forceBefore) {
	if(forceBefore != null && el.className.match(new RegExp('(^| )' + forceBefore))) {
		el.className = el.className.replace(new RegExp("( |^)" + forceBefore), '$1' + cls + ' ' + forceBefore);

	} else if(!el.className.match(new RegExp('(^| )' + cls + '($| )'))) {
		el.className += ' ' + cls;
		el.className = el.className.replace(/(^ +)|( +$)/g, '');
	}
}
function removeClass(el, cls) {
	var old = el.className;
	var newCls = ' ' + el.className + ' ';
	newCls = newCls.replace(new RegExp(' (' + cls + ' +)+','g'), ' ');
	el.className = newCls.replace(/(^ +)|( +$)/g, '');
} 

/*
 * Handlers for automated loading
 */ 
 _LOADERS = Array();

function callAllLoaders() {
	var i, loaderFunc;
	for(i=0;i<_LOADERS.length;i++) {
		loaderFunc = _LOADERS[i];
		if(loaderFunc != callAllLoaders) loaderFunc();
	}
}

function appendLoader(loaderFunc) {
	if(window.onload && window.onload != callAllLoaders)
		_LOADERS[_LOADERS.length] = window.onload;

	window.onload = callAllLoaders;

	_LOADERS[_LOADERS.length] = loaderFunc;
}

appendLoader(autoInit_trees);