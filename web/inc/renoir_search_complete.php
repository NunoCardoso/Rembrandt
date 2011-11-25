<!-- SEARCH DIV - includes logo -->
<DIV ID="rrs-search-box">

	<DIV CLASS="rrs-logo"><IMG SRC="<?php echo $config->imgdir."/".$config->renoir_head_image;?>"></DIV>
		<FORM ACTION="<?php echo curPageURL(array('do'=>'search'));?>" ID="rrs-search-form" 
		  METHOD="POST" AUTOCOMPLETE="OFF" style="display:inline-block;">
		<TEXTAREA style="width:400px; height:40px;" CLASS="ac_input" AUTOCOMPLETE="OFF" ID="q" 
		NAME="q"><?php echo utf8_encode($query); ?></TEXTAREA>
	    
		<DIV ID="rrs-search-advanced"> 
			 <INPUT TYPE="CHECKBOX" ID="rrs-search-suggestion" 
			<?php	if (!$suggestion || $suggestion != "false") echo "CHECKED";?>> 
			 <?php echo $i18n->search["suggestions"][$lang];?> | 
			
			<A HREF="#" ID="rrs-search-advanced-link">
			   <?php echo $i18n->search["search-advanced"][$lang];?>
			   <DIV ID="rrs-search-advanced-arrows" style="display:inline">>></DIV>
			   </A>
		</DIV>
		
		<!-- ADVANCED SEARCH DIV -->
 		<DIV style="display:none;" ID="rrs-search-advanced-div">
			<?php echo $i18n->search['qe'][$lang];?>: <SELECT style="display:inline;" ID="as_qe" SIZE="1">
				
				<OPTION VALUE="no"<?php if ($_POST['as_qe'] === "no") echo " SELECTED";?>>No</OPTION>
				<OPTION VALUE="brf"<?php if ($_POST['as_qe'] === "brf") echo " SELECTED";?>>BRF</OPTION>
				<OPTION VALUE="sqr"<?php if ($_POST['as_qe'] === "sqr") echo " SELECTED";?>>SQR</OPTION>
				</SELECT>
			<?php echo $i18n->search['model'][$lang];?>: <SELECT style="display:inline;" ID="as_model" SIZE="1">
				<OPTION value="bm25"<?php if ($_POST['as_qe'] === "bm25") echo " SELECTED";?>>BM25</OPTION>
				</SELECT>
		    <INPUT TYPE="CHECKBOX" ID="as_maps" <?php if ($_POST['as_maps'] != "false") echo " CHECKED";?>> 
			<?php echo $i18n->search['maps'][$lang];?>
			 <INPUT TYPE="CHECKBOX" ID="as_feedback" <?php if ($_POST['as_feedback'] != "false") echo " CHECKED";?>> 
			<?php echo $i18n->search['feedback'][$lang];?>   
		</DIV>
			
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

		<div style="margin-left:140px;">
				<A CLASS="main-button" id="rrs-search-submit-button" HREF="#">
					<SPAN><?php echo $i18n->buttons["search"][$lang];?></SPAN></A>  
		</div>	
</DIV>