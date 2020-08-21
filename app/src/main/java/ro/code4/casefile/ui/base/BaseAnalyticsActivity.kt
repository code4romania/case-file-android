package ro.code4.casefile.ui.base

import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.android.inject
import ro.code4.casefile.interfaces.AnalyticsScreenName

abstract class BaseAnalyticsActivity<out T : BaseViewModel> : BaseActivity<T>(), AnalyticsScreenName {

    private val firebaseAnalytics: FirebaseAnalytics by inject()

    override fun onResume() {
        super.onResume()

        firebaseAnalytics.setCurrentScreen(this, getString(screenName), null)
    }
}
