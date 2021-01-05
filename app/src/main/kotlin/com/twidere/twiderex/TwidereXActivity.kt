/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.viewinterop.viewModel
import androidx.core.net.ConnectivityManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.fragment.DialogFragmentNavigator
import com.twidere.twiderex.action.AmbientStatusActions
import com.twidere.twiderex.action.StatusActions
import com.twidere.twiderex.component.foundation.AmbientInAppNotification
import com.twidere.twiderex.launcher.ActivityLauncher
import com.twidere.twiderex.launcher.AmbientLauncher
import com.twidere.twiderex.navigation.Router
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.preferences.ProvidePreferences
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.AmbientActiveAccountViewModel
import com.twidere.twiderex.ui.AmbientActivity
import com.twidere.twiderex.ui.AmbientApplication
import com.twidere.twiderex.ui.AmbientIsActiveNetworkMetered
import com.twidere.twiderex.ui.AmbientViewModelProviderFactory
import com.twidere.twiderex.ui.AmbientWindow
import com.twidere.twiderex.ui.AmbientWindowPadding
import com.twidere.twiderex.ui.ProvideWindowPadding
import com.twidere.twiderex.viewmodel.ActiveAccountViewModel
import org.koin.android.ext.android.inject

class TwidereXActivity : FragmentActivity() {

    val navController by lazy {
        NavHostController(this).apply {
            navigatorProvider.apply {
                addNavigator(ComposeNavigator())
                addNavigator(DialogFragmentNavigator(this@TwidereXActivity, supportFragmentManager))
            }
        }
    }

    private lateinit var launcher: ActivityLauncher
    private val isActiveNetworkMetered = MutableLiveData(false)

    private val statusActions: StatusActions by inject()

    private val inAppNotification: InAppNotification by inject()

    private val connectivityManager: ConnectivityManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = ActivityLauncher(activityResultRegistry)
        lifecycle.addObserver(launcher)
        isActiveNetworkMetered.postValue(
            ConnectivityManagerCompat.isActiveNetworkMetered(
                connectivityManager
            )
        )
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    isActiveNetworkMetered.postValue(
                        ConnectivityManagerCompat.isActiveNetworkMetered(
                            connectivityManager
                        )
                    )
                }
            },
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        setContent {
            val accountViewModel = viewModel<ActiveAccountViewModel>()
            val account by accountViewModel.account.observeAsState()
            val isActiveNetworkMetered by isActiveNetworkMetered.observeAsState(initial = false)
            Providers(
                AmbientInAppNotification provides inAppNotification,
                AmbientLauncher provides launcher,
                AmbientWindow provides window,
                AmbientViewModelProviderFactory provides defaultViewModelProviderFactory,
                AmbientActiveAccount provides account,
                AmbientApplication provides application,
                AmbientStatusActions provides statusActions,
                AmbientActivity provides this,
                AmbientActiveAccountViewModel provides accountViewModel,
                AmbientIsActiveNetworkMetered provides isActiveNetworkMetered
            ) {
                ProvidePreferences {
                    ProvideWindowPadding {
                        val windowPadding = AmbientWindowPadding.current
                        Box(
                            modifier = Modifier.padding(windowPadding)
                        ) {
                            Router(
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}
