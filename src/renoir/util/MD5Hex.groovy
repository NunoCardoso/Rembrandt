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
package renoir.util
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @author dr. Xi, taken from http://www.xinotes.org/notes/note/370/
 *
 */
class MD5Hex {
   
    public static String digest(String s) {
        
     String result = null
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                byte[] digest = md5.digest(s.getBytes());
                result = toHex(digest);
            }
            catch (NoSuchAlgorithmException e) {
                // this won't happen, we know Java has MD5!
            }
            return result;
        }
        
        public static String toHex(byte[] a) {
            StringBuilder sb = new StringBuilder(a.length * 2);
            for (int i = 0; i < a.length; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        }
        
        public static void main(String[] args) {
            println "MD5 for "+args[0]+" = "+digest(args[0])
        }
    }
   
