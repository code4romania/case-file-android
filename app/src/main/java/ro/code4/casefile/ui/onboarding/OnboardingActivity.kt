package ro.code4.casefile.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_onboarding.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.casefile.R
import ro.code4.casefile.adapters.OnboardingAdapter
import ro.code4.casefile.adapters.helper.OnboardingScreen
import ro.code4.casefile.helper.startActivityWithoutTrace
import ro.code4.casefile.ui.base.BaseAnalyticsActivity
import ro.code4.casefile.ui.welcome.WelcomeActivity
import java.util.Locale

class OnboardingActivity : BaseAnalyticsActivity<OnboardingViewModel>(),
    OnboardingAdapter.OnLanguageChangedListener {

    override val layout: Int
        get() = R.layout.activity_onboarding
    override val screenName: Int
        get() = R.string.analytics_title_onboarding
    override val viewModel: OnboardingViewModel by viewModel()
    private lateinit var onboardingAdapter: OnboardingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onboarding().observe(this, Observer {
            setData(it)
        })
        viewModel.languageChanged().observe(this, Observer {
            recreate()
        })
        backButton.setOnClickListener {
            onboardingViewPager.setCurrentItem(onboardingViewPager.currentItem - 1, true)
        }
        nextButton.setOnClickListener {
            if (onboardingViewPager.currentItem == onboardingAdapter.itemCount - 1) {
                viewModel.onboardingCompleted()
                startActivityWithoutTrace(WelcomeActivity::class.java)
            } else {
                onboardingViewPager.setCurrentItem(onboardingViewPager.currentItem + 1, true)
            }
        }
        onboardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val lastIdx = onboardingAdapter.itemCount - 1
                val (nextButtonTextResId, backButtonVisibility) = when {
                    position == 0 -> Pair(
                        R.string.onboarding_next,
                        View.GONE
                    )
                    lastIdx == position -> Pair(
                        R.string.onboarding_to_app,
                        View.VISIBLE
                    )
                    else -> Pair(R.string.onboarding_next, View.VISIBLE)
                }
                nextButton.text = getString(nextButtonTextResId)
                nextButton.requestLayout()
                backButton.visibility = backButtonVisibility
            }
        })
    }

    private fun setData(screens: ArrayList<OnboardingScreen>) {
        onboardingAdapter = OnboardingAdapter(this, screens)
        onboardingViewPager.adapter = onboardingAdapter
        onboardingViewPager.currentItem = 0
        indicator.setViewPager(onboardingViewPager)
    }

    override fun onLanguageChanged(locale: Locale) {
        viewModel.changeLanguage(locale)
    }
}
