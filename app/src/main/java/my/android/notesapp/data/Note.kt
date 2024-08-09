package my.android.notesapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey val id: String = "",
    val title: String = "",
    val content: String = ""
)
