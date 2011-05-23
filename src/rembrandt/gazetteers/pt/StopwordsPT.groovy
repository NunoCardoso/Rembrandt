/** This file is part of REMBRANDT - Named Entity Recognition Software
 *  (http://xldb.di.fc.ul.pt/Rembrandt)
 *  Copyright (c) 2008-2009, Nuno Cardoso, University of Lisboa and Linguateca.
 *
 *  REMBRANDT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  REMBRANDT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with REMBRANDT. If not, see <http://www.gnu.org/licenses/>.
 */
 
package rembrandt.gazetteers.pt

/**
 * @author Nuno Cardoso
 * This class stores stopword information.
 */
class StopwordsPT {
    
     static public List<String> stopwordNEs = [
'A','Ai','As','Antão','Até','Antes','Agora','Adeus','Acho','Actualmente','À','Á','Às','Aí','Ainda','Após',
'Acha','Acho','Achas','Algum','Alguns','Alguma','Algumas','Alguém','Aquele','Aquela','Aqueles','Aquelas','Além',
'Apesar','Ao','Aos','Abaixo','Assim','Aqui','Acolá','Ali','Ah','Aliás','Afinal',
'Boa','Bem','Bom',
'Como','Com','Contudo','Concordo','Creio','Conforme','Cada','Convém','Claro','Cá','Caso','Consoante','Claro',
'Contra',
'Durante','De','Do','Da','Das','Dos','Diga','Depois','Desde','Desta','Deste','Destas','Destes','Dessa','Desse',
'Dessas','Desses','Daí','Diante',
'Esta','Estas','Este','Estes','Esse','Esses','Essa','Essas','E','É','És','Eu','Ele','Ela','Eles','Elas','Então',
'Embora','Era','Eram','Éramos','Estive','Estou','Eis','Espero','Em','Entre','Entretanto','Estava','Emfim','É-me',
'Está','Estão','Enquanto','Enfim',
'Foi','Fui','Fiz','Foram', 'Face','Faço',
'Há','Havia','Hoje',
'Isto','Isso','Instantes',
'Já','Junto',
'Local','Lá','Logo',
'Mas','Mais','Muito','Mesmo','Meu','Meus','Minha','Minhas','Me',
'Na','No','Nas','Nos','Num','Nuns','Numa','Numas','Naquela','Naquele','Naquelas','Naqueles',
'Nós','Não','Nessa','Nem','Ninguém','Nenhum','Nenhuns','Nenhuma','Nenhumas','Neste','Nesta','Nestes','Nestas', 
'Nesse','Nesses','Nessa','Nessas','Nunca','Nada','Nele','Nela','Neles',
'O','Oh','Os','Onde','Outra','Outro','Outras','Outros','Ou','Ora','Olha',
'Para','Pois','Por','Porquê','Porque','Posso','Pouco','Prezado','Prezada','Prezados','Prezadas','Perante','P',
'Próximo','Porém','Pela','Pode',
'Quando','Qual','Quais','Quem','Quanto','Quantos','Quantas','Queria','Querias','Quero','Queriam','Que','Queríamos','Quer',
'Queremos','Qualquer','Queres',
'R',
'Se','Será','Sou','Segundo','Sei','Sempre','Sim','Sendo','Sem','Somos','Sobre','Sob','São','Sobretudo','Seja',
'Serão','Seria','Só','Seus','Sugere','Senão',
'Tem','Ter','Todos','Todo','Todas','Toda','Tenho','Tinha','Tivémos','Tivemos','Talvez','Temos','Também',
'Tudo','Tens','Tomara','Tendo','Tal','Tu','Todavia','Têm',
'Um','Uma','Uns','Umas',
'Você','Vocês','Vós','Vou','Vamos','Vale','Vai','Vá',
	                   
'Agradeceu-me','Apeei-me','Acesse','Andei','Ajudou-me','Adorava','Aproveitamos','Antigamente','Aprendeu',	  
'Aumentam','Assume','Arrancava','Aparentemente','Aplique','Acelera','Adiciona','Almocei','Atualmente',
'Ajude','Achava','Acesso','Altitude','Autor','Ambos','Área','Assunto','Atravessando','Antecedentes',
'Auxiliares','ALVO','Aproveitando','Admitia-se','Arriscar','Afortunadamente','Agradeço','Arranjávamos',
'Abertura',
'Batizado',
'Concluiu','Comíamos','Cozia', 'Conte-nos', 'Chegar','Criar', 'Chamava-se', 'Comecei', 'Continua',
'Concluiu-se','Comprar','Comia-se','Contamos','Conheci','Conheces-me','Conheço','Conjecturou','Colocado',
'Começa','Clique','Conteúdo','Começo','Contam','Conhece','Cuidar','Custavam','Convivia','Chegou',
'Cuidado','CD','Clima','Contactos','Candidatos','Comandado','Copyright', 'CEP','Comprovou-se','COMUNICADO',
'Cadastro','Controle','Continuar','Coloque','Casavam',
'Dava-se','Dava','Diga','Descreva', 'Dê-me', 'Distribui-se','Deve','Devemos', 'Desejaria','Diariamente',  
'Dávamo-nos','Devido','Doença','Dei','Deixar','Debate','DEBATEDORES','Devo','Divorciado','Dentro',
'Explorou', 'Esgotei','Estudei','Existem','Estimular','Exemplo','Encontram-se','Estende-se','Envia','Envie',
'Extrai','Estacionada','Encontrará','Entendemos','Embarquei','Esperamos','Entrei','Eleições','Eleitores',
'Endereço','Encontramo-nos','Esteve','Emissão','Entrada','Entendeu','Evite','Espera',
'Formei','Ficava','Fiquei','Falta','Fomos','Ficaremos','Finalmente','Fujamos','Formar','Fortalecer','Faça',
'Faltam','Formas','Fazer','Fluxo','Fere',
'Gostava','Gosto','Gosta','Guardavam-se','Gostaria','Gostei','Ganhos','Guarde',
'Indaguei','Íamos', 'Ia','Iam','Iluminai-lhe','Intervém','Inclusivamente','Igualmente','Imagina','Isolada',
'Importa','Interessavam','Informatize','Impressão','Idem','Inclusive',
'Jantamos','Justamente','Juntos','Jogador',
'Lembra-se',  'Levava','Lembro-me','Livrai-me','Ligue','Limpe','Lamentavelmente','Levantava-me','Liderada',
'Limites','Líder','Limite','Lembre-se','Leia',
'Morreu','Morei','Maiores','Morou','Morávamos','Movimentaram-se','Máxima','Modifique','Mude',
'Nasceu','Nasci','Nascida','Nada','Novidades','Novo',
'Optei','Obs','Ocorreu','Ouvi',
'Passando','Passeando', 'Punha-me','Prende-se','Passava','Paralelamente','Peço','Penso', 'Palavra',   
'Protegida','Pensamos','Possivelmente','Pensavam','Pegue','Preço','População','Parece','Programa','Pôr',
'Pedido','Pesquisadores','Participei','Pagar','Palestrante','Prejuízos','Podemos','Procure','Pior',
'Paguei','Pense','Prazos','Primeira','Primeiro',
'Regressámos','Resolvemos','Reconhecendo','Relativamente','Ris','Recomenda-se','Remove','Recordo','Recebera',
'Reparei','Raramente','Recebe','Relacionava-me','Religiosos','Regressado','Retirado','Respondiam',
'Removida','Retorna','REPERCUSSÃO','Realização','Recebi','Regressavam','Representa',
'Sabe','Saí','Sentou-se','Surpreendeu-me','Socorrei-me','Sedeada','Seguramente','Salva','Salvo','Sentia','Supõe',
'Surgiam','Segue-se','Sua','Seu','Semelhante','Sentir','Subdivisões','Simplesmente','Segui','Siga','Somente',
'Trabalhavam','Trabalhou','Terminei','Trata-se','Trabalha-se','Tive','Tentámos','Teve','Tel.',' Tornamo-nos',
' Tendo-lhe','Tradução',
'Usáva-mos','Usavam-se','Ultima','Unicamente','Ultimamente',
'Viemos','Vivia','Vários','Voltar','Venha','Venham','Verifica-se','Voltando','Variava','Vendia','Vendas','Visto',
'Viveu','Verificou-se','Vagarosamente','Voltava','Viúva','Versão','Visitei'
]
   
static final List<String> beginningStopwordList = ["A","O","As","Os","No","Na","Do","Da",'Para']      
   
}