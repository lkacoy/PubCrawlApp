package com.example.pubcrawl;

import java.util.ArrayList;

import com.example.pubcrawl.R;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class PubCrawlMain extends ListActivity {

       private GoogleMap myMap;
       private WebView webView;
       private TextView inputField;
       private static final String tag = "Widgets";
       private ArrayList<String> things = new ArrayList<String>();
       private ArrayAdapter<String> adapt = null;
       private long lastTouchTimeDown = -1;     
       private long lastTouchTimeUp = -1;
       private static final float zoom = 14.0f;
       private TextView text;
       
       private NotificationManager mNotificationManager; //sets up the notification system
       private Notification notifyDetails;
       private int SIMPLE_NOTFICATION_ID;
       
       final int PICK1 = Menu.FIRST + 1;
       final int PICK2 = Menu.FIRST + 2;
       
       LocationManager  locManager;
       LocationListener locListener;
      

       String blah = "";
       int pos = 0;
       String zzz = "";
       double lat;
       double lon;
       
       Handler handler = new Handler(){
              public void handleMessage(Message msg) {
                     String title =(String) msg.obj;
                 //    text.append(title + "\n" +"\n");
              }
       };
       
       public void onLocationChanged(Location loc) {

              lat = loc.getLatitude();
              lon = loc.getLongitude();


       }
       @Override
       protected void onCreate(Bundle savedInstanceState) {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_pub_crawl_main); //sets the layout
              mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); //notification system created
              
              
              TabHost tabHost = (TabHost)findViewById(R.id.tabhost); //creates the tabhost for our app
              tabHost.setup();
              TabHost.TabSpec spec;
              
              //tab 1-------------------------------------------------------------Dedicated to our map
              spec=tabHost.newTabSpec("tag1");  //create new tab specification
              spec.setContent(R.id.tab1);    //add tab view content
              spec.setIndicator("Map");    //put text on tab
              tabHost.addTab(spec);             //put tab in TabHost container
              
              locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
              
            /**  locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0,
                           locListener);**/
              
              myMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();

              myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
              myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.36,-71.0568), zoom));
             
               myMap.setOnMapClickListener( 
                            new OnMapClickListener() {
                                  public void onMapClick(LatLng point) {
                                         Toast.makeText(getApplicationContext(), "Pub Crawl", Toast.LENGTH_SHORT).show();                     
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
                     
             //tab 3-------------------------------------------------------------Dedicated to a website that the user can view
                    
               spec=tabHost.newTabSpec("tag3"); //create new tab specification
                     spec.setContent(R.id.tab3);    //add tab view content
                     spec.setIndicator("Website");    //put text on tab
                     tabHost.addTab(spec);             //put tab in TabHost container
                     
                     adapt = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, things);
               setListAdapter(adapt);
               inputField = (TextView) findViewById(R.id.input);
               webView = (WebView) findViewById(R.id.web);
                  webView.getSettings().setJavaScriptEnabled(true);
   
                  //Create a background thread to run the Yelp API
                  Thread t=new Thread(background);
                  t.start();
        
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
                  String JSONFeed = yelp.search("bar", 42.35,-71.18, search_limit); //searches for pub
              
              
                  try{
                                 
                     JSONObject obj = new JSONObject(JSONFeed);
                           
                           //get total of businesses in response without limit
                           int total = obj.getInt("total");
                           Log.i("JSON", "total " + total);
                                         
                           JSONArray businesses = new JSONArray();
                           businesses = obj.getJSONArray("businesses");
                           Log.i("JSON",
                                         "Number of entries " + businesses.length());
                           
                           //for each array item get name and date
                           for (int i = 0; i < businesses.length(); i++) {
                                  JSONObject jsonObject = businesses.getJSONObject(i);
                                  String name = jsonObject.getString("name");
                               double rating = jsonObject.getDouble("rating");
                               
                               JSONObject location = new JSONObject();
                               location = jsonObject.getJSONObject("location");
                               
                               String city = location.getString("city");
                               
                               String data = name + ", " + city + " - " + rating;
                               
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
       
public void addMarkers(GoogleMap map) {
              
       for (int i = 0; i < things.size(); i++)
              map.addMarker(new MarkerOptions()
        .position(new LatLng(41.498370,-81.693883))
        .title("Terminal Tower")
        .snippet("At the heart of the city")
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
       
       
}
}

