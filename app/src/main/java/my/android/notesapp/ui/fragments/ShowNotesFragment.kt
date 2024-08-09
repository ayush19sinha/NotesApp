package my.android.notesapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.android.notesapp.R
import my.android.notesapp.data.AppDatabase
import my.android.notesapp.databinding.FragmentShowNotesBinding

class ShowNotesFragment : Fragment(), MenuProvider {

    private var _binding: FragmentShowNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private var noteId: String = "-1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            noteId = it.getString("noteId", "-1")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getDatabase(requireContext())

        requireActivity().addMenuProvider(this, viewLifecycleOwner)

        lifecycleScope.launch {
            loadNote()
        }
    }

    private suspend fun loadNote() {
        withContext(Dispatchers.IO) {
            val note = db.noteDao().getById(noteId)
            withContext(Dispatchers.Main) {
                binding.note = note
            }
        }
    }
    private suspend fun deleteNote(noteId: String) {
        withContext(Dispatchers.IO) {
            db.noteDao().deleteById(noteId)
        }
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.show_notes_menu, menu)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val action = ShowNotesFragmentDirections.actionShowNotesFragmentToAddEditNoteFragment(noteId)
                findNavController().navigate(action)
                true
            }
            R.id.action_share -> {
                shareNote()
                true
            }
            R.id.action_delete -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    deleteNote(noteId)
                }
                findNavController().navigateUp()
                true
            }
            else -> false
        }
    }

    private fun shareNote() {
        val title = binding.noteTitleTextView.text.toString()
        val content = binding.noteContentTextView.text.toString()
        val shareText = """📌Note:: Title: $title   Content: $content """.trimIndent()
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share note via"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
