package rembrandt.test.rules

import rembrandt.rules.PlaceDetector

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import saskia.gazetteers.*
import saskia.obj.*
import rembrandt.obj.*

/**
 * @author Nuno Cardoso
 */
class PlaceDetectorTest extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
	PlaceDetector pl_en, pl_pt
	
	public PlaceDetectorTest() {
	    pl_en = new PlaceDetector("en")
	    pl_pt = new PlaceDetector("pt")
	}
    
    void testEnglish() {

	Sentence s = Sentence.simpleTokenize("Portuguese musicians")
	def result = pl_en.process(s)
	assert result.placeAdjectiveTerms*.text == ["Portuguese"]
	assert result.subjectTerms*.text == ["musicians"]	                                     
	                                              
	s = Sentence.simpleTokenize("Musicians of Portugal")
	result = pl_en.process(s)	
	assert result.placeNameTerms*.text == ["Portugal"]
	assert result.subjectTerms*.text == ["Musicians"]	        
	                                     
	s = Sentence.simpleTokenize("Rock bands of the United States of America in the 2000s")
	result = pl_en.process(s)	
	assert result.placeNameTerms*.text == ["United", "States", "of", "America"]
	assert result.subjectTerms*.text == ["Rock","bands"]	        
	
	s = Sentence.simpleTokenize("American rock bands")
	result = pl_en.process(s)	
	assert result.placeAdjectiveTerms*.text == ["American"]
	assert result.subjectTerms*.text == ["rock","bands"]	    	                                     
    }
	
    void testPortuguese() {
	Sentence s = Sentence.simpleTokenize("Músicos portugueses")
	def result = pl_pt.process(s)
	assert result.placeAdjectiveTerms*.text == ["portugueses"]
	assert result.subjectTerms*.text == ["Músicos"]	                
	                                     	                                     
	s = Sentence.simpleTokenize("Músicos de Portugal")
	result = pl_pt.process(s)	
	assert result.placeNameTerms*.text == ["Portugal"]
	assert result.subjectTerms*.text == ["Músicos"]	   

	// de notar o "de" e "dos"... deve ser inteligente a ponto de não ter problenas                                     
	s = Sentence.simpleTokenize("Bandas de rock dos Estados Unidos da América de 2000")
	result = pl_pt.process(s)	
	assert result.placeNameTerms*.text == ["Estados", "Unidos", "da", "América"]
	assert result.subjectTerms*.text == ["Bandas","de","rock"]	        
	                        	
	s = Sentence.simpleTokenize("Bandas de rock norte-americanas")
	result = pl_pt.process(s)	
	assert result.placeAdjectiveTerms*.text == ["norte-americanas"]
	assert result.subjectTerms*.text ==  ["Bandas","de","rock"]	     
    }

}	