<H2>Erros Frequentes</H2>

<P>O REMBRANDT ainda não é o que se pode chamar um programa maduro. Ainda falta algum tempo até atingir a versão estável 1.0, e até lá, há muito para melhorar. Veja se o problema que encontrou está aqui explicado. Sugestão: use um ficheiro <code>log4j.properties</code> configurado para escrever mensagens de <I>debug</I>, para depurar o funcionamento do REMBRANDT.


<a name="top"></a>
<ul>Execução do serviço de rede REMBRANDT
   <li><a href="#remote1">A caixa de texto diz "O serviço Rembrant devolveu um erro".</a></li>
</ul>
<ul>Execução local do programa REMBRANDT
<li><a href="#local1">Dá-me uma excepção <code>in thread "main" java.lang.NoClassDefFoundError</code></a></li>
	<li><a href="#local2">O REMBRANDT falha muitas EM óbvias!</a></li>
</ul>

<hr>

<H3>Execução do serviço de rede REMBRANDT</H3>

<a name="remote1"></a>
<H4>A caixa de texto diz "O serviço Rembrant devolveu um erro".</H4>
<P>Passa-se alguma coisa com o serviço, que foi abaixo. O servidor está programado para reactivar o serviço em cada hora, se não o encontrar. Tome um café e volte a tentar mais tarde.</P>
<P>
<a class="intlink" href="#top">Voltar ao topo da página</a>
<hr>

<H3>Execução local do programa REMBRANDT</H3>

<a name="local1"></a>
<H4>Dá-me uma excepção <code>in thread "main" java.lang.NoClassDefFoundError</code></H4>
<P>O Java não encontrou o REMBRANDT. Verifique se o CLASSPATH está bem configurado, ou se não escreveu um erro na linha de comandos.</P>

<P>
<a class="intlink" href="#top">Voltar ao topo da página</a>
<hr>

<a name="local2"></a>
<H4>O REMBRANDT falha muitas EM óbvias!</H4>

<P>Isso só acontece com EM que incluam caracteres não-ASCII? Experimente anotar, por exemplo, "Lisboa e Óbidos"; Se 'Óbidos' não foi anotado mas 'Lisboa' sim, isso indicia uma incompatibilidade em codificações de caracteres algures na execução do programa. Por outras palavras, o caracter 'Ó' é lido incorrectamente durante a execução do REMBRANDT, comprometendo a classificação da EM.</P>	

<P>O suspeito mais comum é o MySQL, que pode estar a devolver os resultados numa codificação inesperada. Recomendamos que reconfigure o MySQL para trabalhar em UTF-8, tanto o servidor como o cliente, e que as bases de dados sejam carregadas nessa codificação. Confirme também que o ambiente da consola / ficheiro de entrada está na codificação certa. Pode sempre ajudar o Java a compreender qual a codificação que deve usar por omissão, adicionando o parâmetro <code>-Dfile.encoding</code>.
<P>
<a class="intlink" href="#top">Voltar ao topo da página</a>
<hr>
<P>Não encontraste resposta ao seu problema? Então <a href="<?php echo curPageURL(array('do'=>'devel-issues'));?>">denuncia o erro</a>. Ajuda o REMBRANDT a ser um programa melhor.</P>