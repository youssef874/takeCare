package com.example.takecare

import com.example.takecare.model.Patient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject

fun authenticatedUser() = FirebaseAuth.getInstance().currentUser

fun logOut(){
    FirebaseAuth.getInstance().signOut()
}

 fun convertQuerySnapshotToPatientList(querySnapshot: QuerySnapshot?):List<Patient>{
    val list = ArrayList<Patient>()
    querySnapshot?.let {
        for (document in querySnapshot)
            list.add(document.toObject<Patient>())
    }
    return list
}