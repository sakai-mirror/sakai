/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/webapp/js/discussionscript.js,v 1.1 2004/03/12 01:57:59 ggolden Exp $
*
***********************************************************************************
@license@
**********************************************************************************/

// 
// <p>The appendMessage function is used in the CHEF chat log to append messages 
// to the end of the transcript of chat messages.  Called in chef_chat-List.vm
//
// @author University of Michigan, CHEF Software Development Team
// @version $Revision: 1.1 $
// 
function getRealTop(el) {
    yPos = el.offsetTop;
    tempEl = el.offsetParent;
    while (tempEl != null) {
        yPos += tempEl.offsetTop;
        tempEl = tempEl.offsetParent;
    }
    return yPos;
}

function scroll(mId)
{

	var position = 0;
	var undefined;
	var docheight = 0, frameheight = 300;	

	if (mId == "")
	{
		// not message selected 
		position = 0;
	}
	else
	{
		for (var loop=0; loop<document.anchors.length; loop++) 
		{
			if (document.anchors[loop].id.indexOf(mId) != -1)
				position = getRealTop(document.anchors[loop]);
		}

		// find the height of the frame containing the discussion
		if(navigator.appName == "Microsoft Internet Explorer" && navigator.userAgent.indexOf("Win") > -1 )
		{
			// WIN_IE
			if(document.documentElement && document.documentElement.clientHeight)
			{
				frameheight = document.documentElement.clientHeight;
			}
			else if(window.contentDocument && window.contentdocument.clientHeight)
			{
				frameheight = window.contentdocument.clientHeight;
			}
		}
		else if(navigator.appName == "Microsoft Internet Explorer" && navigator.userAgent.indexOf("Mac") > -1 )
		{
			// MAC_IE
			frameheight = document.body.clientHeight ;
		}
		else if(window.innerHeight != undefined)
		{
			// WIN_MZ 
			// WIN_NN
			frameheight = window.innerHeight;
		}
		else if(document.body.parentNode !== undefined && document.body.parentNode.clientHeight !== undefined)
		{
			frameheight = document.body.parentNode.clientHeight;
		}

		// adjust scroll
		if(position < frameheight)
		{
			position = 0;
		}
		window.scrollTo(0, position);
	}

}	// scrollTo

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/webapp/js/discussionscript.js,v 1.1 2004/03/12 01:57:59 ggolden Exp $
*
**********************************************************************************/
