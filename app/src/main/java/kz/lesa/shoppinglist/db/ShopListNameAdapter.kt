package kz.lesa.shoppinglist.db

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.lesa.shoppinglist.R
import kz.lesa.shoppinglist.databinding.ListNameItemBinding
import kz.lesa.shoppinglist.entities.ShopListNameItem

class ShopListNameAdapter(private val listener: Listener) : ListAdapter<ShopListNameItem, ShopListNameAdapter.ItemHolder>(ItemComparator()) {

    class ItemHolder (view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ListNameItemBinding.bind(view)

        fun setData(shopListNameItem: ShopListNameItem, listener: Listener) = with(binding) {
            tvListName.text = shopListNameItem.name
            tvTime.text = shopListNameItem.time
            itemView.setOnClickListener {
                listener.onClickItem(shopListNameItem)
            }
            btnDeleteList.setOnClickListener {
                listener.deleteItem(shopListNameItem.id!!)
            }
            btnEditList.setOnClickListener {
                listener.editItem(shopListNameItem)
            }
            val counterText = "${shopListNameItem.checkItemsCounter}/${shopListNameItem.allItemCounter}"
            tvCounter.text = counterText
            progressBar.max = shopListNameItem.allItemCounter
            progressBar.progress = shopListNameItem.checkItemsCounter
            val colorState = ColorStateList.valueOf(getProgressColorState(shopListNameItem, binding.root.context))
            if (shopListNameItem.checkItemsCounter==shopListNameItem.allItemCounter) tvCounter.setTextColor(colorState)
           // progressBar.progressTintList = colorState
        }

        private fun getProgressColorState(item: ShopListNameItem, context: Context): Int {
            val typedValue = TypedValue()
            val typedValue2 = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValue, true)
            theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValue2, true)

            return if(item.checkItemsCounter == item.allItemCounter) {
                typedValue2.data
            } else {
               ContextCompat.getColor(context, androidx.appcompat.R.color.secondary_text_default_material_dark)
                //typedValue.data
            }
            /*return if(item.checkItemsCounter == item.allItemCounter) {
                val typedValue = TypedValue()
                val theme = context.theme
                theme.resolveAttribute(android.R.attr.colorSecondary, typedValue, true)
                val colorData = typedValue.data
                ContextCompat.getColor(context, colorData)
            }
            else {
                ContextCompat.getColor(context, R.color.myRed)
            }*/
        }

        companion object {
            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context).
                    inflate(R.layout.list_name_item, parent, false))
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<ShopListNameItem>() {
        override fun areItemsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position), listener)
    }

    interface Listener {
        fun deleteItem(id: Int)
        fun editItem(shopListName: ShopListNameItem)
        fun onClickItem(shopListNameItem: ShopListNameItem)
    }
}