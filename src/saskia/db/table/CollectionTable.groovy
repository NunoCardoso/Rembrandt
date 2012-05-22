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
package saskia.db.table

import org.apache.log4j.Logger

import saskia.db.database.SaskiaDB
import saskia.db.obj.Collection
import saskia.db.obj.User
/**
 * @author Nuno Cardoso
 *
 */
class CollectionTable extends DBTable {

	static Logger log = Logger.getLogger("Collection")
	static String tablename = "collection"

	// caches
	Map<Long,Collection> cacheIDCollection
	// cache on user-colllection itens
	// first ID is the usr_id, second ID is the col_id
	Map<Long,Map> cacheIDuoc
	// values:
	// uoc_can_read : true or false
	// uoc_can_write : true or false
	// uoc_can_admin : true or false


	public CollectionTable(SaskiaDB db) {
		super(db)
		cacheIDCollection = [:]
		cacheIDuoc = [:]
	}

	public List<Collection> queryDB(String query, ArrayList params = []) {

		List<Collection> list = []
		getSaskiaDB().getDB().eachRow(query, params, {row  ->
			list << Collection.createNew(this, row)
		})
		return list
	}


	public Map<Long,Collection> getAllCollections() {
		if (!cacheIDCollection) refreshCache()
		return cacheIDCollection
	}

	/**
	 * Load the internal cache for collections
	 */
	public void refreshCache() {
		List l = queryDB("SELECT * FROM ${tablename}".toString(), [])
		l.each{cacheIDCollection[it.col_id] = it}
	}

	/**
	 * Load the internal cache for users
	 */
	public Map listAccessibleCollectionsForUser(User user) {

		if (cacheIDuoc.containsKey(user.usr_id))
			return cacheIDuoc[user.usr_id]

		cacheIDuoc[user.usr_id] = [:]
		Map collections = getAllCollections()
		List usergroups = user.getGroups()

		collections.each{col_id, col ->

			boolean canread = false
			boolean canwrite = false
			boolean canadmin = false
			boolean own = false

			List ownergroups = col.col_owner.getGroups()
			List commongroups = ownergroups.intersect(usergroups)

			if (col.col_permission =~ /......r../ ||
			(col.col_permission =~ /...r...../ && !commongroups.isEmpty()) ||
			(col.col_permission =~ /r......../ && col.col_owner.usr_id == user.usr_id) ) {
				canread = true
			}
			if (col.col_permission =~ /.......w./ ||
			(col.col_permission =~ /....w..../ && !commongroups.isEmpty()) ||
			(col.col_permission =~ /.w......./ && col.col_owner.usr_id == user.usr_id) ) {
				canwrite = true
			}
			if (col.col_permission =~ /........a/ ||
			(col.col_permission =~ /.....a.../ && !commongroups.isEmpty()) ||
			(col.col_permission =~ /..a....../ && col.col_owner.usr_id == user.usr_id) ) {
				canadmin = true
			}
			if (col.col_owner.usr_id == user.usr_id) {
				own = true
			}

			if (canread || canwrite || canadmin || own) {
				if (!cacheIDuoc[user.usr_id].containsKey(col_id)) cacheIDuoc[user.usr_id][col_id] = [:]
				cacheIDuoc[user.usr_id][col_id].uoc_own = own
				cacheIDuoc[user.usr_id][col_id].uoc_can_read = canread
				cacheIDuoc[user.usr_id][col_id].uoc_can_write = canwrite
				cacheIDuoc[user.usr_id][col_id].uoc_can_admin = canadmin
			}
		}
		return cacheIDuoc[user.usr_id]
	}

	List filterFromColumnAndNeedle(List haystack, column = null, needle = null) {

		if (column && needle) {

			if (column == "col_id") {
				return haystack.findAll{it.col_id == Long.parseLong(needle)}

			} else if (column == "col_owner") {
				return haystack.findAll{it.col_owner.usr_id == Long.parseLong(needle)}
			}	else {
				// The rest of the colums are String
				return haystack.findAll{it."${column}" =~ /(?i)${needle}/}
			}
		}
		return haystack
	}

	List<Collection> filterFromLimitAndOffset( List<Collection> res, int limit = 0, long offset = 0) {
		List<Collection> res2
		if (res.isEmpty()) return res

		// limit 0 means all of them - don't filter
		if (limit != 0) {
			int lim = (offset+limit-1)
			if (lim > (res.size()-1) ) lim = res.size()-1
			res2 = res[offset..lim]
		}	else {
			res2 = res
		}
		return res2
	}

	/** 
	 * Get the collection list with an optional colum/needle, page them with a limit/offset
	 * All for admin user (that is, no user filtering)
	 */
	HashMap listCollectionForAdminUser( limit = 0, offset = 0, column, needle) {

		List<Collection> res = filterFromColumnAndNeedle(getAllCollections().values().toList(), column, needle)
		List<Collection> res2 = filterFromLimitAndOffset(res, limit, offset)
		return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
			"result":res2, "column":column, "value":needle]
	}

	/** 
	 * Get the collection list with an optional colum/needle, page them with a limit/offset
	 * All for a user (that is, with user filtering), and that the user can read
	 */
	HashMap listReadableCollectionsForUser(User user, limit = 0, offset = 0, column = null, needle = null) {

		Map l = listAccessibleCollectionsForUser(user)
		List<Collection> haystack = l.findAll{col_id, perms -> perms.uoc_can_read == true}
		.collect{col_id, perms -> getFromID(col_id)}
		List<Collection> res = filterFromColumnAndNeedle(haystack, column, needle)
		List<Collection> res2 = filterFromLimitAndOffset(res, limit, offset)

		return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
			"result":res2, "column":column, "value":needle]
	}

	/** 
	 * same as above, but filtered to writable collections for user
	 */
	HashMap listWritableCollectionsForUser(User user, limit = 0, offset = 0, column = null, needle = null) {

		Map l = listAccessibleCollectionsForUser(user)
		List<Collection> haystack = l.findAll{col_id, perms -> perms.uoc_can_write == true}
		.collect{col_id, perms -> getFromID(col_id)}
		List<Collection> res = filterFromColumnAndNeedle(haystack, column, needle)
		List<Collection> res2 = filterFromLimitAndOffset(res, limit, offset)

		return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
			"result":res2, "column":column, "value":needle]
	}

	/** 
	 * same as above, but filtered to showable collections for user
	 */
	HashMap listAdminableCollectionsForUser(User user, limit = 0, offset = 0, column = null, needle = null) {

		Map l = listAccessibleCollectionsForUser(user)
		List<Collection> haystack = l.findAll{col_id, perms -> perms.uoc_can_admin == true}
		.collect{col_id, perms -> getFromID(col_id)}
		List<Collection> res = filterFromColumnAndNeedle(haystack, column, needle)
		List<Collection> res2 = filterFromLimitAndOffset(res, limit, offset)

		return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
			"result":res2, "column":column, "value":needle]
	}

	/** 
	 * same as above, but filtered to showable collections for user
	 */
	HashMap listOwnCollectionsForUser(User user, limit = 0, offset = 0, column = null, needle = null) {

		List<Collection> haystack = getAllCollections().values().toList().findAll{it.col_owner.equals(user)}
		List<Collection> res = filterFromColumnAndNeedle(haystack, column, needle)
		List<Collection> res2 = filterFromLimitAndOffset(res, limit, offset)

		return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
			"result":res2, "column":column, "value":needle]
	}

	/** 
	 * same as above, but filtered to showable collections for user
	 */
	HashMap listAllCollectionsForUser(User user, limit = 0, offset = 0, column = null, needle = null) {

		Map l = listAccessibleCollectionsForUser(user)
		List<Collection> haystack = l.collect{col_id, perms -> getFromID(col_id)}
		List<Collection> res = filterFromColumnAndNeedle(haystack, column, needle)
		List<Collection> res2 = filterFromLimitAndOffset(res, limit, offset)

		def res3 = []
		// from the final list, let's collect perms
		res2.each{col -> res3 << l[col.col_id]}

		return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
			"result":res2, "perms":res3, "column":column, "value":needle]
	}

	boolean canRead(User user, Collection collection) {
		if (!user) return null
		Map res = listAccessibleCollectionsForUser(user)
		return (res[collection.col_id]? res[collection.col_id]?.uoc_can_read : false)
	}

	boolean canWrite(User user, Collection collection) {
		if (!user) return null
		Map res = listAccessibleCollectionsForUser(user)
		return (res[collection.col_id]? res[collection.col_id]?.uoc_can_write : false)
	}

	boolean canAdmin(User user, Collection collection) {
		if (!user) return null
		Map res = listAccessibleCollectionsForUser(user)
		return (res[collection.col_id]? res[collection.col_id]?.uoc_can_admin : false)
	}

	boolean canHaveANewCollection(User user) {
		return user.usr_max_number_collections > collectionsOwnedBy(user)
	}

	Map getPermissionsFromUserOnCollection(User user, Collection collection) {
		if (!user || !collection) return null
		Map res = listAccessibleCollectionsForUser(user)
		return res[collection.col_id]
	}

	/**
	 * Careful using this -- a DB can have more than one collection with the same name.
	 * Don't use this method for getting collections for a given user. 
	 * You have to authenticate the user, then search by ID is highly reccomended.
	 */
	public List<Collection> getFromName(String name) {
		if (!name) return null
		if (!cacheIDCollection) refreshCache()
		List collection_list = cacheIDCollection.values().toList().findAll{it.col_name==name}
//		log.info "Querying for collection name $name got Collection(s) $collection_list."
		return collection_list
	}

	static List<Collection> getFromName(SaskiaDB db, String collection_name) {
		CollectionTable instance = db.getDBTable("CollectionTable")
		return instance.getFromName(collection_name)
	}

	public Collection getFromNameAndOwner(String name, User user) {
		if (!name) return null
		if (!cacheIDCollection) refreshCache()
		List collection_list = cacheIDCollection.values().toList().findAll{it.col_name==name && it.col_owner.equals(user)}
//		log.info "Querying for collection name $name and owner $user got Collection(s) $collection_list."
		return collection_list
	}

	public Collection getFromID(Long id) {
		// version has UNIQUE key
		if (!id) return null
		if (!cacheIDCollection) refreshCache()
		Collection c = cacheIDCollection[id]
//		log.info "Querying for collection id $id got Collection $c."
		return c
	}

	static Collection getFromID(SaskiaDB db, Long id) {
		return  db.getDBTable("CollectionTable").getFromID(id)
	}

	/**
	 * Returns a given collection, based on a name ir an id.
	 * Tries the name first. If it doesn't succed, tries the id.
	 * @param collection_name_or_id Collection's name or id
	 * @return the collection if found,  null otherwise
	 */
	public Collection getFromNameOrID(String collection_name_or_id) {
		List<Collection> collections = getFromName(collection_name_or_id)
		if (!collections) {
			return getFromID(Long.parseLong(collection_name_or_id))
		} else {
			if (collections.size() == 1) {
				return collections[0]
			} else {
				log.fatal "I have more than one collection with name $collection_name_or_id, can't return the list."
				log.fatal "Please disambiguate the collection names in the DB, or give me the ID instead."
				System.exit(0)
			}
		}
	}

	public updateValue(Long col_id, String column, value) {
		if (!col_id) throw new IllegalStateException("Collection col_id is not valid: "+col_id)
		def newvalue
		def object
		switch (Collection.type[column]) {
			case 'String': newvalue = value; break
			case 'Long': 
				newvalue = Long.parseLong(value); 
			break
			case 'User': 
				newvalue = Long.parseLong(value); 
				object = UserTable.getFromID(db, newvalue); 
			break
		}
		def res = getSaskiaDB().getDB().executeUpdate("UPDATE ${tablename} SET ${column}=? WHERE col_id=?",[newvalue, col_id])
		// if we have a User (object), add it to cache
		cacheIDCollection[col_id][column] = (object ? object : newvalue)
//		log.info "Updating value $column to $value for collection ${this}"
		return res
	}

	int collectionsOwnedBy(User user) {
		Map collections = listAccessibleCollectionsForUser(user)
		return collections.findAll{it.value.ouc_own == true}.size()
	}
}
