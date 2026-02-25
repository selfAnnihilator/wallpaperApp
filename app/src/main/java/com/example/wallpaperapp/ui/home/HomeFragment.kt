package com.example.wallpaperapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.example.wallpaperapp.data.wallhaven.ApiClient
import com.example.wallpaperapp.data.wallhaven.WallhavenWallpaper
import kotlinx.coroutines.launch
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.view.inputmethod.InputMethodManager
import android.content.Context
import androidx.activity.OnBackPressedCallback
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.example.wallpaperapp.data.local.SettingsStore

class HomeFragment : Fragment() {

    private lateinit var adapter: WallpaperAdapter
    private val wallpaperList = mutableListOf<WallhavenWallpaper>()
    private var currentPage = 1
    private var isLoading = false
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var seed = System.currentTimeMillis().toString()
    private val loadedIds = mutableSetOf<String>()
    private var currentQuery: String? = null
    private var isSearching = false
    private val categoryMap = mapOf(

        "Nature" to "nature",
        "Minimal" to "minimal",
        "Abstract" to "abstract",
        "Anime" to "anime",
        "Dark" to "dark",
        "AMOLED" to "amoled",
        "Tech" to "technology",
        "Space" to "space",
        "Cars" to "car"
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        swipeRefresh = view.findViewById(R.id.swipeRefresh)

        swipeRefresh.setOnRefreshListener {
            currentPage = 1
            seed = System.currentTimeMillis().toString()
            loadedIds.clear()
            wallpaperList.clear()
            adapter.notifyDataSetChanged()
            fetchWallpapers(currentPage)
        }


        val recyclerView = view.findViewById<RecyclerView>(R.id.wallpaperRecyclerView)

        val layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.layoutManager = layoutManager

        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = null

        val spacingInPx = resources.getDimensionPixelSize(R.dimen.grid_spacing)

        recyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = 2,
                spacing = spacingInPx,
                includeEdge = true
            )
        )

        // 🔹 Adapter now uses API data
        adapter = WallpaperAdapter(wallpaperList) { wallpaper ->
            // Next step: open fullscreen preview using wallpaper.path
        }

        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading &&
                    visibleItemCount + firstVisibleItemPosition >= totalItemCount - 4 &&
                    firstVisibleItemPosition >= 0
                ) {
                    fetchWallpapers(currentPage)
                }
            }
        })

        val searchInput = view.findViewById<EditText>(R.id.searchInput)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    if (isSearching) {
                        exitSearchMode()
                    } else {
                        // Disable this callback and let system handle back
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
        )

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchInput.text?.toString()?.trim()

                if (!query.isNullOrEmpty()) {
                    performSearch(query)
                }

                hideKeyboard()
                true
            } else {
                false
            }
        }

        view.findViewById<View>(R.id.homeRoot).setOnClickListener {
            hideKeyboard()
        }

        searchInput.setOnFocusChangeListener { _, hasFocus ->
            isSearching = hasFocus
            searchInput.isCursorVisible = hasFocus
        }

        searchInput.setOnClickListener {
            searchInput.isFocusable = true
            searchInput.isFocusableInTouchMode = true
            searchInput.isCursorVisible = true
            searchInput.requestFocus()

            val imm = requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT)

            isSearching = true
        }

        val chipGroup = view.findViewById<ChipGroup>(R.id.categoryChips)

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->

            // ✅ No chip selected → ALL feed
            if (checkedIds.isEmpty()) {
                currentQuery = null
                reloadFeed()
                return@setOnCheckedStateChangeListener
            }

            val chip = group.findViewById<Chip>(checkedIds[0])

            chip.animate()
                .scaleX(1.08f)
                .scaleY(1.08f)
                .setDuration(120)
                .withEndAction {
                    chip.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120)
                        .start()
                }
                .start()

            val category = chip.text.toString()

            val query = categoryMap[category] ?: category
            performSearch(query)
        }


        // 🔹 Fetch wallpapers from API
        fetchWallpapers(currentPage)

        return view
    }

    private fun fetchWallpapers(page: Int) {
        if (isLoading) return
        isLoading = true

        val sortingMode = when {
            currentQuery == null -> "random"      // ALL feed
            else -> "relevance"                  // category/search
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val nsfwEnabled = SettingsStore.isNsfwEnabled(requireContext())
                val safeSearch = SettingsStore.isSafeSearchEnabled(requireContext())
                val purity = if (safeSearch) {
                    "100"
                } else {
                    if (nsfwEnabled) "111" else "100"
                }
                val response = ApiClient.api.getWallpapers(
                    apiKey = "OUui4hMvA8P1GP8nQ5PikaM5o8h1DOf7",
                    seed = seed,
                    query = currentQuery,
                    page = page,
                    purity = purity,
                    sorting = sortingMode,
                    order = "desc"
                )

                val startSize = wallpaperList.size
                wallpaperList.addAll(response.data)
                adapter.notifyItemRangeInserted(
                    startSize,
                    response.data.size
                )

                currentPage++

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
                swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun performSearch(query: String) {
        currentQuery = query
        currentPage = 1
        seed = System.currentTimeMillis().toString()

        loadedIds.clear()
        wallpaperList.clear()
        adapter.notifyDataSetChanged()

        fetchWallpapers(currentPage)
    }

    private fun hideKeyboard() {
        val imm = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun exitSearchMode() {
        val searchInput =
            requireView().findViewById<EditText>(R.id.searchInput)

        searchInput.setText("")

        // HARD stop search mode
        searchInput.isCursorVisible = false
        searchInput.clearFocus()
        searchInput.isFocusable = false
        searchInput.isFocusableInTouchMode = false

        hideKeyboard()
        isSearching = false
    }

    override fun onResume() {
        super.onResume()

        resetSearchBar()
    }

    private fun resetSearchBar() {
        val searchInput =
            view?.findViewById<EditText>(R.id.searchInput) ?: return

        searchInput.setText("")
        searchInput.clearFocus()
        searchInput.isCursorVisible = false
        searchInput.isFocusable = false
        searchInput.isFocusableInTouchMode = false

        hideKeyboard()
        isSearching = false
    }

    private fun reloadFeed() {
        currentPage = 1
        seed = System.currentTimeMillis().toString()
        wallpaperList.clear()
        adapter.notifyDataSetChanged()
        fetchWallpapers(currentPage)
    }

}
