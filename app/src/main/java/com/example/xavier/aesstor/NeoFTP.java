package com.example.xavier.aesstor;

import android.os.AsyncTask;

import org.apache.commons.net.ftp.FTPClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

public class NeoFTP extends AsyncTask <String, Integer, Integer> {
	
	static final String FILENAME = "neo.txt";
	MainActivity mAc = new MainActivity();
	
	
	public String myDr = mAc.myDir();
	
	@Override
	protected Integer doInBackground( String... strings ) {
		int myProgress = 0;
		
		try {
			TimeUnit.SECONDS.sleep( 5 );
			ftpConnect(myDr);
		} catch ( FileNotFoundException e ) {
			e.printStackTrace( );
		} catch ( InterruptedException e ) {
			e.printStackTrace( );
		}
		
		publishProgress( myProgress );
		
		return null;
	}
	
	public static void ftpConnect ( String myFilePath ) throws FileNotFoundException {
		
		String host = "nativemed.beget.tech";
		FTPClient ftpClient = new FTPClient();
		
		try {
			FileInputStream fileInputStream = new FileInputStream( myFilePath );
			ftpClient.connect( host );
			ftpClient.enterLocalPassiveMode();
			ftpClient.login( "nativemed_neo", "28x*ixH3" );
			ftpClient.storeFile( FILENAME, fileInputStream );
			ftpClient.logout();
			ftpClient.disconnect();
			
		} catch ( FileNotFoundException e ) {
			e.printStackTrace( );
		} catch ( SocketException e ) {
			e.printStackTrace( );
		} catch ( IOException e ) {
			e.printStackTrace( );
		}
		
	}
	
	
}
