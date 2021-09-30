package com.example.takecare

import com.google.firebase.auth.FirebaseAuth

fun authenticatedUser() = FirebaseAuth.getInstance().currentUser

fun logOut(){
    FirebaseAuth.getInstance().signOut()
}
