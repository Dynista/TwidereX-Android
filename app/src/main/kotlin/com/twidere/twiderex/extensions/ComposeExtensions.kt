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
package com.twidere.twiderex.extensions

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.twidere.twiderex.preferences.AmbientAppearancePreferences
import com.twidere.twiderex.preferences.proto.AppearancePreferences

// @Composable
// inline fun <reified VM : ViewModel> navViewModel(
//    key: String? = null,
//    factory: ViewModelProvider.Factory? = AmbientViewModelProviderFactory.current,
// ): VM {
//    val navController = AmbientNavController.current
//    val backStackEntry = navController.currentBackStackEntryAsState().value
//    return if (backStackEntry != null) {
//        // Hack for navigation viewModel
//        val application = AmbientApplication.current
//        val viewModelFactories = AmbientViewModelFactoriesMap.current
//        val delegate = SavedStateViewModelFactory(application, backStackEntry, null)
//        // https://github.com/google/dagger/issues/2166
//        // idk why people in google like factory pattern,
//        // they might need to take a look at https://github.com/EnterpriseQualityCoding/FizzBuzzEnterpriseEdition
//        val hiltViewModelFactory = HiltViewModelFactory::class.java.declaredConstructors.first()?.also {
//            it.isAccessible = true
//        }?.newInstance(backStackEntry, null, delegate, viewModelFactories) as HiltViewModelFactory
//        viewModel(key, hiltViewModelFactory)
//    } else {
//        viewModel(key, factory)
//    }
// }
//
// @Composable
// inline fun <reified VM : ViewModel> viewModel(
//    vararg dependsOn: Any,
//    noinline creator: (() -> VM)? = null,
// ): VM {
//    return viewModel(
//        if (dependsOn.any()) {
//            dependsOn.joinToString { it.hashCode().toString() } + VM::class.java.canonicalName
//        } else {
//            null
//        },
//        factory = object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                @Suppress("UNCHECKED_CAST")
//                return creator?.invoke() as T
//            }
//        }
//    )
// }
//
// @Composable
// fun ProvideNavigationViewModelFactoryMap(
//    factory: HiltViewModelFactory,
//    content: @Composable () -> Unit
// ) {
//    // Hack for navigationViewModel
//    val factories =
//        HiltViewModelFactory::class.java.getDeclaredField("mViewModelFactories")
//            .also { it.isAccessible = true }
//            .get(factory).let {
//                @Suppress("UNCHECKED_CAST")
//                it as Map<String, ViewModelAssistedFactory<out ViewModel>>
//            }
//    Providers(
//        AmbientViewModelFactoriesMap provides factories
//    ) {
//        content.invoke()
//    }
// }

@Composable
fun isDarkTheme(): Boolean {
    return when (AmbientAppearancePreferences.current.theme) {
        AppearancePreferences.Theme.Auto -> isSystemInDarkTheme()
        AppearancePreferences.Theme.Light -> false
        AppearancePreferences.Theme.Dark -> true
        else -> false
    }
}
