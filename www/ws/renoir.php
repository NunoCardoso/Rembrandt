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
<!DOCTYPE html>
<html lang="{$lang}">
<title>{$i18n->renoir["title"][$lang]}</title>
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
   displayBodyOfRenoir()
})
</SCRIPT>
<DIV ID='main-side-menu'>
	<DIV ID='main-side-menu-header'>
	 <?php echo $i18n->message['options'][$lang]; ?>
	</DIV>
	
	<DIV CLASS='main-side-menu-section'>
		
		<DIV CLASS='main-side-menu-section-header'>
			<A HREF="#"><?php echo $i18n->renoir['advanced-search'][$lang]; ?></A> 
		</DIV>
		
		<DIV CLASS='main-side-menu-section-body'>
			<!-- suggestion checkbox --> 
			<DIV CLASS='main-side-menu-section-body-element'>
				<fieldset>
				<INPUT TYPE="CHECKBOX" ID="rrs-search-suggestion" 
				<?php if (!$suggestion || $suggestion != "false") echo "CHECKED";?>> 
			 	<label for="rrs-search-suggestion">
				<?php echo $i18n->search["suggestions"][$lang];?>
				</label>
				</fieldset>
			</DIV>
			
			<!-- qe -->
			<DIV CLASS='main-side-menu-section-body-element'>
				<fieldset>
					<label for="as_qe"><?php echo $i18n->search['qe'][$lang];?>:</label>
					<select name="as_qe" id="as_qe">
						<OPTION VALUE="no"<?php if ($_POST['as_qe'] === "no") echo " SELECTED";?>>
						<?php echo $i18n->message['no'][lang]; ?></OPTION>
						<OPTION VALUE="brf"<?php if ($_POST['as_qe'] === "brf") echo " SELECTED";?>>BRF</OPTION>
						<OPTION VALUE="sqr"<?php if ($_POST['as_qe'] === "sqr") echo " SELECTED";?>>SQR</OPTION>
					</select>
				</fieldset>
			</DIV>

			<!-- model -->
			<DIV CLASS='main-side-menu-section-body-element'>
			<fieldset>
			<label for="as_model"><?php echo $i18n->search['model'][$lang];?>:</label>
			<select name="as_model" id="as_model">
				<OPTION value="bm25"<?php if ($_POST['as_model'] === "bm25") echo " SELECTED";?>>BM25</OPTION>
				<OPTION value="lm"<?php if ($_POST['as_model'] === "lm") echo " SELECTED";?>>LM</OPTION>
			</select>
			</fieldset>
			</DIV>
			
			<!-- stem -->
			<DIV CLASS='main-side-menu-section-body-element'>
			<fieldset>
			<INPUT TYPE="CHECKBOX" ID="as_stem" 
			<?php	if ($_POST['as_stem'] === "true") echo "CHECKED";?>> 
			 
			<label for="as_stem"><?php echo $i18n->search['stem'][$lang];?></label>
			</fieldset>
			</DIV>	
			
			<!-- partscores -->
			<DIV CLASS='main-side-menu-section-body-element'>
			<fieldset>
					<INPUT TYPE="CHECKBOX" ID="as_partialscores" 
			<?php	if ($_POST['as_partialscores'] === "true") echo "CHECKED";?>> 
			<label for="as_partialscores"><?php echo $i18n->search['partialscores'][$lang];?></label>
			</fieldset>
			</DIV>	
		</DIV>
	</DIV>	

	<DIV CLASS='main-side-menu-section'>
	
		<DIV CLASS='main-side-menu-section-header'>
			<A HREF="#"><?php echo $i18n->search['visualization'][$lang]; ?></A> 
		</DIV>
		
		<DIV CLASS='main-side-menu-section-body'>
			<!-- suggestion checkbox --> 
			<DIV CLASS='main-side-menu-section-body-element'>
				<fieldset>
				<INPUT TYPE="CHECKBOX" ID="as_maps" 
			<?php	if (!$as_maps || $as_maps != "false") echo "CHECKED";?>> 
			 <label for="as_maps"><?php echo $i18n->search["maps"][$lang];?></label>
		  </fieldset>
			</DIV>
		</DIV>	
			
  	</DIV>
</DIV>

<DIV ID="main-header-menu">
	<DIV ID='main-breadcrumbles' style="display:block;"></DIV>
	<DIV ID="main-header-menu-right">
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
	   <!-- COLLECTION DIV  -->
		<DIV ID="rrs-collections" DEFAULT="<?php echo $default_collection; ?>" 
			DEFAULT_ID="<?php echo $default_collection_id; ?>">
      <?php 
			echo $user['collection'][$lang].": <A HREF='#' CLASS='collection' COLLECTION='";
			echo $collection .  "' COLLECTION_ID='" . $collection_id. "'>" . $collection . "</A>";
		?>
	   </DIV>
	</DIV>
</DIV>

<!-- body --> 

<DIV ID="main-form">
<!-- SEARCH DIV - includes logo -->
	<DIV ID="rrs-search-box">
		<DIV ID="rrs-waiting-div" CLASS="rrs-waiting-div" style="display:none;">
			<DIV CLASS="rrs-waiting-div-message"></DIV>
			<DIV CLASS="rrs-waiting-div-balloontip"></DIV>
		</DIV>
		<DIV CLASS="rrs-logo"><IMG SRC="<?php echo $config->imgdir."/".$config->renoir_head_image;?>"></DIV>
		<FORM ACTION="<?php echo curPageURL(array('do'=>'search'));?>" ID="rrs-search-form" 
		  METHOD="POST" AUTOCOMPLETE="OFF" style="display:inline-block;">
		 <TEXTAREA style="width:500px; height:22px;" CLASS="ac_input" AUTOCOMPLETE="OFF" ID="q" 
		 NAME="q"><?php echo utf8_encode($query); ?></TEXTAREA>

		<!-- SEMANTIC TAGS DIV -->
		<DIV ID="rrs-search-tags">
			<?php 
			if ($tag) {	$tags = json_decode($tag, true);}	
			//echo $tags;
			$index = 0;
			if ($tags) {
				foreach($tags as $tag){
					echo "<DIV ID='tag_".(++$index)."' CLASS='tag tag_type_".$tag['type']."'" ;
					echo " value='".$tag['name']."'>".$tag['name']." ";
					echo "<A CLASS='tag_remove'>&times;</A></DIV>";
				}
			}
			?>	
		</DIV> 
		<DIV style="margin-left:190px;">
			<A CLASS="main-button" ID="rrs-search-submit-button" HREF="#">
			<SPAN><?php echo $i18n->buttons["search"][$lang];?></SPAN></A>  
		</DIV>
		</FORM>	
   </DIV> 	
</DIV>

<DIV ID="main-body"></DIV>
