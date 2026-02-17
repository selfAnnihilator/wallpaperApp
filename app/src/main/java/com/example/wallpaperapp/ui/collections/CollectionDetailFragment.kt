package com.example.wallpaperapp.ui.collections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.example.wallpaperapp.data.local.CollectionsStore
import com.example.wallpaperapp.ui.home.GridSpacingItemDecoration
import com.google.android.material.appbar.MaterialToolbar

class CollectionDetailFragment : Fragment() {

    private lateinit var collectionName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectionName = requireArguments().getString(ARG_NAME) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_collection_detail, container, false)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.collectionDetailToolbar)
        toolbar.title = collectionName
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val recycler = view.findViewById<RecyclerView>(R.id.collectionDetailRecycler)
        val layoutManager = GridLayoutManager(requireContext(), 2)
        recycler.layoutManager = layoutManager
        recycler.setHasFixedSize(true)
        recycler.itemAnimator = null

        val spacingInPx = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        recycler.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = 2,
                spacing = spacingInPx,
                includeEdge = true
            )
        )

        val images = CollectionsStore.getImages(requireContext(), collectionName)
        recycler.adapter = CollectionDetailAdapter(images)

        return view
    }

    companion object {
        private const val ARG_NAME = "collection_name"

        fun newInstance(name: String): CollectionDetailFragment {
            val fragment = CollectionDetailFragment()
            val args = Bundle()
            args.putString(ARG_NAME, name)
            fragment.arguments = args
            return fragment
        }
    }
}
