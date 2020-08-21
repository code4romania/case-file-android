package ro.code4.casefile.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.casefile.R
import ro.code4.casefile.analytics.Event
import ro.code4.casefile.helper.callSupportCenter
import ro.code4.casefile.helper.startActivityWithoutTrace
import ro.code4.casefile.ui.base.BaseActivity
import ro.code4.casefile.ui.login.AuthenticationActivity
import ro.code4.casefile.ui.login.ProgressDialogAction
import ro.code4.casefile.widget.ProgressDialogFragment

class MainActivity : BaseActivity<MainViewModel>() {
    override val layout: Int
        get() = R.layout.activity_main

    private val firebaseAnalytics: FirebaseAnalytics by inject()
    override val viewModel: MainViewModel by viewModel()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_forms,
                R.id.nav_guide,
                R.id.nav_about
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // This needs to be set after `setupWithNavController`
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        navView.setCheckedItem(R.id.nav_forms)
        // Workaround to allow actions and navigation in the same component
        navView.setNavigationItemSelectedListener { item ->
            val handled = onNavDestinationSelected(item, navController)
            if (handled) {
                drawerLayout.closeDrawer(navView)
                true
            } else {
                when (item.itemId) {
                    R.id.nav_change_polling_station -> {
                        firebaseAnalytics.logEvent(Event.TAP_CHANGE_STATION.title, null)
                        viewModel.notifyChangeRequested()
                        true
                    }
                    R.id.nav_call -> {
                        firebaseAnalytics.logEvent(Event.TAP_CALL.title, null)
                        callSupportCenter()
                        true
                    }
                    else -> false
                }
            }
        }

        navSettings.setOnClickListener {
            Toast.makeText(this, getString(R.string.menu_settings), Toast.LENGTH_SHORT).show()
        }

        navLogout.setOnClickListener {
            viewModel.logout()
        }

        viewModel.onLogoutLiveData().observe(this, Observer {
            startActivityWithoutTrace(AuthenticationActivity::class.java)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun setTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
