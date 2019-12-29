package com.whiskey.notes


import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import com.whiskey.notes.com.whiskey.notes.NoteModel
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.add_note.*
import kotlinx.android.synthetic.main.view_note.*
import java.util.*
import kotlin.collections.ArrayList


class NewNoteActivity : AppCompatActivity() {

    private fun alertDialog(){
        val dialogBuilder = AlertDialog.Builder(this,R.style.MyDialogTheme)

        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to go back? \n\nThis note will be discarded.")

            .setCancelable(false)
            .setPositiveButton("Ok") {
                    dialog, id -> finish()
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

        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        window.statusBarColor = Color.parseColor("#111116")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)



    }
    override fun onSupportNavigateUp():Boolean{
        onBackPressed()
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId

        if (id == R.id.save) {

            //save function
            //R.id.save.toColor()
            val intent = Intent(this, MainActivity::class.java)
            val noteText = eTxtNote.text.toString()
            val noteTitle = eTxtTitle.text.toString()
            intent.putExtra("note", noteText)
            intent.putExtra("title", noteTitle)

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
        this.alertDialog()

    }
}