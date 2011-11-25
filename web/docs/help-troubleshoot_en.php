<H2>Common errors</H2>

<P>REMBRANDT is not, by any means, a mature software package. It's still far away from a 1.0 version, and there's a lost of improvements to make. See if your problem is listed here. hit: use a  <code>log4j.properties</code> configured to write <I>debug</I> messages, to print out the REMBRANDT inner steps.

<a name="top"></a>
<ul>Executing the REMBRANDT web service
   <li><a href="#remote1"> "Rembrandt service returned an error".</a></li>
</ul>
<ul>Executing the REMBRANDT software locally
<li><a href="#local1">I have an exception <code>in thread "main" java.lang.NoClassDefFoundError</code></a></li>
	<li><a href="#local2">REMBRANDT misses many obvious NEs!</a></li>
</ul>

<hr>

<H3>Executing the REMBRANDT web service</H3>

<a name="remote1"></a>
<H4>The web service returns "Rembrandt service returned an error".</H4>
<P>
There's something wrong with the service. The server will respawn the service every hour, so go grab a cup of coffee and try again later.</P>
<P>
<a class="intlink" href="#top">Go to top of the page</a>
<hr>

<H3>Executing the REMBRANDT software locally</H3>

<a name="local1"></a>
<H4>I have an exception <code>in thread "main" java.lang.NoClassDefFoundError</code></H4>
<P>Java hasn't found REMBRANDT. Check if CLASSPATH is well configured, or if you didn't mispelled the main Rembrandt class on the command line.</P>

<P>
<a class="intlink" href="#top">Go to top of the page</a>
<hr>

<a name="local2"></a>
<H4>O REMBRANDT misses many obvious NEs!</H4>

<P>note that REMBRANDT <B>does NOT have optimized grammar rules for English yet</B>, so the results may be somehow disappointing for now. I'm planning to optimize REMBRANDT to English text on a future release. Until then, mind that this service is <B>just a demonstration for English texts</B>. Thank you.<P>
<a class="intlink" href="#top">Go to top of the page</a>
<hr>
<P>Didn't find an answer to your problem? So <a href="<?php echo curPageURL(array('do'=>'devel-issues'));?>">report the error</a>. Help REMBRANDT being a better software.</P>