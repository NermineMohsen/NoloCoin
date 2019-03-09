import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Global {
    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
    public static String BuffToString(ByteBuffer buffer) {
        String a = "";
        try {
            a = new String(buffer.array(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return a;
    }
    public static String KeyToString(Key privateKey){
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static ByteBuffer StringToBuff(String k){
        byte[] bytes = k.getBytes(StandardCharsets.UTF_8);
        ByteBuffer b = ByteBuffer.wrap(bytes);
        return b;
    }
    public static byte[] StringToBytes(String k){
        byte[] bytes = k.getBytes(StandardCharsets.UTF_8);
        return bytes;
    }
    public static String BytesToString(byte[] bytes){
        return new String(bytes, StandardCharsets.UTF_8);
    }
    public static PublicKey StringToPublickey(String publicK){
        PublicKey public_key = null;
        System.out.println("converting : ("+publicK+")");
        try {
            byte[] bytes  = Base64.getDecoder().decode(publicK);
            KeyFactory factory = null;
            factory = KeyFactory.getInstance("ECDSA", "BC");
            public_key = (ECPublicKey) factory.generatePublic(new X509EncodedKeySpec(bytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return public_key;

    }
}
