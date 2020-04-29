package com.whiskey.notes

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_home.*

class GroupsFragment : Fragment() {
    private var noteList = ArrayList<String>()
    private var searchItems = ArrayList<String>()
    private lateinit var noteadapter: GroupsAdapter
    private var groupsDB = GroupsDB(null, null)
    private lateinit var searchView: SearchView
    private val layoutM = LinearLayoutManager(activity)
    private lateinit var deleteButton: Button
    private lateinit var mView: View
    private lateinit var checkBox: CheckBox
    private lateinit var fab: FloatingActionButton
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var recyclerView: RecyclerView
    private var states = arrayOf(
        intArrayOf(android.R.attr.state_enabled),
        intArrayOf(-android.R.attr.state_enabled)
    )

    private var colors = intArrayOf(
        Color.BLACK,
        Color.BLACK
    )

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mView = view
        deleteButton = mView.findViewById(R.id.btnDelete)
        checkBox = mView.findViewById(R.id.radioButton)
        constraintLayout = mView.findViewById(R.id.constrainG)
        fab = mView.findViewById(R.id.fabButtonG)
        val textView = mView.findViewById<TextView>(R.id.textView8)
        searchItems.clear()
        if (groupsDB.getGroupSize() != 0.toLong()) {

            noteList = groupsDB.getAllGroups()

            searchItems = groupsDB.getAllGroups()

            textView.visibility = View.GONE

        } else
            textView.visibility = View.VISIBLE
        fab.setOnClickListener {

            val alertDialog = AlertDialog.Builder(this.context, R.style.AlertDialogStyle)

            val alertText = TextView(this.context)
            alertText.text = getString(R.string.new_group)
            alertText.gravity = Gravity.CENTER_HORIZONTAL
            alertText.textSize = 25.0F
            alertText.typeface = Typeface.DEFAULT_BOLD

            alertText.setTextColor(Color.BLACK)
//            alertText.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            alertDialog.setCustomTitle(alertText)
            val myList = ColorStateList(states, colors)

            val input: EditText =
                LayoutInflater.from(this.context).inflate(R.layout.dialog_content, null) as EditText
            input.setPadding(30, 180, 30, 30)
            input.inputType = InputType.TYPE_CLASS_TEXT
            input.setHintTextColor(Color.parseColor("#dee1e3"))
            input.backgroundTintList = myList
            input.setTextColor(Color.BLACK)

            input.hasFocus()
            input.hint = "Enter a group name..."

            alertDialog.setView(input)

                .setCancelable(true)
                .setPositiveButton("Add") { dialog, _ ->
                    addItem(input)
                    dialog.dismiss()
                    hideSoftKeyboard(activity!!)
                    input.clearFocus()
                }
                .setNegativeButton("Back") { dialog, _ ->
                    dialog.cancel()
                    input.clearFocus()
                    hideSoftKeyboard(this.activity!!)
                }
            val alert = alertDialog.create()
            alert.show()

            noteadapter.notifyDataSetChanged()
        }

        recyclerView = view.findViewById(R.id.recyclerView_group)

        recyclerView.apply {
            setBackgroundColor(Color.TRANSPARENT)
            layoutM.stackFromEnd = true
            layoutM.reverseLayout = true
            layoutManager = layoutM
            checkBox = activity?.findViewById(R.id.radioButton)!!
            noteadapter = GroupsAdapter(
                deleteButton, checkBox, constraintLayout, this.context,
                recyclerView, noteList, searchItems, textView, fab, this@GroupsFragment
            )
            adapter = noteadapter
            addItemDecoration(VerticalSpacing(25))

        }
    }

    private fun addItem(input: EditText) {
        noteList.add(input.text.toString())
        searchItems.add(input.text.toString())
        groupsDB.addGroup(input.text.toString(), noteList.size)
        noteadapter.notifyItemChanged(noteList.size - 1)

    }
    override fun onAttach(context: Context) {
        groupsDB = GroupsDB(activity, null)
        super.onAttach(context)
    }

    private fun hideSoftKeyboard(activity: FragmentActivity) {
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus?.windowToken,
            0
        )
    }

    @SuppressLint("RestrictedApi")
    fun hideDeleteMenu(view: View) {

        deleteButton = view.findViewById(R.id.btnDelete)
        checkBox = view.findViewById(R.id.radioButton)
        constraintLayout = view.findViewById(R.id.constrainG)
        recyclerView = view.findViewById(R.id.recyclerView_group)

        noteadapter = recyclerView.adapter as GroupsAdapter

        deleteButton.visibility = View.GONE
        deleteButton.isVisible = false

        checkBox.visibility = View.GONE
        checkBox.isVisible = false

        constraintLayout.visibility = View.GONE
        constraintLayout.isVisible = false

        checkBox.isChecked = false
        checkBox.isSelected = false

        noteadapter.hideItems()


    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        val manager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search in Groups..."
        searchView.setSearchableInfo(manager.getSearchableInfo(activity!!.componentName))
        searchView.isIconified = true
        searchView.setOnCloseListener {
            searchItems.clear()
            searchItems = groupsDB.getAllGroups()
            fabButton.visibility = View.VISIBLE

            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                return false

            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onQueryTextChange(newText: String?): Boolean {
                val text: String = newText.toString().trim()

                noteadapter.filter.filter(text)



                return true
            }

        })

        super.onCreateOptionsMenu(menu, menuInflater)
    }
    @SuppressLint("RestrictedApi")
    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        mView = inflater.inflate(R.layout.group_layout, container, false)

        return mView
    }
}