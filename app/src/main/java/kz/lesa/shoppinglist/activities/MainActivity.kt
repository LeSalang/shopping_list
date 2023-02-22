package kz.lesa.shoppinglist.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kz.lesa.shoppinglist.R
import kz.lesa.shoppinglist.databinding.ActivityMainBinding
import kz.lesa.shoppinglist.fragments.FragManager
import kz.lesa.shoppinglist.fragments.NoteFrag

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomNavListener()
    }

    private fun setBottomNavListener() {
        binding.bNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> {
                    Log.d("myLog", "settings")
                }
                R.id.notes -> {
                    FragManager.setFrag(NoteFrag.newInstance(), this)
                }
                R.id.shop_list -> {
                    Log.d("myLog", "shopping")
                }
                R.id.new_item -> {
                    FragManager.currentFrag?.onClickNew()
                }
            }
            true

        }
    }
}