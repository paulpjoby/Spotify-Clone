package net.snatchdreams.spotifyclone.ui.viewmodels

import android.support.v4.media.MediaBrowserCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.snatchdreams.spotifyclone.data.entities.Song
import net.snatchdreams.spotifyclone.exoplayer.MusicServiceConnection
import net.snatchdreams.spotifyclone.exoplayer.isPrepared
import net.snatchdreams.spotifyclone.other.Constants
import net.snatchdreams.spotifyclone.other.Resource
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import net.snatchdreams.spotifyclone.exoplayer.isPlayEnabled
import net.snatchdreams.spotifyclone.exoplayer.isPlaying

class MainViewModel @ViewModelInject constructor(
    private val musicServiceConnection: MusicServiceConnection
): ViewModel(){
    private  val _mediaItems = MutableLiveData<Resource<List<Song>>>()
    val mediaItems: LiveData<Resource<List<Song>>> = _mediaItems

    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val curPlayingSong = musicServiceConnection.curPlayingSong
    val playbackState = musicServiceConnection.playbackState


    init {
        _mediaItems.postValue(Resource.loading(null))
        musicServiceConnection.subscribe(Constants.MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback()  {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    val items = children.map {
                        Song(
                            it.mediaId!!,
                            it.description.title.toString(),
                            it.description.subtitle.toString(),
                            it.description.mediaUri.toString(),
                            it.description.iconUri.toString()
                        )
                    }

                    _mediaItems.postValue(Resource.success(items))
                }
            })
    }

    fun skipToNextSong()
    {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun skipToPreviousSong()
    {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(pos: Long)
    {
        musicServiceConnection.transportControls.seekTo(pos)
    }


    fun playOrToggleSong(mediaItem: Song, toggle: Boolean = false)
    {
        val isPrepared = playbackState.value?.isPrepared ?: false
        if(isPrepared && mediaItem.mediaId == curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID))
        {
            playbackState.value?.let{ playbackState->
                when {
                    playbackState.isPlaying -> if(toggle) musicServiceConnection.transportControls.pause()
                    playbackState.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
        }
        else{
            musicServiceConnection.transportControls.playFromMediaId(mediaItem.mediaId, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(Constants.MEDIA_ROOT_ID,
            object: MediaBrowserCompat.SubscriptionCallback(){})
    }
}