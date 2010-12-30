package tw.edu.sinica.iis.ants.DB;

public class T_Service {
	private int id;
	private int endpoint_id;
	private String name;
	private String description;
	
	public T_Service(){
	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEndpoint_id() {
		return endpoint_id;
	}

	public void setEndpoint_id(int endpointId) {
		endpoint_id = endpointId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
}
