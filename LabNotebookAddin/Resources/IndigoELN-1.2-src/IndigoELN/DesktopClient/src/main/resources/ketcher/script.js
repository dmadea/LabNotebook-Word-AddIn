var closeWarning = "WARNING! Edited molfile wasn't transfered to Chemistry Electronic Notebook! Close this page anyway?";

var withElnProxyId = "?id=" + elnProxyId;
var elnDelim = "[endElnProxyId]";
var url = "/getmolfile" + withElnProxyId;
var shouldReturn = true;

var inFormOrLink;
$('a').live('click', function() { inFormOrLink = true; });
$('form').bind('submit', function() { inFormOrLink = true; });

$(window).bind('beforeunload', function(eventObject) {
    var returnValue = undefined;
    
    if (! inFormOrLink) {
        returnValue = closeWarning;
		returnToEln();
    }    	
	
    eventObject.returnValue = returnValue;
    return returnValue;
}); 

function sendOutputFileAndClose() {
	var ketcher = getKetcher();
	var molfile = ketcher.getMolfile();

	$.post( 
		url, 
		withElnProxyId + elnDelim + molfile
	).success(
		function() {
			closeWindow();
		}	
	).error(
		function(){
			var exit = confirm(closeWarning);
			if(exit){
				closeWindow();
			}
		}
	);
}

function closeWindow() {
	document.title = "Close this page";
	document.body.innerHTML = "You can now close this page.";
	
	inFormOrLink = true;
	
	returnToEln();
}

function returnToEln() {
	if (shouldReturn) {
		$.post(url, "RETURN_TO_ELN" + withElnProxyId + elnDelim);
		shouldReturn = false;
	}
}

function getKetcher() {
	var ketcherFrame = document.getElementById("ifKetcher");
	var ketcher = null;
	
	if ("contentDocument" in ketcherFrame) {
	    ketcher = ketcherFrame.contentWindow.ketcher;
	} else { // IE7
	    ketcher = document.frames["ifKetcher"].window.ketcher;
	}
	return ketcher;
}

function getInputMolFile() {
	$.get(
		url
	).success(
		function(data) {
			if(data != "") {
				var ketcher = getKetcher();
				getKetcher().setMolecule(data);
			}
		}
	).error(
		function(err) {
			alert("Failed to request input mol file.");
		}
	);
	updateElnMolInfo();
}

function updateElnMolInfo() {
	var elnMolInfo = document.getElementById("elnMolInfo");
		
	$.get(
		"/getinfo" + withElnProxyId
	).success(
		function(data) {
			document.title = data + " - Sketcher for Indigo ELN"
			elnMolInfo.innerHTML = data;
		}
	).error(
		function() {
			elnMolInfo.innerHTML = "";
		}
	);
}
