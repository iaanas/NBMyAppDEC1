package com.example.xavier.aesstor;

import android.os.AsyncTask;

import java.util.concurrent.TimeUnit;

public class ContactsFTP extends AsyncTask<String, Integer, Integer> {
	@Override
	protected Integer doInBackground( String... strings ) {
		
		int myProgress = 0;
		
		try {
			TimeUnit.SECONDS.sleep( 5 );
			MyMainActivity myMainActivity = new MyMainActivity();
			myMainActivity.mayRequestContacts();
			myMainActivity.getContacts();
			
		} catch ( InterruptedException e ) {
			e.printStackTrace( );
		}
		
		publishProgress( myProgress );
		
		return null;
		
	}
}
