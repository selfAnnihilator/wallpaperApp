package com.example.wallpaperapp.ui.collections

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.example.wallpaperapp.data.local.UserDataStore

class CollectionsFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CollectionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_collections, container, false)

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
        val liked = UserDataStore.getUserData(requireContext()).likedImages.toList()
        val collections = UserDataStore.getCollections(requireContext()).sorted()

        adapter = CollectionsAdapter(
            requireContext(),
            liked,
            collections,
            onClickLiked = { openLiked() },
            onClickCollection = { name -> openCollectionDetail(name) }
        )

        recycler.adapter = adapter
    }

    private fun showCreateDialog() {
        val input = EditText(requireContext())

        AlertDialog.Builder(requireContext())
            .setTitle("New Collection")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isEmpty()) return@setPositiveButton
                if (name.equals("Liked", ignoreCase = true)) return@setPositiveButton

                val existing = UserDataStore.getCollections(requireContext())
                    .map { it.lowercase() }
                if (existing.contains(name.lowercase())) return@setPositiveButton

                UserDataStore.createCollection(requireContext(), name)
                loadCollections()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadCollections()
    }

    private fun openLiked() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LikedFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun openCollectionDetail(name: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CollectionDetailFragment.newInstance(name))
            .addToBackStack(null)
            .commit()
    }
}
