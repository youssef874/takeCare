package com.example.takecare.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.avionav.bindImage
import com.example.takecare.R
import com.example.takecare.databinding.FragmentProfileBinding
import com.example.takecare.model.Patient

class ProfileFragment : Fragment() {

    private lateinit var bindingSuccess: FragmentProfileBinding

    private lateinit var patient: Patient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            patient = it.get(DoctorsFragment.PATIENT) as Patient
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingSuccess = DataBindingUtil.inflate<FragmentProfileBinding>(
            inflater,R.layout.fragment_profile,container,false
        )
        displayProfile(patient)
        return bindingSuccess.root
    }

    private fun displayProfile(patient: Patient) {
        bindingSuccess.addressText.text = getString(R.string.addr_with_args,patient.address)
        bindingSuccess.emailText.text = getString(R.string.email_with_args,patient.email)
        bindingSuccess.idTextView.text = getString(R.string.id_string,patient.id)
        bindingSuccess.phoneNumberText.text = getString(R.string.tel_with_args_string,patient.phoneNumber)
        bindImage(bindingSuccess.profileImageView,patient.imageUrl)
    }

    companion object {
        const val TAG = "ProfileFragment"
    }
}