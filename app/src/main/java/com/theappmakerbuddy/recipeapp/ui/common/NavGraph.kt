package com.theappmakerbuddy.recipeapp.ui.common

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.theappmakerbuddy.recipeapp.core.Constants
import com.theappmakerbuddy.recipeapp.core.Screen
import com.theappmakerbuddy.recipeapp.ui.home_screen.HomeScreen
import com.theappmakerbuddy.recipeapp.ui.recipe_list_screen.RecipeListScreen
import com.theappmakerbuddy.recipeapp.ui.recipe_screen.RecipeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {

        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController = navController)
        }

        composable(route = Screen.RecipeScreen.route + "/{${Constants.RECIPE_ID}}",
            arguments = listOf(
                navArgument(name = Constants.RECIPE_ID) {
                    type = NavType.IntType
                },
            )) {
            RecipeScreen(navController = navController)
        }

        composable(
            route = Screen.RecipeListScreen.route + "/{${Constants.RECIPE_LIST_SCREEN_RECIPE_CATEGORY_KEY}}/{${Constants.RECIPE_LIST_SCREEN_RECIPE_IMAGE_URL_KEY}}/{${Constants.RECIPE_SCREEN_SHOULD_LOAD_FROM_SAVED_RECIPES}}",
            arguments = listOf(
                navArgument(name = Constants.RECIPE_LIST_SCREEN_RECIPE_CATEGORY_KEY) {
                    type = NavType.StringType
                },
                navArgument(name = Constants.RECIPE_LIST_SCREEN_RECIPE_IMAGE_URL_KEY)
                {
                    type = NavType.StringType
                },
                navArgument(name = Constants.RECIPE_SCREEN_SHOULD_LOAD_FROM_SAVED_RECIPES)
                {
                    type = NavType.BoolType
                },
            )
        ) {
            RecipeListScreen(navController = navController)
        }
    }
}