package com.theappmakerbuddy.recipeapp.data.remote.repository

import android.util.Log
import com.theappmakerbuddy.recipeapp.core.Resource
import com.theappmakerbuddy.recipeapp.data.local.RecipeDatabase
import com.theappmakerbuddy.recipeapp.data.local.RecipeEntity
import com.theappmakerbuddy.recipeapp.data.mapper.*
import com.theappmakerbuddy.recipeapp.data.remote.RecipeApi
import com.theappmakerbuddy.recipeapp.data.remote.dto.categories.CategoryDtoItem
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDtoItem
import com.theappmakerbuddy.recipeapp.domain.model.ModelLocalRecipe
import com.theappmakerbuddy.recipeapp.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val recipeApi: RecipeApi,
    private val recipeDatabase: RecipeDatabase
) :
    RecipeRepository {
    private val recipeDao = recipeDatabase.dao
    override suspend fun getFirstFourRecipes(fetchFromRemote: Boolean): Resource<List<RecipeDtoItem>> {
        try {
            val shouldJustLoadFromCache = !fetchFromRemote && recipeDao.searchRecipe("").isNotEmpty()
            val myRecipes: List<RecipeEntity> = if (shouldJustLoadFromCache) {
                recipeDao.getFirstFourRecipes()
            } else {
                val remoteEntities = recipeApi.getRecipeList("snacks")
                recipeDao.clearRecipes()
                recipeDao.insertRecipes(remoteEntities.map {
                    it.toRecipeEntity()
                })
                recipeDao.getFirstFourRecipes()
            }
            val recipes = myRecipes.map {
                it.toRecipeDtoItem()
            }
            return Resource.Success(data = recipes)
        } catch (e: Exception) {
            return Resource.Error(error = "unable to find top recipes")
        }

    }

    override suspend fun getRecipeByTitle(
        title: String,
        category: String
    ): Flow<Resource<RecipeDtoItem>> = flow {
        try {
            var recipeFromDb = recipeDao.getRecipeByTitle(recipeTitle = title.toString())
            if (recipeFromDb == null) {
                recipeDao.clearRecipes()
                val remoteEntities = recipeApi.getRecipeList(category)
                recipeDao.insertRecipes(remoteEntities.map {
                    it.toRecipeEntity()
                })
                recipeFromDb = recipeDao.getRecipeByTitle(recipeTitle = title)
            }
            val recipeEntity = recipeFromDb?.toRecipeDtoItem() ?: RecipeDtoItem()
            emit(Resource.Success(recipeEntity))
        } catch (e: Exception) {
            emit(Resource.Error(error = "unable to find recipe by title $title"))
        }
    }

    override suspend fun getCategories(): Flow<Resource<List<CategoryDtoItem>>> = flow {
        try {
            emit(Resource.Loading())
            val categoriesBeforeApiCall = recipeDao.getAllCategories()
            emit(Resource.Success(data = categoriesBeforeApiCall.map { it.toCategoryDtoItem() }))
            val recipes = recipeApi.getCategory()

            if (recipes.isNotEmpty()) {
                recipeDao.deleteAllCategories()
                recipeDao.insertLocalCategories(recipes.map { it.toLocalRecipeCategoryEntity() })
            }
            val localCategories = recipeDao.getAllCategories()
            emit(Resource.Success(data = localCategories.map { it.toCategoryDtoItem() }))
        } catch (e: Exception) {
            try {
                val recipes = recipeDao.getAllCategories()
                emit(Resource.Success(recipes.map { it.toCategoryDtoItem() }))
            } catch (e: Exception) {
                emit(Resource.Error("unable to load categories, please try again later"))
            }
        }
    }

    override suspend fun getRecipesByCategory(
        recipe: String,
        category: String,
        page: Int,
        pageSize: Int,
        fetchFromRemote: Boolean,
        getSavedRecipes: Boolean
    ): Resource<List<RecipeDtoItem>> {
        if (getSavedRecipes) {
        val myRecipes = recipeDao.searchSavedRecipe(recipe = recipe)
        val recipes = myRecipes.map {
            it.toModelLocalRecipe().toRecipeDtoItem()
        }
            try {

                val startingIndex = page * pageSize
                val endingIndex = startingIndex + pageSize

                return if (startingIndex < recipes.size) {
                    if (endingIndex < recipes.size) {
                        Resource.Success(data = recipes.slice(startingIndex until startingIndex + pageSize))
                    } else {
                        Resource.Success(data = recipes.slice(startingIndex until recipes.size))
                    }
                } else {
                    Resource.Success(data = emptyList())
                }

            } catch (e: Exception) {
                return Resource.Error("unable to load data, please try again later")
            }
        } else {
            try {
                val shouldJustLoadFromCache =
                    !fetchFromRemote && recipeDao.getRecipeByTag(
                        category = category,
                        recipe = recipe
                    ).isNotEmpty()

                val myRecipes: List<RecipeEntity> = if (!shouldJustLoadFromCache) {
                    if(recipeDao.searchRecipe("").isEmpty()){
                        recipeDao.clearRecipes()
                    }
                    val remoteEntities = recipeApi.getRecipeList(category)
                    recipeDao.insertRecipes(remoteEntities.map {
                        it.toRecipeEntity()
                    })
                    recipeDao.getRecipeByTag(category = category, recipe = recipe)
                } else {
                    recipeDao.getRecipeByTag(category = category, recipe = recipe)
                }
                val recipes = myRecipes.map {
                    it.toRecipeDtoItem()
                }
                val startingIndex = page * pageSize
                val endingIndex = startingIndex + pageSize

                return if (startingIndex < recipes.size) {
                    if (endingIndex < recipes.size) {
                        Resource.Success(data = recipes.slice(startingIndex until startingIndex + pageSize))
                    } else {
                        Resource.Success(data = recipes.slice(startingIndex until recipes.size))
                    }
                } else {
                    Resource.Success(data = emptyList())
                }
            } catch (exception: Exception) {
                return Resource.Error("unable to load data, please try again later")
            }
        }
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

    override suspend fun getLocalRecipeByTitle(title: String): Resource<ModelLocalRecipe?> {
        try {
            val localRecipeEntity = recipeDao.getSavedRecipeByTitle(title = title)
                ?: return Resource.Success(data = null)
            val modelRecipeEntity = localRecipeEntity.toModelLocalRecipe()
            return Resource.Success(data = modelRecipeEntity)
        } catch (e: Exception) {
            return Resource.Error(error = "Unable to load recipes")
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

    override suspend fun getRecipes(
        recipe: String,
        page: Int,
        pageSize: Int,
        fetchFromRemote: Boolean,
    ): Resource<List<RecipeDtoItem>> {

        try {
            val shouldJustLoadFromCache =
                !fetchFromRemote && recipeDao.searchRecipe("").isNotEmpty()
            val myRecipes: List<RecipeEntity> = if (!shouldJustLoadFromCache) {

                recipeDao.clearRecipes()
                val remoteEntities = recipeApi.getRecipeList(recipe)
                recipeDao.insertRecipes(remoteEntities.map {
                    it.toRecipeEntity()
                })
                recipeDao.searchRecipe("")
            } else {
                recipeDao.searchRecipe("")
            }
            val recipes = myRecipes.map {
                it.toRecipeDtoItem()
            }
            val startingIndex = page * pageSize
            val endingIndex = startingIndex + pageSize

            if (startingIndex < recipes.size) {
                return if (endingIndex < recipes.size) {
                    Resource.Success(data = recipes.slice(startingIndex until startingIndex + pageSize))
                } else {
                    Resource.Success(data = recipes.slice(startingIndex until recipes.size))
                }
            } else {
                return Resource.Success(data = emptyList())
            }
        } catch (exception: Exception) {
            return Resource.Error("unable to load data, please try again later")
        }
    }
}