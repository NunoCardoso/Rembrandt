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

<BODY class="main">
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
		<IFRAME onload=\"$(this).height($(this).contents().height());\" id='myframe' name='myframe' width='100%' height='100%' frameborder=0 src='frontpage.php?lg=<?php echo $lang; ?>'>
		</IFRAME>
	</DIV> 	
	<?php footer(); ?>
</DIV>
<DIV ID="rrs-messages" style="display:none;"></DIV>
<?php 
     if ($config->servername != "localhost") {analytics();} 
?>
</body>
</html>
