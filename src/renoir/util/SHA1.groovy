package renoir.util

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class SHA1 {
 
    static String convertToHex(byte[] data) {
     StringBuffer hexString = new StringBuffer();
     for (int i=0; i < data.length; i++) {
     	 String hex = Integer.toHexString(0xFF & data[i]);
    	 if (hex.length() == 1) {
       	     hexString.append("0" + hex);
    	     } else {
       	      hexString.append(hex);
    	      }
	 }
        return hexString.toString();
    }
 
    static String convert(String text) 
    throws NoSuchAlgorithmException, UnsupportedEncodingException  {
    MessageDigest md;
    md = MessageDigest.getInstance("SHA-1");
    byte[] sha1hash = new byte[40];
    md.update(text.getBytes(System.getProperty("file.encoding")), 0, text.length());
    sha1hash = md.digest();
    return convertToHex(sha1hash);
    }

	static main(args) {
		println SHA1.convert(args[0])
	}
}

