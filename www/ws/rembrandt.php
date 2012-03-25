<?php 
require_once(dirname(__FILE__).'/../inc/lib.inc');
require_once(dirname(__FILE__).'/../inc/i18n.inc');
require_once(dirname(__FILE__).'/../inc/config.inc');
require_once(dirname(__FILE__).'/../inc/meta.inc');

$i18n =  i18n::getInstance();
$config = Config::getInstance();
$lang = $_GET['lg']; if (!$lang) $lang = $_POST['lg']; if (!$lang) $lang = 'pt';

echo <<<HTML
<!DOCTYPE html>
<html lang="{$lang}">
<title>{$i18n->rembrandt["title"][$lang]}</title>
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
    displayBodyOfRembrandt();
	appendDocDisplayTo($('#rembrandt-results-1'))
})
</SCRIPT>
<DIV ID='main-side-menu'>
	<DIV ID='main-side-menu-header'>
	<?php echo $i18n->message['options'][$lang]; ?>
	</DIV>
	
	<DIV CLASS='main-side-menu-section'>
		<DIV CLASS='main-side-menu-section-header'>
			<A HREF="#"><?php echo $i18n->rembrandt['tag-options'][$lang]; ?></A> 
		</DIV>
		<DIV CLASS='main-side-menu-section-body'>
			<!-- suggestion checkbox --> 
			<DIV CLASS='main-side-menu-section-body-element'>
			
				<fieldset>
				<label for="submissionLang"><?php echo $i18n->message['textisin'][$lang]; ?>:</label>
				 <select size=1 id="submissionLang" name="submissionLang">
					<option value="pt" default>Português</option>
					<option value="en">English</option>
				</select>.
			</DIV>
		</DIV>
	</DIV>	
</DIV>

<DIV ID="main-header-menu">

	<DIV ID='main-breadcrumbles' style="display:block;">
		<B><?php echo $i18n->rembrandt["tag-text"][$lang]; ?></B>  	
	</DIV>
	<DIV ID="main-header-menu-right"></DIV>
</DIV>

 <!-- form  --> 

<DIV ID="main-form">	
	<DIV ID="rrs-waiting-div" CLASS="rrs-waiting-div" style="display:none;">
		<DIV CLASS="rrs-waiting-div-message"></DIV>
		<DIV CLASS="rrs-waiting-div-balloontip"></DIV>
	</DIV>
	<DIV CLASS="rrs-logo">
		<IMG SRC="<?php echo $config->imgdir."/".$config->rembrandt_head_image;?>">
	</DIV>
	<FORM id="home1" TARGET="rembrandt-results-1" CLASS="rembrandt-submit">
		<TEXTAREA id="text" name="text" style="width:400px; height:60px;"><?php echo $config->exampleText[$lang]; ?></textarea>
		<BR>
		<input type="hidden" id="language" name="language" value="<?php echo $lang; ?>">
		<input type="hidden" id="type" name="type" value="simpleText">
		<div style="margin-left:60px;display:inline-block;">
			<A CLASS="main-button" id="rembrandt-submit-button" HREF="#">
				<SPAN><?php echo $i18n->buttons['execRembrandt'][$lang]; ?></SPAN>
			</A>  
		</div>	
	</FORM>
</DIV>

<!-- body -->

<DIV ID="main-body">		
	<DIV ID="rembrandt-results-1" CLASS="rrs-doc-display main-slidable-div">
	</DIV>
	<DIV style="clear:both; margin-top:50px;">
	<?php echo $i18n->rembrandt["main-page-text"][$lang]; ?>
	</DIV>	
</DIV>
</body>
</html>
