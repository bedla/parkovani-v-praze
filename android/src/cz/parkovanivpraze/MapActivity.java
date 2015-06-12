package cz.parkovanivpraze;

import android.app.Activity;
import android.os.Bundle;

public class MapActivity extends com.google.android.maps.MapActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		get
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
