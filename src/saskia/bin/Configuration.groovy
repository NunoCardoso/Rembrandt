
/* Configuration - Sets up configuration properties.
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

package saskia.bin

import javax.xml.parsers.*
import javax.xml.transform.*
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.apache.log4j.*
import org.w3c.dom.*
import java.util.regex.*

/**
 * @author Nuno Cardoso
 * 
 * Class "borrowed" from Apache's Hadoop source. 
 * All credits of this code goes to Apache Software Foundation gang.
 * I just altered it to suit my needes.
 * 
 * This class parses a XML configuration file.
 */
class Configuration {
    
    public static Logger log = Logger.getLogger("Configuration")     
    def resource = null
    static String defaultconf = System.getProperty("user.dir")+System.getProperty("file.separator")+"rembrandt.properties"
    def List projects = ["global","rembrandt","renoir","saskia"]
    def Properties properties = null 
    def varPat = ~/.*(\$\{[^\}]*\}).*/
    private static int MAX_SUBST = 20	
	static Configuration _this
	
	/**
	 * Get new instance of the Configuration
	 * @param config_resource_path Path to the XML file. Can be internal (jar) or external (file system)
	 */
	public static Configuration newInstance(String config_resource_path = null) {
           
        
	    if (!_this) {
		if (!config_resource_path) {
		    if (new File(Configuration.defaultconf).exists()) {
			log.info "Found default configuration ${Configuration.defaultconf}, loading."
			_this = new Configuration(Configuration.defaultconf)
		    } else {
			log.warn "No configuration file given, Will use default values and environment values. You've been warned!"
			_this = new Configuration(null)
		    }
		} else {
		    _this = new Configuration(config_resource_path)
		}
	    }
	    return _this
	}
	
	/* Get new instance of the Configuration, but passing as parameters a proper XML configuration text.
	 * This is essential for Hadoop, since the XML configuration is passed by a Hadoop job parameter.
	 * This method should be called by Hadoop Map instances.
	 * @param the XML configuration file. 
	 */
	public static Configuration newMapInstance(config_resource) {
	     // hadoop string is here only to select a different private constructor.
		if (_this == null)  _this = new Configuration(config_resource, "hadoop")		
	    return _this		
	}
	
	/**
	 * Get the current XML configuration file.
	 * @return The XML file.
	 */
	public String getResource() {
	    return resource
	}

	/** 
	 * Private constructor for Hadoop Map calls.
	 * @param config_resource the XML configuration. 
	 * @param hadooped flag for Hadoop Map call. 
	 */
	private Configuration(String config_resource, String hadooped) {
	    resource = config_resource
	}
	
	/**
	 * Private constructor. 
	 * Parses a configuration resource file, tries to search on JAR and file system, 
	 * parses XML configuration file.
	 * @param conf the configuration resource file. 
	 */
    private Configuration(String conf) {
    	    def conffile 
	    if (conf) try {conffile= new File(conf)} catch(Exception e) {}
	    if (conffile?.exists()) {
			// Read from file system. 
			log.info "Found configuration file on file system: "+conffile.absolutePath
			resource = ''
			conffile.eachLine{resource += it+"\n"}
			
	    } 
	    properties = new Properties()
	    if (resource) {
			
	      def configuration
	      try {
		      configuration = new XmlParser().parseText(resource)
	      } catch (Exception e) {e.printStackTrace()}
	      
		  configuration.property.each{ prop ->

		     def attr = null, value = null	   
	         prop.name.each{ name_ ->  attr = name_.text()}
		     prop.value.each{ value_ ->  value = value_.text()}

	         if (attr != null && value != null) {       
	            properties.setProperty(attr, value) 
	         } else { 
	         	log.warn(name+":a attempt to override final parameter: "+attr +";  Ignoring.")
	         }
	      }
     	}
		// going for env variables	

		 def relevantVars = System.getProperties().keySet().findAll{it -> 
			projects.contains(it.split(/\./)?.getAt(0))}
		 relevantVars.each{var -> properties.setProperty(var, System.getProperty(var))}   
		
		if (!properties.get("global.lang")) {
			properties.setProperty("global.lang", System.getProperty("user.language"))
		}
	}
	
	//else {
		    // search JAR and classpath. 
		/*	if (!conf.startsWith("/")) conf = "/"+conf
			def url = ClassLoader.getResource(conf)
			
			// Read from JAR.
			if (url != null) {	
				log.info "Found configuration file on jar: "+url.toString()
			    def inputStream = ClassLoader.getResourceAsStream(conf)		   
			    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))		   
			    StringBuffer buffer = new StringBuffer()		    
			    def line
			    while ((line = br.readLine()) != null) {  buffer.append(line+"\n") }
			    resource = buffer.toString()
			    if (resource.length() > 10) success = true
			}
	    }
		*/
		
	//	}
    
    
	/**
	 * Replaces values with pattern ${}. For instance, ${os.name} is converted to its name.
	 * @param expr Value to be parsed.
	 * @return Value resolved to the proper value.
	 */
    private String substituteVars(String expr) {
	    // println "substituteVars(1) expr: $expr"
	    if (expr == null) {return null}	    
	    //Matcher match = varPat.matcher("");
	    String eval = expr;
	      //match.reset();
	      //println "eval: $eval varPat $varPat"
	      def match = eval =~ varPat
	      def matches = match.matches()
	      //println "match: $match"
	    //  println "matche(2):"+matches
	      if (!matches) { return eval }
	      //println "Match(3): ${match} eval:${eval}"
	      String var = match.group(1);
	     // println "var(3.5): $var"
	      def var2 = var.substring(2, var.length()-1); // remove ${ .. }
	    //  println "var(4) = $var2"
	      //println System.getProperties()
	      String val = get(var2);
	     // println "val(5) = $val"
	      if (val == null) {
	        val = getRaw(var);
	      }
	      if (val == null) {
	        return eval; // return literal ${var}: var is unbound
	      }
	      // substitute
	   //   eval = eval.substring(0, match.start())+val+eval.substring(match.end());
		 // println "Gonna replace ($eval) $var with $val"
	      eval = eval.replaceAll(/\$\{$var2\}/,val)
	    // println "Eval is now $eval" 
		  return eval
	  }
	 
	/**
	 * Get a configuration value, converting dynamic patterns in the way.
	 * @param name name of the property
	 * @return value of the property
	 */
    public String get(String name) {
	    return substituteVars(properties.getProperty(name))
	}
    
	/**
	 * Get a configuration value, bypassing dynamic pattern conversion.
	 * @param name name of the property
	 * @return value of the property
	 */
    public String getRaw(String name) {
	    return properties.getProperty(name)
	 }
    
    /**
	 * Set a configuration value
	 * @param name name of the property to be set
	 * @param value value of the property
	 */
    public void set(String name, String value) {    
	    properties.setProperty(name, value)
    }
    
    /**
	 * Get a configuration value, and set a default value if failed.
	 * @param name name of the property
	 * @param defaultValue default value for the property
	 * @return Value of the property
	 */
    public String get(String name, String defaultValue) {
	    return substituteVars(properties.getProperty(name, defaultValue))
	}
    
    /**
	 * Get an Integer configuration value, and set a default value if failed.
	 * @param name name of the property
	 * @param defaultValue default value for the property
	 * @return Value of the property
	 */
    public int getInt(String name, int defaultValue) {
	    String valueString = get(name)
	    if (!valueString) {
			log.warn "Property $name not found, returning default value $defaultValue"
			return defaultValue
		}
	    try {
	      return Integer.parseInt(valueString)
	    } catch (NumberFormatException e) {
			log.warn "Property $name is not a number, returning default value $defaultValue"
	        return defaultValue
	    }
	  }

    /**
	 * Set an Integer configuration value.
	 * @param name name of the property
	 * @param value value for the int property
	 */
	  public void setInt(String name, int value) {
	    set(name, Integer.toString(value))
	  }

	   /**
		* Get a Long configuration value, and set a default value if failed.
		* @param name name of the property
		* @param defaultValue default value for the property
		* @return Value of the property
		*/
	  public long getLong(String name, long defaultValue) {
	    String valueString = get(name)
	    if (!valueString) {
			log.warn "Property $name not found, returning default value $defaultValue"
			return defaultValue
		}
	    try {
	      return Long.parseLong(valueString)
	    } catch (NumberFormatException e) {
			log.warn "Property $name is not a number, returning default value $defaultValue"
	      return defaultValue
	    }
	  }
	  
	  /**
	   * Set a Long configuration value.
	   * @param name name of the property
	   * @param value value for the long property
	   */
	  public void setLong(String name, long value) {
	    set(name, Long.toString(value))
	  }

	  /**
	   * Get a Float configuration value, and set a default value if failed.
	   * @param name name of the property
	   * @param defaultValue default value for the property
	   * @return Value of the property
	   */
	  public float getFloat(String name, float defaultValue) {
	    String valueString = get(name)
	    if (!valueString)  {
			log.warn "Property $name not found, returning default value $defaultValue"
			return defaultValue
		}
	    try {
	      return Float.parseFloat(valueString)
	    } catch (NumberFormatException e) {
		   log.warn "Property $name is not a number, returning default value $defaultValue"
	      return defaultValue
	    }
	  }

	  /**
	   * Get a Boolean configuration value, and set a default value if failed.
	   * @param name name of the property
	   * @param defaultValue default value for the property
	   * @return Value of the property
	   */
	  public boolean getBoolean(String name, boolean defaultValue) {
	    String valueString = get(name)
	    if ("true".equals(valueString)) return true
	    else if ("false".equals(valueString)) return false
	    else {
			log.warn "Property $name not found, returning default value $defaultValue"
			return defaultValue
		}
	  }

	  /**
	   * Set a Boolean configuration value.
	   * @param name name of the property
	   * @param value value for the property
	   */
	  public void setBoolean(String name, boolean value) {
	    set(name, Boolean.toString(value))
	  }

	  /**
	   * Get a configuration value that can have multiple values.
	   * @param name name of the property
	   * @return values for the property
	   */
	  public List getStrings(String name) {
	    if (!name) {
			log.warn "Property $name not found."
			return null
		}
	    return get(name)?.split(",")
	  }
	 
	  public String[] getStringsArray(String name) {
		    if (!name) {
				log.warn "Property $name not found."
				return null
			}
		    def res =  get(name)?.split(",")
			return res
	}
	  
	  /**
	   * Get a File from a property value.
	   * @param dirsProp name of the directory
	   * @param dpath The path.
	   * @return values for the property
	   */
	  public File getFile(String dirsProp, String path)
	    throws IOException {
	    String[] dirs = getStrings(dirsProp).toArray(
		    new String[getStrings(dirsProp).size()]);
	    int hashCode = path.hashCode();
	    for (int i = 0; i < dirs.length; i++) {  // try each local dir
	      int index = (hashCode+i & Integer.MAX_VALUE) % dirs.length;
	      File file = new File(dirs[index], path);
	      File dir = file.getParentFile();
	      if (dir.exists() || dir.mkdirs()) {
	        return file;
	      }
	    }
	    throw new IOException("No valid local directories in property: "+dirsProp);
	  }

	 /**
	  * Iterator for all properties. 
	  * @return Iterator.
	  */
	  public Iterator<Map.Entry<String, String>> iterator() {

	    Map<String,String> result = new HashMap<String,String>()
	    for(Map.Entry<Object,Object> item: properties.entrySet()) {
	      if (item.getKey() instanceof String && item.getValue() instanceof String) {
	        result.put((String) item.getKey(), (String) item.getValue())
	      }
	    }
	    return result.entrySet().iterator()
	  }

	  /**
	   * Write configuration properties to output stream
	   * @param out Outputstream to write configurations.
	   */
	   public void write(OutputStream out) throws IOException {

	    try {
	      Document doc =
	        DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
	      Element conf = doc.createElement("configuration")
	      doc.appendChild(conf)
	      conf.appendChild(doc.createTextNode("\n"))
		  	properties.keys().each{name ->
	       
	        if (name instanceof String) {
	            def object = properties.get(name)
	            String value = null
	            if (object instanceof String) value = (String) object
	                    
	            Element propNode = doc.createElement("property")
	            conf.appendChild(propNode)      
	            Element nameNode = doc.createElement("name")
	            nameNode.appendChild(doc.createTextNode(name))
	            propNode.appendChild(nameNode)      
	            Element valueNode = doc.createElement("value")
	            valueNode.appendChild(doc.createTextNode(value))
	            propNode.appendChild(valueNode)
	            conf.appendChild(doc.createTextNode("\n"))
	        }
	      }
	      
	      DOMSource source = new DOMSource(doc)
	      StreamResult result = new StreamResult(out)
	      TransformerFactory transFactory = TransformerFactory.newInstance()
	      Transformer transformer = transFactory.newTransformer()
	      transformer.transform(source, result)
	    } catch (Exception e) {
	      throw new RuntimeException(e)
	    }
	  }

	 /**
	  * Write Configuration to String.
	  * @return string.
	  */
	  public String toString() {
	    StringBuffer sb = new StringBuffer()
	    sb.append("Configuration: ")
	    //toString(resources, sb)
	    return sb.toString()
	  }

	  /** For debugging purposes. Reads a configuration file from args[0], 
	   * dumps properties to standard output. If not set args[0], outputs 
	   * the default configuration set in Rembrandt.defaultconf. */
	  public static void main(args)  {

	   def conf
	   if (args.size() < 1) {
		    log.info "No configuration file given. Using default file."
		    conf = Configuration.newInstance(Configuration.defaultconf) 
		} else {
		    conf = Configuration.newInstance(args[0])
		}
	    conf.write(System.out)
	  }
}
