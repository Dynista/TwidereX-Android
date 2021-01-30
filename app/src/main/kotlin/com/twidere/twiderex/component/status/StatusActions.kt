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

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope.Companion.weight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.AmbientContentColor
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.AmbientClipboardManager
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.TwidereApp
import com.twidere.twiderex.action.AmbientStatusActions
import com.twidere.twiderex.component.foundation.ActionIconButton
import com.twidere.twiderex.component.navigation.AmbientNavigator
import com.twidere.twiderex.extensions.humanizedCount
import com.twidere.twiderex.extensions.shareText
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.mediumEmphasisContentContentColor
import com.twidere.twiderex.ui.statusActionIconSize
import com.twidere.twiderex.viewmodel.compose.ComposeType
import dagger.hilt.android.qualifiers.ApplicationContext

@Composable
fun ReplyButton(
    status: UiStatus,
    withNumber: Boolean = true,
) {
    val navigator = AmbientNavigator.current
    val icon = vectorResource(id = R.drawable.ic_corner_up_left)
    val contentDescription = stringResource(id = R.string.accessibility_common_status_actions_reply)
    val action = {
        navigator.compose(ComposeType.Reply, statusKey = status.statusKey)
    }
    if (withNumber) {
        StatusActionButtonWithNumbers(
            icon = icon,
            color = mediumEmphasisContentContentColor,
            count = status.replyCount,
            contentDescription = contentDescription,
            onClick = {
                action.invoke()
            },
        )
    } else {
        ActionIconButton(
            onClick = {
                action.invoke()
            },
        ) {
            Icon(
                imageVector = icon,
                tint = mediumEmphasisContentContentColor,
                contentDescription = contentDescription,
            )
        }
    }
}

@Composable
fun LikeButton(
    status: UiStatus,
    withNumber: Boolean = true,
) {
    val actionsViewModel = AmbientStatusActions.current
    val account = AmbientActiveAccount.current
    val color = if (status.liked) {
        Color.Red
    } else {
        mediumEmphasisContentContentColor
    }
    val contentDescription = stringResource(id = R.string.accessibility_common_status_actions_like)
    val icon = vectorResource(id = R.drawable.ic_heart)
    val action = {
        if (account != null) {
            actionsViewModel.like(status, account)
        }
    }
    if (withNumber) {
        StatusActionButtonWithNumbers(
            icon = icon,
            count = status.likeCount,
            color = color,
            contentDescription = contentDescription,
            onClick = {
                action.invoke()
            },
        )
    } else {
        ActionIconButton(
            onClick = {
                action.invoke()
            },
        ) {
            Icon(
                imageVector = icon,
                tint = color,
                contentDescription = contentDescription,
            )
        }
    }
}

@Composable
fun RetweetButton(
    status: UiStatus,
    withNumber: Boolean = true,
) {
    val actionsViewModel = AmbientStatusActions.current
    val account = AmbientActiveAccount.current
    val color = if (status.retweeted) {
        MaterialTheme.colors.primary
    } else {
        mediumEmphasisContentContentColor
    }
    val icon = vectorResource(id = R.drawable.ic_repeat)
    val contentDescription = stringResource(id = R.string.accessibility_common_status_actions_retweet)
    val action = {
        if (account != null) {
            actionsViewModel.retweet(status, account)
        }
    }
    if (withNumber) {
        StatusActionButtonWithNumbers(
            icon = icon,
            count = status.retweetCount,
            color = color,
            contentDescription = contentDescription,
            onClick = {
                action.invoke()
            },
        )
    } else {
        ActionIconButton(
            onClick = {
                action.invoke()
            },
        ) {
            Icon(
                imageVector = icon,
                tint = color,
                contentDescription = contentDescription,
            )
        }
    }
}

@Composable
fun CopyLinkButton(
        status: UiStatus,
        withNumber: Boolean = true,
) {
    val clipboardManager = AmbientClipboardManager.current
    val color = mediumEmphasisContentContentColor
    val icon = vectorResource(id = R.drawable.ic_share)
    val context = AmbientContext.current
    val contentDescription = stringResource(id = R.string.accessibility_common_status_actions_copy_link)
    val action = {
        clipboardManager.setText(
                buildAnnotatedString {
                    append(status.generateShareLink())
                }
        )

        Toast.makeText(context, "Copied link to clipboard", Toast.LENGTH_SHORT).show()
    }
    if (withNumber) {
        StatusActionButtonWithNumbers(
                icon = icon,
                count = 0,
                color = color,
                onClick = {
                    action.invoke()
                },
                contentDescription = contentDescription
        )
    } else {
        ActionIconButton(
                onClick = {
                    action.invoke()
                },
        ) {
            Icon(
                    imageVector = icon,
                    tint = color,
                    contentDescription = contentDescription
            )
        }
    }
}

@Composable
fun ShareButton(
    status: UiStatus,
    compat: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }
    val actionsViewModel = AmbientStatusActions.current
    val account = AmbientActiveAccount.current
    val accountKey = account?.accountKey
    val context = AmbientContext.current
    val icon = Icons.Default.MoreHoriz
    val text = status.contentAnnotatedString()
    val clipboardManager = AmbientClipboardManager.current
    val contentDescription = stringResource(id = R.string.accessibility_common_more)
    val child = @Composable {
        if (compat) {
            TextButton(
                onClick = {
                    expanded = true
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = mediumEmphasisContentContentColor
                )
            ) {
                Icon(
                    modifier = Modifier.size(statusActionIconSize),
                    imageVector = icon,
                    contentDescription = contentDescription,
                )
            }
        } else {
            ActionIconButton(
                onClick = {
                    expanded = true
                },
            ) {
                Icon(
                    imageVector = icon,
                    tint = mediumEmphasisContentContentColor,
                    contentDescription = contentDescription,
                )
            }
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        toggle = child,
    ) {
        DropdownMenuItem(
            onClick = {
                expanded = false
                clipboardManager.setText(text)
            }
        ) {
            Text(
                text = stringResource(id = R.string.common_controls_status_actions_copy_text),
            )
        }
        DropdownMenuItem(
            onClick = {
                expanded = false
                clipboardManager.setText(
                    buildAnnotatedString {
                        append(status.generateShareLink())
                    }
                )
            }
        ) {
            Text(
                text = stringResource(id = R.string.common_controls_status_actions_copy_link),
            )
        }
        DropdownMenuItem(
            onClick = {
                expanded = false
                context.shareText(status.generateShareLink())
            }
        ) {
            Text(
                text = stringResource(id = R.string.common_controls_status_actions_share_link),
            )
        }
        if (status.user.userKey == accountKey) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    actionsViewModel.delete(status, account)
                }
            ) {
                Text(
                    text = stringResource(id = R.string.common_controls_actions_remove),
                    color = Color.Red,
                )
            }
        }
    }
}

@Composable
private fun StatusActionButtonWithNumbers(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String,
    count: Long,
    color: Color = AmbientContentColor.current,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier.weight(1f),
        horizontalArrangement = Arrangement.Start,
    ) {
        TextButton(
            onClick = onClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = color
            )
        ) {
            Icon(
                modifier = Modifier.size(statusActionIconSize),
                imageVector = icon,
                tint = color,
                contentDescription = contentDescription
            )
            if (count > 0) {
                Box(modifier = Modifier.width(4.dp))
                Text(text = count.humanizedCount(), maxLines = 1)
            }
        }
    }
}
