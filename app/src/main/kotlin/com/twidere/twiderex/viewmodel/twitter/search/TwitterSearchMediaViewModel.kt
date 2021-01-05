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
package com.twidere.twiderex.viewmodel.twitter.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.flatMap
import androidx.paging.map
import com.twidere.services.microblog.SearchService
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.paging.mediator.pager
import com.twidere.twiderex.paging.mediator.search.SearchMediaMediator
import kotlinx.coroutines.flow.map

class TwitterSearchMediaViewModel(
    private val account: AccountDetails,
    keyword: String,
) : ViewModel() {
    private val service by lazy {
        account.service as SearchService
    }
    val source by lazy {
        SearchMediaMediator(keyword, account.accountKey, service).pager()
            .flow.map { it.map { it.status.toUi(account.accountKey) } }.cachedIn(viewModelScope)
            .map {
                it.flatMap {
                    it.media.map { media -> media to it }
                }
            }
    }
}
