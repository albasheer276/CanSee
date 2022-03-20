package it.cansee.com.login_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import it.cansee.com.databinding.FragmentLoginBinding
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import it.cansee.com.R


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val _tag = "LoginFragment_CS"
    private lateinit var mBinding: FragmentLoginBinding

    @Inject
    lateinit var auth: FirebaseAuth

    // this stores the phone number of the user
    private var number: String = ""

    // we will use this to match the sent otp from firebase
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        // This method is called when the verification is completed
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
            Log.d(_tag, "onVerificationCompleted Success")
        }

        // Called when verification is failed add log statement to see the exception
        override fun onVerificationFailed(e: FirebaseException) {
            Log.d(_tag, "onVerificationFailed  $e")
        }

        // On code is sent by the firebase this method is called
        // in here we start a new activity where user can enter the OTP
        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(_tag, "onCodeSent: $verificationId")
            storedVerificationId = verificationId
            resendToken = token

            val action = LoginFragmentDirections.actionLoginFragmentToOTPFragment(verificationId)
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // check login
        checkLogin()

        // Inflate the layout for this fragment
        mBinding = FragmentLoginBinding.inflate(inflater)

        return mBinding.root
    }

    private fun checkLogin() {
        if(auth.currentUser != null){
            findNavController().navigate(R.id.action_loginFragment_to_setupProfileFragment)
            Log.d(_tag, "checkLogin: ${auth.currentUser!!.uid}")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup views()
        setupViews()
    }

    private fun setupViews() {
        with(mBinding) {
            loginEdtPhone.requestFocus()
            loginCcp.registerCarrierNumberEditText(loginEdtPhone)
            loginBtnNext.setOnClickListener {
                if (!loginCcp.isValidFullNumber) {
                    loginEdtPhone.error = "Invalid Number"
                } else startOTP()
            }
        }
    }

    private fun startOTP() {
        number = mBinding.loginCcp.fullNumberWithPlus
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}