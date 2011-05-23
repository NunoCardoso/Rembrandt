package saskia.util

import org.tartarus.snowball.*
import java.lang.reflect.Method
import rembrandt.obj.Sentence
import rembrandt.obj.TermWithPoS

public class Stemmer {
        
        SnowballProgram stemmer
        Method stemMethod
        
        public Stemmer(String language) {
                String lang=""
        
                if (language.equalsIgnoreCase("pt")) {lang="portuguese"}
                if (language.equalsIgnoreCase("en")) {lang="english"}
                try {
                        Class stemClass = Class.forName("org.tartarus.snowball.ext."+lang+"Stemmer")
                        stemmer = (SnowballProgram) stemClass.newInstance()
                        stemMethod = stemClass.getMethod("stem", new Class[0])
                }catch(Exception e) {
                        e.printStackTrace()
                }
        }
        
        public String stemIt(String word) {
                stemmer.setCurrent(word)
                try {
                        stemMethod.invoke(stemmer, new Object[0])
                }catch(Exception e) {
                        e.printStackTrace()
                }
                return stemmer.getCurrent()
        }

        public Sentence stemIt(Sentence sentence, List stopwords=[]) {
                def newSentence = new Sentence(sentence.index)
				//sentence.each{term -> 
					stemmer.setCurrent(term)
                try {
                        stemMethod.invoke(stemmer, new Object[0])
                }catch(Exception e) {
                        e.printStackTrace()
                }
                return stemmer.getCurrent()
        }
}
