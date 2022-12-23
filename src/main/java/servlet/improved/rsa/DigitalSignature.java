package servlet.improved.rsa;

import java.math.BigInteger;

import servlet.improved.rsa.key.PrivateKey;
import servlet.improved.rsa.key.PublicKey;

public class DigitalSignature extends RSA {

	public int[] sign(int[] plaintext, PrivateKey keys) {
		int[] signedText = new int[plaintext.length];
		final BigInteger E = new BigInteger(String.valueOf(keys.getD())), N = new BigInteger(String.valueOf(keys.getN()));
		
		for (int i = 0; i < plaintext.length; i++) {
			BigInteger enc = new BigInteger(String.valueOf(plaintext[i])).modPow(E, N);
			signedText[i] = enc.intValue();
		}
		
		return signedText;
	}


	public int[] readSigned(int[] ciphertext, PublicKey keys) {
		final BigInteger N = new BigInteger(String.valueOf(keys.getN()));		
		int[] decryptedBytes = new int[ciphertext.length];
		
		int index = 0;
		for (int i : ciphertext) {
			BigInteger value = new BigInteger(String.valueOf(i)).pow(keys.getE()).mod(N);			
			decryptedBytes[index++] = value.intValue();
		}

		return decryptedBytes;
	}
}
