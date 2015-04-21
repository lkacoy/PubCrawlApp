package com.example.pubcrawl;

import com.example.pubcrawl.R;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class Tab3Activity extends Activity {

	private WebView webView;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		
		 webView = (WebView) findViewById(R.id.web_view);
	     webView.getSettings().setJavaScriptEnabled(true);
	}
	
}