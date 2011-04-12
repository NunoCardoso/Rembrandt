<?php 
global $lang, $default_collection, $default_collection_id;
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
?>

<SCRIPT>
function updateDisplay() {
	displayBodyOfSaskia()
}
</SCRIPT>
<DIV ID="rrs-body">
<DIV id="rrs-header" style="height:auto;">
   <DIV id="rrs-right-header">
	   <!-- USER DIV -->
	   <DIV ID="rrs-user">
      <?php if (!$user_login) {
			$user_login = $default_user[$lang];
			echo $default_user[$lang] . ". <A HREF='#' CLASS='user' USER='";
			echo $default_user[$lang] . "' USER_ID='". $default_user_id ."' TITLE='login'>";
			echo $user['login'][$lang] ."</A>";
		} else {
			echo $user['user'][$lang].": <A HREF='#' CLASS='user' USER='";
			echo $user_login . "' USER_ID='". $user_id ."' TITLE='usermenu'>" .  $user_name . "</A>";
	
			if ($su) {
				echo " <A HREF='#' CLASS='user-admin'>". $user['admin'][$lang]."</A>";
				//	echo "<SCRIPT>$.getScript('js/rembrandt.i18n.admin.js');";
				//	echo "$.getScript('js/rembrandt.admin.js');</SCRIPT>";
			}
		}
		?>
	   </DIV>
	
	   <!-- COLLECTION DIV  -->
		<DIV ID="rrs-collections" DEFAULT="<?php echo $default_collection; ?>" DEFAULT_ID="<?php echo $default_collection_id; ?>">
      <?php 
			echo $user['collection'][$lang].": <A HREF='#' CLASS='collection' COLLECTION='";
			echo $collection .  "' COLLECTION_ID='" . $collection_id. "'>" . $collection . "</A>";
			
			if ($su_collection) {
				echo " <A HREF='#' CLASS='collection-admin'>". $user['admin'][$lang]."</A>";
			}
		?>
	   </DIV>
	</DIV>
	
 	<!-- SEARCH DIV - includes logo -->
	<DIV ID="rrs-search-box">
    	<DIV ID="rrs-logo"><IMG src="images/Saskia-beta.png"></DIV>
      </DIV> 	
    </DIV>


<DIV ID="rrs-content"><DIV ID="rrs-waiting-div"></DIV> 	
<SCRIPT>
updateDisplay();
</SCRIPT>

</DIV>
</DIV>