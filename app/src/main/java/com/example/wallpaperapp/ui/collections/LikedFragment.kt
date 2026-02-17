package com.example.wallpaperapp.ui.collections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.example.wallpaperapp.data.local.FavoritesStore
import com.example.wallpaperapp.ui.home.GridSpacingItemDecoration
import com.google.android.material.appbar.MaterialToolbar

class LikedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_liked, container, false)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.likedToolbar)
        toolbar.title = "Liked"
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val recycler = view.findViewById<RecyclerView>(R.id.likedRecycler)
        val layoutManager = GridLayoutManager(requireContext(), 2)
        recycler.layoutManager = layoutManager
        recycler.setHasFixedSize(true)
        recycler.itemAnimator = null

        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        recycler.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))

        recycler.adapter = LikedAdapter(FavoritesStore.getAllLiked(requireContext()))

        return view
    }

    override fun onResume() {
        super.onResume()
        view?.findViewById<RecyclerView>(R.id.likedRecycler)?.adapter =
            LikedAdapter(FavoritesStore.getAllLiked(requireContext()))
    }
}
