package com.theappmakerbuddy.recipeapp.data.remote

import com.theappmakerbuddy.recipeapp.data.remote.custom.ApiKey
import com.theappmakerbuddy.recipeapp.data.remote.custom.RecipeType
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDetailDto
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDto
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.SearchRecipeDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApi {

    @ApiKey
    @GET("recipes/random")
    suspend fun getRandomRecipe(@Query("number") number: Int): RecipeDto

    @ApiKey
    @GET("recipes/complexSearch")
    suspend fun getRecipeByCategory(
        @Query("type") type: String,
        @Query("query") query: String,
        @Query("offset") offset: Int,
        @Query("number") number: Int,
    ): SearchRecipeDto

    @ApiKey
    @GET("recipes/complexSearch")
    suspend fun searchRecipe(
        @Query("query") query: String,
        @Query("offset") offset: Int,
        @Query("number") number: Int
    ): SearchRecipeDto

    @ApiKey
    @GET("recipes/{id}/information")
    suspend fun getRecipeDetails(
        @Path("id") id: Int,
//        @Query("includeNutrition") includeNutrition: Boolean,
    ): RecipeDetailDto

}