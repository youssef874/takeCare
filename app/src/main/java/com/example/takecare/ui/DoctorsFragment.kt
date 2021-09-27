package com.example.takecare.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.avionav.adapter.DoctorsListAdapter
import com.example.takecare.R
import com.example.takecare.authenticatedUser
import com.example.takecare.databinding.FragmentDoctorsBinding
import com.example.takecare.logOut
import com.example.takecare.model.Doctor
import com.example.takecare.model.Patient
import com.example.takecare.viewmodel.SharedViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class DoctorsFragment : Fragment() {

    private var _binding: FragmentDoctorsBinding? = null
    // This property is only valid between onCreateView and
     // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()

    private lateinit var sinOutItem: MenuItem
    private lateinit var profileItem : MenuItem
    private var isVisibleMenu = true

    private lateinit var patient: Patient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil
            .inflate(inflater,R.layout.fragment_doctors, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.doctorsRecycleView.adapter = DoctorsListAdapter(
           DoctorsListAdapter.ONClickListener {
               if (authenticatedUser() == null){
                   displayAlertDialog(it)
               }else
                   sendEmail(it)
            }
        )

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun navigateToSignInFragment(doctor: Doctor) {
        val action = DoctorsFragmentDirections.actionDoctorsFragmentToSignInFragment(doctor)
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.patients.observe(viewLifecycleOwner,{
            if (it != null){
                viewModel.getAuthenticatedPatient(it)
            }
        })

        viewModel.patient.observe(viewLifecycleOwner,{
            if (it != null)
                patient = it

        })

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.authenticated_user_menu, menu)
        searchForDoctor(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (authenticatedUser() == null){
            isVisibleMenu = false
            sinOutItem = menu.findItem(R.id.log_out_menu_item)
            profileItem = menu.findItem(R.id.profileFragment)
            sinOutItem.isVisible = isVisibleMenu
            profileItem.isVisible = isVisibleMenu
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.log_out_menu_item -> {
                isVisibleMenu = false
                requireActivity().invalidateOptionsMenu()
                logOut()
                true
            }
            R.id.profileFragment -> {
                navigateToProfileFragment(patient)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sendEmail(_doctor: Doctor) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto:")
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, CreateAccountFragment.EMAIL)
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

    private fun displayAlertDialog(doctor: Doctor) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title)
            .setMessage(R.string.dialog_body)
            .setPositiveButton(R.string.positive_dialog) { _, _ ->
                navigateToSignInFragment(doctor)
            }
            .show()
    }

    private fun navigateToProfileFragment(patient: Patient): Boolean {
        var action = DoctorsFragmentDirections.actionDoctorsFragmentToProfileFragment(patient)
        findNavController().navigate(action)
        return true
    }

    private fun searchForDoctor(menu: Menu){
        val searchView = menu.findItem(R.id.app_bar_search)
            .actionView as SearchView
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        if (it.isNotEmpty()) {
                            viewModel
                                .getTheSearchedResult(
                                    it
                                        .lowercase(Locale.getDefault())
                                )
                        }
                    }
                    return false
                }

            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "DoctorsFragment"
        const val DOCTOR = "doctor"
        const val PATIENT = "patient"
    }
}