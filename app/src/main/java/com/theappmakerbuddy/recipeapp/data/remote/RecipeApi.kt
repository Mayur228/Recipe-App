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
    @GET("/random")
    suspend fun getRandomRecipe(@Query("number") number: Int): RecipeDto

    @ApiKey
    @GET("/complexSearch")
    suspend fun getRecipeByCategory(
        @Query("type") type: RecipeType,
        @Query("query") query: String,
        @Query("offset") offset: Int,
        @Query("number") number: Int,
    ): SearchRecipeDto

    @ApiKey
    @GET("/complexSearch")
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


//    @GET("{recipe}.json")
//    suspend fun getRecipeList(@Path("recipe") recipe: String): RecipeDto
//
//    @GET("category.json")
//    suspend fun getCategory(): CategoryDto
}