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
 * and shared between them
 */
class SharedViewModel(private val app: Application ):AndroidViewModel(app) {

    //The root of fireStore
    private val db = Firebase.firestore

    //This represent as id for sender of th PendingIntent and as all the pending
    // do the same thing so all have the same integer value in this class or
    // in the extension file
    private val REQUEST_CODE = 0

    //This property will take all the doctors data in the database as QuerySnapshot
    private val doctorSnapshotData = Repository(db.collection("doctors"))
            as LiveData<QuerySnapshot?>

    //This property will take all the patients data in the database as QuerySnapshot
    private val patientSnapshot = Repository(db.collection("patient"))

    //This property will take all the appointment data in the database as QuerySnapshot
    private val appointmentSnapshot = Repository(db.collection("appointment"))

    //This property will take all the doctors data in the database as List<Doctor>
    private var _doctors = doctorSnapshotData.map {
        convertQuerySnapshotToDoctorList(it)
    } as MutableLiveData<List<Doctor>?>
    val doctors: LiveData<List<Doctor>?>
        get() = _doctors

    //This property will the authenticated patient data from the database
    private var _patients = patientSnapshot.map {
        convertQuerySnapshotToPatientList(it)
    } as MutableLiveData<List<Patient>?>
    val patients: LiveData<List<Patient>?>
        get() = _patients

    //This property will take all the appointment data in the database as List<Appointment>
    private var _patient = MutableLiveData<Patient?>()
    val patient: LiveData<Patient?>
        get() = _patient

    //This property will take all the doctors data in the database as List<Doctor>
    private var _appointments = appointmentSnapshot.map {
        convertQuerySnapshotToAppointmentList(it)
    } as MutableLiveData<List<Appointment>?>
    val appointments: LiveData<List<Appointment>?>
        get() = _appointments

    //This property will the authenticated patient appointments data from the database
    private var _patientAppointment = MutableLiveData<List<Appointment>?>()
    val patientAppointment: LiveData<List<Appointment>?>
        get() = _patientAppointment

    //This property will let other application have right to access
    // certain part of the app, like open the application when user click the notification
    private var notifyPendingIntent: PendingIntent

    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    //This property hold an intent from application to alarmReceiver how
    // inherit from AlarmManager to launch the notification
    // when this application is not running
    private val notifyIntent = Intent(app, AlarmReceiver::class.java)

    init {
        notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            REQUEST_CODE,
            notifyIntent,
            //modify whatever current PendingIntent is associated with the
            // Intent you are supplying.
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /**
     * This function will change _doctors property according to name search result
     * this property is the one how appears in the ui so if we changed the ui will change too
     * @param search: the desired doctor name
     */
    fun getTheSearchedResult(search:String){
        _doctors.value = getResultByName(search)
    }

    /**
     * This function will get the authenticated patient appointment and set it
     * to _patientAppointment property
     * @param list: all appointment in the database
     * @param patient: the authenticated patient
     */
    fun getPatientAppointment(list: List<Appointment>,patient: Patient){
        val temp = ArrayList<Appointment>()
        list.forEach {appointment ->
            if (appointment.patientID == patient.id)
                temp.add(appointment)
        }
        _patientAppointment.value = temp
    }

    /**
     * This function will search for doctors according to his name
     * @param name: the doctor name searched for
     * @return : list of doctors with name match the param or empty list
     * if there is no name match
     */
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

    /**
     * This function responsible on converting [QuerySnapshot] to [List<Doctor>]
     * @param querySnapshot: the list to be converted
     * @return List<Doctor>
     */
    private fun convertQuerySnapshotToDoctorList(querySnapshot: QuerySnapshot?):List<Doctor>{
        val list = ArrayList<Doctor>()
        querySnapshot?.let {
            for (document in querySnapshot)
                list.add(document.toObject<Doctor>())
        }
        return list
    }

    /**
     * This function responsible in finding in all the patient in the database
     * the authenticated one by compare his email with authenticated user email
     * verified by firebase authentication system and set the result to _patient
     * property
     * @param patients: all the patient in the database
     */
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

    /**
     * This function responsible on converting [QuerySnapshot] to [List<Appointment>]
     * @param querySnapshot: the list to be converted
     * @return List<Appointment>
     */
    private fun convertQuerySnapshotToAppointmentList(querySnapshot: QuerySnapshot?):List<Appointment>{
        val list = ArrayList<Appointment>()
        querySnapshot?.let {
            for (document in querySnapshot)
                list.add(document.toObject<Appointment>())
        }
        return list
    }

    /**
     * This function responsible calculating the desired time to start the notification
     * and trigger it
     * @param timestamp: the appointment time
     * @param period: the time before timestamp to trigger the notification
     */
    fun startNotification(timestamp: Timestamp,period: Long){
        val currentTimestamp = Timestamp.now()
        val currentMillisecond = currentTimestamp.seconds * 1000
        + currentTimestamp.nanoseconds / 1000000
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val desiredTime:Long = milliseconds-period
           if (currentMillisecond == desiredTime)
               notification(desiredTime)
    }

    /**
     * This function start notification and setup the [AlarmManager]
     */
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