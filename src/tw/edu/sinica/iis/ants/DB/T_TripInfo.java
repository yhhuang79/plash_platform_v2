package tw.edu.sinica.iis.ants.DB;

import java.sql.*;


public class T_TripInfo {
	private Integer userid;
	private Integer trip_id;
	private String trip_name;
	private Timestamp trip_st;
	private Timestamp trip_et;	
	private Integer trip_length;
	private Integer num_of_pts;
	private String st_addr_prt1;
	private String st_addr_prt2;
	private String st_addr_prt3;
	private String st_addr_prt4;
	private String st_addr_prt5;	
	private String et_addr_prt1;	
	private String et_addr_prt2;
	private String et_addr_prt3;
	private String et_addr_prt4;
	private String et_addr_prt5;	
	private Short update_status;
	private Boolean is_completed;
	public Boolean getIs_completed() {
		return is_completed;
	}
	public void setIs_completed(Boolean is_completed) {
		this.is_completed = is_completed;
	}
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
	public String getSt_addr_prt1() {
		return st_addr_prt1;
	}
	public void setSt_addr_prt1(String st_addr_prt1) {
		this.st_addr_prt1 = st_addr_prt1;
	}
	public String getSt_addr_prt2() {
		return st_addr_prt2;
	}
	public void setSt_addr_prt2(String st_addr_prt2) {
		this.st_addr_prt2 = st_addr_prt2;
	}
	public String getSt_addr_prt3() {
		return st_addr_prt3;
	}
	public void setSt_addr_prt3(String st_addr_prt3) {
		this.st_addr_prt3 = st_addr_prt3;
	}
	public String getSt_addr_prt4() {
		return st_addr_prt4;
	}
	public void setSt_addr_prt4(String st_addr_prt4) {
		this.st_addr_prt4 = st_addr_prt4;
	}
	public String getSt_addr_prt5() {
		return st_addr_prt5;
	}
	public void setSt_addr_prt5(String st_addr_prt5) {
		this.st_addr_prt5 = st_addr_prt5;
	}
	public String getEt_addr_prt1() {
		return et_addr_prt1;
	}
	public void setEt_addr_prt1(String et_addr_prt1) {
		this.et_addr_prt1 = et_addr_prt1;
	}
	public String getEt_addr_prt2() {
		return et_addr_prt2;
	}
	public void setEt_addr_prt2(String et_addr_prt2) {
		this.et_addr_prt2 = et_addr_prt2;
	}
	public String getEt_addr_prt3() {
		return et_addr_prt3;
	}
	public void setEt_addr_prt3(String et_addr_prt3) {
		this.et_addr_prt3 = et_addr_prt3;
	}
	public String getEt_addr_prt4() {
		return et_addr_prt4;
	}
	public void setEt_addr_prt4(String et_addr_prt4) {
		this.et_addr_prt4 = et_addr_prt4;
	}
	public String getEt_addr_prt5() {
		return et_addr_prt5;
	}
	public void setEt_addr_prt5(String et_addr_prt5) {
		this.et_addr_prt5 = et_addr_prt5;
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
	public Integer getNum_of_pts() {
		return num_of_pts;
	}
	public void setNum_of_pts(Integer num_of_pts) {
		this.num_of_pts = num_of_pts;
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
