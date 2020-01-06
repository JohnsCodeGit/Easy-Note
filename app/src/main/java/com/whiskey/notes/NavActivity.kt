//package com.whiskey.notes
//
//import android.annotation.SuppressLint
//import android.app.SearchManager
//import android.content.Context
//import android.content.Intent
//import android.graphics.Color
//import android.graphics.Rect
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import androidx.navigation.ui.AppBarConfiguration
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import android.view.Menu
//import android.view.View
//import androidx.annotation.RequiresApi
//import androidx.appcompat.widget.SearchView
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.whiskey.notes.com.whiskey.notes.NoteModel
//import com.whiskey.notes.com.whiskey.notes.NotesDbHelper
//import kotlinx.android.synthetic.main.fragment_home.*
//
//class NavActivity : AppCompatActivity(){
//
//    private lateinit var appBarConfiguration: AppBarConfiguration
//    private var noteList = ArrayList<NoteModel>()
//    private var searchItems = ArrayList<NoteModel>()
//
//    private lateinit  var noteadapter: NoteAdapter
//    private val notedbHandler = NotesDbHelper(this, null)
//    private lateinit var searchView: SearchView
//    private val layoutM = LinearLayoutManager(this)
//    private lateinit var fabs: FloatingActionButton
//    private lateinit var noteItem: NoteModel
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//
//        outState?.putParcelableArrayList("noteList", noteList)
//
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//
//        noteList = savedInstanceState?.getParcelableArrayList<NoteModel>("noteList")  as ArrayList<NoteModel>
//
//
//    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        actionBar?.setDisplayHomeAsUpEnabled(true)
//        window.statusBarColor = Color.parseColor("#111116")
//        fabButton.setOnClickListener { val intent = Intent(this, NewNoteActivity::class.java)
//
//            intent.putParcelableArrayListExtra("noteList", noteList)
//            startActivity(intent)
//        }
//        fabs = fabButton
//
//        searchItems.clear()
//
//        if(notedbHandler.getNoteSize() != 0.toLong() ) {
//
//            noteList = notedbHandler.getAllNote()
//
//            searchItems = notedbHandler.getAllNote()
//
//
//
//        }
//
//        val noteText = intent.getStringExtra("note")
//        val titleText = intent.getStringExtra("title")
//        val dateText = intent.getStringExtra("date")
//
//        if((noteText != null || titleText != null) && dateText != null) {
//            Log.d("NOTETEXT", noteText)
//            noteItem = NoteModel(noteText, titleText, dateText)
//
//            val position: Int = intent.getIntExtra("position", -1)
//
//            Log.d("notePosition", position.toString())
//            if(position == -1 && (noteText.isNotEmpty() || titleText.isNotEmpty())) {
//                //
//
//
//                noteList.add(noteItem)
//                searchItems.add(noteItem)
//
//                //notedbHandler.addNote(noteText, titleText, dateText, noteList.size)
//
//                Log.d("itemAddedNoteItem", noteItem.toString())
//
//
//            }
//            else if(position != -1 && (noteText.isNotEmpty() || titleText.isNotEmpty())){
//
//                searchItems.removeAt(position)
//                noteList.removeAt(position)
//                notedbHandler.deleteItem(position+1)
//
//
//                noteList.add(noteItem)
//                searchItems.add(noteItem)
//               // notedbHandler.addNote(noteItem.note, noteItem.title, noteItem.date, noteList.size)
//
//
//            }
//
//        }
//        actionBar?.elevation = 0.toFloat()
//        val delete = btnDelete
//        val deleteAll = radioButton
//        recyclerView_main.apply {
//            setBackgroundColor(Color.TRANSPARENT)
//            layoutM.stackFromEnd = true
//            layoutM.reverseLayout = true
//            layoutManager = layoutM
//            val buttonLayout: ConstraintLayout = constrain
//            adapter  = NoteAdapter(delete, deleteAll, buttonLayout, fabButton, this.context,
//                notedbHandler, recyclerView_main, noteList, searchItems)
//            noteadapter = adapter as NoteAdapter
//            addItemDecoration(VerticalSpacing(50))
//
//        }
////        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
////        val navView: NavigationView = findViewById(R.id.nav_view)
////        val navController = findNavController(R.id.nav_host_fragment)
////        // Passing each menu ID as a set of Ids because each
////        // menu should be considered as top level destinations.
////        appBarConfiguration = AppBarConfiguration(
////            setOf(
////                R.id.nav_home, R.id.nav_starred, R.id.nav_trash,
////                R.id.nav_groups
////            ), drawerLayout
////        )
////        setupActionBarWithNavController(navController, appBarConfiguration)
////        navView.setupWithNavController(navController)
////
////        navView.setNavigationItemSelectedListener {
////
////            when (it.itemId){
////
////                R.id.nav_home ->  {
////                    val intent = Intent(this, NavActivity::class.java)
////                    startActivity(intent)
////                    true
////                }
////                R.id.nav_starred -> {
//////                   findNavController(R.id.nav_host_fragment).navigate(R.id.nav_starred)
////                    val intent = Intent(this, StarredActivity::class.java)
////                    startActivity(intent)
////                    true
////                }
////                R.id.nav_trash -> {
////
////                    true
////                }
////                R.id.nav_groups -> {
////
////                    true
////                }
////                else ->{true}
////
////
////            }
////            drawerLayout.closeDrawer(START)
////            true
////
////        }
//    }
//
//    @SuppressLint("RestrictedApi")
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main_menu, menu)
//        val searchItem = menu.findItem(R.id.search)
//        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        searchView = searchItem.actionView as SearchView
//        searchView.queryHint = "Search in Notes..."
//        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
//        searchView.setOnCloseListener {
//            searchItems.clear()
//            searchItems = notedbHandler.getAllNote()
//            fabButton.visibility = View.VISIBLE
//
//            true
//        }
//
//        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
//
//            override fun onQueryTextSubmit(query: String?): Boolean {
//
//                return false
//
//            }
//
//            @RequiresApi(Build.VERSION_CODES.N)
//            override fun onQueryTextChange(newText: String?): Boolean {
//                var text: String = newText.toString().trim()
//
//                noteadapter.filter.filter(text)
//
//
//
//                return true
//            }
//
//        })
//
//        return true
//    }
//    @SuppressLint("RestrictedApi")
//    override fun onBackPressed() {
//        btnDelete.visibility = View.GONE
//        radioButton.visibility = View.GONE
//        constrain.visibility = View.GONE
//        radioButton.isChecked = false
//        radioButton.isSelected = false
//        fabButton.visibility = View.VISIBLE
//
//        noteadapter.hideItems()
//        noteadapter.notifyDataSetChanged()
//
//
//    }
//
//    class VerticalSpacing(height: Int) : RecyclerView.ItemDecoration() {
//
//        private var spaceHeight: Int = height
//
//        override fun getItemOffsets(
//            outRect: Rect,
//            view: View,
//            parent: RecyclerView,
//            state: RecyclerView.State
//        ) {
//            outRect.bottom = spaceHeight
//
//        }
//    }
//
//
////    override fun onSupportNavigateUp(): Boolean {
////        val navController = findNavController(R.id.nav_host_fragment)
////        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
////    }
//
////    override fun onFragmentInteraction(uri: Uri) {
////
////    }
//}
