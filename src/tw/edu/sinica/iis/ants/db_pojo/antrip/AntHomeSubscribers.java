package tw.edu.sinica.iis.ants.db_pojo.antrip;

public class AntHomeSubscribers {
	private int id;
	private String uuid;
	private String fbid;
	private String fb_token;
	private String gmail;
	private String gmail_token;
	
	public AntHomeSubscribers(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFbid() {
		return fbid;
	}

	public void setFbid(String fbid) {
		this.fbid = fbid;
	}

	public String getFb_token() {
		return fb_token;
	}

	public void setFb_token(String fb_token) {
		this.fb_token = fb_token;
	}

	public String getGmail() {
		return gmail;
	}

	public void setGmail(String gmail) {
		this.gmail = gmail;
	}

	public String getGmail_token() {
		return gmail_token;
	}

	public void setGmail_token(String gmail_token) {
		this.gmail_token = gmail_token;
	}

	
}
