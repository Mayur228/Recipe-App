package com.theappmakerbuddy.recipeapp.di

import android.app.Application
import androidx.room.Room
import com.theappmakerbuddy.recipeapp.core.Constants
import com.theappmakerbuddy.recipeapp.data.local.RecipeDatabase
import com.theappmakerbuddy.recipeapp.data.remote.RecipeApi
import com.theappmakerbuddy.recipeapp.data.remote.custom.ApiKeyInterceptor
import com.theappmakerbuddy.recipeapp.data.remote.repository.RecipeRepositoryImpl
import com.theappmakerbuddy.recipeapp.domain.repository.RecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesRecipeApi(): RecipeApi = Retrofit
        .Builder()
        .baseUrl(Constants.BASE_URL)
        .client(
            OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor())
            .build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RecipeApi::class.java)

    @Provides
    @Singleton
    fun providesRecipeDatabase(app: Application): RecipeDatabase = Room
        .databaseBuilder(app, RecipeDatabase::class.java, RecipeDatabase.DATABASE_NAME)
        .build()

    @Provides
    @Singleton
    fun providesRecipeRepository(recipeApi: RecipeApi, database: RecipeDatabase): RecipeRepository =
        RecipeRepositoryImpl(recipeApi = recipeApi, recipeDatabase = database)
}