/* John Wittrock, Greg Herpel, 2012
 * This class implements the Burmester-Desmedt conference keying protocol. 
 * An explanation can be found at http://www.scribd.com/doc/2979523/7/Conference-keying
 */

import java.math.BigInteger;
import java.security.*;

public class ConferenceKey {

	/* From VeriSign's implementation */
	public static final BigInteger DEFAULT_MODULUS
	  = new BigInteger("1551728981814736974712322577637155399157248019669"
                         +"154044797077953140576293785419175806512274236981"
                         +"889937278161526466314385615958256881888899512721"
                         +"588426754199503412587065565498035801048705376814"
                         +"767265132557470407658574792912915723345106432450"
                         +"947150072296210941943497839259847603755949858482"
			   +"53359305585439638443");

	public static final BigInteger DEFAULT_GENERATOR = BigInteger.valueOf(2);

	private SecureRandom random;

	private BigInteger modulus;
	private BigInteger generator;
	private BigInteger privateKey;
	private BigInteger z;
	private BigInteger sKey;

	public ConferenceKey() {
		this(DEFAULT_MODULUS, DEFAULT_GENERATOR);
	}

	public ConferenceKey(BigInteger mod, BigInteger gen) {
		this.modulus = mod;
		this.generator = gen;

		try {
		random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		this.generatePrivateKey();
	}

	public BigInteger getPrivateKey() { return this.privateKey; }
	public BigInteger getZ() { return this.z; }
	public BigInteger getSharedKey() { return this.sKey; }
	

	public void generatePrivateKey() {
		int bitLength = modulus.bitLength();
		BigInteger upperBound = modulus.subtract(BigInteger.ONE);
		BigInteger privateKeyTemp;
		while(true) {
			privateKeyTemp = new BigInteger(bitLength, random);
			if(privateKeyTemp.compareTo(BigInteger.ONE) <= 0 || privateKeyTemp.compareTo(upperBound) >= 0) {
				continue;
			}
			privateKey = privateKeyTemp;
			break;
		}
		
		z = generator.modPow(this.privateKey, this.modulus);
	}

	public BigInteger generateX(BigInteger left, BigInteger right) {
		BigInteger x = right.multiply(left.modInverse(this.modulus));
		x = x.mod(this.modulus);
		x = x.modPow(this.privateKey, this.modulus);
		System.out.println("X generated: " + x);
		return x;
	}
	
	public BigInteger calculateSharedKey(BigInteger[] keys, int index, BigInteger leftZ) {
		int t = keys.length;
		BigInteger bigT = new BigInteger(Integer.toString(t));
		BigInteger sharedKey; 
		
		BigInteger firstExponent = bigT.multiply(this.privateKey);
		sharedKey = leftZ.modPow(firstExponent, this.modulus);

		BigInteger subtract = BigInteger.ONE;

		for(int i = 0; i < keys.length - 1; i++) {
			BigInteger base = keys[(i + index) % t];

			BigInteger xPow = base.modPow(bigT.subtract(subtract), this.modulus);

			subtract = subtract.add(BigInteger.ONE);
			
			sharedKey = sharedKey.multiply(xPow);

		} 
		
		sharedKey = sharedKey.mod(modulus);
		this.sKey = sharedKey;
		System.out.println("Calculated shared key: " + sharedKey);
		return sharedKey;
	}
	
	/* Main method for unit testing */
	public static void main(String[] args) {
		System.out.println(-3 % 10);
		

		ConferenceKey a = new ConferenceKey(BigInteger.valueOf(313), BigInteger.valueOf(2));
		ConferenceKey b = new ConferenceKey(BigInteger.valueOf(313), BigInteger.valueOf(2));
		ConferenceKey c = new ConferenceKey(BigInteger.valueOf(313), BigInteger.valueOf(2));		

		System.out.println("A priv key: " + a.getPrivateKey());
		System.out.println("B priv key: " + b.getPrivateKey());
		System.out.println("C priv key: " + c.getPrivateKey());
		System.out.println();

		System.out.println("A z: " + a.getZ());
		System.out.println("B z: " + b.getZ());
		System.out.println("C z: " + c.getZ());
		System.out.println();

		BigInteger a_x = a.generateX(c.getZ(), b.getZ());
		BigInteger b_x = b.generateX(a.getZ(), c.getZ());
		BigInteger c_x = c.generateX(b.getZ(), a.getZ());
		BigInteger [] xs = {a_x, b_x, c_x};
		
		System.out.println("A X: " + a_x);
		System.out.println("B X: " + b_x);
		System.out.println("C X: " + c_x);
		System.out.println();

		BigInteger a_sk = a.calculateSharedKey(xs, 0, c.getZ());
		BigInteger b_sk = b.calculateSharedKey(xs, 1, a.getZ());
		BigInteger c_sk = c.calculateSharedKey(xs, 2, b.getZ());

		System.out.println("A's shared key: " + a_sk);
		System.out.println("B's shared key: " + b_sk);
		System.out.println("C's shared key: " + c_sk);
	}

}