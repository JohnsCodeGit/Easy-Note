@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.whiskey.notes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.whiskey.notes.com.whiskey.notes.NotesDbHelper
import com.whiskey.notes.com.whiskey.notes.TitlesDbHelper
import com.whiskey.notes.com.whiskey.notes.dateDbHelper
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.view_note.*
import java.util.*
import kotlin.collections.ArrayList

class ViewNoteActivity : AppCompatActivity() {
    var notes = ArrayList<String>()
    var titles = ArrayList<String>()
    var dates = ArrayList<String>()
    lateinit var note: String
    lateinit var title: String

    var position: Int = 0
    var menuVisible = false


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_note)
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("MM/dd/yyyy @ hh:mm aaa")
        val dateText = intent.getStringExtra("date")
        eDate.text = "Modified: $dateText"
        dates = intent.getStringArrayListExtra("dates")
        notes = intent.getStringArrayListExtra("notes")
        titles = intent.getStringArrayListExtra("titles")
        note = intent.getStringExtra("note")
        title = intent.getStringExtra("title")
        position = intent.getIntExtra("position", position)

        intent.putStringArrayListExtra("notes", notes)
        intent.putStringArrayListExtra("titles", titles)
        intent.putStringArrayListExtra("dates", dates)
            eTitle.hint = "Note Title"
            eTitle.setHintTextColor(Color.DKGRAY)
        toolbar.inflateMenu(R.menu.menu)

            eNote.hint = "Notes"
        window.statusBarColor = Color.parseColor("#111116")

        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setBackgroundColor(Color.parseColor("#111116"))
        setSupportActionBar(toolbar)
        toolbar.inflateMenu(R.menu.menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        eNote.clearFocus()
        eTitle.clearFocus()
        eTitle.setText(title)
        eNote.setText(note)
        rConst.setOnClickListener {
            eNote.hasFocus()
            eNote.isSelected = true
            eNote.isCursorVisible = true
            menuVisible = true
            eNote.requestFocus()
            invalidateOptionsMenu()
            showSoftKeyboard(this.findViewById(R.id.eNote))

        }
        eNote.setOnFocusChangeListener { v, hasFocus ->
            eNote.hasFocus()
            eNote.isSelected = true
            eNote.isCursorVisible = true
            menuVisible = true
            invalidateOptionsMenu()

        }
        eTitle.setOnFocusChangeListener { v, hasFocus ->
            eTitle.hasFocus()

            eTitle.isSelected = true
            menuVisible = true
            invalidateOptionsMenu()

        }


    }
    fun Share(title: String, note: String){
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "")
        var shareMessage = title + "\n\n" + note

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Share with..."))
    }
    fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun ResetView(){

        eTitle.clearFocus()
        eNote.clearFocus()
        menuVisible = false
        hideKeyboard()
        title = eTitle.text.toString()
        note = eNote.text.toString()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.save) {
            //save function
            val date = Calendar.getInstance().time
            val formatter = SimpleDateFormat("MM/dd/yyyy @ hh:mm aaa")
            val dateText = formatter.format(date).toString()
            val notedbHandler = NotesDbHelper(this, null)
            val titleDbHandler = TitlesDbHelper(this, null)
            val dateDbHandler = dateDbHelper(this, null)
            notedbHandler.updateNote(eNote.text.toString(), position + 1)
            dateDbHandler.updateNote(dateText, position + 1)
            titleDbHandler.updateNote(eTitle.text.toString(), position + 1)
            eDate.text = "Modified: $dateText"
            ResetView()
            return true
        }
        else if(id == R.id.share){

            Share(eTitle.text.toString(), eNote.text.toString())

        }

        return super.onOptionsItemSelected(item)

    }
    fun Activity.hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.findViewById<EditText>(R.id.eNote).getWindowToken(), 0)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onSupportNavigateUp():Boolean{
        onBackPressed()
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        val menuItem = menu?.findItem(R.id.save)

        menuItem?.isVisible = menuVisible
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBackPressed() {
        if(eTitle.isFocused || eNote.isFocused){
            ResetView()

        }else{
            val mainIntent = Intent(this, MainActivity::class.java)
            val noteText = note
            val noteTitle = title

            val date = Calendar.getInstance().time
            val formatter = SimpleDateFormat("MM/dd/yyyy @ hh:mm aaa")
            val dateText = formatter.format(date).toString()

            mainIntent.putExtra("note", noteText)
            mainIntent.putExtra("title", noteTitle)
            mainIntent.putStringArrayListExtra("notes", notes)
            mainIntent.putStringArrayListExtra("titles", titles)
            mainIntent.putStringArrayListExtra("dates", dates)
            mainIntent.putExtra("date", dateText)
            mainIntent.putExtra("position", position)

            startActivity(mainIntent)
        }

    }
}