package com.beautycoder.pflockscreen.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.security.PFFingerprintPinCodeHelper;
import com.beautycoder.pflockscreen.security.PFSecurityException;
import com.beautycoder.pflockscreen.views.PFCodeView;
import com.beautycoder.pflockscreen.views.PFCodeView.OnPFCodeListener;
import com.example.xavier.R;

public class PFLockScreenFragment extends Fragment {
    private static final String FINGERPRINT_DIALOG_FRAGMENT_TAG = "FingerprintDialogFragment";
    private static final String TAG = PFLockScreenFragment.class.getName();
    private String mCode = "";
    private OnPFLockScreenCodeCreateListener mCodeCreateListener;
    private OnPFCodeListener mCodeListener = new C02536();
    private PFCodeView mCodeView;
    private PFFLockScreenConfiguration mConfiguration;
    private View mDeleteButton;
    private String mEncodedPinCode = "";
    private View mFingerprintButton;
    private boolean mFingerprintHardwareDetected = false;
    private boolean mIsCreateMode = false;
    private TextView mLeftButton;
    private OnPFLockScreenLoginListener mLoginListener;
    private Button mNextButton;
    private OnClickListener mOnDeleteButtonClickListener = new C02032();
    private OnLongClickListener mOnDeleteButtonOnLongClickListener = new C02043();
    private OnClickListener mOnFingerprintClickListener = new C02054();
    private OnClickListener mOnKeyClickListener = new C02021();
    private OnClickListener mOnNextButtonClickListener = new C02077();
    private View mRootView;
    private boolean mUseFingerPrint = true;

    /* renamed from: com.beautycoder.pflockscreen.fragments.PFLockScreenFragment$1 */
    class C02021 implements OnClickListener {
        C02021() {
        }

        public void onClick(View v) {
            if (v instanceof TextView) {
                String string = ((TextView) v).getText().toString();
                if (string.length() == 1) {
                    PFLockScreenFragment.this.configureRightButton(PFLockScreenFragment.this.mCodeView.input(string));
                }
            }
        }
    }

    /* renamed from: com.beautycoder.pflockscreen.fragments.PFLockScreenFragment$2 */
    class C02032 implements OnClickListener {
        C02032() {
        }

        public void onClick(View v) {
            PFLockScreenFragment.this.configureRightButton(PFLockScreenFragment.this.mCodeView.delete());
        }
    }

    /* renamed from: com.beautycoder.pflockscreen.fragments.PFLockScreenFragment$3 */
    class C02043 implements OnLongClickListener {
        C02043() {
        }

        public boolean onLongClick(View v) {
            PFLockScreenFragment.this.mCodeView.clearCode();
            PFLockScreenFragment.this.configureRightButton(0);
            return true;
        }
    }

    /* renamed from: com.beautycoder.pflockscreen.fragments.PFLockScreenFragment$4 */
    class C02054 implements OnClickListener {
        C02054() {
        }

        public void onClick(View v) {
            if (VERSION.SDK_INT >= 23) {
                if (PFLockScreenFragment.this.isFingerprintApiAvailable(PFLockScreenFragment.this.getActivity())) {
                    if (PFLockScreenFragment.this.isFingerprintsExists(PFLockScreenFragment.this.getActivity())) {
                        final PFFingerprintAuthDialogFragment fragment = new PFFingerprintAuthDialogFragment();
                        fragment.show(PFLockScreenFragment.this.getFragmentManager(), PFLockScreenFragment.FINGERPRINT_DIALOG_FRAGMENT_TAG);
                        fragment.setAuthListener(new PFFingerprintAuthListener() {
                            public void onAuthenticated() {
                                if (PFLockScreenFragment.this.mLoginListener != null) {
                                    PFLockScreenFragment.this.mLoginListener.onFingerprintSuccessful();
                                }
                                fragment.dismiss();
                            }

                            public void onError() {
                                if (PFLockScreenFragment.this.mLoginListener != null) {
                                    PFLockScreenFragment.this.mLoginListener.onFingerprintLoginFailed();
                                }
                            }
                        });
                        return;
                    }
                    PFLockScreenFragment.this.showNoFingerprintDialog();
                }
            }
        }
    }

    /* renamed from: com.beautycoder.pflockscreen.fragments.PFLockScreenFragment$5 */
    class C02065 implements DialogInterface.OnClickListener {
        C02065() {
        }

        public void onClick(DialogInterface dialog, int which) {
            PFLockScreenFragment.this.startActivity(new Intent("android.settings.SECURITY_SETTINGS"));
        }
    }

    /* renamed from: com.beautycoder.pflockscreen.fragments.PFLockScreenFragment$7 */
    class C02077 implements OnClickListener {
        C02077() {
        }

        public void onClick(View v) {
            try {
                String encodedCode = PFFingerprintPinCodeHelper.getInstance().encodePin(PFLockScreenFragment.this.getContext(), PFLockScreenFragment.this.mCode);
                if (PFLockScreenFragment.this.mCodeCreateListener != null) {
                    PFLockScreenFragment.this.mCodeCreateListener.onCodeCreated(encodedCode);
                }
            } catch (PFSecurityException e) {
                e.printStackTrace();
                Log.d(PFLockScreenFragment.TAG, "Can not encode pin code");
                PFLockScreenFragment.this.deleteEncodeKey();
            }
        }
    }

    public interface OnPFLockScreenCodeCreateListener {
        void onCodeCreated( String str );
    }

    public interface OnPFLockScreenLoginListener {
        void onCodeInputSuccessful( );

        void onFingerprintLoginFailed( );

        void onFingerprintSuccessful( );

        void onPinLoginFailed( );
    }

    /* renamed from: com.beautycoder.pflockscreen.fragments.PFLockScreenFragment$6 */
    class C02536 implements OnPFCodeListener {
        C02536() {
        }

        public void onCodeCompleted(String code) {
            if (PFLockScreenFragment.this.mIsCreateMode) {
                PFLockScreenFragment.this.mNextButton.setVisibility(0);
                PFLockScreenFragment.this.mCode = code;
                return;
            }
            PFLockScreenFragment.this.mCode = code;
            try {
                boolean isCorrect = PFFingerprintPinCodeHelper.getInstance().checkPin(PFLockScreenFragment.this.getContext(), PFLockScreenFragment.this.mEncodedPinCode, PFLockScreenFragment.this.mCode);
                if (PFLockScreenFragment.this.mLoginListener != null) {
                    if (isCorrect) {
                        PFLockScreenFragment.this.mLoginListener.onCodeInputSuccessful();
                    } else {
                        PFLockScreenFragment.this.mLoginListener.onPinLoginFailed();
                        PFLockScreenFragment.this.errorAction();
                    }
                }
                if (!isCorrect && PFLockScreenFragment.this.mConfiguration.isClearCodeOnError()) {
                    PFLockScreenFragment.this.mCodeView.clearCode();
                }
            } catch (PFSecurityException e) {
                e.printStackTrace();
            }
        }

        public void onCodeNotCompleted(String code) {
            if (PFLockScreenFragment.this.mIsCreateMode) {
                PFLockScreenFragment.this.mNextButton.setVisibility(8);
            }
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_lock_screen_pf, container, false);
        this.mFingerprintButton = view.findViewById( R.id.button_finger_print);
        this.mDeleteButton = view.findViewById( R.id.button_delete);
        this.mLeftButton = (TextView) view.findViewById( R.id.button_left);
        this.mNextButton = (Button) view.findViewById( R.id.button_next);
        this.mDeleteButton.setOnClickListener(this.mOnDeleteButtonClickListener);
        this.mDeleteButton.setOnLongClickListener(this.mOnDeleteButtonOnLongClickListener);
        this.mFingerprintButton.setOnClickListener(this.mOnFingerprintClickListener);
        this.mCodeView = (PFCodeView) view.findViewById( R.id.code_view);
        initKeyViews(view);
        this.mCodeView.setListener(this.mCodeListener);
        if (!this.mUseFingerPrint) {
            this.mFingerprintButton.setVisibility(8);
        }
        this.mFingerprintHardwareDetected = isFingerprintApiAvailable(getContext());
        this.mRootView = view;
        applyConfiguration(this.mConfiguration);
        return view;
    }

    public void onStart() {
        if (!this.mIsCreateMode && this.mUseFingerPrint && this.mConfiguration.isAutoShowFingerprint() && isFingerprintApiAvailable(getActivity()) && isFingerprintsExists(getActivity())) {
            this.mOnFingerprintClickListener.onClick(this.mFingerprintButton);
        }
        super.onStart();
    }

    public void setConfiguration(PFFLockScreenConfiguration configuration) {
        this.mConfiguration = configuration;
        applyConfiguration(configuration);
    }

    @SuppressLint( "WrongConstant" )
    private void applyConfiguration( PFFLockScreenConfiguration configuration) {
        if (this.mRootView != null) {
            if (configuration != null) {
                ((TextView) this.mRootView.findViewById( R.id.title_text_view)).setText(configuration.getTitle());
                if (TextUtils.isEmpty(configuration.getLeftButton())) {
                    this.mLeftButton.setVisibility(8);
                } else {
                    this.mLeftButton.setText(configuration.getLeftButton());
                    this.mLeftButton.setOnClickListener(configuration.getOnLeftButtonClickListener());
                }
                if (!TextUtils.isEmpty(configuration.getNextButton())) {
                    this.mNextButton.setText(configuration.getNextButton());
                }
                this.mUseFingerPrint = configuration.isUseFingerprint();
                boolean z = false;
                if (!this.mUseFingerPrint) {
                    this.mFingerprintButton.setVisibility(8);
                    this.mDeleteButton.setVisibility(0);
                }
                if (this.mConfiguration.getMode() == 0) {
                    z = true;
                }
                this.mIsCreateMode = z;
                if (this.mIsCreateMode) {
                    this.mLeftButton.setVisibility(8);
                    this.mFingerprintButton.setVisibility(8);
                }
                if (this.mIsCreateMode) {
                    this.mNextButton.setOnClickListener(this.mOnNextButtonClickListener);
                } else {
                    this.mNextButton.setOnClickListener(null);
                }
                this.mCodeView.setCodeLength(this.mConfiguration.getCodeLength());
            }
        }
    }

    private void initKeyViews(View parent) {
        parent.findViewById( R.id.button_0).setOnClickListener(this.mOnKeyClickListener);
        parent.findViewById( R.id.button_1).setOnClickListener(this.mOnKeyClickListener);
        parent.findViewById( R.id.button_2).setOnClickListener(this.mOnKeyClickListener);
        parent.findViewById( R.id.button_3).setOnClickListener(this.mOnKeyClickListener);
        parent.findViewById( R.id.button_4).setOnClickListener(this.mOnKeyClickListener);
        parent.findViewById( R.id.button_5).setOnClickListener(this.mOnKeyClickListener);
        parent.findViewById( R.id.button_6).setOnClickListener(this.mOnKeyClickListener);
        parent.findViewById( R.id.button_7).setOnClickListener(this.mOnKeyClickListener);
        parent.findViewById( R.id.button_8).setOnClickListener(this.mOnKeyClickListener);
        parent.findViewById( R.id.button_9).setOnClickListener(this.mOnKeyClickListener);
    }

    @SuppressLint( "WrongConstant" )
    private void configureRightButton( int codeLength) {
        if (this.mIsCreateMode) {
            if (codeLength > 0) {
                this.mDeleteButton.setVisibility(0);
            } else {
                this.mDeleteButton.setVisibility(8);
            }
        } else if (codeLength > 0) {
            this.mFingerprintButton.setVisibility(8);
            this.mDeleteButton.setVisibility(0);
            this.mDeleteButton.setEnabled(true);
        } else {
            if (this.mUseFingerPrint && this.mFingerprintHardwareDetected) {
                this.mFingerprintButton.setVisibility(0);
                this.mDeleteButton.setVisibility(8);
            } else {
                this.mFingerprintButton.setVisibility(8);
                this.mDeleteButton.setVisibility(0);
            }
            this.mDeleteButton.setEnabled(false);
        }
    }

    private boolean isFingerprintApiAvailable(Context context) {
        return FingerprintManagerCompat.from(context).isHardwareDetected();
    }

    private boolean isFingerprintsExists(Context context) {
        return FingerprintManagerCompat.from(context).hasEnrolledFingerprints();
    }

    private void showNoFingerprintDialog() {
        new Builder(getContext()).setTitle( "No fingerprint title").setMessage( "Text").setCancelable(true).setNegativeButton( "Cancel", null).setPositiveButton( "Setting", new C02065()).create().show();
    }

    private void deleteEncodeKey() {
        try {
            PFFingerprintPinCodeHelper.getInstance().delete();
        } catch (PFSecurityException e) {
            e.printStackTrace();
            Log.d(TAG, "Can not delete the alias");
        }
    }

    private void errorAction() {
        @SuppressLint( "WrongConstant" ) Vibrator v = (Vibrator) getContext().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(400);
        }
//        this.mCodeView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_pf));
    }

    public void setCodeCreateListener(OnPFLockScreenCodeCreateListener listener) {
        this.mCodeCreateListener = listener;
    }

    public void setLoginListener(OnPFLockScreenLoginListener listener) {
        this.mLoginListener = listener;
    }

    public void setEncodedPinCode(String encodedPinCode) {
        this.mEncodedPinCode = encodedPinCode;
    }
}
