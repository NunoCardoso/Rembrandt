package rembrandt.test.rules


import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import saskia.gazetteers.*
import saskia.obj.*
import rembrandt.obj.*
import rembrandt.rules.*

/**
 * @author Nuno Cardoso
 */
class SubjectAndPlaceDetectorTest extends GroovyTestCase {

	static Logger log = Logger.getLogger("UnitTest")
	 SubjectAndPlaceDetector detector
	
	public SubjectAndPlaceDetectorTest() {
	    detector = new SubjectAndPlaceDetector()

	}
    
    void testEnglish() {

	Sentence s = Sentence.simpleTokenize("Portuguese musicians")
	def result = detector.process(s, "en")
	println "result: $result"
	assert result.placeAdjectiveTerms*.text == ["Portuguese"]
	assert result.subjectTerms*.text == ["musicians"]	                                     
	                                              
	s = Sentence.simpleTokenize("Musicians of Portugal")
	result = detector.process(s, "en")	
	assert result.placeNameTerms*.text == ["Portugal"]
	assert result.subjectTerms*.text == ["Musicians"]	        
	                                     
	s = Sentence.simpleTokenize("Rock bands of the United States of America in the 2000s")
	result = detector.process(s, "en")	
	assert result.placeNameTerms*.text == ["United", "States", "of", "America"]
	assert result.subjectTerms*.text == ["Rock","bands"]	        
	
	s = Sentence.simpleTokenize("American rock bands")
	result = detector.process(s, "en")	
	assert result.placeAdjectiveTerms*.text == ["American"]
	assert result.subjectTerms*.text == ["rock","bands"]	    	                                     
    }
	
    void testPortuguese() {
	Sentence s = Sentence.simpleTokenize("Músicos portugueses")
	def result = detector.process(s, "pt")
	assert result.placeAdjectiveTerms*.text == ["portugueses"]
	assert result.subjectTerms*.text == ["Músicos"]	                
	                                     	                                     
	s = Sentence.simpleTokenize("Músicos de Portugal")
	result = detector.process(s, "pt")	
	assert result.placeNameTerms*.text == ["Portugal"]
	assert result.subjectTerms*.text == ["Músicos"]	   

	// de notar o "de" e "dos"... deve ser inteligente a ponto de não ter problenas                                     
	s = Sentence.simpleTokenize("Bandas de rock dos Estados Unidos da América de 2000")
	result = detector.process(s, "pt")	
	assert result.placeNameTerms*.text == ["Estados", "Unidos", "da", "América"]
	assert result.subjectTerms*.text == ["Bandas","de","rock"]	        
	                        	
	s = Sentence.simpleTokenize("Bandas de rock norte-americanas")
	result = detector.process(s, "pt")	
	assert result.placeAdjectiveTerms*.text == ["norte-americanas"]
	assert result.subjectTerms*.text ==  ["Bandas","de","rock"]	     
    }

}	