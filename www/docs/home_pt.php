<DIV CLASS="main-right-note" style="font-size:1.2em;">
O REMBRANDT 1.3beta1 já está disponível. <a href="<?php echo curPageURL(array('do'=>'download-release'));?>">Descarregue e experimente já!</a>  
</DIV>

<H2>Benvindo ao sítio do REMBRANDT.</h2> 

<P>O REMBRANDT é uma plataforma de "semantificação" de texto que permite trabalhar sobre as entidades reconhecidas nos documentos, como é o caso de pesquisa avançada ou a extracção de informação. Rembrandt significa <strong>R</strong>econhecimento de <strong>E</strong>ntidades <strong>M</strong>encionadas <strong>B</strong>aseado em <strong>R</strong>elações e <strong>AN</strong>álise <strong>D</strong>etalhada do <strong>T</strong>exto. </P>

<P>O REMBRANDT pode ser usado para: </P>

<DIV CLASS="main-center-note" >
<div CLASS="main-left-description">
<B>1. Pesquisa de documentos</B> - Coloque questões simples e adicione entidades na sua consulta, para ver documentos que respondam o que pretende. Os resultados serão exibidos numa página nova.
</div>
<div style="display:inline-block; clear:left;">
<?php include("inc/renoir_search_complete.php"); ?>
</div>
</DIV>

<DIV CLASS="main-center-note" >
<div CLASS="main-left-description">
<B>2. Anotação de documentos</B> - Coloque um texto qualquer na caixa abaixo, e carregue no botão. As entidades presentes no texto serão anotadas e referenciadas, e poderá ver as relações existentes entre elas. 
</div>

<!-- position relative positions the waiting div. Margin is for getting space --> 	
<DIV style="display:inline-block;">

	<FORM></FORM><!-- must stay -->
	<DIV ID="rrs-waiting-div" CLASS="rrs-waiting-div" style="display:none;">
		<DIV CLASS="rrs-waiting-div-message"></DIV>
		<DIV CLASS="rrs-waiting-div-balloontip"></DIV>
	</DIV>

	<DIV CLASS="rrs-logo" style="float:left;">
		<IMG SRC="<?php echo $config->imgdir."/".$config->rembrandt_head_image;?>">
	</DIV>
	<FORM id="home1" TARGET="rembrandt-results-1" CLASS="rembrandt-submit" style="display:inline-block;">
		<TEXTAREA id="text" name="text" style="width:400px; height:60px;"><?php echo $config->exampleText[$lang]; ?></textarea>
		<BR>
    	<input type="hidden" id="language" name="language" value="<?php echo $lang; ?>" />
    	<input type="hidden" id="type" name="type" value="simpleText" />
		<DIV><?php echo $i18n->message['textisin'][$lang]; ?> 
			<select size=1 id="submissionLang" name="submissionLang">
				<option value="pt" default>Português</option>
				<option value="en">English</option>
			</select>.
		</DIV>
		<div style="display:inline-block;">
			<A CLASS="main-button" id="rembrandt-submit-button" HREF="#">
				<SPAN><?php echo $i18n->buttons['execRembrandt'][$lang]; ?></SPAN>
			</A>  
		</div>
   </FORM>	

   <DIV ID="rembrandt-results-1" CLASS="rrs-doc-display" style="width:auto;"></DIV>
   <script> $(document).ready(function() {			
		appendDocDisplayTo($('#rembrandt-results-1'))
  })
  </script>		
</DIV>

<P>Gostou? Muito bem! Leia também as <a href="<?php echo curPageURL(array('do'=>'help-faq'));?>">perguntas já respondidas</a> para saber o que é o REMBRANDT, e <a href="<?php echo curPageURL(array('do'=>'help-tutorial'));?>">faça o curso rápido</A> sobre este serviço, correndo exemplos e familiarizando-se com o resultados gerados. Se quiser já mais detalhes, pode dar uma vista de olhos à <a href="<?php echo curPageURL(array('do'=>'help-doc'));?>">documentação do REMBRANDT</a>, que inclui artigos e apresentações sobre o programa. </P>

<P><B>Quer usar o REMBRANDT no seu computador?</B> Óptimo! Pode <a href="<?php echo curPageURL(array('do'=>'download-releases'));?>">descarregar a última versão do REMBRANDT</a> e correr o programa pela linha de comandos. A instalação e configuração necessita de um pouco de trabalho, mas em contrapartida poderá correr o REMBRANDT de uma forma mais intensa e automática. Ah, e o REMBRANDT é gratuito, e possui código-fonte livre!</P>	

<P><B>Tem comentários sobre o REMBRANDT?</A></B> Encontrou um erro de execução? Ainda bem, porque um programa sem erros é como um jardim sem flores. <a href="mailto:ncardoso@xldb.di.fc.ul.pt?subject=Nuno,%20o%20REMBRANDT%20fez%20asneira!">Envie-me uma mensagem</a> e reporte o sucedido. Se quiser, pode enviar <a href="mailto:ncardoso@xldb.di.fc.ul.pt?subject=Nuno,%20o%20REMBRANDT%20é%20um%20espectáculo!">mensagens de felicitações</a> (o autor é humano, e também gosta desse tipo de mensagens).</P>  

<P><B>Ficou fã do REMBRANDT?</A></B> Excelente! Então mantenha-se ao corrente das novidades e <a href="<?php echo curPageURL(array('do'=>'news-blog'));?>">veja as notícias</a> ou <a href="<?php echo $config->feedrss[$lang];?>">subscreva o canal RSS</a>. Para os twitterodependentes, também há <a href="<?php echo $config->twitterURL; ?>">uma conta do REMBRANDT no Twitter</A>. Se tiver vontade de melhorar o REMBRANDT, pode fazê-lo sem entraves - o código fonte está disponível de acordo com a licença GPL - ou então, se se sentir com pouca vontade de o fazer, pode <a href="<?php echo curPageURL(array('do'=>'devel-wishlist'));?>">adicionar uma mensagem ao quadro de desejos do REMBRANDT</A>, e eu verei o que posso fazer.</P>
	<hr>
<B>Aviso:</B> O serviço pode ter uma resposta lenta porque o serviço não funciona num servidor dedicado. A interface encontra-se em desenvolvimento, e algumas funcionalidades podem estar limitadas e/ou com erros de execução. Recomenda-se o uso das versões recentes do Firefox, Opera, Safari e Chrome, já que no Internet Explorer os resultados não se vêem bem porque é um navegador que não cumpre as recomendações W3C para HTML e CSS.

