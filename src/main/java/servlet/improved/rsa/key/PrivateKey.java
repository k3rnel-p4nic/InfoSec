package servlet.improved.rsa.key;

public class PrivateKey extends Key {
	public int getD() {
		return x;
	}
	
	public PrivateKey(int d, int n) {
		super(d, n);
	}

	@Override
	public String toString() {
		return String.format("d = %d\nn = %d", getD(), getN());
	}
	
	public static PrivateKey loadPrivateKeys(String account) {
		Key k = loadKeys(PRIVATE_KEY_PATH + account);
		return new PrivateKey(k.x, k.n);
	}
	
	public void save(String account) {
		_save(PRIVATE_KEY_PATH + account, this.toString());
	}
}
