package com.theappmakerbuddy.recipeapp.ui.home_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.theappmakerbuddy.recipeapp.R
import com.theappmakerbuddy.recipeapp.core.MyPadding
import com.theappmakerbuddy.recipeapp.core.Screen
import com.theappmakerbuddy.recipeapp.core.lemonMilkFonts
import com.theappmakerbuddy.recipeapp.ui.categories_screen.CategoryItem
import com.theappmakerbuddy.recipeapp.ui.common.StandardToolbar
import com.theappmakerbuddy.recipeapp.ui.home_screen.components.ComponentCategoriesState
import com.theappmakerbuddy.recipeapp.ui.home_screen.components.ComponentTopRecipesState
import com.theappmakerbuddy.recipeapp.ui.theme.primaryGray
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val topRecipesState by viewModel.topRecipes
    val categoriesListState = viewModel.categoriesState.value

    Scaffold(topBar = {
        StandardToolbar(
            title = {
                Text(
                    text = "Home",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Yellow,
                    fontFamily = lemonMilkFonts,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            showBackArrow = false,
            navActions = {
                IconButton(onClick = {
//                    viewModel.sendUiEvents(HomeScreenUiEvents.NavigateToSearchRecipesScreen)
                    navController.navigate(Screen.SearchScreen.route)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        tint = primaryGray
                    )
                }
            }
        )
    }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CategoriesContent(categoriesListState, topRecipesState, navController)
        }
    }

}

@Composable
fun CategoriesContent(
    categoriesListState: ComponentCategoriesState,
    topRecipesState: ComponentTopRecipesState,
    navController: NavHostController,
) {
    val lazyGridState = rememberLazyGridState()

    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(8.dp)
    ) {
        when {
            categoriesListState.isLoading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.secondary,
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                    contentPadding = PaddingValues(4.dp),
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "Top Recipe",
                            fontFamily = lemonMilkFonts,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(MyPadding.small),
                            style = MaterialTheme.typography.h5
                        )
                    }

                    items(topRecipesState.recipes) { recipe ->
                        Column(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(horizontal = MyPadding.medium)
                                .clickable {
                                    navController.navigate(Screen.RecipeScreen.route + "/${recipe.id}") {
                                        launchSingleTop = true
                                    }
                                }
                        )
                        {
                            AsyncImage(
                                model = recipe.image,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.6f)
                                    .graphicsLayer {
                                        shape = RoundedCornerShape(MyPadding.medium)
                                        clip = true
                                    },
                                contentScale = ContentScale.Crop,
                                filterQuality = FilterQuality.Medium,
                            )
                            Spacer(modifier = Modifier.width(MyPadding.small))
                            Text(
                                text = recipe.title,
                                fontFamily = lemonMilkFonts,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                            )
                            Spacer(modifier = Modifier.width(MyPadding.small))
                        }
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "Categories",
                            fontFamily = lemonMilkFonts,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(MyPadding.small),
                            style = MaterialTheme.typography.h5
                        )
                    }
                    items(categoriesListState.categories) { category ->
                        CategoryItem(
                            categoryDto = category,
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(
                                        route = Screen.RecipeListScreen.route + "/${category.name}/${
                                            URLEncoder.encode(
                                                category.name,
                                                StandardCharsets.UTF_8.toString()
                                            )
                                        }/false"
                                    ) { launchSingleTop = true }
                                }
                        )
                    }
                }
            }
        }
    }
}