package blue.aodev.animeultimetv.presentation.common

import android.content.Context
import android.support.v17.leanback.widget.ImageCardView
import android.support.v17.leanback.widget.Presenter
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import blue.aodev.animeultimetv.R

abstract class BaseCardPresenter(
        val cardImageWidth: Int,
        val cardImageHeight: Int
) : Presenter() {

    private var hasResources = false
    protected var selectedBackgroundColor = -1
    protected var defaultBackgroundColor = -1

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        if (!hasResources) {
            initResources(parent.context)
            hasResources = true
        }

        val cardView = object : ImageCardView(parent.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        // Setup image
        cardView.setMainImageDimensions(cardImageWidth, cardImageHeight)
        cardView.setMainImageScaleType(ImageView.ScaleType.FIT_XY)

        updateCardBackgroundColor(cardView, false)
        return ViewHolder(cardView)
    }

    private fun initResources(context: Context) {
        defaultBackgroundColor = ContextCompat.getColor(context, R.color.card_background_default)
        selectedBackgroundColor = ContextCompat.getColor(context, R.color.card_background_selected)
    }

    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) selectedBackgroundColor else defaultBackgroundColor

        // Both background colors should be set because the view's
        // background is temporarily visible during animations.
        view.setBackgroundColor(color)
        view.findViewById<View>(R.id.info_field).setBackgroundColor(color)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView

        // Remove references to images so that the garbage collector can free up memory.
        cardView.badgeImage = null
        cardView.mainImage = null
    }
}