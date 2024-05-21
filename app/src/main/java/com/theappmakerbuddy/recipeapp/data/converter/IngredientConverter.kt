package com.theappmakerbuddy.recipeapp.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.ExtendedIngredientResponse
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.Ingredient

class IngredientConverter{
    @TypeConverter
    fun fromIngredientToStringToJson(ingredients: List<ExtendedIngredientResponse>): String? = Gson().toJson(ingredients)

    @TypeConverter
    fun fromStringToIngredients(json: String): List<ExtendedIngredientResponse> = Gson().fromJson(json, Array<ExtendedIngredientResponse>::class.java).toList()
}