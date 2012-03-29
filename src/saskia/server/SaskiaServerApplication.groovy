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

import org.apache.log4j.*
import org.restlet.Application
import org.restlet.Restlet
import org.restlet.data.MediaType
import org.restlet.routing.Router

import saskia.bin.Configuration
import saskia.db.database.SaskiaDB
import saskia.db.obj.User

/**
 * @author ncardoso, extended from rlopes's code
 *
 */
public class SaskiaServerApplication extends Application {

	Logger log = Logger.getLogger("SaskiaServerMain")
	List<User> superusers
	Configuration conf
	SaskiaDB db

	public SaskiaServerApplication(Configuration conf, SaskiaDB db) {
		this.conf=conf
		this.db=db
	}

	public synchronized Restlet createRoot() {

		Router router = new Router(this.context)

		AskSaskiaMapping askSaskiaMapping = new AskSaskiaMapping(db)
		// askSaskiaMapping.attach(MediaType.TEXT_HTML, askSaskiaMapping.HTMLanswer)
		askSaskiaMapping.attach(MediaType.APPLICATION_JSON, askSaskiaMapping.JSONanswer)

		DocMapping docMapping = new DocMapping(db)
		docMapping.attach(MediaType.APPLICATION_JSON, docMapping.JSONanswer)

		NEMapping neMapping = new NEMapping(db)
		neMapping.attach(MediaType.APPLICATION_JSON, neMapping.JSONanswer)

		TaskMapping taskMapping = new TaskMapping(db)
		taskMapping.attach(MediaType.APPLICATION_JSON, taskMapping.JSONanswer)

		StatsMapping statsMapping = new StatsMapping(db)
		statsMapping.attach(MediaType.TEXT_HTML, statsMapping.HTMLanswer)

		CollectionMapping collectionMapping = new CollectionMapping(db)
		collectionMapping.attach(MediaType.APPLICATION_JSON, collectionMapping.JSONanswer)

		UserMapping userMapping = new UserMapping(db)
		userMapping.attach(MediaType.TEXT_HTML, userMapping.HTMLanswer)
		userMapping.attach(MediaType.APPLICATION_JSON, userMapping.JSONanswer)

		AdminCollectionMapping adminCollectionMapping = new AdminCollectionMapping(db)
		adminCollectionMapping.attach(MediaType.APPLICATION_JSON, adminCollectionMapping.JSONanswer)

		AdminUserMapping adminUserMapping = new AdminUserMapping(db)
		adminUserMapping.attach(MediaType.APPLICATION_JSON, adminUserMapping.JSONanswer)

		AdminTaskMapping adminTaskMapping = new AdminTaskMapping(db)
		adminTaskMapping.attach(MediaType.APPLICATION_JSON, adminTaskMapping.JSONanswer)

		AdminStatsMapping adminStatsMapping = new AdminStatsMapping(db)
		adminStatsMapping.attach(MediaType.APPLICATION_JSON, adminStatsMapping.JSONanswer)

		AdminDocMapping adminDocMapping = new AdminDocMapping(db)
		adminDocMapping.attach(MediaType.APPLICATION_JSON, adminDocMapping.JSONanswer)

		AdminNEMapping adminNEMapping = new AdminNEMapping(db)
		adminNEMapping.attach(MediaType.APPLICATION_JSON, adminNEMapping.JSONanswer)

		AdminEntityMapping adminEntityMapping = new AdminEntityMapping(db)
		adminEntityMapping.attach(MediaType.APPLICATION_JSON, adminEntityMapping.JSONanswer)

		AdminGeoscopeMapping adminGeoscopeMapping = new AdminGeoscopeMapping(db)
		adminGeoscopeMapping.attach(MediaType.APPLICATION_JSON, adminGeoscopeMapping.JSONanswer)

		AdminSubjectMapping adminSubjectMapping = new AdminSubjectMapping(db)
		adminSubjectMapping.attach(MediaType.APPLICATION_JSON, adminSubjectMapping.JSONanswer)

		AdminSubjectGroundMapping adminSubjectGroundMapping = new AdminSubjectGroundMapping(db)
		adminSubjectGroundMapping.attach(MediaType.APPLICATION_JSON, adminSubjectGroundMapping.JSONanswer)

		router.attach("/Saskia/ask", askSaskiaMapping)

		router.attach("/Saskia/doc", docMapping)
		router.attach("/Saskia/collection", collectionMapping)
		router.attach("/Saskia/task", taskMapping)
		router.attach("/Saskia/ne", neMapping)
		router.attach("/Saskia/stats", statsMapping)
		router.attach("/Saskia/user", userMapping)

		router.attach("/Saskia/admin/collection", adminCollectionMapping)
		router.attach("/Saskia/admin/task", adminTaskMapping)
		router.attach("/Saskia/admin/user", adminUserMapping)
		router.attach("/Saskia/admin/stats", adminStatsMapping)
		router.attach("/Saskia/admin/doc", adminDocMapping)
		router.attach("/Saskia/admin/ne", adminNEMapping)
		router.attach("/Saskia/admin/entity", adminEntityMapping)
		router.attach("/Saskia/admin/geoscope", adminGeoscopeMapping)
		router.attach("/Saskia/admin/subject", adminSubjectMapping)
		router.attach("/Saskia/admin/subjectground", adminSubjectGroundMapping)
		return router
	}
}
