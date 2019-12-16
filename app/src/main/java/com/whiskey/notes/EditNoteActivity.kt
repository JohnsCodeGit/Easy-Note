package com.whiskey.notes

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.add_note.*
import kotlinx.android.synthetic.main.edit_note.*


class EditNoteActivity : AppCompatActivity() {
    var notes = ArrayList<String>()
    var titles = ArrayList<String>()
    lateinit var note: String
    lateinit var title: String
    var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_note)
        toolbar.setTitleTextColor(Color.BLACK)
        toolbar.setBackgroundColor(Color.parseColor("#a8f5ff"))

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        notes = intent.getStringArrayListExtra("notes")
        titles = intent.getStringArrayListExtra("titles")
        note = intent.getStringExtra("note")
        title = intent.getStringExtra("title")
        position = intent.getIntExtra("position",position)

        editTxtNote.setText(note)
        editTxtTitle.setText(title)


        Log.d("notePosition1", position.toString())

    }
    fun alertDialog(){
        val dialogBuilder = AlertDialog.Builder(this,R.style.MyDialogTheme)

        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to go back? \n\nThis note will be discarded.")

            .setCancelable(false)
            .setPositiveButton("Ok") {
                    dialog, id -> finish()
                val noteText = editTxtNote.text.toString()
                val noteTitle = editTxtTitle.text.toString()
                intent.putExtra("note", noteText)
                intent.putExtra("title", noteTitle)
                intent.putStringArrayListExtra("notes", notes)
                intent.putStringArrayListExtra("titles", titles)
                intent.putExtra("position", position)
                finishActivity(1)
            }
            .setNegativeButton("Cancel") {
                    dialog, id -> dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }
    override fun onBackPressed() {
        alertDialog()

    }
    override fun onSupportNavigateUp():Boolean{
        onBackPressed()
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.save) {
            //save function
            val intent = Intent(this, ViewNoteActivity::class.java)
            val noteText = editTxtNote.text.toString()
            val noteTitle = editTxtTitle.text.toString()
            intent.putExtra("note", noteText)
            intent.putExtra("title", noteTitle)
            intent.putStringArrayListExtra("notes", notes)
            intent.putStringArrayListExtra("titles", titles)
            intent.putExtra("position", position)
            Log.d("notePosition2", position.toString())
            if(noteText.isNotBlank() || noteTitle.isNotBlank()){
                Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show()
                ItemPosition.setEditMode(true)
            }


            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

}