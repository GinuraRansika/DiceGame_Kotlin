package com.example.android.dicegameapplication

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.setViewTreeOnBackPressedDispatcherOwner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.dicegameapplication.viewModel.DiceViewModel

class GameActivity : AppCompatActivity() {
    private lateinit var viewModel : DiceViewModel
    private lateinit var btnThrow: Button
    private lateinit var btnScore: Button
    private lateinit var btnSetGameWinningScore: Button
    private lateinit var btnThrowText: String
    private lateinit var linearLayoutEnterTargetScore: LinearLayout
    private var reRollDiceArray = mutableListOf<Int>()
    private var userRemainingRolls: Int = 0
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
    private val textInputGameWinningScore by lazy { findViewById<EditText>(R.id.textInputGameWinningScore) }
    private val textViewFinalWinningScore by lazy { findViewById<TextView>(R.id.textViewFinalWinningScore) }
    private val textViewUserRollFullScore by lazy { findViewById<TextView>(R.id.textViewUserScore) }
    private val textViewRobotRollFullScore by lazy { findViewById<TextView>(R.id.textViewRobotScore) }
    private val textViewUserFullScore by lazy { findViewById<TextView>(R.id.textViewUserFullScore) }
    private val textViewRobotFullScore by lazy { findViewById<TextView>(R.id.textViewRobotFullScore)}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        linearLayoutEnterTargetScore = findViewById(R.id.linearLayoutEnterTargetScore)

        // initialize the view model
        viewModel = ViewModelProvider(this).get(DiceViewModel::class.java)
        // To subscribe to changes in a ViewModel
        // "it" is the new value when it's published from the viewModel
        viewModel.robotDiceArray.observe(this, Observer { updateDisplay(it,imageViewsForRobot) })
        viewModel.userDiceArray.observe(this, Observer { updateDisplay(it,imageViewsForUser) })
        viewModel.userCurrentRollFullScore.observe(this, Observer { textViewUserRollFullScore.text = it })
        viewModel.robotCurrentRollFullScore.observe(this, Observer { textViewRobotRollFullScore.text = it })
        viewModel.userFullScore.observe(this, Observer { textViewUserFullScore.text = it })
        viewModel.robotFullScore.observe(this, Observer { textViewRobotFullScore.text = it })
        viewModel.gameWinningScore.observe(this, Observer { textInputGameWinningScore.setText(it.toString()) })
        viewModel.finalWinningScore.observe(this, Observer { textViewFinalWinningScore.text = it })
        viewModel.userRemainingReRolls.observe(this, Observer { userRemainingRolls = it })
        viewModel.btnThrowText.observe(this, Observer { btnThrow.text = it })
        viewModel.reRollDiceArray.observe(this, Observer { reRollDiceArray = it })


        val userDice1 = findViewById<ImageView>(R.id.userDice1)
        userDice1.setOnClickListener{
            if(viewModel.userAllowToSelectDices.value!!){
                viewModel.updateReRollDiceArray(userDice1, 1, reRollDiceArray)
            }
        }

        val userDice2 = findViewById<ImageView>(R.id.userDice2)
        userDice2.setOnClickListener{
            if(viewModel.userAllowToSelectDices.value!!){
                viewModel.updateReRollDiceArray(userDice2, 2, reRollDiceArray)
            }
        }

        val userDice3 = findViewById<ImageView>(R.id.userDice3)
        userDice3.setOnClickListener{
            if(viewModel.userAllowToSelectDices.value!!){
                viewModel.updateReRollDiceArray(userDice3, 3, reRollDiceArray)
            }
        }

        val userDice4 = findViewById<ImageView>(R.id.userDice4)
        userDice4.setOnClickListener{
            if(viewModel.userAllowToSelectDices.value!!){
                viewModel.updateReRollDiceArray(userDice4, 4, reRollDiceArray)
            }
        }

        val userDice5 = findViewById<ImageView>(R.id.userDice5)
        userDice5.setOnClickListener{
            if(viewModel.userAllowToSelectDices.value!!){
                viewModel.updateReRollDiceArray(userDice5, 5, reRollDiceArray)
            }
        }


        btnThrow = findViewById(R.id.btnThrow)
        btnThrow.setOnClickListener {
            // avoid re throwing dice if the user hasn't scored previous scores
            if(viewModel.userAllowToThrow.value!! && reRollDiceArray.size == 0 ) {
                viewModel.rollDice()
                removeWinningScoreEditor()
            }
            else if(viewModel.userAllowToReRoll.value!!) {
                viewModel.reRollDice(reRollDiceArray.toIntArray())
                updateSelectedDices(imageViewsForUser)
            }
        }

        removeWinningScoreEditor()

        btnSetGameWinningScore = findViewById(R.id.btnSetGameWinningScore)
        btnSetGameWinningScore.setOnClickListener {
            if(viewModel.allowToChangeWinningScore.value!!) {
                viewModel.setGameWinningScore(textInputGameWinningScore.text.toString())
                textInputGameWinningScore.clearFocus()
            }
        }

        btnScore = findViewById(R.id.btnScore)
        btnScore.setOnClickListener {
            if(viewModel.userAllowToScore.value!!) {
                viewModel.scoreDiceValue()
                btnThrowText = getString(R.string.button_throw)
            }
            if(viewModel.hasWin.value!!){
                winnerPopUpWindow(viewModel.winner.value)
            }
        }
    }


    private fun removeWinningScoreEditor(){
        if(viewModel.allowToChangeWinningScore.value!!.not()) {
            textInputGameWinningScore.isEnabled = false
            linearLayoutEnterTargetScore.visibility = View.GONE
        }
    }

    private fun winnerPopUpWindow(winnerName: String?) {
        val inflater : LayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView : View = inflater.inflate(R.layout.winner_popup_layout, null)
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        val focusable = true
        val popupWindow = PopupWindow(popupView, width, height,focusable)
        popupWindow.setOnDismissListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        popupWindow.isOutsideTouchable = false
        popupWindow.showAtLocation(findViewById<View?>(android.R.id.content).rootView, Gravity.BOTTOM, 0, 0)
        val winner = popupView.findViewById<TextView>(R.id.textViewWinner)
        winner.text = winnerName
    }


    private fun updateSelectedDices( imageViews: Array<ImageView>){
        for(i in imageViews.indices){
            imageViews[i].setBackgroundResource(0)
        }
        reRollDiceArray.clear()
    }

    private fun updateDisplay(dice: IntArray, imageViews: Array<ImageView>) {
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
}