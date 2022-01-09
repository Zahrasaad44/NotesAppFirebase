package com.example.notesappfirebase

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application): AndroidViewModel(application) {
    private val notes: MutableLiveData<List<Note>> = MutableLiveData()

    // private val firebaseDB = Firebase.firestore
    private val firebaseDB: FirebaseFirestore = Firebase.firestore

    fun getNotes(): LiveData<List<Note>> {
        return notes
    }

    fun fetchNotes() {
        firebaseDB.collection("notes")
            .get()
            .addOnSuccessListener { result ->
                val userNotes = arrayListOf<Note>()
                for (document in result) {
                    document.data.map { (key, value) ->
                        userNotes.add(
                            Note(
                                document.id,
                                value.toString()
                            )
                        )
                    }
                }
                notes.postValue(userNotes)
            }

            .addOnFailureListener { error ->
                Log.d("viewModel", "ERROR GETTING DOCUMENT, $error")
            }
    }


    fun addNote(aNote: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            val addedNote = hashMapOf(
                "noteContent" to aNote.noteText
            )
            firebaseDB.collection("notes").add(addedNote)
//                .addOnSuccessListener { snapshot ->
//                    Log.d("vv","TASK ADDED $snapshot ////////////////////////////////")
//                }
//                .addOnFailureListener { error ->
//                    Log.d("ll","ERROR ADDING DOCUMENT, $error")
//                }
            fetchNotes()

        }
    }

    fun editNote(noteID: String, noteText: String) {
        CoroutineScope(Dispatchers.IO).launch {
            firebaseDB.collection("notes")
                .get()
                .addOnSuccessListener { snapshot ->
                    for (document in snapshot) {
                        if (document.id == noteID) {
                            firebaseDB.collection("notes").document(noteID)
                                .update("noteContent", noteText)
                        }
                    }
                    fetchNotes()
                }
                .addOnFailureListener {
                    Log.d("up", "FAILED UPDATING document /////////////////////////")
                }
        }
    }

    fun deleteNote(noteID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            firebaseDB.collection("notes")
                .get()
                .addOnSuccessListener { snapshot ->
                    for (document in snapshot) {
                        if (document.id == noteID) {
                            firebaseDB.collection("notes").document(noteID).delete()
                        }
                    }
                    fetchNotes()
                }
                .addOnFailureListener {
                    Log.d("del", "FAILED DELETING document /////////////////////////")
                }
        }
    }

}