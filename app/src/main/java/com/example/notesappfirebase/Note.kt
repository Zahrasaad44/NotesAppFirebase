package com.example.notesappfirebase

data class Note(val pk: String, val noteText: String)
// "pk" of type String because it is a string in Firebase (Firestore)
