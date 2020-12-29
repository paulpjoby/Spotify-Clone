package net.snatchdreams.spotifyclone.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_song.*
import net.snatchdreams.spotifyclone.R
import net.snatchdreams.spotifyclone.data.entities.Song
import net.snatchdreams.spotifyclone.exoplayer.isPlaying
import net.snatchdreams.spotifyclone.exoplayer.toSong
import net.snatchdreams.spotifyclone.other.Status
import net.snatchdreams.spotifyclone.ui.viewmodels.MainViewModel
import net.snatchdreams.spotifyclone.ui.viewmodels.SongViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class SongFragment: Fragment(R.layout.fragment_song) {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var mainViewModel: MainViewModel
    private val songViewModel: SongViewModel by viewModels()

    private var curPlayingSong: Song? = null
    private var playbackState:PlaybackStateCompat? = null

    private var shouldUpdateSeekbar = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        subscribeToObservers()

        ivPlayPauseDetail.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    setCurPlayerTimeToTextView(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekbar = true
                }
            }
        })

        ivSkipPrevious.setOnClickListener {
            mainViewModel.skipToPreviousSong()
        }

        ivSkip.setOnClickListener {
            mainViewModel.skipToNextSong()
        }
    }

    private fun updateTitleAndSongImage(song: Song)
    {
        val title = "${song.title} - ${song.subtitle}"
        tvSongName.text = title
        glide.load(song.imageUrl).into(ivSongImage)
    }

    private fun subscribeToObservers()
    {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) {
            it?.let { resource ->
                when(resource.status)
                {
                    Status.SUCCESS -> {
                        resource.data?.let { songs ->
                            if (curPlayingSong == null && songs.isNotEmpty()) {
                                curPlayingSong = songs[0]
                                updateTitleAndSongImage(songs[0])
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }

        mainViewModel.curPlayingSong.observe(viewLifecycleOwner){
            if(it == null) return@observe
            curPlayingSong = it.toSong()
            updateTitleAndSongImage(curPlayingSong!!)
        }


        mainViewModel.playbackState.observe(viewLifecycleOwner){
            playbackState = it
            ivPlayPauseDetail.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
            seekBar.progress = it?.position?.toInt() ?: 0
        }

        songViewModel.curPlayerPosition.observe(viewLifecycleOwner){ progress ->

            // This is make sure we don't update the seekbar while user is seeking
            if(shouldUpdateSeekbar)
            {
                seekBar.progress = progress.toInt()
                setCurPlayerTimeToTextView(progress)
            }
        }

        songViewModel.curSongDuration.observe(viewLifecycleOwner){
            seekBar.max = it.toInt()
            tvSongDuration.text = formatMillisecondsToSeekBarTime(it)
        }

    }

    private fun setCurPlayerTimeToTextView(ms: Long)
    {
        tvCurTime.text = formatMillisecondsToSeekBarTime(ms)
    }


    private fun formatMillisecondsToSeekBarTime(millis: Long): String? {

        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    millis
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    millis
                )
            )
        )


    }
}