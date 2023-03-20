package com.example.android.dicegameapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.dicegameapplication.util.DiceHelper
import com.example.android.dicegameapplication.viewModel.DiceViewModel

class GameActivity : AppCompatActivity() {
    private var reRollDiceArray =  mutableListOf(1,2,3,4,5) // as the default all the dices should keep
    private var userRemainingReRolls: Int = 0
    private lateinit var gameDifficulty: String
    private var popupWindow: PopupWindow? = null
    private lateinit var viewModel : DiceViewModel
    private lateinit var btnThrow: Button
    private lateinit var btnScore: Button
    private val textViewFinalWinningScore by lazy { findViewById<TextView>(R.id.textViewFinalWinningScore) }
    private val textViewUserRollFullScore by lazy { findViewById<TextView>(R.id.textViewUserScore) }
    private val textViewRobotRollFullScore by lazy { findViewById<TextView>(R.id.textViewRobotScore) }
    private val textViewUserFullScore by lazy { findViewById<TextView>(R.id.textViewUserFullScore) }
    private val textViewRobotFullScore by lazy { findViewById<TextView>(R.id.textViewRobotFullScore)}
    private val textViewUserGameWins by lazy { findViewById<TextView>(R.id.textViewUserGameWins)}
    private val textViewRobotGameWins by lazy { findViewById<TextView>(R.id.textViewRobotGameWins)}
    private val imageViewsForUser by lazy {
        arrayOf<ImageView> (
            findViewById(R.id.userDice1),
            findViewById(R.id.userDice2),
            findViewById(R.id.userDice3),
            findViewById(R.id.userDice4),
            findViewById(R.id.userDice5))
    }
    private val imageViewsForRobot by lazy {
        arrayOf<ImageView> (
            findViewById(R.id.compDice1),
            findViewById(R.id.compDice2),
            findViewById(R.id.compDice3),
            findViewById(R.id.compDice4),
            findViewById(R.id.compDice5))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // initialize the view model
        viewModel = ViewModelProvider(this).get(DiceViewModel::class.java)

        // get the values from the intents
        val userWins = intent.extras!!.getInt("userWins")
        val robotWins = intent.extras!!.getInt("robotWins")
        gameDifficulty = intent.extras!!.getString("gameDifficulty").toString()
        val finalWinningScore = intent.extras!!.getInt("finalWinningScore")

        // change the background image based on the game mode
        val imageViewGamePageBackground = findViewById<ImageView>(R.id.imageViewGamePageBackground)
        imageViewGamePageBackground.scaleType = ImageView.ScaleType.CENTER_CROP
        imageViewGamePageBackground.setImageResource(
            if (gameDifficulty.contains("easy")) R.drawable.easy_game_background else R.drawable.hard_game_background
        )

        // set the values in the viewModel
        viewModel.setGameDifficulty(gameDifficulty)
        viewModel.setGameWinningScore(finalWinningScore.toString())
        viewModel.setValue(userWins, robotWins)

        // saving the data getting from the intents
        // To subscribe to changes in a ViewModel
        // "it" is the new value when it's published from the viewModel
        viewModel.robotDiceArray.observe(this, Observer { updateDices(it,imageViewsForRobot) })
        viewModel.userDiceArray.observe(this, Observer { updateDices(it,imageViewsForUser) })
        viewModel.userCurrentRollFullScore.observe(this, Observer { textViewUserRollFullScore.text = it })
        viewModel.robotCurrentRollFullScore.observe(this, Observer { textViewRobotRollFullScore.text = it })
        viewModel.userFullScore.observe(this, Observer { textViewUserFullScore.text = it })
        viewModel.robotFullScore.observe(this, Observer { textViewRobotFullScore.text = it })
        viewModel.finalWinningScore.observe(this, Observer { textViewFinalWinningScore.text = it })
        viewModel.userWinnings.observe(this, Observer { textViewUserGameWins.text = it.toString() })
        viewModel.robotWinnings.observe(this, Observer { textViewRobotGameWins.text = it.toString() })
        viewModel.btnThrowText.observe(this, Observer { btnThrow.text = it })
        viewModel.userRemainingReRolls.observe(this, Observer { userRemainingReRolls = it })
        viewModel.reRollDiceArray.observe(this, Observer {reRollDiceArray = it })
        viewModel.gameDifficulty.observe(this, Observer { gameDifficulty = it })
        viewModel.btnScoreVisibility.observe(this, Observer { btnScore.visibility = it })
        viewModel.btnThrowVisibility.observe(this, Observer { btnThrow.visibility = it })

        // get the user selected dices to reRoll
        val userDice1 = findViewById<ImageView>(R.id.userDice1)
        updateSelectedDiceBackground(userDice1, 1)

        val userDice2 = findViewById<ImageView>(R.id.userDice2)
        updateSelectedDiceBackground(userDice2, 2)

        val userDice3 = findViewById<ImageView>(R.id.userDice3)
        updateSelectedDiceBackground(userDice3, 3)

        val userDice4 = findViewById<ImageView>(R.id.userDice4)
        updateSelectedDiceBackground(userDice4, 4)

        val userDice5 = findViewById<ImageView>(R.id.userDice5)
        updateSelectedDiceBackground(userDice5, 5)

        btnThrow = findViewById(R.id.btnThrow)
        btnScore = findViewById(R.id.btnScore)

        btnThrow.setOnClickListener {
            // avoid re throwing dice if the user hasn't scored previous scores
            if( viewModel.userAllowToThrow.value!!
                && reRollDiceArray.size == 5
                && viewModel.isTie.value!!.not() ) {
                viewModel.throwDices()
            }
            else if(viewModel.userAllowToReRoll.value!!) {
                viewModel.reRollDices(reRollDiceArray.toIntArray())
                restoreSelectedDices(imageViewsForUser)
                checkWin()
            }
            else if(viewModel.userAllowToThrow.value!!
                    && reRollDiceArray.size == 5
                    && viewModel.isTie.value!! ){
                viewModel.rollDiceWhenTie()
            }
        }

        btnScore.setOnClickListener {
            if(viewModel.userAllowToScore.value!!) {
                viewModel.scoreDicesValue()
                restoreSelectedDices(imageViewsForUser)
                btnThrow.text = getString(R.string.button_throw)
            }
            checkWin() // if someone has won this will trigger the winnerPopupWindow
        }

        // if the below expression returns a null value, set the value of CONFIG_CHANGE to false
        val configChange = savedInstanceState?.getBoolean(CONFIG_CHANGE) ?: false
        if(configChange){
            viewModel.reRollDiceArray.value?.let { updateSelectedDicesBackgrounds(it,imageViewsForUser) }
        }
    }

    /**
     * Check the winner and execute the winnerPopupWindow function
    */
    private fun checkWin() {
        if (viewModel.isWin.value!!) {
            if (viewModel.winner.value.equals("user")) {
                winnerPopUpWindow("You win!")
            } else if (viewModel.winner.value.equals("robot")) {
                winnerPopUpWindow("You lose")
            }
        }
    }

    /**
     * Update each dice when the user selected them to reRoll
    */
    private fun updateSelectedDiceBackground(userDice:ImageView, diceNumber:Int){
        userDice.setOnClickListener{
            if(viewModel.userAllowToSelectDices.value!!){
                viewModel.updateReRollDiceArray(userDice, diceNumber, reRollDiceArray)
            }
        }
    }

    /**
     * set for the default values to the reRoll
    */
    private fun restoreSelectedDices(imageViews: Array<ImageView>){
        for(i in imageViews.indices){
            imageViews[i].setBackgroundResource(0)
        }
        reRollDiceArray.clear()
        reRollDiceArray.addAll(listOf(1,2,3,4,5))
    }

    /**
        updates the dices in the UI
    */
    private fun updateDices(dice: IntArray, imageViews: Array<ImageView>) {
        // loop through image view objects and set the images one die at a time
        for (i in imageViews.indices) {
            // each time through the loop, create a variable "drawableId"
            // it wil contain a resource ID that's pointing to one of these six images
            val drawableId = when (dice[i]) {
                // look at the numeric value in that index position in the array return the respective drawableId
                1 -> R.drawable.die_face_1
                2 -> R.drawable.die_face_2
                3 -> R.drawable.die_face_3
                4 -> R.drawable.die_face_4
                5 -> R.drawable.die_face_5
                6 -> R.drawable.die_face_6
                else -> R.drawable.die_face_6
            }
            imageViews[i].setImageResource(drawableId)
        }
    }

    /**
     * update the selected dice when the screen rotate
     */
    private fun updateSelectedDicesBackgrounds(reRollDiceArray: MutableList<Int>, imageViews: Array<ImageView>){
        val userSelectedKeepDicesArray = DiceHelper.getUserSelectedDices(reRollDiceArray.toIntArray())
        for(i in userSelectedKeepDicesArray.indices){
            if (userSelectedKeepDicesArray[i] == 1) { imageViews[0].setBackgroundResource(R.drawable.image_border) }
            else if (userSelectedKeepDicesArray[i] == 2) { imageViews[1].setBackgroundResource(R.drawable.image_border) }
            else if (userSelectedKeepDicesArray[i] == 3) { imageViews[2].setBackgroundResource(R.drawable.image_border) }
            else if (userSelectedKeepDicesArray[i] == 4) { imageViews[3].setBackgroundResource(R.drawable.image_border) }
            else if (userSelectedKeepDicesArray[i] == 5) { imageViews[4].setBackgroundResource(R.drawable.image_border) }
            else {}
        }
    }

    /**
     *  Display the Results and the winner in the popup Window
     */
    private fun winnerPopUpWindow(winnerName: String?) {
        val inflater : LayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView : View = inflater.inflate(R.layout.winner_popup_layout, null)

        popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow!!.isOutsideTouchable = false
        popupWindow!!.isFocusable = true
        popupWindow!!.showAtLocation(findViewById<View?>(android.R.id.content).rootView, Gravity.CENTER, 0, 0)

        val winner = popupView.findViewById<TextView>(R.id.textViewWinner)
        val imageViewWinnerBannerPopup = popupView.findViewById<ImageView>(R.id.imageViewWinnerBannerPopup)
        val imageViewWinnerPopup = popupView.findViewById<ImageView>(R.id.imageViewWinnerPopup)
        winner.text = winnerName

        // change the UI based on the result
        if(winnerName!!.contains("win")){
            winner.setTextColor(Color.GREEN)
            imageViewWinnerBannerPopup.setImageResource(R.drawable.winner_banner)
            imageViewWinnerPopup.setImageResource(R.drawable.user_winner_image)
        }else {
            winner.setTextColor(Color.RED)
            imageViewWinnerBannerPopup.setImageResource(0)
            imageViewWinnerPopup.setImageResource(R.drawable.robot_winner_image)
        }

        // trigger the StartActivity when the user click the back button
        popupWindow!!.setOnDismissListener {
            intent = Intent(this, MainActivity::class.java)
            intent.action = Intent.ACTION_VIEW
            intent.putExtra("userWins", viewModel.userWinnings.value)
            intent.putExtra("robotWins", viewModel.robotWinnings.value)
            startActivity(intent)
        }
    }

    /**
     * save the InstanceState to update the selected dices when the config change happens
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(CONFIG_CHANGE, true)
        super.onSaveInstanceState(outState)
    }

    /**
     * Destroy the opened Popup Window
     */
    override fun onDestroy() {
        super.onDestroy()
        if (popupWindow != null && popupWindow?.isShowing == true) {
            popupWindow!!.dismiss()
        }
    }
}