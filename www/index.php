<?php

require_once(dirname(__FILE__).'/inc/lib.inc');
require_once(dirname(__FILE__).'/inc/meta.inc');
require_once(dirname(__FILE__).'/inc/i18n.inc');
require_once(dirname(__FILE__).'/inc/config.inc');
require_once(dirname(__FILE__).'/inc/Mustache.php');

$i18n =  i18n::getInstance();
$config = Config::getInstance();

$do = $_GET['do']; if (!$do) $do = $_POST['do']; if (!$do) $do = 'home';
$lang = $_GET['lg']; if (!$lang) $lang = $_POST['lg']; if (!$lang) $lang = 'pt';

global $default_collection, $default_collection_id;

$query = $_GET['q'];
$tag = preg_replace('/\\\"/','"', $_POST['t']);
$limit = intval($_GET['limit']); if (!$limit) $limit=10;
$offset = intval($_GET['offset']); if (!$offset) $offset = 0;

$user_login = $_COOKIE['user_login'];
$user_name = $_COOKIE['user_name'];
$user_id = $_COOKIE['user_id'];
$api_key = $_COOKIE['api_key'];

$su = $_COOKIE['su'];
$su_collection = $_COOKIE['su_collection'];

$collection = $_COOKIE['collection'];
$collection_id = $_COOKIE['collection_id'];

if (!$collection) $collection = $default_collection;
if (!$collection_id) $collection_id = $default_collection_id;

$suggestion = $_COOKIE['search_suggestion']; // true or false, for checked 

echo <<<HTML
<!DOCTYPE html">
<HTML lang="{$lang}" xmlns="http://www.w3.org/1999/xhtml">
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=utf-8" />
HTML;
generateCSS($do);
generateJS($config);
echo <<<HTML
<title>{$i18n->title[$do][$lang]}</title>
<link rel="alternate" type="application/rss+xml" title="{$i18n->news['title'][$lang]}" href="{$config->feedrss[$lang]}" />
</HEAD>
HTML;
?>

<BODY class="top">
<DIV ID="main-container">		
	<DIV ID="main-header">
		<DIV ID="main-header-logo">
			<A HREF="<?php echo curPageURL(array('do'=>'home'));?>">
				<IMG ID="main-logo" ALT="<?php echo $i18n->image['logo-alt'][$lang];?>" SRC="<?php echo $config->imgdir."/".$config->logofile;?>">
			</A>
			<DIV ID="main-header-heading"><?php echo $i18n->image['logo-header'][$lang];?></DIV>
		</DIV>

		<DIV ID="main-header-right">
			<DIV ID="main-languages">
				<a href="<?php echo curPageURL(array('lg'=>'pt'));?>">Português</a> |  
				<a href="<?php echo curPageURL(array('lg'=>'en'));?>">English</a>	
			</DIV>
			
		 <!-- USER DIV -->
		
 			<?php if (!$user_login) {
				$user_login = $config->default_user[$lang];
				echo "<DIV ID='rrs-user' USER='" . $config->default_user[$lang] . "' USER_ID='". $config->default_user_id;
				echo "' api_key='" . $config->api_key . "'>";
				echo $config->default_user[$lang] . ". <A HREF='#' CLASS='USER_LOGIN'>" . $i18n->user['login'][$lang] ."</A>";
			} else {
				echo "<DIV ID='rrs-user' USER='" . $user_login . "' USER_ID='". $user_id ."'>";				
				echo $i18n->user['user'][$lang].": ". $user_name . " | ";
				echo "<A CLASS='USER_SETTINGS' HREF='#'>". $i18n->user['settings'][$lang]." </A> | ";
				if ($su == true) {echo "<A CLASS='USER_ADMIN' HREF='#'>". $i18n->user['admin'][$lang]." </A> | ";}
				echo "<A CLASS='USER_LOGOUT' HREF='#'>". $i18n->user['logout'][$lang]." </A>";
				
			}
			echo "</DIV>\n";
			?>
		</DIV>
   	</DIV>

	<DIV ID="main-menu">

	<?php generateNavMenu(); ?>

		<DIV ID="main-search-box">
			<FORM ID="form-search-box">
				<input ID="form-input-search-box" class="search-icon" type="text" name="sitesearch" value="<?php if (isset($_GET["sitesearch"])) echo $_GET["sitesearch"];?>">
	 			<input type="hidden" name="do" value="sitesearch">
	 		</FORM>
		</DIV> 		
	</DIV>
	
  <DIV ID="main-content">
	
   <?php 
	 $page = "";	

	// select all pages that don't need a side menu
	if ($do != "news" && $do != "sitesearch" && $do != 'collections' && $do != 'tag' && $do != 'search' && $do != 'home')  {
		echo "<DIV ID='main-side-menu'>\n";
		$sidemenu = generateSideMenuFor($do);

		if ($sidemenu) {	
			echo "<DIV ID='main-side-menu-header'>\n";
			echo $sidemenu["main"]["label"];
			echo "</DIV>\n";
			echo "<DIV CLASS=\"main-side-menu-section\">";
			echo "<DIV CLASS=\"main-side-menu-section-body\">";
			foreach($sidemenu["menu"] as $menu_item) {
				echo "<DIV CLASS=\"main-side-menu-section-body-element" 
				 .($menu_item['action'] == $do ? " main-side-menu-section-body-element-active" : "") . "\">" .	   
					"<A HREF=\"".($menu_item["url"] != null ? 
					  $menu_item["url"]  : $menu_item["link"])."\" "
		         . ($menu_item['action'] == $do ? " CLASS='disabled'" : "") . ">" 
			      .  $menu_item["label"] .  "</A>"  ;
				echo "</DIV>\n";
			}
			echo "</DIV>\n";
			echo "</DIV>\n";
		}
		echo "</DIV>\n";
	}
		
	// remove all pages where I don't want breadcrumbles or a flat line
	if ($do != "news" && $do != "sitesearch" && $do != 'collections' && $do != 'tag'  && $do != 'search')  {
	
		echo "<DIV ID='main-header-menu'>\n";
		echo "<DIV ID='main-breadcrumbles'>\n";
		echo generateBreadCrumbles($do, $lang); 
		echo "</DIV>\n";
		echo "</DIV>\n";
	}

	// main-body
	
	if ($do == "search" || $do == "tag" || $do == "collections") { 
		echo "<iframe onload=\"$(this).height($(this).contents().height());console.log($(this).height())\" id='myframe' name='myframe' width='100%' height='100%' frameborder=0 src='";
		switch($do) {
	  		case "search":
			$page = $config->urlbasedir."/".$config->wsdir.'/renoir-search.php?lg='.$lang; echo $page; break;

	  		case  "tag":
			$page = $config->urlbasedir."/".$config->wsdir.'/rembrandt-tag.php?lg='.$lang; echo $page; break;

	  		case  "collections":
			$page = $config->urlbasedir."/".$config->wsdir.'/saskia.php?lg='.$lang; echo $page; break;
		}
		echo "'></iframe>";

	} else if ($do != "search" && $do != "tag" && $do != "collections") { // renoir-search manages its own space */

		echo "<DIV ID='main-body'>\n";
	
	 	switch($do) {

		  case  "confirmregister":
			$page = $config->basedir."/".$config->docdir.'/confirm_register_'.$lang.'.php'; include($page); break;
	
		// main content
		  case "sitesearch":
			$page = $config->basedir."/".$config->incdir.'/site-search.php'; include($page); break;

		 //"download",
		case  "download-release":
			$page = $config->basedir."/".$config->docdir.'/download-releases_'.$lang.'.php'; include($page); break;
		case  "download-dependencies":
			$page = $config->basedir."/".$config->docdir.'/download-dependencies_'.$lang.'.php'; include($page); break;
		case  "download-db":
			$page = $config->basedir."/".$config->docdir.'/download-db_'.$lang.'.php'; include($page); break;

		//"help",
		case  "help-tutorial":
			$page = $config->basedir."/".$config->docdir.'/help-tutorial_'.$lang.'.php'; include($page); break;
		case  "help-faq":
			$page = $config->basedir."/".$config->docdir.'/help-faq_'.$lang.'.php'; include($page); break;
		case  "help-doc":
			$page = $config->basedir."/".$config->docdir.'/help-documentation_'.$lang.'.php'; include($page); break;
		case  "help-troubleshoot":
			$page = $config->basedir."/".$config->docdir.'/help-troubleshoot_'.$lang.'.php'; include($page); break;

		//"devel",
		case  "devel-configuration":
			$page = $config->basedir."/".$config->docdir.'/devel-configuration_'.$lang.'.php'; include($page); break;
		case  "devel-install":
			$page = $config->basedir."/".$config->docdir.'/devel-install_'.$lang.'.php'; include($page); break;
		case  "devel-api":
			$page = $config->basedir."/".$config->docdir.'/devel-api_'.$lang.'.php'; include($page); break;
		case  "devel-issues":
			$page = $config->basedir."/".$config->docdir.'/devel-issues_'.$lang.'.php'; include($page); break;
		case  "devel-wishlist":
			$page = $config->basedir."/".$config->docdir.'/devel-wishlist_'.$lang.'.php'; include($page); break;

		//"news",
		case  "news-blog":
			$page = $config->basedir."/".$config->docdir.'/news-blog_'.$lang.'.php'; include($page); break;
		case  "news-feeds":
			$page = $config->basedir."/".$config->docdir.'/news-feeds_'.$lang.'.php'; include($page); break;
 
		//"about", 
		case  "about-contact":
			$page = $config->basedir."/".$config->docdir.'/about-contact_'.$lang.'.php'; include($page); break;
		case  "about-references":
			$page = $config->basedir."/".$config->docdir.'/about-references_'.$lang.'.php'; include($page); break;
		case  "about-tos":
			$page = $config->basedir."/".$config->docdir.'/about-tos_'.$lang.'.php'; include($page); break;
		case  "about-acknowledgements":
			$page = $config->basedir."/".$config->docdir.'/about-acknowledgements_'.$lang.'.php'; include($page); break;
		case  "about-thanks":
			$page = $config->basedir."/".$config->docdir.'/about-thanks_'.$lang.'.php'; include($page); break;
		//"about-twitter"
		
		default:
		 
		 if ($do && $do != "home") { // for a specific page that doesn't exist...
			echo "<span style='color:red;'>".$i18n->message['page-not-available'][$lang]."</span>\n";
		 }
		 $page = $config->basedir."/".$config->docdir.'/home_'.$lang.'.php';
		 include($page); break;
	 	}
	}	
	
	// main footer 
	// não faz sentido colocar a data na página de notícias, ou nos resultados da pesquisa
	 if ($do != "news-feeds" && $do != "search" && $do != 'collections' && $do != 'renoir' && $do != 'sitesearch' && $do != 'tag') 
		{
			getLastModified($page);}
	 ?>	
	
   </DIV> 
	
</DIV>


<?php footer(); ?>

<DIV ID="rrs-messages" style="display:none;"></DIV>
<?php 
     if ($config->servername != "localhost") {analytics();} 
?>
</body>
</html>
