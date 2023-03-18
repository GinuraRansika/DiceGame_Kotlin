package com.example.android.dicegameapplication

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var btnNewGame: Button
    private lateinit var btnAbout: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // buttons
        btnNewGame = findViewById(R.id.btnNewGame)
        btnAbout = findViewById(R.id.btnAbout)
        var userWins = 0
        var robotWins = 0

        if(intent != null && intent.action == Intent.ACTION_VIEW) {
            Toast.makeText(this, "Hai", Toast.LENGTH_LONG).show()
            userWins = intent.extras!!.getInt("userWins")
            robotWins = intent.extras!!.getInt("robotWins")
        }

        btnNewGame.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("userWins", userWins)
            intent.putExtra("robotWins", robotWins)
            startActivity(intent)
        }

        btnAbout.setOnClickListener {
            createPopUpWindow()
        }
    }

    private fun createPopUpWindow() {
        val inflater : LayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView : View = inflater.inflate(R.layout.popup_layout, null)
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        val focusable = true
        val popupWindow = PopupWindow(popupView, width, height,focusable)
        popupWindow.isOutsideTouchable = false
        popupWindow.showAtLocation(findViewById<View?>(android.R.id.content).rootView, Gravity.BOTTOM, 0, 0)
        val btnAboutClose = popupView.findViewById<Button>(R.id.btnAboutClose)
        btnAboutClose.setOnClickListener{ popupWindow.dismiss() }
    }
}