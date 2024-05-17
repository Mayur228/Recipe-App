package com.theappmakerbuddy.recipeapp.data.remote.dto.recipes

data class SearchRecipeDto(
    val results: List<SearchRecipeDtoItem> = listOf(),
    val number: Int
)
