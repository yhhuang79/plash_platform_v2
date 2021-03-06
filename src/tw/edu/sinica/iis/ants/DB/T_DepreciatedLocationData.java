package tw.edu.sinica.iis.ants.DB;

import java.sql.Timestamp;

import com.vividsolutions.jts.geom.Geometry;


public class T_DepreciatedLocationData {
	private int id;
	private Integer userid;
	private Timestamp timestamp;
	private Geometry gps;
	private Timestamp server_timestamp;
	private Integer trip_id;
	private Integer label;
	
	public T_DepreciatedLocationData(){
	
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
	
}
