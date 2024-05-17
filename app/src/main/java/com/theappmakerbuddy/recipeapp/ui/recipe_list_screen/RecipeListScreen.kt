@file:OptIn(ExperimentalFoundationApi::class)

package com.theappmakerbuddy.recipeapp.ui.recipe_list_screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.theappmakerbuddy.recipeapp.core.MyPadding
import com.theappmakerbuddy.recipeapp.core.Screen
import com.theappmakerbuddy.recipeapp.core.lemonMilkFonts
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RecipeListScreen(
    viewModel: RecipeListViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state = viewModel.state
    val showSearchBoxState = rememberSaveable { mutableStateOf(false) }
    val searchBoxState = viewModel.searchBoxState.value
    val keyboardController = LocalSoftwareKeyboardController.current
    val isEditModeOn = viewModel.isEditModeOn.value
    val scaffoldState = rememberScaffoldState()
    val refreshState = rememberSwipeRefreshState(isRefreshing = viewModel.isRefreshingState.value)

    LaunchedEffect(key1 = Unit) {
        viewModel.toRecipeListScreenEvents.collectLatest {
            when (it) {
                ToRecipeListScreenEvents.NavigateUp -> navController.navigateUp()
                is ToRecipeListScreenEvents.ShowSnackbar -> scaffoldState.snackbarHostState.showSnackbar(
                    it.message
                )
            }
        }
    }

    BackHandler {
        if (isEditModeOn) {
//            viewModel.receiveFromRecipeListScreenEvents(FromRecipeListScreenEvents.DisableEditMode)
        } else {
            viewModel.onEvent(ToRecipeListScreenEvents.NavigateUp)
        }
    }



    if (viewModel.getSavedRecipes.value) {
        SwipeRefresh(state = refreshState, onRefresh = {
            if (viewModel.getSavedRecipes.value) {
//                viewModel.receiveFromRecipeListScreenEvents(FromRecipeListScreenEvents.ChangeRefreshState)
                viewModel.onClearSearchBoxButtonClicked()
//                viewModel.receiveFromRecipeListScreenEvents(FromRecipeListScreenEvents.ChangeRefreshState)
            }
        }) {
            RecipeListUi(
                scaffoldState = scaffoldState,
                viewModel = viewModel,
                isEditModeOn = isEditModeOn,
                navController = navController,
                showSearchBoxState = showSearchBoxState,
                searchBoxState = searchBoxState,
                keyboardController = keyboardController,
                state = state
            )
        }
    }else{
        RecipeListUi(
            scaffoldState = scaffoldState,
            viewModel = viewModel,
            isEditModeOn = isEditModeOn,
            navController = navController,
            showSearchBoxState = showSearchBoxState,
            searchBoxState = searchBoxState,
            keyboardController = keyboardController,
            state = state
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RecipeListUi(
    isEditModeOn: Boolean,
    viewModel: RecipeListViewModel,
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    showSearchBoxState: MutableState<Boolean>,
    searchBoxState: String,
    keyboardController: SoftwareKeyboardController?,
    state: RecipeListScreenState,

    ) {
    Scaffold(scaffoldState = scaffoldState) {padding->
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    SubcomposeAsyncImage(
                        model = viewModel.imageUrl.value,
                        loading = {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(50.dp)
                                    .size(50.dp),
                                color = MaterialTheme.colors.primaryVariant
                            )
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                this.alpha = 0.50f
                                shadowElevation = 8.dp.toPx()
                                clip = true
                            }
                            .align(Alignment.Center),
                        contentScale = ContentScale.Crop,
                        filterQuality = FilterQuality.Medium
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .drawBehind {
                                drawRect(Color.Transparent)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedVisibility(visible = isEditModeOn) {
                            IconButton(
                                onClick = {
//                                    viewModel.receiveFromRecipeListScreenEvents(
//                                        FromRecipeListScreenEvents.DeleteButtonClicked
//                                    )
                                },
                                modifier = Modifier.padding(MyPadding.small)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "goto home screen"
                                )
                            }
                        }

                        AnimatedVisibility(visible = !isEditModeOn) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = { navController.navigateUp() },
                                    modifier = Modifier.padding(MyPadding.small)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "goto home screen"
                                    )
                                }
                                AnimatedVisibility(visible = !showSearchBoxState.value) {
                                    IconButton(onClick = { showSearchBoxState.value = true }) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "search recipe"
                                        )
                                    }
                                }
                                AnimatedVisibility(visible = showSearchBoxState.value) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .clip(shape = RoundedCornerShape(50.dp)) // Adjust the radius as needed
                                            .background(Color.White) // Set white background
                                    ) {
                                        TextField(
                                            value = searchBoxState,
                                            onValueChange = viewModel::onSearchBoxValueChanged,
                                            modifier = Modifier.fillMaxWidth(),
                                            placeholder = {
                                                Text("Search ${viewModel.category.value} recipes", color = Color.Black)
                                            },
                                            keyboardActions = KeyboardActions(onSearch = {
                                                keyboardController?.hide()
                                            }),
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                            colors = TextFieldDefaults.textFieldColors(textColor = Color.Black)
                                        )
                                    }

                                }
                                AnimatedVisibility(visible = showSearchBoxState.value) {
                                    IconButton(
                                        onClick = {
                                            showSearchBoxState.value = false
                                            viewModel.onClearSearchBoxButtonClicked()
                                        },
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = viewModel.category.value.ifBlank { "Search across all recipes!" },
                        style = MaterialTheme.typography.h4,
                        fontWeight = FontWeight.ExtraLight,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onSurface,
                        fontFamily = lemonMilkFonts,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            items(state.items.size) { index ->
                val item = state.items[index]

                LaunchedEffect(key1 = Unit) {
                    if (index >= state.items.size - 5 && !state.endReached && !state.isLoading) {
//                        viewModel.loadNextItems()
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(MyPadding.small)
                        .combinedClickable(onLongClick = {
                            if (!isEditModeOn && viewModel.getSavedRecipes.value) {
                                viewModel.isEditModeOn.value = true
                            }
                        }) {
                            if (!isEditModeOn) {
                                val title = item.title
                                navController.navigate(Screen.RecipeScreen.route + "/${title}/${"name"}/${viewModel.getSavedRecipes.value}") {
                                    launchSingleTop = true
                                }
                            } else {
                                viewModel.onSelectRadioButtonClicked(item.title)
                            }
                        }
                ) {
                    SubcomposeAsyncImage(
                        model = item.image,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((0.8f * 250).dp)
                            .graphicsLayer {
                                shape = RoundedCornerShape(MyPadding.medium)
                                clip = true
                            },
                        contentScale = ContentScale.Crop,
                        filterQuality = FilterQuality.Medium,
                        colorFilter = if (isEditModeOn) ColorFilter.tint(
                            Color.DarkGray,
                            BlendMode.Multiply
                        ) else null
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = item.title,
                            fontFamily = lemonMilkFonts,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                        )
                        AnimatedVisibility(isEditModeOn) {
                            RadioButton(
                                selected = viewModel.recipesToBeDeleted.contains(item.title),
                                onClick = {
                                    viewModel.onSelectRadioButtonClicked(title = item.title)
                                })
                        }
                    }
                }
            }
            if (state.isLoading) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colors.primaryVariant,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}