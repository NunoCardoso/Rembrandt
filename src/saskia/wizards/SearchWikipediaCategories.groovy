package saskia.wizards

import saskia.wikipedia.WikipediaDocument
import saskia.bin.Configuration
import saskia.wikipedia.WikipediaAPI
import saskia.gazetteers.Stopwords
import rembrandt.tokenizer.TokenizerPT
import saskia.util.StringUtil
import rembrandt.gazetteers.pt.ClausesPT
import rembrandt.obj.Rule
import rembrandt.obj.Clause
import rembrandt.obj.Sentence
import renoir.obj.QuestionAnalyser
import rembrandt.rules.MatcherObject
import renoir.obj.Question
import org.apache.lucene.queryParser.*
import org.apache.lucene.search.regex.*
import org.apache.lucene.search.*
import org.apache.lucene.document.*
import org.apache.lucene.index.*
import org.apache.lucene.store.*
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.snowball.*
import saskia.util.Native2AsciiWrapper

class SearchWikipediaCategories {
    
    WikipediaAPI wikipedia
    TokenizerPT tok =  TokenizerPT.newInstance()
    def indexDir
    Directory index 
    IndexSearcher searcher
    String sourceEncoding
    Native2AsciiWrapper n2a
    String lang
    
    public SearchWikipediaCategories(String lang, conf, String sourceEncoding) {
        indexDir = conf.get("saskia.index.dir", "index")+"/lucene-wikipedia-${lang}-tablepage"	
        index = FSDirectory.getDirectory(indexDir, false)
        searcher  = new IndexSearcher(index)
        this.sourceEncoding = sourceEncoding
        this.lang=lang
        wikipedia = WikipediaAPI.newInstance(lang, conf)
        n2a = Native2AsciiWrapper.newInstance()
    }
    
    
    //	def Clause notPlaceAdjective1p = new Clause(name:"notPlaceAdjective1p",
    //		cardinality:Cardinality.OneOrMore, criteria:Criteria.NotPlaceAdjectiveMatch)
    //	def Clause notPlaceName1p = new Clause(name:"notPlaceName1p",
    //		cardinality:Cardinality.OneOrMore, criteria:Criteria.NotPlaceNameMatch)
    def Clause placeAdjective1p = new Clause(name:"placeAdjective1p",
    cardinality:Cardinality.OneOrMore, criteria:Criteria.PlaceAdjectiveMatch)
    def Clause placeName1p = new Clause(name:"placeName1p",
    cardinality:Cardinality.OneOrMore, criteria:Criteria.PlaceNameMatch)
    
    def rules = [
                 // OBSOLETE 
                 
  /*  new Rule(id:'N_Adj', description:'XXX portugueses', debug:true,
    clauses:[placeAdjective1p], 
    action:[{Expando q, QuestionMatcherObject o -> 
        q.subjectTerms = o.getSentencePartBeforeMatchedClause(placeAdjective1p)
        q.adjectiveTerm = o.getTermsMatchedByClause(placeAdjective1p)
        q.match = o.getMatchedAnswerByClause(placeAdjective1p)
        q.matchedRuleID = 'N_Adj'
    }])
    ,
    new Rule(id:'N_de_N', description:'XXX de Portugal', debug:true,
    clauses:[ClausesPT.dnaeosem1c, placeName1p], 
    action:[{Expando q, QuestionMatcherObject o -> 
        q.subjectTerms = o.getSentencePartBeforeMatchedClause(ClausesPT.dnaeosem1c)
        q.placeNameTerm = o.getTermsMatchedByClause(placeName1p)
        q.match = o.getMatchedAnswerByClause(placeName1p)
        q.matchedRuleID = 'N_de_N'
    }])*/
    ]
    
    public Sentence tokenizeLine(String line) {
        return tok.parse(line)?.getAt(0)
    }
    
    
    public WikipediaDocument getCategories(String needle) {
        return wikipedia.getCategoryDocumentFromPageTitle(needle)
    }
    
    public List<WikipediaDocument> getRegexCategories(String needle) {
        return wikipedia.getCategoriesFromRegexTitle(needle)
    }
    
    public List getLuceneCategories(String needle) {
        //println "NEEDLE: $needle"
        needle = n2a.n2a(needle)
        // println "NEEDLE: $needle"
        List finalanswer = []
        def sentence = tokenizeLine(needle)
        def sentence2 = StopwordsPT.removeStopwords(sentence, lang)
        def queryline = ""
        sentence2.each{t-> queryline += "+"+t.text+" "}
        //	println "Query line: $queryline"
        QueryParser qp = new QueryParser("page_title", new StandardAnalyzer())
        Query query = qp.parse(
                StringUtil.convert(queryline.trim(), sourceEncoding, "UTF-8"))
        
        Hits hits = searcher.search(query) 
        for (int i = 0; i < hits.length; i++) {
            def id = hits.doc(i).get("page_id")
            def title = hits.doc(i).get("page_title")
            
            //println "id:${id} title:${title}"	
            def res = [id:id, title:title]	
            if (!finalanswer.contains(res)) finalanswer << res
        }
        return finalanswer
    }
    
    public List getStemCategories(String needle) {
        List finalanswer = []
        QueryParser qp = new QueryParser("page_title", new
                SnowballAnalyzer("Portuguese",StopwordsPT.stopwords.toArray(
                new String[ StopwordsPT.stopwords.size() ] )))
        Query query = qp.parse(
                StringUtil.convert(needle.trim(), sourceEncoding, "UTF-8"))
        println "Query: $query"
        
        Hits hits = searcher.search(query) 
        for (int i = 0; i < hits.length; i++) {
            def id = hits.doc(i).get("page_id")
            def title = hits.doc(i).get("page_title")
            def res = [id:id, title:title]	
            if (!finalanswer.contains(res)) finalanswer << res
        }
        return finalanswer
    }
    
    public List<WikipediaDocument> getMoreCategories(String line) {
        // Escritores em Portugal -> Escritores portugueses
        // Escritores portugueses -> Escritores em Portugal
        
        Sentence sentence =  tokenizeLine(line) 
        QuestionAnalyser qa = new QuestionAnalyser()
        Question q = new Question()
        qa.applyRules(sentence, q, rules)
        // Now I have the place name / adjective and their relation
        if (q.matchedRuleID == 'N_de_N') {
            println "Trying "+q.subjectTerms+" "+q.match.adj
            return getRegexCategories(q.subjectTerms.join(" ")+" "+q.match.adj.join(" "))
        }
        if (q.matchedRuleID == 'N_Adj') {
            def name = ""
            if (q.match.name instanceof List) name = q.match.name.join(" ")
            if (q.match.name instanceof String) name = q.match.name
            println "Trying "+q.subjectTerms+" "+ClausesPT.dnaeosem1c.pattern+" "+name
            return getRegexCategories(q.subjectTerms.join(" ")+" "+ClausesPT.dnaeosem1c.pattern+" "+name)
        }	
        return null
    }
    
    static main(args) {
        
        Configuration conf
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in))	
        SearchWikipediaCategories searchwiki = null
        String line, lang
        
        if (args) conf = Configuration.newInstance(args[0])
        else conf = Configuration.newInstance()
        
        println "Search Wikipedia Categories - a Saskia wizard."
        println "-----------"
        println "Enter language (default: ${conf.get('global.lang')})."	
        
        lang = input.readLine().trim()
        if (lang) {
            if (lang ==~ /\w{2}/) {
                println "Language will be $lang.\n"	
            } else {
                println "Invalid language $lang. Using default.\n"	
                lang = conf.get("global.lang")
            }
        } else {
            println "Using default.\n"	
            lang =  conf.get("global.lang")
        }
        
        def sourceEncoding = System.getProperty("file.encoding")
        println "System default encoding: "+sourceEncoding		
        searchwiki = new SearchWikipediaCategories(lang, conf, sourceEncoding)	
        
        // now, let's chase categories
        println "Enter text, I'll browse Wikipedia categories with it."
        println "Lucene wildcards are accepted."
        
        def start1, end1
        while (true) {
            print "Saskia> "
            line = input.readLine().trim()
            if (!line) break
                start1 = System.currentTimeMillis()
            
            print "Exact results: "			
            def result = searchwiki.getCategories(line)
            if (!result) println "none."
            if (result) println "id:${result.id} title:${result.theTitle}"	
            
            // Too slow
            
            //print "Regex results"
            /*def answer = searchwiki.getRegexCategories(line)
             println answer 
             if (answer) {
             answer.each{a -> 
             res = [id:a.id, title:a.theTitle]
             if (!finalanswer.contains(res)) finalanswer << res
             }
             }*/
            
            print "Lucene results: "
            result = searchwiki.getLuceneCategories(line)
            if (!result) println "none."
            if (result) result.eachWithIndex{r, i -> println "${i}: ${r}"}	
            
            print "Stem results: "
            // adjective patters suck for Lucene regex retrieval. MySQL regex also sucks. 
            // Workaround: Perform basic search with Lucene, filter results with regex here.
            result = searchwiki.getStemCategories(line)
            if (!result) println "none."
            if (result) result.eachWithIndex{r, i -> println "${i}: ${r}"}	
            
            end1 = System.currentTimeMillis()    
            println "Done in "+(end1-start1)+" msecs."
        }
    }
}