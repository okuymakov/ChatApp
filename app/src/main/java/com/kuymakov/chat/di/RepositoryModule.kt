package com.kuymakov.chat.di

import com.kuymakov.chat.data.repositories.*
import com.kuymakov.chat.domain.repositories.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {
    @Binds
    fun provideAuthRepo(repo: AuthRepositoryImpl): AuthRepository

    @Binds
    fun provideChatRepo(repo: ChatRepositoryImpl): ChatRepository

    @Binds
    fun provideUserRepo(repo: UserRepositoryImpl): UserRepository

    @Binds
    fun providePhotoRepo(repo: PhotoRepositoryImpl): PhotoRepository

    @Binds
    fun provideMessageRepo(repo: MessageRepositoryImpl): MessageRepository
}

