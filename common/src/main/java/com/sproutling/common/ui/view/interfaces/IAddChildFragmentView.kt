package com.sproutling.common.ui.view.interfaces

import android.app.DatePickerDialog
import android.graphics.Bitmap

/**
 * Created by subram13 on 1/18/18.
 */
interface IAddChildFragmentView : IBaseFragmentView {
    fun showDatePicker(year: Int, month: Int, dayOfMonth: Int, onDateSetListener: DatePickerDialog.OnDateSetListener)
    fun setBirthDay(birthDay: String)


    fun showBirthDayError(error: Int, show: Boolean)
    fun enableActionBtn(enable: Boolean)
    fun showWhyDateDialog()
    fun onNextBtnClick()
    fun onSaveBtnClick()
    fun showPhotoOptions()
    fun takePhotoFromCamera()
    fun choosePhotoFromLibrary()
    fun openProfilePhotoEdit(imageSrc: String, imageDst: String, imageSize: Int, isCameraPath: Boolean)
    fun setChildPhoto(photo: Bitmap)
    fun onChildCreated()
    fun showErrorDialog()
    fun setBabyName(name: String)
    fun setIsBabyGenderMale(isMale: Boolean)
    fun setPhotoActionText(actionText: Int)
    fun setToolBarTitle(title: String)
}