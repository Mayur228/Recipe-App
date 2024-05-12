package com.theappmakerbuddy.recipeapp.ui.categories_screen.components

import com.theappmakerbuddy.recipeapp.data.remote.dto.categories.CategoryDtoItem

data class CategoryListState(
    val isLoading: Boolean = false,
    val categories: List<CategoryDtoItem> = emptyList(),
    val error: String = "",
)
