package com.theappmakerbuddy.recipeapp.ui.recipe_screen.components

import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDetailDto
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDtoItem

data class RecipeScreenState(
    val recipe: RecipeDetailDto? = RecipeDetailDto(),
    val isLoading: Boolean = true,
    val error: String = "",
)
