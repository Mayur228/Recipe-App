package com.theappmakerbuddy.recipeapp.data.remote.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.theappmakerbuddy.recipeapp.core.Resource
import com.theappmakerbuddy.recipeapp.data.local.RecipeDatabase
import com.theappmakerbuddy.recipeapp.data.mapper.toLocalRecipeEntity
import com.theappmakerbuddy.recipeapp.data.mapper.toModelLocalRecipe
import com.theappmakerbuddy.recipeapp.data.mapper.toRecipeDtoItem
import com.theappmakerbuddy.recipeapp.data.remote.RecipeApi
import com.theappmakerbuddy.recipeapp.data.remote.dto.categories.CategoryDto
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDetailDto
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDtoItem
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.SearchRecipeDtoItem
import com.theappmakerbuddy.recipeapp.domain.model.ModelLocalRecipe
import com.theappmakerbuddy.recipeapp.domain.pagination.RecipePagingSource
import com.theappmakerbuddy.recipeapp.domain.pagination.SearchRecipePagingSource
import com.theappmakerbuddy.recipeapp.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val recipeApi: RecipeApi,
    private val recipeDatabase: RecipeDatabase
) : RecipeRepository {
    private val recipeDao = recipeDatabase.dao
    override suspend fun getTopRecipe(): Resource<List<RecipeDtoItem>> {
        try {
            val recipes = recipeApi.getRandomRecipe(6)
            Log.e("CHECK", recipes.recipes.toString())
            return Resource.Success(data = recipes.recipes)
        } catch (e: Exception) {
            return Resource.Error(error = e.message.toString())
        }
    }

    override suspend fun getRecipeByCategory(
        type: String,
    ): Flow<PagingData<SearchRecipeDtoItem>> {

        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { RecipePagingSource(recipeApi, type) }
        ).flow
    }

    override suspend fun searchRecipe(
        query: String,
    ): Flow<PagingData<SearchRecipeDtoItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchRecipePagingSource(recipeApi, query) }
        ).flow
    }

    override suspend fun getRecipeDetails(
        recipeId: Int,
        isNetworkAvailable: Boolean
    ): Resource<RecipeDetailDto> {
        try {
            if (isNetworkAvailable) {
                val recipe = recipeApi.getRecipeDetails(id = recipeId)
                return Resource.Success(data = recipe)

            } else {
                val remoteEntities = recipeApi.getRecipeDetails(recipeId)
                recipeDao.clearRecipes()
                recipeDao.insertRecipes(remoteEntities.toRecipeDtoItem())
            }
        } catch (e: Exception) {
            return Resource.Error(error = "unable to find top recipes")
        }

    }

    override fun getCategory(): Resource<List<CategoryDto>> {
        val category = listOf(
            CategoryDto("main course"),
            CategoryDto("side dish"),
            CategoryDto("dessert"),
            CategoryDto("appetizer"),
            CategoryDto("salad"),
            CategoryDto("bread"),
            CategoryDto("breakfast"),
            CategoryDto("soup"),
            CategoryDto("beverage"),
            CategoryDto("sauce"),
            CategoryDto("marinade"),
            CategoryDto("fingerfood"),
            CategoryDto("snack"),
            CategoryDto("drink"),
        )

        return Resource.Success(category)
    }

    override suspend fun getSavedRecipes(): Resource<List<ModelLocalRecipe>> {
        return try {
            val savedRecipes = recipeDao.getSavedRecipes()
            val modelLocalRecipe = savedRecipes.map { it.toModelLocalRecipe() }
            Resource.Success(data = modelLocalRecipe)
        } catch (e: Exception) {
            Resource.Error(error = "unable to load saved recipes, please try again later")
        }
    }

    override suspend fun saveRecipe(recipeDtoItem: RecipeDtoItem): Resource<String> {
        return try {
            val localRecipeEntity = recipeDtoItem.toLocalRecipeEntity()
            recipeDao.saveRecipe(localRecipeEntity = localRecipeEntity)
            Resource.Success("Recipe saved successfully")
        } catch (e: Exception) {
            Resource.Error("unable to save recipe, please try again")
        }
    }

    override suspend fun deleteSelectedSavedRecipes(recipeTitles: List<String>): String {
        return try {
            recipeDao.deleteLocalRecipes(titles = recipeTitles)
            "Recipes DELETED successfully"
        } catch (e: Exception) {
            "Recipes NOT deleted successfully, please try again"
        }
    }


}