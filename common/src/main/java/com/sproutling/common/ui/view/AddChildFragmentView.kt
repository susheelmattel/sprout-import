package com.sproutling.common.ui.view

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sproutling.common.R
import com.sproutling.common.ui.dialogfragment.GenericErrorDialogFragment
import com.sproutling.common.ui.dialogfragment.WhyDateDialogFragment
import com.sproutling.common.ui.presenter.AddChildPresenterImpl
import com.sproutling.common.ui.presenter.AddChildPresenterImpl.Companion.DEFAULT
import com.sproutling.common.ui.presenter.AddChildPresenterImpl.Companion.EXTRA_MODE_BABY
import com.sproutling.common.ui.presenter.interfaces.IAddChildPresenter
import com.sproutling.common.ui.view.ProfilePhotoView.Companion.EXTRA_DST
import com.sproutling.common.ui.view.ProfilePhotoView.Companion.EXTRA_IMAGE_SIZE
import com.sproutling.common.ui.view.ProfilePhotoView.Companion.EXTRA_MODE
import com.sproutling.common.ui.view.ProfilePhotoView.Companion.EXTRA_PHOTO_TYPE
import com.sproutling.common.ui.view.ProfilePhotoView.Companion.EXTRA_SRC
import com.sproutling.common.ui.view.ProfilePhotoView.Companion.MODE_URI
import com.sproutling.common.ui.view.interfaces.IAddChildFragmentView
import com.sproutling.common.utils.Utils
import com.sproutling.pojos.CreateChildRequestBody
import kotlinx.android.synthetic.main.fragment_setup_add_child.*
import java.io.File
import java.io.IOException

/**
 * Created by subram13 on 1/18/18.
 */
class AddChildFragmentView : BaseFragmentView(), IAddChildFragmentView {
    override fun setToolBarTitle(title: String) {
        (activity as AddChildListener).setToolBarTitle(title)
    }

    override fun onSaveBtnClick() {
        mAddChildPresenter.updateChildProfile(mChildPhotoFile!!)
    }

    override fun setPhotoActionText(actionText: Int) {
        addPhoto.setText(actionText)
    }

    override fun setBabyName(babyName: String) {
        name.setText(babyName)
    }

    override fun setIsBabyGenderMale(isMale: Boolean) {
        if (isMale) {
            genderGroup.check(R.id.boy)
        } else
            genderGroup.check(R.id.girl)
    }

    override fun onChildCreated() {
        (activity as AddChildListener).onChildProfileCreatedOrUpdated()
    }

    override fun showErrorDialog() {
        GenericErrorDialogFragment().show(fragmentManager, null)
    }


    override fun setChildPhoto(bitmapPhoto: Bitmap) {
        if (photo != null)
            photo.setImageBitmap(bitmapPhoto)
    }

    override fun openProfilePhotoEdit(imageSrc: String, imageDst: String, imageSize: Int, isCameraPath: Boolean) {
        val intent = Intent(activity, ProfilePhotoView::class.java)
        intent.putExtra(EXTRA_MODE, MODE_URI)
        intent.putExtra(EXTRA_SRC, imageSrc)
        intent.putExtra(EXTRA_DST, imageDst)
        intent.putExtra(EXTRA_PHOTO_TYPE, isCameraPath)
        intent.putExtra(EXTRA_IMAGE_SIZE, imageSize)
        activity.startActivityForResult(intent, PHOTO_REQUEST_CROP)
    }

    override fun takePhotoFromCamera() {
        startCamera()

//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//            if (!Utils.hasPermissions(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
//                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CAMERA)
//            } else {
//                startCamera()
//            }
//        } else {
//            startCamera()
//        }
    }

    override fun choosePhotoFromLibrary() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_LIBRARY)
            } else {
                showPhotoLibrary()
            }

        } else {
            showPhotoLibrary()
        }
    }

    private fun showPhotoLibrary() {
        val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PHOTO_REQUEST_GALLERY)
    }

    override fun showPhotoOptions() {
        val view = activity.layoutInflater.inflate(R.layout.photo_options_layout, null)
        val dialog = BottomSheetDialog(activity)
        val tvTakePhoto = view.findViewById(R.id.take_photo) as TextView
        val tvChooseFromLibrary = view.findViewById(R.id.choose_from_library) as TextView
        val tvCancel = view.findViewById(R.id.cancel) as TextView
        tvTakePhoto.setOnClickListener({
            dialog.dismiss()
            mAddChildPresenter.onTakePhotoOptionClick()
        })
        tvChooseFromLibrary.setOnClickListener({
            dialog.dismiss()
            mAddChildPresenter.onChooseFromLibraryClick()
        })
        tvCancel.setOnClickListener({ dialog.dismiss() })
        dialog.setContentView(view)
        dialog.show()
    }

    override fun onNextBtnClick() {
        mAddChildPresenter.createChild(mChildPhotoFile)
    }

    override fun showWhyDateDialog() {
        WhyDateDialogFragment().show(fragmentManager, null)
    }

    override fun enableActionBtn(enable: Boolean) {
        (activity as AddChildListener).enableActionBtn(enable)
    }



    override fun showBirthDayError(error: Int, show: Boolean) {
        birthday.setError(getString(error))
        birthday.showErrorMsg(show)
    }

    override fun setBirthDay(birthDay: String) {
        birthday.setText(birthDay, TextView.BufferType.EDITABLE)
    }


    override fun showDatePicker(year: Int, month: Int, dayOfMonth: Int, onDateSetListener: DatePickerDialog.OnDateSetListener) {
        DatePickerDialog(activity, onDateSetListener, year, month, dayOfMonth).show()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_setup_add_child, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        var mode = DEFAULT
        if (arguments != null) {
            mode = arguments.getString(EXTRA_MODE_BABY)
        }
        photo.setOnClickListener { }
        addPhoto.setOnClickListener { mAddChildPresenter.onAddPhotoClick() }
        why.setOnClickListener { mAddChildPresenter.onWhyClick() }
        genderGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.girl -> {
                    mAddChildPresenter.onGenderSelected(CreateChildRequestBody.FEMALE)
                }
                R.id.boy -> {
                    mAddChildPresenter.onGenderSelected(CreateChildRequestBody.MALE)
                }
            }
        }
        name.onFocusChangeListener = mOnFocusChangeListener
        birthday.onFocusChangeListener = mOnFocusChangeListener


        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mAddChildPresenter.onBabyNameTextChange(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        birthday.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mAddChildPresenter.onBirthDateTextChange(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        mAddChildPresenter = AddChildPresenterImpl(this, mode)
        mAddChildPresenter.loadChildProfileInfo()
        mChildPhotoFile = createImageFile()

    }


    override fun onResume(){
        super.onResume()

        mAddChildPresenter.handleOnResume()

        if (activity is AccountSetupView) {
            (activity as AccountSetupView).disableBack()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CAMERA -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mAddChildPresenter.onTakePhotoOptionClick()
            }
            PERMISSION_REQUEST_LIBRARY -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mAddChildPresenter.onChooseFromLibraryClick()
            }
            else -> {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK){

            if(mChildPhotoFile == null){
                mChildPhotoFile = createImageFile()
            }
            when (requestCode) {
                PHOTO_REQUEST_TAKE_PHOTO ->  {
                    mAddChildPresenter.onPhotoSelected(mPicUri.toString(), mChildPhotoFile!!.absolutePath, photo.measuredWidth, true)
//                editPhoto(mPicUri.toString(), mChildPhotoFile!!.absolutePath, photo.measuredWidth)
                    Log.d(TAG, "PHOTO_REQUEST_TAKE_PHOTO")
                }

                PHOTO_REQUEST_GALLERY -> if (data != null) {
                    mPicUri = data.data
                    Log.d(TAG, "PHOTO_REQUEST_GALLERY")
                    mAddChildPresenter.onPhotoSelected(mPicUri.toString(), mChildPhotoFile!!.absolutePath, photo.measuredWidth, false)
                }

                PHOTO_REQUEST_CROP -> {
                    Log.d(TAG, "PHOTO_REQUEST_CROP")
                    mAddChildPresenter.onPhotoCropped(mChildPhotoFile!!)
                }
            }

        } else{
            mChildPhotoFile = null
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    private val mOnFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        when (v.id) {
            R.id.name -> if (!hasFocus) {
                hideKeyboard()
            }
            R.id.birthday -> {
                mAddChildPresenter.onBirthDayFocusChange(hasFocus)
            }
        }
    }

    private fun editPhoto(imageSrc: String, imageDst: String, imageSize: Int) {
        val intent = Intent(activity, ProfilePhotoView::class.java)
        intent.putExtra(EXTRA_MODE, MODE_URI)
        intent.putExtra(EXTRA_SRC, imageSrc)
        intent.putExtra(EXTRA_DST, imageDst)
        intent.putExtra(EXTRA_IMAGE_SIZE, imageSize)
        startActivityForResult(intent, PHOTO_REQUEST_CROP)
    }

    private fun startCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            if (mChildPhotoFile == null) {
                mChildPhotoFile = createImageFile()
            }

            mPicUri = FileProvider.getUriForFile(activity,
                    "com.sproutling.common.fileProvider",
                    mChildPhotoFile)

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPicUri)
            activity.startActivityForResult(takePictureIntent, PHOTO_REQUEST_TAKE_PHOTO)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name

        //val file = activity.filesDir
        val file = File(context.filesDir, Utils.getChildPhotoFileName())


        // val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val image = File.createTempFile(
//                Utils.getChildPhotoFileName(), /* prefix */
//                ".jpg", /* suffix */
//                file      /* directory */
//        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = file.absolutePath
        return file
    }


    private var mCurrentPhotoPath: String? = null
    private var mPicUri: Uri? = null
    private lateinit var mAddChildPresenter: IAddChildPresenter
    private var mChildPhotoFile: File? = null
    private val PERMISSION_REQUEST_CAMERA = 16
    private val PERMISSION_REQUEST_LIBRARY = 12
    private val PHOTO_REQUEST_TAKE_PHOTO = 1
    private val PHOTO_REQUEST_GALLERY = 2
    private val PHOTO_REQUEST_CROP = 6
    private val IMAGE_FILENAME = "picture"

    interface AddChildListener : BaseFragmentListener {
        fun onChildProfileCreatedOrUpdated()
    }

    companion object {
        const val TAG = "AddChildFragmentView"

        fun instance(mode: String): AddChildFragmentView {
            val bundle = Bundle()
            bundle.putString(EXTRA_MODE_BABY, mode)
            val fragment = AddChildFragmentView()
            fragment.arguments = bundle
            return fragment
        }
    }
}