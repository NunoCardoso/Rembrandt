<H2>Installing REMBRANDT</H2>

<P>This is the recommended pipeline of actions you should perform so that REMBRANDT is 
installed nicely. Please read carefully each instructions. You may skip a step if 
you know what you are doing, but please make sure you REALLY know what you are doing. </P>


<H3>1. Software requirements</H3>
<blockquote>
<P>See DEPENDENCIES.txt for a comprehensive list of jars that REMBRANDT depends on.
These are included on the ${REMBRANDT_HOME}/lib directory.</P>

<P>Rembrandt is being developed and tested with a MySQL server 5.1.32 running on Linux and 
Mac, but any database server should work, provided that you must reconfigure the java 
connector (parameters saskia.wikipedia.db.driver and saskia.wikipedia.db.params).  
</P>

<P>Rembrandt also requires Java VM 1.6.</P>

<H3>2. Load wikipedia databases</H3>

<div style="-moz-border-radius:4px 4px 4px 4px;	
	-webkit-border-radius:4px 4px 4px 4px;
	padding:10px;
	margin:10px;
	background-color:#ffffdd;"><B>Note:</B> if you *know* what you are doing, you can skip this step. Installing the  Wikipedia databases greatly increases REMBRANDT's performance, when DBpedia is not able to classify entities. The cons is that you'll need a considerable amount of space to install Wikipedia snapshots, it will take some time, and you *must* make sure that MySQL is working on UTF-8 charset, to avoid encoding-related problems. 
</div>

<P>Go to <a href="http://download.wikipedia.org/">http://download.wikipedia.org/</a> and download the recent snapshots for your targeted language. </P>

<P>Snapshots required:  
<UL><LI>{lang}wiki-latest-category.sql.gz
<lI>{lang}wiki-latest-categorylinks.sql.gz
<li>{lang}wiki-latest-page.sql.gz
<li>{lang}wiki-latest-redirect.sql.gz
<li>{lang}wiki-latest-pagelinks.sql.gz 
</UL>
</P>
<P>Where {lang} is the two letter code of the targeted language. (note: 'latest' can be a date, p.ex. '20080509'. wikipedia symlinks 'latest' to a date). </P>

<P>Create a database by entering MySQL shell (write 'mysql' in the prompt) and with the command: </P>

<P><CODE>
mysql> CREATE DATABASE 'wikipedia';
</CODE></P>

<P><B>Note:</B> default name is wikipedia; you can change the saskia.wikipedia.db.name parameter). Grant privileges for a user 'wikipedia', password 'saskia' (or reconfigure 
saskia.wikipedia.db.user and saskia.wikipedia.db.password parameters) with the command: </P>

<P><CODE>
mysql> GRANT ALL PRIVILEGES ON wikipedia.* TO 'wikipedia'@'localhost' IDENTIFIED BY 'saskia';
</CODE></P>

<P>Now, exit MySQL and batch-load the Wikipedia database files. </P>

<P>A suggested way to load the files is: </P>
(in Linux): 
<P><CODE>
	zcat ptwiki-latest-category.sql.gz | mysql wikipedia -u wikipedia --password=saskia
</CODE></P>
(in Mac): 
<P><CODE>
	gzcat ptwiki-latest-category.sql.gz | mysql wikipedia -u wikipedia --password=saskia
</CODE></P>

<P>Afterwards, rename all tables by adding a "{lang}_" prefix. This is required, 
to allow the database to serve more than one language. Just replace {lang} to the two letter code. </P>

<P>For example, in the system shell: </P>
<P><CODE>
	mysql saskia -u saskia --password=saskia -e "rename table page to pt_page"
</CODE></P>

<P>or in the MySQL shell:</P>
<P><CODE>
mysql> rename table page to pt_page;
</CODE></P>

</blockquote>

<H3>3. Set Saskia DB database</H3>

<blockquote>
<div style="-moz-border-radius:4px 4px 4px 4px;	
	-webkit-border-radius:4px 4px 4px 4px;
	padding:10px;
	margin:10px;
	background-color:#ffffdd;">
<B>Note:</B> The Saskia DB is an useful repository to store source documents and tagged documents. It is NOT required for document tagging, but it is a nice addition if you want to store tagged documents, analyse them and perform more advanced analysis on them. It is mandatory, although it can be changed in the future.</DIV>

<P>In a similar way, create a database by entering MySQL shell (write 'mysql' in the prompt) and with the command:</P>

<P><CODE>
mysql> CREATE DATABASE 'saskia';
</CODE></P>

<P>(<B>Note:</B> default name is saskia; you can change the saskia.db.name parameter). Grant privileges for a user 'saskia', password 'saskia' (or reconfigure
saskia.db.user and saskia.db.password parameters) with the command:</P>

<P><CODE>
mysql> GRANT ALL PRIVILEGES ON saskia.* TO 'saskia'@'localhost' IDENTIFIED BY 'saskia';
</CODE></P>

<P>Now, exit MySQL and batch-load the Saskia tables, with the following command:</P>

<P><CODE>
mysql -u saskia saskia --password="saskia" &lt; db/Saskia_create_db_6.1.sql
</CODE></P>

<P>Now, let's create the first user. Please, edit the file db/Saskia_create_user.sql by replacing the login name, first name, last name and email name if you wish 
(the remaining parameters are for server accesses, not relevant for now). Issue the command: </P>

<P><CODE>
mysql -u saskia saskia --password="saskia" &lt; db/Saskia_create_user.sql
</CODE></P>

<P>Now, let's create the first collection. Likewise, edit the file db/Saskia_create_collection.sql
by replacing the collection name, language and description. Issue the command:</P>

<P><CODE>
mysql -u saskia saskia --password="saskia" &lt; db/Saskia_create_collection.sql
</CODE></P>

<P>You can check if they were successfully created by going to the MySQL shell and type:</P>

<P><CODE>
mysql> SELECT * from user;
mysql> SELECT * from collection;
</CODE></P>

<H3>4. set WEBSTORE</H3>

<blockquote>
<P>WEBSTORE is a simple document manager, which is useful in offering a document storage 
solution that does not involve using MySQL blobs, keeping the database for metadata only.
Webstore can be downloaded from <a href="http://webstore.sourceforge.net">http://webstore.sourceforge.net</A>, but it is also included 
in REMBRANDT_HOME/lib/webstore-dist.zip. </P>

<P>Unzip it to a home directory (/usr/local/webstore, /home/you/rembrandt/webstore, you choose), 
just make sure that it will be on a partition with lots of space and in a directory with the 
necessary permissions, as Webstore will have running daemons which will store and retrieve all 
your documents!
Wherever your directory of choice is, let's call it WEBSTORE_HOME.</P>

<P>Add the following variables to the environment (make sure they are always configured, by placing 
them on ~/.bashrc or in /etc/profile): </P>

<P><CODE>
export WEBSTORE_HOME={WEBSTORE_HOME}
export WEBSTORE_CONFIG_FILE={WEBSTORE_CONFIG_FILE}
export WEBSTORE_DATA={WEBSTORE_DATA}
</CODE></P>

<P>where:</P>

<UL>
<LI>{WEBSTORE_HOME} - the directory where Webstore files are located.
<LI>{WEBSTORE_CONFIG_FILE} - the path to the conf file. Use REMBRANDT_HOME/conf/webstore-conf.xml.
<LI>{WEBSTORE_DATA} - directory where the files will be stored. Make sure you create this directory. 
For example, WEBSTORE_HOME/data
</UL>

<P>Now, add the Webstore binary files to the PATH, for example with:</P>

<P><CODE>
  export PATH=$PATH:${WEBSTORE_HOME}/bin/linux
</CODE></P>

<P>Now, let's try if webstore is working... write 'webstore' on the shell. You should see the following 
output: </P>

<PRE>
Usage: webstore option {args}*
options:
   * -l,--launch-vol-server volume-id: launch a volume server.
   * -k,--kill-vol-server volume-id: kill a volume server.
   * -f,--count-files volume-id -> count the number of files in a volume.
   * -c,--count-contents volume-id -> count the number of contents in a volume.
   * -o,--count-overloads volume-id -> count the number of fileoverloads in a volume.
   * -m,--make-volume volume-id -> create a volume (directory tree).
   * -e,--erase-volume volume-id -> clear all the contents of a volume (not the directory tree).
   * -s,--store volume-id content -> stores a content in a volume.
   * -r,--retrieve key -> retrives a content.
   * -d,--delete key -> deletes a content.

local volume servers up: (volume ids)
(none)
</PRE>

<P>There are no volume servers running. You must create two: one for source documents, other for 
tagged documents. To do that, issue the following commands (they make take a while): </P>

<P><CODE>
webstore -m sdoc
webstore -m rdoc
</CODE></P>

<P>Now, launch the volumes (important: webstore MUST know where the webstore-conf.xml is, with the 
environment variable WEBSTORE_CONFIG_XML properly set). if you write 'webstore' on the shell, you 
shall see:</P>

<P><CODE>
  local volume servers up: (volume ids)
  sdoc rdoc
</CODE></P>

<P>instead of:</P>

<P><CODE>
  local volume servers up: (volume ids)
  (none)
</CODE></P>

<P>It is VERY important that the volume servers are running (that is, servers on ports 4444 and 4445). 
Note that, if you reboot the system, the volume servers will not be up, unless you tweak the system. 
Also, make sure that if you are running Webstore with non-root privileges, that the data files have 
the right permissions.</P>

</blockquote>

<H3>5. set home directory, CLASSPATH and WEBSTORE</H3> 

<P>If you are reading this, you probably already have untarred the file. 
Decide on a home directory for REMBRANDT (for example, /usr/local/rembrandt, /home/you/rembrandt), 
and make sure the directory structure is maintained. For explaining this step, let's assume 
you installed on /home/you/rembrandt (therefore, the REMBRANDT_HOME directory). </P>

<P>Make sure that the jar files in the REMBRANDT_HOME/lib are in the CLASSPATH. 
Also, make sure that the rembrandt.jar, which is located on REMBRANDT_HOME, is also
included on the CLASSPATH (you can move it to REMBRANDT_HOME/lib, for example).
You can copy them 
to a directory that you use to store all jars. The recommended way is to add the 
following lines on "~/.bashrc" (just for you) or "/etc/profile" (for everyone):</P>

<P><CODE>
export CLASSPATH=$CLASSPATH:`ls /home/you/rembrandt/lib/*.jar | tr "\n" ":"`
</CODE></P>

<H3>6. Test REMBRADNT in simple mode </H3>

<P>Ok, now you are ready to test if REMBRANDT is working. Just type: </P>

<P><CODE>
echo "Rembrandt" | java rembrandt.bin.Rembrandt
</CODE></P>

<P>And the output should be:</P>

<PRE>
&lt;!-- Rembrandted by v.1.3 --&gt;
&lt;DOC DOCID="stdin-1" LANG="pt"&gt;
&lt;TITLE&gt;
&lt;/TITLE&gt;
&lt;BODY&gt;
{&lt;EM ID="0" S="0" T="0" C1="PESSOA" C2="INDIVIDUAL" DB="Rembrandt"&gt;[Rembrandt]&lt;/EM&gt;}
&lt;/BODY&gt;
&lt;/DOC&gt;
</PRE>

<P>Note that, if you see a "DB" parameter, that means that the DBpedia was used to 
classify the NE.</P>

<P>Tips:</P>

<UL>
	<LI> Encodings may ruin everything. You can configure the parameter: 
  <UL><LI> rembrandt.input.encoding
  <LI>rembrandt.output.encoding
  <LI>rembrandt.err.encoding
</UL>
to ensure that the right encodings are used, either in the stdin/stdout/stderr 
streams, or in the files read/written. 
<LI> 
To load text from a file, set up the rembrandt.input.file.  
To write to a file, set up the rembrandt.output.file parameters.
</UL>

<P>for example, the command:</P>

<P><CODE>
java -Drembrandt.input.file=file1.txt 
-Drembrandt.output.file=file2.txt rembrandt.bin.Rembrandt
</CODE></P>

<P>will read from a file named file1.txt, and write to a file2.txt. Check the encodings!</P>

<H3>7. Test REMBRADNT in DB mode</H3>

<H4>7.1 Load documents </H4>

<P>Create a file with a sample text, for instance, "Rembrandt". Name it sample.txt, for example.
First, you need to load that file into the SourceDocument table. </P>

<P>Make sure the webstores are up and running. On the REMBRANDT_HOME you can run the script:</P>

<P><CODE>
./script/importX2Sdocs.sh
</CODE></P>

<P>It will ask you to point out the document file, collection id or name, user id or name, 
document language and original id. You can check the source code for saskia.import.* files 
to check the several import procedures to store a document in MySQL (and in Webstore). </P>

<P>If the document was successfully imported, then in the Saskia DB you should see it
(let's assume that the collection id is 1):</P>

<P><CODE>
mysql> SELECT sdoc_id, sdoc_webstore from source_doc where sdoc_collection=1;
</CODE></P>

<P>You should see a given Webstore key:</P>

<PRE>
+---------+---------------------------+
| sdoc_id | sdoc_webstore             |
+---------+---------------------------+
|       1 | 6502065618501188246@sdoc% |
+---------+--------------------+------+
</PRE>

<P>To see the document, ask Webstore to show it, by passing the key:</P>
 
<P><CODE>
  webstore -r 6502065618501188246@sdoc%
</CODE></P>

<P>You should see something like the document, formatted in REMBRANDT internal format: </P>

<PRE>
&lt;DOC DOCID="doc_20110303090933" LANG="pt"&gt;
&lt;TITLE&gt;
&lt;/TITLE&gt;
&lt;BODY&gt;
{[Rembrandt]}
&lt;/BODY&gt;
&lt;/DOC&gt;

<H3>7.2 Tag documents</H3>

<P>To invoke REMBRANDT on the document(s) you just stored, use the script:</P>

<P><CODE>
./script/importS2Rdocs.sh
</CODE></P>

<P>Give the user/collection, an amount of documents to parse, and it will 
automatically search for untagged documents, tag them, and store them in the 
doc table. Likewise, check them with:</P>

<PRE>
mysql> select doc_id, doc_webstore from doc
+--------+---------------------------+
| doc_id | doc_webstore              |
+--------+---------------------------+
|      1 | 3813646601739006368@rdoc% |
+--------+---------------------------+
</PRE>

<P>and: </P>

<P><CODE>
webstore -r 3813646601739006368@rdoc%
</CODE></P>

<PRE>
&lt;DOC DOCID="doc_20110303090933" LANG="pt"&gt;
&lt;TITLE&gt;
&lt;/TITLE&gt;
&lt;BODY&gt;
{&lt;EM ID="0" S="0" T="0" C1="PESSOA" C2="INDIVIDUAL" DB="Rembrandt"&gt;[Rembrandt]&lt;/EM&gt;}
&lt;/BODY&gt;
&lt;/DOC&gt;
</PRE>

<H4>7.2 Tag documents</H4>

<P>To invoke REMBRANDT on the document(s) you just stored, use the script:</P>

<P><CODE>
./script/importS2Rdocs.sh
</CODE></P>

<P>Give the user/collection, an amount of documents to parse, and it will 
automatically search for untagged documents, tag them, and store them in the 
doc table. Likewise, check them with:
</P>

<P><CODE>
mysql> select doc_id, doc_webstore from doc
</CODE></P>

<PRE>
+--------+---------------------------+
| doc_id | doc_webstore              |
+--------+---------------------------+
|      1 | 3813646601739006368@rdoc% |
+--------+---------------------------+
</PRE>

<P>and: </P>

<P><CODE>
webstore -r 3813646601739006368@rdoc%
</CODE></P>

</PRE>
&lt;DOC DOCID="doc_20110303090933" LANG="pt"&gt;
&lt;TITLE&gt;
&lt;/TITLE&gt;
&lt;BODY&gt;
{&lt;EM ID="0" S="0" T="0" C1="PESSOA" C2="INDIVIDUAL" DB="Rembrandt"&gt;[Rembrandt]&lt;/EM&gt;}
&lt;/BODY&gt;
&lt;/DOC&gt;
</PRE>

<H4>7.3 Analyse documents</H4>

<P>You can populate other tables with NEs, entities and geoscopes, using the script:</P>

<P><CODE>
./script/importR2Pdocs.sh
</CODE></P>
