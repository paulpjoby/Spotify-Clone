package net.snatchdreams.spotifyclone.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import kotlinx.android.synthetic.main.list_item.view.*
import net.snatchdreams.spotifyclone.R



class SwipeSongAdapter: BaseSongAdapter(R.layout.swipe_item) {

    override val differ = AsyncListDiffer(this, diffCallBack)
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            val text = "${song.title} - ${song.subtitle}"
            tvPrimary.text = text

            setOnClickListener {
                onItemClickListener?.let { method ->
                    method(song)
                }
            }
        }
    }
}