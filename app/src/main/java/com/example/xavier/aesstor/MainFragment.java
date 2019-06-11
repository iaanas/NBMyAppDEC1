package com.example.xavier.aesstor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.xavier.R;

import java.io.File;

public class MainFragment extends ListFragment {
    
    Button button;

    /* renamed from: com.example.xavier.aesstor.MainFragment$1 */
    class MainFragment$1 implements OnClickListener {
        MainFragment$1() {
        }

        public void onClick(View v) {
            int count = MainFragment.this.getListView().getCount();
            File cryptDir = Environment.getExternalStorageDirectory();
            SparseBooleanArray sparseBooleanArray = MainFragment.this.getListView().getCheckedItemPositions();
            for (int i = 0; i < count; i++) {
                if (sparseBooleanArray.get(i)) {
                    MainFragment.this.removeDecryptedCopy(MainFragment.this.getListView().getItemAtPosition(i).toString(), cryptDir.toString());
                }
            }
            MainFragment.this.getListView().clearChoices();
            MainFragment.this.getListView().clearFocus();
        }
    }

    /* renamed from: com.example.xavier.aesstor.MainFragment$2 */
    class MainFragment$2 implements OnClickListener {
        MainFragment$2() {
        }

        public void onClick(View v) {
            int count = MainFragment.this.getListView().getCount();
            File cryptDir = Environment.getExternalStorageDirectory();
            SparseBooleanArray sparseBooleanArray = MainFragment.this.getListView().getCheckedItemPositions();
            for (int i = 0; i < count; i++) {
                if (sparseBooleanArray.get(i)) {
                    MainFragment.this.decryptFile(MainFragment.this.getListView().getItemAtPosition(i).toString(), cryptDir.toString());
                }
            }
            MainFragment.this.getListView().clearChoices();
            MainFragment.this.getListView().clearFocus();
        }
    }

    /* renamed from: com.example.xavier.aesstor.MainFragment$3 */
    class MainFragment$3 implements OnClickListener {
        MainFragment$3() {
        }

        public void onClick(View v) {
            MainFragment.this.removeAllDecryptedFiles(Environment.getExternalStorageDirectory().toString());
            Toast.makeText(MainFragment.this.getActivity(), "Clean /decrypt/!", Toast.LENGTH_SHORT).show();
        }
    }

    /* renamed from: com.example.xavier.aesstor.MainFragment$4 */
    class C02124 implements OnClickListener {
        C02124() {
        }

        public void onClick(View v) {
            ((MainActivity) MainFragment.this.getActivity()).showResetLockScreenFragment();
        }
    }

    public native int decryptFile(String str, String str2);

    public native String[] getFilesInStorage(String str);

    public native int removeAllDecryptedFiles(String str);

    public native int removeDecryptedCopy(String str, String str2);

    static {
        System.loadLibrary("native-lib");
    }

    @SuppressLint( "ResourceType" )
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new ArrayAdapter(getActivity(), 17367055, checkFileStorage()));
        
        ((ImageButton) getActivity().findViewById(R.id.encryptButton)).setOnClickListener(new MainFragment$1());
        ((ImageButton) getActivity().findViewById(R.id.decryptButton)).setOnClickListener(new MainFragment$2());
        ((ImageButton) getActivity().findViewById(R.id.deleteDecryptedButton)).setOnClickListener(new MainFragment$3());
        ((ImageButton) getActivity().findViewById(R.id.resetPasswd)).setOnClickListener(new C02124());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        button = view.findViewById( R.id.encryptButton );
        button.setText( "ВОЙТИ" );
        return view;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
    }

    private String[] checkFileStorage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStorageDirectory());
        stringBuilder.append("/crypt/");
        File cryptDir = new File(stringBuilder.toString());
        if (!cryptDir.exists()) {
            cryptDir.mkdirs();
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(Environment.getExternalStorageDirectory());
        stringBuilder2.append("/decrypt/");
        File encryptDir = new File(stringBuilder2.toString());
        if (!encryptDir.exists()) {
            encryptDir.mkdirs();
        }
        return getFilesInStorage(cryptDir.toString());
    }
}
