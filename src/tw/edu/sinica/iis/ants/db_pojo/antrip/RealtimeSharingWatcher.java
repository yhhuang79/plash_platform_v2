package tw.edu.sinica.iis.ants.db_pojo.antrip;

import java.sql.Timestamp;

public class RealtimeSharingWatcher {
	private int id;
	private String token;
	private Timestamp timestamp;	
	private String socialid;	
	private String hashid;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public String getSocialid() {
		return socialid;
	}
	public void setSocialid(String socialid) {
		this.socialid = socialid;
	}
	public String getHashid() {
		return hashid;
	}
	public void setHashid(String hashid) {
		this.hashid = hashid;
	}
}
