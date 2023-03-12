package com.example.android.dicegameapplication.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.android.dicegameapplication.LOG_TAG
import com.example.android.dicegameapplication.util.DiceHelper

class DiceViewModel(app: Application) : AndroidViewModel(app) {
    // add parentheses in the end so that you are calling the constructor
    val userDiceArray = MutableLiveData<IntArray>()
    val robotDiceArray = MutableLiveData<IntArray>()
    val userFullScore = MutableLiveData<String>()
    val robotFullScore = MutableLiveData<String>()
    val userCurrentRollFullScore = MutableLiveData<String>()
    val robotCurrentRollFullScore = MutableLiveData<String>()

    private val context = app

    init {
        Log.i(LOG_TAG, "VIEW MODEL CREATED")
        userFullScore.value = "0"
        robotFullScore.value = "0"
        userCurrentRollFullScore.value = "0"
        robotCurrentRollFullScore.value = "0"
        userDiceArray.value = intArrayOf(6, 6, 6, 6, 6)
        robotDiceArray.value = intArrayOf(6, 6, 6, 6, 6)
    }

    fun rollDice() {
        userDiceArray.value = DiceHelper.rollDice()
        userCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,userDiceArray.value)
        robotDiceArray.value = DiceHelper.rollDice()
        robotCurrentRollFullScore.value = DiceHelper.getCurrentRollFullScore(context,robotDiceArray.value)
    }
}