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

package rembrandt.hadoop;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import rembrandt.bin.RembrandtCore;
import rembrandt.io.Reader;
import rembrandt.io.StyleTag;
import rembrandt.io.Writer;
import rembrandt.util.XMLUtil;
import saskia.bin.Configuration;

/**
 * @author Nuno Cardoso HAREMMap maps documents to an Hadoop Map tip, and
 *         releases RembrandtCore on it.
 */
public class HAREMMap extends MapReduceBase implements
	Mapper<Text, Text, Text, Text> {

    // DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    // DocumentBuilder docBuilder;
    // org.w3c.dom.Document doc;
    Configuration rconf;
    Reader reader;
    Writer writer;
    StyleTag style;

    /**
     * Configure HAREMMap, by fetching the Rembranft Configuration object passed
     * as an argument in the JobConf.
     */
    @Override
    public void configure(JobConf conf) {
	String resource = conf.get("rembrandt.conf");
	rconf = Configuration.newMapInstance(resource);
    }

    /**
     * map method.
     */
    public void map(Text key, Text value, OutputCollector<Text, Text> output,
	    Reporter reporter) throws IOException {

	// doc = null;
	/*
	 * try { docBuilder = dbf.newDocumentBuilder(); doc =
	 * docBuilder.parse(new InputSource(new
	 * StringReader(value.toString().trim())));
	 * doc.getDocumentElement().normalize(); }catch (Exception e) {
	 * System.out
	 * .println("Error in converting DOM of the value passed to HAREMMap.");
	 * e.printStackTrace(); }
	 */
	// String docid = doc.getDocumentElement().getAttribute("DOCID");
	// String text = doc.getDocumentElement().getTextContent().trim();
	// rembrandt.obj.Document doc = new rembrandt.obj.Document(text,docid);

	try {
	    reader = (Reader) Class
		    .forName(rconf.get("rembrandt.input.reader"))
		    .getDeclaredConstructor(new Class[] {}).newInstance();
	    String inputencoding = (rconf.get("rembrandt.input.encoding"));
	    if (inputencoding == null || inputencoding == "")
		inputencoding = "UTF-8";
	    // reader.processInputStream(new ByteArrayInputStream(
	    // XMLUtil.encodeAmpersand(value.toString().trim()).getBytes(inputencoding)));

	    reader.processInputStream(new InputStreamReader(
		    new ByteArrayInputStream(XMLUtil.encodeAmpersand(
			    value.toString().trim()).getBytes(inputencoding)),
		    inputencoding));

	    style = (StyleTag) Class.forName(
		    rconf.get("rembrandt.output.tagstyle"))
		    .getDeclaredConstructor(new Class[] {}).newInstance();
	    writer = (Writer) Class.forName(
		    rconf.get("rembrandt.output.writer"))
		    .getDeclaredConstructor(
			    new Class[] { rembrandt.io.StyleTag.class })
		    .newInstance(style);
	    // printer = new HAREM_II_DocumentWriter(new HAREM_II_StyleTags());
	} catch (IllegalArgumentException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	} catch (SecurityException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	} catch (InstantiationException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	} catch (IllegalAccessException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	} catch (InvocationTargetException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	} catch (NoSuchMethodException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	} catch (ClassNotFoundException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	}

	// build class RembrandtCoreXX, XX = PT, EN, etc.
	Class<?> rembrandtCoreClass = null;
	try {
	    rembrandtCoreClass = Class.forName("rembrandt.bin.RembrandtCore"
		    + (rconf.get("rembrandt.lang").toUpperCase()));
	} catch (ClassNotFoundException e1) {
	    System.err
		    .println("Could not find the correct RembrandtCore class. Check rembrandt.lang property");
	    e1.printStackTrace();
	}
	// Get constructor that accepts a Configuration object
	Constructor<?> con = null;
	try {
	    con = rembrandtCoreClass
		    .getDeclaredConstructor(new Class[] { Configuration.class });
	} catch (SecurityException e1) {
	    e1.printStackTrace();
	} catch (NoSuchMethodException e1) {
	    System.err.println("Could not find the constructor method for "
		    + rembrandtCoreClass.getClass().getName()
		    + ". Check your code.");
	    e1.printStackTrace();
	}
	RembrandtCore core = null;
	try {
	    core = (RembrandtCore) con.newInstance(rconf);
	} catch (IllegalArgumentException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	} catch (InstantiationException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	} catch (IllegalAccessException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	} catch (InvocationTargetException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

	String taggedDocumentText = null;
	try {
	    System.out.println("Got " + reader.getNumberOfDocuments()
		    + " docs.");
	    for (int i = 0; i < reader.docs.size(); i++) {

		rembrandt.obj.Document doc = (rembrandt.obj.Document) reader.docs
			.get(i);
		System.out.println("doc id: " + doc.getDocid());
		rembrandt.obj.Document taggedDoc = core
			.releaseRembrandtOnDocument(doc);
		taggedDocumentText = writer.printDocument(taggedDoc);

		output.collect(new Text((String) doc.getDocid()), new Text(
			taggedDocumentText));
	    }
	} catch (Exception e) {
	    System.out.println("Error executing RembrandtCore:");
	    e.printStackTrace();
	}
    }
}
