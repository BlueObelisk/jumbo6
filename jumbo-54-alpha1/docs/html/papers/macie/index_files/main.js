function redirect(targ,selObj,restore) {
    eval(targ + ".location='" + selObj.options[selObj.selectedIndex].value + "'");
    if (restore) selObj.selectedIndex=0;
}

function prepareErrorLinks() {
    if( ! document.getElementsByTagName ) return false;
    if( ! document.getElementById ) return false;
    if( ! document.getElementById( "validationerror" ) ) return false;
    var errorLinks = document.getElementById( "validationerror" ).getElementsByTagName( "a" );
    for( var i=0; i < errorLinks.length; i++ ) {
        errorLinks[i].onclick = function() {
            var objectId = this.href.substr( this.href.indexOf( "#" ) + 1 );
            if( document.getElementById(objectId) ) {
                document.getElementById(objectId).focus();
            }
        }
    }
}

window.onload = prepareErrorLinks;

var supported = ( document.layers || document.getElementById || document.all );

function encrypt(part1,part2,part3) {
    var all= 'mai'+'lto:'+part1+"@"+part2;
    if( part3 ) all += '?Sub'+'ject='+part3; document.location.href=eval('"'+all+'"');
}

function toggle_display( id ) {
    if( supported ) {
        if( document.layers && document.layers[id] ) {
            document.layers[id].display = ( document.layers[id].display == 'block' ? 'none' : 'block' );
        } else if( document.getElementById && document.getElementById(id) ) {
            document.getElementById(id).style.display = ( document.getElementById(id).style.display == 'block' ? 'none' : 'block' );
        } else if( document.all && document.all[id] ) {
            document.all[id].style.display = ( document.all[id].style.display == 'block' ? 'none' : 'block' );
        }
    }
}

function hide( id ) {
    if( supported ) {
        if( document.layers && document.layers[id] ) {
            document.layers[id].display = 'none';
        } else if( document.getElementById && document.getElementById(id) ) {
            document.getElementById(id).style.display = 'none';
        } else if( document.all && document.all[id] ) {
            document.all[id].style.display = 'none';
        }
    }
}

function unhide( id, display ) {
    // Default to display at block level
    if( ! display ) display = 'block';

    if( supported ) {
        // As IE does not support table-row, translate it to block
        if( navigator.appName == 'Microsoft Internet Explorer' && display == 'table-row' ) display = 'block';

        if( document.layers && document.layers[id] ) {
            document.layers[id].display = display;
        } else if( document.getElementById && document.getElementById(id) ) {
            document.getElementById(id).style.display = display;
        } else if( document.all && document.all[id] ) {
            document.all[id].style.display = display;
        }
    }
}

function visible( id ) {
     if( supported ) {
         if( document.layers && document.layers[id] ) {
             if( document.layers[id].display == 'none' ) return 0;
         } else if( document.getElementById && document.getElementById(id) ) {
             if( document.getElementById(id).style.display == 'none' ) return 0;
         } else if( document.all && document.all[id] ) {
             if( document.all[id].style.display == 'none' ) return 0;
         }
         return 1;
    }
    return 0;
}

function removeSpaces(string) {
     var tstring = "";
     string = '' + string;
     splitstring = string.split(" ");
     for(i = 0; i < splitstring.length; i++) tstring += splitstring[i];
     return tstring;
}

var selectItems = new Object();

function setOptions( listname, selectedItem, optionList ) {
     optionList.options.length = 0;
     optionList[0] = new Option( "Please select", "", true, false );

     if( selectItems[listname][selectedItem] ) {
        var newList = selectItems[listname][selectedItem];
        if( selectedItem != "" ) {
            for( var i = 0; i < newList.length; i++ ) {
                optionList.options[i+1] = new Option( newList[i].text, newList[i].value ? newList[i].value : newList[i].text );
            }
        }
    }
}
