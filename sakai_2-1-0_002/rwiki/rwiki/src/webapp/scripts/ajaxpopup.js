var popupDivStack = new Array();
var popupDivID = "popupDIV";
var popupFocus = null;
var lastPopupFocus = null;
var popupURL = null;
var popupLoader = null;
var lastPopupURL = null;
var popupWaitingDiv = null;
var popupWiatingDivID = "popupwaitingdiv";
var popupindex = 0;
var asyncLoad = false;
var progressiveLoad = false;
				
function popupCallback(responsestring) {
	if ( popupDivStack[popupindex] != null ) 
	{
		//log("popupcallback "+responsestring);
		popupDivStack[popupindex].innerHTML = responsestring;
		popupDivStack[popupindex].style.visibility = "visible";
	}
}


function popupShowWaiting() {
	if ( popupWaitingDiv == null ) 
	{
		popupWaitingDiv = document.getElementById(popupWaitingDivID);
	}
	if ( popupWaitingDiv != null && popupDivStack[popupindex] != null) 
	{
		popupDivStack[popupindex].innerHTML = popupWaitingDiv.innerHTML;
		
		popupDivStack[popupindex].style.visibility = "visible";		
	} 
}
function updatePopupFocus() {

				
					if ( popupDivStack[popupindex] == null ) 
					{
						popupDivStack[popupindex] = document.getElementById(popupDivID+popupindex);
					}
					if ( popupDivStack[popupindex] == null ) 
					{
						popupDivStack[popupindex]=document.createElement("DIV");
    					    popupDivStack[popupindex].style.visibility="hidden";
    					    popupDivStack[popupindex].style.position="absolute";
    					    popupDivStack[popupindex].style.left="10";
    					    popupDivStack[popupindex].style.top="10";
    					    popupDivStack[popupindex].style.width="100";
						popupDivStack[popupindex].style.height="100";
						popupDivStack[popupindex].style.zIndex="100";
						document.body.appendChild(popupDivStack[popupindex]);
						
					}
					if ( lastPopupFocus != popupFocus ) 
					{
						//log("updateFormFocus, change of focus from "+lastPopupFocus+" to "+popupFocus);
						// changed focus
						if ( popupDivStack[popupindex] != null ) 
						{
							// hide the div
							popupDivStack[popupindex].style.visibility="hidden";
							if ( popupFocus != null ) 
							{
								// position the div below the component
								var pos = getAbsolutePos(popupFocus);
								var width =  popupFocus.offsetWidth;
								var height =  popupFocus.offsetHeight;
								pos.y += height;
								//log("Width "+width+":"+height+":"+pos.y+":"+pos.x);
								popupDivStack[popupindex].style.width = width;
								popupDivStack[popupindex].style.top = pos.y+"px ";
								popupDivStack[popupindex].style.left = pos.x+"px ";
								popupDivStack[popupindex].style.bgolor = "#cccccc";
							
							}		
						}
						lastPopupFocus = popupFocus;
					} 
					if ( popupFocus == null ) return; // now null so ignore
					
					var url = popupURL+"&puid="+(popupindex+1);
					if ( lastPopupURL != url ) 
					{
						//log("popupFocus reload URL "+url);
						lastPopupURL = url;
						popupShowWaiting();
						popupLoader.loadXMLDoc(url,"popupCallback");
					}
					
					
					if ( progressiveLoad ) 
					{
						window.setTimeout("updatePopupFocus()",100);
					}
}
function popupClose(downTo) {

	//log("Doing popupclose down to "+downTo);
	for ( i = popupindex; i >= (downTo-1); i-- ) 
	{
		if ( popupDivStack[i] != null )
		{
			popupDivStack[i].style.visibility="hidden";
		}
		lastPopupURL = null;
	}
	popupindex = downTo-1;
	if ( popupindex < 0 ) popupindex = 0;
}




	
function ajaxRefPopup(element,url,poplevel) {
	
	//log("Doing popup on "+element+" URL "+url+" Level "+poplevel+" last "+popupindex);
	if ( popupLoader == null ) 
	{
		popupLoader = new AsyncDIVLoader();
 		popupLoader.loaderName = "popupLoader";
						
	}
	popupindex = poplevel;
	popupFocus = element;
	popupURL = url;
	//log("LOADING");
	if ( asyncLoad ) {
	   //log("Doing Async Load");
	   window.setTimeout("updatePopupFocus()",100);
	} else {
       //log("Doing Direct Focus");	
	   updatePopupFocus();
	}
	//log("Loading DONE");
	
}
function getAbsolutePos(el) {
					var SL = 0, ST = 0;
					var is_div = /^div$/i.test(el.tagName);
					if (is_div && el.scrollLeft)
						SL = el.scrollLeft;
					if (is_div && el.scrollTop)
						ST = el.scrollTop;
					var r = { x: el.offsetLeft - SL, y: el.offsetTop - ST };
					if (el.offsetParent) {
						var tmp = getAbsolutePos(el.offsetParent);
						r.x += tmp.x;
						r.y += tmp.y;
					}
					return r;
}

function showPopupHere(el,divid) {
        var targetdiv;
        if ( document.all ) {
            targetdiv = document.all[divid];
		} else {
		    targetdiv = document.getElementById(divid);
		}
		if ( targetdiv != null ) {
			var pos = getAbsolutePos(el);
			var width =  el.offsetWidth;
			var height =  el.offsetHeight;
			pos.y += height;
			//log("Width "+width+":"+height+":"+pos.y+":"+pos.x);
			//targetdiv.style.width = width;
			targetdiv.style.top = pos.y+"px ";
			targetdiv.style.left = pos.x+"px ";
			targetdiv.style.bgolor = "#cccccc";		    
		    targetdiv.style.visibility = "visible";		
		} else {
            	alert(targetdiv.innerHTML);
		}
}
function hidePopup(divid) {
    var targetdiv = document.getElementById(divid);
    if ( targetdiv != null ) {
        targetdiv.style.visibility = "hidden";		
     }
} 