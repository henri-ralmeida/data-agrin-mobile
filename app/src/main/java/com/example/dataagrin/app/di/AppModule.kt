package com.example.dataagrin.app.di

import com.example.dataagrin.app.data.connectivity.ConnectivityChecker
import com.example.dataagrin.app.data.firebase.TaskFirestoreRepository
import com.example.dataagrin.app.data.local.AppDatabase
import com.example.dataagrin.app.data.location.LocationHelper
import com.example.dataagrin.app.data.remote.WeatherApi
import com.example.dataagrin.app.data.repository.TaskRegistryRepositoryImpl
import com.example.dataagrin.app.data.repository.TaskRepositoryImpl
import com.example.dataagrin.app.data.repository.WeatherRepositoryImpl
import com.example.dataagrin.app.domain.repository.TaskRegistryRepository
import com.example.dataagrin.app.domain.repository.TaskRepository
import com.example.dataagrin.app.domain.repository.WeatherRepository
import com.example.dataagrin.app.domain.usecase.DeleteTaskUseCase
import com.example.dataagrin.app.domain.usecase.GetTaskByIdUseCase
import com.example.dataagrin.app.domain.usecase.GetTaskRegistriesUseCase
import com.example.dataagrin.app.domain.usecase.GetTasksUseCase
import com.example.dataagrin.app.domain.usecase.GetWeatherUseCase
import com.example.dataagrin.app.domain.usecase.InsertTaskRegistryUseCase
import com.example.dataagrin.app.domain.usecase.InsertTaskUseCase
import com.example.dataagrin.app.domain.usecase.UpdateTaskUseCase
import com.example.dataagrin.app.presentation.viewmodel.TaskRegistryViewModel
import com.example.dataagrin.app.presentation.viewmodel.TaskViewModel
import com.example.dataagrin.app.presentation.viewmodel.WeatherViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule =
    module {
        single { AppDatabase.getDatabase(androidContext()) }
        single { get<AppDatabase>().taskDao() }
        single { get<AppDatabase>().taskRegistryDao() }
        single { get<AppDatabase>().weatherDao() }

        // Firebase
        single { Firebase.firestore }
        single { TaskFirestoreRepository(get()) }

        single<TaskRepository> { TaskRepositoryImpl(get<AppDatabase>().taskDao()) }
        single<TaskRegistryRepository> { TaskRegistryRepositoryImpl(get<AppDatabase>().taskRegistryDao()) }
        single<WeatherRepository> { WeatherRepositoryImpl(get<WeatherApi>(), get<AppDatabase>().weatherDao()) }
        single { ConnectivityChecker(androidContext()) }
        single { LocationHelper(androidContext()) }

        factory { GetTasksUseCase(get()) }
        factory { GetTaskByIdUseCase(get()) }
        factory { InsertTaskUseCase(get()) }
        factory { UpdateTaskUseCase(get()) }
        factory { DeleteTaskUseCase(get()) }
        factory { GetWeatherUseCase(get()) }
        factory { GetTaskRegistriesUseCase(get()) }
        factory { InsertTaskRegistryUseCase(get()) }

        viewModel { TaskViewModel(get(), get(), get(), get(), get(), get(), get()) }
        viewModel { WeatherViewModel(get(), androidContext()) }
        viewModel { TaskRegistryViewModel(get(), get(), get()) }

        single {
            val okHttpClient =
                OkHttpClient
                    .Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build()

            Retrofit
                .Builder()
                .baseUrl("https://api.open-meteo.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApi::class.java)
        }
    }
