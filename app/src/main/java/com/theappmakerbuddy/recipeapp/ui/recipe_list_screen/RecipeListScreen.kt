package com.theappmakerbuddy.recipeapp.ui.recipe_list_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.theappmakerbuddy.recipeapp.core.Screen
import com.theappmakerbuddy.recipeapp.core.lemonMilkFonts
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.SearchRecipeDtoItem
import com.theappmakerbuddy.recipeapp.ui.common.StandardToolbar
import java.util.Locale

@Composable
fun RecipeListScreen(
    viewModel: RecipeListViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val searchResults = viewModel.recipeResults.collectAsLazyPagingItems()

    Scaffold(topBar = {
        StandardToolbar(
            title = {
                Text(
                    text = viewModel.category.value.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    },
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Yellow,
                    fontFamily = lemonMilkFonts,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            showBackArrow = true,
            onBackArrowClicked = { navController.popBackStack() }
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(searchResults.itemCount) { index ->
                    val recipe = searchResults[index]

                    if (recipe != null) {
                        RecipeItem(recipe = recipe, navController = navController)
                    }
                }
                searchResults.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                        }

                        loadState.append is LoadState.Loading -> {
                            item {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                        }

                        loadState.refresh is LoadState.Error -> {
                            val e = loadState.refresh as LoadState.Error
                            item {
                                Text(
                                    text = "Error: ${e.error.localizedMessage}",
                                    color = Color.Red,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        loadState.append is LoadState.Error -> {
                            val e = loadState.append as LoadState.Error
                            item {
                                Text(
                                    text = "Error: ${e.error.localizedMessage}",
                                    color = Color.Red,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun RecipeItem(recipe: SearchRecipeDtoItem, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // Navigate to recipe details
                navController.navigate(Screen.RecipeScreen.route + "/${recipe.id}") {
                    launchSingleTop = true
                }
            }
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = recipe.image,
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(recipe.title, style = MaterialTheme.typography.h6)
                Text(
                    recipe.title,
                    style = MaterialTheme.typography.body2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}