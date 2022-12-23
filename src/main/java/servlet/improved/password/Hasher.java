package servlet.improved.password;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Hasher {

    public static String getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return new String(salt, StandardCharsets.UTF_8);
    }
    
    private static String SHA_256_encryption(String s, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(s.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
    
    
    public static boolean validateWithHashedString(String plaintext, String hashed, String salt) {
    	return hashed.equals(SHA_256_encryption(plaintext, salt));
    }
    
    public static String hashString(String s, String salt) {
    	return SHA_256_encryption(s, salt);
    }
//    public static String hashString(String s) {
//    	try {
//			return SHA_256_encryption(s, getSalt());
//		} catch (NoSuchAlgorithmException e) {
//			throw new RuntimeException(e.getMessage());
//		}
//    }
}
