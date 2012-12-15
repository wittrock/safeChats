/* 
 * This is a ludicrously simple class to hold a message encrypted with
 * AES, along with its IV and MAC. This is really so we can return a
 * single object from functions that would otherwise have to return a
 * message concatenated with an IV and a length, and then do the same
 * thing when we want to add a MAC.
 */

public class EncryptedMessage {
	private byte[] iv;
	private byte[] message;
	private byte[] mac;
	
	
	public EncryptedMessage(byte[] iv, byte[] message) {
		this.iv = iv;
		this.message = message;
	}

	public void setMAC(byte[] mac) {
		this.mac = mac;
	}


	public int getIVLength() { return this.iv.length; }
	public int getMACLength() { return this.mac.length; }


	// Helper function. Concatenates two byte arrays.
	private byte[] arrayConcat(byte[] one, byte[] two) {
		byte[] concat = new byte[one.length + two.length];

		for (int i = 0; i < one.length; i++) {
			concat[i] = one[i];
		}

		for (int j = 0; j < two.length; j++) {
			concat[j + one.length] = two[j];
		}

		return concat;
	}

	public byte[] getIVAndMessage() {
		return arrayConcat(this.iv, this.message);
	}

	public byte[] getIVMessageAndMac() {
		return arrayConcat(this.mac, this.getIVAndMessage());
	}

}