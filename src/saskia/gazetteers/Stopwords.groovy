package saskia.gazetteers

import rembrandt.obj.Sentence

class Stopwords {
	
   enum Case {SENSITIVE, INSENSITIVE}
	
	//ê = \u00ea
   static Map<String, String> stopwords = [
    "pt":["a","as","à", "às", "e", "o","os", "do", "da","dos","das","de","the",
          "ao","aos","no","na","nos","nas", "um","uma","uns","umas", "que", "em", "para",
          "num","numa","nuns","numas","pelo","pela","pelos","pelas","com", "por", "não", "nao",
          "seu","sua","seus","suas", "este","esta","estes","estas","deste","desta","destes",
          "dele","dela","deles","delas","vez","destas","neste","nesta","nestes","nestas","esse","esses",
          "qual","quais","onde","porque","quem","quando","quanto",
          "se","ou","mais","como", "todos","sobre","mas","cada","mesmo","entre","ha","ja",
          "há","já","voce","você","dois","depois","pois","outra","outras","outros","todo","so","só",
          "ontem","hoje","ser","tem","são","sao","ver","foi","foram","ter","mas","estão","estao",
          "ano","anos","dia","dias","segundo","on","menos","houve",
          "ate","até", "ainda","muito","aqui","como","apenas","tal","assim","i","porem","porém","d",
          "vai","vão","agora","sem","isto", "isso","disse","pode","vai","nossas","vossas","havia",
	"teve","desde","diz","sim","durante","podem","eram","devem",
	"deve","qualquer", "tambem", "têm"],
    //
    "en":["the","and","of","in","at","on","one","some","that","for",
        "by","to","a","an","with","not","as","near","around",
        "his","her","their","this","that","those","these","hers","there",
        "i","you","she","he","it","we","they",
        "what","who","when","whose","where", "which","why",
        "if","or","more","all","every","about","but","each","same","between","already",
        "two","after","other","yesterday","today","now",
        "be","been","am","are","is","was","were","have","has","had","got","do","did",
        "year","years","day","days","less",
        "since","yet","lot","here","just",
        "would","will","can","could","should","shall","so","from","ocurring"] ]
 	
	static Sentence removeStopwords(Sentence sentence, String lang, Case wordcase = Case.INSENSITIVE) {
       	    Sentence newSentence = new Sentence(sentence.index)
       	    sentence.each{t -> 
       	    	switch(wordcase) {
       	    	   case Case.INSENSITIVE:
       	    	       if (!stopwords[lang].contains(t.text.toLowerCase())) newSentence << t
       	    	       break
       	    	   case Case.SENSITIVE:
       	    	       if (!stopwords[lang].contains(t.text)) newSentence << t
       	    	       break
       	    	   }
       	       }
	       return newSentence
	}
	
	static Sentence stripPunctuation(Sentence sentence) {
	    Sentence newSentence = new Sentence(sentence.index)
	    sentence.each{t ->  if (!t.matches(/^\p{Punct}+$/)) newSentence << t }
	    return newSentence
	}
}