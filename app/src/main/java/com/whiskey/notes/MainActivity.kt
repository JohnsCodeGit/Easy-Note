package com.whiskey.notes


import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.whiskey.notes.R.color
import com.whiskey.notes.com.whiskey.notes.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.fabButton


class MainActivity : AppCompatActivity() {

    private var noteList = ArrayList<NoteModel>()
    private var searchItems = ArrayList<NoteModel>()
    private lateinit var barLay: ConstraintLayout
    private lateinit  var noteadapter: NoteAdapter
   private val notedbHandler = NotesDbHelper(this, null)
//    private lateinit var searchView: SearchView
//    private val layoutM = LinearLayoutManager(this)
//    private lateinit var fabs: FloatingActionButton
    private lateinit var noteItem: NoteModel

    var selectedFragment: Fragment = HomeFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setBackgroundColor(ContextCompat.getColor(this, color.dark))
        window.statusBarColor = ContextCompat.getColor(this, color.dark)
        barLay = findViewById(R.id.const_layout)
        val navView: BottomNavigationView = findViewById(R.id.bot_view)
        if(savedInstanceState == null){
            this.supportFragmentManager.beginTransaction()
                .replace(R.id.frag_container, selectedFragment).commit()

        }
        navView.setOnNavigationItemSelectedListener{
            for (i in 0 until supportFragmentManager.backStackEntryCount){
                this.supportFragmentManager.popBackStack()

            }


            when(it.itemId){

                R.id.nav_home ->{
                    selectedFragment = HomeFragment()
                }
                R.id.nav_fav ->{

                    selectedFragment = FavoriteFragment()
                }
                R.id.nav_groups ->{

                    selectedFragment = HomeFragment()
                }
                R.id.nav_trash ->{

                    selectedFragment = TrashFragment()
                }
            }
            this.supportFragmentManager.beginTransaction()
                .replace(R.id.frag_container, selectedFragment).commit()
        true
        }


        val viewpager = ViewPager(this)
        viewpager.offscreenPageLimit = 0

        noteList = notedbHandler.getAllNote()

        searchItems = notedbHandler.getAllNote()
        val noteText = intent.getStringExtra("note")
        val titleText = intent.getStringExtra("title")
        val dateText = intent.getStringExtra("date")
        if(((noteText != null && noteText.isNotBlank()) ||
                    (titleText != null)) && dateText != null) {
            Log.d("NOTETEXT", noteText)
            noteItem = NoteModel(noteText.toString(), titleText.toString(), dateText)

            val position: Int = intent.getIntExtra("position", -1)

            Log.d("notePosition", position.toString())
            if(position == -1 && (noteText!!.isNotEmpty() || titleText!!.isNotEmpty())) {
                //


                noteList.add(noteItem)
                searchItems.add(noteItem)

                notedbHandler.addNote(noteText, titleText.toString(), dateText, noteList.size)

                Log.d("itemAddedNoteItem", noteItem.toString())


            }
            else if(position != -1 && (noteText!!.isNotEmpty() || titleText!!.isNotEmpty())){

                searchItems.removeAt(position)
                noteList.removeAt(position)
                notedbHandler.deleteItem(position+1)


                noteList.add(noteItem)
                searchItems.add(noteItem)
                notedbHandler.addNote(noteItem.note, noteItem.title, noteItem.date, noteList.size)


            }


        }

    }




    @SuppressLint("RestrictedApi")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
           if (id == R.id.search) {
              // fab.visibility = View.GONE
           }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {

        val home = HomeFragment()
        home.HideDeleteMenu(findViewById(R.id.LConst))
    }



}
class VerticalSpacing(height: Int) : RecyclerView.ItemDecoration(){

    private var spaceHeight: Int = height

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = spaceHeight

    }





}


