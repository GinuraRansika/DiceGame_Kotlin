package com.example.android.dicegameapplication.viewModel

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.android.dicegameapplication.LOG_TAG
import com.example.android.dicegameapplication.R
import com.example.android.dicegameapplication.util.DiceHelper

class DiceViewModel(app: Application) : AndroidViewModel(app) {
    // add parentheses in the end so that you are calling the constructor
    val userDiceArray = MutableLiveData<IntArray>()
    val userFullScore = MutableLiveData<String>()
    val userCurrentRollFullScore = MutableLiveData<String>()
    val userRemainingReRolls = MutableLiveData<Int>()
    val userWinnings = MutableLiveData<Int>()

    var userAllowToThrow = MutableLiveData<Boolean>()
    var userAllowToReRoll = MutableLiveData<Boolean>()
    var userAllowToScore = MutableLiveData<Boolean>()
    var userAllowToSelectDices = MutableLiveData<Boolean>()

    val robotDiceArray = MutableLiveData<IntArray>()
    val robotFullScore = MutableLiveData<String>()
    val robotCurrentRollFullScore = MutableLiveData<String>()
    private val robotRemainingReRolls = MutableLiveData<Int>()
    val robotWinnings = MutableLiveData<Int>()

    var winner = MutableLiveData<String>()
    var gameDifficulty = MutableLiveData<String>()
    private var gameWinningScore = MutableLiveData<String>()
    var finalWinningScore = MutableLiveData<String>()
    var btnThrowText = MutableLiveData<String>()
    var reRollDiceArray = MutableLiveData<MutableList<Int>>()
    var isWin = MutableLiveData<Boolean>()
    var isTie = MutableLiveData<Boolean>()
    // change the visibility of the button
    var btnScoreVisibility = MutableLiveData<Int>()
    var btnThrowVisibility = MutableLiveData<Int>()

    private val maxReRolls = 2
    private val context = app
    private val defaultWinningScore = 101

    init {
        // initialization of values
        Log.i(LOG_TAG, "VIEW MODEL CREATED") // check the viewModel
        userAllowToThrow.value = true
        userAllowToReRoll.value = false
        userAllowToScore.value = false
        userAllowToSelectDices.value = false

        btnScoreVisibility.value = View.GONE
        btnThrowVisibility.value = View.VISIBLE
        btnThrowText.value = context.getString(R.string.button_throw)

        isWin.value = false
        isTie.value = false
        reRollDiceArray.value?.addAll(listOf(1,2,3,4,5))
        finalWinningScore.value = defaultWinningScore.toString()
        winner.value = ""
        userFullScore.value = "0"
        robotFullScore.value = "0"
        userCurrentRollFullScore.value = "0"
        robotCurrentRollFullScore.value = "0"
        userDiceArray.value = intArrayOf(6, 6, 6, 6, 6)
        robotDiceArray.value = intArrayOf(6, 6, 6, 6, 6)
    }

    /**
     * throw the dices and update the respective values
     */
    fun throwDices() {
        userAllowToThrow.value = false
        userAllowToReRoll.value = true
        userAllowToScore.value = true
        userAllowToSelectDices.value = true

        // set default value in every throw
        userRemainingReRolls.value = maxReRolls
        robotRemainingReRolls.value = maxReRolls

        btnThrowText.value = "REROLL (${userRemainingReRolls.value})"
        btnScoreVisibility.value = View.VISIBLE
        updateDiceArraysAndScores()
    }

    /**
     * Score the Current values on the dices
     */
    fun scoreDicesValue() {
        userAllowToThrow.value = true
        userAllowToScore.value = false
        userAllowToReRoll.value = false
        userAllowToSelectDices.value = false

        userFullScore.value = DiceHelper.getFullScore(context,userFullScore.value, userCurrentRollFullScore.value)

        if(gameDifficulty.value.equals("easy")){
            if(robotRemainingReRolls.value!! >= 1){
                for(i in 1..robotRemainingReRolls.value!!){
                    robotRemainingReRolls.value = (robotRemainingReRolls.value!!- 1)
                    val robotReRollDiceArray: IntArray = DiceHelper.getRobotDicesToBeReRolled()
                    robotDiceArray.value = robotDiceArray.value?.let { DiceHelper.getReRolledArray(it, robotReRollDiceArray) }
                    robotCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,robotDiceArray.value)
                }
            }
        } else{
            if(robotRemainingReRolls.value!! >= 1){
                for(i in 1.. robotRemainingReRolls.value!!){
                    robotRemainingReRolls.value = (robotRemainingReRolls.value!!- 1)
                    if(userFullScore.value?.toInt()!!  >= robotFullScore.value?.toInt()!!){
                        val robotReRollDiceArray: IntArray = DiceHelper.optimumReRollFunctionRunnerWhenUserMax(context, userFullScore.value!!.toInt(), robotFullScore.value!!.toInt(), robotDiceArray.value!!, gameWinningScore.value!!.toInt())
                        robotDiceArray.value = robotDiceArray.value?.let { DiceHelper.getReRolledArray(it, robotReRollDiceArray) }
                        robotCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context, robotDiceArray.value)
                    } else{
                        val robotReRollDiceArray: IntArray = DiceHelper.optimumReRollFunctionRunnerWhenRobotMax(context, userFullScore.value!!.toInt(), robotFullScore.value!!.toInt(), robotDiceArray.value!!, gameWinningScore.value!!.toInt())
                        robotDiceArray.value = robotDiceArray.value?.let { DiceHelper.getReRolledArray(it, robotReRollDiceArray) }
                        robotCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context, robotDiceArray.value)
                    }
                }
            }
        }

        robotFullScore.value = DiceHelper.getFullScore(context,robotFullScore.value, robotCurrentRollFullScore.value)
        btnThrowText.value = context.getString(R.string.button_throw)
        btnScoreVisibility.value = View.GONE
        btnThrowVisibility.value = View.VISIBLE

        if(getTheWinner()){
            userAllowToThrow.value = false
            userAllowToScore.value = false
            userAllowToReRoll.value = false
            userAllowToSelectDices.value = false
            isWin.value = true

            if(isTie.value == true){
                userAllowToThrow.value = true
                userAllowToScore.value = true
            }
        }
    }

    /**
     * Roll the dices when the Scores are equal
     */
    fun rollDiceWhenTie(){
        userAllowToThrow.value = false
        userAllowToReRoll.value = false
        userAllowToSelectDices.value = false
        userAllowToScore.value = true
        btnThrowVisibility.value = View.GONE
        btnScoreVisibility.value = View.VISIBLE
        // update the arrays with the values and update the scores
        updateDiceArraysAndScores()
    }

    /**
     * run the ReRolling on User and Robot Dices
     */
    fun reRollDices(userKeepDiceArray: IntArray) {
        reRollRobotDices()
        reRollUserDices(userKeepDiceArray)
    }

    /**
     * ReRoll the Robots Dices
     */
    private fun reRollRobotDices(){
        // run the respective function based on the Game Difficulty
        if(gameDifficulty.value.equals("easy")){
            val robotReRollDecision = DiceHelper.getRobotReRollDecision()
            if(robotRemainingReRolls.value!! >= 1) {
                if(robotReRollDecision) {
                    robotRemainingReRolls.value = (robotRemainingReRolls.value!!- 1)
                    val robotReRollDiceArray: IntArray = DiceHelper.getRobotDicesToBeReRolled()
                    robotDiceArray.value = robotDiceArray.value?.let { DiceHelper.getReRolledArray(it, robotReRollDiceArray) }
                    robotCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,robotDiceArray.value)
                }
            }
        }else{
            if(robotRemainingReRolls.value!! >= 1){
                robotRemainingReRolls.value = (robotRemainingReRolls.value!!- 1)
                if(userFullScore.value?.toInt()!!  >= robotFullScore.value?.toInt()!!){
                    val robotReRollDiceArray: IntArray = DiceHelper.optimumReRollFunctionRunnerWhenUserMax(context, userFullScore.value!!.toInt(), robotFullScore.value!!.toInt(), robotDiceArray.value!!, gameWinningScore.value!!.toInt())
                    robotDiceArray.value = robotDiceArray.value?.let { DiceHelper.getReRolledArray(it, robotReRollDiceArray) }
                    robotCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context, robotDiceArray.value)
                } else{
                    val robotReRollDiceArray: IntArray = DiceHelper.optimumReRollFunctionRunnerWhenRobotMax(context, userFullScore.value!!.toInt(), robotFullScore.value!!.toInt(), robotDiceArray.value!!, gameWinningScore.value!!.toInt())
                    robotDiceArray.value = robotDiceArray.value?.let { DiceHelper.getReRolledArray(it, robotReRollDiceArray) }
                    robotCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context, robotDiceArray.value)
                }
            }
        }
    }

    /**
     * ReRoll the users' Dices
     */
    private fun reRollUserDices(userKeepDiceArray: IntArray){
        if(userRemainingReRolls.value!! > 1) {
            if(userKeepDiceArray.isNotEmpty()){
                userAllowToScore.value =  true
                userRemainingReRolls.value = (userRemainingReRolls.value!!.toInt() - 1)
                userDiceArray.value = userDiceArray.value?.let { DiceHelper.getReRolledArray(it,userKeepDiceArray) }
                userCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,userDiceArray.value)

                userAllowToThrow.value = false
                userAllowToReRoll.value = true
                btnThrowText.value = "REROLL (${userRemainingReRolls.value})"
            }
        }
        else if(userRemainingReRolls.value!! == 1) {
            userDiceArray.value = userDiceArray.value?.let { DiceHelper.getReRolledArray(it,userKeepDiceArray) }
            userCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,userDiceArray.value)
            scoreDicesValue() // score the current values without clicking the button
            userAllowToReRoll.value = false
            btnScoreVisibility.value = View.GONE
            btnThrowText.value = context.getString(R.string.button_throw)
        }
    }

    /**
     * check the winner and update the values
     */
    private fun getTheWinner():Boolean{
        isTie.value = false
        return if (DiceHelper.isWin(userFullScore.value?.toInt() , robotFullScore.value?.toInt(), finalWinningScore.value?.toInt())){
            winner.value = DiceHelper.getTheWinner(userFullScore.value!!.toInt() , robotFullScore.value!!.toInt(), finalWinningScore.value!!.toInt())
            if(winner.value.equals("user")){
                userWinnings.value = userWinnings.value?.plus(1)
            }else if(winner.value.equals("robot")){
                robotWinnings.value = robotWinnings.value?.plus(1)
            }else if(winner.value.equals("equal")){
                isTie.value = true
            }
            true
        }else{
            false
        }
    }

    /**
     * Update the ReRoll Dices Array and their backgrounds
     */
    fun updateReRollDiceArray(selectedDice: ImageView, diceNumber:Int, reRollDiceArray: MutableList<Int>): MutableList<Int>{
        btnThrowText.value = "REROLL (${userRemainingReRolls.value})"
        userAllowToReRoll.value = true

        if(containsCheck(diceNumber, reRollDiceArray)){
            selectedDice.setBackgroundResource(R.drawable.image_border)
            reRollDiceArray.remove(diceNumber)
            if(reRollDiceArray.size == 0){
                btnThrowText.value = context.getString(R.string.button_throw)
                userAllowToReRoll.value = false
            }
        }else{
            selectedDice.setBackgroundResource(0)
            reRollDiceArray.add(diceNumber)
        }
        this.reRollDiceArray.value = reRollDiceArray
        return this.reRollDiceArray.value!!
    }
    private fun updateDiceArraysAndScores() {
        userDiceArray.value = DiceHelper.rollDice()
        userCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,userDiceArray.value)
        robotDiceArray.value = DiceHelper.rollDice()
        robotCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,robotDiceArray.value)
    }

    /**
     * checks the dice number whether it's in the array or not
     */
    private fun containsCheck(diceNumber:Int, reRollDiceArray: MutableList<Int>):Boolean{
        for(i in reRollDiceArray.indices){
            if(reRollDiceArray[i]==diceNumber) {return true}
        }
        return false
    }

    /**
     * Sets the game difficulty value
     */
    fun setGameDifficulty(gameDifficulty:String){
        this.gameDifficulty.value = gameDifficulty
    }

    /**
     * Sets the Game Wining score
     */
    fun setGameWinningScore(userEnteredScore: String){
        gameWinningScore.value = userEnteredScore
        finalWinningScore.value = gameWinningScore.value
    }

    /**
     * Sets the Received values of the User total winning games and Computers' total winning games
     */
    fun setValue(userWins:Int, robotWins:Int){
        userWinnings.value = userWins
        robotWinnings.value = robotWins
    }

}