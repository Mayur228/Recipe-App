package com.theappmakerbuddy.recipeapp.ui.home_screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.theappmakerbuddy.recipeapp.core.MyPadding
import com.theappmakerbuddy.recipeapp.core.Screen
import com.theappmakerbuddy.recipeapp.core.lemonMilkFonts
import com.theappmakerbuddy.recipeapp.ui.categories_screen.CategoryItem
import com.theappmakerbuddy.recipeapp.ui.home_screen.components.ComponentCategoriesState
import kotlinx.coroutines.flow.collectLatest
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    navController: NavHostController,
    onFinishCalled: () -> Unit,
) {
    val topRecipesState by viewModel.topRecipes
    val scaffoldState = rememberScaffoldState()
    val categoriesListState = viewModel.categoriesState.value
    val url = URLEncoder.encode(
        "https://as2.ftcdn.net/v2/jpg/01/57/28/31/1000_F_157283191_ALJy3vuwlPKVdDtPPCZsPpRk3BksFlwF.jpg",
        StandardCharsets.UTF_8.toString()
    )

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest {
            when (it) {
                HomeScreenUiEvents.CloseNavDrawer -> {
                    scaffoldState.drawerState.close()
                }
                HomeScreenUiEvents.OpenNavDrawer -> {
                    scaffoldState.drawerState.open()
                }
                HomeScreenUiEvents.NavigateUp -> {
                    navController.navigateUp()
                }
                HomeScreenUiEvents.NavigateToSearchRecipesScreen -> {
                    navController.navigate(
                        route = Screen.RecipeListScreen.route + "/ /${
                            URLEncoder.encode(
                                "https://plazmasticarnica.com/storage/SDXaMTAobXmDUL3kWrNgvTFVF6Q1hd-metaUmVjcHRTdHJhbmljYURlc2t0b3BfUHJhem5pxI1uaV9kZXplcnRpIGNvcHkuanBn-.jpg",
                                StandardCharsets.UTF_8.toString()
                            )
                        }/false"
                    )
                }
                HomeScreenUiEvents.NavigateToCategoriesScreen -> {
                    navController.navigate(route = Screen.CategoriesScreen.route)
                }
            }
        }
    }

    BackHandler {
        if (scaffoldState.drawerState.isOpen) {
            viewModel.sendUiEvents(HomeScreenUiEvents.CloseNavDrawer)
        } else {
            onFinishCalled()
        }
    }


    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.primary)) {

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(Screen.RecipeListScreen.route + "/saved/$url/true")
                    }
                    .padding(MyPadding.medium), verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Favourites")
                    Spacer(modifier = Modifier.width(MyPadding.small))
                    IconButton(onClick = { navController.navigate(Screen.RecipeListScreen.route + "/saved/$url/true") }) {
                        Icon(imageVector = Icons.Default.Favorite,
                            contentDescription = "saved recipes")
                    }
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.sendUiEvents(HomeScreenUiEvents.NavigateToCategoriesScreen)
                    }
                    .padding(MyPadding.medium), verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Categories")
                    Spacer(modifier = Modifier.width(MyPadding.small))
                    IconButton(onClick = { viewModel.sendUiEvents(HomeScreenUiEvents.NavigateToCategoriesScreen) }) {
                        Icon(imageVector = Icons.Default.More, contentDescription = "saved recipes")
                    }
                }

            }
        }
    ) { padding ->
        Column(){
            LazyColumn() {
                item(1) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .drawBehind {
                                    drawRect(Color.Transparent)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,

                            ) {
                            IconButton(
                                onClick = { viewModel.sendUiEvents(HomeScreenUiEvents.OpenNavDrawer) },
                                modifier = Modifier.padding(MyPadding.small)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuOpen,
                                    contentDescription = "open menu",
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            IconButton(
                                onClick = { viewModel.sendUiEvents(HomeScreenUiEvents.NavigateToSearchRecipesScreen) },
                                modifier = Modifier.padding(MyPadding.small)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "search recipes",
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                        }

                    }
                }
                item(2) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Top Recipes",
                            fontFamily = lemonMilkFonts,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(MyPadding.medium),
                            style = MaterialTheme.typography.h5
                        )
                    }
                }
                item(3) {
                    when {
                        topRecipesState.loading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colors.primaryVariant)
                            }
                        }
                        topRecipesState.error.isNotBlank() -> {
                            Text(text = topRecipesState.error,
                                color = Color.Yellow,
                                modifier = Modifier.padding(horizontal = MyPadding.medium))
                        }
                        else -> {
                            LazyRow(verticalAlignment = Alignment.CenterVertically) {
                                items(topRecipesState.recipes) { item ->
                                    Column(
                                        modifier = Modifier
                                            .width(250.dp)
                                            .height(170.dp)
                                            .padding(horizontal = MyPadding.medium)
                                            .clickable {
                                                navController.navigate(Screen.RecipeScreen.route + "/${item.title}/${item.summary}/false") {
                                                    launchSingleTop = true
                                                }
                                            }
                                    )
                                    {
                                       /* SubcomposeAsyncImage(
                                            model = item.imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(0.6f)
                                                .graphicsLayer {
                                                    shape = RoundedCornerShape(MyPadding.medium)
                                                    clip = true
                                                },
                                            contentScale = ContentScale.Crop,
                                            loading = {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(20.dp),
                                                    color = MaterialTheme.colors.primaryVariant
                                                )
                                            },
                                            filterQuality = FilterQuality.Medium,
                                        )*/
                                        Spacer(modifier = Modifier.width(MyPadding.small))
                                        Text(
                                            text = item.title,
                                            fontFamily = lemonMilkFonts,
                                            fontWeight = FontWeight.Normal,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight()
                                        )
                                        Spacer(modifier = Modifier.width(MyPadding.small))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Text(
                text = "Categories",
                fontFamily = lemonMilkFonts,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(MyPadding.small),
                style = MaterialTheme.typography.h5
            )
            CategoriesContent(categoriesListState,navController)

        }

    }
}

@Composable
fun CategoriesContent(
    categoriesListState: ComponentCategoriesState,
    navController: NavHostController,
    ) {
    val lazyGridState = rememberLazyGridState()

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {
        when {
            categoriesListState.isLoading -> {
                CircularProgressIndicator(color = MaterialTheme.colors.secondary,
                    modifier = Modifier.align(
                        Alignment.Center))
            }
            else -> {
                LazyVerticalGrid(columns = GridCells.Fixed(2),
                    state = lazyGridState,
                    contentPadding = PaddingValues(4.dp)) {
                    items(categoriesListState.categories) { category ->
                        CategoryItem(
                            categoryDto = category,
                            modifier = Modifier
                                .size(200.dp)
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