package com.example.dataagrin.app.di

import com.example.dataagrin.app.data.local.AppDatabase
import com.example.dataagrin.app.data.remote.WeatherApi
import com.example.dataagrin.app.data.repository.ActivityRepositoryImpl
import com.example.dataagrin.app.data.repository.TaskRepositoryImpl
import com.example.dataagrin.app.data.repository.WeatherRepositoryImpl
import com.example.dataagrin.app.domain.repository.ActivityRepository
import com.example.dataagrin.app.domain.repository.TaskRepository
import com.example.dataagrin.app.domain.repository.WeatherRepository
import com.example.dataagrin.app.domain.usecase.GetActivitiesUseCase
import com.example.dataagrin.app.domain.usecase.GetTasksUseCase
import com.example.dataagrin.app.domain.usecase.GetWeatherUseCase
import com.example.dataagrin.app.domain.usecase.InsertActivityUseCase
import com.example.dataagrin.app.domain.usecase.UpdateTaskUseCase
import com.example.dataagrin.app.presentation.viewmodel.ActivityViewModel
import com.example.dataagrin.app.presentation.viewmodel.TaskViewModel
import com.example.dataagrin.app.presentation.viewmodel.WeatherViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().taskDao() }
    single { get<AppDatabase>().activityDao() }
    single { get<AppDatabase>().weatherDao() }

    single<TaskRepository> { TaskRepositoryImpl(get<AppDatabase>().taskDao()) }
    single<ActivityRepository> { ActivityRepositoryImpl(get<AppDatabase>().activityDao()) }
    single<WeatherRepository> { WeatherRepositoryImpl(get<WeatherApi>(), get<AppDatabase>().weatherDao()) }

    factory { GetTasksUseCase(get()) }
    factory { UpdateTaskUseCase(get()) }
    factory { GetWeatherUseCase(get()) }
    factory { GetActivitiesUseCase(get()) }
    factory { InsertActivityUseCase(get()) }

    viewModel { TaskViewModel(get(), get()) }
    viewModel { WeatherViewModel(get(), androidContext()) }
    viewModel { ActivityViewModel(get(), get()) }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}
