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

import renoir.obj.Question
import saskia.db.database.WikipediaDB;
import rembrandt.obj.Sentence
import groovy.sql.Sql
import saskia.dbpedia.DBpediaAPI
import org.apache.log4j.*
import saskia.bin.Configuration

class WriteRun {
	
   WikipediaDB db 
	static Logger log = Logger.getLogger("RenoirMain")
	DBpediaAPI dbpediaAPI
	List allowedLangs = ["pt","en","es","de","it","nn","nl","no","ro","bg"]
	Configuration conf
	QueryCandidateAnswers qca
	
	public WriteRun() {
		conf = Configuration.newInstance()
		conf.set("global.lang","pt")
		conf.set("saskia.dbpedia.mode", "webservice")
		dbpediaAPI = DBpediaAPI.newInstance(conf)
		qca = new QueryCandidateAnswers()
		if (conf.getBoolean("saskia.wikipedia.enabled",true)) {
			db = WikipediaDB.newInstance()
		} else {
			println("Can't work: Wikipedia mode is off, as requested.")
			println("Set saskia.wikipedia.enabled to true, to proceed. Exiting...")
			System.exit(0)
		}
	}

	public List format(Question q, String language) {	
		def answers = []
		q.answer.eachWithIndex{answer, i -> 
			def string = ""
			def convertedAnswer = resource2string(language, answer)
			def gikiclefAnswer = string2gikiclef(language,convertedAnswer)
			
			string += "${q.id} "+gikiclefAnswer+" "
			string += "{"
			
			def boolJust = true
			def listConvertedJust = []
			q.answerJustification?.each{j -> 
				def convertedJust = resource2string(language, j)
				listConvertedJust << convertedJust
				def gikiclefJust = string2gikiclef(language,convertedJust)
				string += gikiclefJust+" "
				// check if all justifications are on the list
				boolJust = boolJust && qca.isOnTheList(gikiclefJust)
			}
			string += "}"
			println "String so fat: $string"
			answers << [line:string, boolAnswer:(qca.isOnTheList(gikiclefAnswer)),
			  boolJust:boolJust]
			
			// go for langlinks
			println "langlinks para $convertedAnswer"
			db.eachRow("""select ll_lang, ll_title from ${language}_langlinks, ${language}_page where 
			ll_from = page_id and page_namespace=0 and page_title=?""", [convertedAnswer], {row -> 
				 def res = row[1].replaceAll(" ","_")
				 def lang = row[0]
				 if (allowedLangs.contains(lang)) {
					def string2 = ""
					def gikiclefAnswer2 = string2gikiclef(lang,res)
					string2 += "${q.id} "+gikiclefAnswer2+" "				
					
					List justList = []
					def boolJust2 = true

					listConvertedJust.each{it -> 
						db.eachRow("""select ll_lang, ll_title from ${language}_langlinks, 
					    ${language}_page where ll_from = page_id and page_namespace=0 and page_title=?""", 
						[it], {row2 -> 
							def res2 = row2[1].replaceAll(" ","_")
				 			def lang2 = row2[0]
							if (lang2 == lang) justList << string2gikiclef(lang,res2)
					    })
						boolJust2 = boolJust2 && qca.isOnTheList(it)
					}
				
					string2 += "{"+justList.join(" ")+"}"
					answers << [line:string2, 
					boolAnswer:(qca.isOnTheList(gikiclefAnswer2)),
			  		boolJust:boolJust2]	
				}
			})
		}
		
		return answers
	}
	
	String resource2string(String language, String answer) {
		if (!answer) return null
		log.debug "Have raw resource $answer"
		def convertedAnswer = dbpediaAPI.getLabelFromDBpediaResource(answer,language)
		if (!convertedAnswer) return null
		convertedAnswer = convertedAnswer.replaceAll(" ","_")
		log.debug "Converted to $convertedAnswer"
		return convertedAnswer 
	}

	public String string2gikiclef(String lang, String answer) {
		if (!answer) return null 
		String name = answer.replaceAll(/"/,"\\\"")
		name = name.replaceAll(/'/,"\\'") //"
		name = name.replaceAll(/\(/,"\\(")
		name = name.replaceAll(/\)/,"\\)")
		name = name.replaceAll(/;/,"\\;")
		name = name.replaceAll(/%/,"\\%")
		name = name.replaceAll(/&/,"\\&")
		name = name.replaceAll(/\Q|\E/,"\\|")
		name = name.replaceAll(/\Q*\E/,"\\*") // */
		name = name.replaceAll(/`/,"\\`")
 		if (name.endsWith("\\")) name += "\\"
		println "answer $answer got converted to $name"
			
		def sout = new StringBuffer(), serr = new StringBuffer()
		def command = "php script/getPathName.php $lang "+name
		def proc = command.execute()
		proc.consumeProcessOutput(sout, serr)
		proc.waitForOrKill(1000)
		def convertedAnswer = sout.toString().trim()+".html"
		log.debug "Converted $answer to $convertedAnswer"
		return convertedAnswer
	}
	
	static main(args) {	
		def Question q = new Question(new Sentence([],0))
		q.id="test1"
		q.answer=["José Saramago"]
		q.answerJustification=["José Saramago"]
		WriteRun wr = new WriteRun()
		def answer = wr.format(q)
		answer.each{println it}	
	 //assert answer == "pt/j/o/s/Jos�_Saramago_f8ad", "Got $answer instead"
	}
}