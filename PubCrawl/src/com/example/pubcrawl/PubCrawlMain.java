package com.example.pubcrawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.webkit.WebView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class PubCrawlMain extends ListActivity implements OnInitListener {

       private GoogleMap myMap; //map will show where the pubs are located
       private WebView webView; //will load the web pages of the bars
       private TextView inputField; 
       private ListView listCrawl;
       private static final String tag = "Widgets";
       private ArrayList<String> things = new ArrayList<String>();
       private ArrayList<String>urls= new ArrayList<String>();
       private ArrayList<String>bars= new ArrayList<String>();
    		   
       private ArrayAdapter<String> adapt = null;
       private long lastTouchTimeDown = -1;     
       private long lastTouchTimeUp = -1;
       private static final float zoom = 14.0f; //camera will zoom to this level on the map
       
       private NotificationManager mNotificationManager; //sets up the notification manager
       private Notification notifyDetails;				//variable to have the actual notification
       private int SIMPLE_NOTFICATION_ID;
       private String contentTitle = "Pub Crawl";  //sets the title of the notification to the pub crawl
       private String contentText = "Your plan has been successfully updated";
       
       //the menu will let users add or delete bars from their crawl plan
       final int PICK1 = Menu.FIRST + 1;
       final int PICK2 = Menu.FIRST + 2;
       
       //declaring the variables for the location
       LocationManager  locManager;
       LocationListener locListener;
      

       String blah = "";
       int pos = 0;
       String zzz = "";
       double lat = 42.3600;  //default coordinates for Fanueil Hall
       double lon = -71.0568;
       double longitude;
       private TextToSpeech speaker; //declares a variable for the app to inform user that they are updated their list
       int total=0; //total will print the total number of pubs in the area
       
       
       //Handles the message sent from the JSON to deliver the Yelp information 
       Handler handler = new Handler(){
              public void handleMessage(Message msg) {
                     String title =(String) msg.obj;
                     inputField.append(title+ "/n"+"/n");
              }
       };
       
       //Location Change stuff

   	public class MyLocationListener implements LocationListener{
   		
   		@Override
   		public void onLocationChanged(Location loc) {

   	        lat = loc.getLatitude();
   	        lon = loc.getLongitude();
   	 }

   		//If GPS is disabled, set location to Faneuil Hall
   		@Override
   		public void onProviderDisabled(String provider) {
   			
   			Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();
   			lat = 42.3600;
   			lon = -71.0568;
   		}

   		//If GPS enabled, notification to let user know it's enabled
   		@Override
   		public void onProviderEnabled(String provider) {
   			
   			Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show(); //if GPS is on, a popup will let the user know
   		
   		}

   		@Override
   		public void onStatusChanged(String provider, int status, Bundle extras) {
   			
   		}
   		
   	}

       @Override
       protected void onCreate(Bundle savedInstanceState) {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_pub_crawl_main); //sets the layout
              speaker = new TextToSpeech(this, this);
              
              final TabHost tabHost = (TabHost)findViewById(R.id.tabhost); //creates the tabhost for our app
              tabHost.setup();
              TabHost.TabSpec spec;
              
              //tab 1-------------------------------------------------------------Dedicated to our map
              spec=tabHost.newTabSpec("tag1");  //create new tab specification
              spec.setContent(R.id.tab1);    //add tab view content
              spec.setIndicator("Map");    //put text on tab
              tabHost.addTab(spec);             //put tab in TabHost container
              
              locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
              locListener = new MyLocationListener();
              locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0,
      				locListener);
              Location current = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
              
              if (current != null) {
            	  lat = current.getLatitude();
            	  lon = current.getLongitude();
              }
              
              myMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
              myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
              myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon), zoom));
              myMap.setMyLocationEnabled(true);
              
              //adds markers to bars on the map
              addMarkers(myMap);
              
              myMap.setOnMapClickListener( 
                            new OnMapClickListener() {
                                  public void onMapClick(LatLng point) {
                                       //  Toast.makeText(getApplicationContext(), "Pub Crawl", Toast.LENGTH_SHORT).show();  
                                	  Toast.makeText(getApplicationContext(), "Total found: " + things.get(0), Toast.LENGTH_LONG).show();  //toast will print out the number of bars
                                 
                                	  
                                  }
                            }
                            );
              
              //On clicking a marker, will switch to tab 2 and go to Yelp page of bar
              myMap.setOnMarkerClickListener( 
  	        		new OnMarkerClickListener() {
  	        			
  	        			public boolean onMarkerClick(Marker m) {
  	        				String title = m.getTitle();
  	        				String snip = m.getSnippet();
  	        				Toast.makeText(getApplicationContext(),  title, Toast.LENGTH_LONG).show();
  	        				webView.loadUrl(snip);
  	        				tabHost.setCurrentTab(2);
  	        				return true;
  	        			}
  	        		}
  	        		);
             
           //tab 2-------------------------------------------------------------Dedicated to our Crawl plan that the user can decide
             
             
              spec=tabHost.newTabSpec("tag2");  //create new tab specification
                     spec.setContent(R.id.tab2);    //add tab view content
                     spec.setIndicator("Crawl");    //put text on tab
                     tabHost.addTab(spec);             //put tab in TabHost container
                     
                     adapt = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, things);
               setListAdapter(adapt);
               inputField = (TextView) findViewById(R.id.input);
               //listCrawl=(ListView)findViewById(android.R.id.list);
                     
             //tab 3-------------------------------------------------------------Dedicated to a website that the user can view
                    
               spec=tabHost.newTabSpec("tag3"); //create new tab specification
                     spec.setContent(R.id.tab3);    //add tab view content
                     spec.setIndicator("Website");    //put text on tab
                     tabHost.addTab(spec);             //put tab in TabHost container
                     
                     adapt = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, things);
               setListAdapter(adapt);
               
               	webView = (WebView) findViewById(R.id.web); //
                  webView.getSettings().setJavaScriptEnabled(true);
   
                  //Create a background thread to run the Yelp API
                  Thread t=new Thread(background);
                  t.start();
                  
                  mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); //notification system created
                  //Set up the intents for the notification system which will let users know when they added something or deleted from crawl
                  Intent notifyIntent=new Intent(this,PubCrawlMain.class);
                  
                  //Pending intent to fire the notification when needed
                  PendingIntent pendingIntent = PendingIntent.getActivity(
          				this, 0, notifyIntent,
          				android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                  
                  
                  notifyDetails=new Notification.Builder(this)
                  			.setContentTitle(contentTitle) //set Notification title 
                  			.setContentText(contentText)
                  			.setSmallIcon(R.drawable.ic_launcher)
                  			.setWhen(System.currentTimeMillis())
                  			
                  			//Set title, text and pending intent to fire when notify() executed
                  			.addAction(R.drawable.ic_launcher, contentTitle,pendingIntent)
                  			//set Android to vibrate when notified
                  			.setVibrate(new long[] {100, 100, 200, 300})
                  			//flash LED 
                  			.setLights(Integer.MAX_VALUE,500,500)
                  			.build();
                  
                  

       }
       
       @Override
              public boolean onCreateOptionsMenu(Menu menu) {
                     //Create the menu for the user to add, remove and edit crawl space
                     super.onCreateOptionsMenu(menu);
                     MenuItem item1 = menu.add(0, PICK1, Menu.NONE, "Add To Crawl");
                     MenuItem item2 = menu.add(0, PICK2, Menu.NONE, "Delete From Crawl");
                     item1.setShortcut('1', 'd');
                     item2.setShortcut('2', 'w');


                     
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
                 
                  //Let's the user add a bar to their crawl list (tab 2)
                  case PICK1 : {
                     
                     blah = inputField.getText().toString();
                     things.add(blah);
                     adapt.notifyDataSetChanged();
                     String addCrawl="Added to Crawl";
                     speak(addCrawl);
                     return true;
                  }
                  
                  //Let's the user deleted from the crawl list (tab 2)
                  case PICK2 : {
                     blah = inputField.getText().toString();
                     if(things.contains(blah)){
                     things.remove(blah);
                     adapt.notifyDataSetChanged();
                     String deleteCrawl="Deleted from Crawl";
                     speak(deleteCrawl);}
                     
                     else{
                     Toast.makeText(this, "Please select which entry to delete first.", Toast.LENGTH_LONG).show();} //makes sure the user selects an entry first
                     
                     return true;
                     
                  }
                  
                  }
                               
           return false;
       }
       
       Runnable background= new Runnable(){ //This is the background thread that will run the Yelp API

              @Override
              public void run() {
                     
                     //Yelp API keys (these are Lexi's keys)
                     String consumerKey = "y649aR90R2aZY2b3KDWKVQ";
                  String consumerSecret = "h7ESEiDFcfo-ZuC5IApeXTJ9LBU";
                  String token = "OUnZIyQiPPg6U1r-zqX7BrVNwzFcFu-j";
                  String tokenSecret = "dKpT7ep2mOcoQJRQTjaTnA7Ft1M";
                  
                  int search_limit = 20;
                     
                  Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret); //create a new Yelp object with the generated keys
                  String JSONFeed = yelp.search("bar", lat, lon, search_limit); //searches for pub
              
              
                  try{
                      //creates a JSON object to let us search for the information stored in Yelp API          
                     JSONObject obj = new JSONObject(JSONFeed);
                           
                           //get total of businesses in response without limit
                           total = obj.getInt("total");
                           Log.i("JSON", "total " + total);
                           //will show how many bars are found (hopefully...)
                          // Toast.makeText(getApplicationContext(), "Total found: " + total, Toast.LENGTH_SHORT).show(); 
                                         
                           JSONArray businesses = new JSONArray();
                           businesses = obj.getJSONArray("businesses");
                           Log.i("JSON",
                                         "Number of entries " + businesses.length());
                           
                           //for each array item get name, location, rating
                           for (int i = 0; i < businesses.length(); i++) {
           					JSONObject jsonObject = businesses.getJSONObject(i);
           					String name = jsonObject.getString("name");
           				    double rating = jsonObject.getDouble("rating");
           				    
           				    JSONObject location = new JSONObject();
           				    location = jsonObject.getJSONObject("location");
           				    String streets=location.getString("cross_streets");
           				    String state=location.getString("state_code");
           				    String country=location.getString("country_code");
           				    String addy = location.getString("address").substring(2, location.getString("address").length()-2);
           				    String city = location.getString("city");
           				    String data = addy + ", " + city + ", " + country;
           				    String url =jsonObject.getString("mobile_url");
           				    
           				    urls.add(url);
           				    things.add(data);
           				    bars.add(name);
           				    
           				    //sent to Handler queue 
           				    Message msg = handler.obtainMessage();
           				    msg.obj = data;
           				    handler.sendMessage(msg);
           				    
                                  Log.i("JSON", data);
                                  
                     
                  }
                  }
                  catch(JSONException e){e.getMessage();
                     e.printStackTrace();
                  }
                     
                  
              }
              
              
              
       };
       
       //adds markers to the map to indicate where the pubs are located
public void addMarkers(GoogleMap map) {
              
       for (int i = 0; i < things.size(); i++) {
    	   Geocoder fwd = new Geocoder(this);
    	   String streetAddr =things.get(i);
    	   List<Address> locations = null;        //contains returned geoPoints
    	   
    	   try {
			locations = fwd.getFromLocationName(streetAddr, 1);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    	   Address a = locations.get(i);


              map.addMarker(new MarkerOptions()
        .position(new LatLng(a.getLatitude(),a.getLongitude()))
        .title(bars.get(i))
        .snippet(urls.get(i))
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));}
       
       
}

@Override
public void onInit(int status) {
	// TODO Auto-generated method stub
	
}

//speak method to let the app notify user that things have been added/deleted from the crawl list on tab 2
public void speak(String output){
	speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null);
}
}

