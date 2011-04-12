<?php

require_once("./inc/var.php");

$errorreport = urldecode($_POST["errorreport"]);
$captcha = urldecode($_POST["captcha"]);
$lang = $_GET['lg'];
if (!$lang) $lang = $_POST['lg'];
if (!$lang) $lang = 'pt';

$time = time();
$day = date("j", $time);
$month = date("n", $time);

if ( intval($day+$month)  == intval($captcha)) {
	
   $mailmessage = "Relatório de erro: $errorreport.";
   $headers  = 'MIME-Version: 1.0' . "\r\n";
   $headers .= 'Content-type: text/plain; charset=UTF-8' . "\r\n";
   $headers .= 'From: Rembrandt Error Report <rembrandt.error.report@barbosa.di.fc.ul.pt">' . "\r\n";

	$sent = mail("ncardoso@xldb.di.fc.ul.pt", 
	   "O REMBRANDT recebeu um relatório de erro.", 
		$mailmessage, $headers);

	if ($sent) {
		if ($lang == "pt") {
			echo "<H2>Envio de relatório de erro com sucesso.</H2>\n";
			echo "<P>O seu relatório de erro foi enviado com sucesso.</P>\n";
			echo "<div class=\"results\">".preg_replace("/\n/","<BR>",$errorreport)."</div>\n";
			echo "<P>Obrigado pela sua ajuda. </P>\n";
		} else if ($lang == "en") {
			echo "<H2>Error report sent successfully.</H2>\n";
			echo "<P>Your error report was sent.</P>\n";
			echo "<div class=\"results\">".preg_replace("/\n/","<BR>",$errorreport)."</div>\n";
			echo "<P>Thank you for your help. </P>\n";
		}
	} else {
		if ($lang == "pt") {
			echo "<H2>Envio de relatório de erro NÃO foi enviado :(</H2>\n";
			echo "<P>Ocorreu um erro no envio do relatório. Por favor, <a href=\"mailto:ncardoso@xldb.di.fc.ul.pt?subject=Nuno,%20aqui%20%tens%20um%20relato%20de%20erro%20%do%20REMBRANDT%.\">envie-me um mail com o texto do relatório</a>. Obrigado. </P>\n";
			echo "<div class=\"results\">".preg_replace("/\n/","<BR>",$erroreport)."</div>\n";
		} else if ($lang == "en") {
			echo "<H2>Error report was NOT sent :(</H2>\n";
			echo "<P>There was an error while sending the report. Please, <a href=\"mailto:ncardoso@xldb.di.fc.ul.pt?subject=Nuno,%20here%20%you%20have%20an%20error%20report%20%of%20REMBRANDT%.\">send me an e-mail with the text below</a>. Thank you. </P>\n";
			echo "<div class=\"results\">".preg_replace("/\n/","<BR>",$erroreport)."</div>\n";
		} 
	}
	
// failed the captcha.
} else {
	if ($lang == "pt") {
		echo "<DIV class=\"results\"><H3>Mmmm... você é humano?</H3>";
		echo "<P>Você respondeu ";
		if ($captcha) echo "<B>$captcha</B>";
		else echo "nada";
		echo "... ora veja novamente a data: ".prettyPrintDate(time()).". Volte a tentar.</P></DIV>\n";
 } else if ($lang == "en") {
		echo "<DIV class=\"results\"><H3>Mmmm... are you human?</H3>";
		echo "<P>You answered ";
		if ($captcha) echo "<B>$captcha</B>";
		else echo "nothing";
		echo "... well, let's see the date again: ".prettyPrintDate(time()).". Try again.</P></DIV>\n";
 }
   $page = $basedir."/".$docdir.'/reporterror_'.$lang.'.php'; 
    include($page); 
}

