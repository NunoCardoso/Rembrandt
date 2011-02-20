INSTALL PROCEDURE 
==================

NOTE: for more updated info and details / troubleshoot, please visit http://xldb.di.fc.ul.pt/Rembrandt/

1. Software requirements

1.1 Main jars: see DEPENDENCIES.txt

1.4 Database server

Rembrandt is being developed ant tested with a MySQL server 5.1.32 running on Linux and 
Mac, but any database server should work, provided that you must reconfigure the java 
connector (parameters saskia.wikipedia.db.driver and saskia.wikipedia.db.params).  

1.5 Java VM

Rembrandt requires Java VM 1.6

2. Load wikipedia databases

Go to http://download.wikipedia.org/ptwiki/ and download the recent snapshots for the PT 
languages (Rembrandt 1.0 will support any language, but for now, PT only. Sorry.) 

Required:  
ptwiki-latest-category.sql.gz
ptwiki-latest-categorylinks.sql.gz
ptwiki-latest-page.sql.gz
ptwiki-latest-redirect.sql.gz
ptwiki-latest-pagelinks.sql.gz 
(note: 'latest' can be a date, p.ex. '20080509'). 

Create a database (default name is saskia; you can change the saskia.wikipedia.db.name 
parameter). Grant privileges for a user 'saskia', password 'saskia' (or reconfigure 
saskia.wikipedia.db.user and saskia.wikipedia.db.password parameters). 

A faster way to load the files:

zcat ptwiki-latest-category.sql.gz | mysql saskia -u saskia --password=saskia

Note that there may be some issues with the encodings. We recommend UTF-8 for everything, 
but it might issue some problems with MySQL index sizes. You can either disable key 
indexes or create shorter indexes, or try a non-UTF-8 encoding (not recommended). 

Afterwards, rename all tables by adding a "pt_" prefix. 
For example: 
mysql saskia -u saskia --password=saskia -e "rename table page to pt_page"

3. Configuration

Rembrandt works with default parameters, but you can tweak configuration settings by:
 1 - Writing  a default configuration file in ~/.rembrandt_renoir_saskia_conf.xml
 2 - Specifying a configuration file as a first argument
 3 - Adding -D environment variables 

Parameters will be overrided acording to that order: first, the default file; second, 
the argument; last, the environment variables, which are in the top. 

4. Run Rembrandt

The simplest way is through stdin and stdout. 
On a command line, type: 

echo "Rembrandt" | java rembrandt.bin.Rembrandt

The output should be:

<!-- Rembrandted by v.1.2 -->
<DOC DOCID="stdin-1" LANG="pt">
<TITLE>
</TITLE>
<BODY>
{<EM ID="0" S="0" T="0" C1="PESSOA" C2="INDIVIDUAL" DB="Rembrandt">[Rembrandt]</EM>}
</BODY>
</DOC>

The number on the COMENT reports to the page_id on the pt_page table; see it as an identifier for the resource pt.wikipedia.org/wiki/Rembrandt.

4.1 Encodings may ruin everything. You can configure the parameter: 
      - rembrandt.input.encoding
      - rembrandt.output.encoding
      - rembrandt.err.encoding
to ensure that the right encodings are used, either in the stdin/stdout/stderr 
streams, or in the files read/written. 

4.2 To load text from a file, set up the rembrandt.input.type and rembrandt.input.value.
To write to a file, set up the rembrandt.output.type and rembrandt.output.value parameters.

for example, the command:

java -Drembrandt.input.type=File -Drembrandt.input.value=file1.txt 
-Drembrandt.output.type=File -Drembrandt.output.value=file2.txt rembrandt.bin.Rembrandt

will read from a file named file1.txt, and write to a file2.txt. Check the encodings!

4.3. For batch process of several files, one can bundle them either in Second HAREM's XML format, or in a HTML/XML-ish file (with a root tag <htmlset> containing one or more <html> tags) to separate documents. The HTML/XML-ish format strips HTML tags and only tags the body. The writing format can also be configured in a similar way. 

Suppose that we have a file, file1.html, as follows (text extracted from Wikipedia): 

<htmlset>
<html><head><title><title>Rembrandt</title></head>
<body>Rembrandt <B>Harmenszoon</B> van Rijn (Leiden, 15 de julho de 1606 – Amesterdão, 
4 de outubro de 1669) foi um pintor e gravador holandês.</body></html>
<html><head><title>Saskia van Uylenburgh</title></head>
<body>Saskia van Uylenburgh (2 de agosto de 1612, Leeuwarden; 14 de junho de 1642, 
Amsterdã) foi filha do prefeito de Amsterdã e casada com Rembrandt, o mestre da arte 
barroca. Seu pai era um burgomestre à época, pertencente à nata da sociedade local.
</body></html>
<html><head><title>Pierre-Auguste Renoir</title></head>
<body>Pierre-Auguste Renoir (Limoges, 25 de fevereiro de 1841 - Cagnes-sur-Mer, 3 de 
dezembro de 1919) foi um dos mais célebres pintores franceses e um dos mais importantes 
nomes do movimento impressionista.
</body></html>
</htmlset>

By issuing the command:

java -Drembrandt.input.type=File -Drembrandt.input.value=file1.html -Drembrandt.input.reader=rembrandt.io.HTMLCollectionReader -Drembrandt.output.writer=rembrandt.io.HTMLDocumentWriter rembrandt.bin.Rembrandt

The output will have a bundle of HTML documents, with a <colHAREM> root tag.  

5. Other configuration settings: 
 
 5.1 - rembrandt.core.doEntityRelation  (can be true or false)

 Turns on Entity Relation Detection (default: off). After NE recognition, it tries to 
recover NEs with unknown classification by looking into its surrounding NEs. 
   Advantages: it increases the number of NEs with classification. 
   Disadvantages: it is still not optimized, and can be time-consuming.

 5.2 - rembrandt.core.doALT (can be true or false)
 
 Allows the generation of alternative classifications on the same text excerpt 
 (default: on). 
    Advantages: It generates more NEs that suits more contexts. For instance, 
 'Universidade de Lisboa' is bothtagged as 'Universidade  de Lisboa' and 'Lisboa'.
    Disadvantages: The <ALT> tag style to represent alternatives repeats the text, 
  so the text post-process must be done very carefully.

 5.3. rembrandt.core.removeRemainingUnknownNE (can be true or false)

 In the end, it decides what to do for NE without a classification (default: on)
 If true, it discards the NEs. If false, it leaves the NEs without classification.
   Advantages on being on: It is more precise, restricts to well-defined NEs.
   Disadvantages: Damages recall.

for more information about configuration parameters, see CONFIGURATION.txt, or the online documentation.
