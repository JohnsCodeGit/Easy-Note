package com.whiskey.notes.com.whiskey.notes

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.renderscript.Sampler
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColor
import com.whiskey.notes.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.theme_layout.*

class ThemeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.theme_layout)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.BLACK)
        toolbar.setBackgroundColor(ContextCompat.getColor(this.applicationContext, R.color.colorAccent))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        var background: Color = cord.solidColor.toColor()
        if(cord.background == R.color.Background.toDrawable()){
            darkSwitch.isChecked = true
            lightSwitch.isChecked = false
        }
        else {
            lightSwitch.isChecked = true
            darkSwitch.isChecked = false
        }

    }
}