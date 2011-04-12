<DIV class="body">
	<H2>Denuncie um erro</H2>

<P>Encontraste um erro de execução do REMBRANDT? Já confirmaste que o erro <a href="<?php echo curPageURL(array('do'=>'troubleshoot'));?>">não está referido na lista de erros frequentes?</a> Muito bem vamos então denunciar o erro.</P> 

<hr>

<H3>1. Se preferir enviar um mail...</H3>
	
<P>Por favor, inclua a seguinte informação no relatório do erro:</P>
<P><B>I. Ambiente de execução</B>: Qual o sistema operativo? Qual a versão dos programas Java, Groovy, REMBRANDT, SASKIA e RENOIR? Quais as variáveis de ambiente usadas (CLASSPATH) e parâmetros de configuração do REMBRANDT? Qual a codificação usada na linha de comandos na altura da execução do programa?</P>
<P><B>II. Pilha de erros</B>: Se o programa REMBRANDT parou inesperadamente com um erro, devolva um excerto da pilha de erros que inclua referências a classes dos pacotes <code>rembrandt</code>, <code>saskia</code> ou <code>renoir</code>. </P>

<P><B>III. Textos de entrada e saída</B>: Envie um excerto do texto que causa o erro, para que o possa reproduzir. Por favor, envie-o na codificação original (e refira qual é a codificação), para facilitar a depuração.</P>

<P><B>IV. Relatórios de depuração</B>: Use um ficheiro <code>log4j.properties</code> configurado em modo <i>debug</i>, e inclua-o no relatório do erro. Muitos erros só serão detectados ao analisar o funcionamento de um pequeno módulo em particular.</P>

<P><B>V. Descrição do tipo de erro</B>: O que correu mal? Estava à espera de ver certas entidades anotadas? A classificação não foi a que desejava? Diga o que lhe vai na alma.</P>

<P><B>VI. Dados pessoais</B>: Pelo menos o teu nome e email de contacto, para que eu possa depois dizer-te quando o erro está corrigido, ou para tirar algumas dúvidas do(s) relatório(s) enviado(s).</P>

<P><a href="mailto:ncardoso@xldb.di.fc.ul.pt?subject=Nuno,%20o%20REMBRANDT%20fez%20asneira!">Envie-me então um relatório desse erro irritante</A>, e eu verei o que posso fazer. 

<hr>

<H3>2. Se preferir preencher um formulário...</H3>
	
<?php
// note-se que já há uma variável "do" em get... há que substituí-la.
?>
<FORM method="POST" action="<?php echo curPageURL(array('do'=>'errorform'));?>">
<P>
<TEXTAREA name="errorreport" style="width:95%; height:300px;">
<?php
 if ($_POST['errorreport']) {echo $_POST['errorreport'];}
 else {echo <<<OUTPUT
I. Ambiente de execução:
 
  a. Sistema Operativo? Linux Fedora 9, Windows XP, MacOS X 10.5
  b. Versão Java: Java 1.6.0_11
  c. Versão Groovy: Groovy 1.7.0
  d. Variável CLASSPATH: 
  e. Parâmetros REMBRANDT: -Drembrandt.input.encoding=UTF-8
  f. Codificação (encoding) usada: ISO-8859-1, UTF-8

II. Pilha de erros:
   Exception in thread "main" java.lang.NoClassDefFoundError: rembrandt/bin/ExemploDePilhaDeErro

III. Excertos de texto:
  a. Texto de entrada: "O Rembrandt é um pintor holandês"
  b. Texto de saída: "O <EM CATEG="PESSOA" TIPO="INDIVIDUAL">Rembrandt</EM> é um pintor holandês"

IV. Relatório de depuração do log4j: Coloque aqui imformação detalhada do funcionamento dos módulos críticos para o tipo de erro que encontrou. 

V. Descrição do erro: Preencha aqui as suas impressões sobre o que está mal. 

VI: Dados pessoais: Rembrandt Harmenszoon van Rijn, rembrandt@gmail.com
OUTPUT;

}
?>
</textarea>
</P>

<P>Agora, para verificar se você é realmente humano... hoje é <?php echo prettyPrintDate(time()); ?>. Diga qual é a soma do dia mais o número do mês:
	<input type="text" name="captcha" size=4> e 
	<input type="submit" value="<?php echo $buttons['sendErrorForm'][$lang]; ?>">. Obrigado.
</FORM>	

<hr>
<P>
</DIV>