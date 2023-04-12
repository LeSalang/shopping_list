package kz.lesa.shoppinglist.utils

import android.content.Intent
import kz.lesa.shoppinglist.entities.ShopListItem

object ShareHelper {
    fun shareShopList(shopList: List<ShopListItem>, listName: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plane"
        intent.apply {
            putExtra(Intent.EXTRA_TEXT, makeShareText(shopList, listName))
        }
        return intent
    }

    private fun makeShareText(shopList: List<ShopListItem>, listName: String): String {
        val sBuilder = StringBuilder()
        sBuilder.append("(*°▽°*)  $listName  (*°▽°*)")
        sBuilder.append("\n")
        var counter = 0
        shopList.forEach {
            if (it.itemInfo.isNullOrEmpty()) {
                sBuilder.append("${++counter} - ${it.name}")
            } else {
                sBuilder.append("${++counter} - ${it.name} (${it.itemInfo})")
            }
            if (it.itemChecked) {
                sBuilder.append(" ✓")
            }
            sBuilder.append("\n")
        }
        return sBuilder.toString()
    }
}