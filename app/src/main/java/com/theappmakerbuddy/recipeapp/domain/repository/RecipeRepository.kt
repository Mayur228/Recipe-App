package com.theappmakerbuddy.recipeapp.domain.repository

import androidx.paging.PagingData
import com.theappmakerbuddy.recipeapp.core.Resource
import com.theappmakerbuddy.recipeapp.data.remote.custom.RecipeType
import com.theappmakerbuddy.recipeapp.data.remote.dto.categories.CategoryDto
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDetailDto
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDtoItem
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.SearchRecipeDtoItem
import com.theappmakerbuddy.recipeapp.domain.model.ModelLocalRecipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun getTopRecipe(): Resource<List<RecipeDtoItem>>

    suspend fun getRecipeByCategory(type: String): Flow<PagingData<SearchRecipeDtoItem>>

    suspend fun searchRecipe(query: String): Flow<PagingData<SearchRecipeDtoItem>>

    suspend fun getRecipeDetails(recipeId: Int, isNetworkAvailable: Boolean): Resource<RecipeDetailDto>

    fun getCategory(): Resource<List<CategoryDto>>

    suspend fun getSavedRecipes(): Resource<List<ModelLocalRecipe>>

    suspend fun saveRecipe(recipeDtoItem: RecipeDtoItem): Resource<String>

    suspend fun deleteSelectedSavedRecipes(recipeTitles: List<String>): String


   /* suspend fun getRecipes(
        recipe: String,
        page: Int,
        pageSize: Int,
        fetchFromRemote: Boolean,
        ): Resource<List<RecipeDtoItem>>

    suspend fun getFirstFourRecipes(fetchFromRemote: Boolean): Resource<List<RecipeDtoItem>>

    suspend fun getRecipeByTitle(title: String, category: String): Flow<Resource<RecipeDtoItem>>

    suspend fun getCategories(): Flow<Resource<List<CategoryDtoItem>>>

    suspend fun getRecipesByCategory(
        recipe: String,
        category: String,
        page: Int,
        pageSize: Int,
        fetchFromRemote: Boolean,
        getSavedRecipes: Boolean,
        ): Resource<List<RecipeDtoItem>>

    suspend fun getSavedRecipes(): Resource<List<ModelLocalRecipe>>

    suspend fun saveRecipe(recipeDtoItem: RecipeDtoItem): Resource<String>

    suspend fun getLocalRecipeByTitle(title: String): Resource<ModelLocalRecipe?>

    suspend fun deleteSelectedSavedRecipes(recipeTitles: List<String>): String*/
}