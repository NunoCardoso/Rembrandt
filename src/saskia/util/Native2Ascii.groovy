
package saskia.util;

public class Native2Ascii {



	public static String nativeToAscii( String input ) {
		if (input == null) {
			return null;
		}
		StringBuffer buffer = new StringBuffer( input.length() + 60 );
		for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c <= 0x7E) { 
                buffer.append(c);
            }
            else {
            	buffer.append("\\u");
            	String hex = Integer.toHexString(c);
            	for (int j = hex.length(); j < 4; j++ ) {
            		buffer.append( '0' );
            	}
            	buffer.append( hex );
            }
        }
		return buffer.toString();
	}
	
	

	public static String asciiToNative( String input ) {
		if (input == null) {
			return null;
		}
		StringBuffer buffer = new StringBuffer( input.length() );
		boolean precedingBackslash = false;
		for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (precedingBackslash) {
            	switch (c) {
            	case 'f': c = '\f'; break;
            	case 'n': c = '\n'; break;
            	case 'r': c = '\r'; break;
            	case 't': c = '\t'; break;
            	case 'u':
            		String hex = input.substring( i + 1, i + 5 );
            		c = (char) Integer.parseInt(hex, 16 );
            		i += 4;
            	}
            	precedingBackslash = false;
            } else {
            	precedingBackslash = (c == '\\');
            }
            if (!precedingBackslash) {
                buffer.append(c);
            }
        }
		return buffer.toString();
	}
/*

         static String native2Ascii(String str) {
                StringBuffer sb = new StringBuffer(str.length());
                sb.setLength(0);
                for (int i = 0; i < str.length(); i++) {
                        char c = str.charAt(i);
                        sb.append(native2Ascii(c));
                }
                return (new String(sb));
        }

         static StringBuffer native2Ascii(char charater) {
                StringBuffer sb = new StringBuffer();
                if (charater > 255) {
                        sb.append("\\u");
                        int lowByte = (charater >>> 8);
                        sb.append(int2HexString(lowByte));
                        int highByte = (charater & 0xFF);
                        sb.append(int2HexString(highByte));
                } else {
                        sb.append(charater);
                }
                return sb;
        }

         static String int2HexString(int code) {
                String hexString = Integer.toHexString(code);
                if (hexString.length() == 1)
                        hexString = "0" + hexString;
                return hexString;
        }
*/
}