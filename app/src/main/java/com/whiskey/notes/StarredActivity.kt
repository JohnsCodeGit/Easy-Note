package com.whiskey.notes.com.whiskey.notes

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.whiskey.notes.*
//import com.whiskey.notes.StarFragment.OnFragmentInteractionListener
import kotlinx.android.synthetic.main.activity_main.*
//import kotlinx.android.synthetic.main.star_layout.*

class StarredActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var noteList = ArrayList<NoteModel>()
    private var searchItems = ArrayList<NoteModel>()

    private lateinit  var noteadapter: DeleteAdapter
    private val notedbHandler = NotesDbHelper(this, null)
    private lateinit var searchView: SearchView
    private val layoutM = LinearLayoutManager(this)
    private lateinit var fabs: FloatingActionButton
    private lateinit var noteItem: NoteModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.star_layout_main)
       // val toolbar: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = Color.parseColor("#111116")
        searchItems.clear()

        if(notedbHandler.getNoteSize() != 0.toLong() ) {

            noteList = notedbHandler.getAllNote()

            searchItems = notedbHandler.getAllNote()

        }

        val noteText = intent.getStringExtra("note")
        val titleText = intent.getStringExtra("title")
        val dateText = intent.getStringExtra("date")

        if((noteText != null || titleText != null) && dateText != null) {
            Log.d("NOTETEXT", noteText)
            noteItem = NoteModel(noteText, titleText, dateText)

            val position: Int = intent.getIntExtra("position", -1)

            Log.d("notePosition", position.toString())
            if(position == -1 && (noteText.isNotEmpty() || titleText.isNotEmpty())) {
                //


                noteList.add(noteItem)
                searchItems.add(noteItem)

                notedbHandler.addNote(noteText, titleText, dateText, noteList.size)

                Log.d("itemAddedNoteItem", noteItem.toString())


            }
            else if(position != -1 && (noteText.isNotEmpty() || titleText.isNotEmpty())){

                searchItems.removeAt(position)
                noteList.removeAt(position)
                notedbHandler.deleteItem(position+1)


                noteList.add(noteItem)
                searchItems.add(noteItem)
                notedbHandler.addNote(noteItem.note, noteItem.title, noteItem.date, noteList.size)


            }

        }
//        actionBar?.elevation = 0.toFloat()
//        val delete = butnDelete
//        val deleteAll = radButton
//        recyclerView_delete.apply {
//            setBackgroundColor(Color.TRANSPARENT)
//            layoutM.stackFromEnd = true
//            layoutM.reverseLayout = true
//            layoutManager = layoutM
//            val buttonLayout: ConstraintLayout = constraint
//            adapter  = DeleteAdapter(delete, deleteAll, buttonLayout, this.context,
//                notedbHandler, recyclerView_delete, noteList, searchItems)
//            noteadapter = adapter as DeleteAdapter
//            addItemDecoration(NavActivity.VerticalSpacing(50))
//
//        }
//        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
//        val navView: NavigationView = findViewById(R.id.nav_view)
//        val navController = findNavController(R.id.nav_host_fragment)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.nav_home, R.id.nav_starred, R.id.nav_trash,
//                R.id.nav_groups
//            ), drawerLayout
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
//
//        navView.setNavigationItemSelectedListener {
//
//            when (it.itemId){
//
//                R.id.nav_home ->  {
//                    val intent = Intent(this, NavActivity::class.java)
//                    startActivity(intent)
//                    true
//                }
//                R.id.nav_starred -> {
//                    val intentStar = Intent(this, StarredActivity::class.java)
//                    startActivity(intentStar)
//                    true
//                }
//                R.id.nav_trash -> {
//
//                    true
//                }
//                R.id.nav_groups -> {
//
//                    true
//                }
//                else ->{true}
//
//
//            }
//
//
//        }
    }
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }
    override fun onBackPressed() {
        super.onBackPressed()
    }
//
//    override fun onFragmentInteraction(uri: Uri) {
//
//    }


}