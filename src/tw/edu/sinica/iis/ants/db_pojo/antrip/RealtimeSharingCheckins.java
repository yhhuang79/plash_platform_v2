package tw.edu.sinica.iis.ants.db_pojo.antrip;

import java.sql.Timestamp;

import com.vividsolutions.jts.geom.Geometry;


public class RealtimeSharingCheckins {
	private String token;
	private String url;
	private Double latitude;
	private Double longitude;	
	private Timestamp timestamp;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}	

}
