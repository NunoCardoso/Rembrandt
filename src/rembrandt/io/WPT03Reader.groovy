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
package rembrandt.io

import java.util.regex.*
import rembrandt.obj.Document

/**
 * @author Nuno Cardoso
 *
 */
class WPT03Reader extends Reader {
	
	
	/**
	 * This class imports WPT03 files to the Source Documents
	 ----!!- colecção WPT 03 - separador de documento -!!----
	 URL: http://7mares.terravista.pt/atlda/
	 (Content-Length, -1)
	 (Content-Type, text/html)
	 (Last-Modified, unknown)
	 (ServerSW, apache)
	 (dataRec, 16/04/2003)
	 (estado, 200)
	 (filtrado, /vcrMom/data/WEBSTATS/filtrados/20/24/748)
	 (ip, 62.151.16.12)
	 (language, unknown)
	 (prof, 0)
	 (realSize, 778)
	 (textSize, 14)
	 (title, Alves e Trigo)
	 Alves e Trigo
	 */
	
	
	public WPT03Reader(StyleTag style) {
		super(style)
	}
	
	/**
	 * Process the HTML input stream
	 */
	public void processInputStream(InputStreamReader is) {
		
		Matcher m
		Boolean indoc = false
		Boolean inbody = false
		Boolean inheader = false
		
		String text // buffer
		
		Date date_modified
		Date date_fetched
		String lang
		String id
		
		String content
		String url
		String title
		
		BufferedReader br = new BufferedReader(is)
		
		String l
		while ((l = br.readLine()) != null) {
			
			m = l =~ /^----!!- .* WPT 03 - separador de documento -!!----$/
			if (m.matches()) {
				if (inbody && indoc) {
					inbody = false; indoc=false; inheader = false;
					Document doc = new Document()
					doc.body = text.trim()
					if (title && title != "null")
						doc.title = title
					doc.docid = id
					doc.lang = lang
					doc.tokenize()
					
					Date date = null
					if (date_modified)
						date = date_modified
					if (!date && date_fetched)
						date = date_fetched
					if (!date)
						date = new Date(0)
					
					doc.date_created = date
					docs << doc
					text = "";title = ""; url = "";content = "";
					lang="";id="";date_modified=null;date_fetched=null;
				}
			} else {
				
				m = l =~ /^\((.*?), (.*?)\)$/
				if (m.matches()) {
					if (inheader && !inbody) {
						def key = m.group(1)
						def value = m.group(2)
						
						if (key == "Last-Modified") {
							if (value != "unknown") {
								date_modified = Date.parse("dd/MM/yyyy", value)
							}
						}
						if (key == "dataRec") {
							if (value != "unknown") {
								date_fetched = Date.parse("dd/MM/yyyy", value)
							}
						}
						if (key == "language") {
							if (value == "portuguese") {
								lang="pt"
							}else if (value == "english") {
								lang="en"
							} else {
								lang="xx"
							}
						}
						if (key == "title") {
							title = value
						}
					} else {
						if (inbody) {
							// é uma linha que começa e acaba com (), mas que pertence ao corpo
							text += l
							//log.error("Erro: linha $l não é suposto estar aqui!")
							//System.exit(0)
						}
					}
					
				} else {
					
					Matcher m2 = l =~ /URL: https?:\/\/(.*)/
					
					if (m2.matches()) {
						if (!inheader) {
							inheader = true;
							indoc = true
							url = m2.group(1);
							
							// o id vai ser o URL, só que há URLs que, truncados a 255, ficam iguais.
							// vou usar uma hash com 8 números, um '_', depois o URL truncado a 240.
							String random =  Long.toHexString(Double.doubleToLongBits(Math.random()));
							int index = (url.size() > 240 ? 240 : url.size())
							id = random.substring(0,8)+"_"+url.substring(0,index)
						}
						// body
					} else {
						if (inbody && !inheader) {
							text += l
						}
						else if (inheader && !inbody) {
							inheader = false;
							inbody = true;
							text = ""
						}
					}
				}
			}
		}
	}	
}
