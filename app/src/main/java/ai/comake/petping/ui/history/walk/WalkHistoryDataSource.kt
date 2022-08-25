package ai.comake.petping.ui.history.walk

import ai.comake.petping.AppConstants
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.WalkRepository
import ai.comake.petping.data.vo.PetProfileConfig
import ai.comake.petping.data.vo.WalkRecord
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import java.io.IOException

/**
 * android-petping-2
 * Class: WalkHistoryDataSource
 * Created by cliff on 2022/02/25.
 *
 * Description:
 */

class WalkHistoryDataSource(
    private val walkRepository: WalkRepository,
    private val viewModel:WalkHistoryViewModel,
    private val petId:Int,
    private val viewMode:String
) : PagingSource<Int, WalkRecord>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WalkRecord> {
        val page = params.key ?: 0
        return try {
            val response = walkRepository.getWalkingRecords(
                AppConstants.AUTH_KEY,
                petId,
                AppConstants.ID,
                page,
                viewMode
            )

            when(response) {
                is Resource.Success -> {
                    viewModel.isPublic.value = response.value.data.isPublicWalkingRecords
                    viewModel.isDelete.value = response.value.data.isDeleteRight
                    viewModel.viewMode.value = response.value.data.viewMode

                    val items = response.value.data.walks
                    val listSize = response.value.data.listSize
                    val pagingSize = response.value.data.numOfRows
                    viewModel.walkRecordList.value = items
                    LoadResult.Page(
                        data = items,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (listSize < pagingSize) null else page + 1
                    )
                }
                else -> LoadResult.Error(Throwable("fail to api call"))
            }
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, WalkRecord>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}