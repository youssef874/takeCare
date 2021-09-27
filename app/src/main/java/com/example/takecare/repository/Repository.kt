package com.example.takecare.repository

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*

class Repository
    (private val collectionReference: CollectionReference )
    : LiveData<QuerySnapshot?>(),EventListener<QuerySnapshot> {

    private var listenerRegistration: ListenerRegistration? = null



    override fun onActive() {
        super.onActive()
        if (listenerRegistration == null)
            listenerRegistration = collectionReference
                .addSnapshotListener(this as EventListener<QuerySnapshot>)
    }

    override fun onInactive() {
        super.onInactive()
        listenerRegistration?.remove()
    }

    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null)
            return
        this.value = value
    }
}