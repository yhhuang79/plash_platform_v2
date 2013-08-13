package tw.edu.sinica.iis.ants.db_pojo.antrip;

import java.sql.Timestamp;

import com.vividsolutions.jts.geom.Geometry;


public class RealtimeSharingPoints {
	private int id;
	private Integer userid;
	private String token;
	private Geometry gps;
	private Timestamp timestamp;	
	private Double altitude;
	private Double accuracy;
	private Double speed;
	private Double bearing;
	private String location_source;	
	private String hashid;
	
	public String getHashid() {
		return hashid;
	}

	public void setHashid(String hashid) {
		this.hashid = hashid;
	}

	private Double latitude;
	private Double longitude;
	
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

	public RealtimeSharingPoints(){
	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Geometry getGps() {
		return gps;
	}

	public void setGps(Geometry gps) {
		this.gps = gps;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public Double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Double getBearing() {
		return bearing;
	}

	public void setBearing(Double bearing) {
		this.bearing = bearing;
	}

	public String getLocation_source() {
		return location_source;
	}

	public void setLocation_source(String location_source) {
		this.location_source = location_source;
	}
	
}
