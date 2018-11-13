package com.sproutling.common.ui.presenter

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import com.sproutling.api.SproutlingApi
import com.sproutling.common.R
import com.sproutling.common.app.BaseApplication
import com.sproutling.common.pojos.events.ChildUpdateEvent
import com.sproutling.common.ui.presenter.interfaces.IAddChildPresenter
import com.sproutling.common.ui.view.interfaces.IAddChildFragmentView
import com.sproutling.common.utils.AccountManagement
import com.sproutling.common.utils.Utils.getBitmapFromFile
import com.sproutling.pojos.*
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by subram13 on 1/19/18.
 */
class AddChildPresenterImpl : BaseFrgPresenterImpl, IAddChildPresenter {
    override fun handleOnResume() {
        mAddChildFragmentView.setToolBarTitle(if (mMode == SETTINGS) {
            BaseApplication.sInstance!!.getString(R.string.child_profile_title)
        } else {
            BaseApplication.sInstance!!.getString(R.string.child_profile_onboarding_title)
        })
    }

    override fun updateChildProfile(childPhotoFile: File) {
        if (validateFields()) {
            mAddChildFragmentView.showProgressBar(true)

            var child = AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).child
            if (child == null) {
                createChild(childPhotoFile)
                return
            }
            child.firstName = mName
            child.gender = mGender
            child.birthDate = getFormattedDate(mBirthdayCalendar.time)
            child.dueDate = getFormattedDate(mBirthdayCalendar.time)
            var updateChildRequestBody = UpdateChildRequestBody(child)
            var accessToken = AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).userAccount.accessToken
            SproutlingApi.updateChild(updateChildRequestBody, object : Callback<UpdateChildResponse> {
                override fun onFailure(call: Call<UpdateChildResponse>?, t: Throwable?) {
                    mAddChildFragmentView.showProgressBar(false)
                }

                override fun onResponse(call: Call<UpdateChildResponse>?, response: Response<UpdateChildResponse>?) {

                    if (response!!.isSuccessful) {
                        var currentPhoto = AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).child.childPhoto
                        AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).child = response.body() as Child
                        AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).child.childPhoto = currentPhoto
                        EventBus.getDefault().post(ChildUpdateEvent())
                        uploadChildPhoto(childPhotoFile, response.body()!!.id)
                    } else {
                        mAddChildFragmentView.showProgressBar(false)
                        mAddChildFragmentView.showErrorDialog()
                    }
                }

            }, child.id, accessToken)
        }
    }

    override fun loadChildProfileInfo() {
        mAddChildFragmentView.enableActionBtn(false)
        if (mMode == SETUP || mMode == DEFAULT) {
            mBirthdayCalendar.timeInMillis = System.currentTimeMillis()
            mDueDateCalendar.timeInMillis = System.currentTimeMillis()
            mAddChildFragmentView.setPhotoActionText(R.string.child_profile_add_photo)

        } else {
            loadChildInfo()
            mAddChildFragmentView.setPhotoActionText(R.string.child_profile_change_photo)
            mGenderStatePass = true
            mNameStatePass = true
            mBirthDayStatePass = true
        }

    }

    override fun onPhotoCropped(photoFile: File) {
        var bitmap = getBitmapFromFile(photoFile)
        if (bitmap != null) {
            mAddChildFragmentView.setChildPhoto(bitmap)
            mIsPhotoChanged = true
        }
        mAddChildFragmentView.enableActionBtn(true)
    }

    override fun onPhotoSelected(imageSrc: String, imageDst: String, imageSize: Int, isCameraPath: Boolean) {
        mAddChildFragmentView.openProfilePhotoEdit(imageSrc, imageDst, imageSize, isCameraPath)
    }

    override fun onTakePhotoOptionClick() {
        mAddChildFragmentView.takePhotoFromCamera()
    }

    override fun onChooseFromLibraryClick() {
        mAddChildFragmentView.choosePhotoFromLibrary()
    }

    override fun onAddPhotoClick() {
        mAddChildFragmentView.showPhotoOptions()
    }

    override fun createChild(childPhotoFile: File?) {

        val accessToken = AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).userAccount.accessToken
        if (validateFields()) {
            mAddChildFragmentView.showProgressBar(true)
            SproutlingApi.createChild(CreateChildRequestBody(mName, "", mBirthday, mBirthday, null, mGender), object : Callback<CreateChildResponse> {
                override fun onFailure(call: Call<CreateChildResponse>?, t: Throwable?) {
                    Log.e(TAG, "Unable to create a child")
                    mAddChildFragmentView.showProgressBar(false)
                }

                override fun onResponse(call: Call<CreateChildResponse>?, response: Response<CreateChildResponse>?) {

                    if (response!!.isSuccessful) {
                        Log.i(TAG, "Successfully created a child")


                        val child = response.body() as Child
                        if(childPhotoFile != null){
                            child.childPhoto = getBitmapFromFile(childPhotoFile)
                        }
                        AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).child = child
                        uploadChildPhoto(childPhotoFile, response.body()!!.id)

                    } else {
                        mAddChildFragmentView.showProgressBar(false)
                        mAddChildFragmentView.showErrorDialog()
                    }
                }

            }, accessToken)
        }
    }

    override fun onWhyClick() {
        mAddChildFragmentView.showWhyDateDialog()
    }

    override fun onBabyNameTextChange(babyName: String) {
        mNameStatePass = babyName.isNotBlank()
        mName = babyName
        mAddChildFragmentView.enableActionBtn(validateFields())
    }

    override fun onGenderSelected(@CreateChildRequestBody.Gender gender: String) {
        mGender = gender
        mGenderStatePass = true
        mAddChildFragmentView.enableActionBtn(validateFields())
    }

    override fun onBirthDateTextChange(birthDate: String) {
        if (birthDate.isNotBlank()) {
//            mBirthday = birthDate
            mBirthDayStatePass = isBirthdayValid()
            mAddChildFragmentView.showBirthDayError(R.string.child_profile_birthday_cannot_be_in_future, !mBirthDayStatePass)
        }
        mAddChildFragmentView.enableActionBtn(validateFields())
    }

    private fun isBirthdayValid(): Boolean {
        val currentCalender = Calendar.getInstance()
        mBirthdayCalendar.set(Calendar.HOUR_OF_DAY, currentCalender.get(Calendar.HOUR_OF_DAY))
        mBirthdayCalendar.set(Calendar.HOUR, currentCalender.get(Calendar.HOUR))
        mBirthdayCalendar.set(Calendar.MINUTE, currentCalender.get(Calendar.MINUTE))
        mBirthdayCalendar.set(Calendar.SECOND, currentCalender.get(Calendar.SECOND))
        mBirthdayCalendar.set(Calendar.MILLISECOND, currentCalender.get(Calendar.MILLISECOND))
        Log.d(TAG, "currentCalender :" + currentCalender.timeInMillis)
        Log.d(TAG, "Birthday Calender :" + mBirthdayCalendar.timeInMillis)
        return mBirthdayCalendar.timeInMillis <= currentCalender.timeInMillis
    }

    private fun validateFields(): Boolean {
        return mGenderStatePass && mNameStatePass && mBirthDayStatePass
    }

    override fun onBirthDayFocusChange(hasFocus: Boolean) {
        if (hasFocus) {
            mAddChildFragmentView.hideKeyboard()
            mAddChildFragmentView.showDatePicker(mBirthdayCalendar.get(Calendar.YEAR), mBirthdayCalendar.get(Calendar.MONTH), mBirthdayCalendar.get(Calendar.DAY_OF_MONTH), DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                mBirthdayCalendar.set(Calendar.YEAR, year)
                mBirthdayCalendar.set(Calendar.MONTH, month)
                mBirthdayCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val month = mBirthdayCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
                mBirthday = getFormattedDate(mBirthdayCalendar.time)
                mAddChildFragmentView.setBirthDay("$month $dayOfMonth, $year")
            })
        }
    }


    private fun uploadChildPhoto(childPhotoFile: File?, childId: String) {
        if (childPhotoFile != null && childPhotoFile.exists() && mIsPhotoChanged) {
            mAddChildFragmentView.showProgressBar(true)
            Log.d(TAG, "Uploading Child Photo")
            SproutlingApi.createPhoto(childId, childPhotoFile, object : Callback<CreatePhotoResponse> {
                override fun onResponse(call: Call<CreatePhotoResponse>, response: Response<CreatePhotoResponse>) {
                    mAddChildFragmentView.showProgressBar(false)

                    if (response.isSuccessful) {
                        val createPhotoResponse = response.body()
                        AccountManagement.getInstance(BaseApplication.sInstance!!.getAppContext()).child.childPhoto = getBitmapFromFile(childPhotoFile)
                        EventBus.getDefault().post(ChildUpdateEvent())
                        // saveProfilePhotoBitmap(Utils.getBitmapFromFile(childPhotoFile))
                        mAddChildFragmentView.onChildCreated()
                    } else {
                        mAddChildFragmentView.showErrorDialog()
                    }
                }

                override fun onFailure(call: Call<CreatePhotoResponse>, t: Throwable) {

                }
            })
        } else {
            mAddChildFragmentView.showProgressBar(false)
            mAddChildFragmentView.onChildCreated()
        }
    }

    private fun getFormattedDate(date: Date): String {
        val dateFormat = SimpleDateFormat(FORMAT_DATE, Locale.US)
        return dateFormat.format(date)
    }

    private fun getDateFromString(dateStr: String): Date {
        val dateFormat = SimpleDateFormat(FORMAT_DATE, Locale.US)
        return dateFormat.parse(dateStr)
    }

    private fun loadChildInfo() {
        var child = AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).child

        if (child == null) {
            mAddChildFragmentView.showProgressBar(true)
            var accessToken = AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).userAccount.accessToken
            SproutlingApi.getChildrenList(object : Callback<ArrayList<Child>> {
                override fun onFailure(call: Call<ArrayList<Child>>?, t: Throwable?) {
                    mAddChildFragmentView.showProgressBar(false)
                }

                override fun onResponse(call: Call<ArrayList<Child>>?, response: Response<ArrayList<Child>>?) {
                    mAddChildFragmentView.showProgressBar(false)
                    if (response!!.isSuccessful && !response.body()!!.isEmpty()) {
                        child = response.body()!!.get(0)
                        AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).setChild(child)
                        setChildInfo(child)
                        downloadChildPhoto(child)
                    }
                }
            }, accessToken)
        } else {
            setChildInfo(child)
            val childBitmap = child.getChildPhoto()
            if (child.getChildPhoto() != null) {
                mAddChildFragmentView.setChildPhoto(childBitmap)
            } else {
                downloadChildPhoto(child)
            }
        }

    }

    private fun downloadChildPhoto(child: Child) {
        mAddChildFragmentView.setPhotoActionText(R.string.child_profile_add_photo)
        if (child != null && !TextUtils.isEmpty(child.photoUrl)) {
            mAddChildFragmentView.setPhotoActionText(R.string.child_profile_change_photo)
            SproutlingApi.downloadChildPhoto(child.photoUrl, object : Callback<ResponseBody> {

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val imageStream = response.body()!!.byteStream()
                        val bitmap = BitmapFactory.decodeStream(imageStream)
                        if (bitmap != null) {
                            if (mAddChildFragmentView != null) {
                                mAddChildFragmentView.setChildPhoto(bitmap)
                            }
                            //saveProfilePhotoBitmap(bitmap)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                }
            }, AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).userAccount.accessToken)
        }
    }

    private fun setChildInfo(child: Child) {

        var calender = Calendar.getInstance()
        calender.time = getDateFromString(child.birthDate)
        mBirthdayCalendar = calender
        var calender2 = Calendar.getInstance()
        calender2.time = getDateFromString(child.dueDate)
        mDueDateCalendar = calender2

        mAddChildFragmentView.setBabyName(child.firstName)
        mAddChildFragmentView.setIsBabyGenderMale(child.gender == CreateChildRequestBody.MALE)
        mAddChildFragmentView.setBirthDay("${mBirthdayCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())} ${mBirthdayCalendar.get(Calendar.DAY_OF_MONTH)}, ${mBirthdayCalendar.get(Calendar.YEAR)}")
    }

    private val FORMAT_DATE = "yyyy-MM-dd"
    private var mAddChildFragmentView: IAddChildFragmentView
    private var mBirthdayCalendar = Calendar.getInstance()
    private var mDueDateCalendar = Calendar.getInstance()
    private var mBirthday: String? = null
    private lateinit var mGender: String
    private var mMode: String
    private var mGenderStatePass = false
    private var mNameStatePass = false
    private var mBirthDayStatePass = false
    private lateinit var mName: String
    private var mIsPhotoChanged = false

    constructor(addChildFragmentView: IAddChildFragmentView, mode: String) : super(addChildFragmentView) {
        mAddChildFragmentView = addChildFragmentView
        mMode = mode
    }


    companion object {
        const val TAG = "AddChildPresenterImpl"
        val EXTRA_MODE_BABY = "extra_mode"
        const val SETTINGS = "mode_settings"
        const val SETUP = "mode_setup"
        const val DEFAULT = SETUP
    }
}