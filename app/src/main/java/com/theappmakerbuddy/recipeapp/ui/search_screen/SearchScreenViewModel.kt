package com.theappmakerbuddy.recipeapp.ui.search_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.SearchRecipeDtoItem
import com.theappmakerbuddy.recipeapp.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel@Inject constructor(
    private val recipeRepository: RecipeRepository,
) : ViewModel() {
    private val _searchResults = MutableStateFlow<PagingData<SearchRecipeDtoItem>>(PagingData.empty())
    val searchResults: StateFlow<PagingData<SearchRecipeDtoItem>> = _searchResults

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    init {
        getAllRecipes()
    }

     private fun getAllRecipes() {
        viewModelScope.launch {
            recipeRepository.searchRecipe("")
                .catch { _searchResults.value = PagingData.empty() }
                .collectLatest { pagingData -> _searchResults.value = pagingData }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        searchRecipe(newQuery)
    }

    private fun searchRecipe(query: String) {
        viewModelScope.launch {
            recipeRepository.searchRecipe(query)
                .catch { _searchResults.value = PagingData.empty() }
                .collectLatest { pagingData -> _searchResults.value = pagingData }
        }
    }
}