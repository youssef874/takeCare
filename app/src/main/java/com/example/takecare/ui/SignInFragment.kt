package com.example.takecare.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.takecare.R
import com.example.takecare.databinding.FragmentSignInBinding
import com.example.takecare.model.Doctor
import com.example.takecare.model.Patient
import com.example.takecare.setError
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth


class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding

    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var pswInputLayout: TextInputLayout
    private lateinit var loading: CircularProgressIndicator

    private lateinit var _doctor: Doctor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            _doctor = it.get(DoctorsFragment.DOCTOR) as Doctor
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignInBinding.inflate(inflater,container,false)
        emailInputLayout = binding.emailInputLayout
        pswInputLayout = binding.password
        loading = binding.signInCreateAccountLoading
        loading.visibility = View.INVISIBLE
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.signInCreateAccountButton.setOnClickListener {
            navigateToCreateAccountFragment()
        }

        binding.loginButton.setOnClickListener {
            loading.visibility = View.VISIBLE
            val email = binding.emailEditText.text.toString()
            val psw = binding.passwordEditText.text.toString()
            if (email.isNotEmpty() && psw.isNotEmpty()){
                singIn(email, psw)
            }
            if (email.isEmpty()){
                loading.visibility = View.INVISIBLE
                emailInputLayout.setError(true,getString(R.string.empty_text_field_error_message))
            }
            if (psw.isEmpty()){
                loading.visibility = View.INVISIBLE
                pswInputLayout.setError(true,getString(R.string.empty_text_field_error_message))
            }
        }
    }

    private fun singIn(email:String,psw:String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,psw).addOnCompleteListener {
            if (it.isSuccessful){
                loading.visibility = View.GONE
                navigateToDoctorFragment()
                Snackbar.make(
                    binding.signInConstraintLayout,
                    getString(R.string.authenticated_message),
                    Snackbar.LENGTH_LONG
                ).show()
            }else{
                loading.visibility = View.INVISIBLE
                emailInputLayout.setError(true,getString(R.string.wrong_input_message))
                pswInputLayout.setError(true,getString(R.string.wrong_input_message))
            }
        }
    }

    private fun navigateToDoctorFragment() {
        val action = SignInFragmentDirections.actionSignInFragmentToDoctorsFragment()
        findNavController().navigate(action)
    }

    private fun navigateToCreateAccountFragment() {
        val action = SignInFragmentDirections.actionSignInFragmentToCreateAccountFragment(_doctor)
        findNavController().navigate(action)
    }

    companion object {
        const val TAG = "SignInFragment"
        const val DOCTOR = "doctor_create"
    }
}