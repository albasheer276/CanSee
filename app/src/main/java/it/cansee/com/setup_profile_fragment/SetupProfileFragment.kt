package it.cansee.com.setup_profile_fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.kroegerama.imgpicker.BottomSheetImagePicker
import com.kroegerama.imgpicker.ButtonType
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import it.cansee.com.R
import it.cansee.com.databinding.FragmentSetupProfileBinding
import java.io.File
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class SetupProfileFragment : Fragment(), BottomSheetImagePicker.OnImagesSelectedListener {

    private val _tag = "SetupProfileFragment_CS"
    private lateinit var mBinding: FragmentSetupProfileBinding

    @Inject
    lateinit var auth: FirebaseAuth

    //private lateinit var imagePicker: ImagePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode == RESULT_OK && result.resultCode == UCrop.REQUEST_CROP) {
                val resultUri = UCrop.getOutput(result.data!!)
                Glide.with(this).load(resultUri).circleCrop().into(mBinding.setupProfileImgProfile)
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(result.data!!)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSetupProfileBinding.inflate(inflater)
        /*imagePicker = ImagePicker(activity as AppCompatActivity, BuildConfig.APPLICATION_ID)
        imagePicker.setImageSelectedListener(this)*/
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(_tag, auth.uid.toString())
        // request CAMERA and READ_EXTERNAL_STORAGE permissions
        Dexter.withContext(activity)
            .withPermissions(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                }

                override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                }

            })
            .check()
        mBinding.setupProfileImgProfile.setOnClickListener {

            BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                .cameraButton(ButtonType.Button)            //style of the camera link (Button in header, Image tile, None)
                .galleryButton(ButtonType.Button)           //style of the gallery link
                .singleSelectTitle(R.string.pick_single)    //header text
                .peekHeight(R.dimen.peekHeight)             //peek height of the bottom sheet
                .columnSize(R.dimen.columnSize)             //size of the columns (will be changed a little to fit)
                .requestTag("single")               //tag can be used if multiple pickers are used
                .show(childFragmentManager)

            /*val dialog = BottomSheetDialog(requireActivity())
            val bindingBottomSheet = BottomSheetGetImageBinding.bind(layoutInflater.inflate(R.layout.bottom_sheet_get_image, mBinding.root, false))

            bindingBottomSheet.btmShtLayoutOpenGallery.setOnClickListener {
                imagePicker.takePhotoFromGallery()
                dialog.dismiss()
            }
            bindingBottomSheet.btmShtLayoutOpenCamera.setOnClickListener {
                imagePicker.takePhotoFromCamera()
                dialog.dismiss()
            }

            dialog.setContentView(bindingBottomSheet.root)
            dialog.show()*/
        }
    }

    override fun onImagesSelected(uris: List<Uri>, tag: String?) {
        for (uri in uris) {
            val destinationUri = Uri.fromFile(File(requireActivity().cacheDir, UUID.randomUUID().toString() + ".jpg"))
            UCrop.of(uri, destinationUri)
                .withAspectRatio(1F, 1F)
                .start(this.requireContext(), this, UCrop.REQUEST_CROP);
        }
    }
}