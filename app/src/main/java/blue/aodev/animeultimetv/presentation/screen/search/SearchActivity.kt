package blue.aodev.animeultimetv.presentation.screen.search

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import blue.aodev.animeultimetv.R
import blue.aodev.animeultimetv.presentation.screen.LeanbackActivity

class SearchActivity : LeanbackActivity() {

    private lateinit var fragment: SearchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        fragment = fragmentManager.findFragmentById(R.id.search_fragment) as SearchFragment
    }

    override fun onSearchRequested(): Boolean {
        if (fragment.hasResults()) {
            startActivity(Intent(this, SearchActivity::class.java))
        } else {
            fragment.startRecognition()
        }
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // If there are no results found, press the left key to reselect the microphone
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && !fragment.hasResults()) {
            fragment.focusOnSearch()
        }
        return super.onKeyDown(keyCode, event)
    }
}
