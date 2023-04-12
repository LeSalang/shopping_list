package kz.lesa.shoppinglist.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import kz.lesa.shoppinglist.R

class SettingsFrag : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferens, rootKey)
    }
}