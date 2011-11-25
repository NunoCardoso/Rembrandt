<?php
 require_once("../inc/lib.php");
 require_once("../inc/var.php");
date_default_timezone_set("Europe/Paris");

 global $newsfile_pt, $rembrandtwsdir, $basedir; 
 echo "<?xml version=\"1.0\"?>\n";
?>
<rss version="2.0">
   <channel>
      <title>Notícias REMBRANDT</title>
      <link>http://xldb.di.fc.ul.pt/Rembrandt</link>
      <description>Notícias sobre o sistema de reconhecimento de entidades mencionadas REMBRANDT</description>
      <language>pt_PT</language>
      <pubDate>Thu, 01 Dec 2008 00:00:00 GMT</pubDate>
      <lastBuildDate><?php echo date("r",filectime($basedir."/".$newsfile['pt'])); ?></lastBuildDate>
      <?php //<docs>http://blogs.law.harvard.edu/tech/rss</docs> ?>
      <generator>REMBRANDT RSS generator</generator>
      <managingEditor>ncardoso@xldb.di.fc.ul.pt</managingEditor>
      <webMaster>ncardoso@xldb.di.fc.ul.pt</webMaster>
      <?php
		$arr = readNews('pt');	
	    for($i = 1; $i<=$arr["total"]; $i++) {
		  echo "<item>\n";
		  echo "<title>".$arr[$i]['title']."</title>\n"; 
		  echo "<link>http://xldb.di.fc.ul.pt".$rembrandtwsdir."/index.php?lg=pt&do=news#".$arr[$i]['time']."</link>\n";
	   	  echo "<description>".$arr[$i]['description']."</description>\n";
	 	  echo $arr[$i]['description']."\n";
	      echo "<pubDate>".date("r", $arr[$i]['time'])."</pubDate>\n";
		  echo "<guid>http://xldb.di.fc.ul.pt".$rembrandtwsdir."/".md5($arr[$i]['description'])."</guid>\n";
          echo "</item>\n";
	   }
	 ?>
 </channel>
</rss>