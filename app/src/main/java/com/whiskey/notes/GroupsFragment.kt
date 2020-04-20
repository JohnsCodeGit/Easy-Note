package com.whiskey.notes

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mView = view
        deleteButton = mView.findViewById(R.id.btnDelete)
        checkBox = mView.findViewById(R.id.radioButton)
        constraintLayout = mView.findViewById(R.id.constrainG)
        fab = mView.findViewById(R.id.fabButtonG)
        val textView = mView.findViewById<TextView>(R.id.textView8)
        searchItems.clear()

        fab.setOnClickListener {

            val builder = AlertDialog.Builder(this.context)
            builder.setMessage("")

            val input = AppCompatEditText(this.context)
            input.setPadding(60, 0, 60, 0)
            input.inputType = InputType.TYPE_CLASS_TEXT
            input.setHintTextColor(Color.LTGRAY)
            input.setTextColor(Color.BLACK)
//            input.setBackgroundColor(Color.BLACK)
//            input.setTextColor(Color.WHITE)
//            val colorStateList = ColorStateList.valueOf(Color.BLACK)
//            ViewCompat.setBackgroundTintList(input, colorStateList)
            input.hasFocus()
            //showKeyboard(mView.findViewById<ConstraintLayout>(R.id.constrainG).context)
            input.hint = "Enter a group name..."

            builder.setView(input)

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
            val alert = builder.create()
            alert.show()


        }

        if (groupsDB.getGroupSize() != 0.toLong()) {

            noteList = groupsDB.getAllGroups()

            searchItems = groupsDB.getAllGroups()

        }

        if (noteList.size != 0)
            textView.visibility = View.GONE
        else {
            textView.visibility = View.VISIBLE
            Log.d("visibility", true.toString())
        }
        Log.d("favList", noteList.toString())

        recyclerView = view.findViewById(R.id.recyclerView_group)

        recyclerView.apply {
            setBackgroundColor(Color.TRANSPARENT)
            layoutM.stackFromEnd = true
            layoutM.reverseLayout = true
            layoutManager = layoutM
            checkBox = activity?.findViewById(R.id.radioButton)!!
            noteadapter = GroupsAdapter(
                deleteButton, checkBox, constraintLayout, this.context,
                recyclerView, noteList, searchItems, textView, fab
            )
            adapter = noteadapter
            addItemDecoration(VerticalSpacing(25))

        }
    }

    private fun addItem(input: EditText) {
        noteList.add(input.text.toString())
        searchItems.add(input.text.toString())
        groupsDB.addGroup(input.text.toString(), noteList.size)
        noteadapter.notifyDataSetChanged()

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

    private fun showKeyboard(context: Context) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
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
        Log.d("visible?", checkBox.isVisible.toString())

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