package tw.edu.sinica.iis.ants.db_pojo.antrip;

import java.sql.Timestamp;

import com.vividsolutions.jts.geom.Geometry;


public class RealtimeSharingSessions {
	private int id;
	private String uuid;
	private String token;
	private String sharing_method;
	private Integer duration_type;
	private Integer duration_value;	
	private Timestamp timestamp;	
	private String friend_id;
	private Integer status;
	private String url;
	private String hashid;
	private String username;
	
	public RealtimeSharingSessions(){
	
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getDuration_type() {
		return duration_type;
	}

	public void setDuration_type(Integer duration_type) {
		this.duration_type = duration_type;
	}

	public Integer getDuration_value() {
		return duration_value;
	}

	public void setDuration_value(Integer duration_value) {
		this.duration_value = duration_value;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getFriend_id() {
		return friend_id;
	}

	public void setFriend_id(String friend_id) {
		this.friend_id = friend_id;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSharing_method() {
		return sharing_method;
	}

	public void setSharing_method(String sharing_method) {
		this.sharing_method = sharing_method;
	}

	public String getHashid() {
		return hashid;
	}

	public void setHashid(String hashid) {
		this.hashid = hashid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
