package cz.parkovanivpraze;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SearchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final EditText textAddress = (EditText) findViewById(R.id.searchAddress);
        
        Button clickButton = (Button) findViewById(R.id.searchButton);
        clickButton.setOnClickListener( new OnClickListener() {

            @Override
            public void onClick(View v) {
            	String address = textAddress.getText().toString();
            	
            	if (address == null || address.trim().isEmpty()) {
            		LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            		
            		LocationListener locationListener = new LocationListener() {
						
						@Override
						public void onStatusChanged(String provider, int status, Bundle extras) {
						}
						
						@Override
						public void onProviderEnabled(String provider) {
						}
						
						@Override
						public void onProviderDisabled(String provider) {
						}
						
						@Override
						public void onLocationChanged(Location location) {
							new OpenMapTask(SearchActivity.this, location.getLatitude(), location.getLongitude()).execute();
						}
					};
            		mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);

            	} else {
					new OpenMapTask(SearchActivity.this, address).execute();
            	}
            }
        });        
    }
    
}
