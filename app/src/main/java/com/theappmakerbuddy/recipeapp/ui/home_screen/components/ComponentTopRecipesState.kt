package com.theappmakerbuddy.recipeapp.ui.home_screen.components

import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDtoItem

data class ComponentTopRecipesState(
    val recipes: List<RecipeDtoItem> = emptyList(),
    val error: String = "",
    val loading: Boolean = true
)
