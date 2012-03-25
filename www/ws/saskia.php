<?php 
require_once(dirname(__FILE__).'/../inc/lib.inc');
require_once(dirname(__FILE__).'/../inc/i18n.inc');
require_once(dirname(__FILE__).'/../inc/config.inc');
require_once(dirname(__FILE__).'/../inc/meta.inc');

$i18n =  i18n::getInstance();
$config = Config::getInstance();
$lang = $_GET['lg']; if (!$lang) $lang = $_POST['lg']; if (!$lang) $lang = 'pt';
$su = $_COOKIE['su'];
$su_collection = $_COOKIE['su_collection'];

$collection = $_COOKIE['collection'];
$collection_id = $_COOKIE['collection_id'];

if (!$collection) $collection = $default_collection;
if (!$collection_id) $collection_id = $default_collection_id;

echo <<<HTML
<!DOCTYPE html">
<html lang="{$lang}">
<title>{$i18n->saskia["title"][$lang]}</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<base href="{$config->urlbasedir}/">
HTML;

generateCSS($do);
generateJS($config);
?>
</head>
<body class="top">
<script>
$(document).ready(function() {
   displayBodyOfSaskia();
})
</script>
<DIV ID='main-side-menu'>
	<DIV ID='main-side-menu-header'>
	 <?php echo $i18n->message['options'][$lang]; ?>
	</DIV>
	
	<DIV CLASS='main-side-menu-section'>
	</DIV>	
</DIV>

<DIV ID="main-header-menu" CLASS="main-space-for-side-menu">
  <DIV ID='main-breadcrumbles'></DIV>
</DIV>

<!-- body --> 

<DIV ID="main-form">
<!-- SEARCH DIV - includes logo -->
	<DIV ID="rrs-search-box">
		<DIV ID="rrs-waiting-div" CLASS="rrs-waiting-div" style="display:none;">
			<DIV CLASS="rrs-waiting-div-message"></DIV>
			<DIV CLASS="rrs-waiting-div-balloontip"></DIV>
		</DIV>
		<DIV CLASS="rrs-logo">
			<IMG SRC="<?php echo $config->imgdir."/".$config->saskia_head_image;?>">
		</DIV>	
	</DIV> 	
</DIV>

<DIV ID="main-body">
	<DIV ID='rrs-homepage-collection' CLASS='main-slidable-div' TITLE='<?php echo $i18n->saskia['collections'][$lang]; ?>'>

<?php if ($lang == "pt") { 
include "../docs/saskia_main_pt.php";
} else { 
include "../docs/saskia_main_en.php";
}
?>
	</DIV>
</DIV>
</body>
</html>