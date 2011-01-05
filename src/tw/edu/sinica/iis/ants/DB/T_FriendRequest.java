package tw.edu.sinica.iis.ants.DB;

public class T_FriendRequest {
	private int fid;
	private Integer useraid;
	private Integer userbid;
	private String friendemail;
	private String passcode;
	private Boolean confirmed;
	
	public T_FriendRequest(){
		
	}

	public int getFid() {
		return fid;
	}

	public void setFid(int fid) {
		this.fid = fid;
	}

	public int getUseraid() {
		return useraid;
	}

	public void setUseraid(Integer useraid) {
		this.useraid = useraid;
	}

	public int getUserbid() {
		return userbid;
	}

	public void setUserbid(Integer userbid) {
		this.userbid = userbid;
	}

	public String getFriendemail() {
		return friendemail;
	}

	public void setFriendemail(String friendemail) {
		this.friendemail = friendemail;
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
	
	

}
