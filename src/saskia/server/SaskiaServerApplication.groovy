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
 
import java.net.URLDecoder
import org.apache.log4j.*

import saskia.io.RembrandtedDoc
import saskia.io.User

import saskia.util.Native2AsciiWrapper

import org.restlet.Application
import org.restlet.Restlet
import org.restlet.routing.Router
import org.restlet.data.MediaType
import org.restlet.data.Status


/**
 * @author ncardoso, extended from rlopes's code
 *
 */
public class SaskiaServerApplication extends Application {

	//RembrandtedDocument2PlainTextConverter r2t
	Logger log = Logger.getLogger("SaskiaServer")
	List<User> superusers
	
	public synchronized Restlet createRoot() {

	    Router router = new Router(this.context) 
	  //  r2t = new RembrandtedDocument2PlainTextConverter()	    
        
            AskSaskiaMapping askSaskiaMapping = new AskSaskiaMapping()
           // askSaskiaMapping.attach(MediaType.TEXT_HTML, askSaskiaMapping.HTMLanswer)        
            askSaskiaMapping.attach(MediaType.APPLICATION_JSON, askSaskiaMapping.JSONanswer)

       	    SearchMapping searchMapping = new SearchMapping()    
	    searchMapping.attach(MediaType.APPLICATION_JSON, searchMapping.JSONanswer)
    
	    DocMapping docMapping = new DocMapping()
	    docMapping.attach(MediaType.APPLICATION_JSON, docMapping.JSONanswer)
	    
	    NEMapping neMapping = new NEMapping()
	    neMapping.attach(MediaType.APPLICATION_JSON, neMapping.JSONanswer)

	    StatsMapping statsMapping = new StatsMapping()
	    statsMapping.attach(MediaType.TEXT_HTML, statsMapping.HTMLanswer)

	    UserMapping userMapping = new UserMapping()
	    userMapping.attach(MediaType.TEXT_HTML, userMapping.HTMLanswer)
	    userMapping.attach(MediaType.APPLICATION_JSON, userMapping.JSONanswer)

	    AdminCollectionMapping adminCollectionMapping = new AdminCollectionMapping()
	    adminCollectionMapping.attach(MediaType.APPLICATION_JSON, adminCollectionMapping.JSONanswer)
	     
	    AdminUserMapping adminUserMapping = new AdminUserMapping()
	    adminUserMapping.attach(MediaType.APPLICATION_JSON, adminUserMapping.JSONanswer)

	    AdminStatsMapping adminStatsMapping = new AdminStatsMapping()
	    adminStatsMapping.attach(MediaType.APPLICATION_JSON, adminStatsMapping.JSONanswer)

	    AdminSourceDocMapping adminSourceDocMapping = new AdminSourceDocMapping()
	    adminSourceDocMapping.attach(MediaType.APPLICATION_JSON, adminSourceDocMapping.JSONanswer)

	    AdminRembrandtedDocMapping adminRembrandtedDocMapping = new AdminRembrandtedDocMapping()
	    adminRembrandtedDocMapping.attach(MediaType.APPLICATION_JSON, adminRembrandtedDocMapping.JSONanswer)

	    AdminNEMapping adminNEMapping = new AdminNEMapping()
	    adminNEMapping.attach(MediaType.APPLICATION_JSON, adminNEMapping.JSONanswer)

	    AdminEntityMapping adminEntityMapping = new AdminEntityMapping()
	    adminEntityMapping.attach(MediaType.APPLICATION_JSON, adminEntityMapping.JSONanswer)

	    AdminGeoscopeMapping adminGeoscopeMapping = new AdminGeoscopeMapping()
	    adminGeoscopeMapping.attach(MediaType.APPLICATION_JSON, adminGeoscopeMapping.JSONanswer)

	    AdminSubjectMapping adminSubjectMapping = new AdminSubjectMapping()
	    adminSubjectMapping.attach(MediaType.APPLICATION_JSON, adminSubjectMapping.JSONanswer)

	    AdminSubjectGroundMapping adminSubjectGroundMapping = new AdminSubjectGroundMapping()
	    adminSubjectGroundMapping.attach(MediaType.APPLICATION_JSON, adminSubjectGroundMapping.JSONanswer)

	    router.attach("/Saskia/ask", askSaskiaMapping)
	    router.attach("/Saskia/search", searchMapping)
	    router.attach("/Saskia/doc", docMapping)
	    router.attach("/Saskia/ne", neMapping)
	    router.attach("/Saskia/stats", statsMapping)
	    router.attach("/Saskia/user", userMapping)	
	    router.attach("/Saskia/admin/collection", adminCollectionMapping)	
	    router.attach("/Saskia/admin/user", adminUserMapping)	
	    router.attach("/Saskia/admin/stats", adminStatsMapping)	
	    router.attach("/Saskia/admin/sdoc", adminSourceDocMapping)	
	    router.attach("/Saskia/admin/rdoc", adminRembrandtedDocMapping)	
	    router.attach("/Saskia/admin/ne", adminNEMapping)	
	    router.attach("/Saskia/admin/entity", adminEntityMapping)	
	    router.attach("/Saskia/admin/geoscope", adminGeoscopeMapping)	
	    router.attach("/Saskia/admin/subject", adminSubjectMapping)	
	    router.attach("/Saskia/admin/subjectground", adminSubjectGroundMapping)			
	    return router
	}	
}
