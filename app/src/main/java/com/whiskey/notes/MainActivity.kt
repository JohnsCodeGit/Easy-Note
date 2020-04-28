@file:Suppress(
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)

package com.whiskey.notes


import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var noteList = ArrayList<NoteModel>()
    private var searchItems = ArrayList<NoteModel>()
    private var groupItems = ArrayList<String>()
    private lateinit var barLay: ConstraintLayout
    private val notesDB = NotesDB(this, null)
    private val groupsDB = GroupsDB(this, null)
    private lateinit var noteItem: NoteModel

    private var selectedFragment: Fragment = HomeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        //toolbar.setBackgroundColor(ContextCompat.getColor(this, color.colorAccent))
//        window.statusBarColor = Color.parseColor("#13151a")

        barLay = findViewById(R.id.const_layout)
        val navView: BottomNavigationView = findViewById(R.id.bot_view)
        if (savedInstanceState == null) {
            Log.d("frag", "None")

            selectedFragment = HomeFragment()
            this.supportFragmentManager.beginTransaction()
                .replace(R.id.frag_container, selectedFragment, "Notes").commit()
        }
//        else {
//            val frag = intent.getStringExtra("frag")
//            Log.d("frag", frag)
//            selectedFragment = supportFragmentManager.findFragmentByTag(frag)!!
//            this.supportFragmentManager.beginTransaction()
//                .replace(R.id.frag_container, selectedFragment, frag).commit()
//        }
        navView.setOnNavigationItemSelectedListener{
            for (i in 0 until supportFragmentManager.backStackEntryCount){
                this.supportFragmentManager.popBackStack()

            }


            when(it.itemId){

                R.id.nav_home ->{
                    selectedFragment = HomeFragment()
                    toolbar.title = "Notes"
                    this.supportFragmentManager.beginTransaction()
                        .replace(R.id.frag_container, selectedFragment, "Notes").commit()

                }
                R.id.nav_fav ->{

                    selectedFragment = FavoriteFragment()
                    toolbar.title = "Favorites"
                    this.supportFragmentManager.beginTransaction()
                        .replace(R.id.frag_container, selectedFragment, "Favorites").commit()


                }
                R.id.nav_groups ->{

                    selectedFragment = GroupsFragment()
                    toolbar.title = "Groups"
                    this.supportFragmentManager.beginTransaction()
                        .replace(R.id.frag_container, selectedFragment, "Groups").commit()


                }
                R.id.nav_trash ->{

                    selectedFragment = TrashFragment()
                    toolbar.title = "Trash"
                    this.supportFragmentManager.beginTransaction()
                        .replace(R.id.frag_container, selectedFragment, "Trash").commit()


                }
            }
        true
        }


        val viewpager = ViewPager(this)
        viewpager.offscreenPageLimit = 0

        groupItems = groupsDB.getAllGroups()
        noteList = notesDB.getAllNote()
        searchItems = notesDB.getAllNote()

        var noteText = intent.getStringExtra("note")
        var titleText = intent.getStringExtra("title")
        var dateText = intent.getStringExtra("date")
        val group = intent.getStringExtra("group")
        val bool = intent.getIntExtra("bool", 0)

        if(((noteText != null && noteText.isNotBlank()) ||
                    (titleText != null)) && dateText != null) {

            noteText = noteText.toString()
            titleText = titleText.toString()
            dateText = dateText.toString()

            noteItem = NoteModel(noteText, titleText, dateText, group)

            val position: Int = intent.getIntExtra("position", -1)

            if(position == -1 && (noteText.isNotEmpty() || titleText.isNotEmpty())) {

                noteList.add(noteItem)
                searchItems.add(noteItem)

                notesDB.addNote(noteText, titleText.toString(), dateText, 0, noteList.size)
//                favDbHandler.addNote(noteText, titleText.toString(), dateText, noteList.size)


            } else if (position == -2 && (noteText.isNotEmpty() || titleText.isNotEmpty())) {

            } else if ((position != -1 || position != -2) && (noteText.isNotEmpty() || titleText.isNotEmpty())) {

                searchItems.removeAt(position)
                noteList.removeAt(position)
//                val groupName = notedbHandler.getGroupName(position + 1)
                notesDB.deleteItem(position + 1)

                noteList.add(noteItem)
                searchItems.add(noteItem)

                notesDB.addNote(
                    noteItem.note,
                    noteItem.title,
                    noteItem.date,
                    bool,
                    noteItem.group,
                    noteList.size
                )

//                notedbHandler.updateGroup(groupName, position + 1)

            }


        }

    }

    @SuppressLint("RestrictedApi")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //val id = item.itemId
//           if (id == R.id.search) {
              // fab.visibility = View.GONE
//           }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        val frag1 = supportFragmentManager.findFragmentByTag("Notes")
        val frag2 = supportFragmentManager.findFragmentByTag("Favorites")
        val frag3 = supportFragmentManager.findFragmentByTag("Trash")
        val frag4 = supportFragmentManager.findFragmentByTag("Groups")


        val home = HomeFragment()
        val fav = FavoriteFragment()
        val trash = TrashFragment()
        val group = GroupsFragment()

        if (frag1 != null)
            home.hideMenuItems(findViewById(R.id.LConst))
        if (frag2 != null)
            fav.hideDeleteMenu(findViewById(R.id.LConstR))
        if (frag3 != null)
            trash.HideDeleteMenu(findViewById(R.id.LConstT))
        if (frag4 != null)
            group.hideDeleteMenu(findViewById(R.id.LConstG))
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


