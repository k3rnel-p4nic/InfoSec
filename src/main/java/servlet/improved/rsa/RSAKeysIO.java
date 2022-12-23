package servlet.improved.rsa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public abstract class RSAKeysIO {
	protected static final String SERVER_RSA_PATH = "/opt/infosecproj/";	// public
	protected static final String CLIENT_RSA_PATH = "/usr/local/opt/infosecproj/";	// private
	public static final String PUBLIC_KEY_PATH = SERVER_RSA_PATH + "pubkeys/";
	public static final String PRIVATE_KEY_PATH = CLIENT_RSA_PATH + "prikeys/";
	
	protected void _save(String user, String key) {
		try {
			BufferedWriter buf = new BufferedWriter(new FileWriter(user));
			buf.write(key);
			buf.flush();
			buf.close();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	protected static int[] load(String file) {
		try {
			Scanner reader = new Scanner(new File(file));
			int[] r = new int[2];
			while (reader.hasNextLine()) {
				String[] split = reader.nextLine().split("=");
				int val = Integer.parseInt(split[1].strip());
				switch (split[0].strip().charAt(0)) {
				case 'e':
				case 'd':
					r[0] = val;
					break;
				case 'n':
					r[1] = val;
					break;
				default:
					break;
				}
			}
			return r;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	
	@Deprecated
	public static final String RSA_FILEPATH = "/opt/infosecproj/rsa_";
	
	@Deprecated
	public void save(String email) {
		_save(email, toString());
	}
	
	@Deprecated
	public static RSAKeys loadFromFile(String file) {
		try {
			Scanner reader = new Scanner(new File(file));
			int e = 0, d = e, n = d;
			while (reader.hasNextLine()) {
				String[] split = reader.nextLine().split("=");
				int val = Integer.parseInt(split[1].strip());
				switch (split[0].strip().charAt(0)) {
				case 'e':
					e = val;
					break;
				case 'd':
					d = val;
					break;
				case 'n':
					n = val;
					break;
				default:
					break;
				}
			}
			return new RSAKeys(e, d, n);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
