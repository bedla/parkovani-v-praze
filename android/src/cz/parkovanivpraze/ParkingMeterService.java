package cz.parkovanivpraze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParkingMeterService {
	private static final ParkingMeterService INSTANCE = new ParkingMeterService();
	
	public static ParkingMeterService get() {
		return INSTANCE;
	}
	
	public List<ParkingMeter> findNearby(double lat, double lng) throws ServiceException {
		String data;
		try {
			data = callService("?lat=" + lat + "&long=" + lng);
		} catch (Exception e) {
			throw new ServiceException("Request to service failed", e);
		}
		
		try {
			return parseResponse(data);
		} catch (JSONException e) {
			throw new ServiceException("Failed to parse response", e);
		}
	}
	
	public List<ParkingMeter> findNearby(String address) throws ServiceException {
		String data;
		try {
			data = callService("?address=" + address);
		} catch (Exception e) {
			throw new ServiceException("Request to service failed", e);
		}
		
		try {
			return parseResponse(data);
		} catch (JSONException e) {
			throw new ServiceException("Failed to parse response", e);
		}
	}
	
	private List<ParkingMeter> parseResponse(String data) throws JSONException {
		JSONArray jsonArray = new JSONArray(data);
		
		List<ParkingMeter> result = new ArrayList<ParkingMeter>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = jsonArray.getJSONObject(i);
			
			
			ParkingMeter parkingMeter = new ParkingMeter();
			parkingMeter.setLat(object.getDouble("lat"));
			parkingMeter.setLng(object.getDouble("long"));
			parkingMeter.setTarifKc(object.getDouble("tarif_kc"));
			parkingMeter.setTarifEur(object.getDouble("tarif_eur"));
			parkingMeter.setLng(object.getDouble("long"));
			parkingMeter.setHours(object.getString("hours"));
			parkingMeter.setType(object.getString("typ"));
			result.add(parkingMeter);
		}

		return result;
	}
	
	private String callService(String params) throws URISyntaxException, ClientProtocolException, IOException {

        BufferedReader in = null;
        String data = null;

        try
        {
            HttpClient client = new DefaultHttpClient();
            URI website = new URI("http://parkovanivpraze.apiary.io/parkingMeter" + params);
            HttpGet request = new HttpGet();
            request.setURI(website);
            HttpResponse response = client.execute(request);
            
            response.getStatusLine().getStatusCode();

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String l = "";
            String nl = System.getProperty("line.separator");
            while ((l = in.readLine()) !=null){
                sb.append(l + nl);
            }
            in.close();
            data = sb.toString();
            return data;        
        } finally{
            if (in != null){
                try{
                    in.close();
                    return data;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }		
	}
	
	public static class ServiceException extends Exception {

		public ServiceException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}
		
	}
}
