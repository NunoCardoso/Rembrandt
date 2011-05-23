/**
 * 
 * Adapted and improved from http://www.iuliandogariu.com/2006/09/getting-groovy-with-json/
 * 
 */
package renoir.server

import java.util.Map
import java.util.List
import java.util.Set
import net.sf.json.JSONObject
import net.sf.json.JSONArray
import net.sf.json.JSONSerializer
/**
 * @author rlopes
 *
 */
public class JSONHelper {

    public static fromJSONObject(obj) {
	   def j = JSONObject.fromObject( obj );  
	   return j
    }
    
    public static fromJSONArray(obj) { 	
	   def j = JSONArray.fromObject( obj );  
	   return j
   }
    
	public static String toJSON(obj) {
		String ret = "${obj}"
		
		if (obj == null) {
			ret = "null"
		}
		else if (obj instanceof String) {
			def escaped = obj.replace("\"", "\\\"")
			ret = "\"${escaped}\""
		}
		else if (obj instanceof Date) {
			def escaped = obj.toString().replace("\"", "\\\"")
			ret = "\"${escaped}\""
		}
		else if (obj instanceof Map) {
	        ret = "{" + obj.keySet().inject("") { accu, k ->
	            (accu.length() == 0? "": "${accu},") +
	            "\"${k}\": ${JSONHelper.toJSON(obj[k])}"
	        } + "}"
	    }
	    else if (obj instanceof List || obj instanceof Set) {
	        ret = "[" + obj.inject("") { accu, item ->
	            (accu.length() == 0? "": "${accu},") + JSONHelper.toJSON(item)
	        } + "]"
	    }
		
		return ret.replaceAll(/\n/,"")
	}
	
}
