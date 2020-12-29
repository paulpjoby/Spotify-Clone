package net.snatchdreams.spotifyclone.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import net.snatchdreams.spotifyclone.data.entities.Song
import net.snatchdreams.spotifyclone.other.Constants
import java.lang.Exception


class MusicDatabase {
    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(Constants.SONG_COLLECTION)

    suspend fun getAllSongs(): List<Song>
    {
        return try{
            songCollection.get().await().toObjects(Song::class.java)
        }
        catch (ex: Exception)
        {
            emptyList<Song>()
        }
    }
}