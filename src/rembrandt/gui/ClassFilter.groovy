package rembrandt.gui

import java.util.jar.*;
import java.util.*;
import java.io.*;

/**
 gets a list of classes
 */
public class ClassFilter {

	public static List getReaderClasses() {
		return ClassFilter.getClasses("rembrandt.io.Reader","Reader")
	}
	public static List getWriterClasses(){
		return ClassFilter.getClasses("rembrandt.io.Writer","Writer")
	}

	public static List getClasses(String packageName, String filter){
		def classes = []
		if (!filter) filter = ".*"

		Class clazz = Class.forName(packageName);
		String className = clazz.getSimpleName()
		String jarName = clazz.getResource(className+ ".class").toString();

		if (!jarName.startsWith("jar")) {
			jarName = jarName.replaceAll(/^file:/, "")
			jarName = jarName.replaceAll(/\/[^\/]*$/, "")
			println jarName

			List files = new File(jarName).list().toList()

			files?.each{
				println it
				if (it.matches(filter)) {
					classes << it
				}
			}
		} else {
			jarName = jarName.replaceAll("\\." , "/")

			try{
				JarInputStream jarFile = new JarInputStream(new FileInputStream (jarName))
				JarEntry jarEntry

				while(true) {
					jarEntry=jarFile.getNextJarEntry()
					if(jarEntry == null){
						break;
					}

					if((jarEntry.getName().startsWith (packageName)) &&
					(jarEntry.getName().endsWith (".class")) &&
					jarEntry.getName().matches(filter) ) {

						classes << jarEntry.getName().replaceAll("/", "\\.")
					}
				}
			}
			catch( Exception e){
				e.printStackTrace ();
			}
		}
		return classes;
	}

	/**
	 *
	 */
	/*  public static void main (String[] args){
	 List list =  PackageUtils.getClasseNamesInPackage
	 ("C:/j2sdk1.4.1_02/lib/mail.jar", "com.sun.mail.handlers");
	 System.out.println(list);
	 /*
	 output :
	 Jar C:/j2sdk1.4.1_02/lib/mail.jar looking for com/sun/mail/handlers
	 Found com.sun.mail.handlers.text_html.class
	 Found com.sun.mail.handlers.text_plain.class
	 Found com.sun.mail.handlers.text_xml.class
	 Found com.sun.mail.handlers.image_gif.class
	 Found com.sun.mail.handlers.image_jpeg.class
	 Found com.sun.mail.handlers.multipart_mixed.class
	 Found com.sun.mail.handlers.message_rfc822.class
	 [com.sun.mail.handlers.text_html.class, 
	 com.sun.mail.handlers.text_xml.class,  com
	 .sun.mail.handlers.image_jpeg.class, 
	 , com.sun.mail.handlers.message_rfc822.class]
	 }*/
}