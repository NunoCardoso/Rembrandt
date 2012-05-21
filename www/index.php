<?php

require_once(dirname(__FILE__).'/inc/lib.inc');
require_once(dirname(__FILE__).'/inc/meta.inc');
require_once(dirname(__FILE__).'/inc/i18n.inc');
require_once(dirname(__FILE__).'/inc/config.inc');
require_once(dirname(__FILE__).'/inc/Mustache.php');

header('Content-Type: text/html; charset=utf-8');
header('X-Frame-Options: GOFORIT'); 

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
<script type="text/javascript" src="js/lib/jquery.iframe-auto-height.plugin.1.6.0.min.js"></script>
<title>{$i18n->title[$do][$lang]}</title>
<link rel="alternate" type="application/rss+xml" title="{$i18n->news['title'][$lang]}" href="{$config->feedrss[$lang]}" />

<style>
html, body { height: 100% }
</style>

</HEAD>
HTML;
?>

<BODY class="main">
	<script>

	
//	$(document).ready(function () {
 //   $('iframe').iframeAutoHeight();  
 // });
</script>
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
			
		</DIV>
   	</DIV>

	<DIV ID="main-menu">

	<?php generateNavMenu(); ?>

		<DIV ID="main-search-box">
			<FORM ID="form-search-box">
				<input ID="form-input-search-box" class="search-icon" type="search" name="sitesearch" value="<?php if (isset($_GET["sitesearch"])) echo $_GET["sitesearch"];?>">
	 			<input type="hidden" name="do" value="sitesearch">
	 		</FORM>
		</DIV> 		
	</DIV>
	
	<DIV ID="main-content">
		<IFRAME id="myframe" name='myframe' width='100%' height='100%' frameborder=0 src='frontpage.php?lg=<?php echo $lang; ?>'>
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
