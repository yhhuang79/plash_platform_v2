package tw.edu.sinica.iis.ants.DB;

import java.sql.Timestamp;

import com.vividsolutions.jts.geom.Geometry;


public class T_UserPointLocationTime {
	private int id;
	private int userid;
	private Timestamp timestamp;
	private Geometry gps;
	private Timestamp server_timestamp;
	private int trip_id;
	private int label;
	
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

	public void setUserid(int userid) {
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

	public void setTrip_id(int tripId) {
		trip_id = tripId;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}
	
}
