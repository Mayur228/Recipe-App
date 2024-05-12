package com.theappmakerbuddy.recipeapp.domain.pagination

interface Paginator <Key, Item> {
    suspend fun loadNextItems()
    suspend fun reset()
}