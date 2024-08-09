package my.android.notesapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.android.notesapp.R
import my.android.notesapp.data.AppDatabase
import my.android.notesapp.data.Note
import my.android.notesapp.databinding.FragmentAddEditNoteBinding
import java.util.UUID

class AddEditNoteFragment : Fragment(), MenuProvider {

    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private var noteId: String = "-1"
    private var originalTitle: String = ""
    private var originalContent: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getDatabase(requireContext())

        val args: AddEditNoteFragmentArgs by navArgs()
        noteId = args.noteId

        if (noteId != "-1") {
            lifecycleScope.launch {
                loadNote()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            onBackPressed()
        }

        requireActivity().addMenuProvider(this, viewLifecycleOwner)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.add_edit_note_menu, menu)
    }

    override fun onPrepareMenu(menu: Menu) {
        super.onPrepareMenu(menu)

        val editItem = menu.findItem(R.id.action_edit)
        if (editItem != null) {
            editItem.isVisible = false
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveNote()
                true
            }
            else -> false
        }
    }

    private fun onBackPressed() {
        if (hasChanges()) {
            showSaveChangesDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun saveNote() {
        val title = binding.titleEditText.text.toString()
        val content = binding.noteEditText.text.toString()

        if (title.isNotEmpty() && content.isNotEmpty()) {
            lifecycleScope.launch {
                performSaveNote(title, content)
            }
        } else {
            if (title.isEmpty()) {
                binding.titleEditText.error = "Title cannot be empty"
            }
            if (content.isEmpty()) {
                binding.noteEditText.error = "Note content cannot be empty"
            }
        }
    }

    private suspend fun performSaveNote(title: String, content: String) {
        withContext(Dispatchers.IO) {
            if (noteId == "-1") {
                val uniqueId = UUID.randomUUID().toString()
                db.noteDao().insert(Note(uniqueId, title, content))
            } else {
                db.noteDao().update(Note(noteId, title, content))
            }
        }
        withContext(Dispatchers.Main) {
            findNavController().navigateUp()
        }
    }

    private suspend fun loadNote() {
        withContext(Dispatchers.IO) {
            val note = db.noteDao().getById(noteId.toString())
            withContext(Dispatchers.Main) {
                binding.titleEditText.setText(note.title)
                binding.noteEditText.setText(note.content)
                originalTitle = note.title
                originalContent = note.content
            }
        }
    }

    private fun hasChanges(): Boolean {
        val currentTitle = binding.titleEditText.text.toString()
        val currentContent = binding.noteEditText.text.toString()
        return if (noteId == "-1") {
            currentTitle.isNotEmpty() || currentContent.isNotEmpty()
        } else {
            currentTitle != originalTitle || currentContent != originalContent
        }
    }

    private fun showSaveChangesDialog() {
        val dialog = SaveChangesDialogFragment(
            onSave = { saveNote() },
            onDiscard = { findNavController().navigateUp() }
        )
        dialog.show(parentFragmentManager, "SaveChangesDialog")
    }
}
