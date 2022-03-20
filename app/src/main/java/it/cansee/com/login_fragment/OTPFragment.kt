package it.cansee.com.login_fragment

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.rommansabbir.animationx.Zoom
import com.rommansabbir.animationx.animationXZoom
import dagger.hilt.android.AndroidEntryPoint
import it.cansee.com.R
import it.cansee.com.activity.MainActivity
import it.cansee.com.databinding.FragmentOTPBinding
import javax.inject.Inject

@AndroidEntryPoint
class OTPFragment : Fragment() {

    private val _tag = "OTPFragment_CS"
    private lateinit var mBinding: FragmentOTPBinding

    @Inject
    lateinit var auth: FirebaseAuth

    private val args: OTPFragmentArgs by navArgs()
    private lateinit var storedVerificationId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentOTPBinding.inflate(inflater)
        // start animation of views
        startAnimation()
        // get storedVerificationId from the intent
        storedVerificationId = args.verificationId
        return mBinding.root
    }

    /**
     * Start animation of the views
     */
    private fun startAnimation() {
        with(mBinding) {
            otpTxtTitle.animationXZoom(Zoom.ZOOM_IN_UP, 800)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setup views()
        setupViews()
    }

    private fun setupViews() {
        val otpTextView: OtpTextView
        mBinding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                // fired when user types something in the Otpbox
            }

            override fun onOTPComplete(otp: String) {
                // fired when user has entered the OTP fully.
                startVerification(otp)
            }
        }

        mBinding.otpBtnNext.setOnClickListener {
            mBinding.otpView.otp?.let { otp -> startVerification(otp) }
        }
    }

    private fun startVerification(otp: String) {
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
            storedVerificationId, otp
        )
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_OTPFragment_to_setupProfileFragment)
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Snackbar.make(mBinding.root, "Error", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
    }
}