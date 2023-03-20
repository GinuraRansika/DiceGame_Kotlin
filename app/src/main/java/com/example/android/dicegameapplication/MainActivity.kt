package com.example.android.dicegameapplication

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

/**
 * Demo Video Link : https://drive.google.com/file/d/1Bo9jeJSVekoUpjvCPrXfQK1lpbSZgKvJ/view?usp=sharing
 */
class MainActivity : AppCompatActivity() {
    private lateinit var btnNewGame: Button
    private lateinit var btnAbout: Button
    private var gameDifficulty: String = "easy"
    private var finalWinningScore = 101
    private var userWins = 0
    private var robotWins = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // buttons
        btnNewGame = findViewById(R.id.btnNewGame)
        btnAbout = findViewById(R.id.btnAbout)

        // get the passed intents and saved in the respective variables
        if(intent != null && intent.action == Intent.ACTION_VIEW) {
            userWins = intent.extras!!.getInt("userWins")
            robotWins = intent.extras!!.getInt("robotWins")
        }

        btnNewGame.setOnClickListener { createGameModePopUpWindow() }
        btnAbout.setOnClickListener { createAboutPopUpWindow() }
    }

    /**
     * Creates Popup Window for the Game Mode Setting Selection
     */
    private fun createGameModePopUpWindow(){
        val popupView : View = layoutInflater.inflate(R.layout.game_setting_popup_screen, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isFocusable = true

        val btnConfirmedSettings = popupView.findViewById<Button>(R.id.btnConfirmedSettings)
        val gameWinningScore = popupView.findViewById<EditText>(R.id.textInputGameWinningScore)

        // confirmed button clicked -> user's selected data go to the GameActivity with the intent
        btnConfirmedSettings.setOnClickListener{
            // check whether the user entered a value or not
            if(gameWinningScore.text.toString().trim() != "") {
                finalWinningScore = gameWinningScore.text.toString().toInt()
            }
            // send values to the next activity
            val intent = Intent(this, GameActivity::class.java)
            intent.action = Intent.ACTION_VIEW
            intent.putExtra("userWins", userWins)
            intent.putExtra("robotWins", robotWins)
            intent.putExtra("gameDifficulty", gameDifficulty)
            intent.putExtra("finalWinningScore", finalWinningScore)
            startActivity(intent)
            popupWindow.dismiss()
        }

        val btnEasy = popupView.findViewById<ImageView>(R.id.imageViewEasyMode)
        val btnHard = popupView.findViewById<ImageView>(R.id.imageViewHardMode)

        btnEasy.setOnClickListener{
            btnHard.setImageResource(R.drawable.hard_game_mode)
            btnEasy.setImageResource(R.drawable.easy_game_mode_clicked)
            gameDifficulty = "easy"
        }

        btnHard.setOnClickListener{
            btnHard.setImageResource(R.drawable.hard_game_mode_clicked)
            btnEasy.setImageResource(R.drawable.easy_game_mode)
            gameDifficulty = "hard"
        }
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0)
    }


    /**
     * Popup Window for the about button
     */
    private fun createAboutPopUpWindow()
    {
        val inflater : LayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView : View = inflater.inflate(R.layout.about_popup_layout, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true)
        popupWindow.isOutsideTouchable = false
        popupWindow.showAtLocation(findViewById<View?>(android.R.id.content).rootView, Gravity.BOTTOM, 0, 0)
        val btnAboutClose = popupView.findViewById<Button>(R.id.btnAboutClose)
        btnAboutClose.setOnClickListener{ popupWindow.dismiss() }
    }
}