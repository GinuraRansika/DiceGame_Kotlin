package com.example.android.dicegameapplication.viewModel

import android.app.Application
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
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

    val robotDiceArray = MutableLiveData<IntArray>()
    val robotFullScore = MutableLiveData<String>()
    val robotCurrentRollFullScore = MutableLiveData<String>()
    private val robotRemainingReRolls = MutableLiveData<Int>()

    var userAllowToThrow = MutableLiveData<Boolean>()
    var userAllowToReRoll = MutableLiveData<Boolean>()
    var userAllowToScore = MutableLiveData<Boolean>()
    var userAllowToSelectDices = MutableLiveData<Boolean>()

    var winner = MutableLiveData<String>()

    var gameWinningScore = MutableLiveData<String>()
    var allowToChangeWinningScore = MutableLiveData<Boolean>()
    var finalWinningScore = MutableLiveData<String>()
    private var rolledTimes = MutableLiveData<Int>()
    var btnThrowText = MutableLiveData<String>()
    var reRollDiceArray = MutableLiveData<MutableList<Int>>()
    var hasWin = MutableLiveData<Boolean>()

    private val maxReRolls = 2
    private val context = app
    private val defaultWinningScore = 101

    init {
        Log.i(LOG_TAG, "VIEW MODEL CREATED")
        userAllowToThrow.value = true
        userAllowToReRoll.value = false
        userAllowToScore.value = false
        userAllowToSelectDices.value = false
        allowToChangeWinningScore.value = true
        hasWin.value = false
        rolledTimes.value = 0

        userRemainingReRolls.value = maxReRolls
        robotRemainingReRolls.value = maxReRolls
        finalWinningScore.value = defaultWinningScore.toString()

        btnThrowText.value = context.getString(R.string.button_throw)
        winner.value = ""
        userFullScore.value = "0"
        robotFullScore.value = "0"
        userCurrentRollFullScore.value = "0"
        robotCurrentRollFullScore.value = "0"
        userDiceArray.value = intArrayOf(6, 6, 6, 6, 6)
        robotDiceArray.value = intArrayOf(6, 6, 6, 6, 6)
    }

    fun setGameWinningScore(userEnteredScore: String){
        if(allowToChangeWinningScore.value == true){
            gameWinningScore.value = userEnteredScore
            finalWinningScore.value = gameWinningScore.value
        }
    }

    fun rollDice() {
        rolledTimes.value = rolledTimes.value?.plus(1)
        allowToChangeWinningScore.value = false
        userAllowToThrow.value = false
        userAllowToReRoll.value = false
        userAllowToScore.value = true
        userAllowToSelectDices.value = true
        userRemainingReRolls.value = maxReRolls
        robotRemainingReRolls.value = maxReRolls

        userDiceArray.value = DiceHelper.rollDice()
        userCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,userDiceArray.value)
        robotDiceArray.value = DiceHelper.rollDice()
        robotCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,robotDiceArray.value)
    }

    fun getTheWinner():Boolean{
        if (DiceHelper.hasWin(userFullScore.value?.toInt() , robotFullScore.value?.toInt(), finalWinningScore.value?.toInt())){
            Toast.makeText(context,"Win",Toast.LENGTH_LONG).show()
            winner.value = DiceHelper.getTheWinner(userFullScore.value!!.toInt() , robotFullScore.value!!.toInt(), finalWinningScore.value!!.toInt())
            return true
        }else{
            return false
        }
    }


    fun scoreDiceValue() {
        userAllowToThrow.value = true
        userAllowToScore.value = false
        userAllowToReRoll.value = false
        userAllowToSelectDices.value = false

        userFullScore.value = DiceHelper.getFullScore(context,userFullScore.value, userCurrentRollFullScore.value)
        robotFullScore.value = DiceHelper.getFullScore(context,robotFullScore.value, robotCurrentRollFullScore.value)

        if(getTheWinner()){
            userAllowToThrow.value = false
            userAllowToScore.value = false
            userAllowToReRoll.value = false
            userAllowToSelectDices.value = false
            hasWin.value = true
        }
    }

    fun updateReRollDiceArray(selectedDice: ImageView, diceNumber:Int, reRollDiceArray: MutableList<Int>): MutableList<Int>{
        btnThrowText.value = "REROLL (${userRemainingReRolls.value})"
        userAllowToReRoll.value = true
        if(!containsCheck(diceNumber, reRollDiceArray)){
            selectedDice.setBackgroundResource(R.drawable.image_border)
            reRollDiceArray.add(diceNumber)
        }else{
            selectedDice.setBackgroundResource(0)
            reRollDiceArray.remove(diceNumber)
            if(reRollDiceArray.size == 0){
                btnThrowText.value = context.getString(R.string.button_throw)
                userAllowToReRoll.value = false
            }
        }
        this.reRollDiceArray.value = reRollDiceArray
        return this.reRollDiceArray.value!!
    }

    private fun containsCheck(diceNumber:Int, reRollDiceArray: MutableList<Int>):Boolean{
        for(i in reRollDiceArray.indices){
            if(reRollDiceArray[i]==diceNumber) {return true}
        }
        return false
    }

    fun reRollDice(userReRollDiceArray: IntArray) {
        val robotReRollDecision = DiceHelper.getRobotReRollDecision()
        if(robotRemainingReRolls.value!! >= 1) {
            if(robotReRollDecision) {
                robotRemainingReRolls.value = (robotRemainingReRolls.value!!- 1)
                val robotReRollDiceArray: IntArray = DiceHelper.getRobotReRollingDices()

                robotDiceArray.value = robotDiceArray.value?.let { DiceHelper.getReRolledArray(it, robotReRollDiceArray) }
                robotCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,robotDiceArray.value)
            }
        }

        if(userRemainingReRolls.value!! > 1) {
            if(userReRollDiceArray.isNotEmpty()){
                userAllowToScore.value =  true
                userRemainingReRolls.value = (userRemainingReRolls.value!!.toInt() - 1)
                userDiceArray.value = userDiceArray.value?.let { DiceHelper.getReRolledArray(it,userReRollDiceArray) }
                userCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,userDiceArray.value)

                userAllowToThrow.value = false
                userAllowToReRoll.value = false
                btnThrowText.value = context.getString(R.string.button_throw)
            }
        }
        else if(userRemainingReRolls.value!! == 1) {
            userDiceArray.value = userDiceArray.value?.let { DiceHelper.getReRolledArray(it,userReRollDiceArray) }
            userCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,userDiceArray.value)
            scoreDiceValue()
            btnThrowText.value = context.getString(R.string.button_throw)
        }
    }
}