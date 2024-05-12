package com.theappmakerbuddy.recipeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.theappmakerbuddy.recipeapp.data.converter.IngredientConverter
import com.theappmakerbuddy.recipeapp.data.converter.MethodConverter

@Database(
    entities = [RecipeEntity::class, LocalRecipeEntity::class, LocalRecipeCategoryEntity::class],
    version = 1,
)
@TypeConverters(IngredientConverter::class, MethodConverter::class)
abstract class RecipeDatabase : RoomDatabase(){
    abstract val dao: RecipeDao
    companion object{
        const val  DATABASE_NAME = "recipedatabase"
    }
}