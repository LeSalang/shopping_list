package kz.lesa.shoppinglist.db
import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kz.lesa.shoppinglist.entities.NoteItem

@Dao
interface Dao {
    @Query ("SELECT * FROM note_list")
    fun getAllNotes() : Flow<List<NoteItem>>

    @Query ("DELETE FROM note_list WHERE id IS :id")
    suspend fun deleteNote(id: Int)

    @Insert
    suspend fun insertNote(note: NoteItem)

    @Update
    suspend fun updateNote(note: NoteItem)


}