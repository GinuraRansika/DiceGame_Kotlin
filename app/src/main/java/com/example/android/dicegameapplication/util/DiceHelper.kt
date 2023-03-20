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
 *  Strategy - First checks whether the user selected "gameDifficulty" is "easy" or "hard"
 *             If it is "hard" Then checks the remaining re-rolls for the robot
 *             If computer has more re-rolls then checks who's Full score is greater
 *
 *             If user's full score is greater than or equal to computer's full score
 *                  if it is true
 *                      Then runs the "optimumReRollFunctionRunnerWhenUserMax" method
 *                          First I get the score gap of computer's and user's then checks
 *                          If the user's full score is greater than the Game Winnings score's Three quarters (Game Winning score * 0.75 )
 *                              If it is true
 *                                  then checks the score gap is greater than 10
 *                                      if it is true
 *                                          I get the array of dices which has scored lower than or equal to 4 and ReRoll those again
 *                                      if it is false
 *                                          I get the array of dices which has scored lower than or equal to 3 and ReRoll those again
 *                              If it is false
 *                                  I get the array of dices which has score lower than or equal to 3 and ReRoll those again
 *                  if it is false
 *                      I get the array of dices which has scored lower than or equal to 3 and ReRoll those again
 *
 *  Opinion - In my opinion this works well
 *            because if the user's and computer's full scores are nearby values,
 *              computers shouldn't re-roll dices which has higher values because if the random value gets a small value
 *              computer might lose his game because of the high score loss
 *              So, most intelligent thing is
 *              re-roll dices which has vales as 3 or below because in this way computer has a equal probability of getting a
 *              higher or lower value and even if computer gets a lower value computer won't lose to much scores
 *              and if computer gets a higher value it will give the computer a good lead from the user
 *
 *           if the user is close to the winning score
 *              computer should take a risk of getting high values as possible because if he scored normal values
 *              computer won't even have a chance
 *              So, in that case computer should re-roll dices which has values as 4 or below
 *              if computer gets a higher value dices he might have a chance at least
 *
 *  Advantages - if the computer got high value dices in the first throw computers won't lose them because of the re-rolls
 *               Even the user close to winning score computer has a better probability of winning comparing to the random methods
 *
 *
 *  Disadvantages - When the user close to winning score computer has a probability of losing his chance of getting the scores equal
 *                  In every re-roll computer has a probability of loosing 1 or 2 values if the computer has scored 3 as the value before
 */
