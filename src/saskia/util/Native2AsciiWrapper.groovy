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

package saskia.util

/**
 * @author Nuno Cardoso
 * Invokes external Perl script for term tokenization.
 * The script uses Lingua::PT::PLN, a portuguese tokenizer but it also 
 * works pretty well with English. It splits terms ans sentences, in a 
 * XML format.
 */
class Native2AsciiWrapper {

	private static Process process = null, reverseProcess= null
	private static BufferedReader wrapperOutputStream = null, reverseWrapperOutputStream = null
	private static PrintStream wrapperInputStream = null, reverseWrapperInputStream = null
	private static BufferedReader wrapperErrorStream = null, reverseWrapperErrorStream = null

	private static Native2AsciiWrapper _this
	
    /** 
     * Initializes wrapper for tokenizer streams
     */
	private Native2AsciiWrapper() throws IOException {
			String[] wrapperCommand = new String[1]
			String[] reverseWrapperCommand = new String[2]
			wrapperCommand[0] = "native2ascii"
			reverseWrapperCommand[0] = "native2ascii"
			reverseWrapperCommand[1] = "-reverse"
			String[] envArray = new String[0]
			process = Runtime.getRuntime().exec(wrapperCommand, envArray)
			reverseProcess = Runtime.getRuntime().exec(reverseWrapperCommand, envArray)
			wrapperOutputStream = new BufferedReader(new InputStreamReader(process.getInputStream()))
			wrapperInputStream = new PrintStream(new BufferedOutputStream(process.getOutputStream()),true)
			wrapperErrorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()))
			reverseWrapperOutputStream = new BufferedReader(new InputStreamReader(reverseProcess.getInputStream()))
			reverseWrapperInputStream = new PrintStream(new BufferedOutputStream(reverseProcess.getOutputStream()),true)
			reverseWrapperErrorStream = new BufferedReader(new InputStreamReader(reverseProcess.getErrorStream()))
	}
	
	/**
	 * Returns new instance of the tokenizer singleton.
	 */
	public static Native2AsciiWrapper newInstance() {
	    if (_this == null) _this = new Native2AsciiWrapper()
	    return _this
	}

	/**
	 * closes wrapper streams.
	 */
	public static void cleanUp() {
		try {
			wrapperOutputStream.close()
			wrapperInputStream.close()
			wrapperErrorStream.close()
			reverseWrapperOutputStream.close()
			reverseWrapperInputStream.close()
			reverseWrapperErrorStream.close()
			process = null
			reverseProcess = null
		} catch (Exception e) {e.printStackTrace();}
	}
	
	

	public String convert(String text) {
		return n2a(text)
	}
	
	public String revert(String text) {
		return a2n(text)
	}
	
	public String process(String text) {
		return n2a(text)
	}

	public String unprocess(String text) {
		return a2n(text)
	}
	/**
	 * Tokenize a text.
	 * @param text to be tokenized
	 * @return tokenized text.
	 */
	public String n2a(String text) {

		wrapperInputStream.println(text+"\n");
		wrapperInputStream.flush();

		String line = ""
		String res = ""
		try {
			while (line = wrapperOutputStream.readLine() ) {
				res += line
			}
		}catch(Exception e) {e.printStackTrace();}
		return res;
	}
	
		/**
	 * Tokenize a text.
	 * @param text to be tokenized
	 * @return tokenized text.
	 */
	public String a2n(String text) {

		reverseWrapperInputStream.println(text+"\n");
		reverseWrapperInputStream.flush();

		String line = ""
		String res = ""
		try {
			while (line = reverseWrapperOutputStream.readLine() ) {
				res += line
			}
		}catch(Exception e) {e.printStackTrace();}
		return res;
	}

	/**
	 * for debug purposes.
	 * @param args the language in "xx" format.
	 * @throws Exception the IOException.
	 */
	public static void main(String[] args) throws Exception {
		
		Native2AsciiWrapper wrapper = new Native2AsciiWrapper()
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
		
		String text = ""
		String line = null
		println "Enter text to CONVERT: "
		while ((line = input.readLine()) != null) {
			if (line.startsWith(".")) break;
			text += line+"\n";
		}
		def text2 = wrapper.process(text);	
		println "Convert results:\n"+text2
		def text3 = wrapper.unprocess(text);	
		println "Revert results:\n"+text3
		wrapper.cleanUp();
	}

}