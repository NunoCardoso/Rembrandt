<H2>Instalar o REMBRANDT</H2>

<P>Esta é a sequência recomendada de acções para instalar o REMBRANDT. Por favor, leia atentamente cada instrução. Se souber o que esta´a fazer, pode saltar alguns passos, mas garanta que sabe o que está a fazer. </P>

<H3>1. Requisitos</H3>

<P>Veja o ficheiro DEPENDENCIES.txt, que tem uma lista de ficheiros jar que o REMBRANDT precisa. Estes ficheiros estão incluídos na directoria ${REMBRANDT_HOME}/lib.</P>

<P>Rembrandt é desenvolvido no MySQL server 5.1.32 a correr num Linux e num MacOS X, mas em princípio qualquer base de dados serve, desde que o conector próprio esteja configurado em saskia.wikipedia.db.driver e saskia.wikipedia.db.params.  </P>

<P>O Rembrandt também precisa do Java VM 1.6.</P>

<H3>1. Criação das bases de dados da Wikipédia</H3>
<blockquote>

<div style="-moz-border-radius:4px 4px 4px 4px;	
	-webkit-border-radius:4px 4px 4px 4px;
	padding:10px;
	margin:10px;
	background-color:#ffffdd;"><B>Nota:</B> pode-se saltar este passo, se tiver a certeza do que está a fazer. Instalar as bases de dados da Wikipédia melhora consideravelmente o desempenho do REMBRANDT quando a DBpedia não consegue classificar entidades. No entanto, este passo requer bastante espaço em disco para instalar as bases de dados da Wikipédia, pode demorar algum tempo, e você tem de garantir que o MySQL está a funcionar no charset UTF-8, para minimizar os problemas de codificação de caracteres.</div>

<P> vá a <a href="http://download.wikipedia.org/">http://download.wikipedia.org/</A> e descarregue as bases de dados para a sua língua. 

<P>Os ficheiros necessários são:</P>
<UL>
<LI>{lang}wiki-latest-category.sql.gz
<LI>{lang}wiki-latest-categorylinks.sql.gz
<LI>{lang}wiki-latest-page.sql.gz
<LI>{lang}wiki-latest-redirect.sql.gz
<LI>{lang}wiki-latest-pagelinks.sql.gz 
</UL>

<P>Onde {lang} é o código de duas letras da língua que pretende. (Nota: 'latest' pode ser uma data, p.ex. '20080509').</P> 

<P>Crie uma base de dados, ao entrar na consola do MySQL (escreva 'mysql') e use o comando: 
<PRE>
mysql> CREATE DATABASE 'wikipedia';
</PRE>

<P>(<B>Nota:</B> se optar por outro nome, precisa de <a href="<?php echo curPageURL(array('do'=>'devel-config'));?>">alterar as configurações do REMBRANDT)</A>.</P>
	
<P> Dê permissões de acesso para leitura ao utilizador '<B>wikipedia</B>', com a senha '<B>saskia</B>' (mais uma vez, estes parâmetros podem ser alterados), com o comando:</P>

<P><CODE>
	GRANT ALL PRIVILEGES ON wikipedia.* TO 'wikipedia'@'localhost' IDENTIFIED BY 'saskia';
</P>

<P>Agora, saia do MySQL e carregue os ficheiros da Wikipédia.</P>

<P>Uma sugestão para os carregar é:</P>
<P><CODE>(em Linux): zcat ptwiki-latest-category.sql.gz | mysql wikipedia -u wikipedia --password=saskia</CODE></P>
<P><CODE>(em Mac): gzcat ptwiki-latest-category.sql.gz | mysql wikipedia -u wikipedia --password=saskia</CODE></P>

<P>Por último, renomeie todas as tabelas de forma a ter um prefixo "<B>{lang}_</B>". Por exemplo:</P> 

<P><code>mysql saskia -u saskia --password=saskia -e "rename table page to pt_page"</code></P>
<P>ou, na consola do MySQL:</P>
<P><code>
mysql> rename table page to pt_page;
</CODE></P>
</blockquote>

<H3>3. Criar a Saskia DB</H3>
<div style="-moz-border-radius:4px 4px 4px 4px;	
	-webkit-border-radius:4px 4px 4px 4px;
	padding:10px;
	margin:10px;
	background-color:#ffffdd;"><B>Nota:</B> A Saskia DB é um repositório importante para armazenar documentos fonte e documentos anotados. Não é necessário para anotar, mas por enquanto é obrigatória a sua instalação, por causa de certas dependências, uma situação que mudará no futuro.
</div>

<P>Crie uma base de dados, ao entrar na consola do MySQL (escreva 'mysql') e use o comando: 
<PRE>
mysql> CREATE DATABASE 'saskia';
</PRE>

<P>(<B>Nota:</B> se optar por outro nome, precisa de <a href="<?php echo curPageURL(array('do'=>'devel-config'));?>">alterar as configurações do REMBRANDT)</A>.</P>
	
<P> Dê permissões de acesso para leitura ao utilizador '<B>saskia</B>', com a senha '<B>saskia</B>' (mais uma vez, estes parâmetros podem ser alterados), com o comando:</P>

<P><CODE>
	GRANT ALL PRIVILEGES ON saskia.* TO 'saskia'@'localhost' IDENTIFIED BY 'saskia';
</P>

<P>Agora, saia do MySQL e carregue as tabelas da Saskia DB, com o seguinte comando:</P>

<P><CODE>
mysql -u saskia saskia --password="saskia" &lt; db/Saskia_create_db_6.1.sql
</CODE></P>

<P>Agora, para criar o primeiro utilizador: edite o ficheiro  db/Saskia_create_user.sql e substitua o login, primeiro e último nome, e email se desejar (o resto são informações para modo servidor, irrelevante por agora). Introduza o commando: </P>

<P><CODE>
mysql -u saskia saskia --password="saskia" &lt; db/Saskia_create_user.sql
</CODE></P>

<P>A tabela 'user' tem os seguintes campos:</P>
<UL>
	<li><B>usr_login</B> - o Login</LI>
	<li><B>usr_enabled</B> - se o utilizador foi aceite e é válido, ou não. </LI>
	<li><B>usr_groups</B> - os grupos do utilizador (tal como no Unix), rodeados e separados por ';'</LI>
	<li><B>usr_superuser</B> - se o utilizador é um super-utilizador (tal como root em Unix)</LI>
	<li><B>usr_firstname</B> - O primeiro nome</LI>
	<li><B>usr_lastname</B> - O último nome </LI>
	<li><B>usr_email</B> - O email do utilizador</LI>
	<li><B>usr_password</B> - uma hash md5 da senha (só é preciso quando o serviço é usado via web)</LI>
	<li><B>usr_tmp_password</B> - uma hash md5 da senha temporária (só é preciso quando o serviço é usado via web)</LI>
	<li><B>usr_api_key</B> - uma chave API - 40 caracteres de [0-9a-f] (só é preciso quando o serviço é usado via web)</LI>
	<li><B>usr_tmp_api_key</B> uma chave temporária API - 40 caracteres de [0-9a-f] (só é preciso quando o serviço é usado via web)</LI>
	<li><B>usr_pub_key</B> - uma chave pública - 40 caracteres de [0-9a-f] (só é preciso quando o serviço é usado via web)</LI>
	<li><B>usr_max_number_collections</B> - número máximo de colecções que o utilizador pode criar.</LI>
	<li><B>usr_max_number_tasks</B> - número máximo de tarefas que o utilizador pode criar.</LI>
	<li><B>usr_max_docs_per_collection</B> - número máximo de documentos por colecção. </LI>
	<li><B>usr_max_daily_api_calls</B> - o número máximo de chamadas diárias pela API.</LI>
	<li><B>usr_current_daily_api_calls</B> - o número actual diário de chamadas API.</LI>
	<li><B>usr_total_api_calls</B> - o número total de chamadas API.</LI>
	<li><B>usr_date_last_api_call</B> - data da última chamada API, para fazer a recontagem diária.</LI>
</UL>

<P>De notar que há um campo, o <code>usr_id</code>, que é automaticamente preenchido com um número, que aumenta sequencialmente. Como tal, o primeiro utilizador terá o ID de 1. </P>

<P>
Agora, para criar a primeira colecção: edite o ficheiro db/Saskia_create_collection.sql e substitua o nome da colecção, língua e descrição. Introduza o comando:</P>

<P><CODE>
mysql -u saskia saskia --password="saskia" &lt; db/Saskia_create_collection.sql
</CODE></P>

<P>A tabela COLLECTION tem os seguintes campos:</P>

<UL>
	<li><B>col_name</B> - o nome da colecção</LI>
	<li><B>col_owner</B> - O ID do utilizador dono da colecção.</LI>
	<li><B>col_lang</B> - a língua da colecção.</LI>
	<li><B>col_permission</B> - permissões da colecção.</LI>
	<li><B>col_comment</B> - texto descritivo da colecção.</LI>
</UL>


Tal como na tabela <code>user</code>, há um campo, o <code>col_id</code>, que é automaticamente preenchido com um número, que aumenta sequencialmente. Como tal, a colecção terá o ID 1. De notar que o parâmetro <code>col_owner</code> é obrigatório, ou seja, é preciso ter pelo menos um utilizador válido. Neste caso, a colecção pertence ao utilizador com ID 1.
</P>

<P>
As permissões funcionam de forma semelhante ao Unix: as três primeiras letras controlam as permissões para o próprio utilizador, as três letras seguintes as permissões para utilizadores que partilhem o mesmo grupo, e as últimas três letras controlam as permissões para todos os utilizadores em geral.</P>

<P>A letra 'r' define a permissão para ler, a etra 'w' define a permissão para escrever, e a letra 'a' define a permissão para administrar. Por outras palavras, uma entrada 'rwar--r--' quer dizer que os documentos da colecção estão acessíveis para ler para todos, mas só o próprio utilizador é que pode escrever / adicionar mais documentos, e só ele é que pode administrar a colecção.
</P> 


<P>Pode verificar se foi realizado om sucesso, ao escrever na consola do MySQL:</P>
<PRE>
mysql> SELECT * from user;
mysql> SELECT * from collection;
</PRE>

<H3>4. Instalar o WEBSTORE</H3>

<P>O webstore é um gestor de documentos, que irá guardar os textos dos documentos-fonte e documentos anotados. O webstore pode ser descarregado em <a href="http://webstore.sourceforge.net/">http://webstore.sourceforge.net/</a>, mas também está incluído no pacote do REMBRANDT, em REMBRANDT_HOME/lib/webstore-dist.zip. O webstore é <B>obrigatório</B>, e importante para que o tamanho das colecções não prejudique o desempenho da DB naquilo que esta é importante, ou seja, na indexação e organização dos documentos.</P>

<P>Para instalar o Webstore, descompacte o ficheiro para uma directoria (/usr/local/webstore, /home/you/rembrandt/webstore, você escolhe), apenas garanta que a partição tem bastante espaço em disco e que a directoria tem as permissões necessárias, uma vez que o Webstore é um daemon que irá guardar e aceder a todos os documentos. </P>

<P>Seja qual for a sua directoria escolhida, vamos chamar de WEBSTORE_HOME. Adicione as seguintes variáveis de ambiente (certifique-se que elas serão sempre definidas, ao usar o ~/.bashrc ou o /etc/profile):</P> 

<PRE>
export WEBSTORE_HOME={WEBSTORE_HOME}
export WEBSTORE_CONFIG_FILE={WEBSTORE_CONFIG_FILE}
export WEBSTORE_DATA={WEBSTORE_DATA}
</PRE>

Onde:

<UL>
<LI>{WEBSTORE_HOME} - directoria onde os ficheiros do Webstore estão.
<LI>{WEBSTORE_CONFIG_FILE} - O caminho até ao ficheiro conf file. Use REMBRANDT_HOME/conf/webstore-conf.xml.
<LI>{WEBSTORE_DATA} - directoria onde os documentos serão guardados. Crie esta directoria, se necessário. Por exemplo, WEBSTORE_HOME/data
</UL>

<P>Agora adicione os executáveis Webstore ao PATH, por exemplo: </P>

<PRE>
  export PATH=$PATH:${WEBSTORE_HOME}/bin/linux
</PRE>

<P>Agora vamos verificar se está a funcionar. Escreva 'webstore' na linha de comandos, deve ver o seguinte:</P>

<pre>
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
</pre>

Não há volumes, há que os criar: um para os documentos fonte, outro para os documentos anotados. Para tal, escreva os comandos:

<PRE>
webstore -m doc
</PRE>

<P>Agora lance os volumes (importante: o webstore TEM de ter o webstore-conf.xml configurado com a variável de ambiente WEBSTORE_CONFIG_XML definida). Se escrever 'webstore', deverá ver: </P>

<PRE>
  local volume servers up: (volume ids)
  doc
</PRE>

<P>em vez de:</P>

<PRE>
  local volume servers up: (volume ids)
  (none)
</PRE>

<P>É muito importante que os volumes estejam a ser servidos nos portos 4444 e 4445. Se o sistema for reiniciado, eles precisam de ser novamente lançados. E verifique que, se o Webstore correr como utilizador normal, que os ficheiros de dados possuem as permissões certas.</P>


<H3>5. Configuração do CLASSPATH</H3>
<blockquote>

<P>Decida uma directoria base para o REMBRABDT (por exemplo, /usr/local/rembrandt, /home/you/rembrandt), e garanta que mantém a mesma estrutura de directorias. Vamos assumir que escolheu REMBRANDT_HOME.</P>

<P>Garanta que os ficheiros jar no REMBRANDT_HOME/lib estão referidos no CLASSPATH. Garanta também que o rembrandt.jar, que está em REMBRANDT_HOME, também esteja no CLASSPATH (movendo-o, por exemplo, para a directoria REMBRANDT_HOME/lib). Recomendamos que adicione as seguintes linhas no "~/.bashrc" (só para si) ou "/etc/profile" (para todos):</P>

<P><CODE>
export CLASSPATH=$CLASSPATH:`ls /home/you/rembrandt/lib/*.jar | tr "\n" ":"`
</CODE></P>

<P>O CLASSPATH é a variável de ambiente do Java que indica os sítios onde pode encontrar os pacotes jar necessários. Como tal, o REMBRANDT e os restantes pacotes jar precisam de estar numa directoria incluída no CLASSPATH.</P>

</blockquote>


<H3>6. Testar o REMBRADNT em modo simples </H3>

<P>Ok, estamos prontos para ver se o REMBRANDT está a funcionar. Basta escrever: </P>

<P><CODE>
echo "Rembrandt" | java rembrandt.bin.Rembrandt
</CODE></P>

<P>E o resutado será:</P>

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

<P>Repare que, se vir um parâmetro 'DB', isso quer dizer que a DBpedia foi usada para anotar a EM.</P>

<P>Dicas:</P>

<UL>
	<LI> As codificações de caracteres podem estragar a anotação. Pode pré-definir as codificações ao configurar os parâmetros:
  <UL><LI> rembrandt.input.encoding
  <LI>rembrandt.output.encoding
  <LI>rembrandt.err.encoding
</UL>
para se certificar que as codificações certas são usadas, nos streams stdin/stdout/stderr e nos ficheiros lidos/escritos.
<LI> 
Para ler um ficheiro, defina a variável <code>rembrandt.input.file</code>; para escrever num ficheiro, defina a variável <code>rembrandt.output.file</code>. Por omissão, o stdin e stdout são usados.
</UL>

<P>Por exemplo, o comando:</P>

<P><CODE>
java -Drembrandt.input.file=file1.txt 
-Drembrandt.output.file=file2.txt rembrandt.bin.Rembrandt
</CODE></P>

<P>lê um ficheiro chamado file1.txt, anota-o e escreve o resultado no ficheiro file2.txt, usando as codificações dadas pela variável de ambiente Java  <code>file.encoding</code>.</P>

<H3>7. Testar o REMBRANDT em modo BD</H3>

<H4>7.1 Ler os documentos </H4>

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
./script/tagDocs.sh
</CODE></P>

<P>Give the user/collection, an amount of documents to parse, and it will 
automatically search for untagged documents, tag them, and store them in the 
doc table. Likewise, check them with:</P>

<PRE>
mysql> select doc_id, doc_webstore from doc
+--------+---------------------------+
| doc_id | doc_webstore              |
+--------+---------------------------+
|      1 | 3813646601739006368@doc% |
+--------+---------------------------+
</PRE>

<P>and: </P>

<P><CODE>
webstore -r 3813646601739006368@doc%
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
./script/tagDocs.sh
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
|      1 | 3813646601739006368@doc% |
+--------+---------------------------+
</PRE>

<P>and: </P>

<P><CODE>
webstore -r 3813646601739006368@doc%
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
