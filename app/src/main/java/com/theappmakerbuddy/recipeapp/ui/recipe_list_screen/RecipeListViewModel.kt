package com.theappmakerbuddy.recipeapp.ui.recipe_list_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.theappmakerbuddy.recipeapp.core.Constants
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
class RecipeListViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val category = mutableStateOf("")

    private val _recipeResults =
        MutableStateFlow<PagingData<SearchRecipeDtoItem>>(PagingData.empty())
    val recipeResults: StateFlow<PagingData<SearchRecipeDtoItem>> = _recipeResults

    init {
        category.value =
            savedStateHandle.get<String>(Constants.RECIPE_LIST_SCREEN_RECIPE_CATEGORY_KEY) ?: ""

        getAllRecipes(category.value)
    }

    private fun getAllRecipes(category: String) {
        viewModelScope.launch {
            recipeRepository.getRecipeByCategory(category)
                .catch { _recipeResults.value = PagingData.empty() }
                .collectLatest { pagingData -> _recipeResults.value = pagingData }
        }
    }

}