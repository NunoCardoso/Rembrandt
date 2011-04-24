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

package saskia.db

import saskia.bin.Configuration
import groovy.sql.Sql
import java.sql.SQLException
import org.apache.log4j.*

/**
 * @author Nuno Cardoso
 * This is the DB connect singleton.
 */
abstract class DB {
    
    def db
	 Configuration conf
    static Logger log = Logger.getLogger("DB")

    String default_db_driver = 'com.mysql.jdbc.Driver'
    String default_db_url = 'jdbc:mysql://127.0.0.1'
    String default_db_name = 'saskia'
    String default_db_user = 'saskia'
    String default_db_password = 'saskia'
    String default_db_param = 'useUnicode=yes&characterEncoding=UTF8&characterSetResults=UTF8&autoReconnect=true' 
    static long lastCall
 
	 String default_conf_driver = 'saskia.db.driver'
	 String default_conf_url = 'saskia.db.url'
	 String default_conf_name = 'saskia.db.name'
	 String default_conf_user = 'saskia.db.user'
	 String default_conf_password = 'saskia.db.password'
	 String default_conf_param = 'saskia.db.param'
	
    long timeout = 691200000
	
    /**
     * Initializes a connection to te DB.
     * @param conf Configuration file.
     */
    public DB(Configuration conf) {
		this.conf = conf
	 }
        
    public connect() {
	   	def driver, url, name, param, user, password
 		
        driver = conf.get(getDefault_conf_driver())
        if (!driver) {
            log.info "No DB driver, using default ${getDefault_db_driver()}"
            driver = getDefault_db_driver()
        }
        
        url= conf.get(getDefault_conf_url())
        if (!url) {
            log.info "No DB URL, using default ${getDefault_db_url()}"
            url = getDefault_db_url()
        }
        
        name= conf.get(getDefault_conf_name())
        if (!name) {
            log.info "No DB name, using default ${getDefault_db_name()}"
            name = getDefault_db_name()
        }
        
        user= conf.get(getDefault_conf_user())
        if (!user) {
            log.info "No DB user, using default ${getDefault_db_user()}"
            user = getDefault_db_user()
        }
        
        password= conf.get(getDefault_conf_password())
        if (!password) {
            log.info "No DB password, using default password"
            password = getDefault_db_password()
        }
        
        param= conf.get(getDefault_conf_param())
        if (!param) {
            log.info "No DB param, using default param"
            param = getDefault_db_param()
        }
        param = saskia.util.XMLUtil.decodeAmpersand(param)
        
        println "Connecting to $driver:$url/$name?${param}"
        try {
            db = Sql.newInstance("$url/${name}?${param}", user, password, driver)
            lastCall = System.currentTimeMillis()
            log.trace "DB: lastCall initialized with "+new Date(lastCall)
        }  catch (Exception e) {	   
            log.fatal "Can't open db: "+e.getMessage()
            log.fatal "Is your MySQL server running? You should check it out."
        }
        if (db) {
            log.info "Database initialized: $driver:$url/$name"
        } else {
            log.fatal "Database NOT initialized. Exiting."
            System.exit(0)
        }
    }
    
    
    
    /**
     * get DB.
     * Good to hook a timeout check. 
     */
    public getDB() {
        long thiscall = System.currentTimeMillis()
        boolean recent = ( (thiscall - lastCall) < timeout)
       //  log.trace "SaskiaDB: recent? $recent ($thiscall - $lastCall) < $timeout "
        
        if (recent) {
            lastCall = thiscall
         //   log.trace "SaskiaDB: returning recent db"
            return db
        }
        else {
            log.warn "Connection is old, I'll try to revive it"
            int retry = 3
            boolean connected = false 
            
            while (!connected && (retry > 0)) {
              log.warn "retrying..."
              try { db.eachRow("Select 1", {row -> 
                   def x = row[0]
                   if (x == 1) {
                    log.info "DB is now up and running"
                    connected = true
                   }
              })
              } catch (SQLException e) {
               //
               // The two SQL states that are 'retry-able' are 08S01
               // for a communications error, and 40001 for deadlock.
               //
               // Only retry if the error was due to a stale connection,
               // communications problem or deadlock
               //
               String sqlState = e.getSQLState()
               log.warn "Got SQL error $sqlState"
                if ("08S01".equals(sqlState) || "40001".equals(sqlState))  retry--
                else retry = 0
             }
           }
           if (connected) {
            lastCall = thiscall
            return db       
            }
        }
        log.error "Couldn't start DB, returning null"
        return null
    }
    /**
     * Close the database.
     */
    public close() {db.close() }
}  
