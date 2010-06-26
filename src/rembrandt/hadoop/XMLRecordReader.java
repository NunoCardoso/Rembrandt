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

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;


/**
 * @author Nuno Cardoso
 * This code was adapted from a XMLRecordReader, published by Colin Evans in the 
 * Nabble Hadoop users, in http://www.nabble.com/map-reduce-function-on-xml-string-td15816818.html
 * It marks the boundaries of each document on the file.
 */
@SuppressWarnings("unchecked")
public class XMLRecordReader implements RecordReader {
        private byte[] startTag; 
        private byte[] endTag;
        private long start;
        private long end;
        private FSDataInputStream fsin;
        private DataOutputBuffer buffer = new DataOutputBuffer();
        private String inputEncoding = "UTF-8";

        public XMLRecordReader(FileSplit split, JobConf jobConf) throws IOException {
            startTag = jobConf.get("xmlinput.start").getBytes(inputEncoding);
            endTag = jobConf.get("xmlinput.end").getBytes(inputEncoding);

            // open the file and seek to the start of the split
            start = split.getStart();
            end = start + split.getLength();
            Path file = split.getPath();
            FileSystem fs = file.getFileSystem(jobConf);
            fsin = fs.open(split.getPath());
            fsin.seek(start);
        } 

        public boolean next(WritableComparable key, Writable value) throws IOException {
            if (fsin.getPos() < end) {
                if (readUntilMatch(startTag, false)) {
                    try {
                        buffer.write(startTag);
                        if (readUntilMatch(endTag, true)) {
                            ((Text) key).set(Long.toString(fsin.getPos()));
                            ((Text) value).set(buffer.getData(), 0, buffer.getLength());
                            return true;
                        }
                    }
                    finally {
                        buffer.reset();
                    }
                }
            }
            return false;
        }

        public WritableComparable createKey() {
            return new Text();
        }

        public Writable createValue() {
            return new Text();
        }

        public long getPos() throws IOException {
            return fsin.getPos();
        }

        public void close() throws IOException {
            fsin.close();
        }

        public float getProgress() throws IOException {
            return ((float) (fsin.getPos() - start)) / ((float) (end - start));
        }

        private boolean readUntilMatch(byte[] match, boolean withinBlock) throws IOException {
            int i = 0;
            while (true) {
                int b = fsin.read();
                // end of file:
                if (b == -1) return false;
                // save to buffer:
                if (withinBlock) buffer.write(b);

                // check if we're matching:
                if (b == match[i]) {
                    i++;
                    if (i >= match.length) return true;
                } else i = 0;
                // see if we've passed the stop point:
                if(!withinBlock && i == 0 && fsin.getPos() >= end) return false;
            }
        }
    
}
