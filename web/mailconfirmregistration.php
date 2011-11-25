<?php

$api_key=$_POST["api_key"];
$email = $_POST["email"];
$lang = $_POST["lang"];
if (!$lang) $lang="en";

$title=array("en" => "SASKIA confirm registration", 
			"pt" =>"Confirmação do registo na SASKIA");
$host="xldb.di.fc.ul.pt";
$service="Rembrandt/?do=confirmregister&lg=".$lang."&a=".$api_key;
$message = "";

if ($lang == "pt")	{
	$message .= "<P>Viva.</P><BR><P>Alguém, provavelmente tu mesmo ou o administrador de um serviço REMBRANDT (<a href='http://xldb.di.fc.ul.pt/Rembrandt/'>http://xldb.di.fc.ul.pt/Rembrandt/</A>), fez um registo no serviço com este endereço de correio electrónico. Como tal, queremos que confirme o registo ao visitar o seguinte endereço:</P><P></P>";
	$message .= " <P>Confirmar registo: <A href='http://${host}/${service}'>http://${host}/${service}'</A></P><P></P>\n";
	$message .= " Se não foi você que quis registar-se, não se preocupe, sem a confirmação não há possibilidade de ser usado o seu nome e endereço de mail no sistema.</P>\n";
	$message .= "<P></P><P>Cumprimentos, </P><P></P><P>Rembrandt</P>"; 
} else if ($lang == "en") {
	$message .= "<P>Hello.</P><BR><P>Someone, probably you or the admin of a REMBRANDT service  (<a href='http://xldb.di.fc.ul.pt/Rembrandt/'>http://xldb.di.fc.ul.pt/Rembrandt/</A>), made a user registration using this email address. As such, we want you to confirm this request by visiting the following link:</P><P></P>";
	$message .= " <P>Confirm: <A href='http://${host}/${service}'>http://${host}/${service}'</A></P><P></P>\n";
	$message .= "If you did not requested the registration, don't worry -- without the confirmation, your name and email address cannot be used in the service.</P>\n";
	$message .= "<P></P><P>Cheers, </P><P></P><P>Rembrandt</P>"; 
}

$headers  = 'MIME-Version: 1.0' . "\r\n";
$headers .= 'Content-type: text/html; charset=UTF-8' . "\r\n";
$headers .= 'From: REMBRANDT <rembrandt.no.reply@${host}">' . "\r\n";

$send = mail ($email, $title[$lang], $message, $headers);

if ($send) {
	if ($lang == "pt")	{echo "Mail enviado.";}
	if ($lang == "en")	{echo "Mail sent.";}
} else {
	if ($lang == "pt")	{echo "Mail NÃO enviado.";}
	if ($lang == "en")	{echo "Mail NOT sent.";}	
}
?>
