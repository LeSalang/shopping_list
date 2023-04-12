package kz.lesa.shoppinglist.db

import android.content.Context
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.lesa.shoppinglist.R
import kz.lesa.shoppinglist.databinding.ListNameItemBinding
import kz.lesa.shoppinglist.databinding.ShopLibraryListItemBinding
import kz.lesa.shoppinglist.databinding.ShopListItemBinding
import kz.lesa.shoppinglist.entities.ShopListItem
import kz.lesa.shoppinglist.entities.ShopListNameItem

class ShopListItemAdapter(private val listener: Listener) : ListAdapter<ShopListItem, ShopListItemAdapter.ItemHolder>(ItemComparator()) {

    class ItemHolder (val view: View) : RecyclerView.ViewHolder(view) {

        fun setItemData(shopListItem: ShopListItem, listener: Listener) {
            val binding = ShopListItemBinding.bind(view)
            binding.apply {
                tvName.text = shopListItem.name
                tvInfo.text = shopListItem.itemInfo
                tvInfo.visibility = infoVisibility(shopListItem)
                checkBox.isChecked = shopListItem.itemChecked
                setPaintFlagAndColor(binding)
                checkBox.setOnClickListener {
                    listener.onClickItem(shopListItem.copy(itemChecked = checkBox.isChecked), CHECK_BOX)
                }
                btnEdit.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT)
                }
            }
        }

        fun setLibraryData(shopListItem: ShopListItem, listener: Listener) {
            val binding = ShopLibraryListItemBinding.bind(view)
            binding.apply {
                tvName.text = shopListItem.name
                btnEdit.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT_LIBRARY_ITEM)
                }
                btnDelete.setOnClickListener {
                    listener.onClickItem(shopListItem, DELETE_LIBRARY_ITEM)
                }
                itemView.setOnClickListener {
                    listener.onClickItem(shopListItem, ADD)
                }
            }
        }

       /* private fun getColorFromAttribute(context: Context, attribute: Int): Int {
            val typedValue = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(attribute, typedValue, true)
            return typedValue.data
        }  */

        private fun getColorFromAttribute(context: Context, attribute: Int): Int {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(attribute, typedValue, true)
            return ContextCompat.getColor(context, typedValue.resourceId)
        }

        private fun setPaintFlagAndColor(binding: ShopListItemBinding) {
            binding.apply {
                if (checkBox.isChecked) {
                    tvName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvName.setTextColor(getColorFromAttribute(root.context, com.google.android.material.R.attr.colorSecondary))
                    tvInfo.setTextColor(getColorFromAttribute(root.context, com.google.android.material.R.attr.colorSecondary))
                } else {
                    tvName.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvInfo.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvName.setTextColor(getColorFromAttribute(root.context, com.google.android.material.R.attr.colorPrimary))
                    tvInfo.setTextColor(getColorFromAttribute(root.context, com.google.android.material.R.attr.colorPrimary))
                }
            }
        }
/*
        private fun setPaintFlagAndColor(binding: ShopListItemBinding) {
            val colorSecondary = getColorFromAttribute(binding.root.context, com.google.android.material.R.attr.colorSecondary)
            val colorPrimary = getColorFromAttribute(binding.root.context, com.google.android.material.R.attr.colorPrimary)
            binding.apply {
                if (checkBox.isChecked) {
                    tvName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvName.setTextColor(ContextCompat.getColor(root.context, R.color.myBlue))
                    tvInfo.setTextColor(ContextCompat.getColor(root.context, R.color.myBlue))
                } else {
                    tvName.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvInfo.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvName.setTextColor(ContextCompat.getColor(root.context, R.color.myBlue))
                    tvInfo.setTextColor(ContextCompat.getColor(root.context, R.color.myBlue))
                }
            }
        }
*/

        private fun infoVisibility(shopListItem: ShopListItem) : Int {
            return if (shopListItem.itemInfo.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        companion object {
            fun createShopItem(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context).
                    inflate(R.layout.shop_list_item, parent, false))
            }
            fun createLibraryItem(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context).
                    inflate(R.layout.shop_library_list_item, parent, false))
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<ShopListItem>() {
        override fun areItemsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return if (viewType == 0)
            ItemHolder.createShopItem(parent)
        else
            ItemHolder.createLibraryItem(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        if (getItem(position).itemType==0)
            holder.setItemData(getItem(position), listener)
        else
            holder.setLibraryData(getItem(position), listener)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType
    }
    interface Listener {
        fun onClickItem(shopListItem: ShopListItem, state: Int)
    }

    companion object {
        const val EDIT = 0
        const val CHECK_BOX = 1
        const val EDIT_LIBRARY_ITEM = 2
        const val DELETE_LIBRARY_ITEM = 3
        const val ADD = 4
    }
}