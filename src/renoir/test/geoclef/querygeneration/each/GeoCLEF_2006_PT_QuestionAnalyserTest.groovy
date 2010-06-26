
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
package renoir.test.geoclef.querygeneration.each


import groovy.util.GroovyTestCase
import org.apache.log4j.Logger
import saskia.bin.Configuration
import renoir.obj.*
import renoir.rules.*
import rembrandt.obj.Document
import rembrandt.bin.*
import rembrandt.tokenizer.*
/**
 * @author Nuno Cardoso
 *
 */
 class GeoCLEF_2006_PT_QuestionAnalyserTest extends GroovyTestCase{
        
    static Logger log = Logger.getLogger("SaskiaTest")
    Configuration conf = Configuration.newInstance()
    Map topics = [:]
                  
    public GeoCLEF_PT_QuestionAnalyserTest() {
	conf = Configuration.newInstance()
	String fileseparator = System.getProperty("file.separator")
	String homedir = conf.get("rembrandt.home.dir",".")
    
	String topicfile = homedir+fileseparator+"resources"+fileseparator+"eval"+
            fileseparator+"geoclef"+fileseparator+"topics"+fileseparator+"GeoCLEF_PT_2006_baseline_queries.txt"
       
    
       	File topicf = new File(topicfile)
	
	topicf.eachLine{l -> 
	    def m = l =~ /label:(\d+) (.*)/
	    if (m.matches()) {
		topics[Integer.parseInt(m.group(1))] = m.group(2)
	    } else {
		log.warn "did NOT matched line $l" 
	    }
	 }        // Force the 
    }
  
       
    Question process(String s, String lang) {
	Question question = new Question()
	List sentences = TokenizerPT.newInstance().parse(s)
	
	question.sentence = sentences[0]
	QuestionAnalyser qa = new QuestionAnalyser()
	    
	RembrandtCore core = Class.forName("rembrandt.bin.RembrandtCore"+lang.toUpperCase()+"forHAREM").newInstance() 	    
	Document doc= new Document()
	doc.body_sentences = [question.sentence.clone()]
	doc.indexBody()
	doc = core.releaseRembrandtOnDocument(doc)	
	question.nes = doc.bodyNEs
       // Rembrandt the sentence, add NEs to the Question NE list
	// good, now for the 'apply rules', we can load these NEs and use rules that have NEMatch clauses
	
	    
	QuestionRules qr = Class.forName("renoir.rules.QuestionRules2"+lang.toUpperCase()).newInstance() 	    
	println "question1: q sentenbce: ${q.sentence} "
	qa.applyRules(question, qr.rulesToDetectQuestionType)
	println "question2: q sentenbce: ${q.sentence} "
	qa.applyRules(question, qr.rulesToCaptureSubjects)
	qa.applyRules(question, qr.rulesToCaptureConditions)
	qa.applyRules(question, qr.rulesToCaptureEAT)
	return question
    }
    
    /**
label:026 Regiões vinícolas na margem de rios da Europa  
label:027 Cidades a menos de 100 quilómetros de Francoforte
label:028 Tempestades de neve na América do Norte 
label:029 Comércio de diamantes em Angola e na África do Sul  
label:030 Carros armadilhados nos arredores de Madrid 
label:031 Combates e embargo no norte do Iraque 
label:032 Movimento para a independência do Quebec 
label:033 Competições desportivas internacionais no Ruhr 
label:034 Malária nos trópicos  
label:035 Empréstimos ao antigo Bloco de Leste  
label:036 Indústria automóvel no Mar do Japão  
label:037 Descobertas arqueológicas no Oriente Médio 
label:038 Eclipses solar e lunar no Sudoeste Asiático  
label:039 Tropas russas no sul do Cáucaso  
label:040 Cidades perto de vulcões activos 
label:041 Naufrágios no Oceano Atlântico  
label:042 Eleições regionais no norte da Alemanha  
label:043 Pesquisa científica em universidades da Nova Inglaterra  
label:044 Venda de armas na antiga Jugoslávia  
label:045 Turismo no nordeste do Brasil  
label:046 Fogos florestais no norte de Portugal  
label:047 Jogos da Liga dos Campeões no Mediterrâneo  
label:048 Pescas na Terra Nova e na Gronelândia  
label:049 A ETA em França  
label:050 Cidades no Danúbio e Reno  
*/ 
/*    void test026() {
	   int topicid = 16
	  println "Loading topic $topicid: ${topic[topicid]}"
	   
	   if (topic)
	  
	Question question = process(topic[topicid], "pt")
	println question.dump()
	println "Question sentence: ${question.sentence}"
	println "Question explanation: ${question.explanation}"
	println "Question expectedAnswerType: ${question.expectedAnswerType}"
	println "Question questionType: ${question.questionType}"
	println "Question questionTypeTerms: ${question.questionTypeTerm}"
 	println "Question NEs: ${question.nes}"           
       	println "Question subject: ${question.subject}"           
       	println "Question conditions: ${question.conditions}"      
	}

    }*/
   
}
