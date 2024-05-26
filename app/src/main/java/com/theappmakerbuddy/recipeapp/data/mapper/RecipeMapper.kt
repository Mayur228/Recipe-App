package com.theappmakerbuddy.recipeapp.data.mapper

import com.theappmakerbuddy.recipeapp.data.local.LocalRecipeEntity
import com.theappmakerbuddy.recipeapp.data.local.RecipeEntity
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDetailDto
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDtoItem
import com.theappmakerbuddy.recipeapp.domain.model.ModelLocalRecipe

fun RecipeEntity.toRecipeDtoItem(): RecipeDtoItem = RecipeDtoItem(
    id = id,
    image = imageUrl,
    summary = method,
    title = title,
)
fun RecipeDtoItem.toRecipeEntity(): RecipeEntity = RecipeEntity(
    id = id,
    imageUrl = image,
    method = summary,
    title = title
)

fun RecipeDtoItem.toLocalRecipeEntity(): LocalRecipeEntity = LocalRecipeEntity(
    id = id,
    imageUrl = image,
    method = summary,
    title = title,
)

fun LocalRecipeEntity.toModelLocalRecipe(): ModelLocalRecipe = ModelLocalRecipe(
    id = id,
    imageUrl = imageUrl,
    ingredient = ingredient,
    method = method,
    title = title
)

fun ModelLocalRecipe.toRecipeDtoItem(): RecipeDtoItem = RecipeDtoItem(
    id = id,
    image = imageUrl,
    summary = method,
    title = title
)