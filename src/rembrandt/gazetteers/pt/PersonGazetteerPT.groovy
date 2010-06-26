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

import rembrandt.obj.Clause
import rembrandt.obj.Criteria
import rembrandt.obj.Cardinality
import java.util.regex.Pattern

/**
 * @author Nuno Cardoso
 * This class stores gazetteers for PESSOA category.
 */
class PersonGazetteerPT {
 
   // to be included in the NE
   static final List<Pattern> cargoIncludeSingle = [~/(?:ex|vice|sub)?-?alt[oa]-comissári[oa]/, ~/(?:ex)?-?bispo/, 
	~/(?:ex|vice|sub)?-?chanceler/, ~/(?:ex|vice|sub)?-?chefe/, ~/conde(?:ssa)?/, ~/c[oô]nsule?a?/, 
	~/(?:ex)?-?czar(?:ina)?/, ~/(?:ex|vice|sub)?-?direc?tora?/, ~/(?:ex|vice|sub)?-?directora?-geral/, 
	~/(?:ex)?-?duque(?:sa)?/, ~/(?:ex|vice|sub)?-?embaixadora?/, ~/(?:ex|vice|sub)?-?embaixatriz/, ~/infant[ae]/, 
	~/(?:ex|vice|sub)?-?governadora?/, ~/(?:ex)?-?líder/, ~/(?:ex|vice|sub)?-?marqu[êe]sa?/, ~/(?:ex|vice|sub)?-?ministr[ao]/,
	~/(?:ex)?-?padre/, ~/(?:ex|vice|sub)?-?patr(?:ão|oa)/, ~/(?:ex|vice|sub)?-?porta-?voz/, ~/(?:ex|vice|sub)?-?presidente/,
	~/(?:ex|vice|sub)?-?primeir[oa]-ministr[ao]/, ~/(?:ex)?-?princesa/, ~/(?:ex)?-?príncipe/, ~/(?:ex)?-?rabi(no)?/, 
	~/(?:ex)?-?ra[íi]nha/, ~/(?:ex)?-?rei/, ~/(?:ex|vice|sub)?-?reitora?/, ~/(?:ex|vice|sub)?-?secretári[oa]/, 
	~/(?:ex|vice|sub)?-?secretári[oa]-geral/, ~/(?:ex)?-?sultão/, ~/(?:ex)?-?visconde(?:ssa)?/ ]
 
   // to be excluded in the NE  
   static final List<Pattern> cargoIncludePlural = [~/(?:ex|vice|sub)?-?alt[oa]-comissári[oa]s/, ~/(?:ex)?-?bispos/, 
	~/(?:ex|vice|sub)?-?chanceleres/, ~/(?:ex|vice|sub)?-?chefes/, ~/conde(?:ssa)?s/, ~/c[oô]nsul(?:es)?[ea]s?/, 
	~/(?:ex)?-?czar(?:e|ina)s/, ~/(?:ex|vice|sub)?-?direc?tor[ae]s/, ~/(?:ex|vice|sub)?-?director[ae]s-gerais/, 
	~/(?:ex)?-?duque(?:sa)?s/, ~/(?:ex|vice|sub)?-?embaixador[ae]s/, ~/(?:ex|vice|sub)?-?embaixatrizes/, ~/infant[ae]s/, 
	~/(?:ex|vice|sub)?-?governadora?/, ~/(?:ex)?-?líder/, ~/(?:ex|vice|sub)?-?marqu[êe]sa?/, ~/(?:ex|vice|sub)?-?ministr[ao]/,
	~/(?:ex)?-?padres/, ~/(?:ex|vice|sub)?-?patrões/, ~/(?:ex|vice|sub)?-?porta-?vozes/, ~/(?:ex|vice|sub)?-?presidentes/,
	~/(?:ex|vice|sub)?-?primeir[oa]s?-ministr[ao]s/, ~/(?:ex)?-?princesas/, ~/(?:ex)?-?príncipes/, ~/(?:ex)?-?rabi(no)?s/, 
	~/(?:ex)?-?ra[íi]nhas/, ~/(?:ex|el)?-?reis/, ~/(?:ex|vice|sub)?-?reitor[ae]s/, ~/(?:ex|vice|sub)?-?secretári[oa]s/, 
	~/(?:ex|vice|sub)?-?secretári[oa]s-geral/, ~/(?:ex)?-?sultões/, ~/(?:ex)?-?visconde(?:ssa)?s/ ]

   // to be included in the NE   
   static final List cargoExcludeSingle = [~/(?:ex|vice|sub)?-?prefeito/,[~/(?:ex|vice|sub)?-?primeir[oa]/, ~/ministr[oa]/],
	                		   'el-rei', ~/comissári[oa]/]	

   // to be excluded in the NE  	   
   static final cargoExcludePlural = [~/(?:ex|vice|sub)?-?prefeitos/, [~/(?:ex|vice|sub)?-?primeir[oa]s/, ~/ministr[oa]s/],
	    				  'el-reis', ~/comissári[oa]s/]	
   
   static final List<Pattern> prefixList = [~/senhor[ae]?s/]
   
   // to be included in the NE 
   static final List<Pattern> jobInclude = [~/[Aa]rquitec?t[oa]s?/, ~/[Dd]outor[ea]?s?/, ~/[Ee]ngenheir[oa]s?/, 
	   ~/[Mm]mestres?/, ~/[Pp]rofessor[ae]?s?/, ~/[Ss]enhor[ae]?s?/]
   
	// to be EXCLUDED in the NE. 
   static final List jobExclude = [~/[Aa]c?tor(es)?/, ~/[Aa]c?triz(es)?/, ~/[Aa]str[ôó]nom[oa]s/, ~/[Aa]tletas?/,
	~/[Aa]utor[ae]?s?/, ~/[Aa]dvogad[ao]s?/, ~/[Aa]presentador[ea]?s?/, ~/[Bb]ateristas?/, ~/[Bb]aixistas?/, ~/[Cc]antor[ae]?s?/,
	~/[Cc]ineastas?/, ~/[Cc]ientistas?/, ~/[Cc]ompositor[ea]?s?/, ~/[Cc]riminosos?/, ~/[Cc]oronel/, ~/[Dd]iplomatas?/,
	~/[Dd]ramaturg[oa]s?/, ~/[Dd]eputad[oa]s?/, ~/[Ee]scritor[ae]?s?/, ~/[Ee]scultor[ae]?s?/, ~/[Ee]ngenheir[ao]s?/,
	~/[Ee]conomistas?/, ~/[Ee]xplorador[ae]?s?/, ~/[Ff]utebolistas?/, ~/[Ff]il[ôó]sof[ao]s?/, ~/[Ff]ísic[ao]s?/,
	~/[Gg]enera[li]s?/, ~/[Gg]overnador[ea]?s?/, ~/[Gg]oleiro/, ~/[Gg]uitarristas?/, ~/[Hh]istoriadores?/,
	~/[Hh]umoristas?/, ~/[Hh]umanistas?/, ~/[Ii]mperador[ea]s?/, ~/[Jj]ogador[ea]s?/, ~/[Jj]ornalistas?/, ~/[Jj]udocas?/, 
	~/[Mm]édiu[mn]s?/, ~/[Mm]ilitar(?:es)?/, ~/[Mm]úsicos?/, ~/[Nn]avegador(?:es?)/, ~/[Pp]ugilistas?/,
	~/[Pp]ersonalidades?/, ~/[Pp]intor[ae]?s?/, ~/[Pp]rodutor/, ~/[Pp]rimeir[ao]s?-[Mm]inistr[ao]s?/,
	[~/[Pp]rimeir[ao]s?/, ~/[Mm]inistr[ao]s?/], ~/[Pp]remi[eê]r/, ~/[Pp]refeitos?/, ~/[Pp]residentes?/, 
	~/[Pp]ilotos?/, ~/[Pp]oetas?/, ~/[Pp]olíticos?/, ~/[Rr]ealizador[ae]s?/, ~/[Rr]omancistas?/, ~/[Rr]eis?/, 
	~/[Rr]aínhas?/, ~/[Ss]enador[ea]?s?/, ~/[Tt]enistas?/, ~/[Tt]eólogos?/,    
	~/[Tt]reinador[ea]?s?/, ~/[Tt]enista/, ~/[Vv]iolinista/, ~/[Vv]ioloncelista/, ~/[Zz]agueiro/ ]   
   
   //'seu' gera muito lixo
   static final List<Pattern> parentescoInclude = [~/av[ôó]s?/, ~/v[ôó]v[ôó]s?/, ~/ti(ti)?[ao]s?/, 
	   ~/do[nm]a?s?/, ~/irmão?s?/, ~/madres?/, ~/sir/, ~/sô/]
   
   static final List<Pattern> parentescoExclude = [~/pa(?:pa)?i/, ~/m(?:am)?ãe/, ~/[bis|tris|tetr]av[ôó]s/,
	~/filh[oa]s?/, ~/net[ao]s?/, ~/[bis|tris|tetra]net[ao]s?/, ~/entead[ao]s?/, ~/[pm]adrast[oa]s?/, ~/afilhad[oa]s/,
	~/sobrinh[oa]s/, ~/man[oa]s?/, ~/prim[oa]s?/, ~/cunhad[oa]s/, ~/ti[oa]s?-av[ôó]s?/, ~/sogr[oa]s?/, ~/[pm]adrinh[oa]s?/,
	~/genros?/, ~/noras?/, ~/(?:ex-)?maridos?/, ~/(?:ex-)?espos[oa]s?/, ~/sobrinh[oa]s?-net[oa]s?/, ~/namorad[oa]s?/, ~/amantes?/ ]
      
   static final List otherPersonQualifier = ['homem', 'mulher', ~/senhora?/]

   static final List otherPersonOccupation = [~/[Cc]omunista/, ~/[Ss]ocialista/, ~/[Ss]ocial-[Dd]emocrata/, ~/[Pp]opular/, ~/[Dd]emocrata/]
  
   static final List<String> firstName = ['Abraão', 'Adalberto', 'Adelaide', 'Adão', 'Adolfo', 'Adriano', 'Afonso', 'Alfredo', 'Agenor', 'Alcino',
   'Aloísio', 'Aloizio', 'Adelino', 'Alexandra', 'Alexandre', 'Alice', 'Amadeu', 'Amanda', 'Américo', 'Amália', 'Amélia', 'Ana',
   'Anabela', 'André', 'Andreia', 'Angélica', 'Anselmo', 'Antero', 'Aristides', 'Armando', 'Arménio', 'Artur', 'Avelino', 'Augusto',
   'Aristide', 'Antônio', 'Alberto',
   'Baptista', 'Bárbara', 'Baltazar', 'Baltasar', 'Bartolomeu', 'Beatriz', 'Belinda', 'Benedita', 'Benedito', 'Benjamim', 'Beto', 'Bernardo',
   'Brandão', 'Bruno', 'Bruna', 'Caetano', 'Cácia', 'Camila', 'Cândido', 'Carina', 'Carla', 'Carlos', 'Cármen', 'Carmo', 'Celso',
   'Carolina', 'Célio', 'Célia', 'Cecília', 'Cesário', 'César', 'Cidália', 'Clara', 'Cláudia', 'Cláudio', 'Conceição', 'Constantino',
   'Cristina', 'Cristiana', 'Cristiano', 'Cristóvão', 'Cândida', 'Candida',
   'Dália', 'Daniel', 'Daniela', 'Damião', 'David', 'Demétrios', 'Denise', 'Diamantino', 'Dina', 'Dino', 'Diogo', 'Domingos', 'Dora', 'Dóris',
   'Dorival',
   'Edgar', 'Edite', 'Edmundo', 'Eduardo', 'Egídio', 'Eládio', 'Élio', 'Elmano', 'Elisabete', 'Elizabete', 'Elsa', 'Emanuel', 'Emília', 'Emilio',
   'Emiliana', 'Esmeralda', 'Estela', 'Eunice', 'Eva', 'Evandro', 'Ezequiel', 'Eronildes', 'Emílio', 'Fábio', 'Fabrício', 'Fabiano',
   'Fátima', 'Fausto', 'Félix', 'Feliciano', 'Fernando', 'Fernanda', 'Fernão', 'Filipe', 'Filipa', 'Filomena', 'Flávio',
   'Frederico', 'Francisco', 'Gabriela', 'Gabriel', 'Galileu', 'Gaspar', 'Geraldo', 'Gil', 'Gilberto', 'Guilherme', 'Gustavo',
   'Hélder', 'Hélio', 'Helena', 'Henrique', 'Hermano', 'Horácio', 'Hugo', 'Hélio', 'Humberto',
   'Idália', 'Igor', 'Ilídio', 'Inês', 'Isaac', 'Isabel', 'Isidro', 'Iva', 'Ivo', 'Ivone', 'Ives',
   'Jacques', 'Jessica', 'Jesus', 'Jesualdo', 'Joana', 'José', 'João', 'Joaquim', 'Joel', 'Jorge', 'Júlia', 'Júlio',
   'Laurinda', 'Leandro', 'Leonardo', 'Leopoldo', 'Leonel', 'Leonor', 'Lídia', 'Liliana', 'Luciana', 'Luís', 'Luiz', 'Lurdes',
   'Madalena', 'Mafalda', 'Magda', 'Marcelo', 'Márcia', 'Margarida', 'Manuel', 'Manuela', 'Marcírio', 'Maria', 'Mariana', 'Marília', 'Marina', 'Marisa',
   'Marlene', 'Mateus', 'Matilde', 'Mauro', 'Melinda', 'Melissa', 'Micael', 'Miguel', 'Mónica', 'Mônica', 'Marco', 'Maurílio',
   'Nadia', 'Natália', 'Natércia', 'Nélson', 'Nicolau', 'Nestor', 'Norberto', 'Nuno', 'Nizia', 'Nelson',
   'Odete', 'Onofre', 'Orlando', 'Orlanda', 'Óscar', 'Osvaldo',
   'Pamela', 'Patrícia', 'Pedro', 'Plácido', 'Pompeu', 'Porfírio', 'Paulo', 'Paula',
   'Quim',
   'Rafael', 'Rafaela', 'Renato', 'Reinaldo', 'Ricardo', 'Rita', 'Rodrigo', 'Roberta', 'Roberto', 'Rodolfo', 'Rogério', 'Romão', 'Ronaldo',
   'Rúben', 'Rui', 'Rute',
   'Salomé', 'Samuel', 'Santana', 'Santiago', 'Sara', 'Sérgio', 'Silvia', 'Silvino', 'Simão', 'Sofia', 'Sónia', 'Soraia', 'Susana',
   'Tânia', 'Tadeu', 'Tatiana', 'Telmo', 'Tiago', 'Teresa', 'Tomás', 'Tomé',
   'Valdemar', 'Valentim', 'Vanda', 'Vânia', 'Vasco', 'Vera', 'Vicente', 'Vinícius', 'Vítor', 'Violeta', 'Vitória', 'Viviana', 'Valéria',
   'Wilson',
   'Xavier', 'Zita', 'Zé', 'Zézé', 'Zezinha'
   ]
                                      
  static final List<String> lastName = [
   'Anjos', 'Andrade', 'Almeida', 'Alves', 'Araújo', 'Amaral', 'Alves', 'Antunes', 'Agostinho', 'Albuquerque',
   'Bravo', 'Barreto', 'Barroso', 'Barbosa', 'Bandeira', 'Belo', 'Branco',
   'Cardoso', 'Carvalho', 'Cordeiro', 'Costa', 'Chaves', 'Cunha', 'Craveiro', 'Carneiro', 'Cruz',
   'Cabral',
   'Duarte', 'Dias', 'Deus', 'Farias', 'Freitas', 'Fernandes', 'Ferreira', 'Fonseca',
   'Gomes', 'Guterres', 'Guedes', 'Gaspar',
   'Lino', 'Lourenço', 'Loureiro', 'Lopes', 'Lucena', 'Leão', 'Lobo',
   'Mendes', 'Meneses', 'Miranda', 'Melo', 'Martins', 'Moniz', 'Machado', 'Mendes', 'Monteiro', 'Moreira', 'Mello',
   'Meireles', 'Medeiros', 'Moita', 'Moraes', 'Morais', 'Melo', 'Menezes', 'Matos', 'Mascarenhas',
   'Nunes', 'Neves',
   'Pinto', 'Pereira', 'Pedroso', 'Pinheiro', 'Pimentel', 'Pimenta', 'Pires', 'Prado',
   'Rodrigues', 'Ribeiro', 'Rodrigues', 'Rocha', 'Raimundo', 'Raposo', 'Rosário',
   'Santana', 'Seabra', 'Soares', 'Salvador', 'Silva', 'Santos', 'Sousa', 'Salgado', 'Souza', 'Sarmento',
   'Senna', 'Saraiva', 'Sampaio', 'Sá',
   'Teixeira', 'Torres', 'Trancoso',
   'Vieira', 'Vasconcelos']
   
   static final List personActionPrefix = [ [~/(?:liderad[ao]|comandad[oa])/, 'por'] , ['presidente', 'em', 'exercício', ','] ]
   
   static final List<String> people = ["povo"]
   /** CLAUSE **/
   
// SINGULARES   
   
   // cargos a serem incluídos (presidente, etc)
  static final Clause cargoIncludeSingle1c = Clause.newConcept1Clause(cargoIncludeSingle, "cargoIncludeSingle")
   
   // cargos a serem excluídos (el-rei, etc)
  static final Clause cargoExcludeSingle1nc = Clause.newConcept1Clause(cargoExcludeSingle, "cargoExcludeSingle", false)

   // cargos a serem incluídos (presidentes, etc)
  static final Clause cargoIncludePlural1c = Clause.newConcept1Clause(cargoIncludePlural, "cargoIncludePlural")
   
   // cargos a serem excluídos (el-reis, etc)
  static final Clause cargoExcludePlural1nc = Clause.newConcept1Clause(cargoExcludePlural, "cargoExcludePlural", false)
  
  // profissões a serem incluídos (padre, etc)
  static final Clause jobInclude1c = Clause.newConcept1Clause(jobInclude, "jobInclude")
 
   // profissões a serem excluídas (músico, etc)   
  static final Clause jobExclude1nc = Clause.newConcept1Clause(jobExclude, "jobExclude", false)   
   
   // parentescos a serem incluídos (irmão, etc)
  static final Clause parentescoInclude1c = Clause.newConcept1Clause(parentescoInclude, "parentescoInclude")
 
   // parentescos a serem excluídos (sobrinho, etc) 
  static final Clause parentescoExclude1nc =  Clause.newConcept1Clause(parentescoExclude,"parentescoExclude", false)  

  // parentescos a serem excluídos (sobrinho, etc) 
  static final Clause people1nc =  Clause.newConcept1Clause(people,"people", false)  

  
// MISTURAS 

   // cargos a serem incluídos + excluídos
   static final Clause cargoAllSingle1nc = Clause.newConcept1Clause((cargoIncludeSingle + cargoExcludeSingle), "cargoAllSingle1nc", false)

   static final Clause cargoAllPlural1nc = Clause.newConcept1Clause((cargoIncludePlural + cargoExcludePlural), "cargoAllPlural1nc", false)
  
   static final Clause cargoAll1nc= Clause.newConcept1Clause((cargoIncludeSingle + cargoExcludeSingle + 
	   cargoIncludePlural + cargoExcludePlural), "cargoAll1nc", false)
   
   static final Clause parentescoAll1nc = Clause.newConcept1Clause((parentescoInclude + parentescoExclude), "parentescoAll1nc", false)
       
   static final Clause jobAll1nc = Clause.newConcept1Clause((jobInclude + jobExclude), "jobAll1nc", false)

   // igual à clausula jobListClause1, com mais alguns (homem, mulher, etc) 
   static final Clause jobAllPlusOtherQualifier1nc = new Clause(name:"jobListClause2", cardinality:Cardinality.One, collectable:false,
	   criteria:Criteria.ConceptMatch, pattern:(jobInclude + jobExclude + otherPersonQualifier)	)

   // Adjectives for persons (gentilics + others)
   static final Clause personAdjective01nc = Clause.newConcept01Clause(
	   	(otherPersonOccupation + LocalGazetteerPT.cityAdjective + 
		LocalGazetteerPT.regionAdjective + LocalGazetteerPT.alternativeCountryAdjective + 
		LocalGazetteerPT.countryAdjective + LocalGazetteerPT.continentAdjective), 
		"person adjectives", false)  

   // hints for person actions, "Commanded by", etc.
   static final Clause personActionPrefix1nc = Clause.newConcept1Clause(
	personActionPrefix, "person actions", false)
}