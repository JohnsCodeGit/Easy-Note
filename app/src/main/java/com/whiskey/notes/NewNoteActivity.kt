package com.whiskey.notes


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColor
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_note.*
import kotlinx.android.synthetic.main.note_row_item.*
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.add_note.toolbar as toolbar1

class NewNoteActivity : AppCompatActivity() {
    var notes = ArrayList<String>()
    var titles = ArrayList<String>()
    var dates = ArrayList<String>()


    fun alertDialog(){
        val dialogBuilder = AlertDialog.Builder(this,R.style.MyDialogTheme)

        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to go back? \n\nThis note will be discarded.")

            .setCancelable(false)
            .setPositiveButton("Ok") {
                    dialog, id -> finish()
                finishActivity(1)
            }
            .setNegativeButton("Cancel") {
                    dialog, id -> dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_note)
        notes = intent.getStringArrayListExtra("notes")
        titles = intent.getStringArrayListExtra("titles")
        dates = intent.getStringArrayListExtra("dates")
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setBackgroundColor(Color.parseColor("#000000"))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }
    override fun onSupportNavigateUp():Boolean{
        onBackPressed()
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.save) {

            //save function
            R.id.save.toColor()
            val intent = Intent(this, MainActivity::class.java)
            val noteText = eTxtNote.text.toString()
            val noteTitle = eTxtTitle.text.toString()
            intent.putExtra("note", noteText)
            intent.putExtra("title", noteTitle)
            intent.putStringArrayListExtra("notes", notes)
            intent.putStringArrayListExtra("titles", titles)
            intent.putStringArrayListExtra("dates", dates)

            val date = Calendar.getInstance().time
            val formatter = SimpleDateFormat("MM/dd/yyyy @ hh:mm aaa")
            val dateText = formatter.format(date).toString()

            intent.putExtra("date", dateText)
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    override fun onBackPressed() {
        alertDialog()

    }
}