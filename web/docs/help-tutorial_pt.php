<H2>Tutorial</H2>

<P>Escreva um texto em português na caixa de texto abaixo, e execute o REMBRANDT sobre esse texto ao carregar no botão. Pode deixar ficar o exemplo, se se sentir preguiçoso hoje.</P>
<P><B>Nota:</B> Esta página funciona perfeitamente no Firefox 3, Safari 4 e Opera 9+. No entanto, o IE parece que não gosta. Se calhar, está na altura de finalmente mudar de navegador...</P> 

	<?php include('inc/rembrandt-form.php'); ?>
 	<?php include('inc/rembrandt-div.php'); ?> 	


<DIV class="rembrandt-tutorial" style="clear:both;margin-top:30px;">
<P>Se o serviço REMBRANDT ainda estiver a correr, os resultados irão ser apresentados na caixa acima.<P>

<P>As EM reconhecidas pelo REMBRANDT são anotadas com etiquetas <B>&lt;EM&gt;</B>, que possuem alguns atributos:</P>
<P><B>C1</B> - Categoria da EM, de acordo com a <a href="http://www.linguateca.pt/aval_conjunta/HAREM/tabela.html">classificação semântica</a> do <a href="http://www.linguateca.pt/HAREM">Segundo HAREM</a>.<BR>
<B>C2</B> - Tipo da EM (opcional), de acordo com a <a href="http://www.linguateca.pt/aval_conjunta/HAREM/tabela.html">classificação semântica</a> do <a href="http://www.linguateca.pt/HAREM">Segundo HAREM</a>.<BR>
<B>C3</B> - Subtipo da EM (opcional, e só para certas categorias), de acordo com a <a href="http://www.linguateca.pt/aval_conjunta/HAREM/tabela.html">classificação semântica</a> do <a href="http://www.linguateca.pt/HAREM">Segundo HAREM</a>.
<P><B>S e T</B> - Número da frase e do termo.
<P><B>WK e DB</B> - Identificadores: WK representa os ids das entidades na base de dados da Wikipédia (tabela page), e DB representa a entidade na DBpedia.
</P>

<P>Note que, quando o REMBRANDT não consegue decidir entre um grupo de categories e/ou tipos, opta por mostrar todas, separadas com um símbolo |. Não é fácil decidir só por uma única categoria; na frase 'Ajude os Bombeiros', 'Bombeiros' serão uma organização ou um grupo de pessoas? Não é assim tão claro... e se não é para nós, também não é para o REMBRANDT.</P>

<P>Se o REMBRANDT for configurado para tal, pode também mostrar anotações alternativas para uma mesma expressão. Ou seja, considere a EM 'Universidade de Lisboa'; o que deve ser anotado, a instituição 'Universidade de Lisboa' ou o local 'Lisboa'? Nestes casos, o REMBRANDT usa etiquetas <B>&lt;ALT&gt;</B> para representar as duas alternativas.</P>
</DIV>