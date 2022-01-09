package com.example.notesappfirebase

import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notesappfirebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var recyclerAdapter: NotesAdapter

    lateinit var viewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        viewModel.getNotes().observe(this, { notes ->
            recyclerAdapter.updateNotes(notes) // To populate the RV with the note
        })

        viewModel.fetchNotes() // To display the notes once opening the app


        binding.apply {
            recyclerAdapter = NotesAdapter(this@MainActivity)
            notesRV.adapter = recyclerAdapter
            notesRV.layoutManager = GridLayoutManager(this@MainActivity, 2)
            floatingActionButton.setOnClickListener { showAddNoteDialog() }
        }
    }

    private fun showAddNoteDialog() {
        val dialog = Dialog(this, R.style.Theme_AppCompat_DayNight)
        dialog.setContentView(R.layout.add_note_dialog)
        dialog.setCanceledOnTouchOutside(true)

        val addBtnD = dialog.findViewById<Button>(R.id.addBtnD)
        val addNoteET = dialog.findViewById<EditText>(R.id.addNoteET)

        addBtnD.setOnClickListener {
            if (addNoteET.text.isNotEmpty()) {
                viewModel.addNote(Note("", addNoteET.text.toString()))
                addNoteET.text.clear()
                dialog.dismiss()

            } else {
                Toast.makeText(
                    applicationContext,
                    "Type something to add a note",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        dialog.show()
    }

    fun showEditDeleteDialog(id: String, text: String) {
        val dialog = Dialog(
            this,
            R.style.Theme_AppCompat_DayNight
        )

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.edit_delete_dialog)

        val deleteBtn = dialog.findViewById<Button>(R.id.deleteBtn)
        val editBtn = dialog.findViewById<Button>(R.id.editBtn)
        val editDeleteET = dialog.findViewById<EditText>(R.id.editDeleteET)
        editDeleteET.setText(text)

        editBtn.setOnClickListener {
            viewModel.editNote(id, editDeleteET.text.toString())
            dialog.dismiss()
        }
        deleteBtn.setOnClickListener {
            displayDeleteConformationDialog(id)
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun displayDeleteConformationDialog(id: String) {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                viewModel.deleteNote(id)
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                dialog.cancel()
            })
        val alert = dialogBuilder.create()
        alert.setTitle("Delete Confirmation")
        alert.show()
    }
}

