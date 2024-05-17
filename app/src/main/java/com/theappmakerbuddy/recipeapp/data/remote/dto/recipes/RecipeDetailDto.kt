package com.theappmakerbuddy.recipeapp.data.remote.dto.recipes

data class RecipeDetailDto(
    val title: String = "",
    val readyInMinutes: Int = 0,
    val servings: Int = 0,
    val image: String = "",
    val summary: String = "",
    val extendedIngredients: List<ExtendedIngredientResponse> = listOf()
)
