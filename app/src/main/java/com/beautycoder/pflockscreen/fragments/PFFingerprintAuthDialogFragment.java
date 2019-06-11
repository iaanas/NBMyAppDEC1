package com.beautycoder.pflockscreen.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat.CryptoObject;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xavier.R;

@RequiresApi(api = 23)
public class PFFingerprintAuthDialogFragment extends DialogFragment {
    private PFFingerprintAuthListener mAuthListener;
    private Button mCancelButton;
    private Context mContext;
    private CryptoObject mCryptoObject;
    private PFFingerprintUIHelper mFingerprintCallback;
    private View mFingerprintContent;
    private Stage mStage = Stage.FINGERPRINT;

    /* renamed from: com.beautycoder.pflockscreen.fragments.PFFingerprintAuthDialogFragment$1 */
    class PFFingerprintAuthDialogFragment$1 implements OnClickListener {
        PFFingerprintAuthDialogFragment$1() {
        }

        public void onClick(View view) {
            PFFingerprintAuthDialogFragment.this.dismiss();
        }
    }

    /* renamed from: com.beautycoder.pflockscreen.fragments.PFFingerprintAuthDialogFragment$2 */
    static /* synthetic */ class PFFingerprintAuthDialogFragment$2 {
        /* renamed from: $SwitchMap$com$beautycoder$pflockscreen$fragments$PFFingerprintAuthDialogFragment$Stage */
        static final /* synthetic */ int[] f9x6a51018e = new int[Stage.values().length];

        static {
            try {
                f9x6a51018e[Stage.FINGERPRINT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    public enum Stage {
        FINGERPRINT
    }

    @SuppressLint( "ResourceType" )
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(0, 16974393);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Sign In");
        View v = inflater.inflate( R.layout.view_pf_fingerprint_dialog_container, container, false);
        this.mCancelButton = (Button) v.findViewById( R.id.cancel_button);
        this.mCancelButton.setOnClickListener(new PFFingerprintAuthDialogFragment$1());
        this.mFingerprintContent = v.findViewById( R.id.fingerprint_container);
        this.mFingerprintCallback = new PFFingerprintUIHelper(FingerprintManagerCompat.from(getContext()), (ImageView) v.findViewById( R.id.fingerprint_icon), (TextView) v.findViewById( R.id.fingerprint_status), this.mAuthListener);
        updateStage();
        return v;
    }

    public void onResume() {
        super.onResume();
        if (this.mStage == Stage.FINGERPRINT) {
            this.mFingerprintCallback.startListening(this.mCryptoObject);
        }
    }

    public void setStage(Stage stage) {
        this.mStage = stage;
    }

    public void onPause() {
        super.onPause();
        this.mFingerprintCallback.stopListening();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void updateStage() {
        if (PFFingerprintAuthDialogFragment$2.f9x6a51018e[this.mStage.ordinal()] == 1) {
            this.mCancelButton.setText( "Cancel");
            this.mFingerprintContent.setVisibility(0);
        }
    }

    public void setAuthListener(PFFingerprintAuthListener authListener) {
        this.mAuthListener = authListener;
    }
}
