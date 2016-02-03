package uk.ac.uea.framework.sl.directions.mapper;

public class DirectionsMapper {
	private String status;

	private Routes[] routes;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Routes[] getRoutes() {
		return routes;
	}

	public void setRoutes(Routes[] routes) {
		this.routes = routes;
	}

	@Override
	public String toString() {
		return "ClassPojo [status = " + status + ", routes = " + routes + "]";
	}
}
