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
 
 
package renoir.bin

/** Message wrapper for Renoir Client-to-server communications.
    goal: to facilitate the error report from server, 
	and the exception formats.
**/
class RenoirMessage {
	// -1: error
	// 1: good	
	int code
	def message
	
	static generateError(Exception e, String additionalMessage="") {
		StackTraceElement[] trace = e.getStackTrace()
		StackTraceElement tr = null
		trace.each{t-> 
			if (t.getClassName().matches("renoir")) {
				tr = t
				return
			}
		}
		if (!tr) tr = trace[0]
		return new RenoirMessage(code:-1, 
		 message:"${additionalMessage}\n"+
	      "Class: ${tr.getClassName()}\n"+
		  "Method: ${tr.getMethodName()}\n"+
		  "Line: ${tr.getLineNumber()}\n"
		)
	}
}