package com.example.pubcrawl;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import android.os.Bundle;


import android.app.Activity;
public class Tab1Activity extends Activity {

	private GoogleMap myMap;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	  myMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
              .getMap();           
      myMap.setMyLocationEnabled(true);
      
      myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
      
      
}

}