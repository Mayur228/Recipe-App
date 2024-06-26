package com.theappmakerbuddy.recipeapp.ui.search_screen

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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun SearchScreen(
    viewModel: SearchScreenViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val query by viewModel.query.collectAsState()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()

    Scaffold(topBar = {
        StandardToolbar(
            title = {
                Text(
                    text = "Search",
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
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

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

/*

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
) {
    TextField(
        modifier = modifier,
        value = state.searchTerm,
        onValueChange = {

        },
        placeholder = {
            Text(
                text = "Search Your Recipes here",
                color = primaryGray
            )
        },
        shape = RoundedCornerShape(50),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            autoCorrect = true,
            keyboardType = KeyboardType.Text,
        ),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            disabledTextColor = Color.Transparent,
            backgroundColor = Color.Gray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        textStyle = TextStyle(color = Color.White),
        maxLines = 1,
        singleLine = true,
//        trailingIcon = {
//            IconButton(onClick = {
//                onEvent(SearchUiEvents.SearchFilm(searchTerm = state.searchTerm))
//            }) {
//                Icon(
//                    imageVector = Icons.Default.Search,
//                    tint = primaryGray,
//                    contentDescription = null
//                )
//            }
//        },
    )
}*/
