package com.theappmakerbuddy.recipeapp.domain.usecases

import com.theappmakerbuddy.recipeapp.core.Resource
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.RecipeDtoItem
import com.theappmakerbuddy.recipeapp.domain.repository.RecipeRepository
import com.theappmakerbuddy.recipeapp.ui.recipe_screen.RecipeSaveState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UseCaseSaveRecipe @Inject constructor (
        private val recipeRepository: RecipeRepository,
    ) {
    suspend operator fun invoke(recipeDtoItem: RecipeDtoItem): Flow<RecipeSaveState> = flow  {
        val recipeFromDatabase = recipeRepository.getSavedRecipes()
        when(recipeFromDatabase){
            is Resource.Error -> {
                emit(RecipeSaveState.UNABLE_TO_SAVE)
            }
            else -> {
                val recipe = recipeFromDatabase.data
                if(recipe == null){
                    recipeRepository.saveRecipe(recipeDtoItem = recipeDtoItem)
                    emit(RecipeSaveState.SAVED)
                }else{
                    recipeRepository.deleteSelectedSavedRecipes(listOf(recipeDtoItem.title))
                    emit(RecipeSaveState.NOT_SAVED)
                }
            }
        }
    }
}
