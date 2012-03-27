var i18n = {'class':{'en':{
		'PERSON':{'INDIVIDUAL':[],'POSITION':[],'INDIVIDUALGROUP':[],'POSITIONGROUP':[],
		'MEMBER':[],'MEMBERGROUP':[],'PEOPLE':[]},
		'PLACE':{'PHYSICAL':{'ISLAND':[],'WATERCOURSE':[],'WATERMASS':[],'MOUNTAIN':[],'REGION':[],'PLANET':[]},
	 		 'HUMAN':{'CONSTRUCTION':[],'DIVISION':[],'STREET':[],'COUNTRY':[]},
			 'VIRTUAL':{'MEDIA':[],'SITE':[],'ARTICLE':[] } },
		'ORGANIZATION':{'ADMINISTRATION':[],'INSTITUTION':[],'COMPANY':[]},
		'MASTERPIECE':{'WORKOFART':[],'PLAN':[],'REPRODUCED':[]},
		'EVENT':{'PASTEVENT':[],'HAPPENING':[],'ORGANIZED':[]},
		'THING':{'CLASS':[],'CLASSMEMBER':[],'OBJECT':[],'SUBSTANCE':[]},	
		'TIME':{'GENERIC':[],'DURATION':[],'FREQUENCY':[],'CALENDAR':{'HOUR':[],'INTERVAL':[],'DATE':[]}},
		'VALUE':{'CURRENCY':[],'QUANTITY':[],'CLASSIFICATION':[]},
		'NUMBER':{'ORDINAL':[],'TEXTUAL':[],'NUMERAL':[],'NUMBER':[]},
		'ABSTRACTION':{'DISCIPLINE':[],'STATE':[],'IDEA':[],'NAME':[]}
		},'pt':{
		'PESSOA':{	'INDIVIDUAL':[],'CARGO':[],'GRUPOIND':[],'GRUPOCARGO':[],
			'MEMBRO':[],'GRUPOMEMBRO':[],'POVO':[]},
		'LOCAL':{'FISICO':{'ILHA':[],'AGUACURSO':[],'AGUAMASSA':[],'RELEVO':[],'REGIAO':[],'PLANETA':[]},
			 'HUMANO':{'CONSTRUCAO':[],'DIVISAO':[],'RUA':[],'PAIS':[]},
			 'VIRTUAL':{'COMSOCIAL':[],'SITIO':[],'ARTIGO':[] } },
		'ORGANIZACAO':{'ADMINISTRACAO':[],'INSTITUICAO':[],'EMPRESA':[]},
		'OBRA':{'ARTE':[],'PLANO':[],'REPRODUZIDA':[]},
		'ACONTECIMENTO':{'EFEMERIDE':[],'EVENTO':[],'ORGANIZADO':[]},
		'COISA':{'CLASSE':[],'MEMBROCLASSE':[],'OBJECTO':[],'SUBSTANCIA':[]},
		'TEMPO':{'GENERICO':[],'DURACAO':[],'FREQUENCIA':[],'TEMPO_CALEND':{'HORA':[],'INTERVALO':[],'DATA':[]}},
		'VALOR':{'MOEDA':[],'QUANTIDADE':[],'CLASSIFICACAO':[]},
		'NUMERO':{'ORDINAL':[],'TEXTUAL':[],'NUMERAL':[],'NUMERO':[]},
		'ABSTRACCAO':{'DISCIPLINA':[],'ESTADO':[],'IDEIA':[],'NOME':[]} }  },
	'admin':{'pt':'Administrar', 'en':'Admin'},
 	'admin-collection':{'pt':'Colecção', 'en':'Collection'},
	'admin-collections':{'pt':'Colecções', 'en':'Collections'},
	'admin-colstats':{'pt':'Estatísticas da colecção', 'en':'Collection statistics'},
	'admin-users':{'pt':'Utilizadores', 'en':'Users'},
	'admin-sdocs':{'pt':'Documentos fonte', 'en':'Source docs'},
	'admin-rdocs':{'pt':'Documentos anotados', 'en':'Tagged docs'},
	'admin-nes':{'pt':'EMs', 'en':'NEs'},
	'admin-entities':{'pt':'Entidades', 'en':'Entities'},
	'admin-geoscopes':{'pt':'Âmbitos geográficos', 'en':'Geoscopes'},
	'admin-subjects':{'pt':'Assuntos', 'en':'Subjects'},
	'admin-subjectgrounds':{'pt':'Ref. assuntos', 'en':'Subject ground'},
	'all':{'pt':'Todos', 'en':'All'},
	'ancestors':{'pt':'Antecessores', 'en':'Ancestors'},
	'api_key':{'pt':'Chave API', 'en':'API Key'},
	'ays':{'pt':'Tem a certeza que quer apagar ','en':'Are you sure you want to delete '},
	'areyousuredeleteAllNE':{'pt':'Tem a certeza que quer apagar a(s) EM', 'en':'Are you sure you want do delete NEs'},
	'areyousuredeleteNE':{'pt':'Tem a certeza que quer apagar a EM:','en':'Are you sure you want do delete NE'},
	'authenticate':{'pt':'Autenticar','en':'Authenticate'},
	'bademail':{'pt':'Email não válido. Por favor corrija.', 'en':'Invalid email. Please correct it.'},
	'back':{'pt':'Voltar atrás','en':'Go back'},
	'back-to-search-results':{'pt':'Voltar aos resultados','en':'Back to Search results'},
	'belongsto':{'pt':'Pertence a','en':'Belongs to'},
	'cache_stats':{'pt':'Estatísticas na cache','en':'Cache stats'},
 	'canadmin':{'pt':'Pode administrar', 'en':'Can admin'},
	'cancel':{'pt':'Cancelar', 'en':'Cancel'},
	'canadmin':{'pt':'Pode administrar', 'en':'Can admin'},
	'canread':{'pt':'Pode ler', 'en':'Can read'},
	'canwrite':{'pt':'Pode escrever', 'en':'Can write'},
	'captchamismatch':{'pt':'Falhou a pergunta do dia', 'en':'Failed the today question'},
	'category':{'pt':'Categoria', 'en':'Category'},
	'change':{'pt':'Alterar', 'en':'Change'},
	'changecollection':{'pt':'Mudar de colecção', 'en':'Change collection'},
	'changepassword':{'pt':'Mudar de senha', 'en':'Change password'},
	'changepassworddesc':{'pt':'Alterar a sua senha actual.', 'en':'Change your current password'},
	'children':{'pt':'Filhos', 'en':'Children'},
	'collection':{'pt':'Colecção', 'en':'Collection'},
	'collection-show':{'pt':'Detalhes da colecção', 'en':'Collection details'},
	'collection_list':{'pt':'Lista de colecções', 'en':'Collection list'},
	'collections':{'pt':'Colecções', 'en':'Collections'},
	'collection_created':{'pt':'Colecção criada', 'en':'Collection created'},
	'collection_deleted':{'pt':'Colecção apagada', 'en':'Collection deleted'},
	'collection_updated':{'pt':'Colecção actualizada', 'en':'Collection uppated'},
	'collection_permissions':{'pt':'Permissões em colecções','en':'Collection permissions'},
	'comment':{'pt':'Comentário', 'en':'Comment'},
	'content':{'pt':'Conteúdo', 'en':'Content'},
	'create':{'pt':'criar', 'en':'create'},
	'createalt':{'pt':'Criar ALT', 'en':'Create ALT'},
	'createne':{'pt':'Criar nova EM', 'en':'Create new NE'},
	'createnew':{'pt':'Criar nova', 'en':'Create new'},
	'createNEwithclass':{'pt':'Criar NE com classificação', 'en':'Create NE with classification'},
	'create_new_collection':{'pt':'Criar colecção nova','en':'Create new collection'},
	'create_new_geoscope':{'pt':'Criar novo âmbito geográfico','en':'Create new geoscope'},
	'create_new_entity':{'pt':'Criar nova entidade','en':'Create new entity'},
	'create_new_ne':{'pt':'Criar EM nova','en':'Create new NE'},
	'create_new_sdoc':{'pt':'Criar novo documento fonte','en':'Create new source doc'},
	'create_new_user':{'pt':'Criar utilizador novo','en':'Create new user'},
	'create_new_subject':{'pt':'Criar novo assunto','en':'Create new subject'},
	'create_new_subjectground':{'pt':'Criar nova referenciação de assunto','en':'Create new subject ground'},
	'current_daily_api_calls':{'pt':'Num. de chamadas API hoje', 'en':'Nr. of API calls today'},
	'date_created':{'pt':'Criado em', 'en':'Date created'},
	'date_tagged':{'pt':'Anotado em', 'en':'Date tagged'},
	'dbpedia_class':{'pt':'Classe DBpedia','en':'DBpedia class'},
	'dbpedia_resource':{'pt':'Recurso DBpedia','en':'DBpedia resource'},	
	'default_text':{'pt':'(Clique para alterar)', 'en':'(Click here to add text)'},
	'delete':{'pt':'Apagar', 'en':'Delete'},
	'deleted':{'pt':'Apagado', 'en':'Deleted'},
	'deleteall':{'pt':'Apagar todas', 'en':'Delete all'},
	'delete_collection':{'pt':'Apagar colecção','en':'Delete collection'},
	'delete_geoscope':{'pt':'Apagar âmbito geográfico','en':'Delete geoscope'},
	'delete_entity':{'pt':'Apagar entidade','en':'Delete entity'},
	'delete_subject':{'pt':'Apagar assunto','en':'Delete subject'},
	'delete_subjectground':{'pt':'Apagar referenciação de assunto','en':'Delete subject ground'},
	'delete_ne':{'pt':'Apagar EM','en':'Delete NE'},
	'delete_user':{'pt':'Apagar utilizador','en':'Delete user'},
	'detail':{'pt':'Detalhes', 'en':'Details'},
	'document':{'pt':'Documento', 'en':'Document'},
	'editable':{'pt':'Editável', 'en':'Editable'},
	'editon':{'pt':'Modo edição', 'en':'Edit mode'},
	'enabled':{'pt':'Activo', 'en':'Enabled'},
	'entity':{'pt':'Entidade', 'en':'Entity'},
	'entity_created':{'pt':'Entidade criada', 'en':'Entity created'},
	'entity_deleted':{'pt':'Entidade apagada', 'en':'Entity deleted'},
	'entity_updated':{'pt':'Entidade actualizada', 'en':'Entity upated'},
	'entity_list':{'pt':'Lista de entidades', 'en':'Entity list'},
	'entities':{'pt':'entidades', 'en':'entities'},
	'email':{'pt':'Email', 'en':'Email'},
	'error':{'pt':'Erro', 'en':'Error'},
	'errormessage':{'pt':"Ocorreu um erro. Inadmissível. Por favor, reclame.", 'en':"An error ocurred. Unforgiven. Please complain to me"},
	'explanation':{'pt':'Explicação','en':'Explanation'},
	'filterby':{'pt':'Filtrar por','en':'Filter by'},
	'firstname':{'pt':"Primeiro nome", 'en':"Firstname"},
	'for':{'pt':"para", 'en':"for"},
	'forgotpassword':{'pt':"Esqueceu a senha?", 'en':"Forgot password?"},
	'forgotpasswordentermail':{'pt':'Escreva o seu email para receber uma nova senha', 'en':'Write your email so that we can send you a new password'},
	'formmismatch':{'pt':"Faltam elementos no formulário", 'en':"Missing form elements"},
	'geonetpt02':{'pt':'GeoNetPT 02','en':'GeoNetPT 02'},	
	'geoscope':{'pt':"Âmbito geográfico", 'en':"Geoscope"},
	'geoscopes':{'pt':"âmbitos geográficos", 'en':"geoscopes"},
	'geoscope_created':{'pt':'Âmbito geográfico criado', 'en':'Geoscope created'},
	'geoscope_deleted':{'pt':'Âmbito geográfico apagado', 'en':'Geoscope deleted'},
	'geoscope_updated':{'pt':'Âmbito geográfico actualizado', 'en':'Geoscope uppated'},
	'geoscope_list':{'pt':'Lista de âmbitos geográficos', 'en':'Geoscope list'},
	'go':{'pt':"Ver", 'en':"Go"},
	'group':{'pt':"Grupo", 'en':"Group"},
	'groups':{'pt':"Grupos", 'en':"Groups"},
	'guest':{'pt':"Convidado", 'en':"Guest"},
	'having':{'pt':'contendo', 'en':'having'},
	'hide':{'pt':'Esconder', 'en':'Hide'},
	'hidealltooltips':{'pt':'Esconder balões', 'en':'Hide tooltips'}, 
	'hideallrelations':{'pt':'Esconder relações', 'en':'Hide relations'}, 
	'hiderelations':{'pt':'Esconder relações', 'en':'Hide relations'}, 
	'homepage':{'pt':'Página inicial', 'en':'Home page'}, 
	'id':{'pt':'ID', 'en':'ID'}, 
	'invalid_email':{'pt':'Email inválido', 'en':'Invalid email'}, 
	'keep':{'pt':'manter', 'en':'keep'}, 
	'email':{'pt':'Email', 'en':'Email'},
	'enterlogin':{'pt':'Entrar', 'en':'Login'},
	'lastname':{'pt':"Apelido", 'en':"Lastname"},
	'lang':{'pt':'Idioma', 'en':'Language'}, 
	'limit':{'pt':'Limite', 'en':'Limit'}, 
	'login':{'pt':'Entrar', 'en':'Login'}, 
	'loginchars':{'pt':'Por favor, use só números e letras sem acentos ( ou _ ) no nome da conta.', 'en':'Please use numbers and simple letters (or _ ) on the user name.'}, 
	'loginname':{'pt':'Nome da conta', 'en':'User name'}, 
	'logintoosmall':{'pt':'Nome da conta muito pequeno. Use  6 ou mais caracteres', 'en':'User name too small. Please use 6 or more characters'}, 
	'loginuser':{'pt':'Por favor, entre o seu nome de utilizador e senha', 'en':'Please insert your username and password'}, 
	'logoutareyousure':{'pt':'Tem a certeza que quer sair do utilizador', 'en':'Are you sure you want to logout from user'}, 
	'logout':{'pt':'Sair', 'en':'Logout'}, 
	'logoutdesc':{'pt':'Sair do utilizador', 'en':'Logout from user'}, 
	'max_number_collections':{'pt':'Num. máx. de colecções', 'en':'Max. nr. of collections'},
	'max_number_tasks':{'pt':'Num. máx. de tarefas', 'en':'Max. nr. of tasks'},
	'max_number_docs_per_collection':{'pt':'Num. máx. de docs por colecção', 'en':'Max. nr. of docs per collection'},
	'max_daily_api_calls':{'pt':'Num. máx. de chamadas diárias à API', 'en':'Max. nr. of API daily calls'},
	'metadata':{'pt':'Metadados', 'en':'metadata'},
	'mode':{'pt':'Modo', 'en':'Mode'},
	'moreinfo':{'pt':'Mais informação', 'en':'More info'},
	'name':{'pt':'Nome', 'en':'Name'},
	'ne':{'pt':'EM', 'en':'NE'},
	'nes':{'pt':'EMs', 'en':'NEs'},
	'ne_created':{'pt':'EM criada', 'en':'NE created'},
	'ne_deleted':{'pt':'EM apagada', 'en':'NE deleted'},
	'ne_updated':{'pt':'EM actualizada', 'en':'NE uppated'},
	'ne_list':{'pt':'Lista de EM', 'en':'NEs list'},
	'ne_category':{'pt':'Categoria', 'en':'Category'},
	'ne_type':{'pt':'Tipo', 'en':'Type'},
	'ne_subtype':{'pt':'Subtipo', 'en':'Subtype'},
	'nedetails':{'pt':'Detalhes da EM', 'en':'NE details'},
	'neighbors':{'pt':'Vizinhos', 'en':'Neighbors'},
	'newne':{'pt':'Nova EM', 'en':'New NE'},
	'newclass':{'pt':'Classificação nova', 'en':'New classification'},
	'newuser':{'pt':'Novo utilizador', 'en':'New user'},
	'newpassword':{'pt':'Nova senha', 'en':'New password'},
	'no':{'pt':'Não', 'en':'No'},
	'no-abstract-available':{'pt':'Resumo não disponível', 'en':'Abstract not available'},
	'no-info-avaliable':{'pt':'Sem mais informação','en':'No info available'},
	'noNEcreationBecause':{'pt':'Não é possível criar a EM porque','en':'It\'s not possible to create a NE because'},
	'noNEfound':{'pt':'Não se encontraram EM', 'en':'No NEs found.'},
	'norelations':{'pt':'Não há relações', 'en':'No relations found.'},
	'of':{'pt':'de', 'en':'of'},
	'offset':{'pt':'Deslocamento', 'en':'Offset'}, 
	'oldclass':{'pt':'Classificação antiga', 'en':'Old classification'},
	'oldpassword':{'pt':'Senha antiga', 'en':'Old password'},
	'originalid':{'pt':'ID original', 'en':'Original ID'},
	'OK':{'pt':'OK', 'en':'OK'},
	'other':{'pt':'Outro', 'en':'Other'},
	'owns':{'pt':'Pertence-lhe', 'en':'Owns'},
	'owner':{'pt':'Dono', 'en':'Owner'},
	'page':{'pt':'página', 'en':'page'},
	'parent':{'pt':'Pai', 'en':'Parent'},
	'password':{'pt':'Senha', 'en':'Password'},
	'passwordmismatch':{'pt':'Senhas não coincidem.', 'en':'Passwords don\'t match.'},
	'passwordtoosmall':{'pt':'Senha é muito pequena. Use 6 ou mais caracteres.', 'en':'Password is too small. Please use 6 or more characters.'},
	'permissions':{'pt':'Permissões', 'en':'Permissions'},
	'perpage':{'pt':'por página', 'en':'per page'},
	'pickclassforNE':{'pt':'Escolha a classe para a EM', 'en':'Pick classification for NE'},
	'place':{'pt':'Local', 'en':'Place'},
	'pressescape':{'pt':'Carregue no Esc para sair desta janela.','en':'Press ESC to quit this window.'},	
	"prev":{"en":"&lg; Previous", "pt":"&lt; Anterior"},
	"priority":{"en":"Priority", "pt":"Prioridade"},
	"processable":{"pt":"Processável", "en":"Processable"},
	"next":{"en":"Next &gt;", "pt":"Seguinte &gt;"},
	'read':{'pt':'Leitura', 'en':'Read'},
	'rdoc':{'pt':'Documento anotado', 'en':'Tagged doc'},
	'rdocs':{'pt':'Documentos anotados', 'en':'Tagged docs'},
	'rdoc_created':{'pt':'Documento anotado criado', 'en':'Tagged document created'},
	'rdoc_deleted':{'pt':'Documento anotado apagado', 'en':'Tagged document deleted'},
	'rdoc_updated':{'pt':'Documento anotado actualizado', 'en':'Tagged document upated'},
	'rdoc_import':{'pt':'Anotar documentos automaticamente', 'en':'Annotate documents automatically'},
	'rdoc_export':{'pt':'Exportar documentos anotados', 'en':'Export tagged documents'},
	'rdoc_list':{'pt':'Lista de documentos anotados', 'en':'Tagged document list'},
	'recoverpassword':{'pt':'Recuperar senha','en':'Recover password'},
	'refresh':{'pt':'Actualizar','en':'Refresh'},
	'refreshing_collection':{'pt':'Actualizando as estatísticas...','en':'Refreshing stats...'},
	'register':{'pt':'registar', 'en':'register'},
	'rememberme':{'pt':'Lembrar-se de mim', 'en':'Remember me'},
	'repeatpassword':{'pt':'Repetir senha', 'en':'Repeat password'},
	"results":{"en":"Results", "pt":"Resultados"},
	'retry':{'pt':'Voltar a tentar', 'en':'Retry'},
	'save':{'pt':'Guardar', 'en':'Save'},
	'saving':{'pt':'Guardando...', 'en':'Saving...'},
	'select_text':{'pt':'Escolha um valor', 'en':'Select a value'},
	'sdoc':{'pt':'Documento fonte', 'en':'Source doc'},
	'sdocs':{'pt':'Documentos fonte', 'en':'Source docs'},
	'sdoc_created':{'pt':'Documento fonte criado', 'en':'Source document created'},
	'sdoc_deleted':{'pt':'Documento fonte apagado', 'en':'Source document deleted'},
	'sdoc_updated':{'pt':'Documento fonte actualizado', 'en':'Source document upated'},
	'sdoc_import':{'pt':'Importar documentos fonte', 'en':'Import source documents'},
	'sdoc_export':{'pt':'Exportar documentos fonte', 'en':'Export source documents'},
	'sdoc_list':{'pt':'Lista de documentos fonte', 'en':'Source document list'},
	'search':{'pt':'Pesquisa', 'en':'Search'},
	'search-results':{'pt':'Resultados da pesquisa', 'en':'Search results'},
	'search-result-list':{'pt':'Lista de resultados', 'en':'Result list'},
	'selectionTermError':{'pt':'os termos seleccionados não são contíguos.', 'en':'the selected terms are not contiguous'},
	'selectionSentenceError':{'pt':'a EM só pode estar numa única frase', 'en':'NE must be part of a single sentence'},
	'selecton':{'pt':'Modo selecção', 'en':'Select mode'}, 
	'sentence':{'pt':'Frase', 'en':'Sentence'}, 
	'service-unavailable-sorry':{'pt':'Serviço não disponível, pedimos desulpas pelo sucedido',
	 'en':'Service not available, we apologize for that'}, 
	'settings':{'pt':'Definições', 'en':'Settings'}, 
	'show':{'pt':'Mostrar', 'en':'Show'}, 
	'showing':{'pt':'Mostrando', 'en':'Showing'}, 
	'showrelations':{'pt':'Mostrar relações', 'en':'Show relations'},
	'showallrelations':{'pt':'Mostrar relações', 'en':'Show relations'}, 
	'showalltooltips':{'pt':'Mostrar balões', 'en':'Show tooltips'}, 
	'siblings':{'pt':'Irmãos', 'en':'Siblings'},
	'source_code':{'pt':'Código fonte', 'en':'Source code'}, 
	'stats':{'pt':'Estatísticas','en':'Statistics'},
	'status':{'pt':'Estado','en':'Status'},
	'subject':{'pt':'Assunto', 'en':'Subject'}, 
	'subjects':{'pt':'assuntos', 'en':'subjects'}, 
	'subject_created':{'pt':'Assunto criado', 'en':'Subject created'},
	'subject_deleted':{'pt':'Assunto apagado', 'en':'Subject deleted'},
	'subject_updated':{'pt':'Assunto actualizado', 'en':'Subject upated'},
	'subject_list':{'pt':'Lista de assuntos', 'en':'Subject list'},
	'subjectground':{'pt':'Assunto referenciado', 'en':'Subject ground'}, 
	'subjectgrounds':{'pt':'assuntos referenciados', 'en':'subject grounds'}, 
	'subjectground_created':{'pt':'Assunto referenciado criado', 'en':'Subject ground created'},
	'subjectground_deleted':{'pt':'Assunto referenciado apagado', 'en':'Subject ground deleted'},
	'subjectground_updated':{'pt':'Assunto referenciado actualizado', 'en':'Subject ground upated'},
	'subjectground_list':{'pt':'Lista de assuntos referenciados', 'en':'Subject ground list'},
	'subtype':{'pt':'Subtipo', 'en':'Subtype'}, 
	'superuser':{'pt':'Super-utilizador', 'en':'Superuser'}, 
	'syncable':{'pt':'Sincronizável', 'en':'Syncable'}, 
	'tag':{'pt':'Etiqueta', 'en':'Tag'},
	'task':{'pt':'Tarefa', 'en':'Task'},
	'task_scope':{'pt':'Âmbito da tarefa', 'en':'Task scope'},
	'task_scope_bat':{'pt':'Batch', 'en':'Batch'},
	'task_scope_srv':{'pt':'Servidor', 'en':'Server'},
	'task_type_s2r':{'pt':'Anotar documento(s) fonte', 'en':'Tag source doc(s)'},
	'task_type_r2p':{'pt':'Sincronizar documentos anotados', 'en':'Sync tagged docs'},
	'task_type_geo':{'pt':'Gerar assinaturas geográficas', 'en':'Generate geographic signatures'},
	'task_type_tim':{'pt':'Gerar assinaturas temporais', 'en':'Gerar assinaturas temporais'},
	'task_persistence':{'pt':'Duração', 'en':'Duration'},
	'task_persistence_tmp':{'pt':'Temporário', 'en':'Temporary'},
	'task_persistence_prm':{'pt':'Permanente', 'en':'Permanent'},
	'tasks':{'pt':'Tarefas', 'en':'Tasks'},
	'task_created':{'pt':'Tarefa criada', 'en':'Task created'},
	'task_deleted':{'pt':'Tarefa apagada', 'en':'Task deleted'},
	'task_updated':{'pt':'Tarefa actualizada', 'en':'Task upated'},
	'term':{'pt':'Termo', 'en':'Term'},
	'terms':{'pt':'Termos', 'en':'Terms'},
	'total_api_calls':{'pt':'Total de chamadas API', 'en':'Total API calls'},
	'type':{'pt':'Tipo', 'en':'Type'},
	'unknownClass':{'pt':'EM', 'en':'NE'},
	'user':{'pt':'Utilizador', 'en':'User'},
	'user_list':{'pt':'Lista de utilizadores', 'en':'User list'},
	'user_created':{'pt':'Utilizador criado', 'en':'User created'},
	'user_deleted':{'pt':'Utilizador apagado', 'en':'User deleted'},
	'user_updated':{'pt':'Utilizador actualizado', 'en':'User upated'},
	'user_settings':{'pt':'Definições do utilizador', 'en':'User settings'},
	'users':{'pt':'utilizadores', 'en':'users'},
	'usermenu':{'pt':'Menu do utilizador','en':'User menu'},
	'version':{'pt':'Versão', 'en':'Version'},
	'viewon':{'pt':'Modo normal', 'en':'Normal mode'},
	'view':{'pt':'Ver', 'en':'View'},
	'webstore':{'pt':'WebStore','en':'WebStore'},
	'welcome':{'pt':'Bem-vindo','en':'Welcome'},
	'wait-please':{"pt":"A processar... aguarde, por favor.", "en":"Processing... please wait."},
	'whatdayistoday':{'pt':'Que dia é hoje','en':'What day is today'},
	'wikipedia_category':{'pt':'Categoria Wikipédia','en':'Wikipedia Category'},
	'withclass':{'pt':'com classificação','en':'with classification'},
	'woeid':{'pt':'WOEID','en':'WOEID'},
	'woeid_type':{'pt':'Tipo WOEID','en':'WOEID Type'},
	'woeid_place':{'pt':'Local WOEID','en':'WOEID Place'},
	'woeid_parent':{'pt':'Pai WOEID','en':'WOEID Parent'},
	'woeid_ancestors':{'pt':'Antecessores WOEID','en':'WOEID Ancestors'},
	'woeid_belongsto':{'pt':'Pertence a WOEID','en':'WOEID belongsto'},
	'woeid_neighbors':{'pt':'Vizinhos WOEID','en':'WOEID neighbors'},
	'woeid_siblings':{'pt':'Irmãos WOEID','en':'WOEID siblings'},
	'woeid_children':{'pt':'Filhos WOEID','en':'WOEID children'},
	'write':{'pt':'Escrita','en':'Write'},
	'yes':{'pt':'Sim', 'en':'Yes'}
}

