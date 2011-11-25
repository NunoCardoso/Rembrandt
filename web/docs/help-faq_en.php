
<H2>Frequently asked questions</H2>

<a name="top"></a>
<ul>Introduction
	<li><a href="#1">What is REMBRANDT?</a></li>
	<li><a href="#1.2">What is SASKIA and RENOIR?</a></li>
	<li><a href="#1.5">Why these names, REMBRANDT, SASKIA and RENOIR?</a></li>
</ul>
<ul>Execution
	<li><a href="#2">Can I run REMBRANDT on my computer?</a></li>
	<li><a href="#2.2">Can I change REMBRANDT source code for my own use?</a></li>
	<li><a href="#2.5">What kinds of entities does REMBRANDT annotate?</a></li>
	<li><a href="#2.7">What languages is REMBRANDT prepared for?</a></li>
	<li><a href="#3">How does REMBRANDT work?</a></li>
	<li><a href="#3.5">Is REMBRANDT good? Can it tag EVERYTHING?</a></li>
	<li><a href="#4">REMBRANDT does not tag entities as I was expecting!</a></li>
</ul><ul>References
	<li><a href="#5">How do I cite REMBRANDT?</a></li>
</ul>
<hr>
<H3>What is REMBRANDT?</H3>
<a name="1"></a>
<P>REMBRANDT is a named entity recognition tool that identifies and classifies all named entities (NE) in the text (that is, entity names such as proper names, places or organizations), and detects the relations among NEs. REMBRANDT is prepared to classify entities that have potentially different meanings, and disambiguates its meaning whenever possible.</P>

<P>REMBRANDT is a software package developed by me, <a href="http://xldb.di.fc.ul.pt/ncardoso">Nuno Cardoso</a>, on behalf of my PhD work.</P>
	
<P>My PhD work is related to two projects: 1) <A href="http://www.linguateca.pt">Linguateca</A>, located in <A href="http://www.sintef.no">SINTEF</A>, and 2) <a href="http://xldb.di.fc.ul.pt/wiki/Grease">GReaSE</A>, from the   <a href="http://xldb.di.fc.ul.pt">XLDB team</a>, <a href="http://www.lasige.di.fc.ul.pt">LaSIGE laboratory</a>, of the <a href="http://www.di.fc.ul.pt">Department of Informatics</a> of the <a href="http://www.fc.ul.pt">Faculty of Sciences, University of Lisbon</a>.</P>

<P><a class="intlink" href="#top">Go to Top of the page</a></P>
<hr>

<H3>What is SASKIA and RENOIR?</H3>
<a name="1"></a>
<P><B>SASKIA</B> is a program that proxies REMBRANDT requests to raw data from <a href="http://www.wikipedia.org">Wikipedia</a> and <a href="http://www.dbpedia.org">DBpedia</a>, to extract knowledge which classifying NEs.</P>
<P><B>RENOIR</B> is an advanced question parser and answering module, aimed to extract intentions and more elaborated meanings from NEs in queries, and reason over them. For instance, RENOIR can understand the question "<I>What is the capital of Portugal?</I>" and, with the help of REMBRANDT and SASKIA, get the correct NE that answers it, "Lisbon".</P>

<P><a class="intlink" href="#top">Go to Top of the page</a></P>
<hr>

<a name="1.5"></a>
<H3>Why these names, REMBRANDT, SASKIA and RENOIR?</H3>
<P><B>REMBRANDT</B> is an acronym for <B>R</B>econhecimento de <B>E</B>ntidades <B>M</B>encionadas <B>B</B>aseado em <B>R</B>elações e <B>AN</B>álise <B>D</B>etalhada do <B>T</B>exto (roughly, <I>Named Entity Recognition Based of Relations and Detailed Analysis of Text</I>). A good acronym is the first step for good software.It seems it was also a <a href="http://pt.wikipedia.org/wiki/Rembrandt">dutch painter</A>...</P>
	
<P><B>SASKIA</B> is an acronym for <B>S</B>PARQL <B>A</B>PI <B>S</B>ervice for <B>K</B>nowledge and <B>I</B>nformation <B>A</B>ccess. It seems that there is also a <a href="http://pt.wikipedia.org/wiki/Saskia_van_Uylenburgh">person called Saskia, who married the painter Rembrandt</a>. Coincidences...</P>

<P><B>RENOIR</B> is an acronym for <B>R</B>EMBRANDT's <B>E</B>xtended <B>N</B>ER <B>O</B>n <B>I</B>nformation <B>R</B>etrieval, until I find a better acronym. Coincidentally, it looks like it's also the name of a <a href="http://en.wikipedia.org/wiki/Renoir">french painter</a>...</P>

<P><a class="intlink" href="#top">Go to Top of the page</a></P>
<hr>

<H3>Can I run REMBRANDT on my computer?</H3>
<a name="2"></a>
<P>Yes. REMBRANDT is freely available to everyone (please, <a href="<?php echo curPageURL(array('do'=>'about-tos'));?>">read the disclaimer</a> before using it). REMBRANDT can be <a href="<?php echo curPageURL(array('do'=>'download-release'));?>">downloaded</a> and executed on any machine, as long as it has <a href="http://www.java.com/getjava/">Java 1.6</a> installed. You'll need also to download <a href="<?php echo curPageURL(array('do'=>'download-dependencies'));?>">other Java packages that REMBRANDT requires</a>, as well as have access to a database.</P>
	
<P>As a data source, REMBRANDT needs a local copy of the Wikipedia databases for the language(s) you want to annotate. These databases can be downloaded for free, and <a href="<?php echo curPageURL(array('do'=>'download-db'));?>">the downloads page</a> has kinks to those databases. It's also required a database server, like <a href="http://www.mysql.com">MySQL</a>, which is freely available. In summary, you can run REMBRANDT on your computer for free. </P>

<P>The installation instructions are detailed on the <a href="<?php echo curPageURL(array('do'=>'devel-install'));?>">REMBRANDT's installation page</a>.</P>

<P><a class="intlink" href="#top">Go to Top of the page</a></P>
<hr>

<H3>Can I change REMBRANDT source code for my own use?</H3>

<P>Yes, the source code is included on the software packages, under a <a href="http://www.gnu.org/copyleft/gpl.html">GPL license</a>.</P>

<P><a class="intlink" href="#top">Go to Top of the page</a></P>
<hr>

<H3>What kinds of entities does REMBRANDT annotate?</H3>
<a name="2.5"></a>
<P>REMBRANDT identifies and classifies entities according to the <a href="http://www.linguateca.pt/HAREM">Second HAREM directives</a>, created by <a href="http://www.linguateca.pt">Linguateca</a> in collaboration with researchers on Portuguese natural language processing. </P>

<P>The semantic classification is made through a generic <I>category</I>, and a specialization in two levels (<I>type</I> and <I>subtype</I>). There is nine main categories:</P>
<ul>
<li><B>PERSON</B> - Includes person names, positions or groups of persons.</li>
<li><B>ORGANIZATION</B> - Includes companies, institutions and other administrative entities.</li>
<li><B>PLACE</B> - Includes geographic places and virtual places (such as newspapers, TV shows or Internet sites).</li>
<li><B>TIME</B> - Includes temporal expressions like time, dates or weekdays.</li>
<li><B>VALUE</B> - Includes numeric expressions like quantities and measurements.</li>
<li><B>MASTERPIECE</B> - Includes works of art, films, paintings, etc.</li>
<li><B>EVENT</B> - Includes past events and relevant happenings.</li>
<li><B>THING</B> - Includes entities that refer to objects or object classes.</li>
<li><B>ABSTRACTION</B> - Includes abstract concepts such as intellectual movements, research areas, 
	philosophical concepts, etc.</li>
</ul>

<P><a class="intlink" href="#top">Go to Top of the page</a></P>
<hr>

<a name="2.7"></a>
<H3>What languages is REMBRANDT prepared for?</H3>
<P>REMBRANDT is prepared to use annotation rules for several languages, and tag texts of different languages simultaneously. Nonetheless, the rules for English text are not optimized, and as such the results for English are not famous.</P>

<P>While it is not an urgent matter, I hope to great English grammar rules from scratch on a forthcoming release. Stay tuned to the <a href="<?php echo curPageURL(array('do'=>'devel-wishlist'));?>">wish-list page of  REMBRANDT</a>, to know when it's planned to be addressed. Nonetheless, you can still tag English texts although the quality of the results are not comparable to Portuguese annotation results.</P> 	

<P><a class="intlink" href="#top">Go to Top of the page</a></P>
<hr>

<a name="3"></a>
<H3>How does REMBRANDT work?</H3>

<P>REMBRANDT implements two main strategies on named entity recognition: i) it uses grammar rules for each language, namely on the detection of internal and external evidence, like the presence of "Mr." preceding a person's name. 2) it extracts information from Wikipedia, to obtain knowledge and know the different meanings associated to each name.</P>

<P>Please see the <a href="<?php echo curPageURL(array('do'=>'help-doc'));?>">published papers and presentation</a> for more information about REMBRANDT.</P>

<P><a class="intlink" href="#top">Go to Top of the page</a></P>
<hr>

<a name="3.5"></a>
<H3>Is REMBRANDT good? Can it tag EVERYTHING?</H3>
<P>REMBRANDT is not an oracle, and it fails like any tool made by a human. REMBRANDT participated on the <a href="http://www.linguateca.pt/HAREM">Second HAREM, a specific evaluation contest for named entity recognition systems for Portuguese</a>, organized by <a href="http://www.linguateca.pt">Linguateca</a> in April 2008 with its version 0.7, and among 10 systems, it achieved second place on the overall NER task, with a F-measure value of 0.567. In the scenario with only <B>PLACE</B> entities, it achieved first place, among 8 systems, with a F-measure of 0,625. On the entity relation detection task, REMBRANDT achieved first place among three participating systems.</P>

<P>In summary, o REMBRANDT it's pretty alright, but I wish it could be better. That's why I have a page to <a href="<?php echo curPageURL(array('do'=>'devel-issues'));?>">collect error reports and tagging problems</a>, so that I can improve REMBRANDT, its grammar rules and include cases that I've overlooked.</P>

<P><a class="intlink" href="#top">Go to Top of the page</a></P>
<hr>

<a name="4"></a>
<H3>REMBRANDT does not tag entities as I was expecting!</H3>

<P>This can happen for many reasons, but first, mind the following: <B>REMBRANDT annotates named entities within context</b>, that is, it tries to assign the meaning that the entity has on the sentence, not the most common meaning associated to that entity name.<P> 
<P>That is, tagging 'Portugal' always as a country is not the REMBRANDT goal: the name 'Portugal' can have other roles that depend on the context, such as a group of persons (in the case of a sports team), or an organization (in the case of a governmental decision). Isn't it what's happening?</P>

<P><a class="intlink" href="#top">Go to Top of the page</a></P>
<hr>

<a name="5"></a>
<H3>How do I cite REMBRANDT?</H3>

<P>Please, cite REMBRANDT with the following reference:</P>

<P>Nuno Cardoso, <I>REMBRANDT - Reconhecimento de Entidades Mencionadas Baseado em Relações e ANálise Detalhada do Texto</I>. In Cristina Mota &amp; Diana Santos (eds.). Desafios na avaliação conjunta do reconhecimento de entidades mencionadas: O Segundo HAREM. Linguateca. 2008. In Portuguese.</P>
<P><a class="intlink" href="#top">Go to Top of the page</a></P>