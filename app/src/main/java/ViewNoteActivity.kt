@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.whiskey.notes

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.whiskey.notes.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.add_note.*
import kotlinx.android.synthetic.main.edit_note.*
import kotlinx.android.synthetic.main.view_note.*

class ViewNoteActivity : AppCompatActivity() {
    var notes = ArrayList<String>()
    var titles = ArrayList<String>()
    lateinit var note: String
    lateinit var title: String
    var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_note)

        notes = intent.getStringArrayListExtra("notes")
        titles = intent.getStringArrayListExtra("titles")
        note = intent.getStringExtra("note")
        title = intent.getStringExtra("title")
        position = intent.getIntExtra("position",position)

        intent.putStringArrayListExtra("notes", notes)
        intent.putStringArrayListExtra("titles", titles)


        toolbar.setTitleTextColor(Color.BLACK)
        toolbar.setBackgroundColor(Color.parseColor("#a8f5ff"))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setTitle(title)

        eNote.setText(note)

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.edit) {
            //save function
            val editIntent = Intent(this, EditNoteActivity::class.java)

            editIntent.putExtra("note", note)
            editIntent.putExtra("title", title)
            editIntent.putStringArrayListExtra("notes", notes)
            editIntent.putStringArrayListExtra("titles", titles)
            editIntent.putExtra("position", position)
            Log.d("notePosition2", position.toString())



            startActivity(editIntent)
            return true
        }

        return super.onOptionsItemSelected(item)

    }
    override fun onSupportNavigateUp():Boolean{
        onBackPressed()
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }
    override fun onBackPressed() {
        val mainIntent = Intent(this, MainActivity::class.java)
        val noteText = note
        val noteTitle = title
        mainIntent.putExtra("note", noteText)
        mainIntent.putExtra("title", noteTitle)
        mainIntent.putStringArrayListExtra("notes", notes)
        mainIntent.putStringArrayListExtra("titles", titles)
        mainIntent.putExtra("position", position)

        startActivity(mainIntent)

    }
}