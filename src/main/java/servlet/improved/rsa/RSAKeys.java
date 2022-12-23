package servlet.improved.rsa;

import servlet.improved.rsa.key.Key;
import servlet.improved.rsa.key.PrivateKey;
import servlet.improved.rsa.key.PublicKey;

public class RSAKeys extends RSAKeysIO {
		private int e;	/* Public */
		private int d;	/* Private */
		private int n;  

		public RSAKeys(int e, int d, int n) {
			this.e = e;
			this.d = d;
			this.n = n;
		}

		public int getE() {
			return e;
		}

		public int getD() {
			return d;
		}

		public int getN() {
			return n;
		}

		
		public void savePublicKeys(String account) {
			new PublicKey(e, n).save(account);
		}
		
		public void savePrivateKeys(String account) {
			new PrivateKey(d, n).save(account);
		}
		
		
		@Override
		public String toString() {
			return String.format("e = %d\nd = %d\nn = %d\n", e, d, n);
		}
}
