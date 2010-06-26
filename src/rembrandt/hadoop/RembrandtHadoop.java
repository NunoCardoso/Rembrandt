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

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import saskia.bin.Configuration;
import rembrandt.util.*;

/**
 * Main configuration for the Rembrandt plugin for Hadoop.
 * It increases timeout on the machines, reads a Rembrandt Configuration file 
 * and stuffs it into the JobConf in order to pass it on all Map tasks.
 * @author Nuno Cardoso
 */
public class RembrandtHadoop  {
	
       /**
        * Main method. 
        * @param args
        * @throws Exception
        */
	public static void main(String[] args) throws Exception {
		    
	   String conffilepath = null;	   

	   JobConf jobconf = new JobConf(RembrandtHadoop.class);
	    
	   if (args.length < 1) {
		System.out.println("No conf file provided. Exiting.");
		System.exit(0); 
	   } else {
		conffilepath = args[0];
	   }
	     
	   Configuration rembrandtConf = Configuration.newInstance(conffilepath);
	   jobconf.setJobName("Rembrandt v."+rembrandt.bin.Rembrandt.getVersion()); 
	  
	   // stuff configuration in JobConf, to pass o all Map instances.
	   jobconf.set("rembrandt.conf", rembrandtConf.getResource());
	   	    
       	   // for jobtasker local debug
       	   // jobconf.set("mapred.job.tracker", "local");
	    
	   // increase timeout 
	   jobconf.setInt("mapred.task.timeout",
		   rembrandtConf.getInt("rembrandt.hadoop.timeout",1000000)) ;
	   jobconf.setInt("mapred.tasktracker.expiry.interval",
		   rembrandtConf.getInt("rembrandt.hadoop.timeout",1000000)) ;

	   jobconf.setOutputKeyClass(Text.class);
	   jobconf.setOutputValueClass(Text.class);
		    
	   jobconf.setMapperClass(HAREMMap.class);
	   jobconf.setReducerClass(HAREMReduce.class); 
       
	    // etiquetas que separam os registos dos ficheiros
	   jobconf.set("xmlinput.start", XMLUtil.decodeXML((String) 
		rembrandtConf.getProperty("rembrandt.hadoop.xmlinput.start")));
	   jobconf.set("xmlinput.end", XMLUtil.decodeXML((String) 
			rembrandtConf.getProperty("rembrandt.hadoop.xmlinput.end")));
	     
	   // args must be counted from right to left, 
	   // because of the additional args for the jar file.
	   jobconf.setInputPath(new Path(args[args.length-2]));
	   jobconf.setOutputPath(new Path(args[args.length-1]));
 
	   jobconf.setInputFormat(HAREMInputFormat.class);
	   jobconf.setOutputFormat(HAREMOutputFormat.class);
	    
	   int nummaps = rembrandtConf.getInt("rembrandt.hadoop.map.number", -1);
	   if (nummaps >0) jobconf.setNumMapTasks(nummaps);
	   int numreduces = rembrandtConf.getInt("rembrandt.hadoop.reduce.number", -1);
	   if (numreduces >0) jobconf.setNumReduceTasks(numreduces);
	    
	   JobClient.runJob(jobconf);
     }
 }