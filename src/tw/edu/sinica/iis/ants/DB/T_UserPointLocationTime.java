	package tw.edu.sinica.iis.ants.DB;

import java.sql.Timestamp;

import com.vividsolutions.jts.geom.Geometry;


public class T_UserPointLocationTime {
	private int id;
	private Integer userid;
	private Timestamp timestamp;
	private Geometry gps;
	private Timestamp server_timestamp;
	private Integer trip_id;
	private Integer label;
	private Double alt;
	private Double accu;
	private Double spd;
	private Double bear;
	private Double accex;
	private Double accey;
	private Double accez;
	private String gsminfo;
	private String wifiinfo;
	private Integer app;
	
	public Integer getApp() {
		return app;
	}

	public void setApp(Integer app) {
		this.app = app;
	}

	public T_UserPointLocationTime(){
	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserid() {
		return userid;
	}

	public Double getAlt() {
		return alt;
	}

	public void setAlt(Double alt) {
		this.alt = alt;
	}

	public Double getAccu() {
		return accu;
	}

	public void setAccu(Double accu) {
		this.accu = accu;
	}

	public Double getSpd() {
		return spd;
	}

	public void setSpd(Double spd) {
		this.spd = spd;
	}

	public Double getBear() {
		return bear;
	}

	public void setBear(Double bear) {
		this.bear = bear;
	}

	public Double getAccex() {
		return accex;
	}

	public void setAccex(Double accex) {
		this.accex = accex;
	}

	public Double getAccey() {
		return accey;
	}

	public void setAccey(Double accey) {
		this.accey = accey;
	}

	public Double getAccez() {
		return accez;
	}

	public void setAccez(Double accez) {
		this.accez = accez;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Geometry getGps() {
		return gps;
	}

	public void setGps(Geometry gps) {
		this.gps = gps;
	}

	public Timestamp getServer_timestamp() {
		return server_timestamp;
	}

	public void setServer_timestamp(Timestamp serverTimestamp) {
		server_timestamp = serverTimestamp;
	}

	public int getTrip_id() {
		return trip_id;
	}

	public void setTrip_id(Integer tripId) {
		trip_id = tripId;
	}

	public int getLabel() {
		if (label == null){
			label = -1;
		}
		return label;
	}

	public void setLabel(Integer label) {
		this.label = label;
	}

	public String getGsminfo() {
		return gsminfo;
	}

	public void setGsminfo(String gsminfo) {
		this.gsminfo = gsminfo;
	}

	public String getWifiinfo() {
		return wifiinfo;
	}

	public void setWifiinfo(String wifiinfo) {
		this.wifiinfo = wifiinfo;
	}
	
	
}
