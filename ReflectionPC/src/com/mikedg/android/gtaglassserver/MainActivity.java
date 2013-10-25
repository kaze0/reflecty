package com.mikedg.android.gtaglassserver;

import java.io.IOException;

import com.mikedg.java.glass.gtaglassclient.GtaServer;
import com.mikedg.java.glass.gtaglassclient.ImageByteHandler;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private GtaServer mServer;
	private ImageView mDisplay;
	private TextView mIpAddressTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupServer();
		mDisplay = (ImageView)findViewById(R.id.imageView1);
		mIpAddressTextView = (TextView)findViewById(R.id.textView1);
		showIpAddress();
	}

	private void showIpAddress() {
		WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		String ipAddress = Formatter.formatIpAddress(ip);
		mIpAddressTextView.setText(ipAddress);
	}

	private void setupServer() {
		mServer = new GtaServer(new DisplayImageByteHandler());
		mServer.setupServer();
	}

	private class DisplayImageByteHandler implements ImageByteHandler {
		@Override
		public void doSomethingWithImageBytes(final byte[] buffer) throws IOException {
			//FIXME: is the data coming in correct?
			//Maybe I'm not pulling in the full data? so thats why these get butchered? maybe lots of 0's at the end?
			runOnUiThread(new Runnable() {
				private Bitmap oldBm = null;

				public void run() {
					//FIXME: getting out of memory errors still
					if (oldBm != null) {
						oldBm.recycle();
					}
					Bitmap bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
					mDisplay.setImageBitmap(bm);
					
					oldBm = bm;
				}
			});
		}
		
	}
}