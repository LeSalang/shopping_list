package kz.lesa.shoppinglist.db

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import kz.lesa.shoppinglist.entities.LibraryItem
import kz.lesa.shoppinglist.entities.NoteItem
import kz.lesa.shoppinglist.entities.ShopListItem
import kz.lesa.shoppinglist.entities.ShopListNameItem

class MainViewModel(dataBase: MainDataBase) : ViewModel() {
    val dao = dataBase.getDao()
    val allNotes: LiveData<List<NoteItem>> = dao.getAllNotes().asLiveData()
    val allShopListNames: LiveData<List<ShopListNameItem>> = dao.getAllShoppingListNames().asLiveData()
    val libraryItems = MutableLiveData<List<LibraryItem>>()

    fun getAllItemsFromList(listId: Int): LiveData<List<ShopListItem>>{
        return dao.getAllShopListItems(listId).asLiveData()
    }

    fun getAllLibraryItems(name: String) = viewModelScope.launch {
        libraryItems.postValue(dao.getAllLibraryItems(name))
    }

    fun insertNote(note: NoteItem) = viewModelScope.launch {
        dao.insertNote(note)
    }

    fun insertShoppingListName(listName: ShopListNameItem) = viewModelScope.launch {
        dao.insertShopListName(listName)
    }

    fun insertShopItem(shopListItem: ShopListItem) = viewModelScope.launch {
        dao.insertItem(shopListItem)
        if (!(isLibraryItemExists(shopListItem.name))) dao.insertLibraryItem(LibraryItem(null, shopListItem.name))
    }

    fun updateListItem(item: ShopListItem) = viewModelScope.launch {
        dao.updateShopListItem(item)
    }

    fun updateNote(note: NoteItem) = viewModelScope.launch {
        dao.updateNote(note)
    }

    fun updateListName(listName: ShopListNameItem) = viewModelScope.launch {
        dao.updateShopListName(listName)
    }

    fun updateLibraryItem(item: LibraryItem) = viewModelScope.launch {
        dao.updateLibraryItem(item)
    }

    fun deleteNote(id: Int) = viewModelScope.launch {
        dao.deleteNote(id)
    }

    fun deleteLibraryItem(id: Int) = viewModelScope.launch {
        dao.deleteLibraryItem(id)
    }

    fun deleteShoppingList(id: Int, deleteList: Boolean) = viewModelScope.launch {
        if (deleteList) dao.deleteShoppingListName(id)
        dao.deleteShopListItemsByListId(id)
    }

    private suspend fun isLibraryItemExists(name: String): Boolean {
        return dao.getAllLibraryItems(name).isNotEmpty()
    }

    class MainViewModelFactory(val dataBase: MainDataBase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(dataBase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}