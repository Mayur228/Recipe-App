package com.theappmakerbuddy.recipeapp.ui.home_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theappmakerbuddy.recipeapp.core.Resource
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
            async { loadTopRecipes(fetchFromRemote = false) }
            async { loadCategories() }
        }
    }

    private suspend fun loadTopRecipes(fetchFromRemote: Boolean) {
        when (val recipeState = recipeRepository.getFirstFourRecipes(fetchFromRemote = fetchFromRemote)) {
            is Resource.Error -> {
                topRecipesState.value =
                    topRecipesState.value.copy(error = "unable to load recipes", loading = false)
            }
            is Resource.Loading -> {
                topRecipesState.value = topRecipesState.value.copy(error = "", loading = true)
            }
            is Resource.Success -> {
                topRecipesState.value = topRecipesState.value.copy(
                    error = "",
                    loading = false,
                    recipes = recipeState.data!!
                )
            }
        }
    }

    private suspend fun loadCategories() {
        recipeRepository.getCategories().collectLatest { result ->
            when (result) {
                is Resource.Error -> {
                    _categoriesState.value = _categoriesState.value.copy(
                        isLoading = false,
                        error = result.error ?: "unable to load categories please try again later"
                    )
                }
                is Resource.Loading -> {
                    _categoriesState.value = _categoriesState.value.copy(isLoading = true)
                }
                is Resource.Success -> {
                    _categoriesState.value = _categoriesState.value.copy(isLoading = false, categories = result.data ?: emptyList())
                }
            }
        }
    }

    fun sendUiEvents(event: HomeScreenUiEvents){
        viewModelScope.launch {
            when(event){
                HomeScreenUiEvents.CloseNavDrawer -> {
                    _uiEvents.send(HomeScreenUiEvents.CloseNavDrawer)
                }
                HomeScreenUiEvents.OpenNavDrawer -> {
                    _uiEvents.send(HomeScreenUiEvents.OpenNavDrawer)
                }
                HomeScreenUiEvents.NavigateUp -> {
                    _uiEvents.send(HomeScreenUiEvents.NavigateUp)
                }
                HomeScreenUiEvents.NavigateToSearchRecipesScreen -> {
                    _uiEvents.send(HomeScreenUiEvents.NavigateToSearchRecipesScreen)
                }
                HomeScreenUiEvents.NavigateToCategoriesScreen -> {
                    _uiEvents.send(HomeScreenUiEvents.NavigateToCategoriesScreen)
                }
            }
        }
    }
}

sealed interface HomeScreenUiEvents{
    object CloseNavDrawer: HomeScreenUiEvents
    object OpenNavDrawer: HomeScreenUiEvents
    object NavigateUp: HomeScreenUiEvents
    object NavigateToSearchRecipesScreen: HomeScreenUiEvents
    object NavigateToCategoriesScreen: HomeScreenUiEvents
}