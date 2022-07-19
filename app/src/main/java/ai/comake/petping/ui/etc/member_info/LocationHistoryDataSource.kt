package ai.comake.petping.ui.etc.member_info

import ai.comake.petping.AppConstants
import ai.comake.petping.UiState
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.data.vo.PersonalLocationInformationInquiryLog
import ai.comake.petping.data.vo.WalkRecord
import ai.comake.petping.emit
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import java.io.IOException

/**
 * android-petping-2
 * Class: LocationHistoryDataSource
 * Created by cliff on 2022/03/21.
 *
 * Description:
 */
private const val START_INDEX = 0

class LocationHistoryDataSource(
    private val userDataRepository: UserDataRepository,
    private val viewModel: LocationHistoryViewModel
) : PagingSource<Int, PersonalLocationInformationInquiryLog>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PersonalLocationInformationInquiryLog> {
        val startIndex = params.key ?: START_INDEX
        return try {
            viewModel.uiState.emit(UiState.Loading)
            val response = userDataRepository.getLocationHistoryList(
                AppConstants.AUTH_KEY,
                AppConstants.ID,
                startIndex
            )
            when (response) {
                is Resource.Success -> {
                    viewModel.uiState.emit(UiState.Success)
                    val items = response.value.data.personalLocationInformationInquiryLogs
                    if (START_INDEX == 0) {
                        viewModel.locationHistoryItems.value = items
                    }

                    LoadResult.Page(
                        data = items,
                        prevKey = if (startIndex == START_INDEX) null else startIndex - 1,
                        nextKey = if (items.isEmpty()) null else startIndex + 1
                    )
                }
                else -> {
                    viewModel.uiState.emit(UiState.Failure(null))
                    LoadResult.Error(Throwable("fail to api call"))
                }
            }
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PersonalLocationInformationInquiryLog>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}