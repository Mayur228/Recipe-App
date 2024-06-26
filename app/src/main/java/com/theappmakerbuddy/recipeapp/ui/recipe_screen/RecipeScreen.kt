package com.theappmakerbuddy.recipeapp.ui.recipe_screen

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.theappmakerbuddy.recipeapp.core.MyPadding
import com.theappmakerbuddy.recipeapp.core.lemonMilkFonts
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.Ingredient
import com.theappmakerbuddy.recipeapp.ui.recipe_screen.components.ingredientToPDF
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecipeScreen(
    navController: NavHostController,
    viewModel: RecipeScreenViewModel = hiltViewModel(),
) {
    val screenState = viewModel.recipeState.value
    val scaffoldState = rememberScaffoldState()
    val favoriteButtonState = viewModel.favouriteState.value
    val multiplePermissionState =
        rememberMultiplePermissionsState(permissions = listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.uiRecipeScreenEvents.collectLatest { event ->
            when (event) {
                is RecipeScreenEvents.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    when {
        screenState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colors.primaryVariant)
            }
        }
        screenState.error.isNotBlank() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = screenState.error, color = MaterialTheme.colors.secondary)
            }
        }
        else -> {
            Scaffold(scaffoldState = scaffoldState) {padding->
                LazyColumn(modifier = Modifier.fillMaxSize()) {

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()

                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter)
                                    .drawBehind { drawRect(color = Color.Transparent) },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = { navController.navigateUp() },
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "goto home screen",
                                    )
                                }

                                IconButton(
                                    onClick = viewModel::onSaveRecipeButtonClicked,
                                ) {
                                    Icon(
                                        imageVector =
                                        when (favoriteButtonState) {
                                            RecipeSaveState.NOT_SAVED -> {
                                                Icons.Outlined.Favorite
                                            }
                                            RecipeSaveState.ALREADY_EXISTS -> {
                                                Icons.Default.Favorite
                                            }
                                            RecipeSaveState.SAVED -> {
                                                Icons.Default.Favorite
                                            }
                                            else -> {
                                                Icons.Outlined.Favorite
                                            }
                                        },
                                        contentDescription = "Save Recipe",
                                        tint = if (favoriteButtonState == RecipeSaveState.NOT_SAVED) Color.White else Color.Red
                                    )
                                }
                            }

                            SubcomposeAsyncImage(
                                model = screenState.recipe?.image,
                                loading = {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(50.dp),
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
                            Text(
                                text = screenState.recipe?.title ?: "",
                                style = MaterialTheme.typography.h4,
                                fontWeight = FontWeight.ExtraLight,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colors.onSurface,
                                fontFamily = lemonMilkFonts,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                            Text(
                                text = "Download Ingredients",
                                fontFamily = lemonMilkFonts,
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(horizontal = MyPadding.medium)
                                    .clickable{
                                        if (multiplePermissionState.shouldShowRationale) {
                                            viewModel.sendRecipeScreenUiEvent(RecipeScreenEvents.ShowSnackbar(
                                                "Please provide storage permission to continue"))
                                        } else if (!multiplePermissionState.allPermissionsGranted) {
                                            multiplePermissionState.launchMultiplePermissionRequest()
                                        } else {
//                                            exportPdf(context = context,
//                                                ingredients = ingredients,
//                                                viewModel = viewModel,
//                                                name = screenState.recipe.title)
                                        }

                                    }
                            )
                            Spacer(modifier = Modifier.height(MyPadding.medium))
                            IconButton(onClick = {
                                if (multiplePermissionState.shouldShowRationale) {
                                    viewModel.sendRecipeScreenUiEvent(RecipeScreenEvents.ShowSnackbar(
                                        "Please provide storage permission to continue"))
                                } else if (!multiplePermissionState.allPermissionsGranted) {
                                    multiplePermissionState.launchMultiplePermissionRequest()
                                } else {
//                                    exportPdf(context = context,
//                                        ingredients = ingredients,
//                                        viewModel = viewModel,
//                                        name = screenState.recipe.title)
                                }
                            }) {
                                Icon(imageVector = Icons.Default.DownloadForOffline,
                                    contentDescription = "download recipe")
                            }
                        }
                        Spacer(modifier = Modifier.height(MyPadding.medium))
                    }

                    item {
                        Text(
                            text = "Ingredients",
                            fontFamily = lemonMilkFonts,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.h4,
                            modifier = Modifier.padding(horizontal = MyPadding.medium)
                        )
                        Spacer(modifier = Modifier.height(MyPadding.medium))
                    }

                    items(screenState.recipe?.extendedIngredients ?: emptyList()) { ingredient ->

                        Text(
                            text = " ${ingredient.name} ${ingredient.amount} ${ingredient.unit}",
                            fontFamily = lemonMilkFonts,
                            fontWeight = FontWeight.Normal,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(horizontal = MyPadding.medium)
                        )
                        Spacer(modifier = Modifier.height(MyPadding.medium))
                    }

                    item {
                        Text(
                            text = "Method",
                            fontFamily = lemonMilkFonts,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.h4,
                            modifier = Modifier.padding(horizontal = MyPadding.medium)
                        )
                        Spacer(modifier = Modifier.height(MyPadding.medium))
                    }

                    item {
                        Text(
                            text = screenState.recipe?.summary ?: "",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.ExtraLight,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colors.onSurface,
                            fontFamily = lemonMilkFonts,
                            modifier = Modifier.padding(horizontal = MyPadding.medium)
                        )
                    }
                }
            }
        }
    }
}

fun exportPdf(context: Context, ingredients: List<Ingredient>, viewModel: RecipeScreenViewModel, name: String) {

    val pdfDocument =
        ingredientToPDF(ingredient = ingredients, name = name)

    if (Build.VERSION.SDK_INT > 29) {
        val resolver = context.contentResolver
        val values = ContentValues()
        // save to a folder
        values.put(MediaStore.MediaColumns.DISPLAY_NAME,
            "${viewModel.recipeState.value.recipe?.title}.pdf")
        values.put(MediaStore.MediaColumns.MIME_TYPE,
            "application/pdf")
        values.put(MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_DOWNLOADS + "/" + "recipeapp")
        val uri =
            resolver.insert(MediaStore.Files.getContentUri("external"),
                values)
        val outputStream = resolver.openOutputStream(uri!!)

        try {
            pdfDocument.writeTo(outputStream)
            Toast.makeText(context,
                "file saved successfully at Downloads/recipeapp/${viewModel.recipeState.value.recipe?.title}.pdf",
                Toast.LENGTH_LONG).show()
            Log.d("Check",
                "file saved successfully at ${uri}")

        } catch (e: Exception) {
            Log.d("Check",
                "error occurred while saving file\n $e")
            Toast.makeText(context,
                "unable to save file",
                Toast.LENGTH_LONG).show()
        }

    } else {
        val file =
            File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS),
                "recipeapp/ingredients.pdf"
            )

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context,
                "file saved successfully at Downloads/recipeapp/ingredient.pdf",
                Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context,
                "unable to save file",
                Toast.LENGTH_LONG).show()
        }
    }
}