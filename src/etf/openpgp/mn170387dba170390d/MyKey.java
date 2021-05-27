package etf.openpgp.mn170387dba170390d;

public class MyKey {
	//bilo da je kljuc public ili secret od master kljuca uzimamo keyID i userID koji je name<email>
	private String name;
	private String email;
	private String keyID; //Long.toHexString
	
	public MyKey(String name_, String email_, String keyID_){
		name=name_;
		email=email_;
		keyID = keyID_;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getKeyID() {
		return keyID;
	}

	public void setKeyID(String keyID) {
		this.keyID = keyID;
	}

	@Override
	public String toString() {
		return "MyKey [name=" + name + ", email=" + email + ", keyID=" + keyID + "]";
	}
	
	

}
