<DIV CLASS="main-right-note" style="font-size:1.2em;">
REMBRANDT 1.3beta1 is avaliable. <a href="<?php echo curPageURL(array('do'=>'download-release'));?>">Download it now!</a>  
</DIV>

<H2>Welcome to REMBRANDT website.</h2> 

<P>is a text "semantification" platform that allows you to work over document entities, as in advanced document search or information extraction. Rembrandt is an acronym for <strong>R</strong>econhecimento de <strong>E</strong>ntidades <strong>M</strong>encionadas <strong>B</strong>aseado em <strong>R</strong>elações e <strong>AN</strong>álise <strong>D</strong>etalhada do <strong>T</strong>exto. </P>

<P>REMBRANDT can be used for: </P>

<DIV CLASS="main-center-note" >
<div CLASS="main-left-description">
<B>1. Document search</B> - Submit simple questions and add entities to your queries, to see documents that meet what you want. Results will be displayed in a new page.</div>
<div style="display:inline-block; clear:left;">
<?php include("inc/renoir_search_complete.php"); ?>
</div>
</DIV>

<DIV CLASS="main-center-note" >
<div CLASS="main-left-description">
<B>2. Document annotation</B> - Write / paste a text in the text area below, and push the button. Entities in the text will be annotated and grounded, and you may see relationships between them.</div>

<div style="display:inline-block; clear:left;">
		<DIV ID="rrs-waiting-div" CLASS="rrs-waiting-div" style="display:none;">
			<DIV CLASS="rrs-waiting-div-message"></DIV>
			<DIV CLASS="rrs-waiting-div-balloontip"></DIV>
		</DIV> 
<FORM></FORM><!-- must stay -->
  <DIV CLASS="rrs-logo"><IMG SRC="<?php echo $config->imgdir."/".$config->rembrandt_head_image;?>"></DIV>
  <FORM id="home1" TARGET="rembrandt-results-1" CLASS="rembrandt-submit" style="display:inline-block;">
  <TEXTAREA id="text" name="text" style="width:400px; height:60px;"><?php echo $config->exampleText[$lang]; ?></textarea><BR>
    <input type="hidden" id="language" name="language" value="<?php echo $lang; ?>">
    <input type="hidden" id="type" name="type" value="simpleText">
	 <div style="margin-left:60px;"><?php echo $i18n->message['textisin'][$lang]; ?> 
		<select size=1 id="submissionLang" name="submissionLang">
		<option value="pt" default>Portuguese</option>
		<option value="en">English</option>
		</select>.
	 </DIV>
	 <div style="margin-left:60px;display:inline-block;">
		<A CLASS="main-button" id="rembrandt-submit-button" HREF="#">
	   <SPAN><?php echo $i18n->buttons['execRembrandt'][$lang]; ?></SPAN></A>  
	</div>	
   </FORM>		

   <DIV ID="rembrandt-results-1" CLASS="rrs-doc-display" style="width:400px;"></DIV>
   <script> $(document).ready(function() {			
		appendDocDisplayTo($('#rembrandt-results-1'))
  })
  </script>		
</div>
</DIV>

Liked it? Read also the <a href="<?php echo curPageURL(array('do'=>'help-faq'));?>">FAQ</a> to know what is REMBRANDT, and <a href="<?php echo curPageURL(array('do'=>'help-tutorial'));?>">do the tutorial</A> about this service, by running a few examples and get acquainted with the results. If you want, you can look into the <a href="<?php echo curPageURL(array('do'=>'help-doc'));?>">REMBRANDT documentation</a>, which includes papers and presentations about this annotation tool.</P>

<P><B>Want to use REMBRANDT in your own computer?</B> Great! You can <a href="<?php echo curPageURL(array('do'=>'download-releases'));?>">download the latest version of REMBRANDT</a> and run it on your computer. The installation and configuration is a little tricky, but once you get the hang of it, you'll may run REMBRANDT in a more intense and automatic way. Oh, and REMBRANDT is free of charge, and open-source!</P>	

<P><B>Have any comments about REMBRANDT?</A></B> Found any run error? Great, because a software without bugs is like a garden without flowers. <a href="mailto:ncardoso@xldb.di.fc.ul.pt?subject=Nuno,%20REMBRANDT%20made%20an%20error!">Send me an email</a> and tell me what happened. If you want, you can send a <a href="mailto:ncardoso@xldb.di.fc.ul.pt?subject=Nuno,%20REMBRANDT%20rocks!">congratulations message</a> (I'm human, I also like that kind of messages).</P>  

<P><B>Becoming a fan of REMBRANDT?</A></B> Excellent! So keep up with it and <a href="<?php echo curPageURL(array('do'=>'news-blog'));?>">read the news</a> or <a href="<?php echo $config->feedrss[$lang];?>">subscribe the RSS feed</a>. for the twitter-o-holics, there's even a <a href="<?php echo $config->twitterURL; ?>">Twitter account of REMBRANDT to follow</a>. If you are willing to improve REMBRANDT, you can do it - the source code is available under the GPL license - or, if you are felling lazy today, you can <a href="<?php echo curPageURL(array('do'=>'devel-wishlist'));?>">add a request to REMBRANDT's wishlist table</A>, and I'll see what I can do.</P>
	

<hr>
<B>Warning:</B> the server might have a slow response because it's not a dedicated server. The interface is still under development, so some functionalities may be limited and/or with errors. We recommend recent versions of browsers Firefox, Opera, Safari and Chrome, because Internet Explorer doesn't fully comply with W3C recommendations for HTML and CSS, and the results may not be displayed.
