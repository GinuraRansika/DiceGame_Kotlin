package com.example.android.dicegameapplication.util

import android.content.Context
import kotlin.random.Random

class DiceHelper {
    // you can call companion from the class itself without create an instance of the class
    companion object {


        // Get a random number between 1 and 6
        private fun getDie(): Int {
            return Random.nextInt(1, 7)
        }

        // Roll the dice, return 5 random integers in an array
        fun rollDice(): IntArray {
            return intArrayOf(
                getDie(),
                getDie(),
                getDie(),
                getDie(),
                getDie()
            )
        }

        fun getCurrentRollFullScore(context: Context, diceArray: IntArray?): String{
            var currentRollFullScore = 0
            for(i in diceArray!!.indices) {
                currentRollFullScore += diceArray[i]
            }

            return currentRollFullScore.toString()
        }


    }
}