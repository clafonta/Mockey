function spaces(len)
{
	var s = '';
	var indent = len*4;
	for (i=0;i<indent;i++) {s += " ";}

	return s;
}

function format_xml(str)
{
	var xml = '';

	// add newlines
	str = str.replace(/(>)(<)(\/*)/g,"$1\r$2$3");

	// add indents
	var pad = 0;
	var indent;
	var node;

	// split the string
	var strArr = str.split("\r");

	// check the various tag states
	for (var i = 0; i < strArr.length; i++) {
		indent = 0;
		node = strArr[i];

		if(node.match(/.+<\/\w[^>]*>$/)){ //open and closing in the same line
			indent = 0;
		} else if(node.match(/^<\/\w/)){ // closing tag
			if (pad > 0){pad -= 1;}
		} else if (node.match(/^<\w[^>]*[^\/]>.*$/)){ //opening tag
			indent = 1;
		} else
			indent = 0;
		//}

		xml += spaces(pad) + node + "\r";
		pad += indent;
	}

	return xml;
}

