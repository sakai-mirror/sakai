<script language="JavaScript" type="text/JavaScript">
<!--
function disableObj(fromIndex,toIndex,boo){
  if(boo)
    for(i=fromIndex;i<=toIndex;i++){
      eval("document.forms[0].elements["+i+"].disabled = 1");}
    else
    for(i=fromIndex;i<=toIndex;i++){
      eval("document.forms[0].elements["+i+"].disabled = 0");}
     }

function disable(id){
  eval("document.getElementById(id).disabled = 1");
}

function enable(id){
  eval("document.getElementById(id).disabled = 0");
}

function uncheck(id){
  eval("document.getElementById(id).checked = 0");
}

function check(id){
  eval("document.getElementById(id).checked = 1");
}

function disableCheckboxes(these) {
  var numelements = document.forms[0].length;
  for (var i=0 ; i<numelements ; i++) {
    var item = document.forms[0].elements[i];
		var pos = item.name.indexOf(these);
//		alert("debug "+item.name+" "+pos);
	  if (pos>=0) {
        item.disabled = true;
		}
  }
}

// end the hiding comment -->

 
</script> 
