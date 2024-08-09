package my.android.notesapp.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import my.android.notesapp.R
import my.android.notesapp.data.Note

enum class NoteAction {
    EDIT, DELETE
}

class NotesAdapter(
    private var notes: List<Note>,
    private val actionHandler: (Note, NoteAction) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_NOTE = 0
    private val ITEM_NO_RESULTS = 1

    private val colors = listOf(
        "#FFCDD2", "#F8BBD0", "#E1BEE7", "#D1C4E9",
        "#C5CAE9", "#BBDEFB", "#B3E5FC", "#B2EBF2",
        "#B2DFDB", "#C8E6C9", "#DCEDC8", "#F0F4C3",
        "#FFF9C4", "#FFECB3", "#FFE0B2", "#FFCCBC",
        "#D7CCC8", "#F5F5F5", "#CFD8DC", "#FFE57F",
        "#FFD740", "#FFC400", "#FFAB00", "#FF6F00",
        "#FF8A65", "#FF7043", "#FF5722", "#F4511E"
        )

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val noteTextView: TextView = itemView.findViewById(R.id.noteTextView)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val deleteIcon: ImageButton = itemView.findViewById(R.id.deleteIcon)

        fun bind(note: Note) {
            titleTextView.text = note.title
            noteTextView.text = note.content
            cardView.setCardBackgroundColor(Color.parseColor(colors[adapterPosition % colors.size]))

            titleTextView.visibility = View.VISIBLE
            noteTextView.visibility = View.VISIBLE
            deleteIcon.visibility = View.GONE

            itemView.setOnClickListener {
                actionHandler(note, NoteAction.EDIT)
            }

            itemView.setOnLongClickListener {
                cardView.setCardBackgroundColor(Color.RED)
                titleTextView.visibility = View.GONE
                noteTextView.visibility = View.GONE
                deleteIcon.visibility = View.VISIBLE
                true
            }

            deleteIcon.setOnClickListener {
                actionHandler(note, NoteAction.DELETE)
            }
        }
    }

    inner class NoResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_NOTE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
                NoteViewHolder(view)
            }
            ITEM_NO_RESULTS -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.no_results_item, parent, false)
                NoResultsViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ITEM_NOTE -> {
                val noteHolder = holder as NoteViewHolder
                val note = notes[position]
                noteHolder.bind(note)
            }
            ITEM_NO_RESULTS -> {
            }
        }
    }

    override fun getItemCount(): Int {
        return if (notes.isEmpty()) 1 else notes.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (notes.isEmpty()) ITEM_NO_RESULTS else ITEM_NOTE
    }

    fun updateNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}
