
# Android application to manage patients appointment

This is android application developped with android framwork in kotlin and firebase for backend.To manage patients appointment. 


## Acknowledgements

 - [android developper](https://developer.android.com/)
 - [stack over flow](https://stackoverflow.com/)
 - [firebasedocumentaion](https://firebase.google.com/docs/auth/android/start)
 - [meduim](https://medium.com/)


## Documentation

In this section we going to specify our some project stucture.

First Firebase Realtime database structure:

appoinment/ID_APPOINTMENT/{
    date:"appointment date",
    doctorName:"The doctor name of this appointment",
    id:"ID_APPOINTMENT",
    patientId:"The patient id for this appointment",
    speciality:"doctor speciality(eyes ears ...)"
}

doctors/DOCTOR_ID/{
    age:"doctor age",
    id:"DOCTOR_ID",
    imageUrl:"doctor image url",
    name:"doctor name",
    speciality:"doctor speciality"
}

patient/PATIENT_ID/{
    address:"patient location",
    email:"patient email address",
    id:"PATIENT_ID",
    imageUrl:"The patient image url",
    name:"patient name",
    phoneNumber:"The patient phone number"
}

## Demo

In the screnshot file you'll find the demo for this app.


## Installation

To Install the project

First create your project in firebase console and choose android as the the platform your applicatio will support.

Then follow the [guidance](https://console.firebase.google.com/)

Then Add your data your data to the firebase Firestorz database as the documentation stactured.
Don't forget to specify your rule for your database [Security](https://firebase.google.com/docs/database/security)

Then add the images of the doctors and patients to firebase storage as the documentation disciption.

Then add user in the user firebase authentication.

Lastely run your application via your favorite ide(Android studion recommended).