/* Renoir 
 * Copyright (C) 2009 Nuno Cardoso. ncardoso@xldb.di.fc.ul.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
 
package renoir.server

import org.restlet.Application
import org.restlet.Restlet
import org.restlet.routing.Router
import org.restlet.data.MediaType

import org.apache.log4j.*

/**
 * @author ncardoso, extended from rlopes's code
 *
 */
public class RenoirServerApplication extends Application {


    Logger log = Logger.getLogger("RenoirServerMain") 
    
	public synchronized Restlet createRoot() { 
	
	    Router router = new Router(this.context)

	    SuggestMapping suggestMapping = new SuggestMapping()
	    suggestMapping.attach(MediaType.TEXT_HTML, suggestMapping.HTMLanswer)
	    suggestMapping.attach(MediaType.APPLICATION_JSON, suggestMapping.JSONanswer)
	
	    DBObjectSuggestMapping dboMapping = new DBObjectSuggestMapping()
	    dboMapping.attach(MediaType.APPLICATION_JSON, dboMapping.JSONanswer)

	    SearchMapping searchMapping = new SearchMapping()
	    searchMapping.attach(MediaType.APPLICATION_JSON, searchMapping.JSONanswer)

	    router.attach("/Renoir/suggest", suggestMapping)
	    router.attach("/Renoir/dbosuggest", dboMapping)
	    router.attach("/Renoir/search", searchMapping)
				
	    return router
	}
	
}
