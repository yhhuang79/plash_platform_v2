package tw.edu.sinica.iis.ants.db_pojo.common;

// Generated Jul 16, 2012 8:53:00 PM by Hibernate Tools 3.4.0.CR1

import java.sql.Timestamp;

/**
 * TrajectoryPoints generated by hbm2java
 */
public class TrajectoryPoints implements java.io.Serializable {

	private long pointId;
	private int trajectoryId;
	private Timestamp recordTime;
	private double latitude;
	private double longitude;
	private Timestamp receiveTime;
	private Float altitude;
	private Float accuracy;
	private Float speed;
	private Float bearing;
	private Float accelX;
	private Float accelY;
	private Float accelZ;
	private Float azimuth;
	private Float pitch;
	private Float roll;

	public TrajectoryPoints() {
	}

	public TrajectoryPoints(long pointId, int trajectoryId, double latitude,
			double longitude) {
		this.pointId = pointId;
		this.trajectoryId = trajectoryId;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public TrajectoryPoints(long pointId, int trajectoryId,
			Timestamp recordTime, double latitude, double longitude,
			Timestamp receiveTime, Float altitude, Float accuracy, Float speed,
			Float bearing, Float accelX, Float accelY, Float accelZ,
			Float azimuth, Float pitch, Float roll) {
		this.pointId = pointId;
		this.trajectoryId = trajectoryId;
		this.recordTime = recordTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.receiveTime = receiveTime;
		this.altitude = altitude;
		this.accuracy = accuracy;
		this.speed = speed;
		this.bearing = bearing;
		this.accelX = accelX;
		this.accelY = accelY;
		this.accelZ = accelZ;
		this.azimuth = azimuth;
		this.pitch = pitch;
		this.roll = roll;
	}

	public long getPointId() {
		return this.pointId;
	}

	public void setPointId(long pointId) {
		this.pointId = pointId;
	}

	public int getTrajectoryId() {
		return this.trajectoryId;
	}

	public void setTrajectoryId(int trajectoryId) {
		this.trajectoryId = trajectoryId;
	}

	public Timestamp getRecordTime() {
		return this.recordTime;
	}

	public void setRecordTime(Timestamp recordTime) {
		this.recordTime = recordTime;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Timestamp getReceiveTime() {
		return this.receiveTime;
	}

	public void setReceiveTime(Timestamp receiveTime) {
		this.receiveTime = receiveTime;
	}

	public Float getAltitude() {
		return this.altitude;
	}

	public void setAltitude(Float altitude) {
		this.altitude = altitude;
	}

	public Float getAccuracy() {
		return this.accuracy;
	}

	public void setAccuracy(Float accuracy) {
		this.accuracy = accuracy;
	}

	public Float getSpeed() {
		return this.speed;
	}

	public void setSpeed(Float speed) {
		this.speed = speed;
	}

	public Float getBearing() {
		return this.bearing;
	}

	public void setBearing(Float bearing) {
		this.bearing = bearing;
	}

	public Float getAccelX() {
		return this.accelX;
	}

	public void setAccelX(Float accelX) {
		this.accelX = accelX;
	}

	public Float getAccelY() {
		return this.accelY;
	}

	public void setAccelY(Float accelY) {
		this.accelY = accelY;
	}

	public Float getAccelZ() {
		return this.accelZ;
	}

	public void setAccelZ(Float accelZ) {
		this.accelZ = accelZ;
	}

	public Float getAzimuth() {
		return this.azimuth;
	}

	public void setAzimuth(Float azimuth) {
		this.azimuth = azimuth;
	}

	public Float getPitch() {
		return this.pitch;
	}

	public void setPitch(Float pitch) {
		this.pitch = pitch;
	}

	public Float getRoll() {
		return this.roll;
	}

	public void setRoll(Float roll) {
		this.roll = roll;
	}

}
