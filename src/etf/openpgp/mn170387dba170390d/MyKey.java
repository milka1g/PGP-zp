package etf.openpgp.mn170387dba170390d;

public class MyKey {
	//bilo da je kljuc public ili secret od master kljuca uzimamo keyID i userID koji je name<email>
	private String name;
	private String email;
	private String keyID; //Long.toHexString
	private long keyIdLong;
	private boolean isPublic;
	
	public MyKey(String name_, String email_, String keyID_, long keyidlong, boolean flag){
		name=name_;
		email=email_;
		keyID = keyID_;
		keyIdLong = keyidlong;
		isPublic = flag;
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
	

	public long getKeyIdLong() {
		return keyIdLong;
	}

	public void setKeyIdLong(long keyIdLong) {
		this.keyIdLong = keyIdLong;
	}
	
	

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	@Override
	public String toString() {
		return "MyKey [name=" + name + ", email=" + email + ", keyID=" + keyID + ", keyIdLong=" + keyIdLong
				+ ", isPublic=" + isPublic + "]";
	}

	

	
	
	

}
