package kz.lesa.shoppinglist.activities

import android.app.Application
import kz.lesa.shoppinglist.db.MainDataBase

class MainApp : Application() {
    val database by lazy {
        MainDataBase.getDataBase(this)
    }
}