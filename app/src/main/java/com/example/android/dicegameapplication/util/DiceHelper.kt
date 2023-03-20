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
            return intArrayOf(getDie(), getDie(), getDie(), getDie(), getDie())
        }

        // Get the ReRolled Array
        fun getReRolledArray( diceArray: IntArray, reRollDiceArray:IntArray?): IntArray {
            for (i in reRollDiceArray!!.indices) {
                val index = reRollDiceArray[i] - 1 // get the dices values
                diceArray[index] = getDie()
            }
            return diceArray
        }

        // Get the Full Score of the Current Roll
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

        // Checks the Arrays and get the Winner
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

        // checks whether someone has won or not
        fun isWin(userFullScore:Int?, robotFullScore:Int?, winningScore:Int?):Boolean{
            val result = getTheWinner(userFullScore!!, robotFullScore!!, winningScore!!)
            return result != ""
        }


        //  get the robot's dices which are need to be rerolled
        fun getRobotDicesToBeReRolled():IntArray {
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

        // Get a Random ReRolling Decision
        fun getRobotReRollDecision():Boolean{
            return Random.nextBoolean()
        }

        // Get a Random Dice to ReRoll
        private fun getRobotDiceToReRoll():Int{
            return Random.nextInt(1,6)
        }

        // Get the User Selected Dices Array to ReRoll
        fun getUserSelectedDices(reRollDiceArray:IntArray?): IntArray{
            val rolledArray = mutableListOf(1,2,3,4,5)
            if (reRollDiceArray != null) {
                for(i in reRollDiceArray.indices){
                    if(rolledArray.contains(reRollDiceArray[i])){
                        rolledArray.remove(reRollDiceArray[i])
                    }
                }
            }
            return rolledArray.toIntArray()
        }

        // Run the Respective function based on the scores
        fun optimumReRollFunctionRunnerWhenUserMax(context: Context, userFullScore: Int, robotFullScore: Int, robotDiceArray: IntArray, gameWinningScore: Int):IntArray{
            val scoreGap = userFullScore - robotFullScore
            val diceToRoll: IntArray = if(userFullScore >= (gameWinningScore*0.75)){
                if(scoreGap >= 10){
                    getDicesArrayToBeReRolled(context, robotDiceArray,4)
                }else{
                    getDicesArrayToBeReRolled(context, robotDiceArray,3)
                }
            } else{
                getDicesArrayToBeReRolled(context, robotDiceArray, 3)
            }
            return diceToRoll
        }

        // Run the Respective function based on the scores
        fun optimumReRollFunctionRunnerWhenRobotMax(context: Context, userFullScore: Int, robotFullScore: Int, robotDiceArray: IntArray, gameWinningScore: Int): IntArray {
            val scoreGap = robotFullScore - userFullScore
            return getDicesArrayToBeReRolled(context, robotDiceArray, 3)
        }

        // Optimal Versions of Getting the ReRolling dices array
        private fun getDicesArrayToBeReRolled(context: Context, robotDiceArray: IntArray, number: Int):IntArray{
            val dicesToReRoll = mutableListOf<Int>()

            for(i in robotDiceArray.indices){
                if(robotDiceArray[i] <= number){
                    dicesToReRoll.add(i+1)
                }
            }
            return dicesToReRoll.toIntArray()
        }
    }
}

/**
 *
 */
