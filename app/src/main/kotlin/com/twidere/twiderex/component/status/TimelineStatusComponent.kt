/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.component.status

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.AmbientContentColor
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.component.HumanizedTime
import com.twidere.twiderex.component.navigation.AmbientNavigator
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.preferences.AmbientDisplayPreferences
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.ui.statusActionIconSize

@Composable
fun TimelineStatusComponent(
    data: UiStatus,
    showActions: Boolean = true,
) {
    val navigator = AmbientNavigator.current
    Column {
        val status = (data.retweet ?: data)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        navigator.status(data.statusKey)
                    }
                )
                .padding(
                    start = standardPadding * 2,
                    top = standardPadding * 2,
                    end = standardPadding * 2
                ),
        ) {
            if (data.retweet != null) {
                RetweetHeader(data = data)
                Spacer(modifier = Modifier.height(standardPadding))
            }
            StatusComponent(
                status = status,
                onStatusTextClicked = {
                    navigator.status(data.statusKey)
                }
            )
            if (showActions) {
                Providers(
                    AmbientContentAlpha provides ContentAlpha.medium
                ) {
                    Spacer(modifier = Modifier.height(standardPadding))
                    Row {
                        ReplyButton(status = status)
                        RetweetButton(status = status)
                        LikeButton(status = status)
                        CopyLinkButton(status = status)
                        ShareButton(status = status, compat = true)
                    }
                }
            }
            Spacer(modifier = Modifier.height(standardPadding))
        }
    }
}

@Composable
private fun StatusComponent(
    status: UiStatus,
    modifier: Modifier = Modifier,
    onStatusTextClicked: () -> Unit = {},
) {
    val navigator = AmbientNavigator.current
    val isMediaPreviewEnabled = AmbientDisplayPreferences.current.mediaPreview
    Column {
        Row(modifier = modifier) {
            UserAvatar(user = status.user)
            Spacer(modifier = Modifier.width(standardPadding))
            Column {
                Row {
                    Column(
                            modifier = Modifier.weight(1f)
                    ) {
                        Text(
                                text = status.user.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                        )
                        Providers(AmbientContentAlpha provides ContentAlpha.medium) {
                            Text(
                                    text = "@${status.user.screenName}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    HumanizedTime(time = status.timestamp)
                }
            }
        }
        Row(modifier = modifier) {
            Column {
                Spacer(modifier = Modifier.height(4.dp))

                StatusText(status = status, onStatusTextClicked = onStatusTextClicked)

                if (status.media.any()) {
                    Spacer(modifier = Modifier.height(standardPadding))
                    if (isMediaPreviewEnabled) {
                        StatusMediaComponent(
                                status = status,
                        )
                    } else {
                        Providers(
                                AmbientContentAlpha provides ContentAlpha.medium
                        ) {
                            Row(
                                    modifier = Modifier
                                            .clickable(
                                                    onClick = {
                                                        navigator.media(statusKey = status.statusKey)
                                                    }
                                            )
                                            .fillMaxWidth()
                            ) {
                                Icon(imageVector = vectorResource(id = R.drawable.ic_photo))
                                Spacer(modifier = Modifier.width(standardPadding))
                                Text(text = stringResource(id = R.string.common_controls_status_media))
                            }
                        }
                    }
                }

                if (!status.placeString.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(standardPadding))
                    Providers(
                            AmbientContentAlpha provides ContentAlpha.medium
                    ) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = vectorResource(id = R.drawable.ic_photo),
                                contentDescription = stringResource(
                                    id = R.string.accessibility_common_status_media
                                )
                            )
                            Box(modifier = Modifier.width(standardPadding))
                            Text(text = status.placeString)
                        }
                    }
                }

                if (status.quote != null) {
                    Spacer(modifier = Modifier.height(standardPadding))
                    Box(
                            modifier = Modifier
                                    .border(
                                            1.dp,
                                            AmbientContentColor.current.copy(alpha = 0.12f),
                                            MaterialTheme.shapes.medium
                                    )
                                    .clip(MaterialTheme.shapes.medium)
                    ) {
                        StatusComponent(
                                status = status.quote,
                                modifier = Modifier
                                        .clickable(
                                                onClick = {
                                                    navigator.status(statusKey = status.quote.statusKey)
                                                }
                                        )
                                        .padding(standardPadding),
                                onStatusTextClicked = {
                                    navigator.status(statusKey = status.quote.statusKey)
                                }
                        )
                    }
                }
            }
        }
    }
}
