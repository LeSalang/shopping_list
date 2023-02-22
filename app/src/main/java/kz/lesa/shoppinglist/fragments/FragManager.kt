package kz.lesa.shoppinglist.fragments

import androidx.appcompat.app.AppCompatActivity
import kz.lesa.shoppinglist.R

object FragManager {
    var currentFrag: BaseFrag? = null

    fun setFrag(newFrag: BaseFrag, activity: AppCompatActivity) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.placeHolder, newFrag)
        transaction.commit()
        currentFrag = newFrag
    }
}