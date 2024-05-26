package com.theappmakerbuddy.recipeapp.domain.pagination
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.theappmakerbuddy.recipeapp.data.remote.RecipeApi
import com.theappmakerbuddy.recipeapp.data.remote.custom.RecipeType
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.SearchRecipeDtoItem
import retrofit2.HttpException
import java.io.IOException

class RecipePagingSource(
    private val recipeApi: RecipeApi,
    private val type: String,
) : PagingSource<Int, SearchRecipeDtoItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchRecipeDtoItem> {
        val position = params.key ?: 0
        return try {
            val response = recipeApi.getRecipeByCategory(type,"",position, params.loadSize)
            LoadResult.Page(
                data = response.results,
                prevKey = if (position == 0) null else position - params.loadSize,
                nextKey = if (response.results.isEmpty()) null else position + params.loadSize
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SearchRecipeDtoItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }
}
