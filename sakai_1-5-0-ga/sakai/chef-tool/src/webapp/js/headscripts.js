/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/webapp/js/headscripts.js,v 1.9 2004/10/19 19:04:18 gsilver.umich.edu Exp $
*
***********************************************************************************
@license@
**********************************************************************************/

var courierRunning = false;

var focus_path;

var ignoreCourier = false;
var doubleDeep = false;

function openWindow(url, title, options)
{
	var win = top.window.open(url, title, options);
	win.focus();
	return win;
}

function sitehelp(whereto) 
{
	umcthelp=window.open(whereto,'umcthelpWindow','toolbar=yes,scrollbars=yes,resizable=yes,menubar=no,status=yes,directories=no,location=no,width=600,height=400')
}

function hideElement(hideMe)
{
	if (hideMe != 'none')
	{
		var menuItem = document.getElementById(hideMe);
		if(menuItem != null)
			menuItem.style.display = 'none';
	}
	return true;
}

/* %%% currently disabled, used for paitence display -ggolden
function showSubmitMessage()
{
	var submitDiv = document.getElementById('SubmitMessage');
	var normalDiv = document.getElementById('chefPortletContainer')
	if (submitDiv != null) 
	{
		submitDiv.style.display = '';
		if (normalDiv != null)
			normalDiv.style.display = 'none';
	}
	return true;
}
*/

// escape also the plus sign!
function fullEscape(value)
{
	var once = myEscape(value);
	var twice = "";
	for (var i=0; i < once.length; i++)
	{
		if (once.charAt(i) == '+')
		{
			twice += "%2b";
		}
		else
		{
			twice += once.charAt(i);
		}
	}
	return twice;
} 
 
function myEscape(value)
{
	// escape Unicode characters as "&nnnn;" NOT
	// as "%unnnn" - since Tomcat doesn't understand
	// "%unnnn" encoding properly.
	var ret = "";
	for (var i=0; i<value.length; i++)
	{
		if (value.charCodeAt(i) > 255)
		{
			ret += "&#";
			ret += value.charCodeAt(i);
			ret += ";";
		}
		else
		{
			ret += value.charAt(i);
		}
	}
	return escape(ret);
}

function buildQueryString(theFormName)
{
	theForm = document.forms[theFormName];
	var qs = '';
	for (var e=0; e<theForm.elements.length; e++)
	{
		var element = theForm.elements[e];
		if ((element.name) && (element.type) && (element.value))
		{
			var checkRadio = ((element.type == "checkbox") || (element.type == "radio"));
			if ((element.name != '') && (element.name.indexOf("eventSubmit_") == -1))
			{
				if ( !checkRadio || element.checked)
				{
					qs += '&' + fullEscape(element.name) + '=' + fullEscape(element.value);
				}
			}
		}
	}
	return qs;
}

// use if peer w/ courier, both frames in "top" parent
function updCourier(dd, ic)
{
	if (ic) return;

	if (dd)
	{
		parent.updCourier(false, false);
		return;
	}

	if ((!courierRunning) && (window.courier) && (window.courier.location.toString().length > 1))
	{
		courierRunning = true;
		window.courier.location.replace(window.courier.location);
	}
}

function formSubmitOnEnter(field, event)
{
	var keycode;
	if (window.event)
	{
		keycode = window.event.keyCode;
	}
	else
	{
		keycode = event.which ? event.which : event.keyCode
	}

	if (keycode == 13)
	{
		field.form.submit();
		return false;
	}
	
	return true;
}

// click an element id = element
function clickOnEnter(event, element)
{
	var keycode;
	if (window.event)
	{
		keycode = window.event.keyCode;
	}
	else
	{
		keycode = event.which ? event.which : event.keyCode
	}

	if (keycode == 13)
	{
		if (document.getElementById(element) && document.getElementById(element).click)
		{
			document.getElementById(element).click();
		}
		return false;
	}
	
	return true;
}

// if the containing frame is small, then offsetHeight is pretty good for all but ie/xp.
// ie/xp reports clientHeight == offsetHeight, but has a good scrollHeight
function setMainFrameHeight(id)
{
// run the script only if this window's name matches the id parameter
// this tells us that the iframe in parent by the name of 'id' is the one who spawned us
	if (typeof window.name != "undefined" && id != window.name) return;

	var obj = parent.document.getElementById(id);
	if (obj)
	{
// reset the scroll
		parent.window.scrollTo(0,0);

// to get a good reading from some browsers (ie?) set the height small
		obj.style.height="50px";

		var height = document.body.offsetHeight;
// here's the detection of ie/xp
		if ((height == document.body.clientHeight) && (document.body.scrollHeight))
		{
			height = document.body.scrollHeight;
		}
// here we fudge to get a little bigger
		height = height + 50;
// no need to be smaller than...
//		if (height < 200) height = 200;
		obj.style.height=height + "px";
	}
}

// find the first form's first text field and place the cursor there
function firstFocus()
{
	if (document.forms.length > 0)
	{
		var f = document.forms[0];
		for (var i=0; i<f.length; i++)
		{
			var e = f.elements[i];
			if ((e.name) && (e.type) && ((e.type=='text') || (e.type=='textarea')) && (!e.disabled))
			{
				e.focus();
				break;
			}
		}
	}
}

// set focus on a particular element in a page.
// the path to that element is given by a series of element id's in an array (param "elements")
// which should be the id's of zero or more frames followed by the id of the element.
function setFocus(elements)
{
	if(typeof elements == "undefined")
	{
		return;
	}

	var focal_point = document;
	for(var i = 0; i < elements.length; i++)
	{
		if(focal_point.getElementById(elements[i]))
		{
			focal_point = focal_point.getElementById(elements[i]);
		}
		
		if(focal_point.contentDocument)
		{
			focal_point = focal_point.contentDocument;
		}
		else if(focal_point.contentWindow)
		{
			focal_point = focal_point.contentWindow;
			if(focal_point.document)
			{
				focal_point = focal_point.document;
			}
		}
		else
		{
			break;
		}
	}
	
	if(focal_point && focal_point.focus)
	{
		focal_point.focus();
	}
}

// return the url with auto=courier appended, sensitive to ? already in there or not
function addAuto(loc)
{
	var str = loc.toString();

	// not if already there
	if (str.indexOf("auto=courier") != -1) return str;
	
	if (str.indexOf("?") != -1)
	{
		// has a ?
		return str + '&auto=courier';
	}
	else
	{
		// has no ?
		return str + '?auto=courier';
	}
}

function showNotif(item, button,formName)
{
	if (button !="noBlock")
	{
		eval("document." + formName + "." + button + ".disabled=true")
	}
	if (item !="noNotif")
	{
		var browserType;
		if (document.all) {browserType = "ie"}
		if (window.navigator.userAgent.toLowerCase().match("gecko")) {browserType= "gecko"}
		if (browserType == "gecko" )
			document.showItem = eval('document.getElementById(item)');
		else if (browserType == "ie")
			document.showItem = eval('document.all[item]');
		else
			document.showItem = eval('document.layers[item]');

			document.showItem.style.visibility = "visible";
	}		
}

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/webapp/js/headscripts.js,v 1.9 2004/10/19 19:04:18 gsilver.umich.edu Exp $
*
**********************************************************************************/
