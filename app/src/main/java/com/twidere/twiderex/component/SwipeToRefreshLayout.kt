package com.twidere.twiderex.component

import androidx.compose.foundation.Box
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offsetPx
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp

private val RefreshDistance = 80.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToRefreshLayout(
    refreshingState: Boolean,
    onRefresh: () -> Unit,
    refreshIndicator: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val refreshDistance = with(DensityAmbient.current) { RefreshDistance.toPx() }
    val state = rememberSwipeableState(refreshingState) { newValue ->
        // compare both copies of the swipe state before calling onRefresh(). This is a workaround.
        if (newValue && !refreshingState) onRefresh()
        true
    }

    Stack(
        modifier = Modifier
            .swipeable(
                state = state,
                anchors = mapOf(
                    -refreshDistance to false,
                    refreshDistance to true
                ),
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Vertical
            )
            .fillMaxSize()
    ) {
        content()
        Box(Modifier.align(Alignment.TopCenter).offsetPx(y = state.offset)) {
            if (state.offset.value != -refreshDistance) {
                refreshIndicator()
            }
        }

        // TODO (https://issuetracker.google.com/issues/164113834): This state->event trampoline is a
        //  workaround for a bug in the SwipableState API. Currently, state.value is a duplicated
        //  source of truth of refreshingState.
        onCommit(refreshingState) {
            state.animateTo(refreshingState)
        }
    }
}