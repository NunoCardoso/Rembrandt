package renoir.gikiclef

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import com.thoughtworks.xstream.*

import java.util.zip.*

import renoir.obj.*
import renoir.rules.*
import renoir.bin.WriteRun

import rembrandt.obj.*
import rembrandt.util.XMLUtil
import rembrandt.bin.*

import saskia.bin.Configuration
import saskia.dbpedia.*
import saskia.ontology.*

import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag
import saskia.io.RembrandtedDoc

class TestTopic01 extends GroovyTestCase {

	def Logger log = Logger.getLogger("jUnitTest")
   	QuestionAnalyser qa
	Question q 
	Configuration conf
	XStream xstream
	WriteRun wr
	RembrandtReader reader
     
	public TestTopic01() {
		// initialize
		conf = Configuration.newInstance()
		def path = "data/gikiclef"
		// Force the setup to use the training stuff
		conf.set("saskia.dbpedia.mode", "webservice")
//		conf.set("saskia.dbpedia.mode", "local")
//		conf.set("saskia.dbpedia.local.files","")
		qa = new QuestionAnalyser()
		xstream = new XStream()
		wr =  new WriteRun()
		reader = new RembrandtReader(new RembrandtStyleTag("pt"))	
	}
	
	void testGenerateTopic01() {

	 // ****  T�pico: ***	
	 String linguateca_line = """
Lugares	lugar	N	0	P	M	NPHR	0
em	em	PRP	0	0	0	N<	0
It�lia	It�lia	PROP	0	S	F	P<	0
que	que	SPEC_rel	0	S	M	ACC>_#FS-N<	0
Ernest	Ernest=Hemingway	PROP	0	S	M	SUBJ>	0
Hemingway	Ernest=Hemingway	PROP	0	S	M	SUBJ>	0
visitou	visitar	V	PS_IND	3S	0	FMV	0
.	.	PU	0	0	0	PONT	0"""
			
	 SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(
		linguateca_line, SentenceWithPoS.FROM_LINGUATECA_PoS)
  	 q = new Question(sentence)
	 q.id = "GC-2009-01"
	
	 // Assert qestion type 
	 assert q.questionType == QuestionType.None	
	 qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
	 assert q.questionType == QuestionType.Which, "Question type is ${q.questionType}"
	
	 // Assert query parse
     qa.applyRules(q, GikiCLEFQuestionRulesPT.gikiclefRules)

	 assert q.subject.terms*.text == ["Lugares","em","It�lia"], "Got ${q.subject.terms*.text} instead"
	 assert q.subject.scope*.text == ['It�lia'], "Got ${q.subject.scope*.text} instead"
	 assert q.subject.groundedScope.name == ['It�lia'], "Got ${q.subject.groundedScope.name} instead"

	 assert q.subject.ontologyDBpedia == ['http://dbpedia.org/ontology/Place'], \
		"Got ${q.subject.ontologyDBpedia} instead"
	 assert q.subject.categoryHAREM == ['LOCAL'], "Got ${q.subject.categoryHAREM} instead"

	 assert q.conditions.size() == 1, "It should have one condition, it has ${q.conditions.size()}"
	 Condition c = q.conditions[0]
	
	 assert c.predicate.terms.size() == 1, "Condition predicate has ${c.predicate.terms.size()} terms."
	 assert c.predicate.terms[0].text == "visitou"
	 assert c.predicate.terms[0].lemma == "visitar"
	// n�o sei a que mapear em DBpediaPropertyDefinitionsPT, fica vazio
	 assert c.predicate.dbpedia_ontology_property == [], \
	    "Got ${c.predicate.dbpedia_ontology_property} instead"

	 assert c.object.ne.terms*.text == ["Ernest","Hemingway"], "Got ${c.object.ne.terms*.text} instead."
	 assert c.object.ne.classification*.category == ["PESSOA"], "Got ${c.object.ne.category} instead."
	 assert c.object.ne.dbpediaPage == ["http://dbpedia.org/resource/Ernest_Hemingway"], \
		"Got ${c.object.ne.dbpediaPage} instead"
		

	 assert c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/Writer"), \
		"Condition dbpediaOntologyClass: ${c.object.dbpediaOntologyClass}"
	 assert c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:Writers_from_Chicago"), \
		"Condition dbpediaWikipediaCategory: ${c.object.dbpediaWikipediaCategory}"

	// Assert Question EAT
	 qa.executeRules(q, QuestionRulesPT.rulesToDetectEAT)
	 assert q.expectedAnswerType.categoryHAREM.contains('LOCAL')
//	 assert q.expectedAnswerType.resolvesTo == ExpectedAnswerType.OntologyClass
	
	 // Assert results
	
	 //Question2SPARQL qd = new Question2SPARQL()
	// qd.query(q)
	
	// no results, because there's no predicate to map
	 assert q.answer == []
	 assert q.answerJustification == []
	
	/******** plan B *******/
	
	WGO_API wgo = WGO_API.newInstance()
	
	// Add Wikipedia page id of Ernest Hemingway to the list of pages to visit.  
	// Get tagged version of Wikipedia pages of Ernest Hemingway 
	List<RembrandtedDoc> docs = RembrandtedDoc.getFromOriginalIDs(
		c.object.ne.wikipediaPage.keySet().toList())

	log.info "Got RembrandtedDoc pages ${docs} for object:${c.object.ne}" 
	
	// filter docs with a NE that matches EAT. 
	// if the EAT is a LOCAL, check ontology for partOf
	 
	def categoryNeedles = []
	def categoryCriteria = []
	if (q.expectedAnswerType.categoryHAREM) {
		categoryNeedles << q.expectedAnswerType.categoryHAREM
		categoryCriteria << ClassificationCriteria.AnyCategoryIn
	} 
	if (q.expectedAnswerType.typeHAREM) {
		categoryNeedles << q.expectedAnswerType.typeHAREM
		categoryCriteria << ClassificationCriteria.AnyTypeIn
	} 
	if (q.expectedAnswerType.subtypeHAREM) {
		categoryNeedles << q.expectedAnswerType.subtypeHAREM
		categoryCriteria << ClassificationCriteria.AnySubtypeIn
	} 
	
	log.info "EAT matching condition needle: $categoryNeedles criteria: $categoryCriteria" 

	docs.each{d -> 
		log.debug "Getting NEs for doc $d..." 
        
		Document doc =reader.createDocument(d.rdoc_content)
        
		log.debug "done."
		// check for NEs that match the EAT criteria
		def goodNEs = []
		
		if (doc.bodyNEs.isEmpty()) log.warn "Doc ${doc} has no NEs in it... something went wrong."
		doc.bodyNEs.each{ne -> 
			log.info "Analysing NE ${ne}..."
			def match = ne.matchesClassification(categoryNeedles, categoryCriteria)
			//log.debug "ne matches $categoryNeedles, $categoryCriteria ? $match"
			if (ne.matchesClassification(categoryNeedles, categoryCriteria)) {
				log.info "NE ${ne} matched the EAT criteria! "
				
				// if they are LOCAL, let's check ontology.
				// if they are not, let's check terms 
				if (q.expectedAnswerType.categoryHAREM.contains("LOCAL")) {
					log.info "EAT is LOCAL... let's use the ontology."
					// larger feature: 
					def largerFeat = q.subject.groundedScope.name.join(" ")
					log.debug "larger feat: $largerFeat"
					def smallerFeat = ne.printTerms()
					log.debug "smaller feat: $smallerFeat"
				
					if (smallerFeat.toLowerCase().equals(largerFeat.toLowerCase())) {
						log.debug "small feat and larger feat have same terms! Adding ${ne} to goodNEs"
						goodNEs << ne
					} else {
						def smallerfeatid = wgo.getFeatureResourcesFromPrefLabel(smallerFeat, "pt")
						log.debug "$smallerFeat has a WGO resource $smallerfeatid"
						def largerfeatid = wgo.getFeatureResourcesFromPrefLabel(largerFeat, "pt")
						log.debug "$largerFeat has a WGO resource $largerfeatid"
						def answer = wgo.isPartOf(smallerfeatid, largerfeatid)
						if (answer) {
							log.debug "Yes, $smallerfeatid is part of $largerfeatid. Adding to goodNEs"
							goodNEs << ne
						}	
					}
				} else {
					log.debug "EAT is not LOCAL... adding to goodNE."
					goodNEs << ne
				}
				// check if they are the same, before going to ontology.
			}
			
		}//each ne	
		
		// add to answer
		goodNEs.each{ne -> 
			ne.dbpediaPage.each{
				q.answer << it.toString()
			}
			c.object.ne.dbpediaPage.each{
				q.answerJustification << it.toString()
			}
		}	
	}//each doc
	q.answer = q.answer.unique()
	q.answerJustification = q.answerJustification.unique()
	
/* For doc 1:Ernest Hemingway:1:0.8.6, 
 * goodNEs = [NE:52:15:1:cat(LOCAL,LOCAL):typ(HUMANO,HUMANO):sub(PAIS,DIVISAO):[It?lia], 
 * NE:167:59:29:cat(LOCAL):typ(HUMANO):sub(PAIS):[It?lia]]
*/	

	assert q.answer.contains("http://dbpedia.org/resource/Italy"), "Got ${q.answer} instead"
	assert q.answerJustification.contains("http://dbpedia.org/resource/Ernest_Hemingway"), "Got ${q.answerJustification} instead"
	
	List answer = wr.format(q, "pt")
	answer.each{it -> 
		// map with keys line, boolAnswer and boolJust
//		println ""+it.boolAnswer+" "+it.boolJust+" "+it.line
	    println ""+it.boolAnswer+" "+it.boolJust+" "+it.line
	}

	}
   
}
    