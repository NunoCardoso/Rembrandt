<H2>Perguntas já respondidas</H2>

<a name="top"></a>
<ul>Introdução
	<li><a href="#1">O que é o REMBRANDT?</a></li>
	<li><a href="#1.2">O que é a SASKIA e o RENOIR?</a></li>
	<li><a href="#1.5">Porquê esses nomes, REMBRANDT, SASKIA e RENOIR?</a></li>
</ul>
<P>
<ul>Funcionamento
	<li><a href="#2">Posso correr o REMBRANDT no meu computador?</a></li>
	<li><a href="#2.2">Posso alterar o código do REMBRANDT para o meu uso próprio?
	<li><a href="#2.5">Que tipo de entidades detecta o REMBRANDT?</a></li>
	<li><a href="#2.7">O REMBRANDT só anota textos em português?</a></li>
	<li><a href="#3">Como funciona o REMBRANDT?</a></li>
	<li><a href="#3.5">O REMBRANDT é bom? Consegue anotar TUDO?</a></li>
	<li><a href="#4">O REMBRANDT não anota as entidades como eu estava à espera!</a></li>
</ul>
<P>
	<ul>Referências
	<li><a href="#4.5">Há outros programas/serviços para o português, como o REMBRANDT?</a></li>
	<li><a href="#5">Como cito o REMBRANDT?</a></li>
</ul>
<hr>
<H3>O que é o REMBRANDT?</H3>
<a name="1"></a>
<P>O REMBRANDT é um programa que reconhece as entidades mencionadas (EM) no texto (ou seja,  nomes de entidades como pessoas, locais ou empresas), e detecta as relações que existem entre as EM. O REMBRANDT está preparado para interpretar entidades que podem ter significados diferentes, e desambigua o seu sentido sempre que é possível.</P>

<P>O REMBRANDT é um programa desenvolvido por mim, <a href="http://xldb.di.fc.ul.pt/ncardoso">Nuno Cardoso</a>, no âmbito do meu doutoramento.</P>
<P>O meu doutoramento insere-se dentro de dois projectos: 1) a <A href="http://www.linguateca.pt">Linguateca</A>, sediada no <A href="http://www.sintef.no">SINTEF</A>, e 2) o <a href="http://xldb.di.fc.ul.pt/wiki/Grease">GReaSE</A>, da <a href="http://xldb.di.fc.ul.pt">equipa do XLDB</a>, <a href="http://www.lasige.di.fc.ul.pt">laboratório LaSIGE</a> do <a href="http://www.di.fc.ul.pt">Departamento de Informática</a> da <a href="http://www.fc.ul.pt">Faculdade de Ciências da Universidade de Lisboa</a>.

<P>
<a class="intlink" href="#top">Voltar ao topo da página</a>
<hr>

<H3>O que é a SASKIA e o RENOIR?</H3>
<a name="1"></a>
<P>A <B>SASKIA</B> é um programa que facilita ao REMBRANDT o acesso aos dados brutos da <a href="http://www.wikipedia.org">Wikipédia</a> e da <a href="http://www.dbpedia.org">DBpedia</a>, para que se possa extrair conhecimento na altura de classificar as entidades. </P>
<P>O <B>RENOIR</B> é um módulo de interpretação avançada de frases, com o intuito de extrair intenções e significados mais elaborados à volta das EM, e que raciocina sobre essas frases. Por exemplo, o RENOIR pode interpretar a pergunta "<I>Qual é a capital de Portugal?</I>" e, com a ajuda do REMBRANDT e da SASKIA, obter a EM que corresponde à resposta, "Lisboa".</P>
<P>
<P><a class="intlink" href="#top">Voltar ao topo da página</a></P>
<hr>

<a name="1.5"></a>
<H3>Porquê esses nomes, REMBRANDT, SASKIA e RENOIR?</H3>

<P><B>REMBRANDT</B> é um acrónimo para <B>R</B>econhecimento de <B>E</B>ntidades <B>M</B>encionadas <B>B</B>aseado em <B>R</B>elações e <B>AN</B>álise <B>D</B>etalhada do <B>T</B>exto. Um bom acrónimo é o primeiro passo para um bom programa. Ao que parece, também foi <a href="http://pt.wikipedia.org/wiki/Rembrandt">um pintor holandês</A>...</P>
	
<P><B>SASKIA</B> é um acrónimo para <B>S</B>PARQL and <B>A</B>PI <B>S</B>ervice for <B>K</B>nowledge and <B>I</B>nformation <B>A</B>ccess. Ao que parece, há uma <a href="http://pt.wikipedia.org/wiki/Saskia_van_Uylenburgh">pessoa chamada Saskia que se casou com o pintor Rembrandt</a>. Há cada coincidência...</P>

<P><B>RENOIR</B> é um acrónimo para <B>R</B>EMBRANDT's <B>E</B>xtended <B>N</B>ER <B>O</B>n <B>I</B>nformation <B>R</B>etrieval, até arranjar um acrónimo mais elegante. Falando em coincidências, parece que também é um <a href="http://en.wikipedia.org/wiki/Renoir">pintor francês</a>...</P>
<P><a class="intlink" href="#top">Voltar ao topo da página</a></P>
<hr>

<a name="2"></a>
<H3>Posso correr o REMBRANDT no meu computador?</H3>

<P>Sim. O REMBRANDT está disponível gratuitamente a todos os interessados (por favor, <a href="<?php echo curPageURL(array('do'=>'about-tos'));?>">leia as condições de acesso</a> antes de o descarregar). O REMBRANDT pode ser <a href="<?php echo curPageURL(array('do'=>'download-release'));?>">descarregado</a> e executado em qualquer máquina, desde que tenha o <a href="http://www.java.com/getjava/">Java 1.6</a> instalado. Para tal, é necessário descarregar também <a href="<?php echo curPageURL(array('do'=>'download-dependencies'));?>">outros pacotes Java que o REMBRANDT precisa</a>, bem como ter acesso a uma base de dados.</P>
	
<P>Como fonte de dados, o REMBRANDT precisa de ter uma cópia local das bases de dados da Wikipédia na(s) língua(s) que pretende realizar a anotação. Essas bases de dados podem ser acedidas gratuitamente, e estão referenciadas <a href="<?php echo curPageURL(array('do'=>'download-db'));?>">na página de programas</a>. Também é necessário um gestor de base de dados, como o <a href="http://www.mysql.com">MySQL</a>, que é gratuito. Em resumo, pode correr o REMBRANDT no seu computador de forma gratuita. </P>

<P>As instruções de instalação estão detalhadas na <a href="<?php echo curPageURL(array('do'=>'devel-install'));?>">página de instalação do REMBRANDT</a>.</P>
<P>
<a class="intlink" href="#top">Voltar ao topo da página</a>
<hr>

<a name="2.2"></a>
<H3>Posso alterar o código do REMBRANDT para o meu uso próprio?</H3>

<P>Sim, o código fonte está incluído nos programas, sob a <a href="http://www.gnu.org/copyleft/gpl.html">licença GPL</a>.</P>
<P>
<a class="intlink" href="#top">Voltar ao topo da página</a>
<hr>

<a name="2.5"></a>
<H3>Que tipo de entidades detecta o REMBRANDT?</H3>

<P>O REMBRANDT detecta e classifica entidades de acordo com as directivas do <a href="http://www.linguateca.pt/HAREM">Segundo HAREM</a>, definidas pela <a href="http://www.linguateca.pt">Linguateca</a> em conjunto com outros investigadores na área de processamento computacional do português.</P>

<P>A classificação é feita através de uma <I>categoria</I> genérica, e uma especialização em dois níveis (<I>tipo</I> e <I>subtipo</I>). Há nove categorias principais:</P>
<ul>
<li><B>PESSOA</B> - Inclui nomes de pessoas, cargos, personagens e povos.</li>
<li><B>ORGANIZACAO</B> - Inclui empresas, instituições e outras entidades governativas.</li>
<li><B>LOCAL</B> - Inclui nomes de locais geográficos e locais virtuais (como são o exemplo de jornais, programas de televisão ou locais na internet).</li>
<li><B>TEMPO</B> - Inclui expressões temporais como datas, horas e durações.</li>
<li><B>VALOR</B> - Inclui expressões numéricas, quantidades e medidas.</li>
<li><B>OBRA</B> - Inclui trabalhos, filmes, quadros, artigos, etc.</li>
<li><B>ACONTECIMENTO</B> - Inclui eventos e efemérides relevantes</li>
<li><B>COISA</B> - Inclui entidades que referem objectos ou classes de objectos</li>
<li><B>ABSTRACCAO</B> - Inclui conceitos abstractos como movimentos intelectuais, áreas de estudo, conceitos filosóficos, etc.</li>
</ul>
<P><a class="intlink" href="#top">Voltar ao topo da página</a></P>
<hr>

<a name="2.7"></a>
<H3>O REMBRANDT só anota textos em português?</H3>
<P>O REMBRANDT está preparado para usar regras de anotação noutras línguas, e pode inclusivé anotar textos de diferentes línguas ao mesmo tempo. No entanto, já fiz experiências de anotação em textos em inglês, e reparei que as EM em textos ingleses necessitam de regras específicas para o inglês, uma vez que os resultados obtidos não são nada famosos.</P>

<P>Apesar de não ser uma prioridade, espero arranjar um pouco de tempo livre para criar regras de detecção de entidades para o inglês. Fique atento à <a href="<?php echo curPageURL(array('do'=>'devel-wishlist'));?>">página de desejos do REMBRANDT</a>, para saber quando é que planeio incluir as regras de EM inglesas. Seja como for, pode tentar a anotação dos textos em inglês, apesar de os resultados não serem tão bons como acontece com textos em português.</P> 	
<P><a class="intlink" href="#top">Voltar ao topo da página</a></P>
<hr>

<a name="3"></a>
<H3>Como funciona o REMBRANDT?</H3>

<P>O REMBRANDT aplica duas estratégias no reconhecimento de entidades: 1) uso de regras gramaticais para cada língua, nomeadamente na detecção de evidências dentro e fora da entidade, como é o exemplo da presença de "Dr." no início de nomes de pessoas. 2) extracção de informação da Wikipédia, para obter conhecimento sobre os vários significados associados a cada entidade.</P>

<P>Consulte a seccção dos <a href="<?php echo curPageURL(array('do'=>'help-doc'));?>">artigos publicados</a> para mais informações detalhadas, artigos e apresentações sobre o REMBRANDT, pode consultar a secção de documentação.</P>
<P>
<P><a class="intlink" href="#top">Voltar ao topo da página</a></P>
<hr>

<H3>O REMBRANDT é bom? Consegue anotar TUDO?</H3>
<a name="3.5"></a>
<P>O REMBRANDT não é nenhum oráculo, e também falha como qualquer programa feito por um humano. O REMBRANDT participou no <a href="http://www.linguateca.pt/HAREM">Segundo HAREM, uma avaliação específica para sistemas de reconhecimento de entidades mencionadas para português</a>, organizado pela <a href="http://www.linguateca.pt">Linguateca</a> em Abril de 2008 com a sua versão 0.7, e entre 10 sistemas, obteve o segundo lugar na tarefa geral de anotação, com um resultado de medida F de 0,567. No caso de entidades de categoria <B>LOCAL</B>, obteve o primeiro lugar, em 8 sistemas, com um valor de medida F de 0,625. Na tarefa de detecção de relações ente entidades, obteve o primeiro lugar entre três sistemas participantes.</P>

<P>Em resumo, o REMBRANDT até que não é mau de todo, mas eu gostaria que fosse ainda melhor. Por isso é que há uma secção para <a href="<?php echo curPageURL(array('do'=>'devel-issues'));?>">denunciar erros de execução e de anotação</a>, para que possa melhorar o seu sistema de regras e contemplar outros casos que ainda não tinha visto.</P>
<P><a class="intlink" href="#top">Voltar ao topo da página</a></P>
<hr>

<H3>O REMBRANDT não anota as entidades como eu estava à espera!</H3>
<a name="4"></a>
<P>Isto pode acontecer por várias razões, mas em primeiro lugar, note o seguinte: <B>o REMBRANDT anota as entidades em contexto</b>, isto é, procura atribuir o significado que a entidade possui na frase respectiva, e não o significado mais comum que essa entidade tem.<P> 
<P>Ou seja, anotar 'Portugal' sempre como um país não é o objectivo do REMBRANDT; A expressão 'Portugal' pode ter outros papéis dependendo do contexto, como é o caso de um grupo de pessoas (no caso de uma equipe desportiva), ou uma organização (no caso de uma decisão governamental). Não será isso que está a acontecer?</P>

<P><a class="intlink" href="#top">Voltar ao topo da página</a></P>
<hr>

<H3>Há outros programas/serviços para o português, como o REMBRANDT?</H3>
<a name="4.5"></a>
<P>Sim. Muitos deles participaram no <a href="http://www.linguateca.pt/HAREM/">HAREM, uma avaliação dedicada a sistemas de reconhecimento de entidades mencionadas para o português</a>, que comparou o seu desempenho na anotação de uma colecção comum de textos. Aqui está a lista, por ordem alfabética:</P>
<ul>
<li>O <B>CaGE</B>, desenvolvido por Bruno Martins, é um sistema REM focado no reconhecimento de locais. Pode obter mais detalhes sobre a primeira versão do CaGE no <a href="http://www.linguateca.pt/aval_conjunta/LivroHAREM/">capítulo 8 do livro do Primeiro HAREM</a>, e sobre a segunda versão do CaGE no <a href="http://www.linguateca.pt/HAREM/actas/LivroSegundoHAREM.html">capítulo 7 do livro do Segundo HAREM</a></LI>
<li>O <B>Cortex</B>, desenvolvido por Christian Nunes Aranha. Pode obter mais detalhes sobre o Cortex no <a href="http://www.linguateca.pt/aval_conjunta/LivroHAREM/">capítulo 9 do livro do Primeiro HAREM</a>.</li>
<li>O <B><a href="http://lxren.di.fc.ul.pt/">LX-NER</a></B>, desenvolvido pelo <a href="http://nlx.di.fc.ul.pt/">NLX-Group</a> da Faculdade de Ciências da Universidade de Lisboa, e que possui um <a href="http://lxren.di.fc.ul.pt/">serviço web de anotação de textos</a>.
<li>O <B>Malinche</B>, sistema de identificação de entidades (sem classificação) desenvolvido por Thamar Solorio. Pode obter mais detalhes sobre o Malinche no <a href="http://www.linguateca.pt/aval_conjunta/LivroHAREM/">capítulo 10 do livro do Primeiro HAREM</a>.</li>
<li>O <B>NERUA</B>, um sistema REM espanhol adaptado para o português e desenvolvido por Óscar Ferrández, Zornitsa Kozareva, Antonio Toral, Rafael Muñoz e Andrés Montoyo. Pode obter mais detalhes sobre o NERUA no <a href="http://www.linguateca.pt/aval_conjunta/LivroHAREM/">capítulo 12 do livro do Primeiro HAREM</a>.</li>
<li>O <B>Palavras_NER</B>, desenvolvido por <a href="http://visl.sdu.dk/visl/about/eckhard.html">Eckhard Bick</a>. Pode obter mais detalhes sobre o Palavras_NER no <a href="http://www.linguateca.pt/aval_conjunta/LivroHAREM/">capítulo 9 do livro do Primeiro HAREM</a>.</li>
<li>O sistema REM da <B><a href="http://www.priberam.pt">Priberam</a></B>, desenvolvido por Carlos Amaral, Helena Figueira, Afonso Mendes, Pedro Mendes, Cláudia Pinto e Tiago Veiga. Pode obter mais detalhes sobre este sistema no <a href="http://www.linguateca.pt/HAREM/actas/LivroSegundoHAREM.html">capítulo 9 do livro do Segundo HAREM</a>.</li> 
<li>O <B>PorTexTO</B>, desenvolvido por Olga Craveiro, Joaquim Macedo e Henrique Madeira, dedicado a expressões temporais. Pode obter mais detalhes sobre o PorTexTO no <a href="http://www.linguateca.pt/HAREM/actas/LivroSegundoHAREM.html">capítulo 8 do livro do Segundo HAREM</a>.</li>
<li>O <B>REMMA</B>, desenvolvido por Liliana Ferreira, António Teixeira e João Paulo da Silva Cunha. Pode obter mais detalhes sobre o REMMA no <a href="http://www.linguateca.pt/HAREM/actas/LivroSegundoHAREM.html">capítulo 12 do livro do Segundo HAREM</a>.</li>
<li>O <B>RENA</B>, desenvolvido por João José de Almeida. Pode obter mais detalhes sobre o RENA no <a href="http://www.linguateca.pt/aval_conjunta/LivroHAREM/">capítulo 13 do livro do Primeiro HAREM</a>.</li>
<li>O <B><a href="http://poloclup.linguateca.pt/siemes/">SIEMÊS</a></B>, desenvolvido por Luís Sarmento. Pode obter mais detalhes sobre o SIEMÊS no <a href="http://www.linguateca.pt/aval_conjunta/LivroHAREM/">capítulo 14 do livro do Primeiro HAREM</a>.</LI>
<li>O <B>SEI-Geo</B>, um sistema dedicado à extracção de locais  desenvolvido por Marcírio Chaves. Pode obter mais detalhes sobre o SEI-Geo no <a href="http://www.linguateca.pt/HAREM/actas/LivroSegundoHAREM.html">capítulo 13 do livro do Segundo HAREM</a>.</LI>	
<li>O <B>SeRELeP</B>, desenvolvido por Mírian Bruckschen, José Guilherme Camargo de Souza, Renata Vieira e Sandro Rigo. Pode obter mais detalhes sobre o SeRELeP no <a href="http://www.linguateca.pt/HAREM/actas/LivroSegundoHAREM.html">capítulo 14 do livro do Segundo HAREM</a>.</LI>	
<li>O <B><a href="http://label.ist.utl.pt/pt/smell_intr_pt.php">SMELL</a></B>, desenvolvido pela equipa do <a href="http://label.ist.utl.pt">LabEL</a>. O SMELL possui um <a href="http://label.ist.utl.pt/pt/smell_online_pt.php">serviço web de anotação de texto</a>.
<li>O <B>Stencil/NooJ</B>, desenvolvido por Cristina Mota e Max Silbertzein. Pode obter mais detalhes sobre o Stencil/NooJ no <a href="http://www.linguateca.pt/aval_conjunta/LivroHAREM/">capítulo 15 do livro do Primeiro HAREM</a>, e sobre o sistema R3M desenvolvido por Cristina Mota para o Segundo HAREM no <a href="http://www.linguateca.pt/HAREM/actas/LivroSegundoHAREM.html">capítulo 8 do livro do Segundo HAREM</a>.</li>
<li>O <B>XIP</B>, desenvolvido por Caroline Hagège, Jorge Baptista e Nuno Mamede, numa colaboração entre o INESC-L2f e a Xerox. Pode obter mais detalhes sobre o XIP no <a href="http://www.linguateca.pt/HAREM/actas/LivroSegundoHAREM.html">capítulo 15 do livro do Segundo HAREM</a>.</LI>	

<P>O REMBRANDT é inquestionavelmente o sistema REM que apresenta o melhor desempenho dentro do grupo de sistemas REM dedicados ao português cujo nome pode também designar pintores holandeses.</P>

</ul>
</P>

<P><a class="intlink" href="#top">Voltar ao topo da página</a></P>
<hr>


<H3>Como cito o REMBRANDT?</H3>
<a name="5"></a>
<P>Por favor, cite o REMBRANDT com a seguinte referência:</P>

<P>Nuno Cardoso, <I>REMBRANDT - Reconhecimento de Entidades Mencionadas Baseado em Relações e ANálise Detalhada do Texto</I>. In Cristina Mota &amp; Diana Santos (eds.). Desafios na avaliação conjunta do reconhecimento de entidades mencionadas: O Segundo HAREM. Linguateca. 2008.</P>
<P><a class="intlink" href="#top">Voltar ao topo da página</a></P>