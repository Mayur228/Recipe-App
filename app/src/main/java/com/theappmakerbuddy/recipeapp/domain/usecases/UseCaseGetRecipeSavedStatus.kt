package com.theappmakerbuddy.recipeapp.domain.usecases

import com.theappmakerbuddy.recipeapp.core.Resource
import com.theappmakerbuddy.recipeapp.domain.repository.RecipeRepository
import com.theappmakerbuddy.recipeapp.ui.recipe_screen.RecipeSaveState
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class UseCaseGetRecipeSavedStatus @Inject constructor(private val recipeRepository: RecipeRepository) {
    suspend operator fun invoke(title: String): Flow<RecipeSaveState> = flow {
        when(val recipe =  recipeRepository.getSavedRecipes()){
            is Resource.Error -> {
                emit(RecipeSaveState.NOT_SAVED)
            }
            is Resource.Loading -> {
                emit(RecipeSaveState.NOT_SAVED)
            }
            is Resource.Success -> {
                if((recipe.data == null)){
                    emit(RecipeSaveState.NOT_SAVED)
                }
                else{
                    emit(RecipeSaveState.ALREADY_EXISTS)
                }
            }
        }
    }
}
