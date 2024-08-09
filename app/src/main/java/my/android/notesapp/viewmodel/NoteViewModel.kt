package my.android.notesapp.viewmodel

import NoteRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import my.android.notesapp.data.Note

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    val allNotes: Flow<List<Note>> = repository.getAllNotesFlow()

    fun addNote(note: Note) {
        viewModelScope.launch {
            repository.addNoteToLocal(note)
            repository.addNoteToFirebase(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNoteInLocal(note)
            repository.updateNoteInFirebase(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            try {
                repository.deleteNoteFromFirebase(note.id)
                repository.deleteNoteFromLocal(note)
            } catch (e: Exception) {
                Log.e("NoteViewModel", "Failed to delete note from Firebase", e)
            }
        }
    }

    fun syncNotes() {
        repository.syncNotes(viewModelScope)
    }

    fun deleteLocalNotes() = viewModelScope.launch {
        repository.deleteLocalNotes()
    }
}