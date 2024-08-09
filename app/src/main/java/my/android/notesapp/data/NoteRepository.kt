import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import my.android.notesapp.data.Note
import my.android.notesapp.data.NoteDao

class NoteRepository(private val noteDao: NoteDao) {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserNotesRef(): DatabaseReference {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        return database.getReference("users").child(userId).child("notes")
    }

    suspend fun addNoteToLocal(note: Note) = withContext(Dispatchers.IO) {
        try {
            noteDao.insert(note)
        } catch (e: Exception) {
        }
    }

    suspend fun updateNoteInLocal(note: Note) = withContext(Dispatchers.IO) {
        try {
            noteDao.update(note)
        } catch (e: Exception) {
        }
    }

    suspend fun deleteNoteFromLocal(note: Note) = withContext(Dispatchers.IO) {
        try {
            noteDao.delete(note)
        } catch (e: Exception) {
        }
    }

    fun getAllNotesFlow(): Flow<List<Note>> = noteDao.getAllFlow()

    suspend fun addNoteToFirebase(note: Note) = withContext(Dispatchers.IO) {
        try {
            getUserNotesRef().child(note.id).setValue(note).await()
        } catch (e: Exception) {
            Log.e("NoteRepository", "Error adding note to Firebase", e)
        }
    }

    suspend fun updateNoteInFirebase(note: Note) = withContext(Dispatchers.IO) {
        try {
            getUserNotesRef().child(note.id).setValue(note).await()
        } catch (e: Exception) {
            Log.e("NoteRepository", "Error updating note in Firebase", e)
        }
    }

    suspend fun deleteNoteFromFirebase(noteId: String) = withContext(Dispatchers.IO) {
        try {
            getUserNotesRef().child(noteId).removeValue().await()
        } catch (e: Exception) {
            Log.e("NoteRepository", "Error deleting note from Firebase", e)
        }
    }

    fun syncNotes(coroutineScope: CoroutineScope) {
        syncNotesFromFirebaseToLocal()
        syncNotesFromLocalToFirebase(coroutineScope)
    }

    private fun syncNotesFromFirebaseToLocal() {
        getUserNotesRef().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notes = mutableListOf<Note>()
                for (dataSnapshot in snapshot.children) {
                    val note = dataSnapshot.getValue(Note::class.java)
                    note?.let { notes.add(it) }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        noteDao.insertNotes(notes)
                    } catch (e: Exception) {
                        Log.e("NoteRepository", "Error inserting notes from Firebase", e)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("NoteRepository", "Firebase data sync cancelled", error.toException())
            }
        })
    }

    private fun syncNotesFromLocalToFirebase(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            getAllNotesFlow().collect { localNotes ->
                localNotes.forEach { note ->
                    addNoteToFirebase(note)
                }
            }
        }
    }

    suspend fun deleteLocalNotes() = withContext(Dispatchers.IO) {
        try {
            noteDao.deleteAll()
        } catch (e: Exception) {
            Log.e("NoteRepository", "Error deleting local notes", e)
        }
    }
}
