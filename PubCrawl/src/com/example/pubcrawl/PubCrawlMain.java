	package com.example.pubcrawl;

import java.util.ArrayList;

import com.example.pubcrawl.R;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.webkit.WebView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PubCrawlMain extends ListActivity {

	private GoogleMap myMap;
	private WebView webView;
	private EditText inputField;
	private static final String tag = "Widgets";
	private ArrayList<String> things = new ArrayList<String>();
	private ArrayAdapter<String> adapt = null;
	
	final int PICK1 = Menu.FIRST + 1;
	final int PICK2 = Menu.FIRST + 2;
	final int PICK3 = Menu.FIRST + 3;

	String blah = "";
	int pos = 0;
	String zzz = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pub_crawl_main);
		
		TabHost tabHost = (TabHost)findViewById(R.id.tabhost);
		tabHost.setup();
		TabHost.TabSpec spec;
		
		//tab 1-------------------------------------------------------------
		spec=tabHost.newTabSpec("tag1");	//create new tab specification
		spec.setContent(R.id.tab1);    //add tab view content
		spec.setIndicator("Map");    //put text on tab
		tabHost.addTab(spec);             //put tab in TabHost container
		
		
					
		  myMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();           
	      myMap.setMyLocationEnabled(true);
	      
	      myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	      
	    //tab 2-------------------------------------------------------------
	      
	      
	      	spec=tabHost.newTabSpec("tag2");	//create new tab specification
			spec.setContent(R.id.tab2);    //add tab view content
			spec.setIndicator("Crawl");    //put text on tab
			tabHost.addTab(spec);             //put tab in TabHost container
			
			adapt = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, things);
	        setListAdapter(adapt);
	        inputField = (EditText) findViewById(R.id.input);
			
	      //tab 3-------------------------------------------------------------
		      
	        spec=tabHost.newTabSpec("tag3");	//create new tab specification
			spec.setContent(R.id.tab3);    //add tab view content
			spec.setIndicator("Website");    //put text on tab
			tabHost.addTab(spec);             //put tab in TabHost container
			
			adapt = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, things);
	        setListAdapter(adapt);
	        inputField = (EditText) findViewById(R.id.input);
	        webView = (WebView) findViewById(R.id.web);
		    webView.getSettings().setJavaScriptEnabled(true);
   
        tabHost.addTab(spec);
		
	}
	
	 @Override
		public boolean onCreateOptionsMenu(Menu menu) {
			
			super.onCreateOptionsMenu(menu);
			MenuItem item1 = menu.add(0, PICK1, Menu.NONE, "Add To Crawl");
			MenuItem item2 = menu.add(0, PICK2, Menu.NONE, "Delete From Crawl");
			MenuItem item3 = menu.add(0, PICK3, Menu.NONE, "Edit");
			item1.setShortcut('1', 'd');
			item2.setShortcut('2', 'w');
			item3.setShortcut('3', 't');

			
			return true;
		}
	 
	 @Override
		protected void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);
			
			zzz = things.get(position);
			pos = position;
			inputField.setText(zzz);
		}
		
	public boolean onOptionsItemSelected(MenuItem item) {		
		    
		    int itemID = item.getItemId();  
		    
		    switch (itemID) {
		   
		    case PICK1 : {
		    	
		    	blah = inputField.getText().toString();
		    	things.add(blah);
		    	adapt.notifyDataSetChanged();
		    	return true;
		    }
		    case PICK2 : {
		    	blah = inputField.getText().toString();
		    	if(things.contains(blah)){
		    	things.remove(blah);
		    	adapt.notifyDataSetChanged();}
		    	
		    	else{
		    	Toast.makeText(this, "Please select which entry to delete first.", Toast.LENGTH_LONG).show();}
		    	
		    	return true;
		    	
		    }
		    
		    case PICK3 :
		    	
		    	blah = inputField.getText().toString();
		    	if(things.contains(zzz)){
		    	things.set(pos, blah);
		    	adapt.notifyDataSetChanged();	
		    	}
		    	else{
			    	Toast.makeText(this, "Please select which entry to edit first.", Toast.LENGTH_LONG).show();}
		    	
		    	return true;
		    
		    default: super.onOptionsItemSelected(item);
		    }
		   		   
	    return false;
	}
}
