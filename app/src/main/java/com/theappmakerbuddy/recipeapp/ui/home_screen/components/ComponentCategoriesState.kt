package com.theappmakerbuddy.recipeapp.ui.home_screen.components

import com.theappmakerbuddy.recipeapp.data.remote.custom.RecipeType
import com.theappmakerbuddy.recipeapp.data.remote.dto.categories.CategoryDto

data class ComponentCategoriesState(
    val isLoading: Boolean = true,
    val categories: List<CategoryDto> = emptyList(),
)
