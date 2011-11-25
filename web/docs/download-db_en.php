<H2>Database</H2>
<P><a href="http://www.mysql.com">MySQL</a> is the recommended database server (version 5.1.32 or higher), not only because it's being used on the development of REMBRANDT, but because it's also the Wikipedia's default database server and, as such, the database import should be more seamless. Nonetheless, REMBRANDT should work with most database servers, as long as the Wikipedia databases are properly imported, and you use a required Java connector.</P>
 
<H3>4. Wikipedia databases</H3>

<P><a href="http://www.wikipedia.org">Wikipedia</A> releases periodically snapshots of their articles, in HTML and Wiki format, and their databases, and anyone can freely download those files. From the <a href="http://download.wikipedia.org/ptwiki/">latest version for the English Wikipedia</a>, REMBRANDT needs the following files (note that these may be very long files):</P>
<ul>English Wikipedia:
<li><a href="http://download.wikipedia.org/enwiki/latest/enwiki-latest-category.sql.gz">enwiki-latest-category.sql.gz</a></li>
<li><a href="http://download.wikipedia.org/enwiki/latest/enwiki-latest-categorylinks.sql.gz">enwiki-latest-categorylinks.sql.gz</a></li>
<li><a href="http://download.wikipedia.org/enwiki/latest/enwiki-latest-page.sql.gz">enwiki-latest-page.sql.gz</a></li>
<li><a href="http://download.wikipedia.org/enwiki/latest/enwiki-latest-redirect.sql.gz">enwiki-latest-redirect.sql.gz</a></li>	
<li><a href="http://download.wikipedia.org/enwiki/latest/enwiki-latest-pagelinks.sql.gz">enwiki-latest-pagelinks.sql.gz</a></li>	
</UL>
<BR>
<ul>Portuguese Wikipedia:
<li><a href="http://download.wikipedia.org/ptwiki/latest/ptwiki-latest-category.sql.gz">ptwiki-latest-category.sql.gz</a></li>
<li><a href="http://download.wikipedia.org/ptwiki/latest/ptwiki-latest-categorylinks.sql.gz">ptwiki-latest-categorylinks.sql.gz</a></li>
<li><a href="http://download.wikipedia.org/ptwiki/latest/ptwiki-latest-page.sql.gz">ptwiki-latest-page.sql.gz</a></li>
<li><a href="http://download.wikipedia.org/ptwiki/latest/ptwiki-latest-redirect.sql.gz">ptwiki-latest-redirect.sql.gz</a></li>	
<li><a href="http://download.wikipedia.org/ptwiki/latest/ptwiki-latest-pagelinks.sql.gz">ptwiki-latest-pagelinks.sql.gz</a></li>	
</UL>
