package com.beautycoder.pflockscreen.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.xavier.R;

import java.util.ArrayList;
import java.util.List;

public class PFCodeView extends LinearLayout {
    private static final int DEFAULT_CODE_LENGTH = 4;
    private String mCode = "";
    private int mCodeLength = 4;
    List<CheckBox> mCodeViews = new ArrayList();
    private OnPFCodeListener mListener;

    public interface OnPFCodeListener {
        void onCodeCompleted( String str );

        void onCodeNotCompleted( String str );
    }

    public PFCodeView(Context context) {
        super(context);
        init();
    }

    public PFCodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PFCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_code_pf_lockscreen, this);
        setUpCodeViews();
    }

    public void setCodeLength(int codeLength) {
        this.mCodeLength = codeLength;
        setUpCodeViews();
    }

    private void setUpCodeViews() {
        removeAllViews();
        this.mCodeViews.clear();
        this.mCode = "";
        for (int i = 0; i < this.mCodeLength; i++) {
            @SuppressLint( "WrongConstant" ) CheckBox view = (CheckBox) ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate( R.layout.view_pf_code_checkbox, null);
            LayoutParams layoutParams = new LayoutParams(-2, -2);
            int margin = getResources().getDimensionPixelSize( R.dimen.code_fp_margin);
            layoutParams.setMargins(margin, margin, margin, margin);
            view.setLayoutParams(layoutParams);
            view.setChecked(false);
            addView(view);
            this.mCodeViews.add(view);
        }
        if (this.mListener != null) {
            this.mListener.onCodeNotCompleted("");
        }
    }

    public int input(String number) {
        if (this.mCode.length() == this.mCodeLength) {
            return this.mCode.length();
        }
        ((CheckBox) this.mCodeViews.get(this.mCode.length())).toggle();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.mCode);
        stringBuilder.append(number);
        this.mCode = stringBuilder.toString();
        if (this.mCode.length() == this.mCodeLength && this.mListener != null) {
            this.mListener.onCodeCompleted(this.mCode);
        }
        return this.mCode.length();
    }

    public int delete() {
        if (this.mListener != null) {
            this.mListener.onCodeNotCompleted(this.mCode);
        }
        if (this.mCode.length() == 0) {
            return this.mCode.length();
        }
        this.mCode = this.mCode.substring(0, this.mCode.length() - 1);
        ((CheckBox) this.mCodeViews.get(this.mCode.length())).toggle();
        return this.mCode.length();
    }

    public void clearCode() {
        if (this.mListener != null) {
            this.mListener.onCodeNotCompleted(this.mCode);
        }
        this.mCode = "";
        for (CheckBox codeView : this.mCodeViews) {
            codeView.setChecked(false);
        }
    }

    public int getInputCodeLength() {
        return this.mCode.length();
    }

    public String getCode() {
        return this.mCode;
    }

    public void setListener(OnPFCodeListener listener) {
        this.mListener = listener;
    }
}
