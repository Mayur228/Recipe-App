package com.theappmakerbuddy.recipeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.ExtendedIngredientResponse
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.Ingredient

@Entity()
data class LocalRecipeEntity(
    val id: Int = 0,
    val imageUrl: String = "",
    val ingredient: List<ExtendedIngredientResponse> = listOf(),
    val method: String = "",
    val title: String = "",
    @PrimaryKey(autoGenerate = true) val primaryKey: Long? = null,
)
