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
 
import java.io.IOException;

import org.apache.hadoop.mapred.*;

/**
 * @author Nuno Cardoso
 * Reads records that are delimited by a specific begin/end tag.
 */
public class HAREMInputFormat extends TextInputFormat {
    
    public static final String START_TAG_KEY = "xmlinput.start";
    public static final String END_TAG_KEY = "xmlinput.end";
 
    /**
     * Calls super configure.
     */
    public void configure(JobConf jobConf) {
        super.configure(jobConf);
    }

   @SuppressWarnings("unchecked")
    public RecordReader getRecordReader(InputSplit inputSplit, JobConf jobConf, Reporter reporter) throws IOException {
        return new XMLRecordReader((FileSplit) inputSplit, jobConf);
    }
} 