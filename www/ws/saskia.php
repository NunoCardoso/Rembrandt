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

?>
<html lang="<?php echo $lang;?>">
<title><?php echo $i18n->saskia["title"][$lang];?></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<base href="<?php echo $config->urlbasedir."/";?>">
<?php
	generateCSS($do);
	generateJS($config);
?>
</head>

<body>
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
	<DIV ID="rrs-waiting-div" CLASS="rrs-waiting-div" style="display:none;">
		<DIV CLASS="rrs-waiting-div-message"></DIV>
		<DIV CLASS="rrs-waiting-div-balloontip"></DIV>
	</DIV> 
  <DIV ID='main-breadcrumbles'></DIV>		
</DIV>

<!-- body --> 

<DIV ID="main-form">
<!-- SEARCH DIV - includes logo -->
	<DIV ID="rrs-search-box">
		<DIV CLASS="rrs-logo"><IMG SRC="<?php echo $config->imgdir."/".$config->saskia_head_image;?>"></DIV>	
	</DIV> 	
</DIV>

<DIV ID="main-body">
	<DIV ID='rrs-homepage-collection' CLASS='main-slidable-div' TITLE='<?php echo $i18n->saskia['collections'][$lang]; ?>'>

<?php if ($lang == "pt") { ?>
	<H3>Benvindo à SASKIA.</H3>
	<P>Aqui poderá criar e gerir a sua própria colecção de textos e anotá-los com o REMBRANDT. Para saber mais sobre as anotações do REMBRANDT, visite a <a href="<?php echo curPageURL(array('do'=>'help-tutorial'));?>">página de demonstação do REMBRANDT</a>. Para pesquisar as colecções acessíveis a convidados, vá à <a href='<?php echo curPageURL(array('do'=>'search'));?>'>página de pesquisas</a> e escolha a sua colecção. </P>

	<H3>Novo utilizador?</H3>
	<P>Para começar a sua nova colecção, primeiro <a class='USER_LOGIN' href='#'>registe-se ou entre o seu login</A>.
	<H3>Estas são as colecções disponíveis:</H3>
<?php } else { ?>
	<H3>Welcome to SASKIA.</H3>
	<P>Here you can create and manage your own text collection, tag it with REMBRANDT. For more information about REMBRANDT annotations, please visit the <a href="<?php echo curPageURL(array('do'=>'help-tutorial'));?>"> REMBRANDT demo page</a>. 
To search the collections accessible to guests, visit the <a href="<?php echo curPageURL(array('do'=>'search'));?>">search page</a> and choose your collection.</P>

	<H3>New user?</H3>
	<P>To start your new collection, first you <a class='USER_LOGIN' href='#'>have to register or login</A>.
	<H3>These are the accessible collections:</H3>
<?php } ?>
	</DIV>
</DIV>
</body>
</html>