/* Rembrandt
 * Copyright (C) 2008 Nuno Cardoso. ncardoso@xldb.di.fc.ul.pt
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

package rembrandt.util

 /**
  * @author Nuno Cardoso
  * Utilities for encoding and converting stuff.
  */
class XMLUtil {
	
    /**
     * Encode Ampersands (&amp; to &amp;amp;)
     */
    public static String encodeAmpersand(String text) {	
		return text.replaceAll("&","&amp;")	
    }
   
    
    /**
     * Encode greater lower than (&amp; to &amp;amp;)
     */
    public static String encodeLowerGreaterThan(String text) {	
		return text.replaceAll("<","&lt;").replaceAll(">","&gt;")	
    }
    /**
     * Decode Ampersands (&amp;amp; to &amp;)
     */
    public static String decodeAmpersand(String text) {
		return text.replaceAll("&amp;","&")	
    }
    
    /**
     * Decode XML characters
     */
    public static String decodeXML(String text) {
 	   def text2 = text.replaceAll("&amp;","&")
 	   text2 = text2.replaceAll("&quot;","\"")
 	   text2 = text2.replaceAll("&apos;","'")
 	   text2 = text2.replaceAll("&lt;","<")
	   text2 = text2.replaceAll("&gt;",">")
 	   return text2
    }  
    
    /**
     * Encode XML characters
     */
    public static String encodeXML(String text) {
	   def text2 = text.replaceAll("&","&amp;")
	   text2 = text2.replaceAll("\"","&quot;")
	   text2 = text2.replaceAll("'","&apos;")
	   text2 = text2.replaceAll("<","&lt;")
	   text2 = text2.replaceAll(">","&gt;")
	   return text2
 }
    
    public static String cleanStrangeChars(String text) {
		def text2 = text.replaceAll("\u2022","") // seta a apontar para a direita
		text2 = text2.replaceAll("\u2023","") // bola de bullet
		text2 = text2.replaceAll("\u00B2","2") // 2 acima
		text2 = text2.replaceAll("\u0153","oe") // oe
		text2 = text2.replaceAll("&#160;"," ") // oe
	
		return text2
    }	
    
    /**
     * Trim the newlines.
     */
    public static String trimNewlines(String text) {
	   def text2 = text.replaceAll("^\\n+",'')
 	   text2 = text2.replaceAll('\\n+$','')
 	   return text2.trim()
    }
    
    /**
     * Convert UTF-8 strings to safe ISO strings.
     */
    public static String safeString(String text) {
	
		text = text.replaceAll("[&\"<>]","")
		text = text.replaceAll("[\u0101\u0103\u04D1]","a") // A com traço, a com semicirculo invertido
		text = text.replaceAll("\u0110","D") // D com traço
		text = text.replaceAll("[\u0115\u0119\u011B]","e") // e  com traço, a com semicirculo invertido, com cedilha
		text = text.replaceAll("\u0142","l") // l traçado
		text = text.replaceAll("\u0144","n") // n com acento 
		text = text.replaceAll("[\u0161\u015F]","s") // s com circunflexo invertido, s com cedilha
		text = text.replaceAll("\u0160","S") // S com circunflexo invertido
		text = text.replaceAll("[\u0107\u010D]","c") // c  com acento, c com circunflexo invertido
	    return text   
   }
    
   public static getXMLheader(String encoding) {
       return "<?xml version=\"1.0\" encoding=\"${encoding}\" ?>"
   }
}