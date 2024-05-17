package com.theappmakerbuddy.recipeapp.ui.common

import com.theappmakerbuddy.recipeapp.R
import com.theappmakerbuddy.recipeapp.core.Screen

sealed class BottomNavItem(var title: String, var icon: Int,var destination: String) {
    object Home : BottomNavItem(
        title = "Home",
        icon = R.drawable.ic_home,
        destination = Screen.HomeScreen.route
    )
    object Favorite: BottomNavItem(
        title = "Favorite",
        icon = R.drawable.ic_star,
        destination = Screen.RecipeScreen.route
    )
}
