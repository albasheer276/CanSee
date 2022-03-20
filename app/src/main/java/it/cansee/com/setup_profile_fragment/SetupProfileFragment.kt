package it.cansee.com.setup_profile_fragment

 import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import it.cansee.com.databinding.FragmentSetupProfileBinding
import javax.inject.Inject


@AndroidEntryPoint
class SetupProfileFragment : Fragment() {

    private val _tag = "SetupProfileFragment_CS"
    private lateinit var mBinding: FragmentSetupProfileBinding

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSetupProfileBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(_tag, auth.uid.toString())
    }
}