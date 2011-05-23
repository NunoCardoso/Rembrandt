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
package renoir.bin

import org.apache.commons.cli.*
import org.apache.log4j.Logger
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.LgteIsolatedIndexReader
import org.apache.lucene.index.Term
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermsFilter

import pt.utl.ist.lucene.LgteHits
import pt.utl.ist.lucene.LgteIndexManager
import pt.utl.ist.lucene.LgteIndexSearcherWrapper
import pt.utl.ist.lucene.LgteQuery
import pt.utl.ist.lucene.LgteQueryParser
import pt.utl.ist.lucene.Model
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer
import pt.utl.ist.lucene.utils.LgteAnalyzerManager
import rembrandt.bin.Rembrandt
import rembrandt.bin.RembrandtCore
import rembrandt.obj.Document
import renoir.obj.Question
import renoir.obj.QuestionAnalyser
import renoir.obj.ReformulatedQuery
import renoir.obj.RenoirQuery
import renoir.obj.RenoirQueryParser
import renoir.rules.*
import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 * This is the class that takes initial queries, reformulates them, then retrieves docs.
 *  Parameters I'll likely to receive: 
 *  1. For RENOIR:
 *    qe: [no, BRF - Blind Relevance Feedback, SQR - Semantic Query Reformulation] 
 *    search:true or false
 *    eat: (?)
 *    output: output format
 *    limit: limit results
 *    offset: offset results
 *    
 *  2. For LGTE:
 *    model:[BM25]
 *    contents-weight
 *    ne-X-weight:
 *    entity-weight:
 *    woeid-weight:
 *    time-weight:
 *    
 *  3. For Indexes: 
 *    title:
 *    contents: or nothing (default)
 *    ne-X:
 *    entity;
 *    woeid:
 *    time/tg:
 */
class Renoir {

	Configuration conf
	String idx_dir
	static String mainIndexDir = "index"
	static String mainCollectionPrefix = "col"
	LgteIndexSearcherWrapper searcher
	static Logger log = Logger.getLogger("RenoirMain")
	String lang
	boolean stem
	LgteBrokerStemAnalyzer lgteBrokerStemAnalyzer

	public Renoir(Configuration conf, String collection_index_dir, String lang, boolean stem = false) {

		log.info "Renoir initialization: collection_index_dir=$collection_index_dir, lang=$lang, stem=$stem"
		this.conf=conf
		this.lang=lang
		this.stem=stem
		idx_dir = collection_index_dir
		IndexReader readerContents, readerGeo, readerTime, readerNEs, readerEntity

		File f
		if (stem) {
			f = new File(idx_dir+"/term-stem-index")
			if (!f.isDirectory()) throw new IllegalStateException("There is NO term index in $idx_dir")
			readerContents = LgteIndexManager.openReader(idx_dir+"/term-stem-index", Model.OkapiBM25Model)
		} else {
			f = new File(idx_dir+"/term-nostem-index")
			if (!f.isDirectory()) throw new IllegalStateException("There is NO term index in $idx_dir")
			readerContents = LgteIndexManager.openReader(idx_dir+"/term-nostem-index", Model.OkapiBM25Model)
		}
		f = new File(idx_dir+"/geo-index")
		if (f.isDirectory()) readerGeo = LgteIndexManager.openReader(idx_dir+"/geo-index", Model.OkapiBM25Model)
		else log.warn "Did not found geo-index in $idx_dir"

		f = new File(idx_dir+"/time-index")
		if (f.isDirectory()) readerTime = LgteIndexManager.openReader(idx_dir+"/time-index", Model.OkapiBM25Model)
		else log.warn "Did not found time-index in $idx_dir"

		f = new File(idx_dir+"/ne-index")
		if (f.isDirectory()) readerNEs = LgteIndexManager.openReader(idx_dir+"/ne-index", Model.OkapiBM25Model)
		else log.warn "Did not found ne-index in $idx_dir"

		f = new File(idx_dir+"/entity-index")
		if (f.isDirectory()) readerEntity = LgteIndexManager.openReader(idx_dir+"/entity-index", Model.OkapiBM25Model)
		else log.warn "Did not found entity-index in $idx_dir"

		Map readers = [:]

		if (readerContents) readers[conf.get("saskia.index.contents_label","contents")] = readerContents
		if (readerContents) readers[conf.get("saskia.index.id_label","id")] = readerContents
		if (readerContents) readers[conf.get("saskia.index.title_label","title")] = readerContents
		if (readerGeo) readers[conf.get("saskia.index.woeid_label","woeid")] = readerGeo
		if (readerTime) readers[conf.get("saskia.index.time_label","tg")] = readerTime
		if (readerEntity) readers[conf.get("saskia.index.entity_label","entity")] = readerEntity
		if (readerNEs) readers["regexpr(ne-.*)"] = readerNEs

		searcher = new LgteIndexSearcherWrapper( Model.OkapiBM25Model,
				new LgteIsolatedIndexReader(readers) )

		// now let's reconstruct the analyzers used for indexation, to regenerate the explanations
		Map analyzerMap = [:]
		analyzerMap.put(conf.get("saskia.index.id_label","id"), new LgteNothingAnalyzer())
		analyzerMap.put(conf.get("saskia.index.docid_label","docid"), new LgteNothingAnalyzer())

		def analyzerTermsWithStem
		def analyzerTermsWithoutStem

		def analyzerTerms

		if (lang == "pt") {
			analyzerTermsWithStem = LgteAnalyzerManager.getInstance().getLanguagePackage(
					"Portuguese", "stopwords_por.txt").getAnalyzerWithStemming()
			analyzerTermsWithoutStem = LgteAnalyzerManager.getInstance().getLanguagePackage(
					"Portuguese", "stopwords_por.txt").getAnalyzerNoStemming()
		}

		if (lang == "en") {
			analyzerTermsWithStem = LgteAnalyzerManager.getInstance().getLanguagePackage(
					"English", "snowball-english.list").getAnalyzerWithStemming()
			analyzerTermsWithoutStem = LgteAnalyzerManager.getInstance().getLanguagePackage(
					"English", "snowball-english.list").getAnalyzerNoStemming()
		}

		if (stem) analyzerTerms = analyzerTermsWithStem
		else  analyzerTerms = analyzerTermsWithoutStem

		analyzerMap.put(conf.get("saskia.index.title_label","title"), analyzerTerms)
		analyzerMap.put(conf.get("saskia.index.contents_label","contents"), analyzerTerms)
		analyzerMap.put(conf.get("saskia.index.woeid_label","woeid"), new LgteNothingAnalyzer())
		analyzerMap.put(conf.get("saskia.index.time_label","tg"), analyzerTermsWithoutStem)
		analyzerMap.put(conf.get("saskia.index.entity_label","entity"),  new LgteNothingAnalyzer())

		analyzerMap.put("ne-PESSOA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-PESSOA-INDIVIDUAL", analyzerTermsWithoutStem)
		analyzerMap.put("ne-PESSOA-GRUPOIND", analyzerTermsWithoutStem)
		analyzerMap.put("ne-PESSOA-CARGO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-PESSOA-GRUPOCARGO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-PESSOA-MEMBRO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-PESSOA-GRUPOMEMBRO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-PESSOA-POVO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-HUMANO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-HUMANO-RUA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-HUMANO-PAIS", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-HUMANO-DIVISAO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-HUMANO-HUMANOREGIAO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-HUMANO-CONSTRUCAO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-FISICO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-FISICO-ILHA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-FISICO-AGUACURSO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-FISICO-AGUAMASSA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-FISICO-RELEVO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-FISICO-PLANETA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-FISICO-FISICOREGIAO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-VIRTUAL", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-VIRTUAL-COMSOCIAL", analyzerTermsWithoutStem)
		analyzerMap.put("ne-LOCAL-VIRTUAL-SITIO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-ORGANIZACAO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-ORGANIZACAO-ADMINISTRACAO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-ORGANIZACAO-INSTITUICAO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-ORGANIZACAO-EMPRESA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-ACONTECIMENTO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-ACONTECIMENTO-ORGANIZADO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-ACONTECIMENTO-EVENTO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-ACONTECIMENTO-EFEMERIDE", analyzerTermsWithoutStem)
		analyzerMap.put("ne-OBRA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-OBRA-PLANO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-OBRA-REPRODUZIDA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-OBRA-ARTE", analyzerTermsWithoutStem)
		analyzerMap.put("ne-ABSTRACCAO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-ABSTRACCAO-NOME", analyzerTermsWithoutStem)
		analyzerMap.put("ne-ABSTRACCAO-DISCIPLINA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-ABSTRACCAO-ESTADO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-ABSTRACCAO-IDEIA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-COISA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-COISA-CLASSE", analyzerTermsWithoutStem)
		analyzerMap.put("ne-COISA-MEMBROCLASSE", analyzerTermsWithoutStem)
		analyzerMap.put("ne-COISA-OBJECTO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-COISA-SUBSTANCIA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-TEMPO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-TEMPO-TEMPO_CALEND", analyzerTermsWithoutStem)
		analyzerMap.put("ne-TEMPO-TEMPO_CALEND-DATA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-TEMPO-TEMPO_CALEND-HORA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-TEMPO-TEMPO_CALEND-INTERVALO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-TEMPO-GENERICO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-TEMPO-DURACAO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-TEMPO-FREQUENCIA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-NUMERO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-NUMERO-CARDINAL", analyzerTermsWithoutStem)
		analyzerMap.put("ne-NUMERO-ORDINAL", analyzerTermsWithoutStem)
		analyzerMap.put("ne-NUMERO-TEXTUAL", analyzerTermsWithoutStem)
		analyzerMap.put("ne-NUMERO-NUMERAL", analyzerTermsWithoutStem)
		analyzerMap.put("ne-VALOR", analyzerTermsWithoutStem)
		analyzerMap.put("ne-VALOR-MOEDA", analyzerTermsWithoutStem)
		analyzerMap.put("ne-VALOR-QUANTIDADE", analyzerTermsWithoutStem)
		analyzerMap.put("ne-VALOR-CLASSIFICACAO", analyzerTermsWithoutStem)
		analyzerMap.put("ne-EM", analyzerTermsWithoutStem)

		lgteBrokerStemAnalyzer = new LgteBrokerStemAnalyzer(analyzerMap)
	}

	public Map search(String input) {
		RenoirQuery q = RenoirQueryParser.parse(input)
		return search(q)
	}

	public Map search(RenoirQuery q) {

		// DEFAULT VALUES FOR SEARCH //
		boolean search = true // default value
		boolean explain = false // default value
		String qe = "no" // default value
		String model = "BM25Normalized"
		int topkterm = 8
		int topkdoc = 10

		/*** CHECK MODEL ***/
		if (!q.paramsForLGTE.containsKey('model')) {
			log.debug "No model parameter, model is $model by default."
			q.paramsForLGTE['model'] = model
		} else {
			log.debug "RENOIR will use model "+q.paramsForLGTE['model']
		}

		/*** CHECK SEARCH ***/
		if (!q.paramsForRenoir.containsKey('search')) {
			log.debug "No search parameter, search is $search by default."
		} else {
			if (q.paramsForRenoir['search'] == "false") {
				search = false
				log.debug "RENOIR will NOT perform a search, got a search = false."
			} else if (q.paramsForRenoir['search'] == "true") {
				search = true
				log.debug "RENOIR will perform a search, got a search = true."
			}
		}

		/*** CHECK EXPLAIN ***/
		if (!q.paramsForRenoir.containsKey('explain')) {
			log.debug "No explain parameter, explain is $explain by default."
		} else {
			if (q.paramsForRenoir['explain'] == "false") {
				explain = false
				log.debug "RENOIR will NOT explain a search, got a explain = false."
			} else if (q.paramsForRenoir['explain'] == "true") {
				explain = true
				log.debug "RENOIR will perform an explain, got explain = true."
			}
		}

		/*** CHECK QE ***/
		if (q.paramsForRenoir.containsKey('qe'))  qe = q.paramsForRenoir['qe']

		LgteQuery lgteQuery
		Map res = [:]
		res["initial_query_string"] = q.toPlainTermString()   // é o que alimento ao BRF

		if (qe.equalsIgnoreCase("no")) {
			log.debug "No QE, will perform qe $qe by default. Stopwords will be stripped out for lang $lang"
			q.removeStopwordsAndPunctuation(lang)
			//	println "Query  $q stem:$stem lgteBrokerStemAnalyzer = "+lgteBrokerStemAnalyzer
			lgteQuery = q.toLgteQuery(lgteBrokerStemAnalyzer, searcher)
			//	println "LgteQuery: $lgteQuery"
		}

		/** THIS IS WHERE MAGIC HAPPENS **/
		else if (qe =~ /(?i)sqr/) {

			String strategy
			def m = qe =~ /sqr(.*)/
			if (m.matches()) strategy = n.group(1)
			if (!strategy) strategy = "1" // default

			log.debug "QE: Semantic query reformulator $strategy"

			Question question = rq.convertToQuestion(lang)

			// initialize question workers
			QuestionAnalyser qa = new QuestionAnalyser()
			QuestionEATForPT qeat = new QuestionEATForPT()
			QuestionAnswersForPT qanswer = new QuestionAnswersForPT()

			// Reambrandt worker
			RembrandtCore core = Rembrandt.getCore(lang, "harem")
			Document doc= new Document()
			doc.body_sentences = [question.sentence.clone()]
			doc.indexBody()
			doc = core.releaseRembrandtOnDocument(doc)
			question.nes = doc.bodyNEs

			// Rembrandt the sentence, add NEs to the Question NE list
			// good, now for the 'apply rules', we can load these NEs and use rules that have NEMatch clauses
			QuestionRules qr = new QuestionRulesForPT()
			qa.applyRulesBrowseQuestion(question, qr.rulesToDetectQuestionType)
			qa.applyRulesBrowseQuestion(question, qr.rulesToCaptureSubjects)
			qa.applyRulesBrowseQuestion(question, qr.rulesToCaptureConditions)
			// from all captured stuff, let's decide on EAT
			question = qeat.solve(question)
			// let's solve it to answers
			question = qanswer.solve(question)

			ReformulatedQuery refq = Class.forName(
					"renoir.obj.QueryReformulator"+strategy).reformulate(q, question)

			lgteQuery = refq.toLgteQuery(lgteBrokerStemAnalyzer, searcher)
		}

		/** STANDARD BRF **/
		else if (qe.equalsIgnoreCase("BRF")) {
			log.debug "QE: Blind relevance feedback"
			// first retrieval
			log.debug "Stopwords will be stripped out for lang $lang"
			q.removeStopwordsAndPunctuation(lang)

			LgteQuery tempLgteQuery = q.toLgteQuery(lgteBrokerStemAnalyzer, searcher)

			tempLgteQuery.getQueryParams().setQEEnum("lgte") // can be no, text or lgte
			// a queryConfigurationBase tem de ter info de topkterm e topkdoc agora!

			log.debug "Inital lgteQuery: "+tempLgteQuery.query

			Query lucenequery = LgteQueryParser.lucQE(
					tempLgteQuery, q.toPlainTermString(), searcher, null)
			//Eu volto a criar o LgteQuery
			lgteQuery = new LgteQuery(lucenequery, tempLgteQuery.getQueryParams(), lgteBrokerStemAnalyzer)
			log.debug "Reformulated lgteQuery: ${lgteQuery.query}"
		} else {
			log.warn "Unknown qe mode $qe. Ignoring."
		}

		res["final_query_string"] = lgteQuery.getQuery().toString()

		/** PERFORM SEARCH **/
		if (search) {
			log.info "Querying to LGTE: ${lgteQuery.getQuery().toString()}"
			long start = System.currentTimeMillis()

			LgteHits hits
			// let's see if we have filters
			if (q.hasFilters) {
				TermsFilter termfilter = new TermsFilter();
				q.filters.each{term ->
					termfilter.addTerm(new Term(term.field,term.text));
				}
				log.info "Query filters: ${termfilter}"
				hits = searcher.search(lgteQuery, termfilter)
			} else {
				try {
					hits = searcher.search(lgteQuery)
				} catch(IOException e) {
					log.warn "Problem during search: "+e.getMessage()
				}
			}
			long end = System.currentTimeMillis()

			res["time"] = end - start

			if (!hits) {
				res["total"] = 0
				return res
			}

			int total = hits.length()
			res["total"] = total
			int offset = (q.paramsForRenoir.containsKey("offset") ? q.paramsForRenoir["offset"] : 0)
			int limit = (q.paramsForRenoir.containsKey("limit") ? q.paramsForRenoir["limit"] : (total-offset))
			int show = ( (offset + limit ) > total ? total : (offset + limit) )

			res["nr_results_shown"] = limit
			res["nr_first_result"] = offset
			res["nr_last_result"] = show

			res["result"] = []
			for(int i = offset; i < show; i++) {
				// writeRecord(String docId, int hit, Hits hits, float score, String run)
				Map result = [:]
				result["i"] = i
				result["doc_id"] = hits.id(i)
				try {
					result["doc_original_id"] = hits.doc(i).get("id")
				} catch(Exception e) {log.warn "No doc_original_id for doc: "+e.getMessage()}
				result["score"] = hits.score(i)

				if (explain) {
					String explanation
					Map partialscores = [:]

					try {
						// explanation = searcher.indexSearcher.explain(lgteQuery, hits.id(i), lgteBrokerStemAnalyzer)
						explanation = searcher.indexSearcher.explain(lgteQuery.query, hits.id(i))
						//println explanation
						List<Map> partscores
						partscores = explanation.findAll (/([\d\.]+) = subQuery\((.+?):(.+?)\Q*\E(.+?) in doc (.+?)\)/)
						{match, score, field, term, weight, doc ->
							return ["field":""+field, "score":""+score, "term":""+term, "weight":""+weight, "doc":""+doc]}
						//partscores?.each{p ->
						//	partialscores[p.index] = p.score
						//}
						result["partialscore"] = partscores

					} catch(Exception e) {
						//println "explanation unavailable"
						result["partialscore"] = "unavailable"
					}
				}
				res["result"] << result
			}// for
		}// IF SEARCh
		return res
	}
}