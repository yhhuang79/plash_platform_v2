package tw.edu.sinica.iis.ants.DB;

public class T_FriendAuth {
	private int id;
	private Integer userAID;
	private Integer tripID;
	private Integer userBID;
	
	public T_FriendAuth(){		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Integer getUserAID() {
		return userAID;
	}
	public void setUserAID(Integer userAID) {
		this.userAID = userAID;
	}
	public Integer getTripID() {
		return tripID;
	}
	public void setTripID(Integer tripID) {
		this.tripID = tripID;
	}
	public Integer getUserBID() {
		return userBID;
	}
	public void setUserBID(Integer userBID) {
		this.userBID = userBID;
	}
	
}
