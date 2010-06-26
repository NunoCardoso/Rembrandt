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

 import org.apache.log4j.*
 import org.slf4j.bridge.SLF4JBridgeHandler

 import saskia.bin.Configuration
 import org.restlet.Component
 import org.restlet.data.Protocol

 /**
  * @author Nuno Cardoso
  * 
  * Renoir Server. Reads in args[0] a configuration file that sets up 
  * the Renoir, such as server port. proxy settings, etc. 
  */

class RenoirServer {
     
	def static Logger log = Logger.getLogger("RenoirServerMain")  
	Component component
		
	public RenoirServer(int port) {
	    SLF4JBridgeHandler.install()
	   try {
	        component = new Component()           
		    component.servers.add(Protocol.HTTP, port)
		    component.defaultHost.attach(new RenoirServerApplication())           		 
	   } catch (Exception e) { e.printStackTrace()}	          	  
	}
		
	void start() {
	    component.start()
	}
		
	public static void main(args) {
	    
	    def conf
 		if (args.size() < 1) conf = Configuration.newInstance()
 		else conf = Configuration.newInstance(args[0])
 		int port = conf.getInt("renoir.server.port",33335)
		RenoirServer server = new RenoirServer(port)
	    server.start()	
	    log.info "RenoirServer starting on port ${port}"

	}
}
