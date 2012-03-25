<?php
$newpassword = $_POST["newpassword"];
$tmp_api_key=$_POST["tmp_api_key"];
$email = $_POST["email"];
$lang = $_POST["lang"];
if (!$lang) $lang = "en";

$title=array("en" => "REMBRANDT's SASKIA password recovery", "pt" =>"Recuperação de senha do serviço SASKIA, do REMBRANDT");
$host="xldb.di.fc.ul.pt";
$service="Saskia/user?do=confirmpassword&lg=".$lang."&tmp_api_key=".$tmp_api_key;

$message = "";

if ($lang == "pt")	{
	$message .= "<P>Viva.</P><BR><P>Alguém, provavelmente tu mesmo ou o administrador do serviço SASKIA, pediu para recuperar a senha do serviço SASKIA, associada a este endereço de correio. Como tal, o serviço gerou a seguinte senha temporária:<P><P></P>";
	$message .= " <P>senha: ".$newpassword."</P><P></P>"; 
	$message .= " Se não foi você que pediu a mudança da senha, não se preocupe, ignore esta mensagem e a sua senha anterior e a chave API permanece a mesma. Se, no entanto, foi você mesmo que pediu a mudança da senha, então confirme ao carregar no seguinte endereço:</P>\n";
	$message .= "<P></P><P><A href='http://${host}/${service}'>http://${host}/${service}'</A></P><P></P>";
	$message .= "<P>Poderá depois alterar a sua senha novamente no painel de utilizador.<P>";
	$message .= "<P></P><P>Cumprimentos, </P><P></P><P>Rembrandt</P>"; 
} else if ($lang == "en") {
	$message .= "<P>Hello.</P><BR><P>Someone, probably you or an admin of the SASKIA service, requested to recover the password for the SASKIA service, associated to this email address. As such, the service generated the following temporary password: <P><P></P>";
	$message .= " <P>password: ".$newpassword."</P><P></P>"; 
	$message .= " IF it wasn't you who requested the password change, don't worry, just ignore this message and your old password and API key will work perfectly fine. Meanwhile, if it was really you who requested the password change, then confirm it by clicking on the following link:</P>\n";
	$message .= "<P></P><P><A href='http://${host}/${service}'>http://${host}/${service}'</A></P><P></P>";
	$message .= "<P>You can afterwards change the password on the REMBRANDT website.<P>";
	$message .= "<P></P><P>Cheers, </P><P></P><P>Rembrandt</P>"; 
}

$headers  = 'MIME-Version: 1.0' . "\r\n";
$headers .= 'Content-type: text/html; charset=ISO-8859-1' . "\r\n";
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
