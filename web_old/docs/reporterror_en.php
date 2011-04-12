<DIV class="body">
	<H2>Report an error</H2>

<P>You've found an error while running REMBRANDT? Have you confirmed that the problem <a href="<?php echo curPageURL(array('do'=>'troubleshoot'));?>">is not on the most common error list?</a> Great, let's proceed to a proper error report.</P> 

<hr>

<H3>1.In you prefer to send an e-mail...</H3>
	
<P>Please, include the following information on the error report:</P>
<P><B>I. Execution environment</B>: What is the operative system? What are the versions of Java, Groovy, REMBRANDT, SASKIA and RENOIR? What are the Java environment variables used (CLASSPATH) and REMBRANDT configuration parameters? What was the encoding used on the command line while executing REMBRANDT?</P>
<P><B>II. Error stack</B>: If REMBRANDT crashed unexpectedly with an error, include an excerpt of the erorr stack trace that includes references to classes within the packages <code>rembrandt</code>, <code>saskia</code> ou <code>renoir</code>.</P>

<P><B>III. Input and output texts</B>: Include a text excerpt that causes the problem, so that I can reproduce the error. Please, send it on the original encoding (and say which was it), to make it easier to debug.</P>

<P><B>IV. Debug reports</B>: Use a <code>log4j.properties</code> file, configured in debug mode, and include it on the error report. Many errors can be only detected by browsing the logging outputs of small modules.</P>

<P><B>V. Describe the problem</B>: What went wrong? Where you hoping to see some entities to be tagged? The final classification was unexpected?</P>

<P><B>VI. About you</B>: At least your name and e-mail address, so I can be in ouch with you to say what's wrong, to warn when it's solved, and maybe clear out some questions regarding your report.</P>

<P><a href="mailto:ncardoso@xldb.di.fc.ul.pt?subject=Nuno,%20REMBRANDT%20made%20a%20mistake!">Send me a report on this annoying bug</A>, I'll see what I can do. 

<hr>

<H3>2. If you prefer to fill out a form...</H3>
	
<?php
// note-se que já há uma variável "do" em get... há que substituí-la.
?>
<FORM method="POST" action="<?php echo curPageURL(array('do'=>'errorform'));?>">
<P>
<TEXTAREA name="errorreport" style="width:95%; height:300px;">
<?php
 if ($_POST['errorreport']) {echo $_POST['errorreport'];}
 else {echo <<<OUTPUT
I. Execution environment:
 
  a. Operative System? Linux Fedora 9, Windows XP, MacOS X 10.5
  b. Java Version: Java 1.6.0_11
  c. Groovy Version: Groovy 1.6.2
  d. CLASSPATH value: 
  e. REMBRANDT parameters: -Drembrandt.input.encoding=UTF-8
  f. Encoding used: ISO-8859-1, UTF-8

II. Error stack trace:
   Exception in thread "main" java.lang.NoClassDefFoundError: rembrandt/bin/ExemploDePilhaDeErro

III. Text excerpts:
  a. Input text: "Rembrandt is a Dutch painter"
  b. Output text: "<EM CATEG="PERSON" TIPO="INDIVIDUAL">Rembrandt</EM> is a Dutch painter"

IV. log4j loggings: Put here the debug information for the critical modules.

V. Error description: Fill out what do you think it's wrong. 

VI: About me: Rembrandt Harmenszoon van Rijn, rembrandt@gmail.com
OUTPUT;

}
?>
</textarea>
</P>

<P>Now, to check if you're a human... today it's <?php echo prettyPrintDate(time()); ?>. Tell me: what is the day number plus the month number in
	<input type="text" name="captcha" size=4> and 
	<input type="submit" value="<?php echo $buttons['sendErrorForm'][$lang]; ?>">. Thank you.
</FORM>	
<hr>
<P>
</DIV>