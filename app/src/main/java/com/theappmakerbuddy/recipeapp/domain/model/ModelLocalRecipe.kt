package com.theappmakerbuddy.recipeapp.domain.model

import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.ExtendedIngredientResponse
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.Ingredient

data class ModelLocalRecipe(
    val id: Int = 0,
    val imageUrl: String = "",
    val ingredient: List<ExtendedIngredientResponse> = listOf(),
    val method: String = "",
    val title: String = ""
)
