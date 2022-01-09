package com.example.notesappfirebase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesappfirebase.databinding.NoteRowBinding

class NotesAdapter(private val activity: MainActivity): RecyclerView.Adapter<NotesAdapter.NotesViewHolder>(){

    private var notes = emptyList<Note>()  //Removed it from the class constructor

    class NotesViewHolder(val binding: NoteRowBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(NoteRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = notes[position]

        holder.binding.apply {
            noteTV.text = note.noteText
            noteCV.setOnClickListener { activity.showEditDeleteDialog(note.pk,
                noteTV.text as String
            ) }
        }
    }

    override fun getItemCount() = notes.size

    fun updateNotes(userNotes: List<Note>) {
        this.notes = userNotes
        notifyDataSetChanged()
    }

}