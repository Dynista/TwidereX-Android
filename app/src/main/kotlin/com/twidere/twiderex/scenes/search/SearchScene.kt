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
package com.twidere.twiderex.scenes.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.twidere.twiderex.R
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.foundation.TextTabsComponent
import com.twidere.twiderex.component.foundation.TopAppBarElevation
import com.twidere.twiderex.component.lazy.itemsPagingGridIndexed
import com.twidere.twiderex.component.lazy.loadState
import com.twidere.twiderex.component.navigation.AmbientNavigator
import com.twidere.twiderex.component.status.StatusDivider
import com.twidere.twiderex.component.status.StatusMediaPreviewItem
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.di.assisted.viewModel
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.AmbientVideoPlayback
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchMediaViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchTweetsViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchUserViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScene(keyword: String) {
    val account = AmbientActiveAccount.current ?: return
    val tweetsViewModel = viewModel<TwitterSearchTweetsViewModel> {
        parametersOf(account, keyword)
    }
    val mediaViewModel = viewModel<TwitterSearchMediaViewModel> {
        parametersOf(account, keyword)
    }
    val usersViewModel = viewModel<TwitterSearchUserViewModel> {
        parametersOf(account, keyword)
    }
    var selectedTab by savedInstanceState { 0 }
    val navigator = AmbientNavigator.current

    TwidereXTheme {
        InAppNotificationScaffold {
            Column {
                Surface(
                    elevation = TopAppBarElevation,
                ) {
                    Column {
                        AppBar(
                            navigationIcon = {
                                AppBarNavigationButton()
                            },
                            elevation = 0.dp,
                            title = {
                                ProvideTextStyle(value = MaterialTheme.typography.body1) {
                                    Row {
                                        Text(
                                            modifier = Modifier
                                                .clickable(
                                                    onClick = {
                                                        navigator.searchInput(keyword)
                                                    },
                                                    indication = null,
                                                )
                                                .align(Alignment.CenterVertically)
                                                .weight(1F),
                                            text = keyword,
                                            maxLines = 1,
                                            textAlign = TextAlign.Start,
                                        )
                                        IconButton(
                                            onClick = {
                                            }
                                        ) {
                                            Icon(imageVector = vectorResource(id = R.drawable.ic_device_floppy))
                                        }
                                    }
                                }
                            }
                        )
                        TextTabsComponent(
                            items = listOf(
                                stringResource(id = R.string.scene_search_tabs_tweets),
                                stringResource(id = R.string.scene_search_tabs_media),
                                stringResource(id = R.string.scene_search_tabs_users),
                            ),
                            selectedItem = selectedTab,
                            onItemSelected = {
                                selectedTab = it
                            },
                        )
                    }
                }
                Box(
                    modifier = Modifier.weight(1F),
                ) {
                    when (selectedTab) {
                        0 -> SearchTweetsContent(tweetsViewModel)
                        1 -> SearchMediasContent(mediaViewModel)
                        2 -> SearchUsersContent(usersViewModel)
                    }
                }
            }
        }
    }
}

@OptIn(IncomingComposeUpdate::class)
@Composable
private fun SearchTweetsContent(viewModel: TwitterSearchTweetsViewModel) {
    val source = viewModel.source.collectAsLazyPagingItems()
    SwipeToRefreshLayout(
        refreshingState = source.loadState.refresh is LoadState.Loading,
        onRefresh = {
            source.refreshOrRetry()
        }
    ) {
        if (source.itemCount > 0) {
            LazyColumn {
                items(source) { item ->
                    item?.let {
                        TimelineStatusComponent(
                            it,
                        )
                        StatusDivider()
                    }
                }
                loadState(source.loadState.append) {
                    source.retry()
                }
            }
        }
    }
}

@OptIn(IncomingComposeUpdate::class)
@Composable
private fun SearchMediasContent(viewModel: TwitterSearchMediaViewModel) {
    val source = viewModel.source.collectAsLazyPagingItems()
    Providers(
        AmbientVideoPlayback provides DisplayPreferences.AutoPlayback.Off
    ) {
        SwipeToRefreshLayout(
            refreshingState = source.loadState.refresh is LoadState.Loading,
            onRefresh = {
                source.refreshOrRetry()
            }
        ) {
            if (source.itemCount > 0) {
                LazyColumn {
                    item {
                        Box(modifier = Modifier.height(standardPadding))
                    }
                    itemsPagingGridIndexed(
                        source,
                        rowSize = 2,
                        spacing = standardPadding,
                        padding = standardPadding
                    ) { index, pair ->
                        pair?.let { item ->
                            val navController = AmbientNavController.current
                            StatusMediaPreviewItem(
                                item.first,
                                modifier = Modifier
                                    .aspectRatio(1F)
                                    .clip(
                                        MaterialTheme.shapes.medium
                                    ),
                                onClick = {
                                    navController.navigate(
                                        Route.Media(
                                            item.second.statusKey,
                                            selectedIndex = index
                                        )
                                    )
                                }
                            )
                        }
                    }
                    item {
                        Box(modifier = Modifier.height(standardPadding))
                    }
                    loadState(source.loadState.append) {
                        source.retry()
                    }
                }
            }
        }
    }
}

@OptIn(IncomingComposeUpdate::class)
@Composable
private fun SearchUsersContent(viewModel: TwitterSearchUserViewModel) {
    val source = viewModel.source.collectAsLazyPagingItems()
    val navigator = AmbientNavigator.current
    SwipeToRefreshLayout(
        refreshingState = source.loadState.refresh is LoadState.Loading,
        onRefresh = {
            source.refreshOrRetry()
        }
    ) {
        if (source.itemCount > 0) {
            LazyColumn {
                items(source) { item ->
                    item?.let {
                        ListItem(
                            modifier = Modifier.clickable(
                                onClick = {
                                    navigator.user(item)
                                }
                            ),
                            icon = {
                                UserAvatar(user = item)
                            },
                            text = {
                                Row {
                                    Text(
                                        text = item.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colors.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Providers(
                                        AmbientContentAlpha provides ContentAlpha.medium
                                    ) {
                                        Text(
                                            text = "@${item.screenName}",
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            },
                            secondaryText = {
                                Text(text = item.desc, maxLines = 1)
                            },
                        )
                    }
                }
                loadState(source.loadState.append) {
                    source.retry()
                }
            }
        }
    }
}
