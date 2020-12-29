package net.snatchdreams.spotifyclone.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import net.snatchdreams.spotifyclone.R
import net.snatchdreams.spotifyclone.adapters.SwipeSongAdapter
import net.snatchdreams.spotifyclone.exoplayer.MusicServiceConnection
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class) // Live as long as our app lives
object AppModule {

    //Single instance of glide need to be created so we use @Singleton
    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) : RequestManager
    {
        return Glide.with(context).setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.ic_empty_image)
                .error(R.drawable.ic_error_image)
                .diskCacheStrategy(DiskCacheStrategy.DATA) // Cached
        )
    }

    @Singleton
    @Provides
    fun provideMusicServiceConnection(
        @ApplicationContext context: Context,
    ) = MusicServiceConnection(context)


    // There is no construction injection done for the SwipeSongAdapter
    // So dagger hilt wont know how to inject or create that so we are creating a provides method
    // or we could have directly used @Inject constructor() in the SwipeSongAdapter
    @Singleton
    @Provides
    fun provideSongAdapter() = SwipeSongAdapter()
}