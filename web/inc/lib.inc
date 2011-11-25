<?php

require_once(dirname(__FILE__).'/i18n.inc');
require_once(dirname(__FILE__).'/config.inc');
require_once(dirname(__FILE__).'/Snoopy.class.php');

/* Generates an URL with available GET info */
function curPageURL($morevars) {

	$config = Config::getInstance();

	 $pageURL = $config->urlbasedir."/";
	 if ((sizeof($_GET)>0 || sizeof($morevars)>0)) {
		$pageURL .= "?";
		$vars = array();
		foreach($morevars as $k=>$v) {$vars[] = urlencode($k)."=".urlencode($v);}
		foreach($_GET as $k=>$v) {
			if (!array_key_exists($k, $morevars)) {
			$vars[] = urlencode($k)."=".urlencode($v);
			}
		}		
		$pageURL .= implode("&", $vars);
	} 
	return $pageURL;
}	

/* Generates the main navigation bar */
function generateNavMenu() {
	global $lang;
	$config = Config::getInstance();
	$i18n = i18n::getInstance();
	
	echo "<UL>\n";	
	foreach($i18n->nav_menu as $nav_item) {

		echo "<LI>\n";
		if ($nav_item['link'] == "yes") {
			echo "<A HREF=\"".curPageURL(array('do'=>$nav_item['action']))."\">".$nav_item['label'][$lang]."</A>";
		} else {
			echo "".$nav_item['label'][$lang]."";
		}

		if ($nav_item['submenu']) {
			
			echo "<DIV class=\"main-nav-submenu\">\n";
			echo "<UL>\n";
			foreach($nav_item['submenu'] as $nav_subitem) {
					
				echo "<LI>\n";
				if ($nav_subitem['link'] == "yes") {
					if ($nav_subitem['url'] != null) {
						echo "<A HREF=\"".$nav_subitem['url']."\">".$nav_subitem['label'][$lang]."</a>";
					} else {
						echo "<A HREF=\"".curPageURL(array('do'=>$nav_subitem['action']))."\">".$nav_subitem['label'][$lang]."</a>";
					}
				} else {
					echo "".$nav_subitem['label'][$lang]."";
				}	
				echo "</LI>\n";
			}
			echo "</UL>\n";
			echo "</DIV>\n";
		} 
		echo "</LI>\n";
	}
	echo "</UL>\n";
}

/* Generates content for a side menu */
function generateSideMenuFor($page) {

	global $lang;
	$i18n = i18n::getInstance();
	
	$buffer = array();
	foreach($i18n->nav_menu as $nav_item) {
	
		$found = false;
		
		// let's see if $page is within this menu
		if ($nav_item['submenu']) {
			foreach($nav_item['submenu'] as $nav_subitem) {
				if ($nav_subitem['action'] == $page) {$found = true;}
			}
		}
	
		if ($found) {
			// generate menu, return it
			$buffer["main"] = array(
				"label" => $nav_item['label'][$lang],
				"link" => false);
			$buffer["menu"] = array();
				
			foreach($nav_item['submenu'] as $nav_subitem) {
				
				$buffer["menu"][] = array(
					"label" => $nav_subitem['label'][$lang],
					"action" =>  $nav_subitem['action'],
					"link" => ($nav_subitem['action'] == $page ? false : curPageURL(array('do'=>$nav_subitem['action'])) ) ,					
					"url" => $nav_subitem['url'] ,	
					"bold" => ($nav_subitem['action'] == $page ? true : false)
				);
			}
			
			return $buffer;
		}
	}
	return null;

}

/* Generates content for a side menu */
function getMenuFromAction($action) {
	
	$i18n = i18n::getInstance();
	
	foreach($i18n->nav_menu as $nav_item) {
		if ($nav_item['action'] == $action) {return $nav_item;}
		
		if ($nav_item['submenu']) {
			foreach($nav_item['submenu'] as $nav_subitem) {
				if ($nav_subitem['action'] == $action) {return $nav_item;}
			}
		}
	}
	return false;
}


/* Generates a main footer */ 
function footer() {
	 echo "<DIV ID=\"main-footer\">\n";
	 echo "Copyright &copy; 2010 FCUL.";
	 echo "</DIV>\n";
}

/* analytics */
function analytics() {
	echo <<<OUTPUT
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-7819418-2");
pageTracker._trackPageview();
} catch(err) {}</script>
OUTPUT;
}

/* Generates breadcrumbles.
Breadcrumble[0] is always home 
Breadcrumble[1] and [2[ depends on hyphens on $do
for each, let's get link or not
*/
function generateBreadCrumbles($do, $lang) {
	
	$i18n = i18n::getInstance();
	 
	// fist element...
	$output = "<A HREF=\"".curPageURL(array('do'=>'home'))."\">".$i18n->message['home'][$lang]."</A>\n";
	
	$menu = getMenuFromAction($do);
	if (!$menu) {return $output;}
	
	if ($menu['action'] == $do) {
		$output .= "> <B>".$menu['label'][$lang]."</B>\n";
	} else {
		$output .= "> ".$menu['label'][$lang]."\n";		
	}
	
	if ($menu['submenu']) {
		
		foreach ($menu['submenu'] as $submenu) {
			
			if ($submenu['action'] == $do) {
				$output .= "> <B>".$submenu['label'][$lang]."</B>\n";
			} 
		}
	}
	
	return $output;
}

function readNews($lang){
 date_default_timezone_set('Europe/London');
 $config = Config::getInstance();

 $h = fopen($config->basedir."/".$config->newsfile[$lang],"r"); 
 $f = fread($h, filesize($config->basedir."/".$config->newsfile[$lang]));
 $bits = split('---',$f);
 $arr = array();
 $number = 0;

 foreach($bits as $b) {
   $number++;
   $bits2 = split("\n",trim($b));
   $time = array_shift($bits2);
   $title = array_shift($bits2);
   $description = implode("", $bits2);

   $arr[$number] = array("time"=>strtotime($time), "title"=>$title, "description"=> $description);
 }
 fclose($h);
 $arr["total"] = $number;
 return $arr;
}

if ( !function_exists('sys_get_temp_dir') ){
    // Based on http://www.phpit.net/
    // article/creating-zip-tar-archives-dynamically-php/2/
    function sys_get_temp_dir() {
        // Try to get from environment variable
        if ( !empty($_ENV['TMP']) ){
            return realpath( $_ENV['TMP'] );
        } else if ( !empty($_ENV['TMPDIR']) ) {
            return realpath( $_ENV['TMPDIR'] );
        } else if ( !empty($_ENV['TEMP']) ) {
            return realpath( $_ENV['TEMP'] );
        } // Detect by creating a temporary file
        else {
            // Try to use system's temporary directory
            // as random name shouldn't exist
            $temp_file = tempnam( md5(uniqid(rand(), TRUE)), '' );
            if ( $temp_file ) {
                $temp_dir = realpath( dirname($temp_file) );
                unlink( $temp_file );
                return $temp_dir;
            }  else  {
                return FALSE;
            }
        }
    }
}	

function getLastModified($page) {
	global $lang;
	
	$i18n = i18n::getInstance();

	echo "<DIV ID=\"lastModified\">\n";
	echo $i18n->message['lastModified'][$lang]." ";
	if ($lang == "pt") echo "há ";

	$now = time();
	$newtime = intval($now - filectime($page));
	//echo "$newtime $now $dateseconds";
	$text = "";
	$plural = "";
	// get time in seconds.
	if ($newtime != 1) {$plural = "s";} else {$plural = "";}
	if ($newtime < 60) $text = $newtime." ".$i18n->time['sec'][$lang].$plural." ".$i18n->time['ago'][$lang].".";
	// get it in minutes
	if ($text == "") { 
	$newtime = intval($newtime/60.0);
	if ($newtime != 1) {$plural = "s";} else {$plural = "";}	
	if ($newtime< 60) $text = $newtime." ".$i18n->time['min'][$lang].$plural." ".$i18n->time['ago'][$lang].".";
	}// get it in hours
	if ($text == "") { 
	$newtime = intval($newtime/60.0);	
	if ($newtime != 1) {$plural = "s";} else {$plural = "";}	
	if ($newtime< 24) $text = $newtime." ".$i18n->time['hr'][$lang].$plural." ".$i18n->time['ago'][$lang].".";
	}//get it in days
	if ($text == "") { 
	$newtime = intval($newtime/24.0);	
	if ($newtime != 1) {$plural = "s";} else {$plural = "";}
	if ($newtime< 30) $text = $newtime." ".$i18n->time['day'][$lang].$plural." ".$i18n->time['ago'][$lang].".";
	}//get it in months
	if ($text == "") { 
	$newtime = intval($newtime/30.0);	
	if ($newtime != 1) {$plural = "s";} else {$plural = "";}
	if ($newtime< 12) {
		$ret = $newtime." ".$i18n->time['mth'][$lang].$plural." ".$i18n->time['ago'][$lang].".";
		$text = preg_replace("/mêss/","meses",$ret);
	}
	}
	if ($text == "") { 
	//get it in years
	$newtime = intval($newtime/12);	
	if ($newtime != 1) {$plural = "s";} else {$plural = "";}
	$text = $newtime." ".$i18n->time['yr'][$lang].$plural." ".$i18n->time['ago'][$lang].".";
	}
	echo $text;
    echo "</DIV>";
}

function fetch_url($url){
	$config = Config::getInstance();
	$snoopy = new Snoopier;
	$snoopy->agent  = "Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)";
	if (preg_match("/fc\.ul\.pt/",$config->servername)) {
	    $snoopy->proxy_host="proxy.di.fc.ul.pt";
	    $snoopy->proxy_port="3128";
    }
 	if( $snoopy->fetch( $url ) )  return $snoopy->results;
	else return false;
}
	
function addSessionVar($key, $value) {
	$_GET[$key]=$value;	
}

function prettyPrintDate($dateseconds) {
	global $lang;
    $strformat='';

	if ($lang == 'pt') {setlocale(LC_TIME, "pt_PT"); $strformat='%A, %e de %B de %Y, &agrave;s %H:%M';}
	if ($lang == 'en') {setlocale(LC_TIME, "en_US"); $strformat='%A, %e %B, %Y, %I:%M %p';}	

	date_default_timezone_set('Europe/Oslo');
	return strftime($strformat, $dateseconds);
}

function printNews($lang) {
	$i18n = i18n::getInstance();
	$arr = readNews($lang);
    for($i = 1; $i<=$arr["total"]; $i++) {
  	  echo "<P>".$i18n->news['posted'][$lang].": ".prettyPrintDate($arr[$i]['time'])."</P>";
	  echo "<P><A name=\"".$arr[$i]['time']."\"><B>".$arr[$i]['title']."</B></A></P>\n"; 
  	  echo $arr[$i]['description']."\n<HR>\n";	
   } 
}
?>