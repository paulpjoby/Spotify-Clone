package net.snatchdreams.spotifyclone.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import net.snatchdreams.spotifyclone.R
import net.snatchdreams.spotifyclone.adapters.SongAdapter
import net.snatchdreams.spotifyclone.other.Status
import net.snatchdreams.spotifyclone.ui.viewmodels.MainViewModel
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment: Fragment(
    R.layout.fragment_home
) {

    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var songAdapter: SongAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Here we are using the activity life cycle owner for the viewModel instead of the
        // Fragments lifecycle
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        setupRecyclerView()
        subscribeToObserver()

        songAdapter.setItemClickListener {
            mainViewModel.playOrToggleSong(it)
        }
    }

    private fun setupRecyclerView() = rvAllSongs.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = songAdapter
    }

    private fun subscribeToObserver()
    {
        mainViewModel.mediaItems.observe(viewLifecycleOwner){ result ->
            when(result.status)
            {
                Status.SUCCESS -> {
                    allSongsProgressBar.isVisible = false
                    result.data?.let{ songs ->
                        Log.e("Song sizexx ", ""+ songs.size)
                        Log.e("Song succ", ""+ songs.size)
                        songAdapter.songs = songs
                    }
                }

                Status.ERROR -> Unit

                Status.LOADING -> allSongsProgressBar.isVisible = true
            }
        }
    }

}