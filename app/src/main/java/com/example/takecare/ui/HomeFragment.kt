package com.example.takecare.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.takecare.R
import com.example.takecare.authenticatedUser
import com.example.takecare.databinding.FragmentDoctorsBinding
import com.example.takecare.databinding.FragmentHomeBinding
import com.example.takecare.logOut
import com.example.takecare.model.Patient
import com.example.takecare.viewmodel.SharedViewModel
import java.util.*
import java.util.jar.Manifest


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels ()
    private lateinit var patient: Patient
    private var isVisibleMenu = true

    private val phoneNumber = 25836911
    private val latitude = 35.837264f
    private val  longitude = 10.590588f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater,container,false)

        binding.locationContainer.setOnClickListener {
            openOnLocation()
        }

        binding.phoneContainer.setOnClickListener {
            makePhoneCall()
        }

        createChannel(
            getString(R.string.appointment_notification_id),
            getString(R.string.request_appointment)
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.patients.observe(viewLifecycleOwner,{
            if (it != null){
                Log.i(DoctorsFragment.TAG,"all $it")
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

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (authenticatedUser() ==null){
            isVisibleMenu = false
            val sinOutItem = menu.findItem(R.id.log_out_menu_item)
            val profileItem = menu.findItem(R.id.profileFragment)
            sinOutItem.isVisible = isVisibleMenu
            profileItem.isVisible = isVisibleMenu
        }
        val searchItem = menu.findItem(R.id.app_bar_search)
        searchItem.isVisible = false
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
                navigateToProfileFragment()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToProfileFragment(): Boolean {
        var action = HomeFragmentDirections.actionHomeFragmentToProfileFragment(patient)
        findNavController().navigate(action)
        return true
    }

    private fun openOnLocation() {
        val locationUri = Uri.parse("geo:$latitude,$longitude?z=")
        val mapIntent = Intent(Intent.ACTION_VIEW,locationUri)
        if (activity?.let { mapIntent.resolveActivity(it.packageManager) } != null) {
            startActivity(mapIntent)
        }
    }

    private fun makePhoneCall() {
        if (context?.let {
                ActivityCompat.checkSelfPermission(it,
                    android.Manifest.permission.CALL_PHONE)
            } != PackageManager.PERMISSION_GRANTED){
            val errorMessage = activity?.getString(R.string.permission_error)
            context?.let {
                Toast.makeText(it,errorMessage,Toast.LENGTH_LONG).show()
            }
            activity?.let {
                ActivityCompat.requestPermissions(it,
                    arrayOf(android.Manifest.permission.CALL_PHONE),1)
            }
        }else{
            startCalling()
        }
    }

    private fun startCalling() {
        val phoneNumberUri = Uri.parse("tel:$phoneNumber")
        val callIntent = Intent(Intent.ACTION_CALL,phoneNumberUri)
        if (activity?.let { callIntent.resolveActivity(it.packageManager) } != null) {
            startActivity(callIntent)
        }
    }

    private fun createChannel(channelID: String,channelName: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelID,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_channel_message)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}