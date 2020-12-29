package net.snatchdreams.spotifyclone.exoplayer.callbacks

import android.widget.Toast
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import net.snatchdreams.spotifyclone.exoplayer.MusicService

class MusicPlayerEventListener(
    private val musicService: MusicService
) : Player.EventListener{
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        if(playbackState == Player.STATE_READY && !playWhenReady)
        {
            musicService.stopForeground(false)
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService, "An Unknown Error Occurred", Toast.LENGTH_SHORT).show()
    }
}