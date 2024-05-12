package com.theappmakerbuddy.recipeapp.data.mapper

import com.theappmakerbuddy.recipeapp.data.local.LocalRecipeCategoryEntity
import com.theappmakerbuddy.recipeapp.data.local.LocalRecipeEntity
import com.theappmakerbuddy.recipeapp.data.local.RecipeEntity
import com.theappmakerbuddy.recipeapp.data.remote.dto.categories.CategoryDtoItem
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDtoItem
import com.theappmakerbuddy.recipeapp.domain.model.ModelLocalRecipe

fun RecipeEntity.toRecipeDtoItem(): RecipeDtoItem = RecipeDtoItem(
    imageUrl = imageUrl,
    ingredient = ingredient,
    method = method,
    tag = tag,
    title = title,
)

fun RecipeDtoItem.toRecipeEntity(): RecipeEntity = RecipeEntity(
    imageUrl = imageUrl,
    ingredient = ingredient,
    method = method,
    tag = tag,
    title = title
)

fun RecipeDtoItem.toLocalRecipeEntity(): LocalRecipeEntity = LocalRecipeEntity(
    imageUrl = imageUrl,
    ingredient = ingredient,
    method = method,
    tag = tag,
    title = title
)

fun LocalRecipeEntity.toModelLocalRecipe(): ModelLocalRecipe = ModelLocalRecipe(
    imageUrl = imageUrl,
    ingredient = ingredient,
    method = method,
    tag = tag,
    title = title
)

fun ModelLocalRecipe.toRecipeDtoItem(): RecipeDtoItem = RecipeDtoItem(
    imageUrl = imageUrl,
    ingredient = ingredient,
    method = method,
    tag = tag,
    title = title
)

fun CategoryDtoItem.toLocalRecipeCategoryEntity(): LocalRecipeCategoryEntity = LocalRecipeCategoryEntity(
    category = category,
    imageUrl = imageUrl
)

fun LocalRecipeCategoryEntity.toCategoryDtoItem(): CategoryDtoItem = CategoryDtoItem(
    category = category,
    imageUrl = imageUrl
)