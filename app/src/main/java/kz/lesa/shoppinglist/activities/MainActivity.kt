package kz.lesa.shoppinglist.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import kz.lesa.shoppinglist.R
import kz.lesa.shoppinglist.databinding.ActivityMainBinding
import kz.lesa.shoppinglist.dialogs.NewListDialog
import kz.lesa.shoppinglist.fragments.FragManager
import kz.lesa.shoppinglist.fragments.HelloFrag
import kz.lesa.shoppinglist.fragments.NoteFrag
import kz.lesa.shoppinglist.fragments.ShopListFrag
import kz.lesa.shoppinglist.settings.SettingsActivity

class MainActivity : AppCompatActivity(), NewListDialog.Listener {
    lateinit var binding: ActivityMainBinding
    private lateinit var defPref: SharedPreferences
    private var currentMenuItemId = R.id.shop_list
    private var currentTheme = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        currentTheme = defPref.getString("theme_key", "blue").toString()
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomNavListener()
        FragManager.setFrag(ShopListFrag.newInstance(), this)
    }

    private fun setBottomNavListener() {
        binding.bNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> {
                    startActivity(Intent(this, SettingsActivity :: class.java))
                }
                R.id.notes -> {
                    currentMenuItemId = R.id.notes
                    FragManager.setFrag(NoteFrag.newInstance(), this)
                }
                R.id.shop_list -> {
                    currentMenuItemId = R.id.shop_list
                    FragManager.setFrag(ShopListFrag.newInstance(), this)
                }
                R.id.new_item -> {
                    FragManager.currentFrag?.onClickNew()
                }
                else -> {
                    FragManager.setFrag(ShopListFrag.newInstance(), this)
                }
            }
            true

        }
    }

    override fun onResume() {
        super.onResume()
        binding.bNav.selectedItemId = currentMenuItemId
        if (defPref.getString("theme_key", "blue") != currentTheme) recreate()
    }

    private fun getSelectedTheme() : Int {
        return when (defPref.getString("theme_key", "blue")) {
            "blue" -> R.style.Theme_ShoppingListIndigoRed
            "green" -> R.style.Theme_ShoppingListGreenOrange
            else -> R.style.Theme_ShoppingListPurpleYellow
        }
    }


    override fun onClick(name: String) {
        Log.d("MyLog", "Name $name")
    }
}