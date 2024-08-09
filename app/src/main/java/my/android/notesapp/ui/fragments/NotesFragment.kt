package my.android.notesapp.ui.fragments

import NoteRepository
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import my.android.notesapp.R
import my.android.notesapp.data.AppDatabase
import my.android.notesapp.data.Note
import my.android.notesapp.databinding.FragmentNotesBinding
import my.android.notesapp.ui.adapter.NoteAction
import my.android.notesapp.ui.adapter.NotesAdapter
import my.android.notesapp.viewmodel.NoteViewModel
import my.android.notesapp.viewmodel.NoteViewModelFactory

class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesAdapter: NotesAdapter
    private val viewModel: NoteViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = NoteRepository(database.noteDao())
        NoteViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeNotes()

        binding.addNoteButton.setOnClickListener {
            findNavController().navigate(R.id.action_notesFragment_to_addEditNoteFragment)
        }

        setupMenu()

        viewModel.syncNotes()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu, menu)
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView

                val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
                searchEditText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                searchEditText.setHintTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let { filterNotes(it) }
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        newText?.let { filterNotes(it) }
                        return false
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_info -> {
                        InfoDialogFragment().show(parentFragmentManager, "infoDialog")
                        true
                    }
                    R.id.action_logout -> {
                        logout()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter(emptyList()) { note, action ->
            when (action) {
                NoteAction.EDIT -> showNoteDetails(note.id)
                NoteAction.DELETE -> viewModel.deleteNote(note)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = notesAdapter
    }

    private fun observeNotes() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allNotes.collectLatest { notes ->
                notesAdapter.updateNotes(notes)
                updateEmptyViewVisibility(notes)
            }
        }
    }

    private fun updateEmptyViewVisibility(notes: List<Note>) {
        if (notes.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
        }
    }

    private fun showNoteDetails(noteId: String) {
        val action = NotesFragmentDirections.actionNotesFragmentToShowNotesFragment(noteId)
        findNavController().navigate(action)
    }

    private fun logout() {
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN)
        googleSignInClient.signOut().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                FirebaseAuth.getInstance().signOut()
                deleteLocalNotes()
                val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                findNavController().apply {
                    popBackStack(R.id.loginFragment, false)
                    navigate(R.id.loginFragment)
                }
            } else {
                Toast.makeText(requireContext(), "Logout failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun filterNotes(query: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allNotes.collectLatest { notes ->
                val filteredNotes = notes.filter { note ->
                    note.content.contains(query, true) || note.title.contains(query, true)
                }
                notesAdapter.updateNotes(filteredNotes)
            }
        }
    }

    private fun deleteLocalNotes() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.deleteLocalNotes()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error clearing notes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
