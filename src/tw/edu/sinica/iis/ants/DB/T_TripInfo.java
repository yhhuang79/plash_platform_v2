package tw.edu.sinica.iis.ants.DB;

import java.sql.*;


public class T_TripInfo {
	private Integer userid;
	private Integer trip_id;
	private String trip_name;
	private String trip_address;
	private Timestamp trip_st;
	private Timestamp trip_et;	
	private Integer trip_length;
	private Blob trip_preview;
	private Short update_status;
	private Integer id;
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public Integer getTrip_id() {
		return trip_id;
	}
	public void setTrip_id(Integer trip_id) {
		this.trip_id = trip_id;
	}
	public String getTrip_name() {
		return trip_name;
	}
	public void setTrip_name(String trip_name) {
		this.trip_name = trip_name;
	}
	public String getTrip_address() {
		return trip_address;
	}
	public void setTrip_address(String trip_address) {
		this.trip_address = trip_address;
	}
	public Timestamp getTrip_st() {
		return trip_st;
	}
	public void setTrip_st(Timestamp trip_st) {
		this.trip_st = trip_st;
	}
	public Timestamp getTrip_et() {
		return trip_et;
	}
	public void setTrip_et(Timestamp trip_et) {
		this.trip_et = trip_et;
	}
	public Integer getTrip_length() {
		return trip_length;
	}
	public void setTrip_length(Integer trip_length) {
		this.trip_length = trip_length;
	}
	public Blob getTrip_preview() {
		return trip_preview;
	}
	public void setTrip_preview(Blob trip_preview) {
		this.trip_preview = trip_preview;
	}
	public Short getUpdate_status() {
		return update_status;
	}
	public void setUpdate_status(Short update_status) {
		this.update_status = update_status;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}


	
}//end class
