package cz.parkovanivpraze;

import java.util.List;

import cz.parkovanivpraze.ParkingMeterService.ServiceException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class OpenMapTask extends AsyncTask<Object, Object, List<ParkingMeter>> {

	private Context context;
	
	private String address;
	private double lat;
	private double lng;
	
	private Throwable error;
	
	public OpenMapTask(Context context, String address) {
		this.context = context;
		this.address = address;
	}
	
	public OpenMapTask(Context context, double lat, double lng) {
		this.context = context;
		this.lat = lat;
		this.lng = lng;
	}
	
	@Override
	protected List<ParkingMeter> doInBackground(Object... params) {
		try {
			if (address == null) {
				return ParkingMeterService.get().findNearby(lat, lng);
			} else {
				return ParkingMeterService.get().findNearby(address);
			}
		} catch (ServiceException e) {
	    	Log.e("SearchActivity", "Service call failed", e);
	    	error = e;
	    	return null;
		}
	}

    @Override
	protected void onPostExecute(List<ParkingMeter> result) {
    	if (error == null) {
    		openMap(result);
    	} else {
	    	Toast.makeText(context, "Chyba pri volani sluzby", Toast.LENGTH_SHORT).show();
    	}
	}

    private void openMap(List<ParkingMeter> parkingMeters) {
    	Intent intent = new Intent(context, MapActivity.class);
    	context.startActivity(intent);
    }
	
}
