/* Rembrandt
 * Copyright (C) 2008 Nuno Cardoso. ncardoso@xldb.di.fc.ul.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package saskia.test.converters

import org.junit.*
import org.junit.runner.*

import org.apache.log4j.*

import saskia.converters.*
import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 * Tester for WikipediaAPI.
 */
class TestFilterTextFromHTMLDocument extends GroovyTestCase {
	
	 String text
	
    public TestFilterTextFromHTMLDocument() {    
	   	text = """
<!DOCTYPE html> 
<html> 
<head> 
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"> 
<title>Gmail</title> 
<meta name="application-name" content="Gmail"> 
<meta name="description" content="Google&#39;s approach to email"> 
<meta name="application-url" content="https://mail.google.com/mail/u/0"> 
<meta name="google" content="notranslate"> 
<link rel="icon" href="images/2/mail_icon_32.png" sizes="32x32"> 
<link rel="shortcut icon" href="/mail/u/0/images/favicon.ico" type="image/x-icon"> 
<link rel="alternate" type="application/atom+xml" title="Gmail Atom Feed" href="feed/atom"> 
<script> 
var GM_START_TIME=(new Date).getTime();var GM_FIN_URL="";var GM_MOOSE_URL="?ui=html&zy=b";var GM_NO_COOKIE_URL="html/nocookies.html";var GM_NO_ACTIVEX_URL="html/noactivex.html";var GM_MPTO_URL="/mail/?view=btop&fstf=1";var GM_CA_INSTALLED=false;var GM_APP_NAME="Gmail";var GM_ICON_URL="images/2/mail_icon_32.png";
</script> 
<script> 
(function(){var d=null;function f(a,c){return a.indexOf(c)!=-1}var g="",h=".",i="(\\d*)(\\D*)",k="g";.split(h),v=Math.max(e.length,q.length),j=0;b==0&&j<v;j++){var W=e[j]||g,ha=q[j]||g,ia=RegExp(i,k)
</script> 
<script> 
var ssm=0;
</script> 
<style> 
body{margin:0;width:100%;height:100%} body,td,input,textarea,select{font-family:arial,sans-serif} input,textarea,select{font-size:100%} </style> 
</head> 
<body > 
<noscript> 
<style>
#loading {display:none}
</style>
<font face=arial>JavaScript must be enabled in order for you to use Gmail in standard view. However, it seems JavaScript is either disabled or not supported by your browser. To use standard view, enable JavaScript by changing your browser options, then <a href="">try again</a>. <p>To use Gmail basic HTML view, which does not require JavaScript, <a href="?ui=html&zy=c">click here</a>.</p></font><p><font face=arial>If you want to view Gmail on a mobile phone or similar device <a href="?ui=mobile&zyp=c">click here</a>.</font></p>
</noscript> 
<div id="loading"> 
<div class="cmsg"> 
<div class="msg"> 
Loading nuno.cardoso@gmail.com&hellip;
</div> 
<div class="lpb"> 
<div id="lpt"></div> 
</div> 
</div> 
<div id="stb" class="msgb" style="bottom:10px"> 
Loading standard view | <a href="?ui=html&zy=e">Load basic HTML</a> (for slow connections)
</div> 
<div id="loadingError" class="cmsg" style="clear:left;display:none"> 
<p style="font-size:larger;margin:40px 0"> 
This is taking longer than usual.
<a href="" onclick="sc('GMAIL_SL','rld');"><b>Try reloading the page</b></a>.
</p> 
<div> 
If that doesn't work, you can:
<ol> 
<li><a href="?shva=1&labs=0">Disable Labs and try again</a>.
<li>If you're on a slow connection, try <a href="?ui=html&zy=d">basic HTML view</a>.
<li>For more troubleshooting tips, visit the <a href="http://mail.google.com/support/bin/answer.py?answer=8767&src=sl&hl=en">help center</a>.
</body></html>	
""" 
    }
  
    void testParser() {
		String out =  FilterTextFromHTMLDocument.parse(text, "rembrandt")
		/**
		.JavaScript must be enabled in order for you to use Gmail in standard view. However, it seems JavaScript is either disabled or not supported by your browser. To use standard view, enable JavaScript by changing your browser options, then try again. 
<p>To use Gmail basic HTML view, which does not require JavaScript, click here.</p>
<p>If you want to view Gmail on a mobile phone or similar device click here.</p>     Loading nuno.cardoso@gmail.com…       Loading standard view | Load basic HTML (for slow connections)   
<p> This is taking longer than usual. <b>Try reloading the page</b>. </p>  If that doesn't work, you can: 
<ol> 
 <li>Disable Labs and try again. </li>
 <li>If you're on a slow connection, try basic HTML view. </li>
 <li>For more troubleshooting tips, visit the help center.  </li>
</ol>
*/
	}
}

