package tw.edu.sinica.iis.ants.db.common;

// Generated Jul 18, 2012 7:38:37 PM by Hibernate Tools 3.4.0.CR1

import java.sql.Timestamp;

/**
 * TrajectoryInfo generated by hbm2java
 */
public class TrajectoryInfo implements java.io.Serializable {

	private int trajectoryId;
	private Timestamp startTime;
	private Timestamp endTime;
	private Integer tripLength;
	private Integer numOfPts;
	private String stAddrPrt1;
	private String stAddrPrt2;
	private String stAddrPrt3;
	private String stAddrPrt4;
	private String stAddrPrt5;
	private String etAddrPrt1;
	private String etAddrPrt2;
	private String etAddrPrt3;
	private String etAddrPrt4;
	private String etAddrPrt5;
	private short updateStatus;

	public TrajectoryInfo() {
	}

	public TrajectoryInfo(int trajectoryId, short updateStatus) {
		this.trajectoryId = trajectoryId;
		this.updateStatus = updateStatus;
	}

	public TrajectoryInfo(int trajectoryId, Timestamp startTime,
			Timestamp endTime, Integer tripLength, Integer numOfPts,
			String stAddrPrt1, String stAddrPrt2, String stAddrPrt3,
			String stAddrPrt4, String stAddrPrt5, String etAddrPrt1,
			String etAddrPrt2, String etAddrPrt3, String etAddrPrt4,
			String etAddrPrt5, short updateStatus) {
		this.trajectoryId = trajectoryId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.tripLength = tripLength;
		this.numOfPts = numOfPts;
		this.stAddrPrt1 = stAddrPrt1;
		this.stAddrPrt2 = stAddrPrt2;
		this.stAddrPrt3 = stAddrPrt3;
		this.stAddrPrt4 = stAddrPrt4;
		this.stAddrPrt5 = stAddrPrt5;
		this.etAddrPrt1 = etAddrPrt1;
		this.etAddrPrt2 = etAddrPrt2;
		this.etAddrPrt3 = etAddrPrt3;
		this.etAddrPrt4 = etAddrPrt4;
		this.etAddrPrt5 = etAddrPrt5;
		this.updateStatus = updateStatus;
	}

	public int getTrajectoryId() {
		return this.trajectoryId;
	}

	public void setTrajectoryId(int trajectoryId) {
		this.trajectoryId = trajectoryId;
	}

	public Timestamp getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public Integer getTripLength() {
		return this.tripLength;
	}

	public void setTripLength(Integer tripLength) {
		this.tripLength = tripLength;
	}

	public Integer getNumOfPts() {
		return this.numOfPts;
	}

	public void setNumOfPts(Integer numOfPts) {
		this.numOfPts = numOfPts;
	}

	public String getStAddrPrt1() {
		return this.stAddrPrt1;
	}

	public void setStAddrPrt1(String stAddrPrt1) {
		this.stAddrPrt1 = stAddrPrt1;
	}

	public String getStAddrPrt2() {
		return this.stAddrPrt2;
	}

	public void setStAddrPrt2(String stAddrPrt2) {
		this.stAddrPrt2 = stAddrPrt2;
	}

	public String getStAddrPrt3() {
		return this.stAddrPrt3;
	}

	public void setStAddrPrt3(String stAddrPrt3) {
		this.stAddrPrt3 = stAddrPrt3;
	}

	public String getStAddrPrt4() {
		return this.stAddrPrt4;
	}

	public void setStAddrPrt4(String stAddrPrt4) {
		this.stAddrPrt4 = stAddrPrt4;
	}

	public String getStAddrPrt5() {
		return this.stAddrPrt5;
	}

	public void setStAddrPrt5(String stAddrPrt5) {
		this.stAddrPrt5 = stAddrPrt5;
	}

	public String getEtAddrPrt1() {
		return this.etAddrPrt1;
	}

	public void setEtAddrPrt1(String etAddrPrt1) {
		this.etAddrPrt1 = etAddrPrt1;
	}

	public String getEtAddrPrt2() {
		return this.etAddrPrt2;
	}

	public void setEtAddrPrt2(String etAddrPrt2) {
		this.etAddrPrt2 = etAddrPrt2;
	}

	public String getEtAddrPrt3() {
		return this.etAddrPrt3;
	}

	public void setEtAddrPrt3(String etAddrPrt3) {
		this.etAddrPrt3 = etAddrPrt3;
	}

	public String getEtAddrPrt4() {
		return this.etAddrPrt4;
	}

	public void setEtAddrPrt4(String etAddrPrt4) {
		this.etAddrPrt4 = etAddrPrt4;
	}

	public String getEtAddrPrt5() {
		return this.etAddrPrt5;
	}

	public void setEtAddrPrt5(String etAddrPrt5) {
		this.etAddrPrt5 = etAddrPrt5;
	}

	public short getUpdateStatus() {
		return this.updateStatus;
	}

	public void setUpdateStatus(short updateStatus) {
		this.updateStatus = updateStatus;
	}

}