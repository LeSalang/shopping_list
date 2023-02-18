package kz.lesa.shoppinglist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kz.lesa.shoppinglist.entities.LibraryItem
import kz.lesa.shoppinglist.entities.NoteItem
import kz.lesa.shoppinglist.entities.ShoppingListItem
import kz.lesa.shoppinglist.entities.ShoppingListNames

@Database (entities = [LibraryItem::class, NoteItem::class, ShoppingListItem::class, ShoppingListNames::class], version = 1)
abstract class MainDataBase : RoomDatabase() {
    abstract fun getDao() : Dao

    companion object {
        @Volatile
        private var INSTANCE: MainDataBase? = null
        fun getDataBase (context: Context): MainDataBase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDataBase::class.java,
                    "shopping_list.db"
                ).build()
                instance
            }
        }
    }
}