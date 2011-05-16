package tw.edu.sinica.iis.ants.DB;

import java.sql.Timestamp;

import com.vividsolutions.jts.geom.Geometry;

public class T_ActivityUser {
	private Integer userid;
	private Integer activityid;
	
	public T_ActivityUser(){
		
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Integer getActivityid() {
		return activityid;
	}

	public void setActivityid(Integer activityid) {
		this.activityid = activityid;
	}
	
	
}
