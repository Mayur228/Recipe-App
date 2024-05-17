package com.theappmakerbuddy.recipeapp.ui.recipe_screen
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theappmakerbuddy.recipeapp.core.Constants
import com.theappmakerbuddy.recipeapp.core.Resource
import com.theappmakerbuddy.recipeapp.domain.repository.RecipeRepository
import com.theappmakerbuddy.recipeapp.ui.recipe_screen.components.RecipeScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val recipeRepository: RecipeRepository,
//    private val useCaseSaveRecipe: UseCaseSaveRecipe,
//    private val useCaseGetRecipeSavedStatus: UseCaseGetRecipeSavedStatus
) : ViewModel() {
    private val _recipeState = mutableStateOf(RecipeScreenState())
    val recipeState: State<RecipeScreenState> = _recipeState

    private val _uiRecipeScreenEvents: Channel<RecipeScreenEvents> = Channel()
    val uiRecipeScreenEvents = _uiRecipeScreenEvents.receiveAsFlow()

    private val _favouriteState = mutableStateOf(RecipeSaveState.UNABLE_TO_SAVE)
    val favouriteState: State<RecipeSaveState> = _favouriteState

    init {
        viewModelScope.launch {
            getRecipe()
            setFavouriteState()
        }
    }

    private suspend fun getRecipe() {
        val recipeId = savedStateHandle.get<Int>(Constants.RECIPE_ID) ?: 0
        val recipeResult = recipeRepository.getRecipeDetails(recipeId = recipeId)
       viewModelScope.launch {
           when (recipeResult) {
               is Resource.Error -> {
                   _recipeState.value = _recipeState.value.copy(
                       isLoading = false,
                       error = "Unable to load recipe. Please try again later"
                   )
               }
               is Resource.Loading -> {
                   _recipeState.value = _recipeState.value.copy(isLoading = true, error = "")
               }
               is Resource.Success -> {
                   _recipeState.value = _recipeState.value.copy(
                       isLoading = false,
                       recipe = recipeResult.data
                   )
               }
           }
       }
    }

    fun sendRecipeScreenUiEvent(uiEvents: RecipeScreenEvents) {
        viewModelScope.launch {
            when (uiEvents) {
                is RecipeScreenEvents.ShowSnackbar -> _uiRecipeScreenEvents.send(
                    RecipeScreenEvents.ShowSnackbar(
                        message = uiEvents.message
                    )
                )
            }
        }
    }

    private fun setFavouriteState(){
        viewModelScope.launch {
            val currentRecipe = _recipeState.value.recipe
//            useCaseGetRecipeSavedStatus(title = currentRecipe.title).collectLatest {
//                _favouriteState.value = it
//            }
        }
    }

    fun onSaveRecipeButtonClicked() {
        viewModelScope.launch {
            val currentRecipe = _recipeState.value.recipe
//            useCaseSaveRecipe(recipeDtoItem = currentRecipe).collectLatest {
//                _favouriteState.value = it
//            }
            Log.d("recipescreenviewmodel","favourite state is ${_favouriteState.value.name}")
            when (_favouriteState.value){
                RecipeSaveState.SAVED -> {
                    sendRecipeScreenUiEvent(RecipeScreenEvents.ShowSnackbar("Recipe SAVED successfully"))
                }
                RecipeSaveState.ALREADY_EXISTS -> {
                    sendRecipeScreenUiEvent(RecipeScreenEvents.ShowSnackbar("Recipe ALREADY exists"))
                }
                RecipeSaveState.UNABLE_TO_SAVE -> {
                    sendRecipeScreenUiEvent(RecipeScreenEvents.ShowSnackbar("UNABLE to save recipe, try again"))
                }
                RecipeSaveState.NOT_SAVED -> {
                    sendRecipeScreenUiEvent(RecipeScreenEvents.ShowSnackbar("REMOVED from favourites"))
                }
            }

        }
    }

}

sealed interface RecipeScreenEvents {
    class ShowSnackbar(val message: String) : RecipeScreenEvents
}