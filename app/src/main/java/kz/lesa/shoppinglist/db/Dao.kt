package kz.lesa.shoppinglist.db
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kz.lesa.shoppinglist.entities.LibraryItem
import kz.lesa.shoppinglist.entities.NoteItem
import kz.lesa.shoppinglist.entities.ShopListItem
import kz.lesa.shoppinglist.entities.ShopListNameItem

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

    @Query ("SELECT * FROM shopping_list_names")
    fun getAllShoppingListNames() : Flow<List<ShopListNameItem>>
    @Query ("DELETE FROM shopping_list_names WHERE id IS :id")
    suspend fun deleteShoppingListName(id: Int)
    @Insert
    suspend fun insertShopListName(listName: ShopListNameItem)
    @Update
    suspend fun updateShopListName(listName: ShopListNameItem)

    @Query ("SELECT * FROM shop_list_item WHERE listId LIKE :listId")
    fun getAllShopListItems(listId: Int) : Flow<List<ShopListItem>>
    @Query ("DELETE FROM shop_list_item WHERE listId LIKE :listId")
    suspend fun deleteShopListItemsByListId (listId: Int)
    @Insert
    suspend fun insertItem(shopListItem: ShopListItem)
    @Update
    suspend fun updateShopListItem(item: ShopListItem)

    @Query ("SELECT * FROM library WHERE name LIKE :name")
    suspend fun getAllLibraryItems(name: String) : List<LibraryItem>
    @Query ("DELETE FROM library WHERE id IS :id")
    suspend fun deleteLibraryItem(id: Int)
    @Insert
    suspend fun insertLibraryItem(libraryItem: LibraryItem)
    @Update
    suspend fun updateLibraryItem(item: LibraryItem)
}