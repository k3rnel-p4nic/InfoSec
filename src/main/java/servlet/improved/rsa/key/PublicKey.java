package servlet.improved.rsa.key;

public class PublicKey extends Key {
	public int getE() {
		return x;
	}
	
	public PublicKey(int e, int n) {
		super(e, n);
	}

	@Override
	public String toString() {
		return String.format("e = %d\nn = %d", getE(), getN());
	}
	
	public static PublicKey loadPublicKeys(String account) {
		Key k = loadKeys(PUBLIC_KEY_PATH + account);
		return new PublicKey(k.x, k.n);
	}
	
	public void save(String account) {
		_save(PUBLIC_KEY_PATH + account, this.toString());
	}
}
