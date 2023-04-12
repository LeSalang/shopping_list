package kz.lesa.shoppinglist.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.PreferenceManager
import kz.lesa.shoppinglist.R

class SettingsActivity : AppCompatActivity() {
    private lateinit var defPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.placeHolder, SettingsFrag()).commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getSelectedTheme() : Int {
        return when (defPref.getString("theme_key", "blue")) {
            "blue" -> R.style.Theme_ShoppingListIndigoRed
            "green" -> R.style.Theme_ShoppingListGreenOrange
            else -> R.style.Theme_ShoppingListPurpleYellow
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}