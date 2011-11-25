<H2>Base de dados</H2>
<P>O <a href="http://www.mysql.com">MySQL</a> é o servidor de base de dados recomendado (versão 5.1.32 ou superior), não só por ter sido usado no desenvolvimento do REMBRANDT, mas também é o servidor usado pela Wikipédia, e como tal, o carregamento das bases de dados da Wikipédia faz-se de uma forma mais fácil. No entanto, o REMBRANDT deve funcionar com outros servidores, desde que consigam carregar as imagens da Wikipédia, e que seja usado o respectivo conector Java.</P>
 
<H3>Fontes de dados da Wikipédia</H3>

<P>A <a href="http://www.wikipedia.org">Wikipédia</A> lança periodicamente instantâneos das suas páginas, em formato HTML e em formato Wiki, e das suas bases de dados, e qualquer pessoa pode descarregar esses ficheiros de forma livre e gratuita. Da <a href="http://download.wikipedia.org/ptwiki/">versão mais recente da Wikipédia em português</a>, o REMBRANDT só precisa dos seguintes ficheiros:</P>
<ul>Wikipédia portuguesa:
<li><a href="http://download.wikipedia.org/ptwiki/latest/ptwiki-latest-category.sql.gz">ptwiki-latest-category.sql.gz</a></li>
<li><a href="http://download.wikipedia.org/ptwiki/latest/ptwiki-latest-categorylinks.sql.gz">ptwiki-latest-categorylinks.sql.gz</a></li>
<li><a href="http://download.wikipedia.org/ptwiki/latest/ptwiki-latest-page.sql.gz">ptwiki-latest-page.sql.gz</a></li>
<li><a href="http://download.wikipedia.org/ptwiki/latest/ptwiki-latest-redirect.sql.gz">ptwiki-latest-redirect.sql.gz</a></li>	
<li><a href="http://download.wikipedia.org/ptwiki/latest/ptwiki-latest-pagelinks.sql.gz">ptwiki-latest-pagelinks.sql.gz</a></li>	
</UL>
<BR>
<ul>Wikipédia inglesa:
<li><a href="http://download.wikipedia.org/enwiki/latest/enwiki-latest-category.sql.gz">enwiki-latest-category.sql.gz</a></li>
<li><a href="http://download.wikipedia.org/enwiki/latest/enwiki-latest-categorylinks.sql.gz">enwiki-latest-categorylinks.sql.gz</a></li>
<li><a href="http://download.wikipedia.org/enwiki/latest/enwiki-latest-page.sql.gz">enwiki-latest-page.sql.gz</a></li>
<li><a href="http://download.wikipedia.org/enwiki/latest/enwiki-latest-redirect.sql.gz">enwiki-latest-redirect.sql.gz</a></li>	
<li><a href="http://download.wikipedia.org/enwiki/latest/enwiki-latest-pagelinks.sql.gz">enwiki-latest-pagelinks.sql.gz</a></li>	
</UL>
