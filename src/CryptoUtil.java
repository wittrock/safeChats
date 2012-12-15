import java.io.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.math.BigInteger;

public class CryptoUtil {
	
	public static byte[] getSHA512(BigInteger key) {
		MessageDigest sha512 = null;
		try {
			sha512 = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Cannot construct SHA-512 algorithm.");
			return null;
		}
		
		byte[] hash = sha512.digest(key.toByteArray());
		return hash;
	}

	/* This will use AES 256 by default */
	public static EncryptedMessage encrypt(byte[] key, byte[] message) {
		SecretKey k = new SecretKeySpec(key, "AES");
		Cipher enc = null;
		byte[] iv = null;
		byte[] encrypted = null;
		try {
			enc = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			enc.init(Cipher.ENCRYPT_MODE, k);
			iv = enc.getIV();
			encrypted = enc.doFinal(message);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No Such algorithm exception. Returning.");
			e.printStackTrace();
			return null;
		} catch (IllegalBlockSizeException e) {
			System.out.println("Illegal block size exception while encrypting.");
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			System.out.println("Invalid encryption key.");
			e.printStackTrace();
			return null;
		} catch (BadPaddingException e) {
			System.out.println("Bad padding while encrypting message.");
			e.printStackTrace();
			return null;
		} catch (NoSuchPaddingException e) {
			System.out.println("No such padding while encrypting message.");
			e.printStackTrace();
			return null;
		}

		EncryptedMessage msg = new EncryptedMessage(iv, encrypted);
		return msg;
	}

	public static String decrypt(byte[] key, byte[] iv, byte[] message) {
		byte[] plainBytes = null; 

		try {
			Cipher dec = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			SecretKey k = new SecretKeySpec(key, "AES");
			dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));					plainBytes = dec.doFinal(message);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No Such algorithm exception. Returning.");
			e.printStackTrace();
			return null;
		} catch (IllegalBlockSizeException e) {
			System.out.println("Illegal block size exception while encrypting.");
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			System.out.println("Invalid key.");
			e.printStackTrace();
			return "Invalid decryption key. Try re-encrypting the chat session.";
		} catch (BadPaddingException e) {
			System.out.println("Bad padding while decrypting message.");
			e.printStackTrace();
			return null;
		} catch (NoSuchPaddingException e) {
			System.out.println("No such padding while decrypting message.");
			e.printStackTrace();
			return null;
		} catch (InvalidAlgorithmParameterException e) {
			System.out.println("No such algorithm parameter in decryption");
			e.printStackTrace();
			return null;
		}

		String plaintext = new String(plainBytes);
		return plaintext;
	}

	/* HMAC SHA 256 by default */
	public static byte[] getMAC(byte[] key, byte[] message) {
		return null;
	}
	
	public static byte[] verifyMac(byte[] key, byte[] message) {
		return null;
	}


	
}