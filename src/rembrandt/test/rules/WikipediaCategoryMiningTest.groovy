/** Rembrandt
 * Copyright (C) 2008 Nuno Cardoso. ncardoso@xldb.di.fc.ul.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package rembrandt.test.rules

import org.junit.*
import org.junit.runner.*

import org.apache.log4j.*

import rembrandt.obj.NamedEntity
import rembrandt.obj.Sentence
import saskia.wikipedia.*
import rembrandt.rules.harem.pt.*
import rembrandt.rules.*
import rembrandt.gazetteers.pt.*
import rembrandt.gazetteers.CommonClassifications as SC

/**
 * @author Nuno Cardoso
 * Tester for WikipediaCategoryMining.
 */
class WikipediaCategoryMiningTest extends GroovyTestCase {

	def Logger log = Logger.getLogger("RembrandtTestLogger")

	def catminer
	def wikipedia
	def classification

	def meanings

	public WikipediaCategoryMiningTest() {
		//catminer = new WikipediaCategoryMining()
		catminer = new WikipediaCategoryRulesPT()
		wikipedia = WikipediaAPI.newInstance("pt")
		meanings = new WikipediaCategoryDefinitionsPT().meanings
	}


	/** HUMANO PAIS: países, principados, uniões de países. */
	void testLocalHumanoPais() {

		classification = [SC.place_human_country]

		def nePais = [
			new NamedEntity(classification:classification, terms:['Tailândia']),
			new NamedEntity(classification:classification, terms:['Tunísia']),
			new NamedEntity(classification:classification, terms:[
				'Estados',
				'Unidos',
				'da',
				'América'
			]),
			new NamedEntity(classification:classification, terms:['Ilhas', 'Feroé']),
			new NamedEntity(classification:classification, terms:['Mónaco']),
			new NamedEntity(classification:classification, terms:['Rússia'])
		]

		/* Categorias reais, retiradas da snapshot da Wikipedia */
		/* Nota: apesar de virem com Categoria:XXX, o wikipedia.getCategories() retira */
		def categoryPais = [
			[
				"Países da Ásia",
				"Tailândia"
			],
			[
				"Países de África",
				"Tunísia"
			],
			[
				"Estados Unidos da América",
				"Países da América do Norte",
				"Países da América Anglo-Saxônica"
			],
			[
				"Ilhas Feroé",
				"Dependências europeias"
			],
			["Países da Europa", "Mónaco"],
			[
				"Rússia",
				"Países da Europa",
				"Países da Ásia",
				"Nações transcontinentais"]
		]

		log.info "Testing Wikipedia Category Miner with LOCAL HUMANO PAIS"
		nePais.eachWithIndex{ ne, i ->
			def meanings = catminer.detectMeanings(ne.terms, categoryPais[i], meanings, catminer.rules, Detector.COLLECT_ALL_MATCHES)
			//  println "Meanings: ${meanings}"
			assert null != meanings.meanings.find{
				it.category == ne.category[0] &&
						it.type == ne.type[0] && it.subtype == ne.subtype[0] }

		}
	}

	/* DIVISAO: metrópoles, cidades, aldeias, vilas, freguesias, estados (Brasil), concelhos, distritos, 
	 províncias, continentes, ou bairros fiscais*/
	void testLocalHumanoDivisao() {

		classification = [SC.place_human_division]

		def neDivisao = [
			// metrópoles, cidades
			new NamedEntity( classification:classification, terms:['São', 'Paulo']),
			new NamedEntity( classification:classification, terms:['Banguecoque']),
			new NamedEntity( classification:classification, terms:['Nova', 'Iorque']),
			// aldeias
			new NamedEntity( classification:classification, terms:['Esfrega']),
			// vilas/freguesisas
			new NamedEntity( classification:classification, terms:['Alcains']),
			// concelhos/distritos
			new NamedEntity( classification:classification, terms:['Lisboa']),
			// estados / unidades federativas
			new NamedEntity( classification:classification, terms:['Pará']),
			// províncias
			new NamedEntity( classification:classification, terms:['Minho']),
			new NamedEntity( classification:classification, terms:['Algarve']),
			// continentes
			new NamedEntity( classification:classification, terms:['Europa']),
			new NamedEntity( classification:classification, terms:['América']),
			new NamedEntity( classification:classification, terms:['América', 'do', 'Sul']),
			new NamedEntity( classification:classification, terms:['Ocenia']),
			new NamedEntity( classification:classification, terms:['Antártica'])
		]

		def categoryDivisao = [
			// metrópoles, Cidades
			[
				"São Paulo",
				"Região Sudeste do Brasil",
				"Unidades federativas do Brasil"
			],
			[
				"Cidades da Tailândia",
				"Capitais da Ásia"
			],
			[
				"Nova Iorque",
				"Cidades de Nova Iorque"
			],
			// aldeias
			[
				"Aldeias do Distrito de Castelo Branco"
			],
			// Esfrega
			// vilas/freguesias
			[
				"Freguesias de Castelo Branco",
				"Vilas de Portugal"
			],
			//Alcains
			// concelhos, distritos
			[
				"Lisboa",
				"Concelhos da Grande Lisboa"
			],
			//Lisboa
			// estados / unidades federativas
			[
				"Pará",
				"Região Norte do Brasil",
				"Unidades federativas do Brasil"
			],
			// Pará
			// províncias
			[
				"Antigas províncias portuguesas"
			],
			// de Minho (desambiguação)
			//Algarve é simultaneamente região e divisão
			[
				"Regiões de Portugal",
				"NUTS III portuguesas",
				"Antigas províncias portuguesas",
				"Grandes áreas metropolitanas de Portugal"
			],
			["Continentes", "Europa"],
			["Continentes", "América"],
			[
				"América do Sul",
				"Continentes"
			],
			["Continentes"],
			// Oceania
			[
				"Antártida",
				"Continentes",
				"Desertos"]// Antártica
		]

		log.info "Testing Wikipedia Category Miner with LOCAL HUMANO DIVISAO"
		neDivisao.eachWithIndex{ne, i ->
			def meanings = catminer.detectMeanings(ne.terms, categoryDivisao[i], meanings, catminer.rules, Detector.COLLECT_ALL_MATCHES )
			//println "${ne.terms}: ${meanings}"
			assert null != meanings.meanings.find{
				it.category == ne.category[0] &&
						it.type == ne.type[0] && it.subtype == ne.subtype[0] } , meanings+" != "+ne
		}
	}


	/* REGIAO:  Baixa, o Grande Porto, Médio-Oriente, Terceiro Mundo, Nordeste*/
	void testLocalHumanoRegiao() {

		classification = [SC.place_human_humanregion]

		def neRegiao = [
			//
			new NamedEntity( classification:classification, terms:['Grande', 'Lisboa']),
			new NamedEntity( classification:classification, terms:['Algarve'])
		]

		def categoryRegiao = [
			// metrópoles, Cidades
			["NUTS III portuguesas"],
			[
				"Regiões de Portugal",
				"NUTS III portuguesas",
				"Antigas províncias portuguesas",
				"Grandes áreas metropolitanas de Portugal"]
		]

		log.info "Testing Wikipedia Category Miner with LOCAL HUMANO REGIAO"
		neRegiao.eachWithIndex{ne, i ->
			def meanings = catminer.detectMeanings(ne.terms, categoryRegiao[i], meanings, catminer.rules, Detector.COLLECT_ALL_MATCHES)
			//println "${ne.terms}: ${meanings}"
			// mudei pois Algarve retorna como REGIAO e DIVISAO
			assert null != meanings.meanings.find{
				it.category == ne.category[0] &&
						it.type == ne.type[0] && it.subtype == ne.subtype[0] }
		}
	}

	/*CONSTRUCAO: edifícios, sala, galeria, jardim ou piscina, pontes, barragens, portos, etc.*/    
	void testLocalHumanoConstrucao() {

		classification = [SC.place_human_construction]

		def neConstrucao = [
			new NamedEntity( classification:classification, terms:['Casa', 'dos', 'Bicos']),
			new NamedEntity( classification:classification, terms:[
				'Mosteiro',
				'dos',
				'Jerónimos']
			),
			new NamedEntity( classification:classification, terms:['Torre', 'dos', 'Clérigos']),
			new NamedEntity( classification:classification, terms:['Torre', 'Eiffel']),
			new NamedEntity( classification:classification, terms:['Jardim', 'da', 'Estrela']),
			new NamedEntity( classification:classification, terms:['Quinta', 'da', 'Fidalga']),
			new NamedEntity( classification:classification, terms:[
				'Igreja',
				'da',
				'Nossa',
				'Senhora',
				'da',
				'Luz']
			),
			new NamedEntity( classification:classification, terms:[
				'Convento',
				'dos',
				'Agostinhos']
			),
			new NamedEntity( classification:classification, terms:['Ponte', 'da', 'Arrábida']),
			new NamedEntity( classification:classification, terms:['Ponte', '25', 'de', 'Abril']),
			new NamedEntity( classification:classification, terms:['Golden', 'Gate', 'Bridge']),
			new NamedEntity( classification:classification, terms:[
				'Barragem',
				'do',
				'Carrapatelo']
			),
			new NamedEntity( classification:classification, terms:['Porto', 'de', 'Leixões']),
			new NamedEntity( classification:classification, terms:['Aeroporto', 'da', 'Portela'])
		]

		def categoryConstrucao = [
			[
				"Sé (Lisboa)",
				"Património edificado em Lisboa",
				"Casas de Portugal",
				"Monumentos nacionais em Portugal"
			],
			// Casa dos Bicos
			[
				"Santa Maria de Belém",
				"Património edificado em Lisboa",
				"Mosteiros de Portugal",
				"Monumentos nacionais em Portugal",
				"Patrimônio Mundial da UNESCO em Portugal"
			],
			//Mosteiro dos Jerónimos
			[
				"Património edificado no Grande Porto",
				"Arquitetura barroca em Portugal",
				"Torres de Portugal"
			],
			[
				"Paris",
				"Torres da Europa",
				"Construções da França"
			],
			// retirado de Categoria:Torre Eiffel
			[
				"Espaços verdes da Grande Lisboa",
				"Jardins de Portugal"
			],
			["Palácios de Portugal"],
			[
				"Monumentos nacionais em Portugal",
				"Igrejas de Lisboa",
				"Carnide (Lisboa)"
			],
			// Igreja de Nossa Senhora da Luz (Carnide)
			[
				"Portalegre",
				"Conventos de Portugal"
			],
			//COnvento dos Agostinhos,
			[
				"Pontes de Portugal",
				"Porto"
			],
			///Ponte da Arrábida
			[
				"Pontes de Portugal",
				"Património edificado em Almada",
				"Património edificado em Lisboa",
				"Caminhos-de-ferro em Portugal"
			],
			// ponte 25 de Abril
			[
				"Pontes dos Estados Unidos da América",
				"Atrações turísticas dos Estados Unidos da América",
				"San Francisco"
			],
			["Barragens de Portugal"],
			//Carrapatelo
			["Portos de Portugal"],
			//leixões
			[
				"Aeroportos de Portugal"]//Portela
		]
		log.info "Testing Wikipedia Category Miner with LOCAL HUMANO CONSTRUCAO"
		neConstrucao.eachWithIndex{ne, i ->
			def meanings = catminer.detectMeanings(ne.terms, categoryConstrucao[i], meanings, catminer.rules, Detector.COLLECT_ALL_MATCHES)
			// println "${ne.terms}: ${meanings}"
			assert null != meanings.meanings.find{
				it.category == ne.category[0] &&
						it.type == ne.type[0] && it.subtype == ne.subtype[0] }
		}

	}

	/*RUA: ruas, avenidas, estradas, travessas, praças, pracetas,  becos, largos, etc. */    
	void testLocalHumanoRua() {

		classification = [SC.place_human_street]

		def neRua = [
			new NamedEntity( classification:classification, terms:['Rua', 'de', 'São', 'Miguel']),
			new NamedEntity( classification:classification, terms:['Avenida', 'da', 'Liberdade']),
			new NamedEntity( classification:classification, terms:['IP5']),
			new NamedEntity( classification:classification, terms:['Segunda', 'Circular']),
			new NamedEntity( classification:classification, terms:[
				'Via',
				'de',
				'Cintura',
				'Interna']
			),
			// VCI dá para desambiguação
			new NamedEntity( classification:classification, terms:['EN109']),
			new NamedEntity( classification:classification, terms:['Praça', 'da', 'Batalha']),//Praça da Batalha (Porto)
		]

		def categoryRua = [
			["Toponímia do Grande Porto"],
			// R. São Miguel,
			[
				"São José (Lisboa)",
				"Património edificado em Lisboa",
				"Ruas de Lisboa"
			],
			[
				"Itinerários Principais de Portugal"
			],
			[
				"Ruas de Lisboa",
				"Estradas de Portugal"
			],
			//2ª Circular
			[
				"Itinerários Complementares de Portugal"
			],
			//VCI
			[
				"Estradas Nacionais de Portugal"
			],
			[
				"Porto",
				"Praças do Grande Porto"]
		]

		log.info "Testing Wikipedia Category Miner with LOCAL HUMANO RUA"
		neRua.eachWithIndex{ne, i ->
			def meanings = catminer.detectMeanings(ne.terms, categoryRua[i], meanings, catminer.rules, Detector.COLLECT_ALL_MATCHES)
			//println "${ne.terms}: ${meanings}"
			assert null != meanings.meanings.find{
				it.category == ne.category[0] &&
						it.type == ne.type[0] && it.subtype == ne.subtype[0] }
		}
	}

	/* AGUACURSO: rios, ribeiros, riachos, afluentes, quedas de água, cascatas, cachoeiras, etc. */
	/* AGUAMASSA: lagos, mares, oceanos, golfos, estreitos, canais, bacias, barragens, etc. */
	void testLocalFisicoAgua() {

		classification = [
			SC.place_physical_watercourse
		]

		def neCursoAgua = [
			new NamedEntity( classification:classification, terms:['Rio', 'Douro']),
			new NamedEntity( classification:classification, terms:['Rio', 'Nilo']),
			new NamedEntity( classification:classification, terms:['Ebro']),
			new NamedEntity( classification:classification, terms:['Zêzere']),
			new NamedEntity( classification:classification, terms:['Cachoeira', 'do', 'Bisnau']),
			new NamedEntity( classification:classification, terms:['Cataratas', 'do', 'Niagara'])
		]

		def categoryCursoAgua = [
			[
				"Rios da Espanha",
				"Rios de Portugal",
				"Porto"
			],
			[
				"Rios do Ruanda",
				"Rios do Burundi",
				"Rios de Uganda",
				"Rios da República Democrática do Congo",
				"Rios da Etiópia",
				"Rios do Sudão",
				"Rios do Egito"
			],
			["Rios da Espanha"],
			["Afluentes do Rio Tejo"],
			["Cachoeiras"],
			[
				"Cataratas",
				"do",
				"Niágara"]
		]

		classification = [SC.place_physical_watermass]

		def neMassaAgua = [
			new NamedEntity( classification:classification, terms:['Mar', 'Morto']),
			new NamedEntity( classification:classification, terms:['Lago', 'Vitória']),
			new NamedEntity( classification:classification, terms:['Oceano', 'Pacífico']),
			new NamedEntity( classification:classification, terms:['Golfo', 'do', 'México']),
			new NamedEntity( classification:classification, terms:['Baía', 'dos', 'Porcos']),
			new NamedEntity( classification:classification, terms:['Canal', 'da', 'Mancha']),
			new NamedEntity( classification:classification, terms:['Canal', 'do', 'Panamá']),
			//new NamedEntity( classification:classification, terms:['Canal','do','Suez'] ),
			//new NamedEntity( classification:classification, terms:['Bacia','do','Mediterrâneo'] )
		]

		def categoryMassaAgua = [
			["Mares", "Extremos da Terra"],
			//Mar Morto
			[
				"Lagos do Quênia",
				"Lagos da Tanzânia",
				"Lagos de Uganda"
			],
			//Lago Vitória
			["Oceanos", "Oceano Pacífico"],
			//Oceano Pacífico
			["Golfos e Baías"],
			//Golfo do México
			[
				"Golfos e Baías",
				"Guerra Fria"
			],
			//Baía dos Porcos
			[
				"Canais",
				"Geografia do Reino Unido",
				"Geografia da França"
			],
			//Canal da Mancha
			[
				"Canais",
				"Geografia do Panamá"]//Canal do Panamá,
		]

		log.info "Testing Wikipedia Category Miner with LOCAL FISICO CURSOAGUA"
		neCursoAgua.eachWithIndex{ne, i ->
			def meanings = catminer.detectMeanings(ne.terms, categoryCursoAgua[i], meanings, catminer.rules, Detector.COLLECT_ALL_MATCHES)
			//println "${ne.terms}: ${meanings}"
			assert null != meanings.meanings.find{
				it.category == ne.category[0] &&
						it.type == ne.type[0] && it.subtype == ne.subtype[0] }
		}
		log.info "Testing Wikipedia Category Miner with LOCAL FISICO MASSAAGUA"
		neMassaAgua.eachWithIndex{ne, i ->
			def meanings = catminer.detectMeanings(ne.terms, categoryMassaAgua[i], meanings, catminer.rules, Detector.COLLECT_ALL_MATCHES)
			//println "${ne.terms}: ${meanings}"
			assert null != meanings.meanings.find{
				it.category == ne.category[0] &&
						it.type == ne.type[0] && it.subtype == ne.subtype[0] }
		}
	}

	/* RELEVO: montanhas, cordilheiras, montes, serras, planícies, planaltos, vales, etc. */
	void testLocalFisicoRelevo() {

		classification = [SC.place_physical_mountain]

		def neRelevo = [
			new NamedEntity( classification:classification, terms:['Kikimanjaro']),
			new NamedEntity( classification:classification, terms:['K2']),
			new NamedEntity( classification:classification, terms:['Monte', 'Everest']),
			new NamedEntity( classification:classification, terms:['Monte', 'Branco']),
			new NamedEntity( classification:classification, terms:['Serra', 'da', 'Estrela']),
			new NamedEntity( classification:classification, terms:['Sierra', 'Nevada']),
			new NamedEntity( classification:classification, terms:[
				'Cordilheira',
				'dos',
				'Andes'
			]),
			new NamedEntity( classification:classification, terms:['Planalto', 'do', 'Paraná']),
			new NamedEntity( classification:classification, terms:['Florival'])
		]

		def categoryRelevo = [
			[
				"Patrimônio Mundial da UNESCO na Tanzânia",
				"Montanhas da Tanzânia"
			],
			[
				"Montanhas do Paquistão",
				"Montanhas da China",
				"Himalaia"
			],
			//K2
			[
				"Himalaia",
				"Montanhas do Nepal",
				"Montanhas da China",
				"Extremos da Terra"
			],
			//Evereste
			[
				"Montanhas da França",
				"Montanhas da Itália",
				"Patrimônio Mundial da UNESCO"
			],
			//Monte Branco
			[
				"Serras de Portugal",
				"Guarda"
			],
			//Serra da Estrela
			[
				"Montanhas dos Estados Unidos da América",
				"Cordilheiras"
			],
			//Sierra Nevada
			[
				"Cordilheiras",
				"América do Sul"
			],
			//Andes
			["Paraguai", "Planaltos"],
			//Paraná
			[
				"Geografia da França",
				"Vales"]//Florival
		]

		log.info "Testing Wikipedia Category Miner with LOCAL FISICO RELEVO"
		neRelevo.eachWithIndex{ne, i ->
			def meanings = catminer.detectMeanings(ne.terms, categoryRelevo[i], meanings, catminer.rules, Detector.COLLECT_ALL_MATCHES)
			//println "${ne.terms}: ${meanings}"
			assert null != meanings.meanings.find{
				it.category == ne.category[0] &&
						it.type == ne.type[0] && it.subtype == ne.subtype[0] }
		}
	}

	void testLocalFisicoPlaneta() {

		classification = [SC.place_physical_planet]

		def nePlaneta = [
			new NamedEntity( classification:classification, terms:['Júpiter']),
			new NamedEntity( classification:classification, terms:['Plutão']),
			new NamedEntity( classification:classification, terms:['Sol']),
			new NamedEntity( classification:classification, terms:['Terra']),
			new NamedEntity( classification:classification, terms:['Lua'])
		]

		def categoryPlaneta = [
			["Sistema solar", "Planetas"],
			//Júpiter
			["Planetas anões", "Plutão"],
			//Plutão
			["Sistema solar", "Estrelas"],
			//Sol
			[
				"Planetas",
				"Geologia",
				"Geografia"
			],
			//Terra
			[
				"Satélites naturais",
				"Sistema solar"]//Lua
		]

		log.info "Testing Wikipedia Category Miner with LOCAL FISICO PLANETA"
		nePlaneta.eachWithIndex{ne, i ->
			def meanings = catminer.detectMeanings(ne.terms, categoryPlaneta[i], meanings, catminer.rules, Detector.COLLECT_ALL_MATCHES)
			// println "${ne.terms}: ${meanings}"
			assert null != meanings.meanings.find{
				it.category == ne.category[0] &&
						it.type == ne.type[0] && it.subtype == ne.subtype[0] }
		}
	}

	/* Ilhas e Arquipélagos*/
	void testLocalFisicoIlha() {

		classification = [SC.place_physical_island]
		def neIlha = [
			new NamedEntity( classification:classification, terms:['Ilhas', 'Selvagens']),
			new NamedEntity( classification:classification, terms:['Açores']),
			new NamedEntity( classification:classification, terms:['Berlengas']),
			new NamedEntity( classification:classification, terms:['Galápagos']),
			new NamedEntity( classification:classification, terms:['Madagáscar'])
		]

		def categoryIlha = [
			[
				"Territórios disputados",
				"Ilhas de Portugal",
				"Ilhas da Madeira",
				"Região Autónoma da Madeira"
			],
			[
				"Região Autónoma dos Açores",
				"NUTS III portuguesas",
				"Dependências europeias",
				"Ilhas de Portugal",
				"Arquipélagos"
			],
			[
				"Arquipélagos",
				"Ilhas de Portugal",
				"Ilhas do Atlântico",
				"Peniche",
				"Berlengas"
			],
			[
				"Ilhas do Equador",
				"Arquipélagos"
			],
			//galápagos
			[
				"Madagáscar",
				"Países de África",
				"Ilhas do Índico"]//Madagáscar
		]
		log.info "Testing Wikipedia Category Miner with LOCAL FISICO ILHA"
		neIlha.eachWithIndex{ne, i ->
			def meanings = catminer.detectMeanings(ne.terms, categoryIlha[i], meanings, catminer.rules, Detector.COLLECT_ALL_MATCHES)
			// println "${ne.terms}: ${meanings}"
			assert null != meanings.meanings.find{
				it.category == ne.category[0] &&
						it.type == ne.type[0] && it.subtype == ne.subtype[0] }
		}
	}

	/* REGIAO: Estreitos, o Bósforo, Balcãs, Meseta Ibérica, regi√£o do Amazonas, o Deserto do Sahara,
	 * continentes vistos como regi√£o da geografia f√≠sica */
	void testLocalFisicoRegiao() {

		classification = [
			SC.place_physical_physicalregion
		]

		def neFisicoRegiao = [
			new NamedEntity( classification:classification, terms:[
				'Estreito',
				'de',
				'Gibraltar'
			]),
			new NamedEntity( classification:classification, terms:['Bósforo'])
		]

		def categoryFisicoRegiao = [
			[
				"Estreitos",
				"Geografia de Marrocos",
				"Geografia da Espanha",
				"Mediterrâneo"
			],
			//Estreito de Gibraltar
			[
				"Estreitos",
				"Geografia da Turquia"]//Bósforo,
		]

		log.info "Testing Wikipedia Category Miner with LOCAL FISICO REGIAO"
		neFisicoRegiao.eachWithIndex{ne, i ->
			def meanings = catminer.detectMeanings(ne.terms, categoryFisicoRegiao[i], meanings, catminer.rules, Detector.COLLECT_ALL_MATCHES)
			// println "${ne.terms}: ${meanings}"
			assert null != meanings.meanings.find{
				it.category == ne.category[0] &&
						it.type == ne.type[0] && it.subtype == ne.subtype[0] }
		}
	}

	/* VIRTUAL
	 * COMSOCIAL: meios de comunica√ß√£o social, como jornais, televis√£o, r√°dio
	 * SITIO:  Web, WAP, ftp etc.
	 * OBRA: referência a uma obra impressa 
	 */
	void testLocalVirtualComSocial() {

		classification = [SC.place_virtual_media]

		def neComSocial = [
			new NamedEntity( classification:classification, terms:['Jornal', 'de', 'Notícias']),
			new NamedEntity( classification:classification, terms:['RTP']),
			new NamedEntity( classification:classification, terms:['TSF'])
		]

		def categoryComSocial = [
			[
				"Jornais de Portugal",
				"Media do Grande Porto"]
		]

		assert 1 == 1
	}

	void testPessoaIndividual() {

		classification = [SC.person_individual]

		def nes = [
			new NamedEntity( classification:classification, terms:['António', 'Guterres']),
			new NamedEntity( classification:classification, terms:['Steven', 'Spielberg']),
			new NamedEntity( classification:classification, terms:['George', 'W.', 'Bush']),
			new NamedEntity( classification:classification, terms:['Dalai', 'Lama']),
			new NamedEntity( classification:classification, terms:['Ayrton', 'Senna'])
		]

		def categories = [
			[
				"Primeiros-ministros de Portugal",
				"Altos Comissários das Nações Unidas para os Refugiados",
				"Lisboetas"
			],
			[
				"Cineastas dos Estados Unidos da América",
				"Judeus dos Estados Unidos da América",
				"Ordem do Império Britânico"
			],
			[
				"Presidentes dos Estados Unidos da América",
				"Governadores do Texas",
				"Família Bush"
			],
			[
				"Budismo tibetano",
				"Budistas",
				"Ministros de culto religioso"
			],
			[
				"Canhotos",
				"Pilotos de Fórmula 1 do Brasil",
				"Pilotos de automóvel do Brasil",
				"Paulistanos",
				"Ítalo-brasileiros"]
		]

		log.info "Testing Wikipedia Category Miner with ${category} ${type}"
		nes.eachWithIndex{ne, i ->
			def meanings = catminer.detectMeanings(ne.terms, categories[i], meanings, catminer.rules, Detector.COLLECT_ALL_MATCHES)
			//println "${ne.terms}: ${meanings}"
			assert null != meanings.meanings.find{
				it.category == ne.category[0] &&
						it.type == ne.type[0] && it.subtype == ne.subtype[0] }
		}
	}

	void testOrganizacaoEmpresa() {

		classification = [SC.organization_company]


		def nes = [
			new NamedEntity( classification:classification, terms:['Microsoft']),
			new NamedEntity( classification:classification, terms:['Sun', 'Microsystems']),
			new NamedEntity( classification:classification, terms:['Coca-Cola']),
			new NamedEntity( classification:classification, terms:['SONAE']),
			new NamedEntity( classification:classification, terms:['FIAT'])
		]

		def categories = [
			[
				"Empresas de informática",
				"Empresas dos Estados Unidos da América",
				"CRM"
			],
			[
				"Empresas dos Estados Unidos da América",
				"Empresas de informática"
			],
			[
				"Empresas dos Estados Unidos da América",
				"Bebidas de cola"
			],
			[
				"Empresas sediadas no Grande Porto",
				"Empresas de Portugal"
			],
			[
				"Fiat",
				"Empresas da Itália"]
		]
		log.info "Testing Wikipedia Category Miner with ${category} ${type}"
		nes.eachWithIndex{ne, i ->
			def meanings = catminer.detectMeanings(ne.terms, categories[i], meanings, catminer.rules, Detector.COLLECT_ALL_MATCHES)
			//println "${ne.terms}: ${meanings}"
			assert null != meanings.meaning.find{
				it.category == ne.category[0] &&
						it.type == ne.type[0] && it.subtype == ne.subtype[0] }
		}
	}

	void testOrganizacaoInstituicao() {

		classification = [SC.organization_institution]

		def nes = [
			new NamedEntity( classification:classification, terms:[
				'Universidade',
				'de',
				'Lisboa']
			),
			new NamedEntity( classification:classification, terms:[
				'Instituto',
				'Nacional',
				'de',
				'Estatística']
			)
		]

		def categories = [
			[
				"Universidades de Portugal",
				"Lisboa"
			],
			// UL,\
			[
				"Instituições de Portugal",
				"Institutos de estatística"]
		]
		log.info "Testing Wikipedia Category Miner with ${category} ${type}"
		nes.eachWithIndex{ne, i ->
			def meanings = catminer.detectMeanings(ne.terms, categories[i], meanings, catminer.rules, Detector.COLLECT_ALL_MATCHES)
			println "${ne.terms}: ${meanings}"
			assert null != meanings.meanings.find{
				it.category == ne.category[0] &&
						it.type == ne.type[0] && it.subtype == ne.subtype[0] }
		}
	}

	void testOrganizacaoAdministracao() {

		classification = [
			SC.organization_administration
		]

		def nes = [
			new NamedEntity( classification:classification, terms:['PS'])
		]

		def categories =[
			[
				'Partidos Políticos de Portugal']
		]

		log.info "Testing Wikipedia Category Miner with ${category} ${type}"
		nes.eachWithIndex{ne, i ->
			def meanings = catminer.detectMeanings(ne.terms, categories[i], meanings, catminer.rules, Detector.COLLECT_ALL_MATCHES)
			println "${ne.terms}: ${meanings}"
			assert null != meanings.meanings.find{
				it.category == ne.category[0] &&
						it.type == ne.type[0] && it.subtype == ne.subtype[0] }
		}
	}
}


