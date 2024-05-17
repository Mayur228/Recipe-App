package com.theappmakerbuddy.recipeapp.ui.search_screen.components

import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDtoItem

data class ComponentSearchState(
    val isLoading: Boolean = true,
    val error: String = "",
    val recipes: List<RecipeDtoItem> = emptyList(),
)
