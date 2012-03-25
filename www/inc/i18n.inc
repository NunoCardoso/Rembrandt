<?php

require_once(dirname(__FILE__).'/config.inc');

class i18n {

  // singleton instance 
  private static $instance; 
  public $image, $nav_menu, $renoir, $saskia, $rembrandt, $news, $buttons, $message, $time, $search, $user;
  // private constructor function 
  // to prevent external instantiation 
  

  // getInstance method 
  public static function getInstance() { 

    if(!self::$instance) { 
      self::$instance = new i18n(); 
    } 
    return self::$instance; 
  } 

  private function __construct() {	
// For all image setups 
 $this->image = array(	
	"logo-alt" => array(
		"en" => "Rembrandt logo",
		"pt" => "Logotipo do Rembrandt"
	),
	"logo-header" => array(
		"en" => "Framework for Semantic Information Retrieval",
		"pt" => "Bancada para a recolha semântica de documentos"
	)
);

/* link = yes means that the nav menu item is clickable */
 $this->nav_menu = array(

   array(
		"action" => "tag",  "link" => "yes",
		"label" => array("en" => "Tag", "pt" => "Anotar", "no" => "Tag")
	),
	array(
	   "action" => "search", "link" => "yes",
		"label" => array("en" => "Search", "pt" => "Pesquisar", "no" => "Søk")
	),
  array(
		"action" => "collections",  "link" => "yes",
		"label" => array("en" => "Collections", "pt" => "Colecções", "no" => "Collections")
	),	
	array(
		"action" => "download",  "link" => "no",
		"label" => array("en" => "Download", "pt" => "Descarregar", "no" => "download"),
		"submenu" => array(
			array(
				"action" => "download-release",  "link" => "yes",
				"label" => array("en" => "Releases", pt => "Lançamentos") 
			),
			array(
				"action" => "download-dependencies",  "link" => "yes",
				"label" => array("en" => "Dependencies", pt => "Dependências") 
			),
			array(
				"action" => "download-db",  "link" => "yes",
				"label" => array("en" => "Databases", pt => "Bases de dados") 
			)	
		)
	), 
	array(
		"action" => "help",  "link" => "no",
		"label" => array("en" => "Help", "pt" => "Ajuda", "no" => "Hjelpe"),
		"submenu" => array(
			array(
				"action" => "help-tutorial",  "link" => "yes",
				"label" => array("en" => "Tutorial", pt => "Tutorial") 
			),
			array(
				"action" => "help-faq",  "link" => "yes",
				"label" => array("en" => "FAQ", pt => "Perguntas já respondidas") 
			),
			array(
				"action" => "help-doc",  "link" => "yes",
				"label" => array("en" => "Documentation", pt => "Documentação") 
			),
			array(
				"action" => "help-troubleshoot",  "link" => "yes",
				"label" => array("en" => "Troubleshoot", pt => "Problemas frequentes") 
			)
		)
	), 
	array(
		"action" => "devel",  "link" => "no",
		"label" => array("en" => "Developers", "pt" => "Programadores", "no" => "Programmers"),
		"submenu" => array(
			array(
				"action" => "devel-configuration",  "link" => "yes",
				"label" => array("en" => "Configuration", pt => "Configuração") 
			),
			array(
				"action" => "devel-install",  "link" => "yes",
				"label" => array("en" => "Install", pt => "Instalação") 
			),
			array(
				"action" => "devel-api",  "link" => "yes",
				"label" => array("en" => "API", pt => "API") 
			),
			array(
				"action" => "devel-issues",  "link" => "yes",
				"label" => array("en" => "Issues", pt => "Problemas") 
			),
			array(
				"action" => "devel-wishlist",  "link" => "yes",
				"label" => array("en" => "Wishlist", pt => "Caixa de desejos") 
			)
		)
	), 
	array(
		"action" => "news",  "link" => "no",
		"label" => array("en" => "News", "pt" => "Notícias", "no" => "Nyheter"),
		"submenu" => array(
			array(
				"action" => "news-blog",  "link" => "yes",
				"label" => array("en" => "Blog", pt => "Blogue") 
			),
			array(
				"action" => "news-feeds",  "link" => "yes",
				"label" => array("en" => "RSS Feeds", pt => "Canais RSS") 
			)
		)
	), 	
	array(
		"action" => "about",  "link" => "no",
		"label" => array("en" => "About us", "pt" => "Sobre nós", "no" => "Om oss"),
		"submenu" => array(
			array(
				"action" => "about-contact",  "link" => "yes",
				"label" => array("en" => "Contacts", pt => "Contactos") 
			),
			array(
				"action" => "about-references",  "link" => "yes",
				"label" => array("en" => "References", pt => "Referências") 
			),
			array(
				"action" => "about-tos",  "link" => "yes",
				"label" => array("en" => "Terms of service", pt => "Termos do serviço") 
			),
			array(
				"action" => "about-acknowledgements", "link" => "yes",
				"label" => array("en" => "Acknowledgements", pt => "Agradecimentos") 
			),
			array(
				"action" => "about-twitter",  "link" => "yes", "url" => 	$twitterURL,
				"label" => array("en" => "Follow us on Twitter", pt => "Segue-me no Twitter") 
			)
		)
	) 	
);

/** stuff for RENOIR search */
$this->renoir = array(
	'advanced-search' => array("en" => "Advanced search", "pt" => "Pesquisa avançada"),
	'search' => array("en" => "Search", "pt" => "Pesquisa")
);

$this->saskia = array(
	'collections' => array("en" => "Collections", "pt" => "Colecções")

);

$this->rembrandt = array (
	"title"=> array("en" => "REMBRANDT tagger", "pt" => "Anotação REMBRANDT"),
	"tag-options" => array("en" => "Annotation", "pt" => "Anotação"),
	"tag-text" => array("en" => "Annotate text", "pt" => "Anotar texto"),
	"main-page-text" => array(
		"pt" => "
	<P>Instruções:</P>
	<P>1. Insira o texto a anotar. Note que este serviço do REMBRANDT está limitado aos primeiros <?php echo $config->limit_text_bytes; ?> caracteres, para evitar a sobrecarga do servidor.</P>
	<P><B>Nota:</B> Esta página funciona perfeitamente no Firefox 3, Safari 4 e Opera 9+. No entanto, o IE parece que não gosta. Se calhar, está na altura de finalmente mudar de navegador...</P> 

	<P>Nota: o serviço pode ter uma resposta lenta porque o servidor está a ser usado para outros processos REMBRANDT intensivos.</P>
	",
	"en" => "
	<P>Instructions:</P>
	<P>1. Paste the text on the box. Note that this REMBRANDT web service is limited to the first <?php echo $limit_text_bytes; ?> characters, to avoid server overload.</P>
	<P>Also, note that REMBRANDT <B>does NOT have optimized grammar rules for English</B>, so the results may be somehow disappointing for now. I'm planning to optimize REMBRANDT to English text on a future release. Until then, mind that this service is <B>just a demonstration for English texts</B>. Thank you.</P>
	<P>the server might have a slow response because it's being used for other intensive REMBRANDT processes.</P>
	<P><B>Note:</B> This page runs fine on Firefox 3, Safari 4 and Opera 9+. IE, however, doesn't like it. Well, maybe it's time for you to finally switch browsers...</P>
	"
	)	
);

$this->news = array(
	"posted" => array("en" => "Posted", "pt" => "Colocado"), 
	"title" => array("en" => "REMBRANDT News", "pt" => "Notícias REMBRANDT")
	);
	
$this->buttons = array(
	"search" => array("en" => "Search", "pt" => "Pesquisar"),
	"execRembrandt" => array("en" => "Annotate", "pt" => "Anotar"),
	"sendErrorForm" => array("en" => "send error form", "pt" => "envie o relatório"),
	"prev" => array("en" => "Previous", "pt" => "&lt;Anterior"),
	"next" => array("en" => "Next", "pt" => "Seguinte&gt;")
);
	
$this->message = array(
	"yes" => array("en" => "Yes", "pt" => "No"),
	"no" => array("en" => "No", "pt" => "Não"),
	"home" => array("en" => "Homepage", "pt" => "Página principal"),
	"textisin" => array("en" => "Text is in", "pt" => "O texto está em"),
	"lastModified" => array("en" => "Last modified", "pt" => "Última alteração"),
	"options" => array("en" => "Options", "pt" => "Opções"),
	"page-not-available" => array("en" => "Page not available. Redirected to main page.",
		"pt" => "Página não disponível. Redireccionado para página principal."
	)
);
	
$this->time = array(
	"sec" => array("en" => "second", "pt" => "segundo"),
	"min" => array("en" => "minute", "pt" => "minuto"),
	"hr" => array("en" => "hour", "pt" => "hora"),
	"day" => array("en" => "day", "pt" => "dia"),
	"mth" => array("en" => "month", "pt" => "mês"),
	"yr" => array("en" => "year", "pt" => "ano"),
	"ago" => array("en" => "ago", "pt" => "atrás") 
);

$this->search = array(
	"suggestions" => array("en" => "Suggestions", "pt" => "Sugestões"),
	"results" => array("en" => "Results", "pt" => "Resultados"),
	"search-advanced" => array("en" => "Advanced search", "pt" => "Pesquisa avançada"),
	'visualization' => array("en" => "View", "pt" => "Ver"),
	"qe" => array("en" => "Expansion", "pt" => "Expansão"),
	"model" => array("en" => "Model", "pt" => "Modelo"),
	"stem" => array("en" => "Stem", "pt" => "Lemas"),
	"maps" => array("en" => "Maps", "pt" => "Mapas"),
	"feedback" => array("en" => "See feedback", "pt" => "Ver retorno"),
	"partialscores" => array("en" => "Scores", "pt" => "Pontuações"),
	"noresultsfound" => array("en" => "No results found.", "pt" => "Não encontrei resultados." 
	)
); 

$this->user = array(
	"user" => array("en" => "User", "pt" => "Utilizador"),
	"admin" => array("en" => "Admin", "pt" => "Gerir"),
	"settings" => array("en" => "Settings", "pt" => "Definições"),
	"guest" => array("en" => "Guest", "pt" => "Convidado"),
	"login" => array("en" => "Login / Register", "pt" => "Entrar / Registar"),
	"logout" => array("en" => "Logout", "pt" => "Sair"),
	"collection" => array("en" => "Collection", "pt" => "Colecção")
);
}
}
?>
