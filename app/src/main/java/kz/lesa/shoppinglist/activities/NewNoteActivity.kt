package kz.lesa.shoppinglist.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import kz.lesa.shoppinglist.R
import kz.lesa.shoppinglist.databinding.ActivityNewNoteBinding
import kz.lesa.shoppinglist.entities.NoteItem
import kz.lesa.shoppinglist.fragments.NoteFrag
import kz.lesa.shoppinglist.utils.HtmlManager
import kz.lesa.shoppinglist.utils.MyTouchListener
import kz.lesa.shoppinglist.utils.TimeManager
import java.text.SimpleDateFormat
import java.util.*

class NewNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewNoteBinding
    private lateinit var defPref: SharedPreferences
    private var note: NoteItem? = null
    private var pref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)

        setContentView(binding.root)
        actionBarSettings()
        getNote()
        init()
        onClickColorPicker()
        actionMenuCallback()
        setTextSize()
    }

    private fun getSelectedTheme() : Int {
        return when (defPref.getString("theme_key", "blue")) {
            "blue" -> R.style.Theme_ShoppingListIndigoRed
            "green" -> R.style.Theme_ShoppingListGreenOrange
            else -> R.style.Theme_ShoppingListPurpleYellow
        }
    }

    private fun onClickColorPicker() = with(binding) {
        btnRed.setOnClickListener {
            setColorForSelectedText(R.color.picker_red)
        }
        btnOrange.setOnClickListener {
            setColorForSelectedText(R.color.picker_orange)
        }
        btnYellow.setOnClickListener {
            setColorForSelectedText(R.color.picker_yellow)
        }
        btnGreen.setOnClickListener {
            setColorForSelectedText(R.color.picker_green)
        }
        btnBlue.setOnClickListener {
            setColorForSelectedText(R.color.picker_blue)
        }
        btnPurple.setOnClickListener {
            setColorForSelectedText(R.color.picker_purple)
        }
        btnBlack.setOnClickListener {
            setColorForSelectedText(R.color.black)
        }
        btnWhite.setOnClickListener {
            setColorForSelectedText(R.color.white)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        binding.layoutColorPicker.setOnTouchListener(MyTouchListener())
        pref = PreferenceManager.getDefaultSharedPreferences(this)
    }

    private fun getNote() {
        val sNote = intent.getSerializableExtra(NoteFrag.NEW_NOTE_KEY)
        if (sNote != null) {
            note = intent.getSerializableExtra(NoteFrag.NEW_NOTE_KEY) as NoteItem
            fillNote()
        }
    }

    private fun fillNote() = with(binding) {
        edTitle.setText(note?.title)
        edDescription.setText(HtmlManager.getFromHtml(note?.content!!).trim())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                setMainResult()
            }
            android.R.id.home -> {
                finish()
            }
            R.id.bold -> {
                setBoldForSelectedText()
            }
            R.id.colorPicker -> {
                if (binding.layoutColorPicker.isShown) {
                    closeColorPicker()
                } else {
                    openColorPicker()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBoldForSelectedText() = with(binding) {
        val startPosition = edDescription.selectionStart
        val endPosition = edDescription.selectionEnd
        val styles = edDescription.text.getSpans(startPosition, endPosition, StyleSpan::class.java)
        var boldStyle: StyleSpan? = null
        if (styles.isNotEmpty()) {
            edDescription.text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.BOLD)
        }
        edDescription.text.setSpan(boldStyle, startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        edDescription.text.trim()
        edDescription.setSelection(startPosition)
    }

    private fun setColorForSelectedText(colorId: Int) = with(binding) {
        val startPosition = edDescription.selectionStart
        val endPosition = edDescription.selectionEnd
        val styles = edDescription.text.getSpans(startPosition, endPosition, ForegroundColorSpan::class.java)
        if (styles.isNotEmpty()) edDescription.text.removeSpan(styles[0])

        edDescription.text.setSpan(
            ForegroundColorSpan(ContextCompat.
            getColor(this@NewNoteActivity, colorId)),
            startPosition, endPosition,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        edDescription.text.trim()
        edDescription.setSelection(startPosition)
    }

    private fun setMainResult() {
        var editState = "new"
        val tempNote: NoteItem? = if (note == null) {
            createNewNote()
        } else {
            editState = "update"
            updateNote()
        }
        val i = Intent().apply {
            putExtra(NoteFrag.NEW_NOTE_KEY, tempNote)
            putExtra(NoteFrag.EDIT_STATE_KEY, editState)
        }
        setResult(RESULT_OK, i)
        finish()
    }

    private fun updateNote(): NoteItem? = with(binding) {
        return note?.copy(
            title = edTitle.text.toString(),
            content = HtmlManager.toHtml(edDescription.text)
        )
    }

    private fun createNewNote(): NoteItem {
        return NoteItem(
            null,
            binding.edTitle.text.toString(),
            HtmlManager.toHtml(binding.edDescription.text),
            TimeManager.getCurrentTime(),
            ""
        )
    }

    private fun actionBarSettings(){
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openColorPicker() {
        binding.layoutColorPicker.visibility = View.VISIBLE
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.open_color_picker)
        binding.layoutColorPicker.startAnimation(openAnim)
    }

    private fun closeColorPicker() {
        val closeAnim = AnimationUtils.loadAnimation(this, R.anim.close_color_picker)
        closeAnim.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }
            override fun onAnimationEnd(animation: Animation?) {
                binding.layoutColorPicker.visibility = View.GONE
            }
            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        binding.layoutColorPicker.startAnimation(closeAnim)
    }

    private fun actionMenuCallback() {
        val actionCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }
            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return true
            }
            override fun onDestroyActionMode(mode: ActionMode?) {
            }
        }
        binding.edDescription.customSelectionActionModeCallback = actionCallback
    }

    private fun setTextSize() = with(binding) {
        edTitle.setTextSize(pref?.getString("title_size_key", "16"))
        edDescription.setTextSize(pref?.getString("content_size_key", "12"))
    }

    private fun EditText.setTextSize(size: String?) {
        if (size != null) this.textSize = size.toFloat()
    }
}