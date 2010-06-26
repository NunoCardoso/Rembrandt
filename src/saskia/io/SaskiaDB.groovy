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

import saskia.bin.Configuration
import groovy.sql.Sql
import java.sql.SQLException
import org.apache.log4j.*

/**
 * @author Nuno Cardoso
 * This is the DB connect singleton.
 */
class SaskiaDB {
    
    static SaskiaDB _this
    def db
    static Logger log = Logger.getLogger("SaskiaDB")
    static conf = Configuration.newInstance()
    String defaultDriver = 'com.mysql.jdbc.Driver'
    String defaultUrl = 'jdbc:mysql://127.0.0.1'
    String defaultName = 'rembrandtpool'
    String defaultUser = 'saskia'
    String defaultPassword = 'saskia'
    String defaultParam = 'useUnicode=yes&characterEncoding=UTF8&characterSetResults=UTF8&autoReconnect=true' 
    static long lastCall
    //mysql says 8 hours (8*24*60*60*1000 = 691200000)
    long timeout = 691200000
    
    /**
     * Get new instance of the DBConnect
     * @return new instance of DBConnect
     */
    public static SaskiaDB newInstance() {
        if (_this == null) _this = new SaskiaDB(SaskiaDB.conf)
        return _this
    } 
    
    /**
     * Initializes a connection to te DB.
     * @param conf Configuration file.
     */
    private SaskiaDB(conf) {
        
        def driver, url, name, param, user, password
        
        driver = conf.get('saskia.db.driver')
        if (!driver) {
            log.info "No DB driver, using default ${defaultDriver}"
            driver = defaultDriver
        }
        
        url= conf.get('saskia.db.url')
        if (!url) {
            log.info "No DB URL, using default ${defaultUrl}"
            url = defaultUrl
        }
        
        name= conf.get('saskia.db.name')
        if (!name) {
            log.info "No DB name, using default ${defaultName}"
            name = defaultName
        }
        
        user= conf.get('saskia.db.user')
        if (!user) {
            log.info "No DB user, using default ${defaultUser}"
            user = defaultUser
        }
        
        password= conf.get('saskia.db.password')
        if (!password) {
            log.info "No DB password, using default password"
            password = defaultPassword
        }
        
        param= conf.get('saskia.db.param')
        if (!param) {
            log.info "No DB param, using default param"
            param = defaultParam
        }
        param = saskia.util.XMLUtil.decodeAmpersand(param)
        
        log.debug "Connecting to $driver:$url/$name?${param}"
        try {
            db = Sql.newInstance("$url/${name}?${param}", user, password, driver)
            lastCall = System.currentTimeMillis()
            log.trace "SaskiaDB: lastCall initialized with "+new Date(lastCall)
        }  catch (Exception e) {	   
            log.fatal "Can't open db: ", e
        }
        if (db) {
            log.info "Database initialized."
        } else {
            log.fatal "Database NOT initialized. Check what's wrong. Exiting."
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
                    log.warn "DB is now up and running"
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