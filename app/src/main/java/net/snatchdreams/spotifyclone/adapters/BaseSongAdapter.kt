package net.snatchdreams.spotifyclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*
import net.snatchdreams.spotifyclone.R
import net.snatchdreams.spotifyclone.data.entities.Song

abstract class BaseSongAdapter(
    private val layoutId: Int
) : RecyclerView.Adapter<BaseSongAdapter.SongViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(
            layoutId,
            parent,
            false
        )

        return SongViewHolder(view)
    }


    override fun getItemCount(): Int {
        return songs.size
    }

    class SongViewHolder(view: View): RecyclerView.ViewHolder(view)

    protected val diffCallBack = object : DiffUtil.ItemCallback<Song>()
    {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }


    protected abstract val differ :AsyncListDiffer<Song>

    var songs: List<Song>
    get() = differ.currentList
    set(value) = differ.submitList(value)


    protected var onItemClickListener: ((Song) -> Unit)? = null

    fun setItemClickListener(listener: (Song) -> Unit){
        onItemClickListener = listener
    }
}