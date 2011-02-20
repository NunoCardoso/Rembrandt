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
package saskia.io
import org.apache.log4j.Logger

/**
 * @author Nuno Cardoso
 *<TimeSignature version="1.0" totalcount="8">
  <Doc id="539353" original_id="hub-81551" lang="pt" />
  <DocDateCreated>19700101</DocDateCreated>
  <Time count="1">
     <NE>1 de Novembro de 1755</NE>
     <TimeGrounding>!:Y+1755M11D01</TimeGrounding>
     <Index>17551101</Index>
  </Time>
  <Time count="1">
     <NE>mar , entre 150 a 500</NE>
     <TimeGrounding>!:M03</TimeGrounding>
     <Index>175503</Index>
  </Time>
  <Time count="1">
     <NE>em 1969</NE>
     <TimeGrounding>!:Y+1969</TimeGrounding>
     <Index>1969</Index>
  </Time>
  <Time count="1">
     <NE>em 1755</NE>
     <TimeGrounding>!:Y+1755</TimeGrounding>
     <Index>1755</Index>
  </Time>
  <Time count="1">
     <NE>2 horas</NE>
     <TimeGrounding>!:h02</TimeGrounding>
     <Index>1755</Index>
  </Time>
</TimeSignature>     
 */
class TimeSignature {
    
    String version
    int totalcount
    Long doc_id
    String doc_original_id
    String doc_lang
    String date_created
    List timelist = []
    static Logger log = Logger.getLogger("TimeSignature")

    public TimeSignature() {}
    
    public TimeSignature(DocTimeSignature doctimesig) {
		parse(doctimesig.dts_signature)
    }
    
    void parse(String string) {
        
    	Node ts 
		try {
			ts = new XmlParser().parseText(string)
		} catch(Exception e) {
			log.error("Can't parse this: \n"+string)
			System.exit(0)
		}
    	version = ts.attribute("version")
    	totalcount = Integer.parseInt(ts.attribute("totalcount"))

    	ts.children().each{t -> 
           // println "c: $t class: ${t.class.name}"
            switch(t.name().toLowerCase()) {
                case "doc":
                doc_id = Long.parseLong(t.attribute("id"))
                doc_original_id = t.attribute("original_id")
                doc_lang = t.attribute("lang")
                break
                
                case "docdatecreated":
                date_created = t.text()
                break
                
                case "time":
                Map tt = [:]
                tt.count = Integer.parseInt(t.attribute("count"))
                t.children().each{t2 -> 
                  
                    switch(t2.name().toLowerCase()) {  
                        case "ne":
                        tt.ne = t2.text()
                        break
                        case "timegrounding":
                        tt.tg = t2.text()
                        break
                        case "index":
                        tt.idx = t2.text()
                        break                       
                    }
                }
                timelist << tt
            }
        }
    }
}