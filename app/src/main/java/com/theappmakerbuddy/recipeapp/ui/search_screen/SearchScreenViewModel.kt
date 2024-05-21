package com.theappmakerbuddy.recipeapp.ui.search_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.theappmakerbuddy.recipeapp.core.Resource
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.SearchRecipeDtoItem
import com.theappmakerbuddy.recipeapp.domain.repository.RecipeRepository
import com.theappmakerbuddy.recipeapp.ui.home_screen.components.ComponentTopRecipesState
import com.theappmakerbuddy.recipeapp.ui.recipe_list_screen.ToRecipeListScreenEvents
import com.theappmakerbuddy.recipeapp.ui.search_screen.components.ComponentSearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel@Inject constructor(
    private val recipeRepository: RecipeRepository,
) : ViewModel() {
    private val _searchBoxState = mutableStateOf("")
    val searchBoxState: State<String> = _searchBoxState
    var searchJob: Job? = null

    private val searchRecipeState =
        mutableStateOf(ComponentSearchState())
    val recipes = searchRecipeState as State<ComponentSearchState>

    private val _allRecipeEvent = Channel<ComponentSearchState>()
    val allRecipeEvent = _allRecipeEvent.receiveAsFlow()

    suspend fun getAllRecipes() {
        viewModelScope.launch {
            val allRecipe = recipeRepository.searchRecipe("")
            allRecipe.collect {
                when(it) {
                    is Resource.Error -> TODO()
                    is Resource.Loading -> TODO()
                    is Resource.Success -> {

                    }
                }
            }
        }
    }
    fun onSearchBoxValueChanged(newValue: String) {
        _searchBoxState.value = newValue
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            recipeRepository.searchRecipe(newValue)
        }
    }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _searchResults = MutableStateFlow<PagingData<SearchRecipeDtoItem>?>(PagingData.empty())
    val searchResults: StateFlow<PagingData<SearchRecipeDtoItem>?> = _searchResults

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        searchRecipe(newQuery)
    }

    private fun searchRecipe(query: String) {
        viewModelScope.launch {
            recipeRepository.searchRecipe(query)
                .catch { e ->
                    _searchResults.value = PagingData.empty()
                }
                .collect { result ->
                    when (result) {
                        is Resource.Error -> TODO()
                        is Resource.Loading -> TODO()
                        is Resource.Success -> {
                            _searchResults.value = result.data ?: PagingData.empty()
                        }
                    }
                }
        }
    }
}