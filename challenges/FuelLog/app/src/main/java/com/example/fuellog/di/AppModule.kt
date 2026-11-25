package com.example.fuellog.di

import com.example.fuellog.data.AndroidLogger
import com.example.fuellog.data.AppLogger
import com.example.fuellog.data.Clock
import com.example.fuellog.data.FuelLogRepository
import com.example.fuellog.data.InMemoryFuelLogRepository
import com.example.fuellog.data.SystemClock
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppScope(): CoroutineScope = CoroutineScope(SupervisorJob())
}

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {
    @Binds abstract fun bindCLock(impl: SystemClock): Clock
    @Binds abstract fun bindFuelLogRepository(impl: InMemoryFuelLogRepository): FuelLogRepository
    @Binds abstract fun bindAppLogger(impl: AndroidLogger): AppLogger
}