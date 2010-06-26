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

import java.io.DataOutputStream;
import java.io.IOException;
//import java.io.UnsupportedEncodingException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

/** 
 * @author Nuno Cardoso
 * An {@link OutputFormat} that writes plain text files.
 * Adapted from an Hadoop source file.   
 */
public class HAREMOutputFormat<K extends WritableComparable,
                              V extends Writable>
  extends TextOutputFormat<K, V> {

  protected static class LineRecordWriter<K extends WritableComparable,
     V extends Writable> implements RecordWriter<K, V> {
       
    private static final String utf8 = "UTF-8";
    private DataOutputStream out;
    
    public LineRecordWriter(DataOutputStream out) throws IOException  {
      this.out = out;
    }
    
    /**
     * Write the object to the byte stream, handling Text as a special
     * case.
     * @param o the object to print
     * @throws IOException if the write throws, we pass it on
     */
    private void writeObject(Object o) throws IOException {
      if (o instanceof Text) {
        Text to = (Text) o;
        out.write(to.getBytes(), 0, to.getLength());
      } else {
        out.write(o.toString().getBytes(utf8));
      }
    }

    @Override
    public synchronized void write(K key, V value)
      throws IOException {

      writeObject("<DOC DOCID=\""+key.toString()+"\">\n");
      writeObject(value.toString()+"\n</DOC>");
    }

    @Override
    public void close(Reporter arg0) throws IOException {
	out.close();	
    }
  }
}

