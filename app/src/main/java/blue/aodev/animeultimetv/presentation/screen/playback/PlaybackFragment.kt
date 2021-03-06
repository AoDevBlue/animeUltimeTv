package blue.aodev.animeultimetv.presentation.screen.playback

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.support.v17.leanback.app.PlaybackFragment
import android.support.v17.leanback.app.VideoFragment
import android.support.v17.leanback.app.VideoFragmentGlueHost
import android.util.Log
import blue.aodev.animeultimetv.domain.model.Playlist

class PlaybackFragment : VideoFragment() {

    companion object {
        val TAG = "PlaybackFragment"
    }

    val playlist: Playlist by lazy {
        (activity as PlaybackActivity).playlist
    }

    private lateinit var mediaPlayerGlue: PlaylistMediaPlayerGlue
    private val host = VideoFragmentGlueHost(this)

    private val onAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerAdapter = ExoPlayerAdapter(activity)
        playerAdapter.audioStreamType = AudioManager.USE_DEFAULT_STREAM_TYPE

        mediaPlayerGlue = PlaylistMediaPlayerGlue(activity, playerAdapter, playlist)
        mediaPlayerGlue.host = host
        mediaPlayerGlue.onPlaylistChanged = { playlist -> setCurrentEpisodeIndex(playlist.index) }

        val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.w(TAG, "video player cannot obtain audio focus!")
        }

        backgroundType = PlaybackFragment.BG_LIGHT
    }

    override fun onPause() {
        mediaPlayerGlue.pause()
        super.onPause()
    }

    private fun setCurrentEpisodeIndex(index: Int) {
        (activity as PlaybackActivity).setCurrentEpisodeIndex(index)
    }
}