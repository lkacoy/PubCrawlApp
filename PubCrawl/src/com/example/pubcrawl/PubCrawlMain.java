	package com.example.pubcrawl;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.content.Intent;

public class PubCrawlMain extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pub_crawl_main);
		
		TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
		
        TabSpec tab1 = tabHost.newTabSpec("Map");
        TabSpec tab2 = tabHost.newTabSpec("Today's Crawl");
        TabSpec tab3 = tabHost.newTabSpec("Website");
        
        tab1.setIndicator("Tab1");
        tab1.setContent(new Intent(this,Tab1Activity.class));
        
        tab2.setIndicator("Tab2");
        tab2.setContent(new Intent(this,Tab2Activity.class));

        tab3.setIndicator("Tab3");
        tab3.setContent(new Intent(this,Tab3Activity.class));

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
		
	}
}
