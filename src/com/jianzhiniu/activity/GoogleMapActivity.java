package com.jianzhiniu.activity;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * 谷歌地图界面
 * @author Administrator
 *
 */
public class GoogleMapActivity extends BaseFragmentActivity implements LocationListener {
	
	GoogleMap googleMap;
	float dis;
	private TextView topcenter;
	private ImageView returnView;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.googlemap_view);
		
		intent = getIntent();
		topcenter = (TextView)findViewById(R.id.top_centertext);
		returnView = (ImageView)findViewById(R.id.top_return);
		topcenter.setText("Map");
		
		returnView.setOnClickListener(new returnclick());
		// Getting reference to the SupportMapFragment of activity_main.xml
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		
		// Getting GoogleMap object from the fragment
		googleMap = fm.getMap();
		
		// 让定位按钮显示出来
//		googleMap.setMyLocationEnabled(true);	
				
		
		 // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        if (intent.hasExtra("lat")) {
        	Location location2 = new Location(provider);
        	location2.setLatitude(Double.valueOf(intent.getStringExtra("lat")));
        	location2.setLongitude(Double.valueOf(intent.getStringExtra("long")));
        	onLocationChanged(location2);
		}

	}
	
	private class returnclick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			GoogleMapActivity.this.finish();
		}
		
	}
	
	@Override
	public void onLocationChanged(Location location) {
		
		
		// Getting latitude of the current location
		double latitude = location.getLatitude();
		
		// Getting longitude of the current location
		double longitude = location.getLongitude();		
		
		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);
		
		googleMap.addMarker(new MarkerOptions().position(latLng).title(intent.getStringExtra("place")));
		// Showing the current location in Google Map
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		
		// Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
		
		// Setting latitude and longitude in the TextView tv_location
				
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub		
	}
	
}
