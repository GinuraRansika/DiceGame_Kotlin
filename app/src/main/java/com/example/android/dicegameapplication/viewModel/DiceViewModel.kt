package com.example.android.dicegameapplication.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.android.dicegameapplication.LOG_TAG
import com.example.android.dicegameapplication.util.DiceHelper

class DiceViewModel(app: Application) : AndroidViewModel(app) {
    // add parentheses in the end so that you are calling the constructor
    val userDiceArray = MutableLiveData<IntArray>()
    val userFullScore = MutableLiveData<String>()
    val userCurrentRollFullScore = MutableLiveData<String>()
    val userRemainingReRolls = MutableLiveData<String>()



    val robotDiceArray = MutableLiveData<IntArray>()
    val robotFullScore = MutableLiveData<String>()
    val robotCurrentRollFullScore = MutableLiveData<String>()
    val robotRemainingReRolls = MutableLiveData<String>()

    var userAllowToThrow = MutableLiveData<Boolean>()
    var userAllowToReRoll = MutableLiveData<Boolean>()
    var userAllowToScore = MutableLiveData<Boolean>()

    var gameWinningScore = MutableLiveData<String>()
    var allowToChangeWinningScore = MutableLiveData<Boolean>()
    var finalWinningScore = MutableLiveData<String>()

    private val maxReRolls = 2
    private val context = app
    private val defaultWinningScore = 101

    init {
        Log.i(LOG_TAG, "VIEW MODEL CREATED")
        userAllowToThrow.value = true
        userAllowToReRoll.value = false
        userAllowToScore.value = false
        allowToChangeWinningScore.value = true

        userRemainingReRolls.value = maxReRolls.toString()
        robotRemainingReRolls.value = maxReRolls.toString()
        finalWinningScore.value = defaultWinningScore.toString()

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
        allowToChangeWinningScore.value = false
        userAllowToThrow.value = false
        userAllowToReRoll.value = true
        userAllowToScore.value = true
        userRemainingReRolls.value = maxReRolls.toString()
        robotRemainingReRolls.value = maxReRolls.toString()


        userDiceArray.value = DiceHelper.rollDice()
        userCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,userDiceArray.value)
        robotDiceArray.value = DiceHelper.rollDice()
        robotCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,robotDiceArray.value)
    }

    fun scoreDiceValue() {
        userAllowToThrow.value = true
        userAllowToScore.value = false
        userAllowToReRoll.value = false
        Toast.makeText(context, "Remaining RErols ${robotRemainingReRolls.value}",Toast.LENGTH_LONG).show()
        userFullScore.value = DiceHelper.getFullScore(context,userFullScore.value, userCurrentRollFullScore.value)
        robotFullScore.value = DiceHelper.getFullScore(context,robotFullScore.value, robotCurrentRollFullScore.value)
    }

    fun reRollDice(userReRollDiceArray: IntArray) {
        val robotReRollDecision = DiceHelper.getRobotReRollDecision()
        Toast.makeText(context, robotReRollDecision.toString(),Toast.LENGTH_LONG).show()
        if(robotRemainingReRolls.value!!.toInt() > 1) {
            if(robotReRollDecision){
                robotRemainingReRolls.value = (robotRemainingReRolls.value!!.toInt() - 1).toString()
                val robotReRollDiceArray:IntArray = DiceHelper.getRobotReRollingDices()
                Toast.makeText(context, robotReRollDiceArray.joinToString(),Toast.LENGTH_LONG).show()
                robotDiceArray.value = robotDiceArray.value?.let { DiceHelper.getReRolledArray(it, robotReRollDiceArray) }
                robotCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,robotDiceArray.value)
            }
        }

        if(userRemainingReRolls.value!!.toInt() > 1) {
            if(userReRollDiceArray.isNotEmpty()){
                userAllowToScore.value =  true

                userRemainingReRolls.value = (userRemainingReRolls.value!!.toInt() - 1).toString()
                userDiceArray.value = userDiceArray.value?.let { DiceHelper.getReRolledArray(it,userReRollDiceArray) }
                userCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,userDiceArray.value)
            }
        }else if(userRemainingReRolls.value!!.toInt() == 1) {
            userRemainingReRolls.value = (userRemainingReRolls.value!!.toInt() - 1).toString()
            userDiceArray.value = userDiceArray.value?.let { DiceHelper.getReRolledArray(it,userReRollDiceArray) }
            userCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,userDiceArray.value)
            scoreDiceValue()
        }
    }
}