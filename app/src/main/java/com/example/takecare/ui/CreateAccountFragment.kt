package com.example.takecare.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.takecare.R
import com.example.takecare.databinding.FragmentCreateAccountBinding
import com.example.takecare.model.Doctor
import com.example.takecare.model.Patient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class CreateAccountFragment : Fragment() {

    private lateinit var binding: FragmentCreateAccountBinding

    private var patient = Patient()

    private lateinit var _doctor: Doctor

    private var isSent = false

    private lateinit var idInputLayout: TextInputLayout
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var telInputLayout: TextInputLayout
    private lateinit var adrInputLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _doctor = arguments?.get(SignInFragment.DOCTOR) as Doctor
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateAccountBinding.inflate(layoutInflater, container, false)
        idInputLayout = binding.createAccountNidcInputLayout
        nameInputLayout = binding.createAccountNameTextInputLayout
        emailInputLayout = binding.createAccountTextEmailInputLayout
        telInputLayout = binding.createAccountTelInputLayout
        adrInputLayout = binding.createAccountAddressInputLayout
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.createAccountButton.setOnClickListener {
            patient = getPatient()
            if (patient.phoneNumber != 0) {
                displayAlertDialog()
                isSent = true
            }
        }

        binding.createAccountCancelButton.setOnClickListener {
            navigateToDoctorFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isSent)
            Snackbar.make(
                binding.constraintLayout,
                getString(R.string.email_sent_message),
                Snackbar.LENGTH_LONG
            ).show()
    }

    private fun sendIntent() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto:")
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, EMAIL)
            val subject = "${getString(R.string.request_registration)} " +
                    "and ${getString(R.string.request_appointment)}"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(
                Intent.EXTRA_TEXT, getString(
                    R.string.message_body,
                    patient.name,
                    patient.id,
                    _doctor.name,
                    _doctor.speciality,
                    patient.phoneNumber,
                    patient.address
                )
            )
        }
        if (activity?.let { intent.resolveActivity(it.packageManager) } != null) {
            startActivity(intent)
        }
    }

    private fun navigateToDoctorFragment() {
        val action = CreateAccountFragmentDirections.actionCreateAccountFragmentToDoctorsFragment()
        findNavController().navigate(action)
    }

    private fun getPatient(): Patient {
        val id = binding.createAccountNidcEditText.text.toString()
        val name = binding.createAccountNameEditText.text.toString()
        val tel = binding.createAccountTelEditText.text.toString()
        val email = binding.createAccountEmailEditText.text.toString()
        val adr = binding.createAccountAddresslEditText.text.toString()
        if (!canEmailBeSent(id, name, adr, tel, email)) {
            Toast.makeText(context, getString(R.string.empty_forum), Toast.LENGTH_LONG).show()
            return Patient()
        }
        return Patient(id, name, tel.toInt(), adr, email)
    }

    private fun canEmailBeSent(
        id: String,
        name: String,
        adr: String,
        tel: String,
        email: String
    ): Boolean {
        if (id.isNotEmpty()
            && name.isNotEmpty()
            && adr.isNotEmpty()
            && tel.isNotEmpty()
            && email.isNotEmpty()
        )
            return true
        return false
    }

    companion object {
        const val TAG = "CreateAccountFragment"
        const val EMAIL = "contact.hospital@gmail.com"
    }

    private fun displayAlertDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.request_registration)
            .setMessage(R.string.email_dialog)
            .setPositiveButton(R.string.email_dialog_positive) { _, _ ->
                sendIntent()
            }
            .show()
    }
}