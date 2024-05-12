package com.theappmakerbuddy.recipeapp.data.remote

import com.theappmakerbuddy.recipeapp.data.remote.dto.categories.CategoryDto
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDto
import retrofit2.http.GET
import retrofit2.http.Path

interface RecipeApi {
    @GET("{recipe}.json")
    suspend fun getRecipeList(@Path("recipe") recipe: String): RecipeDto

    @GET("category.json")
    suspend fun getCategory(): CategoryDto
}