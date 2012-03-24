<?php 
function generateCSS($do) {
	if ($do == "news-blog") { 
echo <<<HTML
	<link rel="stylesheet" type="text/css" href="css/rss.css" />	
HTML;
	}
echo <<<HTML
	<link rel="stylesheet" type="text/css" href="css/rrs.main.css" />
	<link rel="stylesheet" type="text/css" href="css/rrs.commonui.css" />
	<link rel="stylesheet" type="text/css" href="css/rrs.ne.css" />
	<link rel="stylesheet" type="text/css" href="css/rrs.docdisplay.css" />
	<link rel="stylesheet" type="text/css" href="css/rrs.admin.css" />
	<link rel="stylesheet" type="text/css" href="css/rrs.tabs.css" />
	<link rel="stylesheet" type="text/css" href="css/rrs.stats.css" />
	<link rel="stylesheet" type="text/css" href="css/lib/jquery.visualize.css" />	
	<link rel="stylesheet" type="text/css" href="css/lib/jquery.autocomplete.css" />
	<link rel="stylesheet" type="text/css" href="css/rrs.tags.css" />	
HTML;
}

function generateJS($config) {
	$gmaps = $config->google_maps_url;
echo <<<HTML
<!--	<script type="text/javascript" src="{$gmaps}"></script>
	<script type="text/javascript"  src="http://gmaps-utility-library.googlecode.com/svn/trunk/markermanager/release/src/markermanager.js" ></script>
-->
	<script type="text/javascript" src="js/lib/jquery-1.4.2.min.js" ></script>
	<script type="text/javascript" src="js/lib/ICanHaz.js"></script>
	<script type="text/javascript" src="js/lib/jquery.json-2.2.min.js" ></script>
	<script type='text/javascript' src='js/lib/jquery.cookie.js' ></script>
	<script type="text/javascript" src="js/lib/jquery.tablesorter.js" ></script>
	<script type="text/javascript" src="js/lib/jquery-ui-1.8.2.custom.min.js" ></script>
	<script type="text/javascript" src="js/ui/jquery.ui.selectable.js" ></script>
	<script type='text/javascript' src='js/ui/jquery.ui.sortable.js' ></script>
	<script type="text/javascript" src="js/ui/jquery.effects.core.js" ></script>
	<script type="text/javascript" src="js/ui/jquery.effects.explode.js" ></script>
	<script type='text/javascript' src='js/lib/jquery.hoverflow.min.js' ></script>
	<script type="text/javascript" src="js/lib/jquery.bt.js" ></script>
	<script type="text/javascript" src="js/lib/jquery.svg.js" ></script>
	<script type="text/javascript" src="js/lib/jquery.drawinglibrary.js" ></script>
	<script type="text/javascript" src="js/lib/jquery.color.js" ></script>
	<script type="text/javascript" src="js/lib/jquery.simplemodal-1.3.js" ></script>
	<script type='text/javascript' src='js/lib/jquery.autocomplete.js' ></script>
	<script type="text/javascript" src="js/lib/md5.js" ></script>
	<script type="text/javascript" src="js/lib/jquery.timeout.js" ></script>
	<script type="text/javascript" src="js/lib/jquery.visualizeNuno.js" ></script>
	<script type='text/javascript' src='js/lib/jquery.bgiframe.min.js' ></script>
	<script type='text/javascript' src='js/lib/jquery.ajaxQueue.js' ></script>
	<?php // editinplaceNuno requires cookie calls with getAPIKey() ?>
	<script type="text/javascript" src="js/rrs.common.js" ></script>
	<script type="text/javascript" src="js/lib/jquery.editinplaceNuno.js" ></script> 

	<script type="text/javascript" src="js/rrs.menu.js" ></script>
	<script type="text/javascript" src="js/rrs.i18n.js" ></script>
	<script type="text/javascript" src="js/rrs.tablelist.js" ></script>
	<script type="text/javascript" src="js/rrs.docdisplay.js" ></script>
	<script type="text/javascript" src="js/rrs.waitingdiv.js" ></script>
	<script type="text/javascript" src="js/rrs.stats.js" ></script>
	<script type="text/javascript" src="js/rrs.google.js" ></script>

	<script type="text/javascript" src="js/rrs.searchresult.js" ></script>
	<script type="text/javascript" src="js/rrs.entity.js" ></script>
	<script type="text/javascript" src="js/rrs.geoscope.js" ></script>
	<script type="text/javascript" src="js/rrs.ne.js" ></script>
	<script type="text/javascript" src="js/rrs.subject.js" ></script>
	<script type="text/javascript" src="js/rrs.subjectground.js" ></script>
	<script type="text/javascript" src="js/rrs.task.js" ></script>
	<script type="text/javascript" src="js/rrs.user.js" ></script>
	<script type="text/javascript" src="js/rrs.doc.js" ></script>
	<script type="text/javascript" src="js/rrs.collection.js" ></script>
	<script type="text/javascript" src="js/rrs.admin.js" ></script>

	<script type="text/javascript" src="js/rrs.saskia.js" ></script>
	<script type="text/javascript" src="js/rrs.renoir.js" ></script>
	<script type="text/javascript" src="js/rrs.rembrandt.js" ></script>
HTML;
}
?>