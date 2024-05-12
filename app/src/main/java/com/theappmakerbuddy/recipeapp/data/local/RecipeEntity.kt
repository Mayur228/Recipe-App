package com.theappmakerbuddy.recipeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.Ingredient

@Entity
data class RecipeEntity(
    val imageUrl: String = "",
    val ingredient: List<Ingredient> = listOf(),
    val method: List<String> = listOf(),
    val tag: String = "",
    val title: String = "",
    @PrimaryKey(autoGenerate = true) val primaryKey: Long? = null,
)