package com.twidere.twiderex.paging.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.room.withTransaction
import coil.network.HttpException
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.model.DbPagingTimeline.Companion.toPagingDbTimeline
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.db.model.saveToDb
import com.twidere.twiderex.model.UserKey
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
abstract class PagingTimelineMediatorBase(
    userKey: UserKey,
    database: AppDatabase,
) : PagingMediator(userKey = userKey, database = database) {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DbPagingTimelineWithStatus>
    ): MediatorResult {
        try {
            val key = when (loadType) {
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    lastItem.status.status.data.statusId
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.REFRESH -> {
                    null
                }
            }
            val pageSize = state.config.pageSize

            val result = load(pageSize, key).map {
                it.toDbTimeline(userKey, TimelineType.Custom).toPagingDbTimeline(pagingKey)
            }.let {
                transform(it)
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    clearData(database)
                }
                result.saveToDb(database)
            }

            return MediatorResult.Success(
                endOfPaginationReached = hasMore(result, pageSize)
            )
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    open fun transform(data: List<DbPagingTimelineWithStatus>): List<DbPagingTimelineWithStatus> {
        return data
    }

    protected open fun hasMore(
        result: List<DbPagingTimelineWithStatus>,
        pageSize: Int
    ) = result.size < pageSize

    protected open suspend fun clearData(database: AppDatabase) {
        database.pagingTimelineDao().clearAll(pagingKey, userKey = userKey)
    }

    protected abstract suspend fun load(
        pageSize: Int,
        max_id: String?
    ): List<IStatus>
}