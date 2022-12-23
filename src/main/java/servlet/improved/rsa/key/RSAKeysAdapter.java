package servlet.improved.rsa.key;

import servlet.improved.rsa.RSAKeys;

public class RSAKeysAdapter extends RSAKeys {
	private RSAKeysAdapter(int e, int d, int n) {
		super(e, d, n);
	}
	
	public RSAKeysAdapter(Key k) {
		super(k instanceof PrivateKey ? -1 : k.x, k instanceof PrivateKey ? k.x : -1, k.n);
	}
}
