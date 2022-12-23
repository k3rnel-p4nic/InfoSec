package servlet.improved.rsa.key;

import servlet.improved.rsa.RSAKeysIO;

public class Key extends RSAKeysIO {
	protected int x;
	protected int n;
	
	public int getN() {
		return n;
	}
	
	public Key(int x, int n) {
		this.x = x;
		this.n = n;
	}
	
	public static Key loadKeys(String path) {
		int[] keys = load(path);
		return new Key(keys[0], keys[1]);
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + n + ")";
	}
}
