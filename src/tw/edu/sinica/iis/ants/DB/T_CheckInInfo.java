package tw.edu.sinica.iis.ants.DB;

public class T_CheckInInfo {
	private int id;
	private String message;
	private Integer emotion ;
	private String picture_uri;
	
	public T_CheckInInfo(){
	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getEmotion() {
		return emotion;
	}

	public void setEmotion(Integer emotion) {
		this.emotion = emotion;
	}

	public String getPicture_uri() {
		return picture_uri;
	}

	public void setPicture_uri(String picture_uri) {
		this.picture_uri = picture_uri;
	}
}
