package blue.aodev.animeultimetv.presentation.screen.animedetails

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v17.leanback.app.DetailsFragment
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import blue.aodev.animeultimetv.R
import blue.aodev.animeultimetv.domain.model.Anime
import blue.aodev.animeultimetv.domain.model.AnimeSummary
import blue.aodev.animeultimetv.domain.AnimeRepository
import blue.aodev.animeultimetv.utils.extensions.fromBgToUi
import blue.aodev.animeultimetv.presentation.application.MyApplication
import blue.aodev.animeultimetv.presentation.screen.episodes.EpisodesActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class AnimeDetailsFragment : DetailsFragment() {

    companion object {
        val ACTION_EPISODES = 1
    }

    @Inject
    lateinit var animeRepository: AnimeRepository

    private lateinit var globalAdapter: ArrayObjectAdapter
    private lateinit var presenterSelector: ClassPresenterSelector
    private lateinit var detailsRow: DetailsOverviewRow

    private var anime: Anime? = null

    val animeSummary: AnimeSummary by lazy {
        (activity as AnimeDetailsActivity).animeSummary
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.graph.inject(this)

        setupAdapter()
        setupEmptyDetailsRow()

        animeRepository.getAnime(animeSummary.id)
                .fromBgToUi()
                .subscribeBy(
                        onNext = {
                            this.anime = it
                            updateDetailsRow(it)
                        }
                )
    }

    private fun setupAdapter() {
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(
                DetailsDescriptionPresenter(activity), AnimeDetailsOverviewLogoPresenter())

        detailsPresenter.backgroundColor =
                ContextCompat.getColor(activity, R.color.animeDetails_main)
        detailsPresenter.actionsBackgroundColor =
                ContextCompat.getColor(activity, R.color.animeDetails_actions)
        detailsPresenter.initialState = FullWidthDetailsOverviewRowPresenter.STATE_FULL

        detailsPresenter.onActionClickedListener = OnActionClickedListener { action ->
            if (action.id == ACTION_EPISODES.toLong()) {
                val intent = EpisodesActivity.getIntent(activity, animeSummary)
                activity.startActivity(intent)
            }
        }

        presenterSelector = ClassPresenterSelector()
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        globalAdapter = ArrayObjectAdapter(presenterSelector)
        adapter = globalAdapter
    }

    private fun setupEmptyDetailsRow() {
        detailsRow = DetailsOverviewRow(animeSummary)

        Glide.with(this)
                .load(animeSummary.imageUrl)
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable?,
                                                 transition: Transition<in Drawable>?) {
                        detailsRow.imageDrawable = resource
                    }
                })

        val adapter = SparseArrayObjectAdapter()
        adapter.set(ACTION_EPISODES, Action(ACTION_EPISODES.toLong(),
                resources.getString(R.string.details_action_episodes)))
        detailsRow.actionsAdapter = adapter

        globalAdapter.add(detailsRow)
    }

    private fun updateDetailsRow(anime: Anime) {
        detailsRow.item = anime
    }
}
