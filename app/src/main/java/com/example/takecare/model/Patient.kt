package com.example.takecare.model

import android.os.Parcel
import android.os.Parcelable

data class Patient(
    var id: String? = "",
    var name: String? = "",
    var phoneNumber: Int? = 0,
    var address: String? = "",
    var email: String? = "",
    var imageUrl: String?= ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeValue(phoneNumber)
        parcel.writeString(address)
        parcel.writeString(email)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Patient> {
        override fun createFromParcel(parcel: Parcel): Patient {
            return Patient(parcel)
        }

        override fun newArray(size: Int): Array<Patient?> {
            return arrayOfNulls(size)
        }
    }


}