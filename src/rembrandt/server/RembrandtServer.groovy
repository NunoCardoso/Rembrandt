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

 import org.apache.log4j.*
 import org.slf4j.bridge.SLF4JBridgeHandler
 import saskia.bin.Configuration
 import org.restlet.Component
 import org.restlet.data.Protocol
 
 /**
  * @author Nuno Cardoso
  * 
  * This is the main class. Reads in args[0] a configuration file that sets up 
  * the RembrandtServer, such as server port. proxy settings, etc. 
  */

class RembrandtServer {

	static Logger log = Logger.getLogger("RembrandtServerMain")  
	Component component
		
	public RembrandtServer(Configuration conf, int port) {
        SLF4JBridgeHandler.install()
	   try {
	        component = new Component()           
		    component.servers.add(Protocol.HTTP, port)
		    component.defaultHost.attach(new RembrandtServerApplication(conf))           		 
	   } catch (Exception e) { e.printStackTrace()}	          	  
	}
		
	void start() {
	    component.start()
	}
		
	public static void main(args) {
	    
	    def conf
 		if (args.size() < 1) conf = Configuration.newInstance()
 		else conf = Configuration.newInstance(args[0])
 		int port = conf.getInt("rembrandt.server.port",33333)
		RembrandtServer server = new RembrandtServer(conf, port)
	    server.start()	
	    log.info "RembrandtServer starting on port ${port}"

	}
}