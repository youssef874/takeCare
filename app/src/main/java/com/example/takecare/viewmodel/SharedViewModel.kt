package com.example.takecare.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.example.takecare.R
import com.example.takecare.authenticatedUser
import com.example.takecare.cancelNotifications
import com.example.takecare.model.Appointment
import com.example.takecare.model.Doctor
import com.example.takecare.model.Patient
import com.example.takecare.receiver.AlarmReceiver
import com.example.takecare.repository.Repository
import com.example.takecare.sendNotification
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

/**
 * This class is an mediator between [Repository] and the bottom nav first destination
 */
class SharedViewModel(private val app: Application ):AndroidViewModel(app) {

    private val db = Firebase.firestore

    private val REQUEST_CODE = 0

    private val doctorSnapshotData = Repository(db.collection("doctors"))
            as LiveData<QuerySnapshot?>

    private val patientSnapshot = Repository(db.collection("patient"))

    private val appointmentSnapshot = Repository(db.collection("appointment"))

    private var _doctors = doctorSnapshotData.map {
        convertQuerySnapshotToDoctorList(it)
    } as MutableLiveData<List<Doctor>?>
    val doctors: LiveData<List<Doctor>?>
        get() = _doctors

    private var _patients = patientSnapshot.map {
        convertQuerySnapshotToPatientList(it)
    } as MutableLiveData<List<Patient>?>
    val patients: LiveData<List<Patient>?>
        get() = _patients

    private var _patient = MutableLiveData<Patient?>()
    val patient: LiveData<Patient?>
        get() = _patient

    private var _appointments = appointmentSnapshot.map {
        convertQuerySnapshotToAppointmentList(it)
    } as MutableLiveData<List<Appointment>?>
    val appointments: LiveData<List<Appointment>?>
        get() = _appointments

    private var _patientAppointment = MutableLiveData<List<Appointment>?>()
    val patientAppointment: LiveData<List<Appointment>?>
        get() = _patientAppointment

    private lateinit var notifyPendingIntent: PendingIntent

    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val notifyIntent = Intent(app, AlarmReceiver::class.java)

    init {
        notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun getTheSearchedResult(search:String){
        _doctors.value = getResultByName(search)
    }

    fun getPatientAppointment(list: List<Appointment>,patient: Patient){
        val temp = ArrayList<Appointment>()
        list.forEach {appointment ->
            if (appointment.patientID == patient.id)
                temp.add(appointment)
        }
        _patientAppointment.value = temp
    }

    private fun getResultByName(name:String):List<Doctor>{
        val tempList = ArrayList<Doctor>()
        if (doctors.value!!.isNotEmpty()){
            doctors.value!!.forEach {
                if (it != null) {
                    it.name?.let { s->
                        if ( s.lowercase(Locale.getDefault()).contains(name))
                            tempList.add(it)
                    }
                }
            }
        }
        return tempList
    }

    private fun convertQuerySnapshotToDoctorList(querySnapshot: QuerySnapshot?):List<Doctor>{
        val list = ArrayList<Doctor>()
        querySnapshot?.let {
            for (document in querySnapshot)
                list.add(document.toObject<Doctor>())
        }
        return list
    }

    fun getAuthenticatedPatient(patients: List<Patient>){
        authenticatedUser()?.let { user ->
            patients.forEach { pat ->
                if (user.email.toString() == pat.email)
                    _patient.value = pat
            }
        }
    }

    private fun convertQuerySnapshotToPatientList(querySnapshot: QuerySnapshot?):List<Patient>{
        val list = ArrayList<Patient>()
        querySnapshot?.let {
            for (document in querySnapshot)
                list.add(document.toObject<Patient>())
        }
        return list
    }

    private fun convertQuerySnapshotToAppointmentList(querySnapshot: QuerySnapshot?):List<Appointment>{
        val list = ArrayList<Appointment>()
        querySnapshot?.let {
            for (document in querySnapshot)
                list.add(document.toObject<Appointment>())
        }
        return list
    }

    fun startNotification(timestamp: Timestamp,period: Long){
        val currentTimestamp = Timestamp.now()
        val currentMillisecond = currentTimestamp.seconds * 1000
        + currentTimestamp.nanoseconds / 1000000
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val desiredTime:Long = currentMillisecond+period
           if (milliseconds == desiredTime)
               notification(desiredTime)
    }

    private fun notification(desiredTime: Long) {
        val notificationManager = ContextCompat.getSystemService(
            app,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.cancelNotifications()
        notificationManager.sendNotification(app.
        getString(R.string.notification_channel_message), app)
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            desiredTime,
            notifyPendingIntent
        )


    }
}