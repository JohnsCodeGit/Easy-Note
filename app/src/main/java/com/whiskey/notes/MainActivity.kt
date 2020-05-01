@file:Suppress(
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)

package com.whiskey.notes


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var noteList = ArrayList<NoteModel>()
    private var groupItems = ArrayList<String>()
    private lateinit var barLay: ConstraintLayout
    private val notesDB = NotesDB(this, null)
    private val groupsDB = GroupsDB(this, null)
    private lateinit var noteItem: NoteModel
    private lateinit var selectedFragment: Fragment

    private fun updateRecyclerView(data: Intent?){

        var noteText = data?.getStringExtra("note")
        var titleText = data?.getStringExtra("title")
        var dateText = data?.getStringExtra("date")
        var group = data?.getStringExtra("group")
        var bool = data?.getIntExtra("bool", 0)

        //Log.d("noteText", noteText)
        noteText = noteText.toString()
        titleText = titleText.toString()
        dateText = dateText.toString()
        group = group.toString()
        bool = bool!!.toInt()

        noteItem = NoteModel(noteText, titleText, dateText, group)

        val position: Int = data!!.getIntExtra("position", -1)
        noteList = notesDB.getAllNote()

        if(position == -1 && (noteText.isNotEmpty() || titleText.isNotEmpty())) {
            noteList.add(noteItem)
            Log.d("position23", position.toString())
            notesDB.addNote(noteText, titleText.toString(), dateText, 0, noteList.size)

        } else if (position == -2 && (noteText.isNotEmpty() || titleText.isNotEmpty())) {

        } else if ((position != -1 || position != -2) && (noteText.isNotEmpty() || titleText.isNotEmpty())) {

            noteList.removeAt(position)
            notesDB.deleteItem(position + 1)
            Log.d("position22", position.toString())
            noteList.add(noteItem)

            notesDB.addNote(
                noteItem.note,
                noteItem.title,
                noteItem.date,
                bool,
                noteItem.group,
                noteList.size
            )
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("check11", requestCode.toString())
            when (requestCode) {
                1 -> {
                    supportFragmentManager.inTransaction {
                        remove(selectedFragment)
                        selectedFragment = FavoriteFragment()
                        add(R.id.frag_container,selectedFragment, "Favorites")
                    }
                    updateRecyclerView(data)
                    Log.d("check1", "Fav")
                }
                2 -> {
                    supportFragmentManager.inTransaction {
                        remove(selectedFragment)
                        selectedFragment = HomeFragment()
                        add(R.id.frag_container,selectedFragment, "Notes")
                    }
                    updateRecyclerView(data)
                    Log.d("check1", "Note")


                }
                3 -> {
                    supportFragmentManager.inTransaction {
                        remove(selectedFragment)
                        selectedFragment = GroupsFragment()
                        add(R.id.frag_container,selectedFragment, "Groups")
                    }
                    Log.d("check1", "Group")


                }
            }


    }
    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)

        barLay = findViewById(R.id.const_layout)
        val navView: BottomNavigationView = findViewById(R.id.bot_view)

        if (savedInstanceState == null){
            Log.d("check119", "Null")
            selectedFragment = HomeFragment()
            supportFragmentManager.inTransaction {
                add(R.id.frag_container, selectedFragment)
            }
        }

        navView.setOnNavigationItemSelectedListener{

            when(it.itemId){

                R.id.nav_home ->{


                    toolbar.title = "Notes"
                    supportFragmentManager.inTransaction {
                        remove(selectedFragment)
                        selectedFragment = HomeFragment()
                        add(R.id.frag_container, selectedFragment, "Notes")
                    }


                }
                R.id.nav_fav ->{


                    toolbar.title = "Favorites"
                    supportFragmentManager.inTransaction {
                        remove(selectedFragment)
                        selectedFragment = FavoriteFragment()
                        add(R.id.frag_container, selectedFragment, "Favorites")
                    }

                }
                R.id.nav_groups ->{

                    toolbar.title = "Groups"
                    supportFragmentManager.inTransaction {
                        remove(selectedFragment)
                        selectedFragment = GroupsFragment()
                        add(R.id.frag_container, selectedFragment, "Groups")
                    }


                }
                R.id.nav_trash ->{

                    toolbar.title = "Trash"
                    supportFragmentManager.inTransaction {
                        remove(selectedFragment)
                        selectedFragment = TrashFragment()
                        add(R.id.frag_container, selectedFragment, "Trash")
                    }


                }
            }
        true
        }

        val viewpager = ViewPager(this)
        viewpager.offscreenPageLimit = 3

        groupItems = groupsDB.getAllGroups()
        noteList = notesDB.getAllNote()
    }

    @SuppressLint("RestrictedApi")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


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


