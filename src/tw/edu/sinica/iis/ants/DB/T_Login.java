package tw.edu.sinica.iis.ants.DB;

public class T_Login {
	private int sid;
	private String username;
	private String password;
	private String passcode;
	private Boolean confirmed;
	private String email;
	private String phonenum;
	private int facebookid;
	
	public T_Login(){
	
	}
	
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPasscode() {
		return passcode;
	}
	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}
	public boolean isConfirmed() {
		return confirmed;
	}
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhonenum() {
		return phonenum;
	}
	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}
	public int getFacebookid() {
		return facebookid;
	}
	public void setFacebookid(int facebookid) {
		this.facebookid = facebookid;
	}

}
