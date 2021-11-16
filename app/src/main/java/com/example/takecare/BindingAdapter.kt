package com.example.avionav

import android.content.res.Resources
import android.media.Image
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.avionav.adapter.DoctorsListAdapter
import com.example.takecare.R
import com.example.takecare.adapter.AppointmentAdapter
import com.example.takecare.model.Appointment
import com.example.takecare.model.Doctor
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Put the planes list to the recycle vie adapter using BindingAdapter of data biding
 * and call it in Doctor_fragment.xml in [RecyclerView] tag in app:listData
 * @param recyclerView: will get from xml
 * @param doctors: the doctor list will be in the xml as variable
 */
@BindingAdapter("listDoctorData")
fun binRecycleView(recyclerView: RecyclerView, doctors:List<Doctor>?){
    val adapter = recyclerView.adapter as DoctorsListAdapter
    adapter.submitList(doctors)
}

/**
 * Toad the img from the url using coil third party library
 * It will be used in doctor_list_item.xml in [ImageView] tag in app:imageUrl
 * @param imageView: will get from xml
 * @param imageUrl: the url to be loaded
 */
@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView,imageUrl: String?){
    imageUrl?.let {
        val imageUri = imageUrl.toUri().buildUpon().scheme("https").build()
        imageView.load(imageUri){
        }
    }
}

@BindingAdapter("listAppointmentData")
fun bindAppointmentRecycleView(recyclerView: RecyclerView, appointments:List<Appointment>?){
    val adapter = recyclerView.adapter as AppointmentAdapter
    adapter.submitList(appointments)
}

@BindingAdapter("localDate")
fun bindDateText(textView: TextView,timestamp: Timestamp){
    val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateString = simpleDateFormat.format(milliseconds)
    textView.text = Resources.getSystem().getString(R.string.appointment_date_string,dateString)
}