<H2>Tutorial</H2>

<P>Write a text in English on the textbox below, and then execute REMBRANDT by pressing the button. You can leave the sample text, if you're feeling lazy.</P>
<P><B>Note:</B> This page runs fine on Firefox 3, Safari 4 and Opera 9+. IE, however, doesn't like it. Well, maybe it's time for you to finally switch browsers...</P> 

	<?php include('inc/rembrandt-form.php'); ?>
 	<?php include('inc/rembrandt-div.php'); ?> 	


<DIV class="rembrandt-tutorial" style="clear:both;margin-top:30px;">

<P>If the REMBRANDT service is running, the results will appear in the box above.<P>

<P>Click on the 'code' tab. The named entities (NE) recognized by REMBRANDT are tagged with <B>&lt;NE&gt;</B> tags, which have some attributes:</P>
<P><B>C1</B> - NE category, according to the <a href="http://www.linguateca.pt/aval_conjunta/HAREM/tabela.html">semantic classification</a> of <a href="http://www.linguateca.pt/aval_conjunta/HAREM/harem_ing.html">Second HAREM</a>.<BR>
<B>C2</B> - NE type (optional), according to the <a href="http://www.linguateca.pt/aval_conjunta/HAREM/tabela.html">semantic classification</a> of <a href="http://www.linguateca.pt/aval_conjunta/HAREM/harem_ing.html">Second HAREM</a>.<BR>
<B>C3</B> - NE subtype (optional, and only to certain categories), according to the <a href="http://www.linguateca.pt/aval_conjunta/HAREM/tabela.html">semantic classification</a> of <a href="http://www.linguateca.pt/aval_conjunta/HAREM/harem_ing.html">Second HAREM</a>.
<P><B>S and T</B> - Sentence and term number.
<P><B>WK and DB</B> - Identifiers: WK is the id on the Wikipedia page table; DB is the DBpedia resource.
</P>

<P>Note that, when REMBRANDT cannot decide between a group of categories and/or types, it chooses to show them all, separated by a | symbol. It is not easy to decide on a single category for some NEs; in the sentence 'Help the Firefighters', 'Firefighters' are an organization or a group of people? It's not so clear... and if it's not for us, humans, it's also not for REMBRANDT.</P>

<P>REMBRANDT can also present alternative annotations for the same expression. That is, consider the NE 'University of Lisbon'; what should be tagged, the institution 'University of Lisbon' or the place 'Lisbon'? In these cases, REMBRANDT uses <B>&lt;ALT&gt;</B> tags to represent both alternatives.</P>
</DIV>