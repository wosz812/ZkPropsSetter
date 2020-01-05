/**
 * Created by bogus on 2019-03-26.
 **/
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Hex;

public class RwkEncUtil {
    private String base64str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private String defaultKey = "This is default KEY!!";

    public String Encrypt(String plainText, String key) {
        String base64text = null;
        String hashkey = null;
        Map<Character, Integer> base64map = new HashMap<Character, Integer>();
        String encText = "";

        try {
            base64text = DatatypeConverter.printBase64Binary(plainText.getBytes("UTF-8"));
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            if (key.equals("")) {
                byte[] encodedkeyhash = digest.digest(defaultKey.getBytes(StandardCharsets.UTF_8));
                hashkey = new String(Hex.encodeHex(encodedkeyhash));
            } else {
                byte[] encodedkeyhash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
                hashkey = new String(Hex.encodeHex(encodedkeyhash));
            }
        } catch (Exception e) {
            return "";
        }

        for (int i = 0; i< base64str.length(); i++) {
            base64map.put(base64str.charAt(i), i);
        }

        for (int i = 0; i < base64text.length(); i++) {
            int keyIdex = i % hashkey.length();
            char origChar = base64text.charAt(i);
            char keyChar = hashkey.charAt(keyIdex);

            if (base64map.get(origChar) != null) {
                encText += base64str.charAt((base64map.get(origChar) + ((int) keyChar)) % base64str.length());
            } else {
                encText += origChar;
            }
        }

        return encText;
    }

    public String Decrypt(String encryptedText, String key) {
        String hashkey = null;
        Map<Character, Integer> base64map = new HashMap<Character, Integer>();
        String plainText = "";

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            if (key.equals("")) {
                byte[] encodedkeyhash = digest.digest(defaultKey.getBytes(StandardCharsets.UTF_8));
                hashkey = new String(Hex.encodeHex(encodedkeyhash));
            } else {
                byte[] encodedkeyhash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
                hashkey = new String(Hex.encodeHex(encodedkeyhash));
            }
        } catch (Exception e) {
            return "";
        }

        for (int i = 0; i< base64str.length(); i++) {
            base64map.put(base64str.charAt(i), i);
        }

        for (int i = 0; i < encryptedText.length(); i++) {
            int keyIdex = i % hashkey.length();
            char origChar = encryptedText.charAt(i);
            char keyChar = hashkey.charAt(keyIdex);

            if (base64map.get(origChar) != null) {
                int decIdx = (base64map.get(origChar) - ((int) keyChar)) % base64str.length();

                while (decIdx < 0)
                    decIdx += base64str.length();
                plainText += base64str.charAt(decIdx);
            } else {
                plainText += origChar;
            }
        }

        return new String(Base64.getDecoder().decode(plainText));
    }
}
