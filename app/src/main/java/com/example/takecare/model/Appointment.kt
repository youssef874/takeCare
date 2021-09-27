package com.example.takecare.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Appointment(
    val id: String? ="",
    val patientID: String? = "",
    val doctorName: String? = "",
    val date: Timestamp? = Timestamp.now(),
    val speciality: String? = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(patientID)
        parcel.writeString(doctorName)
        parcel.writeParcelable(date, flags)
        parcel.writeString(speciality)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Appointment> {
        override fun createFromParcel(parcel: Parcel): Appointment {
            return Appointment(parcel)
        }

        override fun newArray(size: Int): Array<Appointment?> {
            return arrayOfNulls(size)
        }
    }
}
