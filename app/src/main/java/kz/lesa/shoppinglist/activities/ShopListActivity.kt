package kz.lesa.shoppinglist.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.TokenWatcher
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import kz.lesa.shoppinglist.R
import kz.lesa.shoppinglist.databinding.ActivityShopListBinding
import kz.lesa.shoppinglist.db.MainViewModel
import kz.lesa.shoppinglist.db.ShopListItemAdapter
import kz.lesa.shoppinglist.dialogs.EditeListItemDialog
import kz.lesa.shoppinglist.entities.LibraryItem
import kz.lesa.shoppinglist.entities.ShopListItem
import kz.lesa.shoppinglist.entities.ShopListNameItem
import kz.lesa.shoppinglist.utils.ShareHelper

class ShopListActivity : AppCompatActivity(), ShopListItemAdapter.Listener {
    private lateinit var binding: ActivityShopListBinding
    private lateinit var defPref: SharedPreferences
    private var shopListNameItem: ShopListNameItem? = null
    private lateinit var saveItem: MenuItem
    private var edItem: EditText? = null
    private var adapter: ShopListItemAdapter? = null
    private lateinit var textWatcher: TextWatcher

    private fun getSelectedTheme() : Int {
        return when (defPref.getString("theme_key", "blue")) {
            "blue" -> R.style.Theme_ShoppingListIndigoRed
            "green" -> R.style.Theme_ShoppingListGreenOrange
            else -> R.style.Theme_ShoppingListPurpleYellow
        }
    }


    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((applicationContext as MainApp).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        binding = ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initRcView()
        listItemObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_list_menu, menu)
        saveItem = menu?.findItem(R.id.save_item)!!
        val newItem = menu.findItem(R.id.add_item)
        edItem = newItem.actionView?.findViewById(R.id.edNewShopItem) as EditText
        newItem.setOnActionExpandListener(expandActionView())
        saveItem.isVisible = false
        textWatcher = textWatcher()
        return true
    }

    private fun textWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("MyLog", "On text changed: $s")
                mainViewModel.getAllLibraryItems("%$s%")
            }

            override fun afterTextChanged(s: Editable?) {
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_item -> addNewShopItem(edItem?.text.toString())
            R.id.delete_list -> {
                mainViewModel.deleteShoppingList(shopListNameItem?.id!!, true)
                finish()
            }
            R.id.clear_list -> mainViewModel.deleteShoppingList(shopListNameItem?.id!!, false)
            R.id.share_list -> {
                startActivity(Intent.createChooser(ShareHelper.shareShopList(adapter?.currentList!!, shopListNameItem?.name!!), "Share by"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNewShopItem(name: String) {
        if (name.isEmpty()) return
        val item = ShopListItem(
            null,
            name,
            "",
            false,
            shopListNameItem?.id!!,
            0
        )
        edItem?.setText("")
        mainViewModel.insertShopItem(item)
    }

    private fun listItemObserver() {
        mainViewModel.getAllItemsFromList(shopListNameItem?.id!!).observe(this) {
            adapter?.submitList(it)
            binding.tvEmpty.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun libraryItemObserver() {
        mainViewModel.libraryItems.observe(this) {
            val tempShopList = ArrayList<ShopListItem>()
            it.forEach { item ->
                val shopItem = ShopListItem(
                    item.id,
                    item.name,
                    "",
                    false,
                    0,
                    1
                )
                tempShopList.add(shopItem)
            }
            adapter?.submitList(tempShopList)
            binding.tvEmpty.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun initRcView() = with(binding) {
        adapter = ShopListItemAdapter(this@ShopListActivity)
        rcView.layoutManager = LinearLayoutManager(this@ShopListActivity)
        rcView.adapter = adapter
    }

    private fun expandActionView(): OnActionExpandListener{
        return object : OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                saveItem.isVisible = true
                edItem?.addTextChangedListener(textWatcher)
                libraryItemObserver()
                mainViewModel.getAllItemsFromList(shopListNameItem?.id!!).removeObservers(this@ShopListActivity)
                mainViewModel.getAllLibraryItems("%%")
                return true
            }
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                saveItem.isVisible = false
                edItem?.removeTextChangedListener(textWatcher)
                invalidateOptionsMenu()
                mainViewModel.libraryItems.removeObservers(this@ShopListActivity)
                edItem?.setText("")
                listItemObserver()
                return true
            }
        }
    }

    private fun init() {
        shopListNameItem = intent.getSerializableExtra(SHOP_LIST_NAME) as ShopListNameItem
    }

    companion object {
        const val SHOP_LIST_NAME = "shop_list_name"
    }

    override fun onClickItem(shopListItem: ShopListItem, state: Int) {
        when(state) {
            ShopListItemAdapter.CHECK_BOX -> mainViewModel.updateListItem(shopListItem)
            ShopListItemAdapter.EDIT -> editeListItem(shopListItem)
            ShopListItemAdapter.EDIT_LIBRARY_ITEM -> editeLibraryItem(shopListItem)
            ShopListItemAdapter.DELETE_LIBRARY_ITEM -> {
                mainViewModel.deleteLibraryItem(shopListItem.id!!)
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%")
            }
            ShopListItemAdapter.ADD -> addNewShopItem(shopListItem.name)
        }
    }

    private fun editeListItem(item: ShopListItem) {
        EditeListItemDialog.showDialog(this, item, object : EditeListItemDialog.Listener {
            override fun onClick(item: ShopListItem) {
                mainViewModel.updateListItem(item)
            }

        })
    }

    private fun editeLibraryItem(item: ShopListItem) {
        EditeListItemDialog.showDialog(this, item, object : EditeListItemDialog.Listener {
            override fun onClick(item: ShopListItem) {
                mainViewModel.updateLibraryItem(LibraryItem(item.id, item.name))
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%")
            }

        })
    }

    private fun saveItemCount() {
        var checkedItemCounter = 0
        adapter?.currentList?.forEach {
            if(it.itemChecked) checkedItemCounter++
        }
        val tempShopListItem = shopListNameItem?.copy(
            allItemCounter = adapter?.itemCount!!,
            checkItemsCounter = checkedItemCounter
        )
        mainViewModel.updateListName(tempShopListItem!!)
    }

    override fun onBackPressed() {
        saveItemCount()
        super.onBackPressed()
    }
}