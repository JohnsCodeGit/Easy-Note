package com.whiskey.notes


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.add_note.*
import java.util.*


class NewNoteActivity : AppCompatActivity() {
    lateinit var mAdView: AdView

    private fun alertDialog(){
        val dialogBuilder = AlertDialog.Builder(this,R.style.MyDialogTheme)

        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to go back? \n\nThis note will be discarded.")

            .setCancelable(false)
            .setPositiveButton("Ok") {
                    _, _ -> finish()
            }
            .setNegativeButton("Cancel") {
                    dialog, _ -> dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_note)

        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
//        window.statusBarColor = Color.parseColor("#13151a")
        MobileAds.initialize(this)
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
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
            val noteText = eNote.text.toString()
            val noteTitle = eTitle.text.toString()
            intent.putExtra("note", noteText)
            intent.putExtra("title", noteTitle)

            val date = Calendar.getInstance().time
            val formatter = SimpleDateFormat("MM/dd/yyyy, hh:mm aaa")
            val dateText = formatter.format(date).toString()
            intent.putExtra("group", "")
            intent.putExtra("date", dateText)
            setResult(Activity.RESULT_OK,intent)
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    override fun onBackPressed() {
        this.alertDialog()

    }
}