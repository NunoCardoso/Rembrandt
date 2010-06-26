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
package saskia.patches

/**
 * @author Nuno Cardoso
 * I have to split to get batches under the daily API limit of 50K calls. 
 *
 */
class SplitFlickrShapefile {
    
    public static void main(args) {
        int limit = 45000
        
        int count = 0
        int filenumber = 0
        
        // XML headers and <places> to be exported to other files
        String header =""
        boolean	inplace	= false
        
        File fw = new File(args[0]+"."+filenumber)
        
        new File(args[0]).eachLine{l ->
            String l2 = l
            fw.append(l+"\n")
            if (!inplace) header += l+"\n"
            
            if (l2.trim().startsWith("<places ")) inplace = true
            
            if (l2.trim().startsWith("</place>")) {
                count++
                if (count == limit) {
                    println "Reaching limit. Changing files"
                    fw.append("</places>")
                    
                    filenumber++
                    fw = new File(args[0]+"."+filenumber)
                    fw.append header
                    count = 0
                }
            }
        }
    }
}
