package servlet.improved.rsa;

import java.util.Random;
public class RSAKeysGenerator {
	private static boolean isPrime(int inputNum){
		if (inputNum <= 3 || inputNum % 2 == 0)
			return inputNum == 2 || inputNum == 3; //this returns false if number is <=1 & true if number = 2 or 3
		int divisor = 3;
		while ((divisor <= Math.sqrt(inputNum)) && (inputNum % divisor != 0))
			divisor += 2; //iterates through all possible divisors
		return inputNum % divisor != 0; //returns true/false
	}

	private static int primeGenerator() {
		Random rand = new Random(); // generate a random number
		int num = rand.nextInt(1000) + 1;

		while (!isPrime(num))
			num = rand.nextInt(1000) + 1;

		return num;
	}

	private static int gcd(int a, int b) {
		return (b == 0) ? a : gcd(b, a % b);
	}

	public RSAKeys generateKeys(){

		// generate two random prime numbers p and q. Tip: https://stackoverflow.com/questions/24006143/generating-a-random-prime-number-in-java
		//int p = primeGenerator(), q = primeGenerator();
		int p = primeGenerator(), q = primeGenerator();
		// calculate n = p*q
		int n = p * q;

		// calculate phi = (p-1)*(q-1)
		int phi = (p - 1) * (q - 1);

		// compute e: the minimum number that is coprime with phi greater than 1 and lower than phi
		int e = primeGenerator();
		while (gcd(e, phi) != 1)
			e = primeGenerator();

		// compute d with the Extended Euclidean algorithm
		// Extended Euclidean Algorithm  Tip: https://www.delftstack.com/howto/java/mod-of-negative-numbers-in-java/#get-the-mod-of-negative-numbers-by-using-the-operator-in-java
		int  p_container[]={0,1}, quotient=0, dividend= phi, divisor= e, remainder=1, pi=0;

		while (remainder > 0) {
			quotient = dividend / divisor;
			remainder = dividend % divisor;
			dividend = divisor;
			divisor = remainder;

			if (remainder != 0) {
				int x = p_container[0] - p_container[1] * quotient;
				int p2 = (x < 0) ? (phi - (Math.abs(x) % phi) ) % phi : (x % phi);
				p_container[0] = p_container[1];
				p_container[1] = p2;
			}
		}
		int d = p_container[1];

		return new RSAKeys(e, d, n);
	}

	public static void main(String[] args) {
		RSAKeysGenerator r = new RSAKeysGenerator();
		System.out.println(r.generateKeys());
	}
}