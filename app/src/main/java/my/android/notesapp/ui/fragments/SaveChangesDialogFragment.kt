package my.android.notesapp.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class SaveChangesDialogFragment(
    private val onSave: () -> Unit,
    private val onDiscard: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d("SaveChangesDialog", "Creating dialog")
        return AlertDialog.Builder(requireContext())
            .setMessage("Save Changes?")
            .setPositiveButton("Save") { _, _ ->
                Log.d("SaveChangesDialog", "Save clicked")
                onSave()
            }
            .setNegativeButton("Discard") { _, _ ->
                Log.d("SaveChangesDialog", "Discard clicked")
                onDiscard()
            }
            .create()
    }
}