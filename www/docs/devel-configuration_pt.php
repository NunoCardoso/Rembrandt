
<H2>Configurar o REMBRANDT</H2>

<H3>1. Hierarquia de variáveis de configuração</H3>
<blockquote>
	
<P>O REMBRANDT pode trabalhar sem a necessidade de especificar parâmetros de configuração, pois usa valores por omissão. No entanto, mais cedo ou mais tarde irá querer alterar um pouco o funcionamento do REMBRANDT, ou adaptá-lo às especificações de uma máquina. Isso pode ser feito da seguinte forma: </P>
<ol>
<li>Escrevendo um ficheiro de configuração <code>rembrandt.properties</code> e coloque-o na directoria corrente. O ficheiro de configuração deve ser XML válido, com uma etiqueta de raíz &lt;configuration&gt;, seguida de uma ou mais etiquetas &lt;property&gt;. A etiqueta &lt;property&gt; deve conter uma etiqueta &lt;name&gt; e uma etiqueta &lt;value&gt;, além de uma etiqueta opcional &lt;description&gt;.</li>
<li>Especificando um ficheiro de configuração como argumento da linha de comandos (ou seja, <code>java rembrandt.bin.Rembrandt --conf=conf-sample.xml</code>.</li>
<li>Definindo variáveis de ambiente Java (<code>-D</code>).</li>
</ol>

<P>Os parâmetros serão lidos nessa ordem, substituindo os valores anteriores se forem especificados mais do que uma vez. Assim sendo, as variáveis  de ambiente Java têm prioridade sobre as variáveis do ficheiro de configuração passado no argumento, e este tem prioridade sobre o ficheiro <code>rembrandt.properties</code> .</P>
</blockquote>

<H3>2. Entradas e saídas do REMBRANDT</H3>
<blockquote>
<P>Por omissão, o REMBRANDT usa o <code>STDIN</code>, o <code>STDOUT</code> e o  <code>STDERR</code> na codificação definida pelo parâmetro Java <code>file.encoding</code>. Caso se pretenda usar ficheiros para carregar e/ou escrever dados, defina as variáveis <code>rembrandt.${stream}.file</code> (${stream} pode tomar os valores <code>input</code>, <code>output</code> e <code>err</code>), como ilustra o seguinte exemplo:</P>

<blockquote>
<P><code>
java -Drembrandt.input.file=file_input.txt 
-Drembrandt.output.file=file_output.txt rembrandt.bin.Rembrandt
</code></P>
</blockquote>

<P>O <code>STDERR</code> pode ser usado para gerar estatísticas adicionais. Por omissão, o <code>STDERR</code> está activo (para desactivar, use <code>rembrandt.err.enabled=false</code>) e gera informação no ficheiro <code>rembrandt.err.log</code>no mais detalhada das mutações que cada EM sofreu até o seu estado final. O err pode ser reconfigurado como no seguinte exemplo:</P> 

<blockquote>
<P><code>
echo "Rembrandt" | java -Drembrandt.err.file=file3.err 
-Drembrandt.err.writer=rembrandt.io.HTMLDocumentWriter -Drembrandt.err.styletag=rembrandt.io.HTMLStyleTag rembrandt.bin.Rembrandt
</code></P>
</blockquote>

<P>O <code>file3.err</code> irá ser usado para escrever uma versão HTML do documento anotado. Note a presença do parâmetro <code>rembrandt.err.writer</code>; os parâmetros <code>rembrandt.${stream}.reader</code> e <code>rembrandt.${stream}.writer</code> definem o formato dos ficheiros de entrada e de saída do REMBRANDT, que podem ir desde simples texto (<code>rembrandt.io.UnformattedReader</code> e <code>rembrandt.io.UnformattedWriter</code>) até formatos que agregam vários documentos HTML, objectos serializados em XML, e o formato REMBRANDT. Os valores devem ser classes válidas que <code>extendam rembrandt.io.Reader</code> e <code>rembrandt.io.Writer</code>.</P>
	
<P>O estilo das etiquetas das EM, por sua vez, são configuradas pelo parâmetro <code>rembrandt.output.styletag</code>, que pode tomar o valor do nome de uma classe que extenda <code>rembrandt.io.StyleTag</code> (por omissão, usa-se o RembrandtStyleTag). Outras configurações do estilo inclui:
	<ul> 
<li><code>rembrandt.output.tagstyle.lang</code>, para definir a língua das etiquetas e classificações</LI>
<li><code>rembrandt.output.tagstyle.verbose</code>, que define os elementos a colocar na etiqueta. Os valores são:
	<uL>
		<li>0 - apenas a classificação</LI> 
		<li>1 - acima, mais um identificador, número de frase e de termo</li>
		<li>2 - acima, mais a referenciação pela Wikipédia / DBpedia</li>
	   <li>3 - acima, mais historial de mutação da EM</li>
	</UL>
</li>
</UL>
</P>
</blockquote>
<H3>3. Configuração do motor do REMBRANDT</H3>
<blockquote>
<P>O parâmetro <code>rembrandt.core.doEntityRelation</code>, que pode ser <I>true</I> ou <I>false</I> (por omissão, está desligado), define se, após o reconhecimento de EM, irá tentar repescar EM através da detecção de relações entre entidades.</P>
<ul>
<li><B>Vantagens:</B> Aumenta o número de EM classificadas.</li> 	  
<li><B>Desvantagens:</B>Não está optimizado, e pode demorar bastante tempo a processar em documentos longos.</li> 	  
</ul>

<P>O parâmetro <code>rembrandt.core.doALT</code>, que pode ser <I>true</I> ou <I>false</I> (por omissão, está ligado), define se o REMBRANDT pode gerar anotações alternativas para o mesmo excerto de texto. </P>
<ul>
<li><B>Vantagens:</B> Gera mais EM que representam melhor a expressão. Por exemplo, '<I>Universidade de Lisboa</I>' fica anotada como '<I>Universidade  de Lisboa</I>' e '<I>Lisboa</I>' ao mesmo tempo.</li>
<li><B>Desvantagens:</B> A etiqueta usada, &lt;ALT&gt;, repete o texto para apresentar as alternativas, e como tal, dificulta o seu pós-processamento.</li>
</ul>
	
<P>O parâmetro <code>rembrandt.core.removeRemainingUnknownNE</code>, que pode ser <I>true</I> ou <I>false</I> (por omissão, está ligado), decide o que fazer com as EM que, no final, não apresentem classificação semântica (ou seja, o seu significado é desconhecido). Por omissão, essas EM são eliminadas. 
</blockquote>
<H3>4. Configuração da SASKIA no acesso à base de dados</H3>
<blockquote>
<P>Para a ligação à base de dados, o módulo Saskia usa os seguintes parâmetros:</P>

<P><code>saskia.wikipedia.db.name</code> - Especifica o nome da base de dados (por omissão, 'saskia').</P>
<P><code>saskia.wikipedia.db.url</code> - Especifica o URL de ligação da base de dados, permitindo ligar a base de dados remotas. Por omissão, o valor é <code>jdbc:mysql://127.0.0.1</code>.</P>
<P><code>saskia.wikipedia.db.user</code> - Especifica o utilizador da base de dados (por omissão, 'saskia').</P>
<P><code>saskia.wikipedia.db.password</code> - Especifica a senha para acesso à base de dados (por omissão, 'saskia').</P>
<P><code>saskia.wikipedia.db.params</code> - Especifica parâmetros adicionais para o conector. Por omissão, os parâmetros <code>useUnicode=true&amp;encodingCharset=UTF-8&amp;autoReconnect=true</code> forçam o MySQL connector/J a usar UTF-8 nas transacções com o MySQL.</P>
<P><code>saskia.wikipedia.table.${name}</code> - Especifica o nome das tabelas $name (page, category, categorylinks, pagelinks e redirect).</P>
</blockquote>