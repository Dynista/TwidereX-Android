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
package com.twidere.twiderex.di.assisted

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.named

@Composable
inline fun <reified VM : ViewModel> viewModel(
    vararg dependsOn: Any,
    noinline parameters: ParametersDefinition? = null
) = getViewModel<VM>(
    if (dependsOn.any()) {
        named(dependsOn.joinToString { it.hashCode().toString() } + VM::class.java.canonicalName)
    } else {
        null
    },
    parameters = parameters
)

// @Composable
// inline fun <reified AF : IAssistedFactory, reified VM : ViewModel> assistedViewModel(
//    vararg dependsOn: Any,
//    noinline creator: ((AF) -> VM)? = null,
// ): VM {
//    getViewModel<VM> {
//        parametersOf()
//    }
//    val a: HomeTimelineViewModel by inject { parametersOf() }
//  /*  val factories = AmbientAssistedFactories.current
//    val factory = factories.firstOrNull { AF::class.java.isInstance(it) } as? AF
//    return viewModel(
//        if (dependsOn.any()) {
//            dependsOn.joinToString { it.hashCode().toString() } + VM::class.java.canonicalName
//        } else {
//            null
//        },
//        factory = object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                @Suppress("UNCHECKED_CAST")
//                return factory?.let { creator?.invoke(it) } as T
//            }
//        }
//    )*/
// }
//
// interface IAssistedFactory

// data class AssistedViewModelFactoryHolder @Inject constructor(
//    val homeTimelineViewModelFactory: HomeTimelineViewModel.AssistedFactory,
//    val twitterStatusViewModelFactory: TwitterStatusViewModel.AssistedFactory,
//    val mentionsTimelineViewModelFactory: MentionsTimelineViewModel.AssistedFactory,
//    val twitterSearchMediaViewModelFactory: TwitterSearchMediaViewModel.AssistedFactory,
//    val twitterSearchTweetsViewModelFactory: TwitterSearchTweetsViewModel.AssistedFactory,
//    val userFavouriteTimelineViewModelFactory: UserFavouriteTimelineViewModel.AssistedFactory,
//    val userTimelineViewModelFactory: UserTimelineViewModel.AssistedFactory,
//    val userMediaTimelineViewModelFactory: UserMediaTimelineViewModel.AssistedFactory,
//    val userViewModelFactory: UserViewModel.AssistedFactory,
//    val composeViewModelFactory: ComposeViewModel.AssistedFactory,
//    val mediaViewModelFactory: MediaViewModel.AssistedFactory,
//    val searchInputViewModelFactory: SearchInputViewModel.AssistedFactory,
//    val draftItemViewModelFactory: DraftItemViewModel.AssistedFactory,
//    val draftComposeViewModelFactory: DraftComposeViewModel.AssistedFactory,
// )
//
// @Composable
// fun ProvideAssistedFactory(
//    factoryHolder: AssistedViewModelFactoryHolder,
//    content: @Composable () -> Unit,
// ) {
//    val factory = remember {
//        listOf(
//            factoryHolder.homeTimelineViewModelFactory,
//            factoryHolder.twitterStatusViewModelFactory,
//            factoryHolder.mentionsTimelineViewModelFactory,
//            factoryHolder.twitterSearchMediaViewModelFactory,
//            factoryHolder.twitterSearchTweetsViewModelFactory,
//            factoryHolder.userFavouriteTimelineViewModelFactory,
//            factoryHolder.userTimelineViewModelFactory,
//            factoryHolder.userMediaTimelineViewModelFactory,
//            factoryHolder.userViewModelFactory,
//            factoryHolder.composeViewModelFactory,
//            factoryHolder.mediaViewModelFactory,
//            factoryHolder.searchInputViewModelFactory,
//            factoryHolder.draftItemViewModelFactory,
//            factoryHolder.draftComposeViewModelFactory,
//        )
//    }
//    Providers(
//        AmbientAssistedFactories provides factory
//    ) {
//        content.invoke()
//    }
// }
//
// val AmbientAssistedFactories = staticAmbientOf<List<IAssistedFactory>>()
