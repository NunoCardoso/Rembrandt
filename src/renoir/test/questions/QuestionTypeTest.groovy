package renoir.test.questions

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import renoir.obj.*
import renoir.rules.*
import rembrandt.obj.*
import saskia.bin.Configuration
import com.thoughtworks.xstream.*

/**
 * Check the detection of question types
 * note: do "echo "XXX" | native2ascii -encoding UTF-8"  to get the \\u XXXX code
 * @author Nuno Cardoso
 */
class QuestionTypeTest extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
   	QuestionAnalyser qa
	Question q 
	Configuration conf
	
	public QuestionTypeTest() {
		// initialize
		 conf = Configuration.newInstance()
	}
    
    void testQTypes() {
	
		// How Much
		Sentence sentence = Sentence.simpleTokenize("Quantos filhos tem o Rembrandt?")
		q = new Question(sentence)	
		assert q.questionType == QuestionType.None
		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.HowMuch, "Question type is ${q.questionType}"
		
		// Which
		sentence = Sentence.simpleTokenize("Qual foi o quadro mais importante de Rembrandt?")
		q = new Question(sentence)	
		assert q.questionType == QuestionType.None
		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.Which, "Question type is ${q.questionType}"
		
		sentence = Sentence.simpleTokenize("Em que s\u00e9culo nasceu Saskia?")
		q = new Question(sentence)	
		assert q.questionType == QuestionType.None
		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.Which, "Question type is ${q.questionType}"
		
		sentence = Sentence.simpleTokenize("Que pa\u00eds albergou Saskia?")
		q = new Question(sentence)	
		assert q.questionType == QuestionType.None
		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.Which, "Question type is ${q.questionType}"
		
		sentence = Sentence.simpleTokenize("Como se chama a esposa de Renoir?")
		q = new Question(sentence)	
		assert q.questionType == QuestionType.None
		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.Which, "Question type is ${q.questionType}"
		
		sentence = Sentence.simpleTokenize("Qual o nome das filhas de Renoir?")
		q = new Question(sentence)	
		assert q.questionType == QuestionType.None
		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.Which, "Question type is ${q.questionType}"
		
		// Who
		sentence = Sentence.simpleTokenize("Quem matou Rembrandt?")
		q = new Question(sentence)	
		assert q.questionType == QuestionType.None
		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.Who, "Question type is ${q.questionType}"
		
		// Where
		sentence = Sentence.simpleTokenize("Onde nasceu Rembrandt?")
		q = new Question(sentence)	
		assert q.questionType == QuestionType.None
		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.Where, "Question type is ${q.questionType}"
		
		// When
		sentence = Sentence.simpleTokenize("Quando nasceu Rembrandt?")
		q = new Question(sentence)	
		assert q.questionType == QuestionType.None
		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.When, "Question type is ${q.questionType}"
		
		// Why
		sentence = Sentence.simpleTokenize("Porque \u00e9 que morreu Rembrandt?")
		q = new Question(sentence)	
		assert q.questionType == QuestionType.None
		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.Why, "Question type is ${q.questionType}"
		
		// What
		sentence = Sentence.simpleTokenize("O que \u00e9 um pincel?")
		q = new Question(sentence)	
		assert q.questionType == QuestionType.None
		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.What, "Question type is ${q.questionType}"
		
		// How
		sentence = Sentence.simpleTokenize("Como morreu Renoir?")
		q = new Question(sentence)	
		assert q.questionType == QuestionType.None
		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.How, "Question type is ${q.questionType}"
    }  

}

/*


#18
onde	onde	ADV_aloc_interr	0	0	0	ADVS>	0
fica	ficar	V_fmc	PR_IND	3S	0	FMV	0
Lisboa	Lisboa	PROP_civ	0	S	F	<SUBJ	0
?	?	PU	0	0	0	PONT	0

#19
onde	onde	ADV_aloc_interr	0	0	0	ADVS>	0
fica	ficar	V_fmc	PR_IND	3S	0	FMV	0
Portugal	Portugal	PROP_civ	0	S	M	<SUBJ	0
?	?	PU	0	0	0	PONT	0

#20
onde	onde	ADV_aloc_interr	0	0	0	ADVS>	0
fica	ficar	V_fmc	PR_IND	3S	0	FMV	0
a	a	PRP	0	0	0	<SC	0
Google	Google	PROP_org	0	S	M/F	P<	0
?	?	PU	0	0	0	PONT	0

#21
onde	onde	ADV_aloc_interr	0	0	0	ADVL>	0
nasceu	nascer	V_fmc	PS_IND	3S	0	FMV	0
José	José=Saramago	PROP_hum	0	S	M	<SUBJ	0
Saramago	José=Saramago	PROP_hum	0	S	M	<SUBJ	0
?	?	PU	0	0	0	PONT	0

#22
como	como	ADV_interr	0	0	0	ADVL>	0
se	se	PERS_obj	ACC	3S	M/F	ACC>	0
chama	chamar	V_fmc	PR_IND	3S	0	FMV	0
a	o	DET_artd	0	S	F	>N	0
capital	capital	N_ac-sign_Lciv	0	S	F	<OC	0
de	de	PRP	0	0	0	N<	0
Portugal	Portugal	PROP_civ	0	S	M	P<	0
?	?	PU	0	0	0	PONT	0

#23
como	como	ADV_interr	0	0	0	ADVL>	0
se	se	PERS_obj	ACC	3S	M/F	ACC>	0
chama	chamar	V_fmc	PR_IND	3S	0	FMV	0
José	José=Saramago	PROP_hum	0	S	M	<OC	0
Saramago	José=Saramago	PROP_hum	0	S	M	<OC	0
?	?	PU	0	0	0	PONT	0

#24
quando	quando	ADV_interr	0	0	0	ADVL>	0
nasceu	nascer	V_fmc	PS_IND	3S	0	FMV	0
José	José=Saramago	PROP_hum	0	S	M	<SUBJ	0
Saramago	José=Saramago	PROP_hum	0	S	M	<SUBJ	0
?	?	PU	0	0	0	PONT	0

#25
quando	quando	ADV_interr	0	0	0	ADVL>	0
morreu	morrer	V_fmc	PS_IND	3S	0	FMV	0
Adolf	Adolf=Hitler	PROP_hum	0	S	M	<SUBJ	0
Hitler	Adolf=Hitler	PROP_hum	0	S	M	<SUBJ	0
?	?	PU	0	0	0	PONT	0

#26
em	em	PRP	0	0	0	ADVL>	0
que	que	DET_interr	0	S	M	>N	0
ano	ano	N_unit_dur_temp_per	0	S	M	SUBJ>	0
nasceu	nascer	V_fmc	PS_IND	3S	0	FMV	0
José	José=Saramago	PROP_hum	0	S	M	<SC	0
Saramago	José=Saramago	PROP_hum	0	S	M	<SC	0
?	?	PU	0	0	0	PONT	0

#27
em	em	PRP	0	0	0	ADVL>	0
que	que	DET_interr	0	S	F	>N	0
cidade	cidade	N_Lciv	0	S	F	SUBJ>	0
nasceu	nascer	V_fmc	PS_IND	3S	0	FMV	0
José	José=Saramago	PROP_hum	0	S	M	<SC	0
Saramago	José=Saramago	PROP_hum	0	S	M	<SC	0
?	?	PU	0	0	0	PONT	0

*/
