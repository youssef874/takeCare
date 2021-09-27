package com.example.takecare.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.takecare.R
import com.example.takecare.adapter.AppointmentAdapter
import com.example.takecare.authenticatedUser
import com.example.takecare.databinding.FragmentMyAppointmentBinding
import com.example.takecare.logOut
import com.example.takecare.model.Appointment
import com.example.takecare.model.Doctor
import com.example.takecare.model.Patient
import com.example.takecare.viewmodel.SharedViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging

class MyAppointmentFragment : Fragment() {

    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var patient: Patient

    private var _binding: FragmentMyAppointmentBinding? = null
    private var isVisibleMenu = true
    private val period = 1800000L
    private val TOPIC = "appointment"

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        if (authenticatedUser() == null){
            binding.noAppointmentText.visibility = View.VISIBLE
            binding.appointmentRecycleView.visibility = View.INVISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyAppointmentBinding.inflate(inflater, container, false)

        binding.apply {
            lifecycleOwner = this@MyAppointmentFragment
            appointmentViewModel = viewModel
            appointmentRecycleView.adapter = AppointmentAdapter(
                AppointmentAdapter.ONClickListener {
                    displayAlertDialog(it)
                })
        }

        createChannel(
            getString(R.string.appointment_notification_id),
            getString(R.string.request_appointment)
        )

        subscribeTopic()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.patient.observe(viewLifecycleOwner, {
            if (it != null)
                patient = it
        })

        viewModel.appointments.observe(viewLifecycleOwner, {
            if (it != null && authenticatedUser() != null)
                viewModel.getPatientAppointment(it, patient)
        })

        viewModel.patientAppointment.observe(viewLifecycleOwner, {
            if (it != null){
                binding.noAppointmentText.visibility = View.INVISIBLE
                it.forEach { appointment ->
                    appointment
                        .date?.let { it1 -> viewModel.startNotification(it1,period) }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.authenticated_user_menu, menu)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (authenticatedUser() == null) {
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
                binding.noAppointmentText.visibility = View.VISIBLE
                binding.appointmentRecycleView.visibility = View.INVISIBLE
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
        val action =
            MyAppointmentFragmentDirections.actionMyAppointmentFragmentToProfileFragment(patient)
        findNavController().navigate(action)
        return true
    }

    companion object {
        const val TAG = "MyAppointmentFragment"
    }

    private fun displayAlertDialog(appointment: Appointment) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.change_appointment_string)
            .setMessage(R.string.change_appointment_body)
            .setPositiveButton(R.string.email_dialog_positive) { _, _ ->
                sendEmail(appointment)
            }
            .show()
    }

    private fun sendEmail(appointment: Appointment) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto:")
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, CreateAccountFragment.EMAIL)
            val subject = getString(R.string.change_appointment_string)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(
                Intent.EXTRA_TEXT, getString(
                    R.string.change_appointment_email_body,
                    patient.name,
                    patient.id,
                    appointment.id
                )
            )
        }
        if (activity?.let { intent.resolveActivity(it.packageManager) } != null) {
            startActivity(intent)
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

    private fun subscribeTopic() {
        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            .addOnCompleteListener { task ->
                var msg = getString(R.string.message_subscribed)
                if (!task.isSuccessful) {
                    msg = getString(R.string.message_subscribe_failed)
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        // [END subscribe_topics]
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}