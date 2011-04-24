package renoir.test.gikiclef

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import renoir.obj.*
import renoir.bin.*
import renoir.rules.*
import rembrandt.obj.*
import saskia.bin.Configuration
import com.thoughtworks.xstream.*
import saskia.dbpedia.DBpediaAPI
import saskia.wikipedia.WikipediaAPI
import saskia.wikipedia.WikipediaDocument

class TestTopic02 extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
   	QuestionAnalyser qa
	Question q 
	Configuration conf
	
	public TestTopic02() {
		// initialize
		conf = Configuration.newInstance()
		def path = "data/gikiclef"
		// Force the setup to use the training stuff
		conf.set("saskia.dbpedia.mode", "dbpedia")
		qa = new QuestionAnalyser()
	}
	
	void testGenerateTopic02() {

	 // ****  T�pico: ***	
	 String linguateca_line ="""Que	que	DET_interr	0	P	M	>N	0
pa�ses	pa�s	N	0	P	M	SUBJ>	0
t�m	ter	V_fmc	PR_IND	3P	0	FMV	0
branco	branco	N	0	S	M	<ACC	0
,	,	PU	0	0	0	PONT	0
verde	verde	ADJ	0	S	M	N<PRED	0
e	e	KC_co-pred	0	0	0	CO	0
vermelho	vermelho	ADJ	0	S	M	N<PRED	0
na	em+o	PRP+DET_artd	0	S	F	<ADVL+>N	0
sua	seu	DET_poss_3S_si	0	S	F	>N	0
bandeira	bandeira	N	0	S	F	P<	0
nacional	nacional	ADJ	0	S	F	N<	0
?	?	PU	0	0	0	PONT	0
"""
			
	 SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(
		linguateca_line, SentenceWithPoS.FROM_LINGUATECA_PoS)
  	 q = new Question(sentence)
	 WriteRun wr = new WriteRun()
 	 q.id = "GC-2009-02"
	
	 // Assert question type 
	 assert q.questionType == QuestionType.None	
	 qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
	 assert q.questionType == QuestionType.Which, "Question type is ${q.questionType}"

	 // Assert question subjects 
     qa.applyRules(q, GikiCLEFQuestionRulesPT.gikiclefRules)
	 assert q.subject.terms*.text == ["pa�ses"], "Got ${q.subject.terms*.text} instead"
	 assert q.subject.subtypeHAREM.contains("PAIS"), "got ${q.subject.subtypeHAREM} instead"

	// Assert Question EAT
	 qa.executeRules(q, QuestionRulesPT.rulesToDetectEAT)
	 assert q.expectedAnswerType.subtypeHAREM.contains('PAIS')
//	 println q.expectedAnswerType.resolvesTo
	 assert q.expectedAnswerType.resolvesTo == ExpectedAnswerType.Type.DBpediaOntologyClass
	
	// strip stopwords
	println "Question sentence: ${q.sentence}"
	Sentence s2 = saskia.gazetteers.StopwordsPT.removeStopwords(q.sentence)
	Sentence s3 = saskia.gazetteers.StopwordsPT.stripPunctuation(s2)
	println "Question w/o stopwords and w/o punctuation: $s3" 

	// let's create a pool of documents that are countries 
//select distinct page_id, page_title from pt_page, pt_categorylinks where 
//cl_from = page_id and cl_to LIKE "Pa�ses%" and page_namespace=0 order by page_id
// tudo acima de 1000000 n�o vale a pena.

/*   List ids = [3374, 21472,1510119, 1826610, 11682, 1444910, 166945, 41001, 818181, 35364,
 648610, 526272, 1244604, 1422288,  536394, 818187, 60810, 1440419, 1748038, 304, 3862, 3847,
 919, 560702, 14378, 37382, 175371, 3863, 3861, 41435, 3864, 497, 790, 639392, 4011, 4739, 4567, 
4010, 4566, 4672, 4737, 3952, 497, 790, 3865, 1274, 3921, 451727, 211401, 304, 306, 308, 1979, 
3908, 3856, 3862, 456, 3907, 3880, 3847, 3843, 3858, 3846, 785, 3951, 3873, 827, 3802, 919, 3859, 
3852, 1039, 3874, 3845, 3855, 3831, 3849, 3875, 37382, 3833, 1354, 3374, 1583, 1480, 1661, 3844, 
1035, 3863, 3861, 1669, 3850, 12141, 3853, 3832, 41435, 3864, 3851, 309, 4655, 4743, 179076, 4678, 
4674, 4741, 1332, 4019, 4629, 4811, 4791, 4789, 4742, 253, 308, 3916, 3908, 3915, 3959, 4569, 3935, 
3955, 3907, 4745, 3880, 3899, 3928, 3946, 3913, 3954, 3802, 1983, 4018, 3878, 3877, 3919, 3940, 1094, 
3918, 3917, 3933, 3920, 4498, 4016, 3934, 3902, 3936, 3912, 3911, 3914, 3931, 1669, 4568, 3960, 3879, 
4905, 3956, 4583, 3910, 3864, 3938, 265, 404, 537, 4668, 939, 1246, 1480, 1730, 4583, 265, 4500, 4658, 
4725, 4659, 4505, 537, 4509, 3970, 4715, 4508, 4706, 3963, 427416, 3946, 3943, 3965, 4667, 4705, 4707, 
4668, 939, 4755, 4756, 20799, 4753, 4717, 4664, 4660, 4663, 4661, 4761, 1246, 4665, 4511, 4499, 3966, 
3969, 3968, 4503, 4708, 4754, 4758, 3962, 4714, 3944, 1730, 4502, 4704, 4501, 3967, 2631, 4506, 4713, 
305, 306, 309, 456, 497, 3928, 3843, 785, 790, 3873, 827, 919, 3852, 3919, 1039, 1094, 3845, 3831, 
3833, 1354, 1332, 3374, 1480, 1661, 3929, 1035, 3850, 4568, 3853, 3832, 3851, 1979, 1088991, 1079147, 
1121132, 3951, 3874, 3855, 391451, 4822, 3953, 4828, 192727, 4819, 4829, 4008, 4565, 4826, 4806, 4827, 
4768, 13182, 111377, 255, 4770, 404, 4771, 192727, 3946, 3954, 4010, 3859, 1983, 4018, 1274, 4511, 
3911, 4769, 522, 3956, 4502, 3864, 1873, 1910, 2631, 4713, 70077, 454328, 1043221, 1345217, 51543, 
1273968, 279924, 507530, 646477, 1450457, 128894, 804117, 622564, 811263, 51546, 841435, 129966, 
43492, 1411261, 874038, 451727, 51539, 75856, 45361, 211401, 1681087, 1046369, 1047267, 1047265, 
668556, 1046330, 1046339, 1046350, 267684, 576994, 1047267, 1065983, 830224, 830212, 1149388, 
433963, 654194, 668316, 668520]
	*/
	
	//new PlanB_Strategy1().getWikipediaRembrandtedPages(ids)
	
//	PlanB_Strategy3 planb3 = new PlanB_Strategy3()
//	plan1.
	/*List<RembrandtedDoc> docs = planb3.getDocsFromQueryAndDBpediaClass(
		s3.toStringLine(), q.expectedAnswerType.ontologyClass)
	docs.each{doc -> 
		println "Got document $doc, pulling out resource "+ doc.doc_entity.ent_dbpedia_resource + " as answer."
		q.answer << doc.doc_entity.ent_dbpedia_resource
	}


		
	List answer = wr.format(q, "pt")
	answer.each{it -> println it.line}
*/
   }

}
    