@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.whiskey.notes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.whiskey.notes.com.whiskey.notes.NotesDbHelper

import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.view_note.*
import java.util.*

class ViewNoteActivity : AppCompatActivity() {

    private var date = Calendar.getInstance().time
    private val formatter = SimpleDateFormat("MM/dd/yyyy @ hh:mm aaa")
    private val dateText = formatter.format(date).toString()

    lateinit var note: String
    lateinit var title: String

    var position: Int = 0
    var menuVisible = false
    private val notedbHandler = NotesDbHelper(this, null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_note)

        val dateText = intent.getStringExtra("date")
        eDate.text = ("Modified: $dateText").toString()

        note = intent.getStringExtra("note")
        title = intent.getStringExtra("title")
        position = intent.getIntExtra("position", 0)

        eTitle.hint = "Note Title"
        eTitle.setHintTextColor(Color.DKGRAY)
        toolbar.inflateMenu(R.menu.menu)

            eNote.hint = "Notes"
        window.statusBarColor = Color.parseColor("#111116")

        toolbar.setTitleTextColor(Color.WHITE)

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
        eNote.setOnFocusChangeListener { _, hasFocus ->

            if(hasFocus) {
                eNote.hasFocus()
                eNote.isSelected = true
                eNote.isCursorVisible = true
                menuVisible = true
                invalidateOptionsMenu()
            }
        }
        eTitle.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                eTitle.hasFocus()

                eTitle.isSelected = true
                menuVisible = true
                invalidateOptionsMenu()
            }
        }

        bullet.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked){
                val lines: Array<String> = note.split("\n").toTypedArray()
                for (i in lines.indices){
//                    if(i == 0) {
//                        eNote.lines[i];
//                    } else if (TextUtils.isEmpty(lines[i].trim()) {
//                            eNote.setText(lines[i]);
//                        } else {
//                        pad.append("\n" + "\u2022" + "  " + lines[i]);
//                    }
                }

            }


        }


    }
    private fun share(title: String, note: String){
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "")
        var shareMessage = title + "\n\n" + note

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Share with..."))
    }
    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun resetView(){

        eTitle.clearFocus()
        eNote.clearFocus()
        menuVisible = false
        hideKeyboard()
        title = eTitle.text.toString()
        note = eNote.text.toString()
    }


    @SuppressLint("SetTextI18n")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId

        if (id == R.id.save) {
            //save function
            val date = Calendar.getInstance().time
            val formatter = SimpleDateFormat("MM/dd/yyyy @ hh:mm aaa")
            val dateText = formatter.format(date).toString()




            eDate.text = ("Modified: $dateText").toString()
            resetView()
            return true
        }
        else if(id == R.id.share){

            share(eTitle.text.toString(), eNote.text.toString())

        }

        return super.onOptionsItemSelected(item)

    }
    private fun Activity.hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.findViewById<EditText>(R.id.eNote).windowToken, 0)
    }
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

    override fun onBackPressed() {
        if(eTitle.isFocused || eNote.isFocused){
            resetView()

        }else{
            val mainIntent = Intent(this, MainActivity::class.java)
            save()
            mainIntent.putExtra("note", note)
            mainIntent.putExtra("title", title)

            mainIntent.putExtra("date", dateText)
            mainIntent.putExtra("position", position)

            startActivity(mainIntent)
        }

    }
    private fun save(){
        date = Calendar.getInstance().time

        notedbHandler.updateNote(eNote.text.toString(), eTitle.text.toString(),
            dateText, position + 1)
    }
}