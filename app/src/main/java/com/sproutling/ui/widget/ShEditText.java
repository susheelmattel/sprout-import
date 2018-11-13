/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.MetricAffectingSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sproutling.R;
import com.sproutling.utils.Utils;

import java.util.ArrayList;

import static android.text.InputType.TYPE_CLASS_DATETIME;
import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_PHONE;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_DATETIME_VARIATION_DATE;
import static android.text.InputType.TYPE_DATETIME_VARIATION_NORMAL;
import static android.text.InputType.TYPE_DATETIME_VARIATION_TIME;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;
import static android.text.InputType.TYPE_NUMBER_VARIATION_NORMAL;
import static android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE;
import static android.text.InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;
import static android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
import static android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
import static android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS;
import static android.text.InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE;
import static android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE;
import static android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT;
import static android.text.InputType.TYPE_TEXT_VARIATION_FILTER;
import static android.text.InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE;
import static android.text.InputType.TYPE_TEXT_VARIATION_NORMAL;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME;
import static android.text.InputType.TYPE_TEXT_VARIATION_PHONETIC;
import static android.text.InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS;
import static android.text.InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE;
import static android.text.InputType.TYPE_TEXT_VARIATION_URI;
import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS;
import static android.text.InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD;


/**
 * Created by subram13 on 2/23/17.
 */

public class ShEditText extends RelativeLayout {
    private Context mContext;
    private RelativeLayout mRelativeLayout;
    private TextInputLayout mTextInputLayout;
    private TextInputEditText mTextInputEditText;
    private Drawable mImgLeftSrc;
    private Drawable mImgRightSrc;
    private Drawable mFocusImg;
    private Drawable mErrorImg;
    private String mHintText;
    private String mInputType;
    private boolean mIsPassword;
    private boolean mIsSingleLine;
    private boolean mIsShowBg;
    private boolean mIsEditable;
    private float mTextSize;
    private OnFocusChangeListener mFocusChangeListener;
    private OnClickListener mOnClickListener;
    private String mErrorMsg;
    private ArrayList<TextWatcher> mTextWatchers;
    private boolean mIsErrorShown;

    public ShEditText(Context context) {
        super(context);
        initView(context, null);
    }

    public ShEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ShEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public ShEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context ctx, AttributeSet attrs) {
        LayoutInflater.from(ctx).inflate(R.layout.widget_sh_edit_text, this);
        mContext = ctx;
        if (attrs != null) {
            TypedArray styledAttributes = mContext.obtainStyledAttributes(attrs, R.styleable.ShEditText);
            mIsPassword = styledAttributes.getBoolean(R.styleable.ShEditText_password, false);
            mIsSingleLine = styledAttributes.getBoolean(R.styleable.ShEditText_singleLine, true);
            mIsShowBg = styledAttributes.getBoolean(R.styleable.ShEditText_showSquareBg, false);
            mImgLeftSrc = styledAttributes.getDrawable(R.styleable.ShEditText_imgLeft);
            mImgRightSrc = styledAttributes.getDrawable(R.styleable.ShEditText_imgRight);
            mFocusImg = styledAttributes.getDrawable(R.styleable.ShEditText_focusImg);
            mErrorImg = styledAttributes.getDrawable(R.styleable.ShEditText_errorImg);
            mInputType = styledAttributes.getString(R.styleable.ShEditText_inputType);
            mErrorMsg = styledAttributes.getString(R.styleable.ShEditText_errorText);
            mHintText = styledAttributes.getString(R.styleable.ShEditText_hintText);
            mTextSize = styledAttributes.getDimension(R.styleable.ShEditText_textSize, getResources().getDimension(R.dimen.textS));
            styledAttributes.recycle();
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        mRelativeLayout = (RelativeLayout) findViewById(R.id.root_layout);
        mTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_layout);
        mTextInputEditText = (TextInputEditText) findViewById(R.id.txt_input_et);
        mTextInputEditText.setTypeface(ShTextView.TypeFaces.get(mContext, ShTextView.TypeFaces.LONDON));
        mTextInputEditText.setTextColor(Utils.getColor(mContext, R.color.dolphin));
        mTextInputEditText.setHintTextColor(Utils.getColor(mContext, R.color.fog));
        setTextSize();
        setUpControls();
    }

    private void setUpControls() {

        setImg(mImgLeftSrc, mImgRightSrc);

        mTextInputEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mFocusChangeListener != null) {
                    view.setId(getId());
                    mFocusChangeListener.onFocusChange(view, b);
                }
                if (b) {
                    if (mFocusImg != null) {
                        setImg(mFocusImg, mImgRightSrc);
                    }
                    setBackground(mIsShowBg ? R.drawable.bg_sh_edit_text_teal : R.drawable.bg_sh_edit_text_line_teal);

                    //When we click the UI, it first calls the focus and then in the next click it calls the onclick, to fix this the onclick is also called here if needed
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick(mTextInputEditText);
                    }
                } else {
                    if (mImgLeftSrc != null) {
                        setImg(mImgLeftSrc, mImgRightSrc);
                    }
                    setBackground(mIsShowBg ? R.drawable.bg_sh_edit_text_grey : R.drawable.bg_sh_edit_text_line_grey);
                }
                if (mIsErrorShown) {
                    setImg(mErrorImg, mImgRightSrc);
                }
            }
        });
        mTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int before, int after) {
                if (mTextWatchers != null) {
                    final ArrayList<TextWatcher> list = mTextWatchers;
                    final int count = list.size();
                    for (int i = 0; i < count; i++) {
                        list.get(i).beforeTextChanged(text, start, before, after);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int after) {
                if (mTextWatchers != null) {
                    final ArrayList<TextWatcher> list = mTextWatchers;
                    final int count = list.size();
                    for (int i = 0; i < count; i++) {
                        list.get(i).onTextChanged(text, start, before, after);
                    }
                }
                if (mIsErrorShown) {
                    showError(false);
                    setImg(mFocusImg, mImgRightSrc);
                    setBackground(mIsShowBg ? R.drawable.bg_sh_edit_text_teal : R.drawable.bg_sh_edit_text_line_teal);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mTextWatchers != null) {
                    final ArrayList<TextWatcher> list = mTextWatchers;
                    final int count = list.size();
                    for (int i = 0; i < count; i++) {
                        list.get(i).afterTextChanged(editable);
                    }
                }
            }
        });
        mTextInputEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(view);
                }
            }
        });
        mRelativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(view);
                }
            }
        });
        if (!TextUtils.isEmpty(mInputType)) {
            mTextInputEditText.setInputType(getInputType());
        }
        mTextInputEditText.setSingleLine(mIsSingleLine);
        setHintText(mHintText);
        showBackground(mIsShowBg);
        //if focus img or error img not set then set it to img left.
        if (mFocusImg == null) {
            mFocusImg = mImgLeftSrc;
        }
        if (mErrorImg == null) {
            mErrorImg = mImgLeftSrc;
        }
        setInputType();
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener listener) {
        mTextInputEditText.setOnEditorActionListener(listener);
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        if (!isClickable()) {
            setClickable(true);
        }
        if (mTextInputEditText.isClickable()) {
            mTextInputEditText.setClickable(true);
        }
        mOnClickListener = onClickListener;
    }

    public void setSingleLine(boolean singleLine) {
        mIsSingleLine = singleLine;
        mTextInputEditText.setSingleLine(mIsSingleLine);
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        setTextSize();
    }

    private void setTextSize() {
        float density = getResources().getDisplayMetrics().density;
        mTextInputEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize / density);
    }

    public void setHintText(String hintText) {
        mHintText = hintText;
        mTextInputEditText.setHint(hintText);
    }

    public void showBackground(boolean show) {
        mIsShowBg = show;
        setBackground(show ? R.drawable.bg_sh_edit_text_grey : R.drawable.bg_sh_edit_text_line_grey);
    }

    private void setBackground(int bgSrc) {
        mTextInputEditText.setBackground(ContextCompat.getDrawable(mContext, bgSrc));
    }

    public void setTextInputLayout(int inputType) {
        mTextInputEditText.setInputType(inputType);
    }


    private void setInputType() {
        if (mIsPassword) {
            mTextInputEditText.setInputType(TYPE_CLASS_TEXT |
                    TYPE_TEXT_VARIATION_PASSWORD);
            mTextInputEditText.setTypeface(ShTextView.TypeFaces.get(mContext, ShTextView.TypeFaces.LONDON));
            mTextInputEditText.setTransformationMethod(new PasswordTransformationMethod());
        }
    }

    public void setInputType(int type) {
        mTextInputEditText.setInputType(type);
    }

    public void setOnFocusChangeListener(OnFocusChangeListener focusChangeListener) {
        mFocusChangeListener = focusChangeListener;
    }

    public void setFocusImg(Drawable focusImg) {
        mFocusImg = focusImg;
    }

    public void setErrorImg(Drawable errorImg) {
        mErrorImg = errorImg;
    }

    public void setPassword(boolean password) {
        mIsPassword = password;
        setInputType();
    }

    public void setImgLeftSrc(Drawable imgLeftSrc) {
        mImgLeftSrc = imgLeftSrc;
        setImg(mImgLeftSrc, mImgRightSrc);
    }

    public void setImgRightSrc(Drawable imgRightSrc) {
        mImgRightSrc = imgRightSrc;
        setImg(mImgLeftSrc, mImgRightSrc);
    }


    public void setCompoundDrawablesWithIntrinsicBounds(Drawable imgLeftSrc, Drawable imgRightSrc) {
        mImgRightSrc = imgRightSrc;
        mImgLeftSrc = imgLeftSrc;
        setImg(mImgLeftSrc, mImgRightSrc);
    }

    private void setImg(Drawable imgLeftSrc, Drawable imgRightSrc) {
        mTextInputEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(imgLeftSrc, null, imgRightSrc, null);
    }


    public void setText(String value) {
        mTextInputEditText.setText(value);
    }

    public String getText() {
        return mTextInputEditText.getText().toString();
    }

    public void setError(String errorMsg) {
        mErrorMsg = errorMsg;
    }

    public void showError(boolean show) {
        mIsErrorShown = show;
        if (show) {
            if (mErrorMsg != null) {
                setImg(mErrorImg, mImgRightSrc);
                SpannableString spannableString = new SpannableString(mErrorMsg);
                spannableString.setSpan(new TypefaceSpan(ShTextView.TypeFaces.get(mContext, ShTextView.TypeFaces.LONDON)),
                        0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTextInputLayout.setError(spannableString);
            }
        } else {
            mTextInputLayout.setError(null);
        }
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        if (mTextWatchers == null) {
            mTextWatchers = new ArrayList<>();
        }
        mTextWatchers.add(textWatcher);
    }

    private class TypefaceSpan extends MetricAffectingSpan {
        private Typeface mTypeface;

        public TypefaceSpan(Typeface typeface) {
            mTypeface = typeface;
        }

        @Override
        public void updateMeasureState(TextPaint p) {
            p.setTypeface(mTypeface);
            p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            tp.setTypeface(mTypeface);
            tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
    }

    private int getInputType() {
        int retVal;
        switch (mInputType) {
            case "0":
                retVal = 0;
                break;
            case "1":
                retVal = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_NORMAL;
                break;
            case "2":
                retVal = TYPE_TEXT_FLAG_CAP_CHARACTERS;
                break;
            case "3":
                retVal = TYPE_TEXT_FLAG_CAP_WORDS;
                break;
            case "4":
                retVal = TYPE_TEXT_FLAG_CAP_SENTENCES;
                break;
            case "5":
                retVal = TYPE_TEXT_FLAG_AUTO_CORRECT;
                break;
            case "6":
                retVal = TYPE_TEXT_FLAG_AUTO_COMPLETE;
                break;
            case "7":
                retVal = TYPE_TEXT_FLAG_MULTI_LINE;
                break;
            case "8":
                retVal = TYPE_TEXT_FLAG_IME_MULTI_LINE;
                break;
            case "9":
                retVal = TYPE_TEXT_FLAG_NO_SUGGESTIONS;
                break;
            case "10":
                retVal = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_URI;
                break;
            case "11":
                retVal = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
                break;
            case "12":
                retVal = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_SUBJECT;
                break;
            case "13":
                retVal = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_SHORT_MESSAGE;
                break;
            case "14":
                retVal = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_LONG_MESSAGE;
                break;
            case "15":
                retVal = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PERSON_NAME;
                break;
            case "16":
                retVal = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_POSTAL_ADDRESS;
                break;
            case "17":
                retVal = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD;
                break;
            case "18":
                retVal = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
                break;
            case "19":
                retVal = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_WEB_EDIT_TEXT;
                break;
            case "20":
                retVal = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_FILTER;
                break;
            case "21":
                retVal = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PHONETIC;
                break;
            case "22":
                retVal = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS;
                break;
            case "23":
                retVal = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_WEB_PASSWORD;
                break;
            case "24":
                retVal = TYPE_CLASS_NUMBER | TYPE_NUMBER_VARIATION_NORMAL;
                break;
            case "25":
                retVal = TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_SIGNED;
                break;
            case "26":
                retVal = TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL;
                break;
            case "27":
                retVal = TYPE_CLASS_NUMBER | TYPE_NUMBER_VARIATION_PASSWORD;
                break;
            case "28":
                retVal = TYPE_CLASS_PHONE;
                break;
            case "29":
                retVal = TYPE_CLASS_DATETIME | TYPE_DATETIME_VARIATION_NORMAL;
                break;
            case "30":
                retVal = TYPE_CLASS_DATETIME | TYPE_DATETIME_VARIATION_DATE;
                break;
            case "31":
                retVal = TYPE_CLASS_DATETIME | TYPE_DATETIME_VARIATION_TIME;
                break;
            default:
                retVal = 0;
                break;
        }
        return retVal;
    }

}
