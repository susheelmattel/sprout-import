package com.sproutling.common.ui.presenter.interfaces

import com.sproutling.pojos.CreateChildRequestBody
import java.io.File

/**
 * Created by subram13 on 1/19/18.
 */
interface IAddChildPresenter : IBaseFrgPresenter {
    fun onBirthDayFocusChange(hasFocus: Boolean)


    fun onBirthDateTextChange(birthDate: String)
    fun onGenderSelected(@CreateChildRequestBody.Gender gender: String)
    fun onBabyNameTextChange(babyName: String)
    fun onWhyClick()
    fun createChild(childPhotoFile: File?)
    fun onAddPhotoClick()
    fun onTakePhotoOptionClick()
    fun onChooseFromLibraryClick()
    fun onPhotoSelected(imageSrc: String, imageDst: String, imageSize: Int, isCameraPath: Boolean)
    fun onPhotoCropped(bitmapPhoto: File)
    fun loadChildProfileInfo()
    fun updateChildProfile(childPhotoFile: File)
    fun handleOnResume()
}