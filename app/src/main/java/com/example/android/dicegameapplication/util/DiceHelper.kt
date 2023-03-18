package com.example.android.dicegameapplication.util

import android.content.Context
import kotlin.random.Random

class DiceHelper {
    // you can call companion from the class itself without create an instance of the class
    companion object {


        // Get a random number between 1 and 6
        fun getDie(): Int {
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

        fun getReRolledArray( diceArray: IntArray, reRollDiceArray:IntArray?): IntArray {
            for (i in reRollDiceArray!!.indices) {
                val index = reRollDiceArray[i] - 1
                diceArray[index] = getDie()
            }
            return diceArray
        }

        fun getCurrentRollFullScore(context: Context, diceArray: IntArray?): String{
            var currentRollFullScore = 0
            for(i in diceArray!!.indices) {
                currentRollFullScore += diceArray[i]
            }

            return currentRollFullScore.toString()
        }

        fun getFullScore(context: Context, currentFullScore:String?, currentRollFullScore: String?):String {
            val currentFullScoreIntValue = currentFullScore!!.toInt()
            val currentRollFullScoreIntValue = currentRollFullScore!!.toInt()
            val fullScore = currentFullScoreIntValue + currentRollFullScoreIntValue
            return fullScore.toString()
        }

        fun getTheWinner(userFullScore:Int, robotFullScore:Int, winningScore:Int):String{
            return if(userFullScore >= winningScore && robotFullScore >= winningScore){
                if(userFullScore > robotFullScore){
                    "user"
                } else if (userFullScore == robotFullScore){
                    "equal"
                } else {
                    "robot"
                }
            } else if(userFullScore >= winningScore) {
                "user"
            } else if(robotFullScore >= winningScore) {
                "robot"
            } else {
                ""
            }
        }

        fun hasWin(userFullScore:Int?, robotFullScore:Int?, winningScore:Int?):Boolean{
            val result = getTheWinner(userFullScore!!, robotFullScore!!, winningScore!!)
            return result != ""
        }


        fun getRobotReRollingDices():IntArray {
            val robotReRollDices = mutableListOf<Int>()
            for (i in 1..getRobotDiceToReRoll()){
                while(true){
                    val diceNo = getRobotDiceToReRoll()
                    if(!robotReRollDices.contains(diceNo)){
                        robotReRollDices.add(diceNo)
                        break
                    }
                    else{ continue }
                }
            }
            return robotReRollDices.toIntArray()
        }

        fun getRobotReRollDecision():Boolean{
            return true
        }

        private fun getRobotDiceToReRoll():Int{
            return Random.nextInt(1,6)
        }
    }
}
