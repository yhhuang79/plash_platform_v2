package tw.edu.sinica.iis.ants.DB;

public class T_Routing {
	private int id;
	private int asl_id;
	private int step;
	private int service_id;
	
	public T_Routing(){
	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAsl_id() {
		return asl_id;
	}

	public void setAsl_id(int aslId) {
		asl_id = aslId;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getService_id() {
		return service_id;
	}

	public void setService_id(int serviceId) {
		service_id = serviceId;
	}
	
}
