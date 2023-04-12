package kz.lesa.shoppinglist.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kz.lesa.shoppinglist.activities.MainApp
import kz.lesa.shoppinglist.activities.ShopListActivity
import kz.lesa.shoppinglist.databinding.FragShopListBinding
import kz.lesa.shoppinglist.db.MainViewModel
import kz.lesa.shoppinglist.db.ShopListNameAdapter
import kz.lesa.shoppinglist.dialogs.DeleteDialog
import kz.lesa.shoppinglist.dialogs.NewListDialog
import kz.lesa.shoppinglist.entities.ShopListNameItem
import kz.lesa.shoppinglist.utils.TimeManager

class ShopListFrag : BaseFrag(), ShopListNameAdapter.Listener {
    private lateinit var binding: FragShopListBinding
    private lateinit var adapter: ShopListNameAdapter

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onClickNew() {
        NewListDialog.showDialog(activity as AppCompatActivity, object : NewListDialog.Listener {
            override fun onClick(name: String) {
                val shopListNameItem = ShopListNameItem(
                    null,
                    name,
                    TimeManager.getCurrentTime(),
                    0,
                    0,
                    ""
                )
                mainViewModel.insertShoppingListName(shopListNameItem)
            }
        }, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragShopListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
    }

    private fun initRcView() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = ShopListNameAdapter(this@ShopListFrag)
        rcView.adapter = adapter
    }

    private fun observer() {
        mainViewModel.allShopListNames.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = ShopListFrag()
    }

    override fun deleteItem(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                mainViewModel.deleteShoppingList(id, true)
            }

        })
    }

    override fun editItem(shopListName: ShopListNameItem) {
        NewListDialog.showDialog(activity as AppCompatActivity, object : NewListDialog.Listener {
            override fun onClick(name: String) {

                mainViewModel.updateListName(shopListName.copy(name = name))
            }
        }, shopListName.name)
    }

    override fun onClickItem(shopListNameItem: ShopListNameItem) {
        val i = Intent(activity, ShopListActivity::class.java).apply {
            putExtra(ShopListActivity.SHOP_LIST_NAME, shopListNameItem)
        }
        startActivity(i)
    }
}