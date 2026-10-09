package com.siarheikrupenich.trailermobile.core.network.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ImageLoaderModule {

    @Singleton
    @Provides
    fun provideImageLoader(
        @ApplicationContext context: Context,
    ): ImageLoader {
        val imageOkHttpClient = OkHttpClient.Builder().build()

        return ImageLoader.Builder(context)
            .okHttpClient(imageOkHttpClient)
            .diskCache {
                DiskCache.Builder()
                    .directory(File(context.cacheDir, IMAGE_CACHE))
                    .maxSizeBytes(CACHE_50_MB)
                    .build()
            }
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(MAX_SIZE_PERCENT)
                    .build()
            }
            .crossfade(true)
            .build()
    }

    private const val IMAGE_CACHE = "image_cache"
    private const val CACHE_50_MB = 50L * 1024L * 1024L
    private const val MAX_SIZE_PERCENT = 0.25
}
