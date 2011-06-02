package tw.edu.sinica.iis.ants.DB;

public class T_Item {
	private Integer id;
	private Integer userid;
	private Integer activityid;
	private String name;
	private Double price;
	private Boolean assigned;
	private Integer assigneduserid;
	
	public T_Item(){
	
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Boolean getAssigned() {
		return assigned;
	}

	public void setAssigned(Boolean assigned) {
		this.assigned = assigned;
	}

	public Integer getAssigneduserid() {
		return assigneduserid;
	}

	public void setAssigneduserid(Integer assigneduserid) {
		this.assigneduserid = assigneduserid;
	}
	
}
