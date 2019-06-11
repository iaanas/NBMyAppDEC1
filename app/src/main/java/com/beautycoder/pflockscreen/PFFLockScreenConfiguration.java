package com.beautycoder.pflockscreen;

import android.content.Context;
import android.view.View.OnClickListener;

import com.example.xavier.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PFFLockScreenConfiguration {
    public static final int MODE_AUTH = 1;
    public static final int MODE_CREATE = 0;
    private boolean mAutoShowFingerprint;
    private boolean mClearCodeOnError;
    private int mCodeLength;
    private String mLeftButton;
    private int mMode;
    private String mNextButton;
    private OnClickListener mOnLeftButtonClickListener;
    private String mTitle;
    private boolean mUseFingerprint;
    
    public PFFLockScreenConfiguration( ) {
    
    }
    
    public static class Builder {
        private boolean mAutoShowFingerprint = false;
        private boolean mClearCodeOnError = false;
        private int mCodeLength = 4;
        private String mLeftButton = "";
        private int mMode = 0;
        private String mNextButton = "";
        private OnClickListener mOnLeftButtonClickListener = null;
        private String mTitle = "";
        private boolean mUseFingerprint = false;

        public Builder(Context context) {
//            this.mTitle = context.getResources().getString( "Text");
        }

        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setLeftButton(String leftButton, OnClickListener onClickListener) {
            this.mLeftButton = leftButton;
            this.mOnLeftButtonClickListener = onClickListener;
            return this;
        }

        public Builder setNextButton(String nextButton) {
            this.mNextButton = nextButton;
            return this;
        }

        public Builder setUseFingerprint(boolean useFingerprint) {
            this.mUseFingerprint = useFingerprint;
            return this;
        }

        public Builder setAutoShowFingerprint(boolean autoShowFingerprint) {
            this.mAutoShowFingerprint = autoShowFingerprint;
            return this;
        }

        public Builder setMode(int mode) {
            this.mMode = mode;
            return this;
        }

        public Builder setCodeLength(int codeLength) {
            this.mCodeLength = codeLength;
            return this;
        }

        public Builder setClearCodeOnError(boolean clearCodeOnError) {
            this.mClearCodeOnError = clearCodeOnError;
            return this;
        }

        public PFFLockScreenConfiguration build() {
            return new PFFLockScreenConfiguration();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PFLockScreenMode {
    }

    private PFFLockScreenConfiguration(Builder builder) {
        this.mLeftButton = "";
        this.mNextButton = "";
        this.mOnLeftButtonClickListener = null;
        this.mUseFingerprint = false;
        this.mAutoShowFingerprint = false;
        this.mTitle = "";
        this.mMode = 1;
        this.mCodeLength = 4;
        this.mClearCodeOnError = false;
        this.mLeftButton = builder.mLeftButton;
        this.mNextButton = builder.mNextButton;
        this.mUseFingerprint = builder.mUseFingerprint;
        this.mAutoShowFingerprint = builder.mAutoShowFingerprint;
        this.mTitle = builder.mTitle;
        this.mOnLeftButtonClickListener = builder.mOnLeftButtonClickListener;
        this.mMode = builder.mMode;
        this.mCodeLength = builder.mCodeLength;
        this.mClearCodeOnError = builder.mClearCodeOnError;
    }

    public String getLeftButton() {
        return this.mLeftButton;
    }

    public String getNextButton() {
        return this.mNextButton;
    }

    public boolean isUseFingerprint() {
        return this.mUseFingerprint;
    }

    public boolean isAutoShowFingerprint() {
        return this.mAutoShowFingerprint;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public OnClickListener getOnLeftButtonClickListener() {
        return this.mOnLeftButtonClickListener;
    }

    public int getCodeLength() {
        return this.mCodeLength;
    }

    public boolean isClearCodeOnError() {
        return this.mClearCodeOnError;
    }

    public int getMode() {
        return this.mMode;
    }
}
