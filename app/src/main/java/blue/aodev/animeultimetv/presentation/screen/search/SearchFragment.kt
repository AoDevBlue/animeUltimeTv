package blue.aodev.animeultimetv.presentation.screen.search

import android.os.Bundle
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import android.view.View
import blue.aodev.animeultimetv.R
import blue.aodev.animeultimetv.domain.model.AnimeSummary
import blue.aodev.animeultimetv.domain.AnimeRepository
import blue.aodev.animeultimetv.utils.extensions.fromBgToUi
import blue.aodev.animeultimetv.presentation.screen.animedetails.AnimeDetailsActivity
import blue.aodev.animeultimetv.presentation.application.MyApplication
import blue.aodev.animeultimetv.presentation.common.AnimeCardPresenter
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class SearchFragment : android.support.v17.leanback.app.SearchFragment(),
        android.support.v17.leanback.app.SearchFragment.SearchResultProvider {

    @Inject
    lateinit var animeRepository: AnimeRepository

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    private lateinit var animeAdapter: ArrayObjectAdapter

    private var query = ""
    private var hasResults = false
    private var currentSearch: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.graph.inject(this)

        animeAdapter = ArrayObjectAdapter(AnimeCardPresenter.default(activity))
        setSearchResultProvider(this)
        setOnItemViewClickedListener { _, item, _, _ ->
            if (item is AnimeSummary) { showAnimeDetails(item) } }
    }

    override fun onStart() {
        super.onStart()
        updateVerticalOffset()
        setupSearchOrb()
    }

    override fun getResultsAdapter(): ObjectAdapter {
        return rowsAdapter
    }

    override fun onQueryTextChange(newQuery: String): Boolean {
        search(newQuery)
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        search(query)
        return true
    }

    private fun search(query: String) {
        if (query == this.query) return

        this.query = query

        if (query.length < 2) {
            clearSearchResults()
            return
        }

        currentSearch?.dispose()

        currentSearch = animeRepository.search(query)
                .fromBgToUi()
                .subscribeBy(
                        onNext = { onSearchResult(it) },
                        onError = { onSearchResult(emptyList()) }
                )
    }

    private fun onSearchResult(results: List<AnimeSummary>) {
        animeAdapter.clear()
        hasResults = results.isNotEmpty()
        val row = if (hasResults) {
            animeAdapter.addAll(0, results)
            ListRow(animeAdapter)
        } else {
            val header = HeaderItem(getString(R.string.search_noResults, query))
            ListRow(header, animeAdapter)
        }
        rowsAdapter.clear()
        rowsAdapter.add(row)
        updateVerticalOffset()
    }

    private fun clearSearchResults() {
        rowsAdapter.clear()
    }

    fun hasResults(): Boolean {
        return hasResults
    }

    fun focusOnSearch() {
        view!!.findViewById<View>(R.id.lb_search_bar).requestFocus()
    }

    private fun updateVerticalOffset() {
        // Override the vertical offset. Not really pretty.
        val offsetResId = if (!hasResults()) {
            R.dimen.searchFragment_rowMarginTop_withHeader
        } else {
            R.dimen.searchFragment_rowMarginTop_withoutHeader
        }

        rowsFragment.verticalGridView.windowAlignmentOffset =
                resources.getDimensionPixelSize(offsetResId)
    }

    private fun showAnimeDetails(anime: AnimeSummary) {
        val intent = AnimeDetailsActivity.getIntent(activity, anime)
        activity.startActivity(intent)
    }

    private fun setupSearchOrb() {
        val defaultColor = ContextCompat.getColor(activity, R.color.searchOrb_default)
        val brightColor = ContextCompat.getColor(activity, R.color.searchOrb_bright)
        val orbColors = SearchOrbView.Colors(defaultColor, brightColor)

        setSearchAffordanceColors(orbColors)
        setSearchAffordanceColorsInListening(orbColors)
    }
}