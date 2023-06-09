package com.example.android.dicegameapplication.viewModel

import android.app.Application
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.android.dicegameapplication.LOG_TAG
import com.example.android.dicegameapplication.R
import com.example.android.dicegameapplication.util.DiceHelper

class DiceViewModel(app: Application) : AndroidViewModel(app) {
    // add parentheses in the end so that you are calling the constructor
    val userDiceArray = MutableLiveData<IntArray>()
    val robotDiceArray = MutableLiveData<IntArray>()
    val userFullScore = MutableLiveData<String>()
    val robotFullScore = MutableLiveData<String>()
    val userCurrentRollFullScore = MutableLiveData<String>()
    val robotCurrentRollFullScore = MutableLiveData<String>()
    val remainingReRolls = MutableLiveData<String>()

    var allowToThrow = MutableLiveData<Boolean>()
    var allowToReRoll = MutableLiveData<Boolean>()
    var allowToScore = MutableLiveData<Boolean>()

    private val maxReRolls = 10
    private val context = app

    init {
        Log.i(LOG_TAG, "VIEW MODEL CREATED")
        allowToThrow.value = true
        allowToReRoll.value = false
        allowToScore.value = false
        remainingReRolls.value = maxReRolls.toString()
        userFullScore.value = "0"
        robotFullScore.value = "0"
        userCurrentRollFullScore.value = "0"
        robotCurrentRollFullScore.value = "0"
        userDiceArray.value = intArrayOf(6, 6, 6, 6, 6)
        robotDiceArray.value = intArrayOf(6, 6, 6, 6, 6)
    }

    fun rollDice() {
        allowToThrow.value = false
        allowToReRoll.value = true
        allowToScore.value = true
        userDiceArray.value = DiceHelper.rollDice()
        userCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,userDiceArray.value)
        robotDiceArray.value = DiceHelper.rollDice()
        robotCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,robotDiceArray.value)

    }

    fun scoreDiceValue() {
        allowToThrow.value = true
        allowToScore.value = false
        allowToReRoll.value = false
        userFullScore.value = DiceHelper.getFullScore(context,userFullScore.value, userCurrentRollFullScore.value)
        robotFullScore.value = DiceHelper.getFullScore(context,robotFullScore.value, robotCurrentRollFullScore.value)
    }

    fun reRollDice(reRollDiceArray: IntArray) {
        if(remainingReRolls.value!!.toInt() > 0) {
            if(reRollDiceArray.isNotEmpty()){
                allowToScore.value =  true
                remainingReRolls.value = (remainingReRolls.value!!.toInt() - 1).toString()
                userDiceArray.value = userDiceArray.value?.let { DiceHelper.getReRolledArray(it,reRollDiceArray) }
                userCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,userDiceArray.value)
            }

        }


    }
}