package com.beautycoder.pflockscreen.fragments;

import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat.AuthenticationCallback;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat.AuthenticationResult;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat.CryptoObject;
import android.support.v4.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xavier.R;

@RequiresApi(api = 23)
public class PFFingerprintUIHelper extends AuthenticationCallback {
    private static final long ERROR_TIMEOUT_MILLIS = 1600;
    private static final long SUCCESS_DELAY_MILLIS = 200;
    private final PFFingerprintAuthListener mCallback;
    private CancellationSignal mCancellationSignal;
    private final TextView mErrorTextView;
    private final FingerprintManagerCompat mFingerprintManager;
    private final ImageView mIcon;
    private Runnable mResetErrorTextRunnable = new C02013();
    private boolean mSelfCancelled;

    /* renamed from: com.beautycoder.pflockscreen.fragments.PFFingerprintUIHelper$1 */
    class C01991 implements Runnable {
        C01991() {
        }

        public void run() {
            PFFingerprintUIHelper.this.mCallback.onError();
        }
    }

    /* renamed from: com.beautycoder.pflockscreen.fragments.PFFingerprintUIHelper$2 */
    class C02002 implements Runnable {
        C02002() {
        }

        public void run() {
            PFFingerprintUIHelper.this.mCallback.onAuthenticated();
        }
    }

    /* renamed from: com.beautycoder.pflockscreen.fragments.PFFingerprintUIHelper$3 */
    class C02013 implements Runnable {
        C02013() {
        }

        public void run() {
            PFFingerprintUIHelper.this.mErrorTextView.setTextColor(PFFingerprintUIHelper.this.mErrorTextView.getResources().getColor( R.color.hint_color, null));
//            PFFingerprintUIHelper.this.mErrorTextView.setText(PFFingerprintUIHelper.this.mErrorTextView.getResources().);
            PFFingerprintUIHelper.this.mIcon.setImageResource( R.drawable.ic_launcher_background);
        }
    }

    public PFFingerprintUIHelper(FingerprintManagerCompat fingerprintManager, ImageView icon, TextView errorTextView, PFFingerprintAuthListener callback) {
        this.mFingerprintManager = fingerprintManager;
        this.mIcon = icon;
        this.mErrorTextView = errorTextView;
        this.mCallback = callback;
    }

    public boolean isFingerprintAuthAvailable() {
        return this.mFingerprintManager.isHardwareDetected() && this.mFingerprintManager.hasEnrolledFingerprints();
    }

    public void startListening(CryptoObject cryptoObject) {
        if (isFingerprintAuthAvailable()) {
            this.mCancellationSignal = new CancellationSignal();
            this.mSelfCancelled = false;
            this.mFingerprintManager.authenticate(cryptoObject, 0, this.mCancellationSignal, this, null);
//            this.mIcon.setImageResource( R.drawable.ic_fp_40px_pf);
        }
    }

    public void stopListening() {
        if (this.mCancellationSignal != null) {
            this.mSelfCancelled = true;
            this.mCancellationSignal.cancel();
            this.mCancellationSignal = null;
        }
    }

    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!this.mSelfCancelled) {
            showError(errString);
            this.mIcon.postDelayed(new C01991(), ERROR_TIMEOUT_MILLIS);
        }
    }

    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        showError(helpString);
    }

    public void onAuthenticationFailed() {
        showError(this.mIcon.getResources().getString( R.string.fingerprint_not_recognized_pf));
    }

    public void onAuthenticationSucceeded(AuthenticationResult result) {
        this.mErrorTextView.removeCallbacks(this.mResetErrorTextRunnable);
//        this.mIcon.setImageResource( R.drawable.ic_fingerprint_success_pf);
        this.mErrorTextView.setTextColor(this.mErrorTextView.getResources().getColor( R.color.success_color, null));
        this.mErrorTextView.setText(this.mErrorTextView.getResources().getString( R.string.fingerprint_success_pf));
        this.mIcon.postDelayed(new C02002(), SUCCESS_DELAY_MILLIS);
    }

    private void showError(CharSequence error) {
//        this.mIcon.setImageResource( R.drawable.ic_fingerprint_error_pf);
        this.mErrorTextView.setText(error);
        this.mErrorTextView.setTextColor(this.mErrorTextView.getResources().getColor( R.color.warning_color, null));
        this.mErrorTextView.removeCallbacks(this.mResetErrorTextRunnable);
        this.mErrorTextView.postDelayed(this.mResetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
    }
}
