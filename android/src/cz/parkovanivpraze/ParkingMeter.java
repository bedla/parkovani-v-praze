package cz.parkovanivpraze;

public class ParkingMeter {

	private double lat;
	private double lng;
	private String hours;
	private double tarifKc;
	private double tarifEur;
	private String type;
	
	
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public String getHours() {
		return hours;
	}
	public void setHours(String hours) {
		this.hours = hours;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getTarifKc() {
		return tarifKc;
	}
	public void setTarifKc(double tarifKc) {
		this.tarifKc = tarifKc;
	}
	public double getTarifEur() {
		return tarifEur;
	}
	public void setTarifEur(double tarifEur) {
		this.tarifEur = tarifEur;
	}
	
}
