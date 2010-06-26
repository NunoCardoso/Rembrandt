/**
 * 
 */
package renoir.server

import java.io.File
import groovy.text.SimpleTemplateEngine

import org.apache.log4j.Logger
import org.restlet.Restlet
import org.restlet.Context
import org.restlet.Request
import org.restlet.Response
import org.restlet.data.ClientInfo
import org.restlet.data.MediaType
import org.restlet.data.Status
import org.restlet.representation.StringRepresentation
import org.restlet.data.CharacterSet
import org.restlet.data.Language
/**
 * @author rlopes
 *
 */
public class WebServiceRestletMapping extends Restlet {

	private def mappings
	private def binding
	private Status status = Status.SUCCESS_OK
	static Logger log = Logger.getLogger("RenoirServerMain") 
	
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
	
	private Reader createReaderFromResource(String templateLocation) {
		Reader r = null
		
		try {
			r = (new File(templateLocation)).newReader()
		}
		catch(FileNotFoundException ex) {
			def tl_url = this.getClass().getResource("/" + templateLocation)
			r = new InputStreamReader(tl_url.openStream())
		}
		
		return r
	}
	
	public String applyTemplate(String templateLocation) {
		def template = new SimpleTemplateEngine().createTemplate(createReaderFromResource(templateLocation)).make(this.binding)
		return template.toString()
	}
	
	public void handle(Request request, Response response) {
		MediaType[] mimes = request.getClientInfo().getAcceptedMediaTypes().collect { pref -> pref.getMetadata() }
		MediaType mime = MediaType.APPLICATION_JSON
		
		for (curr in mimes) {
		    if (this.mappings[curr.toString()]) {
			mime = curr
			break
		    }
		}
		
		String body = this.mappings[mime.toString()].call(
			request, getAllParams(request), this.binding)
		StringRepresentation xml 
		try {
			 xml= new StringRepresentation(body, mime)
           //xml = new StringRepresentation(body, mime, Language.ALL, CharacterSet.UTF_8)		
		}catch(Exception e) {e.printStackTrace()}
		
		xml.setCharacterSet(CharacterSet.UTF_8);  
		//println "Middle/3 Body body = $body mime=$mime"

		response.setEntity(xml)
		//println "After Body"
		response.setStatus(this.status)
	}

}
