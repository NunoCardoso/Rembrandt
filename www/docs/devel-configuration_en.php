<H2>Configuring REMBRANDT</H2>

<H3>1. Configuration variables hierarchy</H3>
<blockquote>
<P>REMBRANDT can run with default configuration values. Nonetheless, you'll sooner or later want to tweak REMBRANDT's configuration, to adapt it to the text and/or the machine used. This can be made through several ways: </P>
<ol>
<li>Creating a configuration file <code>rembrandt.properties</code>. The file must be valid XML, with a &lt;configuration&gt; root tag, followed by one or more &lt;property&gt; tags. A &lt;property&gt; tag must contain &lt;name&gt; and &lt;value&gt; tags, with an optional  &lt;description&gt; tag.</li>
<li>Refer a configuration file as an argument on a command line, (for example, <code>java rembrandt.bin.Rembrandt --conf=conf-sample.xml</code>.</li>
<li>set Java environment variables (<code>-D</code>).</li>
</ol>

<P>The configuration parameters are read on this order, being overwritten when specified more than one time. Thus, the Java environment variables have priority over the variables set in the configuration file from the command line, and this one has priority over the <code>rembrandt.properties</code> file.</P>
</blockquote>

<H3>2. In and Outs of REMBRANDT</H3>
<blockquote>
<P>By default, REMBRANDT uses <code>STDIN</code>, <code>STDOUT</code> and <code>STDERR</code> on the encoding defined in the Java's <code>file.encoding</code> parameter. In case you want to use files to load/write text, you can set the <code>rembrandt.${stream}.file</code> parameters (${stream} can take the values <code>input</code>, <code>output</code> and <code>err</code>), as given in the following examples:</P>

<blockquote>
<P><code>
java -Drembrandt.input.file=file_input.txt 
-Drembrandt.output.file=file_output.txt rembrandt.bin.Rembrandt
</code></P>
</blockquote>

<P><code>STDERR</code> can be used to output additional information. By default, <code>STDERR</code> is enabled (to disable it, use <code>rembrandt.err.enabled=false</code>) and outputs verbose information to the  <code>rembrandt.err.log</code> file, about the mutations ocurred to NEs until their final state. We can reconfigure STDEER, as in:</P> 

<blockquote>
<P><code>
echo "Rembrandt" | java -Drembrandt.err.file=file3.err 
-Drembrandt.err.writer=rembrandt.io.HTMLDocumentWriter -Drembrandt.err.styletag=rembrandt.io.HTMLStyleTag rembrandt.bin.Rembrandt
</code></P>
</blockquote>

<P>Now, <code>file3.err</code> will be used to write a HTML version of the tagged documents. Note the parameter <code>rembrandt.err.writer</code>; the <code>rembrandt.${stream}.reader</code> and <code>rembrandt.${stream}.writer</code> parameters set the file format while reading and writing. We can use simple formats  (<code>rembrandt.io.UnformattedReader</code> and <code>rembrandt.io.UnformattedWriter</code>) or HTML-ish formats, XML serialized objects or the default REMBRANDT format. THe parameter values must be valid classes that extend <code>extendam rembrandt.io.Reader</code> and <code>rembrandt.io.Writer</code>.</P>
	
<P>The NE tag style, on the other way, are confiugured by the  <code>rembrandt.output.styletag</code> parameter, that can take a class name that extends <code>rembrandt.io.StyleTag</code> (RembrandtStyleTag, by default). Other tag style configuration include:
	<ul> 
<li><code>rembrandt.output.tagstyle.lang</code>, to set tag language for classifications</LI>
<li><code>rembrandt.output.tagstyle.verbose</code>, define the verbosity of the tag parameters:
	<uL>
		<li>0 - just classification</LI> 
		<li>1 - plus a id, sentence number and term number</li>
		<li>2 - plus grounding information from Wikipedia / DBpedia</li>
	   <li>3 - plus a NE mutation history</li>
	</UL>
</li>
</UL>
</P>
</blockquote>
<H3>3. Configuring REMBRANDT's core</H3>
<blockquote>
<P>The <code>rembrandt.core.doEntityRelation</code> parameter, which can be <I>true</I> or <I>false</I> (default: false), sets if, after all entity recognition, it will recover unclassified NEs through entity relation detection.</P>
<ul>
<li><B>Advantages:</B> Increases the amount of properly classified NEs.</li> 	  
<li><B>Disadvantages:</B>Not optimized, and it can take a considerable amount of time for longer documents.</li> 	  
</ul>

<P>The <code>rembrandt.core.doALT</code> parameter, which can be <I>true</I> or <I>false</I> (default: true), specifies if REMBRANDT can generate alternative annotations for the same text excerpt.</P>
<ul>
<li><B>Advantages:</B> Generates more NEs that are more complete regarding the text excerpt. For instance, '<I>University of Lisbon</I>' becomes tagged as '<I>University of Lisbon</I>' and '<I>Lisbon</I>' at the same time.</li>
<li><B>Desvantagens:</B> A etiqueta usada, &lt;ALT&gt;, repete o texto para apresentar as alternativas, e como tal, dificulta o seu pós-processamento.</li>
</ul>
	
<P>The <code>rembrandt.core.removeRemainingUnknownNE</code> parameter, which can be <I>true</I> or <I>false</I> (default: true), decides on what to do with the remaining NEs that have no semantic classification (that is, with unknown meaning). By default, these NEs are deleted from the output. 
</blockquote>
<H3>4. Configuring SASKIA on database access</H3>
<blockquote>
<P>To connect to the database, SASKIA uses the following parameters:</P>

<P><code>saskia.wikipedia.db.name</code> - the database name (default: 'saskia').</P>
<P><code>saskia.wikipedia.db.url</code> - the URL for the database connection, which allows connections to remote databases. The default value is <code>jdbc:mysql://127.0.0.1</code>.</P>
<P><code>saskia.wikipedia.db.user</code> - the database user (default: 'saskia').</P>
<P><code>saskia.wikipedia.db.password</code> - the database's user password (default: 'saskia').</P>
<P><code>saskia.wikipedia.db.params</code> - for additional connection parameters. The default parameters for the MySQL connector/J are <code>useUnicode=true&amp;encodingCharset=UTF-8&amp;autoReconnect=true</code>, which enforce the use of UTF-8 on all MySQL transactions.</P>
<P><code>saskia.wikipedia.table.${name}</code> - the database table names, where $name can be: page, category, categorylinks, pagelinks or redirect.</P>
</blockquote>
