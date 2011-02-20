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

package rembrandt.server
 
import saskia.bin.Configuration 
import org.apache.log4j.*

import org.restlet.Application
import org.restlet.Restlet
import org.restlet.routing.Router
import org.restlet.data.MediaType
import org.restlet.data.Status


/**
 * @author ncardoso, extended from rlopes's code
 *
 */
public class RembrandtServerApplication extends Application {

	Logger log = Logger.getLogger("RembrandtServerMain")
	Configuration conf
	
	public RembrandtServerApplication(Configuration conf) {
	    this.conf=conf
	}
	
	public synchronized Restlet createRoot() {

	    Router router = new Router(this.context)
	    
	    RembrandtMapping rembrandtMapping = new RembrandtMapping(conf)    
	    rembrandtMapping.attach(MediaType.APPLICATION_JSON, rembrandtMapping.JSONanswer)
	    log.info "Attaching /Rembrandt/api/rembrandt"
	    router.attach("/Rembrandt/api/rembrandt", rembrandtMapping)
	    return router
	}	
}
