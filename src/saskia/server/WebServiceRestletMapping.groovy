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
package saskia.server

import java.io.File
import groovy.text.SimpleTemplateEngine

import org.restlet.Restlet
import org.restlet.Context
import org.restlet.Request
import org.restlet.Response
import org.restlet.data.ClientInfo
import org.restlet.data.MediaType
import org.restlet.data.Status
import org.restlet.representation.StringRepresentation
import org.restlet.data.CharacterSet
import org.apache.log4j.*
/**
 * @author rlopes
 *
 */
public class WebServiceRestletMapping extends Restlet {

	private def mappings = [:]
	private def binding = [:]
	private Status status = Status.SUCCESS_OK
	
	public WebServiceRestletMapping() {
		super()
		
		this.mappings = [:]
		this.binding = [:]
	}
	
	public void attach(MediaType mime, Closure c) {
		this.mappings[mime.toString()] = c
	}
	
	public void modifyStatus(Status newStatus) {
		this.status = newStatus
	}
	
	public HashMap getAllParams(Request request) {
	    HashMap res = [:] 
	    res["GET"] = request.getResourceRef().getQueryAsForm().getValuesMap()
	    if (request.isEntityAvailable()) {
			res["POST"] = request.getEntityAsForm().getValuesMap()
	    }
	    res["COOKIE"] = request.getCookies().getValuesMap()	    
	    return res
	}
	
	public void handle(Request request, Response response) {
	   // println "request="+request
		MediaType[] mimes = request.getClientInfo().getAcceptedMediaTypes().collect { pref -> pref.getMetadata() }
	    MediaType mime = MediaType.APPLICATION_JSON
		
		for (curr in mimes) {
			if (this.mappings[curr.toString()]) {
				mime = curr
				break
			}
		}
		
		String body = this.mappings[mime.toString()].call(request, getAllParams(request), this.binding)
		StringRepresentation xml 
		try {
			 xml= new StringRepresentation(body, mime)
			 // issue http://restlet.tigris.org/issues/show_bug.cgi?id=367
				// it loops when I set a charset, by updating the size
			 // xml = new StringRepresentation(body, mime, Language.ALL, CharacterSet.UTF_8)		
		}catch(Exception e) {e.printStackTrace()}
		xml.setCharacterSet(CharacterSet.UTF_8)  
		//println "Middle/3 Body body = $body mime=$mime"

		response.setEntity(xml)
		//println "After Body"
		response.setStatus(this.status)
	}
	
}
