<?php

class Config {

  // singleton instance 
  private static $instance; 

  // getInstance method 
  public static function getInstance() { 

    if(!self::$instance) { 
      self::$instance = new Config(); 
    } 
    return self::$instance; 
  } 

  private function __construct() {
 	$this->servername = ($_SERVER["SERVER_NAME"] == 'localhost' ? 'localhost' : 'agatha.inesc-id.pt');//$_SERVER["SERVER_NAME"];	
	$this->basedir = $_SERVER['DOCUMENT_ROOT']."/".$this->rembrandtdir;
	$this->urlbasedir = "http://".$this->servername."/".$this->rembrandturldir;
	
	$this->feedrss = array(
		"en" => $this->feeddir."/rembrandt_news_en.php", 
		"pt" => $this->feeddir."/rembrandt_news_pt.php"
		);
	$this->newsfile =array(
		"en" => $this->feeddir."/noticias_en.txt",
		"pt" => $this->feeddir."/noticias_pt.txt"
	);
	$this->google_maps_url = "http://maps.google.com/maps?file=api&v=2&key=".$this->google_maps_api_key[$this->servername];
  }
	
public $rembrandtLatestVersion='1.5';
public $rembrandtdir='Rembrandt';
public $rembrandturldir='Rembrandt';
public $servername;
public $basedir;
public $urlbasedir;
public $imgdir="img";
public $docdir="docs";
public $feeddir="rss";
public $incdir="inc";
public $wsdir="ws";
public $logofile="Rembrandt-header.png";
public $rembrandt_head_image="rembrandt-head.png";
public $renoir_head_image="renoir-head.png";
public $saskia_head_image="saskia-head.png";
public $twitterURL="http://www.twitter.com/rembrandtNER";
public $rembrandtGoogleCodeURL="http://rembrandt.googlecode.com";

//// END OLD /// 

public $limit_text_bytes=3000;

public $feedrss;
public $newsfile;

public $exampleText = array(
	"en" => "Rembrandt is a dutch painter. He was born on the 15th July 1606 in Leiden, Netherlands. He studied at Leiden University.",
	"pt" => "O Rembrandt é um pintor holandês. Nasceu a 15 de Julho de 1606 em Leiden, nos Países Baixos. Estudou na Universidade de Leiden."
);

public $exampleRenoirTest = array(
	"en" => "Pinturas de ",
	"pt" => "Pinturas de "
);
	
public $yahoo_app_id="9RcxNNPV34FQSwwtIRJZYhzapEKba2rz8haXGFvDbm1HHQTskH8_iNEpuWibi.QqHUbx";

public $google_maps_api_key = array("localhost" => "ABQIAAAA5N1I81XXzTy-kJKp_sG7-hT2yXp_ZAY8_ufC3CFXhHIE1NvwkxTEkoK8J8rTk0Dep-TPkfEgdtnYIw",
"xldb.di.fc.ul.pt" => "ABQIAAAA5N1I81XXzTy-kJKp_sG7-hQHXu6boNkANFFmoLeLMGNKBiyHnRR2lg5CqZ8X7BXzCQKbv6SNtmrR8g",
"agatha.inesc-id.pt" => "ABQIAAAA5N1I81XXzTy-kJKp_sG7-hQHXu6boNkANFFmoLeLMGNKBiyHnRR2lg5CqZ8X7BXzCQKbv6SNtmrR8g");

// this picks the right API key, according to the base sit
public $google_maps_url;

public $searchsite="agatha.inesc-id.pt/Rembrandt";

public $default_collection="CD2 do Segundo HAREM";
public $default_collection_id = 8;

public $default_user=array("en" => "Guest","pt" => "Convidado");
public $default_user_id=1;
}
?>