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
package com.twidere.twiderex.scenes.mastodon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.component.LoginLogo
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.navigation.AmbientNavigator
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.viewmodel.mastodon.MastodonSignInViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.getViewModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun MastodonSignInScene() {
    val viewModel = getViewModel<MastodonSignInViewModel>()
    val host by viewModel.host.observeAsState(initial = "")
    val loading by viewModel.loading.observeAsState(initial = false)
    val navController = AmbientNavController.current
    val lifecycleOwner = AmbientLifecycleOwner.current
    val navigator = AmbientNavigator.current
    TwidereXTheme {
        InAppNotificationScaffold {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .weight(1F),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoginLogo(modifier = Modifier.preferredWidth(200.dp))
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.h4,
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .weight(1F),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (loading == true) {
                        CircularProgressIndicator()
                    } else {
                        OutlinedTextField(value = host, onValueChange = { viewModel.setHost(it) })
                        Button(
                            onClick = {
                                GlobalScope.launch {
                                    withContext(Dispatchers.Main) {
                                        // TODO: dynamic key && secret
                                        viewModel.beginOAuth(
                                            host,
                                        ) { target ->
                                            suspendCoroutine {
                                                navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
                                                    "code"
                                                )?.observe(lifecycleOwner) { result ->
                                                    it.resume(result)
                                                    navController.currentBackStackEntry?.savedStateHandle?.remove<String>(
                                                        "code"
                                                    )
                                                }
                                                navigator.mastodonSignInWeb(target)
                                            }
                                        }.takeIf { it }?.let {
                                            navController.popBackStack()
                                        }
                                    }
                                }
                            }
                        ) {
                            Text(text = stringResource(id = R.string.scene_sign_in_sign_in_with_twitter))
                        }
                    }
                    Box(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}
