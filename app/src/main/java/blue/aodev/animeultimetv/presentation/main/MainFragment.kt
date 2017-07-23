package blue.aodev.animeultimetv.presentation.main

import android.os.Bundle
import android.support.v17.leanback.app.BrowseFragment
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.HeaderItem
import android.support.v17.leanback.widget.ListRow
import android.support.v17.leanback.widget.ListRowPresenter
import blue.aodev.animeultimetv.R
import blue.aodev.animeultimetv.domain.AnimeRepository
import blue.aodev.animeultimetv.domain.model.AnimeSummary
import blue.aodev.animeultimetv.domain.model.EpisodeReleaseSummary
import blue.aodev.animeultimetv.extensions.fromBgToUi
import blue.aodev.animeultimetv.extensions.getColorCompat
import blue.aodev.animeultimetv.presentation.animedetails.AnimeDetailsActivity
import blue.aodev.animeultimetv.presentation.application.MyApplication
import blue.aodev.animeultimetv.presentation.common.AnimeCardPresenter
import blue.aodev.animeultimetv.presentation.common.EpisodeReleaseCardPresenter
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class MainFragment : BrowseFragment() {

    @Inject
    lateinit var animeRepository: AnimeRepository

    private lateinit var categoryRowAdapter: ArrayObjectAdapter
    private lateinit var topAnimesAdapter: ArrayObjectAdapter
    private lateinit var recentEpisodesAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.graph.inject(this)

        animeRepository.getTopAnimes()
                .fromBgToUi()
                .subscribeBy(
                        onNext = { showTopAnimes(it) }
                )

        animeRepository.getRecentEpisodes()
                .fromBgToUi()
                .subscribeBy(
                        onNext = { showRecentEpisodes(it) }
                )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setOnSearchClickedListener {
            activity.onSearchRequested()
        }

        categoryRowAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = categoryRowAdapter

        setOnItemViewClickedListener { _, item, _, _ ->
            when (item) {
                is AnimeSummary -> showAnimeDetails(item)
                is EpisodeReleaseSummary -> showAnimeDetails(item.animeSummary)
            }
        }

        brandColor = activity.getColorCompat(R.color.colorPrimaryDark)
    }

    private fun showTopAnimes(topAnimes: List<AnimeSummary>) {
        val header = HeaderItem(getString(R.string.main_topAnimes_title))
        val presenter = AnimeCardPresenter()
        topAnimesAdapter = ArrayObjectAdapter(presenter)
        val row = ListRow(header, topAnimesAdapter)

        topAnimesAdapter.addAll(0, topAnimes)
        categoryRowAdapter.add(row)
        //TODO replace instead of hiding
    }

    private fun showRecentEpisodes(recentEpisodes: List<EpisodeReleaseSummary>) {
        val header = HeaderItem(getString(R.string.main_recentEpisodes_title))
        val presenter = EpisodeReleaseCardPresenter()
        recentEpisodesAdapter = ArrayObjectAdapter(presenter)
        val row = ListRow(header, recentEpisodesAdapter)

        recentEpisodesAdapter.addAll(0, recentEpisodes)
        categoryRowAdapter.add(row)
        //TODO replace instead of hiding
    }

    private fun showAnimeDetails(anime: AnimeSummary) {
        val intent = AnimeDetailsActivity.getIntent(activity, anime)
        activity.startActivity(intent)
    }
}