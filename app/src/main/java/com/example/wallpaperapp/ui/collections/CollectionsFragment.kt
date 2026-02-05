package com.example.wallpaperapp.ui.collections

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.example.wallpaperapp.data.local.CollectionsStore
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CollectionsFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CollectionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
            R.layout.fragment_collection,
            container,
            false
        )

// ✅ push content below camera / status bar
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val top = insets.getInsets(
                WindowInsetsCompat.Type.statusBars()
            ).top

            v.setPadding(
                v.paddingLeft,
                top + 128,   // extra spacing
                v.paddingRight,
                v.paddingBottom
            )

            insets
        }


        recycler = view.findViewById(R.id.collectionsRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        loadCollections()

        view.findViewById<View>(R.id.btnNewCollection)
            .setOnClickListener {
                showCreateDialog()
            }

        return view
    }

    private fun loadCollections() {
        val names = CollectionsStore
            .getCollections(requireContext())
            .sorted()

        adapter = CollectionsAdapter(requireContext(), names)
        recycler.adapter = adapter
    }

    private fun showCreateDialog() {
        val input = EditText(requireContext())

        AlertDialog.Builder(requireContext())
            .setTitle("New Collection")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    CollectionsStore.createCollection(
                        requireContext(),
                        name
                    )
                    loadCollections()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadCollections()
    }
}
