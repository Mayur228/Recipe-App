package com.theappmakerbuddy.recipeapp.ui.home_screen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theappmakerbuddy.recipeapp.core.Resource
import com.theappmakerbuddy.recipeapp.data.remote.dto.categories.CategoryDto
import com.theappmakerbuddy.recipeapp.domain.repository.RecipeRepository
import com.theappmakerbuddy.recipeapp.ui.home_screen.components.ComponentCategoriesState
import com.theappmakerbuddy.recipeapp.ui.home_screen.components.ComponentTopRecipesState
import com.theappmakerbuddy.recipeapp.ui.recipe_list_screen.RecipeListScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
) : ViewModel() {
    private val topRecipesState =
        mutableStateOf(ComponentTopRecipesState())
    val topRecipes = topRecipesState as State<ComponentTopRecipesState>

    private val _categoriesState =
        mutableStateOf(ComponentCategoriesState())
    val categoriesState: State<ComponentCategoriesState> = _categoriesState

    private val _uiEvents = Channel<HomeScreenUiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            async { loadTopRecipes() }
            async { loadCategories() }
        }
    }

    private suspend fun loadTopRecipes() {
        when (val recipeState = recipeRepository.getTopRecipe()) {
            is Resource.Error -> {
                topRecipesState.value =
                    topRecipesState.value.copy(error = recipeState.error.toString(), loading = false)
            }
            is Resource.Loading -> {
                topRecipesState.value = topRecipesState.value.copy(error = "", loading = true)
            }
            is Resource.Success -> {
                topRecipesState.value = topRecipesState.value.copy(
                    error = "",
                    loading = false,
                    recipes = recipeState.data ?: emptyList()
                )
            }
        }
    }

    private fun loadCategories() {
        when(val categoryState = recipeRepository.getCategory()) {
            is Resource.Error -> TODO()
            is Resource.Loading -> TODO()
            is Resource.Success -> {
                _categoriesState.value = _categoriesState.value.copy(
                    isLoading = false,
                    categories = categoryState.data ?: emptyList()
                )
            }
        }

    }

    fun sendUiEvents(event: HomeScreenUiEvents){
        viewModelScope.launch {
            when(event){
                HomeScreenUiEvents.NavigateUp -> {
                    _uiEvents.send(HomeScreenUiEvents.NavigateUp)
                }
                HomeScreenUiEvents.NavigateToSearchRecipesScreen -> {
                    _uiEvents.send(HomeScreenUiEvents.NavigateToSearchRecipesScreen)
                }
                HomeScreenUiEvents.NavigateToCategoriesScreen -> {
                    _uiEvents.send(HomeScreenUiEvents.NavigateToCategoriesScreen)
                }

                HomeScreenUiEvents.NavigateToFavorite -> {
                    _uiEvents.send(HomeScreenUiEvents.NavigateToFavorite)
                }
            }
        }
    }
}

sealed interface HomeScreenUiEvents{
    object NavigateUp: HomeScreenUiEvents
    object NavigateToSearchRecipesScreen: HomeScreenUiEvents
    object NavigateToCategoriesScreen: HomeScreenUiEvents
    object NavigateToFavorite: HomeScreenUiEvents
}