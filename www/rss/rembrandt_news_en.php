<?php
 require_once("../inc/lib.php");
 require_once("../inc/var.php");
date_default_timezone_set("Europe/Paris");
 global $newsfile_en,  $rembrandtwsdir, $basedir; 
 echo "<?xml version=\"1.0\"?>\n";
?>
<rss version="2.0">
   <channel>
      <title>REMBRANDT News</title>
      <link>http://xldb.di.fc.ul.pt/Rembrandt</link>
      <description>News about the REMBRANDT named entity recognition tool</description>
      <language>en_UK</language>
      <pubDate>Thu, 01 Dec 2008 00:00:00 GMT</pubDate>
      <lastBuildDate><?php echo date("r",filectime($basedir."/".$newsfile['en'])); ?></lastBuildDate>
      <?php //<docs>http://blogs.law.harvard.edu/tech/rss</docs> ?>
      <generator>REMBRANDT RSS generator</generator>
      <managingEditor>ncardoso@xldb.di.fc.ul.pt</managingEditor>
      <webMaster>ncardoso@xldb.di.fc.ul.pt</webMaster>
      <?php
		$arr = readNews('en');	
	    for($i = 1; $i<=$arr["total"]; $i++) {
		  echo "<item>\n";
		  echo "<title>".$arr[$i]['title']."</title>\n"; 
		  echo "<link>http://xldb.di.fc.ul.pt".$rembrandtwsdir."/index.php?lg=en&do=news#".$arr[$i]['time']."</link>\n";
	   	  echo "<description>".$arr[$i]['description']."</description>\n";
	 	  echo $arr[$i]['description']."\n";	
	      echo "<pubDate>".date("r", $arr[$i]['time'])."</pubDate>\n";
		  echo "<guid>http://xldb.di.fc.ul.pt".$rembrandtwsdir."/".md5($arr[$i]['description'])."</guid>\n";
          echo "</item>\n";
	   }
	 ?>
 </channel>
</rss>