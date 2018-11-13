/*
 * Copyright (c) 2017 Fuhu, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fuhu.states.payloads.Payload;
import com.sproutling.App;
import com.sproutling.R;
import com.sproutling.api.SproutlingApi;
import com.sproutling.pojos.CreatePhotoResponse;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.states.Actions;
import com.sproutling.states.States;
import com.sproutling.ui.activity.AvatarActivity;
import com.sproutling.ui.dialogfragment.GenericErrorDialogFragment;
import com.sproutling.ui.dialogfragment.WhyDateDialogFragment;
import com.sproutling.ui.widget.CustomShEditText;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.sproutling.ui.activity.AvatarActivity.EXTRA_DST;
import static com.sproutling.ui.activity.AvatarActivity.EXTRA_IMAGE_SIZE;
import static com.sproutling.ui.activity.AvatarActivity.EXTRA_MODE;
import static com.sproutling.ui.activity.AvatarActivity.EXTRA_SRC;
import static com.sproutling.ui.activity.AvatarActivity.MODE_URI;
import static com.sproutling.utils.LogEvents.CHILD_PROFILE_PHOTO_UPLOADED;

/**
 * Created by bradylin on 7/18/17.
 */

public class BabyFragment extends BaseFragment {

    public static final String TAG = "BabyFragment";

    public static final String EXTRA_MODE_BABY = "extra_mode";
    public static final String MODE_SETTINGS = "mode_settings";
    public static final String MODE_SETUP = "mode_setup";
    public static final String MODE_DEFAULT = MODE_SETUP;

    private static final int PERMISSION_REQUEST_CAMERA = 16;
    private static final int PERMISSION_REQUEST_LIBRARY = 12;
    private static final int PHOTO_REQUEST_TAKE_PHOTO = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CROP = 6;

    private static final String FORMAT_DATE = "yyyy-MM-dd";

    private static final int DUE_DATE_DAYS_AFTER = 20 * 7; // 20 weeks
    private static final int DUE_DATE_DAYS_BEFORE = -6 * 7; // 6 weeks

    private File mChildPhotoFile;
    private Uri mPicUri;

    private SSManagement.Child mChild;

    private OnBabyListener mListener;

    private ImageView mChildPhotoView;
    private RadioGroup mGenderGroup;
    private RadioButton mGirlButton, mBoyButton;
    private ShTextView mAddPhotoView;
    private ShTextView mGenderSelectView;
    private ShTextView mWhyView;
    private CustomShEditText mNameEditText, mBirthdayEditText, mDueDateEditText;
    private Calendar mBirthdayCalendar, mDueDateCalendar;
    private String mBirthday, mDueDate;

    private String mGender = SSManagement.Child.GENDER_GIRL;
    private SimpleDateFormat mDateFormat;

    private String mMode = MODE_DEFAULT;

    public BabyFragment() {
    }

    public static BabyFragment newInstance(String mode) {
        BabyFragment fragment = new BabyFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_MODE_BABY, mode);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        mChildPhotoFile = new File(getActivity().getFilesDir(), Utils.getChildPhotoFileName());
        mDateFormat = new SimpleDateFormat(FORMAT_DATE, Locale.US);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMode = arguments.getString(EXTRA_MODE_BABY);
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_add_child, container, false);

        mChildPhotoView = (ImageView) view.findViewById(R.id.photo);
        mGenderGroup = (RadioGroup) view.findViewById(R.id.gender_group);
        mGirlButton = (RadioButton) view.findViewById(R.id.girl);
        mBoyButton = (RadioButton) view.findViewById(R.id.boy);
        mAddPhotoView = (ShTextView) view.findViewById(R.id.add_photo);
        mGenderSelectView = (ShTextView) view.findViewById(R.id.select_gender);
        mNameEditText = view.findViewById(R.id.name);
        mBirthdayEditText = view.findViewById(R.id.birthday);
        mDueDateEditText = view.findViewById(R.id.due_date);
        mWhyView = (ShTextView) view.findViewById(R.id.why);

        mChildPhotoView.setOnClickListener(mOnChildPhotoViewClickListener);
        mGenderGroup.setOnCheckedChangeListener(mOnCheckedChangedListener);
        mAddPhotoView.setOnClickListener(mOnChildPhotoViewClickListener);
        mWhyView.setOnClickListener(mOnWhyClickListener);

        mNameEditText.setOnFocusChangeListener(mOnFocusChangeListener);
        mBirthdayEditText.setOnFocusChangeListener(mOnFocusChangeListener);
        mDueDateEditText.setOnFocusChangeListener(mOnFocusChangeListener);

        mNameEditText.addTextChangedListener(mTextWatcher);
        mBirthdayEditText.addTextChangedListener(mTextWatcher);
        mDueDateEditText.addTextChangedListener(mTextWatcher);

//        mBirthdayEditText.setOnClickListener(mBirthDateOnClickListener);
//        mDueDateEditText.setOnClickListener(mDueDateOnClickListener);

//        mBirthdayEditText.setOnTouchListener(mOnBirthdayTouchListener);
//        mDueDateEditText.setOnTouchListener(mOnDueDateTouchListener);

        mBirthdayCalendar = Calendar.getInstance();
        mBirthdayCalendar.setTimeInMillis(System.currentTimeMillis());
        mDueDateCalendar = Calendar.getInstance();
        mDueDateCalendar.setTimeInMillis(System.currentTimeMillis());

        mNameEditText.setOnEditorActionListener(mOnEditorActionListener);

        if (MODE_SETTINGS.equals(mMode)) initBabyData();

        return view;
    }

    private void fillViews() {
        if (mChild.isMale()) {
            mBoyButton.performClick();
        } else {
            mGirlButton.performClick();
        }
        mGenderSelectView.setTextColor(getResources().getColor(R.color.fog));
        mNameEditText.setText(mChild.firstName);

        try {
            Date date = mDateFormat.parse(mChild.birthDate);
            mBirthdayCalendar = Calendar.getInstance();
            mBirthdayCalendar.setTime(date);
            String month = mBirthdayCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
            mBirthdayEditText.setText(month + " " + mBirthdayCalendar.get(Calendar.DAY_OF_MONTH) + ", " + mBirthdayCalendar.get(Calendar.YEAR));

            date = mDateFormat.parse(mChild.dueDate);
            mDueDateCalendar = Calendar.getInstance();
            mDueDateCalendar.setTime(date);
            month = mDueDateCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
            mDueDateEditText.setText(month + " " + mDueDateCalendar.get(Calendar.DAY_OF_MONTH) + ", " + mDueDateCalendar.get(Calendar.YEAR));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initOnBabyListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initOnBabyListener();
    }

    private void initOnBabyListener() {
        try {
            mListener = (OnBabyListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnBabyListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void showDatePicker(int year, int month, int dayOfMonth, DatePickerDialog.OnDateSetListener onDateSetListener) {
        new DatePickerDialog(getActivity(), onDateSetListener, year, month, dayOfMonth).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    zoom(mPicUri.toString(), mChildPhotoFile.getAbsolutePath(), mChildPhotoView.getMeasuredWidth());
                }
                break;

            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    mPicUri = data.getData();
                    zoom(mPicUri.toString(), mChildPhotoFile.getAbsolutePath(), mChildPhotoView.getMeasuredWidth());
                }
                break;

            case PHOTO_REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                                Uri.fromFile(mChildPhotoFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        mChildPhotoView.setImageBitmap(bitmap);
                    }
                }
                break;
            default:
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private View.OnClickListener mOnChildPhotoViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPhotoOptions();
        }
    };

    private void showPhotoOptions() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.popup_window_setup_add_child, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        ShTextView tvTakePhoto = (ShTextView) view.findViewById(R.id.take_photo);
        ShTextView tvChooseFromLibrary = (ShTextView) view.findViewById(R.id.choose_from_library);
        ShTextView tvCancel = (ShTextView) view.findViewById(R.id.cancel);
        tvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
                dialog.dismiss();
            }
        });
        tvChooseFromLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFromLibrary();
                dialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    public void chooseFromLibrary() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_LIBRARY);
            } else {
                showPhotoLibrary();
            }

        } else {
            showPhotoLibrary();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                }
                break;
            case PERMISSION_REQUEST_LIBRARY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPhotoLibrary();
                }
                break;
            default:
                break;
        }
    }

    private void showPhotoLibrary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PHOTO_REQUEST_GALLERY);
    }

    public void takePhoto() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA);
            } else {
                startCamera();
            }
        } else {
            startCamera();
        }
    }

    private void startCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
            File imageFile = new File(imageFilePath);
            mPicUri = Uri.fromFile(imageFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mPicUri);
            startActivityForResult(intent, PHOTO_REQUEST_TAKE_PHOTO);
        } catch (ActivityNotFoundException anfe) {
            // TODO: error handling
            String errorMessage = "Your device doesn't support capturing images!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.girl:
                    mGender = SSManagement.Child.GENDER_GIRL;
                    mGirlButton.setChecked(true);
                    mBoyButton.setChecked(false);
                    mGenderSelectView.setTextColor(getResources().getColor(R.color.fog));
                    break;
                case R.id.boy:
                    mGender = SSManagement.Child.GENDER_BOY;
                    mGirlButton.setChecked(false);
                    mBoyButton.setChecked(true);
                    mGenderSelectView.setTextColor(getResources().getColor(R.color.fog));
                    break;
            }
        }
    };

    private View.OnClickListener mOnWhyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            WhyDateDialogFragment
                    .newInstance()
                    .show(getFragmentManager(), null);
        }
    };

    private TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyboard();
            }
            return true;
        }
    };

    private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.name:
                    if (!hasFocus) {
                        hideKeyboard();
                    }
                    break;
                case R.id.birthday:
                    if (hasFocus) {
                        hideKeyboard();
                        showDatePicker(mBirthdayCalendar.get(Calendar.YEAR), mBirthdayCalendar.get(Calendar.MONTH), mBirthdayCalendar.get(Calendar.DAY_OF_MONTH), mBirthdayOnDateSetListener);
                    }
                    break;
                case R.id.due_date:
                    if (hasFocus) {
                        hideKeyboard();
                        showDatePicker(mDueDateCalendar.get(Calendar.YEAR), mDueDateCalendar.get(Calendar.MONTH), mDueDateCalendar.get(Calendar.DAY_OF_MONTH), mDueOnDateSetListener);
                    }
                    break;
            }
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO: compare with saved data, if different, enable action button
            if (mChild != null) {
                if (mNameEditText.getText().length() > 0 &&
                        mBirthdayEditText.getText().length() > 0 &&
                        mDueDateEditText.getText().length() > 0 &&
                        isDateValid()) {
                    if (mListener != null) mListener.onActionButtonEnabled(TAG, true);
                } else if (mListener != null) mListener.onActionButtonEnabled(TAG, false);
            }
        }
    };

    private View.OnClickListener mBirthDateOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showDatePicker(mBirthdayCalendar.get(Calendar.YEAR), mBirthdayCalendar.get(Calendar.MONTH), mBirthdayCalendar.get(Calendar.DAY_OF_MONTH), mBirthdayOnDateSetListener);
        }
    };

    private View.OnClickListener mDueDateOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showDatePicker(mDueDateCalendar.get(Calendar.YEAR), mDueDateCalendar.get(Calendar.MONTH), mDueDateCalendar.get(Calendar.DAY_OF_MONTH), mDueOnDateSetListener);
        }
    };

    private View.OnTouchListener mOnBirthdayTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mBirthdayEditText.setInputType(InputType.TYPE_NULL); // disable soft input
            mBirthdayEditText.onTouchEvent(event); // call native handler
            return true; // consume touch event
        }
    };

    private View.OnTouchListener mOnDueDateTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mDueDateEditText.setInputType(InputType.TYPE_NULL); // disable soft input
            mDueDateEditText.onTouchEvent(event); // call native handler
            return true; // consume touch event
        }
    };

    private DatePickerDialog.OnDateSetListener mBirthdayOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            mBirthdayCalendar.set(Calendar.YEAR, year);
            mBirthdayCalendar.set(Calendar.MONTH, monthOfYear);
            mBirthdayCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String month = mBirthdayCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
            mBirthday = mDateFormat.format(mBirthdayCalendar.getTime());
            mBirthdayEditText.setText(month + " " + dayOfMonth + ", " + year);
//            mBirthdayEditText.setError(getString(R.string.setup_add_child_birthday_error_future));
//            mBirthdayEditText.showError(!isBirthdayValid());
        }
    };

    private DatePickerDialog.OnDateSetListener mDueOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            mDueDateCalendar.set(Calendar.YEAR, year);
            mDueDateCalendar.set(Calendar.MONTH, monthOfYear);
            mDueDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String month = mDueDateCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
            mDueDate = mDateFormat.format(mDueDateCalendar.getTime());
            mDueDateEditText.setText(month + " " + dayOfMonth + ", " + year);
        }
    };

    private boolean isBirthdayValid() {
        return mBirthdayCalendar.getTimeInMillis() < System.currentTimeMillis();
    }

    boolean isDateValid() {
//        if (!isBirthdayValid()) return false;
        long timeDiff = mDueDateCalendar.getTimeInMillis() - mBirthdayCalendar.getTimeInMillis();
        long day = 24 * 60 * 60 * 1000;

        if (timeDiff / day > DUE_DATE_DAYS_AFTER) {
            mDueDateEditText.setError(getString(R.string.setup_add_child_due_date_error_after));
            mDueDateEditText.showErrorMsg(true);
            return false;
        } else if (timeDiff / day < DUE_DATE_DAYS_BEFORE) {
            mDueDateEditText.setError(getString(R.string.setup_add_child_due_date_error_before));
            mDueDateEditText.showErrorMsg(true);
            return false;
        } else {
            mDueDateEditText.showErrorMsg(false);
            return true;
        }
    }

//    private void zoom(Bundle bundle, String imageDst, int imageSize) {
//        Intent intent = new Intent(getActivity(), AvatarActivity.class);
//        intent.putExtra(EXTRA_MODE, MODE_BITMAP);
//        intent.putExtra(EXTRA_BITMAP, bundle);
//        intent.putExtra(EXTRA_DST, imageDst);
//        intent.putExtra(EXTRA_IMAGE_SIZE, imageSize);
//        zoom(intent);
//    }

    private void zoom(String imageSrc, String imageDst, int imageSize) {
        Intent intent = new Intent(getActivity(), AvatarActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_URI);
        intent.putExtra(EXTRA_SRC, imageSrc);
        intent.putExtra(EXTRA_DST, imageDst);
        intent.putExtra(EXTRA_IMAGE_SIZE, imageSize);
        zoom(intent);
    }

    private void zoom(Intent intent) {
        startActivityForResult(intent, PHOTO_REQUEST_CROP);
    }

    boolean isFormReady() {
        boolean ready = true;
        if (mGenderGroup.getCheckedRadioButtonId() == -1) {
            mGenderSelectView.setTextColor(getResources().getColor(R.color.dark_poppy));
            ready = false;
        }
        if (mNameEditText.getText().toString().isEmpty()) {
            mNameEditText.showErrorMsg(true);
            ready = false;
        } else {
            mNameEditText.showErrorMsg(false);

        }
        if (mBirthday == null || mBirthday.isEmpty()) {
            mBirthdayEditText.setError(getString(R.string.setup_add_child_birthday_error));
            mBirthdayEditText.showErrorMsg(true);
            ready = false;
        } else {
            mBirthdayEditText.showErrorMsg(false);
        }
        long timeDiff = mDueDateCalendar.getTimeInMillis() - mBirthdayCalendar.getTimeInMillis();
        long day = 24 * 60 * 60 * 1000;
        if (mDueDate == null || mDueDate.isEmpty()) {
            mDueDateEditText.setError(getString(R.string.setup_add_child_due_date_error));
            mDueDateEditText.showErrorMsg(true);
            ready = false;
        } else if (timeDiff / day > DUE_DATE_DAYS_AFTER) {
            mDueDateEditText.setError(getString(R.string.setup_add_child_due_date_error_after));
            mDueDateEditText.showErrorMsg(true);
            ready = false;
        } else if (timeDiff / day < DUE_DATE_DAYS_BEFORE) {
            mDueDateEditText.setError(getString(R.string.setup_add_child_due_date_error_before));
            mDueDateEditText.showErrorMsg(true);

            ready = false;
        } else {
            mDueDateEditText.showErrorMsg(false);
        }
        return ready;
    }

    public void createChild() {
        if (!isFormReady()) {
            return;
        }
        final String firstName = mNameEditText.getText().toString();
        final String accessToken = AccountManagement.getInstance(getActivity()).getAccessToken();

        new AsyncTask<Void, Void, SSManagement.Child>() {
            SSError mError;

            @Override
            protected void onPreExecute() {
                showProgressBar(true);
            }

            @Override
            protected SSManagement.Child doInBackground(Void... params) {
                try {
                    return SKManagement.createChild(accessToken, firstName, "", mGender, mBirthday, mDueDate);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();

                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(SSManagement.Child child) {
                showProgressBar(false);
                if (child != null) {
                    AccountManagement.getInstance(getActivity()).writeChild(child);
                    Utils.setBabyAgeInMonthsMixpanel(Utils.getMonthCountDifference(mBirthdayCalendar));
                    Utils.logEvents(LogEvents.ADDED_CHILD);
                    uploadChildPhoto(child.id, accessToken);
                } else {
                    if (mListener != null) mListener.onChildCreated(false, "");
                    if (mError != null)
                        handleError(null, R.string.setup_add_child_error_message_body);
                }
            }
        }.execute();
    }

    private void uploadChildPhoto(final String childId, String authToken) {
        if (mChildPhotoFile != null && mChildPhotoFile.exists()) {
            showProgressBar(true);
            SproutlingApi.createPhoto(childId, mChildPhotoFile, authToken, new Callback<CreatePhotoResponse>() {
                @Override
                public void onResponse(Call<CreatePhotoResponse> call, Response<CreatePhotoResponse> response) {
                    showProgressBar(false);
                    if (response.isSuccessful()) {
                        CreatePhotoResponse createPhotoResponse = response.body();
                        SharedPrefManager.saveChildPhotoInfo(getActivity(), createPhotoResponse);
                        Utils.logEvents(CHILD_PROFILE_PHOTO_UPLOADED);
                        if (mListener != null) {
                            if (MODE_SETUP.equals(mMode)) mListener.onChildCreated(true, childId);
                            else mListener.onBabyUpdated(true);
                        }
                    } else {
                        if (mListener != null) {
                            if (MODE_SETUP.equals(mMode)) mListener.onChildCreated(false, "");
                            else mListener.onBabyUpdated(false);
                        }
                        handleError(null, MODE_SETUP.equals(mMode) ? R.string.setup_add_child_error_message_body : R.string.settings_baby_update_error_message_body);
                    }
                }

                @Override
                public void onFailure(Call<CreatePhotoResponse> call, Throwable t) {
                    if (mListener != null) {
                        if (MODE_SETUP.equals(mMode)) mListener.onChildCreated(false, "");
                        else mListener.onBabyUpdated(false);
                    }
                    handleError(null, MODE_SETUP.equals(mMode) ? R.string.setup_add_child_error_message_body : R.string.settings_baby_update_error_message_body);
                }
            });
        } else if (mListener != null) {
            if (MODE_SETUP.equals(mMode)) mListener.onChildCreated(true, childId);
            else mListener.onBabyUpdated(true);
        }
    }

    private void initBabyData() {
        SSManagement.Child child = AccountManagement.getInstance(getActivity()).getChild();
        if (child == null) {
            new AsyncTask<Void, Void, SSManagement.Child>() {
                SSError mError;

                @Override
                protected SSManagement.Child doInBackground(Void... params) {
                    try {
                        SSManagement.User account = AccountManagement.getInstance(getActivity()).getUser();
                        List<SSManagement.Child> children = SKManagement.listChildren(account.accessToken);
                        if (children != null && !children.isEmpty()) {
                            return children.get(0); // TODO: multiple children?
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    } catch (SSException e) {
                        e.printStackTrace();
                        mError = e.getError();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(SSManagement.Child child) {
                    if (child != null) {
                        mChild = child;
                        AccountManagement.getInstance(getActivity()).writeChild(child);
                        fillViews();
                    } else {
                        if (mListener != null) mListener.onActionButtonEnabled(TAG, false);
                        if (mError != null)
                            handleError(mError, R.string.settings_baby_get_error_message_body);
                    }
                }
            }.execute();
        } else {
            mChild = child;
            fillViews();
        }
        downloadChildPhoto();
    }

    private void downloadChildPhoto() {
        final File childPhotoFile = new File(getActivity().getFilesDir(), Utils.getChildPhotoFileName());
        if (childPhotoFile.exists()) {
            mChildPhotoView.setImageBitmap(BitmapFactory.decodeFile(childPhotoFile.getAbsolutePath()));
        }
        if (mChild != null && mChild.photoUrl != null) {
            SproutlingApi.downloadChildPhoto(mChild.photoUrl, new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        InputStream imageStream = response.body().byteStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                        if (bitmap != null) {
                            mChildPhotoView.setImageBitmap(bitmap);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            }, AccountManagement.getInstance(getActivity()).getUser().accessToken);
        }
    }

    public void updateBaby() {
        new AsyncTask<Void, Void, SSManagement.Child>() {
            SSError mError;

            @Override
            protected void onPreExecute() {
                showProgressBar(true);
            }

            @Override
            protected SSManagement.Child doInBackground(Void... params) {
                try {
                    SSManagement.User account = AccountManagement.getInstance(getActivity()).getUser();
                    return SKManagement.updateChildById(account.accessToken, mChild.id, getUpdatedChildJSON());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(SSManagement.Child child) {
                if (child != null) {
                    mChild = child;
                    AccountManagement.getInstance(getActivity()).writeChild(child);
                    Utils.setBabyAgeInMonthsMixpanel(Utils.getMonthCountDifference(mBirthdayCalendar));
                    //update child item in store
                    App.getInstance().dispatchAction(
                            Actions.DATA_UPDATE,
                            new Payload().put(States.Key.DATAITEM_CHILD, mChild)
                    );
                    showProgressBar(false);
                    uploadChildPhoto(mChild.id, AccountManagement.getInstance(getActivity()).getUser().accessToken);
                } else {
                    showProgressBar(false);
                    if (mError != null) {
                        if (mListener != null) mListener.onBabyUpdated(false);
                        handleError(mError, R.string.settings_baby_update_error_message_body);
                    }

                }
            }
        }.execute();
    }

    private void handleError(SSError error, int messageId) {
        GenericErrorDialogFragment.newInstance()
                .setTitle(R.string.settings_baby_error_message_title)
                .setMessage(messageId)
                .setButtonText(android.R.string.ok)
                .show(getFragmentManager(), null);
    }

    private JSONObject getUpdatedChildJSON() throws JSONException {
        String firstName = mNameEditText.getText().toString();

        JSONObject body = mChild.toJSON();
        if (!mGender.equals(mChild.gender)) body.put("gender", mGender);
        if (firstName != null && !firstName.equals(mChild.firstName))
            body.put("first_name", firstName);
        if (mBirthday != null && !mBirthday.equals(mChild.birthDate))
            body.put("birth_date", mBirthday);
        if (mDueDate != null && !mDueDate.equals(mChild.dueDate)) body.put("due_date", mDueDate);
        return body;
    }

    public interface OnBabyListener {
        void onActionButtonEnabled(String tag, boolean enabled);

        void onChildCreated(boolean created, String ownerId);

        void onBabyUpdated(boolean updated);
    }
}
