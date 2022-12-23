package servlet.improved.rsa;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import servlet.improved.rsa.key.PrivateKey;
import servlet.improved.rsa.key.PublicKey;

public class RSA extends RSAKeysGenerator {
	
	protected static final Charset CHARSET = StandardCharsets.UTF_8;

	protected static int[] encrypt(byte[] plaintext, int e, int n) {
		int[] encrypted = new int[plaintext.length];
		final BigInteger E = new BigInteger(String.valueOf(e)), N = new BigInteger(String.valueOf(n));
		
		for (int i = 0; i < plaintext.length; i++) {
			BigInteger enc = new BigInteger(String.valueOf(plaintext[i])).modPow(E, N);
			encrypted[i] = enc.intValue();
		}
		
		return encrypted;
	}

	public int[] encrypt(String plaintext, PublicKey keys) {
		return encrypt(plaintext.getBytes(), keys.getE(), keys.getN());
	}

	public String decrypt(int[] ciphertext, PrivateKey keys) {
		return new String(decrypt(ciphertext, keys.getD(), keys.getN()), CHARSET);
	}

	protected byte[] decrypt(int[] ciphertext, int d, int n) {
		final BigInteger N = new BigInteger(String.valueOf(n));
		
		System.out.println("[ DEBUG ] " + "\t" + n + "\n\t" + N);
		
		byte[] decryptedBytes = new byte[ciphertext.length];
		int index = 0;
		for (int i : ciphertext) {
			BigInteger value = new BigInteger(String.valueOf(i)).pow(d).mod(N);			
			decryptedBytes[index++] = value.byteValue();
		}

		return decryptedBytes;
	}
}
