package com.theappmakerbuddy.recipeapp.ui.home_screen.components

import com.theappmakerbuddy.recipeapp.data.remote.dto.categories.CategoryDtoItem

data class ComponentCategoriesState(
    val isLoading: Boolean = true,
    val categories: List<CategoryDtoItem> = emptyList(),
    val error: String = "",
)
