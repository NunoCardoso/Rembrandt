
package saskia.util

class I18n {
    static I18n _this
    HashMap statstitle 
    HashMap statslabel 
    HashMap collection 
    HashMap servermessage
    HashMap dateformat
    HashMap admin
    
    public static I18n newInstance() {
		if (!_this) _this = new I18n()
		return _this
    }
    
 // name of static variables canNOT have uppercase letters
     I18n() {
        
    
     this.dateformat = ["pt":"dd/MMM/yyyy","en":"yyyy/MMM/dd"]
 
     this.statstitle = [
	 	"differentrepresentations":["pt":"Diferentes representações", "en":"Different representations"],            
	 	"docdetails":["pt":"Detalhes do documento", "en":"Document details"],
		"doc_geo_signature":["pt":"Assinatura geográfica", "en":"Geographic signature"],
 		"doc_map":["pt":"Mapa", "en":"Map"],
		"doc_time_signature":["pt":"Assinatura temporal", "en":"Time signature"],
		"NEs":["pt":"Entidades mencionadas","en":"Named-Entities"],
	 	"neandsentences":["pt":"Frases e EM", "en":"Sentences and NEs"],
	 	"NEondisplay":["pt":"Entidade em destaque","en":"NE on display"],
	 	"relatedNEs":["pt":"Entidades relacionadas", "en":"Related NEs"],
	 	"statsforcollection":["pt":"Estatísticas para a colecção","en":"Statistics for collection"],
	 	"statsfordoc":["pt":"Estatísticas para o documento", "en":"Statistics for document"],
	 	"statsforne":["pt":"Estatísticas para a EM", "en":"Statistics for NE"],
	 	"top10nes":["pt":"10 EM mais frequentes", "en":"Top 10 NEs"],
    ]

    this.statslabel = [
      "generatedin":["pt":"Gerado em", "en":"Generated in"], 
      "categorydistribution":["pt":"Distribuição de categorias","en":"Category distribution"],
      "DBpediaclass":["pt":"Classe DBpedia", "en":"DBpedia class"],
      "DBpediaresource":["pt":"Recurso DBpedia", "en":"DBpedia resource"],
      "date_created":["pt":"Data de criação", "en":"Creation date"],
      "date_tagged":["pt":"Data da anotação","en":"Tagged date"],
      "detailson":["pt":"Detalhes sobre", "en":"Details on"],
      "docswithmorereferencesto":["pt":"Documentos com mais referências a", "en":"Docs with more references to"],
      "mostrelatedNEs":["pt":"EM mais relacionadas", "en":"most related NEs"],
      "NE":["pt":"EM","en":"NE"],
      "NEsPerDoc":["pt":"N.º de EM por documento","en":"Nr. of NEs per document"],
      "noresource":["pt":"Sem recurso", "en":"No resource"],
      "noclass":["pt":"Sem classe", "en":"No class"],
      "numberdocs":["pt":"N.º de documentos","en":"Nr. of documents"],
      "numberneindoctitle":["pt":"Número de EM no título (documento)", "en":"Number of NEs in title (document)"],
      "numberneindocbody":["pt":"Número de EM no corpo (documento)", "en":"Number of NEs in body (document)"],
      "numberneinpooltitle":["pt":"Número de EM no título (piscina)", "en":"Number of NEs in title (pool)"],
      "numberneinpoolbody":["pt":"Número de EM no corpo (piscina)", "en":"Number of NEs in body (pool)"],
      "numberrembrandteddocs":["pt":"Número de documentos anotados", "en":"Number of tagged documents"],
      "numbersentencestitle":["pt":"Número de frases no título", "en":"Number of sentences in title"],
      "numbersentencesbody":["pt":"Número de frases no corpo", "en":"Number of sentences in body"],
      "numbersourcedocs":["pt":"Número de documentos fonte", "en":"Number of source documents"],
      "numbersyncdocs":["pt":"Número de documentos sincronizados", "en":"Number of synced documents"],
      "oldestdoc":["pt":"Documento mais antigo", "en":"Oldest document"],
      "newestdoc":["pt":"Documento mais recente", "en":"Newest document"],
      "recentsentenceswith":["pt":"Frases recentes com a EM", "en":"Recent sentences with NE"],
      "searchne":["pt":"Procurar EM", "en":"Search NEs"],
      "tag_version":["pt":"Anotada com REMBRANDT v.","en":"Tagged with REMBRANDT v."],
      "timelineofthe":["pt":"Linha do tempo das", "en":"Timeline of the"],
      "totalDistinctNEs":["pt":"Total de EM distintas", "en":"Distinct NE total"],
      "totalNEs":["pt":"Total de EM", "en":"NE total"]
   ] 

   this.servermessage = [
      "action_unknown":["pt":"Acção desconhecida","en":"Action unknown"],
      "api_key_limit_exceeded":["pt":"Limite de acesssos via API excedido", "en":"API access limit exceeded"],
      "collection_already_exists":["pt":"A colecção já existe!", "en":"Collection already exists!"],                   
      "collection_number_limit_reached":["pt":"Limite de colecções atingido","en":"Collection number limit reached"],
      "collection_not_found":["pt":"Colecção não encontrada", "en":"Collection not found"],
      "document_not_found":["pt":"Documento não encontrado", "en":"Document not found"],
      "email_not_found":["pt":"Email não encontrado", "en":"Email not found"],
      'error':["pt":"Erro","en":"Error"],           
      'error_deleting_collection':["pt":"Erro ao apagar a colecção","en":"Error deleting collection"],           
      'error_deleting_user':["pt":"Erro ao apagar o utilizador","en":"Error deleting user"],           
      'error_deleting_sdoc':["pt":"Erro ao apagar o documento fonte","en":"Error deleting source document"],           
      'error_deleting_rdoc':["pt":"Erro ao apagar o documento anotado","en":"Error deleting tagged document"],           
    	'error_deleting_ne':["pt":"Erro ao apagar a NE","en":"Error deleting NE"],           
      'error_deleting_entity':["pt":"Erro ao apagar a entidade","en":"Error deleting entity"],           
      'error_deleting_geoscope':["pt":"Erro ao apagar o âmbito geográfico","en":"Error deleting geoscope"],           
      'error_deleting_subject':["pt":"Erro ao apagar o assunto","en":"Error deleting subject"],           
      'error_deleting_subjectground':["pt":"Erro ao apagar o assunto referenciado","en":"Error deleting subject ground"],           
      'error_deleting_task':["pt":"Erro ao apagar tarefa","en":"Error deleting task"],           

      'error_getting_col':["pt":"Erro ao obter a colecção","en":"Error getting collection"],           
      'error_getting_col_list':["pt":"Erro ao obter lista de colecções","en":"Error getting collection list"],           
      'error_getting_task':["pt":"Erro ao obter a tarefa","en":"Error getting task"],           
      'error_getting_task_list':["pt":"Erro ao obter lista de tarefas","en":"Error getting task list"],           
      'error_getting_rdoc':["pt":"Erro ao obter o documento anotado","en":"Error getting tagged document"],           
      'error_getting_rdoc_list':["pt":"Erro ao obter lista de documentos anotados","en":"Error getting tagged document list"],           
     	'error_getting_rdoc_metadata':["pt":"Erro ao obter matadata do documento anotado","en":"Error getting metadata from tagged document"],           
      'error_getting_sdoc':["pt":"Erro ao obter o documento fonte","en":"Error getting source document"],           
      'error_getting_sdoc_list':["pt":"Erro ao obter lista de documentos fonte","en":"Error getting source document list"],           
      'error_getting_user':["pt":"Erro ao obter o utilizador","en":"Error getting user"],           
      'error_getting_user_list':["pt":"Erro ao obter lista de utilizadores","en":"Error getting user list"],           
      'error_getting_ne':["pt":"Erro ao obter EM","en":"Error getting NE"],           
      'error_getting_ne_list':["pt":"Erro ao obter lista de EMs","en":"Error getting NE list"],           
      'error_getting_entity':["pt":"Erro ao obter entidade","en":"Error getting entity"],           
      'error_getting_entity_list':["pt":"Erro ao obter lista de entidades","en":"Error getting entity list"],           
      'error_getting_geoscope':["pt":"Erro ao obter âmbitos geográficos","en":"Error getting geoscope"],           
      'error_getting_geoscope_list':["pt":"Erro ao obter lista de âmbitos geográficos","en":"Error getting geoscope list"],           
      'error_getting_subject':["pt":"Erro ao obter assuntos","en":"Error getting subjects"],           
      'error_getting_subject_list':["pt":"Erro a obter lista de assuntos","en":"Error getting subject list"],           
      'error_getting_subjectground':["pt":"Erro a obter asssuntos referenciados","en":"Error getting subject ground"],   
      'error_getting_subjectground_list':["pt":"Erro a obter lista de asssuntos referenciados","en":"Error getting subject ground list"],           
      'error_updating_collection':["pt":"Erro durante a modificação da colecção","en":"Error updating collection"],           
      'error_updating_task':["pt":"Erro durante a modificação da tarefa","en":"Error updating task"],           
      'error_updating_rdoc':["pt":"Erro durante a modificação de documento","en":"Error updating document"],           
      'error_updating_ne':["pt":"Erro durante a modificação de EM","en":"Error updating NE"],           
      'error_updating_user':["pt":"Erro durante a modificação de utilizador","en":"Error updating user"],           
      'error_updating_entity':["pt":"Erro durante a modificação de entidade","en":"Error updating entity"],           
      'error_updating_geoscope':["pt":"Erro durante a modificação de âmbito geográfico","en":"Error updating geoscope"],           
      'error_updating_subject':["pt":"Erro durante a modificação de assunto","en":"Error updating subject"],           
      'error_updating_subjectground':["pt":"Erro durante a modificação de assunto referenciado","en":"Error updating subject ground"],           
      'error_creating_collection':["pt":"Erro na criação de colecção","en":"Error creating collection"],
      'error_creating_task':["pt":"Erro na criação de tarefa","en":"Error creating tarefa"],
      'error_creating_user':["pt":"Erro na criação de utilizador","en":"Error creating user"],
      'error_creating_ne':["pt":"Erro na criação de EM","en":"Error creating NE"],
      'error_creating_entity':["pt":"Erro na criação de entidade","en":"Error creating entity"],
      'error_creating_geoscope':["pt":"Erro na criação de âmbito geográfico","en":"Error creating geoscope"],
      'error_creating_subject':["pt":"Erro na criação de assunto","en":"Error creating subject"],
      'error_creating_subjectground':["pt":"Erro na criação de assunto referenciado","en":"Error creating subject ground"],
         
      "insufficient_permissions":["pt":"Permissões insuficientes", "en":"Insuficcient permissions"],
      "invalid_api_key":["pt":"Chave API inválida", "en":"Invalid API key"],
      "invalid_id":["pt":"ID inválido", "en":"Invalid ID"],
		"max_number_documents_per_collection_reached":["pt":"Número máximo de documentos por colecção atingido", "en":"Max number of docs per collection reached"],
		"max_number_tasks_reached":["pt":"Número máximo de tarefas atingido", "en":"Max number of tasks reached"],
      "ne_already_exists":["pt":"NE já existe", "en":"NE already exists"],
      "ne_not_found":["pt":"NE não encontrada", "en":"NE not found"],
      "no_api_key":["pt":"Sem chave API", "en":"No API key"],
      "no_collection_admin":["pt":"Não é administrador da colecção", "en":"No permissions for collection admin"],
    
      "no_action":["pt":"Sem acção", "en":"No action"],
      "no_superuser":["pt":"Sem permissões para isso", "en":"No permissions for that"],
      "not_enough_vars":["pt":"Variáveis insuficientes", "en":"Not enough vars"],
      "no_collection_found":["pt":"Colecção não encontrada", "en":"Collection not found"],                   
      "no_user_found":["pt":"Utilizador não encontrado", "en":"User not found"], 
      "ok":["pt":"OK", "en":"OK"],
      "old_password_dont_match":["pt":"A senha antiga está incorrecta", "en":"Old password don't match"],
      "password_and_api_key_changed":["pt":"Senha e chave API alterada","en":"Password and API key changed"],
      "password_changed":["pt":"Senha alterada","en":"Password changed"],
      "subject_already_exists":["pt":"O assunto já existe!", "en":"Subject already exists!"],  
      "subjectground_already_exists":["pt":"O assunto referenciado já existe!", "en":"Subject ground already exists!"],  
    	"subject_not found":["pt":"Assunto não encontrado", "en":"Subject not found"],  
      "subjectground_not_found":["pt":"Assunto referenciado não encontrado", "en":"Subject ground not found"],  
    	"task_not_found":["pt":"Tarefa não encontrada", "en":"Task not found"],  
      "user_already_exists":["pt":"O utilizador já existe!", "en":"User already exists!"],  
      "user_not_allowed_to_create_more_collection":["pt":"Utilizador não tem permissão para criar mais colecções", "en":"User not allowed to create more collections"], 
      "user_not_allowed_to_read_from_collection":["pt":"Utilizador não tem permissão para ler esta colecção", "en":"User not allowed to read from this collection"], 
      "user_enabled":["pt":"Utilizador autenticado","en":"User enabled"],
      "user_login_already_taken":["pt":"Este nome de utilizador já não está disponível. Por favor, escolha outro", "en":"User name already taken. Please choose another"], 
      "user_not_enabled":["pt":"Utilizador não activo", "en":"User not enabled"],
      "user_not_found":["pt":"Utilizador não encontrado", "en":"User not found"],
      "user_not_confirmed":["pt":"Utilizador ainda não confirmou registo. Verifique o seu email", "en":"User hasn't confirmed the registration yet. Check your email"],  
      "wrong_password":["pt":"Senha não está correcta", "en":"Wrong password"]
  ]	
    
   this.collection = [
     "author":["pt":"Autor", "en":"Author"],                          
     "category":["pt":"Categoria", "en":"Category"],
     "pagelink":["pt":"Ligação à página", "en":"Page Link"],
     "rssfeedlink":["pt":"Ligação ao RSS", "en":"RSS Link"],
     "source":["pt":"Fonte", "en":"Source"]
    ]
 }
}